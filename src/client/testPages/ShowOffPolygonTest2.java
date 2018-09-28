package client.testPages;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import polygon.Polygon;
import polygon.PolygonRenderer;
import geometry.Vertex3D;

public class ShowOffPolygonTest2 {
	private static final int NUM_RAYS = 90;
	private static final double FRACTION_OF_PANEL_FOR_DRAWING = 0.9;

	private final PolygonRenderer renderer;
	private final Drawable panel;
	Vertex3D center;
	
	public ShowOffPolygonTest2 (Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;	
		
		makeCenter();
		render();
	}
	
	private void render() {			
		double radius = computeRadius()*0.7;
		double angleDifference = (2.0 * Math.PI) / 50;
		double angle = Math.random();
		int change = -1;
		
		while (radius>5) {
			
			double random = Math.random()*0.4 + 1;
			angleDifference = random*(2.0 * Math.PI) / 50;
			double r = radius*random + 1.5*Math.cos(2*angle);
			Vertex3D p1 = radialPoint(r, angle);
			Vertex3D p2 = radialPoint(r, angle+ angleDifference);
			Vertex3D p3 = radialPoint((1-change*0.5)*r, angle+ angleDifference*0.5);			
			Vertex3D[] vertices= {p1, p2, p3};
			Polygon test_polygon = Polygon.make(vertices);
			renderer.drawPolygon(test_polygon,  panel);			
			angle = angle + angleDifference;	
			
			
			radius-= 0.7;
			change*=-1;
		}	
		
	}
	
	private void makeCenter() {
		int centerX = panel.getWidth() / 2;
		int centerY = panel.getHeight() / 2;
		center = new Vertex3D(centerX, centerY, 0, Color.random());
	}
	
	private Vertex3D radialPoint(double radius, double angle) {
		double x = center.getX() + radius * Math.cos(angle);
		double y = center.getY() + radius * Math.sin(angle);
		return new Vertex3D((int)Math.round(x),(int)Math.round(y), 0, Color.WHITE);
	}
	
	private double computeRadius() {
		int width = panel.getWidth();
		int height = panel.getHeight();
		
		int minDimension = width < height ? width : height;
		
		return (minDimension / 2.0) * FRACTION_OF_PANEL_FOR_DRAWING;
	}
}



	
