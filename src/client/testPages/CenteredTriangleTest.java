package client.testPages;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class CenteredTriangleTest {

	private final PolygonRenderer renderer;
	private final Drawable panel;
	private Vertex3D center;
	private double r = 275.0;
	private int numTriangle = 6;
	private Polygon[] triangles=new Polygon[numTriangle];
	
	public CenteredTriangleTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;	
		
		makeCenter();
		makeTriangles();
		render();
	}
	
	private void render() {

		for (Polygon triangle : triangles) {
				//System.out.println(triangle.get(1).getIntZ() +" | "+triangle.get(2).getIntZ() +" | "+ triangle.get(3).getIntZ() +" | " + triangle.get(1).getColor().asARGB() );
				renderer.drawPolygon(triangle,  panel);	
		}
		
	}
	
	private void makeTriangles() {		
		
		double angleDifference = (2*Math.PI) / 3.0;
		double angleStart;
		double depth;
		Color color;
		double v1;
		
		for (int i=0; i< numTriangle; i++) {
			
			angleStart = (2.0 * Math.PI)/3 *Math.random(); // random angle
			depth =  Math.random()*(-198)-1; // random depth
			
			v1 = 1-0.15*i;
			
			color = Color.WHITE.scale(v1);
			
			Vertex3D p1 = radialPoint(r, angleStart, depth, color);
			Vertex3D p2 = radialPoint(r, angleStart + angleDifference, depth, color);
			Vertex3D p3 = radialPoint(r, angleStart + 2*angleDifference, depth, color);		
			
			Polygon triangle = Polygon.make(p1,p2,p3);			
			triangles[i] = triangle;
		}
	}
	
	private void makeCenter() {
		int centerX = panel.getWidth() / 2;
		int centerY = panel.getHeight() / 2;
		center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
	}
	
	private Vertex3D radialPoint(double radius, double angle, double z, Color color) {
		double x = center.getX() + radius * Math.cos(angle);
		double y = center.getY() + radius * Math.sin(angle);
		return new Vertex3D(x, y, z, color);
	}

}
