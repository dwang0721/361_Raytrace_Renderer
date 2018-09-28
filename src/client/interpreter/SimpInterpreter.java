package client.interpreter;

import java.util.Stack;
import java.util.ArrayList;

import client.interpreter.LineBasedReader;
import client.interpreter.ObjReader;
import geometry.Point3DH;
import geometry.Rectangle;
import geometry.Vertex3D;
import geometry.Light;
import geometry.Lighting;
import geometry.Halfplane3DH;
import line.LineRenderer;
import client.Clipper;
import windowing.drawable.DepthCueingDrawable;
import client.RendererTrio;
import geometry.Transformation;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.Shader;

import polygon.Chain;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

import polygon.shading.FaceShader;
import polygon.shading.PixelShader;
import polygon.shading.VertexShader;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	public RenderStyle renderStyle;
	public ShaderStyle shaderStyle;
	
	private Transformation CTM;
	private Stack<Transformation> CTMStack;
	public Transformation projectedToScreen;
	private Transformation worldToCamera;
	private Transformation worldToScreen;
	
	private static double WORLD_LOW_X = -100;
	private static double WORLD_HIGH_X = 100;
	private static double WORLD_LOW_Y = -100;
	private static double WORLD_HIGH_Y = 100;
	private static double WORLD_NEAR_Z = -1;
	private static double WORLD_FAR_Z = -200;
	private static double VIEW_PLANE = -1;
	
	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	
	private ArrayList<Light> lightList;
	private double kSpecular;
	private double specularExponent;
	
	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;
	public Shader ambientShader = c-> ambientLight.multiply(c);	
	
	private Lighting lighting;
	
	private FaceShader flatFaceShader = poly-> flatShadePolygon(poly);
	private VertexShader nullVertexShader =  (poly, vert) -> vert;
	private PixelShader flatPixelShader = (poly, vert) -> flatShadePixel(poly, vert);
	
	private FaceShader nullFaceShader = poly-> poly;
	private VertexShader gouraudVertexShader =  (poly, vert) -> gouraudShadeVetex(poly, vert);
	private PixelShader colorInterpolatingPixelShader = (poly, vert) -> gouraundShadePixel(poly, vert);
	
	private VertexShader phongVertexShader = (poly, vert) -> phongShadeVertex(poly, vert);
	private PixelShader phongPixelShader = (poly, vert) -> phongShadePixel(poly, vert);
	
	private int fogNear = -99999999;
	private int fogFar  = -99999999;
	
	public Drawable drawable;
	public Drawable depthCueingDrawable;
	
	public LineRenderer lineRenderer;
	public PolygonRenderer filledRenderer;
	public PolygonRenderer wireframeRenderer;	
	public Clipper clipper;
	
	public FaceShader currentFaceShader;
	public VertexShader currentVertexShader;
	public PixelShader currentPixelShader;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;		
	}
	
	public enum ShaderStyle {
		LIGHT,
		GOURAUD,
		PHONG,
		FLAT;
	}
	
	public SimpInterpreter(String filename, 
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.currentFaceShader = nullFaceShader;
		this.currentVertexShader = phongVertexShader;
		this.currentPixelShader = phongPixelShader;
		this.defaultColor = Color.WHITE;
		this.ambientLight = Color.BLACK;
		this.lightList =  new ArrayList<Light>();
		this.lighting = new Lighting (ambientLight, lightList);
		
		makeWorldToScreenTransform(drawable.getDimensions());
		reader = new LineBasedReader(filename);
		kSpecular = 0.3;
		specularExponent = 8;		
		readerStack = new Stack<>();
		
		CTMStack = new Stack<Transformation>();
		shaderStyle = ShaderStyle.PHONG;
		renderStyle = RenderStyle.FILLED;
		CTM = Transformation.identity();
		worldToCamera = Transformation.identity();
	}



//////////////////////////// Transform and Projection //////////////////////////
	
	private void makeWorldToScreenTransform(Dimensions dimensions ) {
		worldToScreen = Transformation.identity();
		double scaleX = dimensions.getWidth() / (WORLD_HIGH_X - WORLD_LOW_X);
		double scaleY = dimensions.getHeight() / (WORLD_HIGH_Y - WORLD_LOW_Y);
		worldToScreen.scale(scaleX, scaleY, 1);		
		worldToScreen.translate(dimensions.getWidth()/2.0, dimensions.getHeight()/2.0, 0);			
	}
	
	
	private void makeProjectedToScreenTransform(Dimensions dimensions,double CamLowX, double CamLowY, double CamHighX, double CamHighY) {
		
		double scaleX = dimensions.getWidth() / (CamHighX - CamLowX);
		double scaleY = scaleX; // uniform scale
//		double scaleY = dimensions.getHeight() / (CamHighY - CamLowY);	
		
		double ratio = (CamHighY - CamLowY)/(CamHighX - CamLowX)>1? 1: (CamHighY - CamLowY)/(CamHighX - CamLowX);
		
		double cameraCenterX = (CamHighX + CamLowX)/2;
		double cameraCenterY = (CamHighY + CamLowY)/2;
		
		double cameraShiftX = dimensions.getWidth()/2.0 - cameraCenterX*dimensions.getWidth();
		double cameraShiftY = dimensions.getHeight()/2.0 - ratio*cameraCenterY*dimensions.getHeight();
		
		projectedToScreen = Transformation.identity();
		projectedToScreen.scale(scaleX, scaleY, 1);		
		projectedToScreen.translate((int)Math.round(cameraShiftX), (int)Math.round(cameraShiftY), 0);			
	}
	
	
	// read the current file, if the file is empty then take the next file from the stack. 
	// read each line from the file basically. 
	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	
	// Break each line into tokens
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	
	
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;
		
		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
//		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;
		case "flat" : 		interpretFlat(tokens);		break;
		case "gouraud":		interpretGouraud(tokens);	break;
		case "phong":		interpretPhong(tokens);		break;
		case "light" :		interpretLight(tokens);		break;
		
		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}
	
	//////////////////////////////////// Shader Functions ///////////////////////////////
	
	private Polygon flatShadePolygon(Polygon poly) {
		Vertex3D p1 = poly.get(0);
		Vertex3D p2 = poly.get(1);
		Vertex3D p3 = poly.get(2);
				
		// center point
		Vertex3D cameraSpacePoint = poly.makeCameraSpacePoint();
		
	
		Point3DH faceNormal;
		if (p1.hasNormal()) {
			faceNormal = p1.getNormal().add(p2.getNormal().add(p3.getNormal()));
			faceNormal = faceNormal.scale(1.0/3.0);
			
			Transformation normTrans = worldToCamera.preMultiplyTransformation(CTM);
			normTrans.invert();
			faceNormal = normTrans.normalPostMultiplyThisTransform(faceNormal.toHalfPlaneNormal());			
			
		}else {					
			faceNormal = poly.getFaceNormal();
			}		
		
		Halfplane3DH normal = faceNormal.toHalfPlaneNormal();		
		
		Color kDiffuse = p1.getColor();		
		Color c =  lighting.light(cameraSpacePoint, kDiffuse, normal, kSpecular, specularExponent);
		poly.setPolyColor(c);	

		return poly;
	}	
	
	private Color flatShadePixel(Polygon poly, Vertex3D vert) {
		return poly.getPolyColor();
	}	
		
	
	private Vertex3D gouraudShadeVetex(Polygon poly, Vertex3D vert) {
		// calculate lighting at the vertex level
		// check if current vertex has face normal or not.
		
		Halfplane3DH normal;
		if (vert.hasNormal()) { //////////////////////////////////////////////////////////////////////////////////
			normal = vert.getNormal();			
			Transformation normTrans = worldToCamera.preMultiplyTransformation(CTM);
			normTrans.invert();
			normal = normTrans.normalPostMultiplyThisTransform(normal);
			
		}else {
			normal = poly.getFaceNormal().toHalfPlaneNormal();

		}
				
		Color kDiffuse = vert.getColor();
		Color c =  lighting.light(vert, kDiffuse, normal, kSpecular, specularExponent);		
		
		vert.setVertexShaderColor(c);		
		return vert;
	}
	
	private Color gouraundShadePixel(Polygon poly, Vertex3D vert) {
		return vert.getVertexShaderColor();
	}
	
		
	private Vertex3D phongShadeVertex(Polygon poly, Vertex3D vert) {		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<

		Halfplane3DH normal= vert.getNormal();		
	
		if (vert.hasNormal()) { 
		}else {
			normal = poly.getFaceNormal().toHalfPlaneNormal();
			vert.setNormal(normal);	
		}	
		
		vert.setToPhongShading(); // tell this is the phong		
		return vert;
	}

	private Color phongShadePixel(Polygon poly, Vertex3D vert) {
		// TODO Auto-generated method stub	
		
		Color kDiffuse = vert.getColor();
		Halfplane3DH normal = vert.getNormal();

		
		if(vert.isObj()) {		

			Transformation normTrans = worldToCamera.preMultiplyTransformation(CTM);
			normTrans.invert();
			normal = normTrans.normalPostMultiplyThisTransform(normal);
		}
		
				
		Color c =  lighting.light(vert, kDiffuse, normal, kSpecular, specularExponent);	
		
		return c;
	}
	
/////////////////////////Interpret Light/////////////////////////////////////////
	
	private void interpretLight(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);		
		double fatA = cleanNumber(tokens[4]);
		double fatB = cleanNumber(tokens[5]);
		
		Point3DH origin = new Point3DH(0,0,0);
		Point3DH lightInCS = worldToCamera.transformPoint3DH(CTM.transformPoint3DH(origin));	
		
		Light i = new Light(r, g, b, fatA, fatB, lightInCS);
		
		// render light	
		lightList.add(i);
		
	}
	
	
////////////////////////// Interpret Functions //////////////////////////
	private void wire() {		
		renderStyle = RenderStyle.WIREFRAME;		
	}
	private void filled() {
		renderStyle = RenderStyle.FILLED;		
	}

	private void interpretFlat(String[] tokens) {
		shaderStyle = ShaderStyle.FLAT;
		lighting = new Lighting (ambientLight, lightList); 
		//filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  depthCueingDrawable, flatFaceShader, nullVertexShader, flatPixelShader);
		currentFaceShader = flatFaceShader;
		currentVertexShader = nullVertexShader;
		currentPixelShader = flatPixelShader;
	}
	
	private void interpretGouraud(String[] tokens) {
		shaderStyle = ShaderStyle.GOURAUD;
		lighting = new Lighting (ambientLight, lightList);
//		filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  depthCueingDrawable, nullFaceShader, gouraudVertexShader, colorInterpolatingPixelShader)
		currentFaceShader = nullFaceShader;
		currentVertexShader = gouraudVertexShader;
		currentPixelShader = colorInterpolatingPixelShader;
	}
	
	private void interpretPhong(String[] tokens) {
		shaderStyle = ShaderStyle.PHONG;
		lighting = new Lighting (ambientLight, lightList);
//		filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  depthCueingDrawable, nullFaceShader, phongVertexShader, phongPixelShader);
		currentFaceShader = nullFaceShader;
		currentVertexShader = phongVertexShader;
		currentPixelShader = phongPixelShader;
	}
	
	private void interpretObj(String[] tokens) {
		String tempfile = tokens[1];
		tempfile = tempfile.replace("\"","");
		String fileN = tempfile+".obj";
		ObjReader objfile = new ObjReader(fileN, defaultColor); 
		objfile.render(this);
	}
	
	
	// update ambient
	private void interpretAmbient(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);		
		ambientLight = new Color(r, g, b);
		lighting = new Lighting (ambientLight, lightList);
	}	
	
	private void interpretSurface(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		kSpecular = cleanNumber(tokens[4]);
		specularExponent  = cleanNumber(tokens[5]);
		defaultColor = new Color(r, g, b);
	}
		
	private void interpretCamera(String[] tokens) {
		
		// get the current input <x low> <y low> <x high> <y high> <near clip> <far clip>
		double camLowX  = cleanNumber(tokens[1]);
		double camLowY  = cleanNumber(tokens[2]);
		double camHighX = cleanNumber(tokens[3]);	
		double camHighY = cleanNumber(tokens[4]);	
		double camNear = cleanNumber(tokens[5]);	
		double camFar = cleanNumber(tokens[6]);		
		
		makeProjectedToScreenTransform(drawable.getDimensions(), camLowX, camLowY, camHighX, camHighY); 
		
		clipper =  new Clipper(camLowX, camLowY , camHighX, camHighY, camNear, camFar, VIEW_PLANE );
		
		// get the current CTM, inverse it, assign to worldToCamera <premultiplyy every matrix later>
		worldToCamera.copyTransform(CTM);
		worldToCamera.invert();		
	}	
	


	public Vertex3D transformToCamera(Vertex3D vertex) {
		Vertex3D transformedVertex = worldToCamera.transformVertex3D(CTM.transformVertex3D(vertex));		
		
		Point3DH cameraPoint = new Point3DH(transformedVertex.getX(),transformedVertex.getY(),transformedVertex.getZ());		
		transformedVertex.setCameraPoint(cameraPoint); // save the camera space coordinates	
		
		return  transformedVertex;
	}
	
	private void push() {
		Transformation x = new Transformation();
		x.copyTransform(CTM);
		CTMStack.push(x);
	}
	
	private void pop() {
		CTM= CTMStack.pop();
	}


	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}	

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		
		// TODO: finish this method		
		CTM.post_scale(sx, sy, sz);
		
	}
	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		
		// TODO: finish this method		
		CTM.post_translate(tx, ty, tz);
		
	}
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);

		// TODO: finish this method
		double angleInRaidiance =  (angleInDegrees/180.0)*(Math.PI);
		
		switch(axisString) {
		case "X": CTM.post_rotateX(angleInRaidiance);break;
		case "Y": CTM.post_rotateY(angleInRaidiance);break;
		case "Z": CTM.post_rotateZ(angleInRaidiance);break;
		default: break;
		}		
	}
	
	private static double cleanNumber(String string) {
		return Double.parseDouble(string);
	}
	
	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);
		
		private int numTokensPerVertex;
		
		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	
//	private void interpretLine(String[] tokens) {			
//		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);
//
//		// TODO: finish this method
//		lineRenderer.drawLine(vertices[0], vertices[1], drawable);
//	}	
	
////////////////////////////////////////// Vertex to Polygon ///////////////////////////////////////////
	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		
		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}

		Vertex3D newPoint = new Vertex3D(point, color);	
		Vertex3D transformVetex = transformToCamera(newPoint);
		transformVetex.setCameraPoint(newPoint);
		return  transformVetex;	// camera inverse ctm			
	}	
	
	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);	
		Vertex3D vertices[] = new Vertex3D[numVertices];
		
		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}

	/////////////// POLY ///////////////
	private void interpretPolygon(String[] tokens) {	
		
		Vertex3D[] polygon = interpretVertices(tokens, 3, 1);
		
		
		// crop z before projection
		Chain verChain= clipper.clipPolyinZ(polygon[0], polygon[1], polygon[2]);	
		

		
		// project every vertex in verChain and save in the projectChain
		Chain projectChain =  new Chain();
		for(int i=0; i< verChain.length(); i++) {
			Vertex3D point = verChain.get(i); 			
			
			point = projectVertexToScreenSpace(point);			
			point = moveProjectedVertexToScreenCenter(point);			
			projectChain.add(point);
		}	

		
		// crop against x and y after projection				
		projectChain = clipper.clipRightAfterProj (projectChain, drawable.getWidth());
		projectChain = clipper.clipLeftAfterProj (projectChain, 0);		
		projectChain = clipper.clipTopAfterProj (projectChain, getClipWindowTop());
		projectChain = clipper.clipBottomAfterProj (projectChain, getClipWindowBottom());

		
		
		// Hacking, make copy of the projectChain
//		Chain normalChain = new Chain();
//
//		for(int i=0; i< projectChain.length(); i++) {
//			Vertex3D vert =  projectChain.get(i);
//			Point3DH normal = vert.getNormal();
//			Vertex3D normalVet = new Vertex3D(normal.getX(), normal.getY(), normal.getY(), Color.WHITE);
//			
//			normalChain.add(normalVet);
//		}
		
		
		// break apart polygons to triangles
		for(int i=1; i< projectChain.length()-1; i++) {
			
			Vertex3D p1 = projectChain.get(0);
			Vertex3D p2 = projectChain.get(i);	
			Vertex3D p3 = projectChain.get(i+1);
			

			
//			p1.getNormal().printNormal();
//			p2.getNormal().printNormal();
//			p3.getNormal().printNormal();

			if (shaderStyle == ShaderStyle.FLAT) {
				filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  depthCueingDrawable, flatFaceShader, nullVertexShader, flatPixelShader);
			}
			
			if (shaderStyle == ShaderStyle.GOURAUD) {
				filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  depthCueingDrawable, nullFaceShader, gouraudVertexShader, colorInterpolatingPixelShader);
			}
			
			if (shaderStyle == ShaderStyle.PHONG) {
				filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  depthCueingDrawable, nullFaceShader, phongVertexShader, phongPixelShader);
			}
			
			if(shaderStyle == ShaderStyle.LIGHT) {	
				filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  depthCueingDrawable);
			}
			
		}
	}
	
	
	
///////////////////////////// Window Clipper //////////////////////////////	
	
	public int getClipWindowTop() {
		double clipWindowRatio = clipper.ratio;
		double clipWindowTop = 0.5* drawable.getHeight() + 0.5* drawable.getHeight()*clipWindowRatio;
		return (int)Math.round(clipWindowTop);
	}
	
	public int getClipWindowBottom() {
		double clipWindowRatio = clipper.ratio;
		double clipWindowBottom = 0.5* drawable.getHeight() - 0.5* drawable.getHeight()*clipWindowRatio;
		return (int)Math.round(clipWindowBottom);
	}
	
	

	
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	public Vertex3D projectVertexToScreenSpace(Vertex3D point) {		
		double factor = VIEW_PLANE/point.getZ();
		
		Vertex3D projectVertex = new Vertex3D(factor*point.getX(), factor*point.getY(), point.getZ(), point.getColor());
		
		projectVertex.setCameraPoint(point);
		
		if (point.hasNormal()) {
			projectVertex.setNormal(point.getNormal());
		}
	
		if (point.isForPhongShading()) {
			projectVertex.setToPhongShading();			
		}
		
		return projectVertex;
		
	}
	
	public Vertex3D moveProjectedVertexToScreenCenter(Vertex3D point) {
		return projectedToScreen.transformVertex3D(point);
	}

	// assignment 3 provided new function
	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		Point3DH point = new Point3DH(x, y, z, w);
		return point;
	}
//	
//	private void objFile(String filename) {
//		ObjReader objReader = new ObjReader(filename, defaultColor);
//		objReader.read();
//		objReader.render();
//	}
//	
	public void interpretDepth(String[] tokens) {
		
		fogNear = (int) cleanNumber(tokens[1]);
		fogFar = (int) cleanNumber(tokens[2]);
		
		double r = cleanNumber(tokens[3]);
		double g = cleanNumber(tokens[4]);
		double b = cleanNumber(tokens[5]);	
		
		Color farColor = new Color(r,g,b);
		depthCueingDrawable =  new DepthCueingDrawable(drawable, fogNear, fogFar, farColor);
		
	};
	
	public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		return new Point3DH(x, y, z);
	}
	
	
	public static Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		return new Color(r, g, b);	
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		// TODO: finish this method
	}
	
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		Vertex3D screenP3 = transformToCamera(p3);
		// TODO: finish this method		
	}



}
