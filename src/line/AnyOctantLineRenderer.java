package line;

import geometry.Vertex;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.shading.PixelShader;
import windowing.drawable.Drawable;

// switches endpoints so that it only has to deal with the first four octants.

public class AnyOctantLineRenderer implements LineRenderer {
	private LineRenderer singleOctantRenderer;

	public AnyOctantLineRenderer(LineRenderer singleOctantRenderer) {
		super();
		this.singleOctantRenderer = singleOctantRenderer;
	}

	public void drawLine(Vertex3D p1, Vertex3D p2, Polygon poly, Drawable drawable, PixelShader pixelShader) {
		if(inOctantsItoIV(p1, p2)) {
			drawUpwardsLine(p1, p2, poly, drawable, pixelShader);
		}
		else {
			drawUpwardsLine(p2, p1, poly, drawable, pixelShader);
		}
	}

	public boolean inOctantsItoIV(Vertex3D p1, Vertex3D p2) {
		return p2.getY() > p1.getY();
	}

	public void drawUpwardsLine(Vertex3D q1, Vertex3D q2, Polygon poly, Drawable drawable, PixelShader pixelShader) {
		Octant octant = findOctant(q1, q2);
		Vertex3D transformedQ1 = octant.toOctant1(q1);
		Vertex3D transformedQ2 = octant.toOctant1(q2);
		
		transformedQ1.setCameraPoint(q1.getCameraSpacePoint());
		transformedQ1.setNormal(q1.getNormal());
		transformedQ2.setCameraPoint(q2.getCameraSpacePoint());
		transformedQ2.setNormal(q2.getNormal());
		if (q1.isForPhongShading()) {
			transformedQ1.setToPhongShading();
			transformedQ2.setToPhongShading();					
		}
		
		transformedQ1.setIsObj(q1);
		transformedQ2.setIsObj(q2);		
		
		singleOctantRenderer.drawLine(transformedQ1, transformedQ2, poly, octant.invertingDrawable(drawable), pixelShader);
	}
	

	private Octant findOctant(Vertex q1, Vertex q2) {
		int vx = q2.getIntX() - q1.getIntX();
		int vy = q2.getIntY() - q1.getIntY();
		return Octant.octantForVector(vx, vy);
	}
}
