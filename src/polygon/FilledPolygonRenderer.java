package polygon;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import line.DDALineRenderer;
import line.LineRenderer;
import polygon.shading.FaceShader;
import polygon.shading.PixelShader;
import polygon.shading.VertexShader;

import java.math.BigDecimal;

import geometry.Vertex3D;

public class FilledPolygonRenderer implements PolygonRenderer{

	private FilledPolygonRenderer() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable panel, FaceShader faceShader, VertexShader vertexShader, PixelShader pixelShader ) {
		
		LineRenderer DDAdrawer = DDALineRenderer.make();
		
		Polygon orderVertices = Polygon.makeEnsuringClockwise(polygon.get(0),polygon.get(1),polygon.get(2));
		
		Chain leftChain = orderVertices.leftChain();		
		Chain rightChain = orderVertices.rightChain();
		
		Vertex3D p_top = rightChain.get(0); // top point
		Vertex3D p_left = (rightChain.length()>=2)? leftChain.get(1):rightChain.get(2);
		Vertex3D p_right = (leftChain.length()>=2)? rightChain.get(1):leftChain.get(2); 		
		//Vertex3D p_left = leftChain.get(1);
		//Vertex3D p_right = rightChain.get(1);  		
		
		// left dda
		double deltaXL = p_top.getX()-p_left.getIntX();
		double deltaYL = p_top.getY()-p_left.getIntY();		
		double mL = deltaXL/deltaYL;	

		
		// right dda
		double deltaXR = p_right.getX()-p_top.getIntX();
		double deltaYR = p_right.getY()-p_top.getIntY();
		double mR = deltaXR/deltaYR;

		
		// lower dda
		double deltaXLower = p_right.getX()-p_left.getIntX();
		double deltaYLower = p_right.getY()-p_left.getIntY();
		double mLower = deltaXLower/deltaYLower;

		
		// find the y_bottom and y_mid
		int y_bottom = Math.min (p_left.getIntY(), p_right.getIntY());
		int y_mid = Math.max (p_left.getIntY(), p_right.getIntY());
		
		// define color
		Color color = Color.random();
		
		//
		double fxl; 
		double fxr;
		
		if (p_top.getY()-p_left.getIntY()!=0 && p_top.getY()-p_right.getIntY()!=0) { // top is not flat, normal triangle
			// initialize right and left x for line drawings. 
			fxl = p_top.getIntX(); 
			fxr = p_top.getIntX();
			
			for (int y = p_top.getIntY(); y> y_bottom; y--) {				
				if(y>y_mid ) { // corner case will not enter here 
					fxl = fxl - mL;
					fxr = fxr - mR;
				}				
				// mid point left, switch left ddl to lower ddl 
				if(y<=y_mid && p_left.getIntY() > p_right.getIntY() ) { 
					fxl = fxl - mLower;
					fxr = fxr - mR;
					}			
				// mid point on right, witch the right ddl
				if (y<=y_mid && p_left.getIntY() < p_right.getIntY()){
					fxl = fxl - mL;
					fxr = fxr - mLower;
					}	
				
				if((int)Math.round(fxr-1) >= (int)Math.round(fxl)) {
					Vertex3D pL = new Vertex3D ((int)Math.round(fxl), y, 0,  color);
					Vertex3D pR = new Vertex3D ((int)Math.round(fxr-1), y, 0,  color);	
					DDAdrawer.drawLine(pL, pR, panel);
					}
				}			
			}  
		
			// top flat, left point and top point construct a flat line				
			if (p_top.getY()-p_left.getIntY() == 0 ) {
				fxl = p_left.getIntX(); 
				fxr = p_top.getIntX();				
				
				for (int y = p_top.getIntY(); y> y_bottom; y--) {
					fxl = fxl - mLower;
					fxr = fxr - mR;
					
					if((int)Math.round(fxr-1) >= (int)Math.round(fxl)) {
						//System.out.println(fxl+"|"+fxr);
						Vertex3D pL = new Vertex3D ((int)Math.round(fxl), y, 0,  color);
						Vertex3D pR = new Vertex3D ((int)Math.round(fxr-1), y, 0,  color);	
						DDAdrawer.drawLine(pL, pR, panel);
					}
				}					
			}
			
			
			if (p_top.getY()-p_right.getIntY() == 0 ) {
				fxl = p_top.getIntX(); 
				fxr = p_right.getIntX();
				
				for (int y = p_top.getIntY(); y> y_bottom; y--) {
					fxl = fxl - mL;
					fxr = fxr - mLower;
					
					if( (int)Math.round(fxr-1) >= (int)Math.round(fxl) ) {
						Vertex3D pL = new Vertex3D ((int)Math.round(fxl), y, 0,  color);
						Vertex3D pR = new Vertex3D ((int)Math.round(fxr-1), y, 0,  color);	
						DDAdrawer.drawLine(pL, pR, panel);
					}
				}		
			}	
		

		// test the point position
//		panel.setPixel(p_top.getIntX(), p_top.getIntY(), 0.0, Color.RED.asARGB());
//		panel.setPixel(p_left.getIntX(), p_left.getIntY(), 0.0, Color.GREEN.asARGB());
//		panel.setPixel(p_right.getIntX(), p_right.getIntY(), 0.0, Color.BLUE.asARGB());
		
	}

	public static PolygonRenderer make() {
		return new FilledPolygonRenderer();	
	}
	
}
