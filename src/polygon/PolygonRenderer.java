package polygon;

import polygon.Shader;
import polygon.shading.FaceShader;
import polygon.shading.PixelShader;
import polygon.shading.VertexShader;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	// assumes polygon is ccw.
	public void drawPolygon(Polygon polygon, Drawable drawable, FaceShader faceShader, VertexShader vertexShader, PixelShader pixelShader); // pass your own shader function.

	default public void drawPolygon(Polygon polygon, Drawable panel) { // default c->c use the same color. 
		drawPolygon(polygon, panel,  poly->poly, (poly, v)->v, (poly, current) -> current.getColor());
	};

}
