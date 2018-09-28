package line;

import geometry.Halfplane3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.shading.PixelShader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineRenderer implements LineRenderer{
	// use the static factory make() instead of constructor.
	private DDALineRenderer() {}

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Polygon poly, Drawable drawable, PixelShader pixelShader) {
	
	
		
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		double slope =  deltaY / deltaX;
		
		double recDeltaZ= 1.0/p2.getZ() - 1.0/p1.getZ();
		double recStepZ = recDeltaZ / deltaX;
		
		// color
		double deltaR = (p2.getColor().getR()/p2.getZ()  - p1.getColor().getR()/p1.getZ() );
		double deltaG = (p2.getColor().getG()/p2.getZ()  - p1.getColor().getG()/p1.getZ() );
		double deltaB = (p2.getColor().getB()/p2.getZ()  - p1.getColor().getB()/p1.getZ() );
		
		double slopeR = deltaR / deltaX;
		double slopeG = deltaG / deltaX;
		double slopeB = deltaB / deltaX;
		
		
		// cam space point
		double deltaCSX =  p2.getCSX()/p2.getZ() - p1.getCSX()/p1.getZ();
		double deltaCSY =  p2.getCSY()/p2.getZ() - p1.getCSY()/p1.getZ();
		double deltaCSZ =  1/p2.getCSZ() - 1/p1.getCSZ();
		
		double slopeCSX = deltaCSX / deltaX;
		double slopeCSY = deltaCSY / deltaX;
		double slopeCSZ = deltaCSZ / deltaX;
		
		//norm 
		double deltaNormX = p2.getNormal().getX()/ p2.getZ() - p1.getNormal().getX()/ p1.getZ();
		double deltaNormY = p2.getNormal().getY()/ p2.getZ() - p1.getNormal().getY()/ p1.getZ();
		double deltaNormZ = p2.getNormal().getZ()/ p2.getZ() - p1.getNormal().getZ()/ p1.getZ();
		
		double slopeNormX = deltaNormX / deltaX;
		double slopeNormY = deltaNormY / deltaX;
		double slopeNormZ = deltaNormZ / deltaX;
		
		

		double y = p1.getIntY();
		
		double recz = 1.0/p1.getZ();
		
		// color
		double r = p1.getColor().getR()/p1.getZ();
		double g = p1.getColor().getG()/p1.getZ();
		double b = p1.getColor().getB()/p1.getZ();
		
		// cam space point
		double csx = p1.getCSX()/p1.getZ();
		double csy = p1.getCSY()/p1.getZ();
		double csz = 1/p1.getCSZ();		
		
		// norm
		
		double normX = p1.getNormal().getX()/ p1.getZ();
		double normY = p1.getNormal().getY()/ p1.getZ();
		double normZ = p1.getNormal().getZ()/ p1.getZ();
		
		
		
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			
			Color c = new Color(r/recz, g/recz, b/recz);	
			Vertex3D cameraSpacePoint =  new Vertex3D(csx/recz, csy/recz, 1.0/recz, p1.getColor());
			Halfplane3DH normal =  new Halfplane3DH(normX/recz, normY/recz, normZ/recz);
			
			cameraSpacePoint.setNormal(normal);
			cameraSpacePoint.setIsObj(p1);
			
			
			if(p1.isForPhongShading()) {
				c = pixelShader.shade(poly, cameraSpacePoint);
			}
			
			drawable.setPixel(x, (int)Math.round(y), 1.0/recz, c.asARGB());
			
			y = y + slope;			

			recz = recz + recStepZ;
			
			// color
			r += slopeR;
			g += slopeG;
			b += slopeB;
			
			// cam space point
			csx += slopeCSX;
			csy += slopeCSY;
			csz += slopeCSZ;
			
			// norm
			normX += slopeNormX;
			normY += slopeNormY;
			normZ += slopeNormZ;

		}

	}
	

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new DDALineRenderer());
		
	}

}


