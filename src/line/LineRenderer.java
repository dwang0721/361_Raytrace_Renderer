package line;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.shading.PixelShader;
import windowing.drawable.Drawable;

public interface LineRenderer {
	public void drawLine(Vertex3D p1, Vertex3D p2, Polygon poly, Drawable panel, PixelShader pixelShader);
	
	default public void drawLine(Vertex3D p1, Vertex3D p2, Drawable panel) { // default c->c use the same color. 
		drawLine(p1, p2, null, panel, (poly, vert) -> vert.getColor());
	};
}
