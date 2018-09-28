package client.testPages;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import polygon.Polygon;
import polygon.PolygonRenderer;
import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;

public class OnePolygonTest {
	
	private final PolygonRenderer renderer;
	private final Drawable panel;
	
	public OnePolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;
		System.out.println("Render Test1");
		render();
	}
	
	private void render() {
		
		
//		double depth =  Math.random()*(-198)-1;
//		depth =  -1;
		
		Vertex3D p1 = new Vertex3D (30, 150, -100, Color.RED);
		Vertex3D p2 = new Vertex3D (200, 400, -100,   Color.RED);	
		Vertex3D p3 = new Vertex3D (400, 100, -100,   Color.RED);
//		Vertex3D p4 = new Vertex3D (300, 600, depth,  Color.GREEN);	
//		Vertex3D p5 = new Vertex3D (50, 30, depth,  Color.GREEN);	
//		Vertex3D p6 = new Vertex3D (600, 15, depth,  Color.GREEN);	
//		Vertex3D pl = new Vertex3D (0, 50, depth,  Color.BLUE);	
//		Vertex3D pr = new Vertex3D (650, 50, depth,  Color.BLUE);	
		
//		 polygon test
		Polygon test_polygon1 = Polygon.make(p3,p1,p2);
//		Polygon test_polygon2 = Polygon.make(p4,p5,p6);
		renderer.drawPolygon(test_polygon1,  panel);	
//		renderer.drawPolygon(test_polygon2,  panel);	
		
//		 line test
//		LineRenderer DDAdrawer = DDALineRenderer.make();
//		DDAdrawer.drawLine(pl, pr, panel);
//		DDAdrawer.drawLine(p1, p2, panel);
//		DDAdrawer.drawLine(p1, p4, panel);
//		DDAdrawer.drawLine(p2, p3, panel);
//		DDAdrawer.drawLine(p3, p4, panel);
		
	}
}
