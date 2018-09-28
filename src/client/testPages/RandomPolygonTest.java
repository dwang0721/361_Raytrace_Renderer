package client.testPages;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import polygon.Polygon;
import polygon.PolygonRenderer;

import java.util.Random;

import geometry.Vertex3D;

public class RandomPolygonTest {
	
	private static final int TRIANGLE_NUM = 20;
	private final PolygonRenderer renderer;
	private final Drawable panel;
	
	static long seed = (int)(Math.random()*100000);
	
	public RandomPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel =  panel;
		this.renderer = renderer;
		
		render();
	}

	private void render() {
	    // create random object
	    Random rnd = new Random();

	    // setting seed
	    rnd.setSeed(seed);
		
		for(int i=0; i<TRIANGLE_NUM; i++ ) {
			Vertex3D p1 = new Vertex3D((int)(rnd.nextDouble()*panel.getWidth()), (int)(rnd.nextDouble()*panel.getHeight()), 0.0, Color.random());
			Vertex3D p2 = new Vertex3D((int)(rnd.nextDouble()*panel.getWidth()), (int)(rnd.nextDouble()*panel.getHeight()), 0.0, Color.random());
			Vertex3D p3 = new Vertex3D((int)(rnd.nextDouble()*panel.getWidth()), (int)(rnd.nextDouble()*panel.getHeight()), 0.0, Color.random());
			
			Polygon polygon = Polygon.make(p1, p2, p3);
			
			renderer.drawPolygon(polygon, panel);			
			}
	}
	
	
}
