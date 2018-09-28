package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import java.util.Random;

public class RandomLineTest {
	
	private final LineRenderer renderer;
	private final Drawable panel;		

	static long seed = (int)(Math.random()*100000);
	
	public RandomLineTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;		
		render();
	}
	
	private void render() {
		
	    // create random object
	    Random rnd = new Random();

	    // setting seed
	    rnd.setSeed(seed);

		
		for (int i=0; i< 30; i++) {
			int x1 = (int)(rnd.nextDouble()* panel.getWidth());
			int x2 = (int)(rnd.nextDouble()* panel.getWidth());
			int y1 = (int)(rnd.nextDouble()* panel.getWidth());
			int y2 = (int)(rnd.nextDouble()* panel.getWidth());
			
			Vertex3D p1 = new Vertex3D (x1, y1, 0, Color.random(rnd));
			Vertex3D p2 = new Vertex3D (x2, y2, 0, Color.random(rnd));	
			
			renderer.drawLine(p1, p2, panel);
		}
		
	}
}
