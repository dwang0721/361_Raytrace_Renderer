package polygon;

import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import polygon.shading.FaceShader;
import polygon.shading.PixelShader;
import polygon.shading.VertexShader;
import windowing.drawable.Drawable;

public class WireframePolygonRenderer implements PolygonRenderer{

	private WireframePolygonRenderer() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable panel, FaceShader faceShader, VertexShader vertexShader, PixelShader pixelShader ) {
		// TODO Auto-generated method stub
		LineRenderer DDAdrawer = DDALineRenderer.make();
		
//		Polygon orderVertices = Polygon.makeEnsuringClockwise(polygon.get(0),polygon.get(1),polygon.get(2));
//		Polygon orderVertices = Polygon.makeEnsuringClockwise(polygon.getVerticeArray());
		
//		Chain leftChain = orderVertices.leftChain();		
//		Chain rightChain = orderVertices.rightChain();		
//		
//		if(rightChain.length()== 1 && leftChain.length()==1) {
//			// catch corner case and do nothing. 
//			return;
//		}		
		
		for (int i=0; i<polygon.length();i++) {
			Vertex3D p_1 = polygon.get(i);
			Vertex3D p_2 = polygon.get((i+1) % polygon.length()); 
			DDAdrawer.drawLine(p_1, p_2, panel);
		}		
	}
	
	
	public static PolygonRenderer make() {
		return new WireframePolygonRenderer();	
	
	}
}
