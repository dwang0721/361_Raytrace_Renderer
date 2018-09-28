package client.testPages;


import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class ParallelogramTest {
	
	private final LineRenderer renderer;
	private final Drawable panel;		
	
	public ParallelogramTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;		
		render();
	}
	
	private void render() {		
		int height = panel.getHeight();
		
		for (int p = 0; p <50; p++) {
			Vertex3D start = new Vertex3D (20, height-80-p, 0, Color.WHITE);
			Vertex3D end = new Vertex3D (150, height-150-p, 0, Color.WHITE);
			renderer.drawLine(start, end, panel);
			
			start = new Vertex3D (160+p, height-270, 0, Color.WHITE);
			end = new Vertex3D (240+p, height-40, 0, Color.WHITE);
			renderer.drawLine(start, end, panel);
		}	
		
	}
}
