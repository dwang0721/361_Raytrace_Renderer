package client.testPages;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import polygon.Polygon;
import polygon.PolygonRenderer;
import geometry.Vertex3D;

public class StarburstPolygonTest {
	private static final int NUM_RAYS = 90;
	private static final double FRACTION_OF_PANEL_FOR_DRAWING = 0.9;

	private final PolygonRenderer renderer;
	private final Drawable panel;
	Vertex3D center;
	
	public StarburstPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;	
		
		makeCenter();
		render();
	}
	
	private void render() {			
		double radius = computeRadius();
		double angleDifference = (2.0 * Math.PI) / NUM_RAYS;
		double angle = 0.0;
		
		for (int ray = 0; ray < NUM_RAYS; ray++) {
			Vertex3D p1 = radialPoint(radius, angle);
			Vertex3D p2 = radialPoint(radius, angle + angleDifference);
			
			Vertex3D[] vertices= {center, p1, p2};
			Polygon test_polygon = Polygon.make(vertices);
			renderer.drawPolygon(test_polygon,  panel);			
			angle = angle + angleDifference;
			//System.out.println( p1.getIntX() +"," + p1.getIntY()+" | "+ p2.getIntX() +"," + p2.getIntY() );
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
