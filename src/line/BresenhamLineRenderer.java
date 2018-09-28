package line;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.shading.PixelShader;
import windowing.drawable.Drawable;

public class BresenhamLineRenderer implements LineRenderer{
	// use the static factory make() instead of constructor.
	private BresenhamLineRenderer() {}
		
//	Bresenham_line(p0, p1)
//		dx = x1 – x0
//		dy = y1 – y0
//		m = 2*dy
//		q = m – 2*dx
//		SetPixel(p0)
//	
//		y = y0
//		err = m - dx
//		for x = x0 + 1 to x1 do
//			if err >= 0
//				err += q
//				y++
//			else
//				err += m
	
//			SetPixel(x, y)

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Polygon poly, Drawable drawable, PixelShader pixelShader) {
		int argbColor = p1.getColor().asARGB();
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		double m = 2*deltaY;
		double q = m- 2*deltaX;
		
		drawable.setPixel(p1.getIntX(), p1.getIntY(), 0.0, argbColor);
		
		int y = p1.getIntY();
		double err = m - deltaX;		
		
		for(int x = p1.getIntX()+1; x <= p2.getIntX(); x++) {
			if (err>=0) {
				err +=q;
				y++;
			}else {
				err+=m;				
			}
		drawable.setPixel(x, y, 0.0, argbColor);
		}		

	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new BresenhamLineRenderer());
		
	}

}


