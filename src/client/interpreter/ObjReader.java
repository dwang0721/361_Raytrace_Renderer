package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import client.interpreter.SimpInterpreter.RenderStyle;
import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Chain;
import polygon.Polygon;
import windowing.graphics.Color;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	// define vertex by 3 properties
	private class ObjVertex {  
		//Store indices for a vertex, a texture, and a normal.  
		// Have getters for them.
		int vertexIndex;
		int textureIndex;
		int normalIndex;
		
		public ObjVertex(int index, int texture, int normal) {
			this.vertexIndex = index;
			this.textureIndex = texture;
			this.normalIndex =  normal;
		}
		
		public int getVertexID() {
			return vertexIndex;
		}
		
		public int getTextureID() {
			return textureIndex;
		}
		
		public int getNormalID() {
			return normalIndex;
		}
		
	}
	
	// read https://stackoverflow.com/questions/8098615/extending-a-java-arraylist
	// store vertex info that defines a Face
	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;			
	}	
	
	private LineBasedReader reader;	
	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;
	private Color defaultColor;
	
	ObjReader(String filename, Color defaultColor) {
		this.reader = new LineBasedReader(filename); // I did not pass reader to anything
		this.objVertices = new ArrayList<Vertex3D>();		
		this.transformedVertices =  new ArrayList<Vertex3D>();		
		this.objNormals = new ArrayList<Point3DH>();
		this.objFaces = new ArrayList<ObjFace>();
		this.defaultColor =  defaultColor;
		read();
	}

	public void render(SimpInterpreter interpreter) {

		// First, transform all of the vertices. 
		
		for(Vertex3D vert: objVertices ) {
			vert = interpreter.transformToCamera(vert); // transform to camera space
			transformedVertices.add(vert);
		}
		
		// Then, go through each face, break into triangles if necessary, and send each triangle to the renderer.
		
		for (ObjFace face: objFaces) { //ObjFace face contains the index of the polygon vertex;
			// get polygon chain from each face
			Chain poly = polygonForFace(face); 
//			poly.print();
			
			
			// crop against z
			poly = interpreter.clipper.clipSideFront(poly);
			poly = interpreter.clipper.clipSideEnd(poly);
			
			// change project
			Chain projectChain =  new Chain();
			
			for(int i=0; i< poly.length(); i++) {
				Vertex3D point = poly.get(i);
				
				/////// problem here!!!!
				point = interpreter.projectVertexToScreenSpace(point);				
				
				point = interpreter.moveProjectedVertexToScreenCenter(point);
				projectChain.add(point);
				
			}		
			
			projectChain = interpreter.clipper.clipRightAfterProj (projectChain, interpreter.drawable.getWidth());	
			projectChain = interpreter.clipper.clipLeftAfterProj (projectChain, 0);
			projectChain = interpreter.clipper.clipTopAfterProj (projectChain, interpreter.getClipWindowTop());
			projectChain = interpreter.clipper.clipBottomAfterProj (projectChain, interpreter.getClipWindowBottom());
			
						
			// break into triangles			
			if (interpreter.renderStyle == RenderStyle.WIREFRAME) {
				Polygon wirePoly =  Polygon.make(projectChain.getVerticeArray());
				interpreter.wireframeRenderer.drawPolygon(wirePoly,  interpreter.drawable);
			}
			
			if(interpreter.renderStyle == RenderStyle.FILLED) {	
				for(int i=1; i< projectChain.length()-1; i++) {
					Vertex3D p1 = projectChain.get(0);
					Vertex3D p2 = projectChain.get(i);	
					Vertex3D p3 = projectChain.get(i+1);	
					p1.setThisToObj();
					p2.setThisToObj();
					p3.setThisToObj();
					// send to the renderer					
					interpreter.filledRenderer.drawPolygon(Polygon.make(p1,p2,p3),  interpreter.depthCueingDrawable, interpreter.currentFaceShader, interpreter.currentVertexShader, interpreter.currentPixelShader);
				}
			}
		}		
	}
	
	private Polygon polygonForFace(ObjFace face) {
		// TODO: This function might be used in render() above.  Implement it if you find it handy.
		// make polygon structure base on the face, polygon is similar to chain
		
		Polygon poly = Polygon. makeEmpty(); // polygon is s sequence of vertex3D
		
		// ArrayList<ObjVertex> faces, face is an array list of ObjVertex
		// Each ObjVertex, f contains getVertexID(), getTextureID(), getNormalID()
		// objV is a vertex representation that builds the face
		
		for(ObjVertex objV: face) { ////////////////////////////////// change vertext attribute here!!!!!!!!!
			int vertID = objV.getVertexID();
//			int textID = objV.getTextureID();
			int normID = objV.getNormalID();
			
			Vertex3D vert = transformedVertices.get(vertID-1);
			
//			objNormals.get(normID-1).printNormal();
			if (!objNormals.isEmpty()) {
				vert.setNormal(objNormals.get(normID-1));
				
			}			
			
//			vert.getNormal().printNormal();
			poly.add(vert);// add the vertex to the poly-chain.
		}
		
		return poly;
	}

	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		
		for(int i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());


			ObjVertex vert = new ObjVertex(vertexIndex, textureIndex, normalIndex);
			face.add(vert);
		}

		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		// write this.  subtokens[tokenIndex], if it exists, holds a string for an index.
		// use Integer.parseInt() to get the integer value of the index.
		// Be sure to handle both positive and negative indices.
		String t = null;
		int int_t;
		
		
		if(tokenIndex>baseForNegativeIndices) {return baseForNegativeIndices % tokenIndex +1;}
	
		t = subtokens[tokenIndex];	
		
		if (t.equals("")) {return NOT_SPECIFIED;}
		
		int_t = Integer.parseInt(t);
		if (int_t >= 0) {
			return int_t;
		}else {
			return baseForNegativeIndices + int_t;
		}
		
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);				
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		
		// TODO: fill in action to take here.
		objNormals.add(normal);		
	}
	
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);

		// TODO: fill in action to take here. ///////////////////////////////////////////////////// creating vertex here
		Vertex3D vertex = new Vertex3D(point, color);
		objVertices.add(vertex);
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}