package client;

import polygon.Polygon;
import windowing.graphics.Color;
import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Chain;

	
public class Clipper {
	double top;
	double bottom;
	double left;
	double right;
	double front;
	double end;
	double viewPlane;
	public double ratio;
	
	//<x low> <y low> <x high> <y high> <near clip> <far clip>
	public Clipper (double l, double b, double r, double t, double f, double e, double d) {
		this.top = t;
		this.bottom = b;
		this.left = l;
		this.right = r;
		this.front = f;
		this.end = e;
		this.viewPlane = -1;
		this.ratio = ((t-b)/(r-l) >1) ? 1:(t-b)/(r-l) ;
//		System.out.println("ratio:" + ratio);
	};
	
	
	//cut polygons, front and back only
	public Chain clipPolyinZ(Vertex3D p0, Vertex3D p1, Vertex3D p2){	

		Chain inputChain = new Chain(p0, p1, p2);
		
		inputChain = clipSideFront(inputChain);
		inputChain = clipSideEnd(inputChain);		
		
		return inputChain;
	}
	
	public Chain cliPolyinZ(Chain inputChain) {
		inputChain = clipSideFront(inputChain);
		inputChain = clipSideEnd(inputChain);				
		return inputChain;
	}
	
	
	public Chain clipLeftAfterProj (Chain verChain, int screenLeft) {
		
		Chain outputChain = new Chain();
		
		for (int i =0; i < verChain.length(); i++ ) {
			Vertex3D start 	= 	verChain.get(i);
			Vertex3D end	= 	verChain.get((i+1) % verChain.length());
			
			// both in
			if (start.getX() >= screenLeft && end.getX() >= screenLeft) {
				outputChain.add(end);					
			}
			
			// both out
			if (start.getX() < screenLeft && end.getX() < screenLeft) {
				// do nothing
			}	
			
			// intersection
			double deltaX = end.getX() - start.getX();
			double deltaY = end.getY() - start.getY();
			double deltaCSX = end.getCSX()/end.getCSZ() - start.getCSX()/start.getCSZ();
			double deltaCSY = end.getCSY()/end.getCSZ() - start.getCSY()/start.getCSZ();
			double deltaCSZ = 1/end.getCSZ() - 1/start.getCSZ();	
			double recDeltaZ = 1/end.getZ() - 1/start.getZ();		
			
			double intersectX = screenLeft;
			double ratio = (intersectX-start.getX())/(deltaX);	
			double intersectY = start.getY() + deltaY*ratio;
			
			double intersectCSZ = 1/(1/start.getCSZ() + deltaCSZ*ratio);
			double intersectCSX = (start.getCSX()/start.getZ() + deltaCSX*ratio)*intersectCSZ;
			double intersectCSY = (start.getCSY()/start.getZ() + deltaCSY*ratio)*intersectCSZ;
			double intersectZ = 1/( 1/start.getZ() + recDeltaZ*ratio);
			
			Color intersectColor =  start.getColor();
			Vertex3D intersect = new Vertex3D(intersectX, intersectY, intersectZ, intersectColor);
			intersect.setCameraPoint(new Point3DH(intersectCSX, intersectCSY, intersectCSZ));
			
			// inside out
			if  (start.getX() >= screenLeft &&  end.getX() < screenLeft) {
				outputChain.add(intersect);			
			}
			
			// outside in
			if (start.getX() < screenLeft &&  end.getX() >= screenLeft) {
				outputChain.add(intersect);
				outputChain.add(end);	
			}		
		}
		return outputChain;
	}
	
	public Chain clipRightAfterProj (Chain verChain, int screenRight) {
		
		Chain outputChain = new Chain();
		
		for (int i =0; i < verChain.length(); i++ ) {
			Vertex3D start 	= 	verChain.get(i);
			Vertex3D end	= 	verChain.get((i+1) % verChain.length());
			
			boolean flag = true;
			// both in
			if (start.getX() <= screenRight && end.getX() <= screenRight) {
				outputChain.add(end);
				flag = false;
			}
			
			// both out
			if (start.getX() > screenRight && end.getX() > screenRight) {
				// do nothing
				flag = false;
			}	
			
			if (flag) {
			// intersection
			double deltaX = end.getX() - start.getX();
			double deltaY = end.getY() - start.getY();				
			
			double deltaR = end.getColor().getR()/end.getCSZ() - start.getColor().getR()/start.getCSZ();
			double deltaG = end.getColor().getG()/end.getCSZ() - start.getColor().getG()/start.getCSZ();
			double deltaB = end.getColor().getB()/end.getCSZ() - start.getColor().getB()/start.getCSZ();
			
			double deltaCSX = end.getCSX()/end.getCSZ() - start.getCSX()/start.getCSZ();
			double deltaCSY = end.getCSY()/end.getCSZ() - start.getCSY()/start.getCSZ();
			double deltaCSZ = 1/end.getCSZ() - 1/start.getCSZ();	
			double recDeltaZ = 1/end.getZ() - 1/start.getZ();	
			
			double intersectX = screenRight;
			double ratio = (intersectX-start.getX())/(deltaX);				
			double intersectY = start.getY() + deltaY*ratio;			
			
			double intersectCSZ = 1/(1/start.getCSZ() + deltaCSZ*ratio);
			double intersectCSX = (start.getCSX()/start.getZ() + deltaCSX*ratio)*intersectCSZ;
			double intersectCSY = (start.getCSY()/start.getZ() + deltaCSY*ratio)*intersectCSZ;
			
			double intersectR = (start.getColor().getR()/start.getCSZ()+ deltaR*ratio)*intersectCSZ;
			double intersectG = (start.getColor().getG()/start.getCSZ()+ deltaG*ratio)*intersectCSZ;
			double intersectB = (start.getColor().getB()/start.getCSZ()+ deltaB*ratio)*intersectCSZ;
			
			double intersectZ = 1/(1/start.getZ() + recDeltaZ*ratio);
			
			Color intersectColor =  new Color(intersectR, intersectG, intersectB);
			
			Vertex3D intersect = new Vertex3D(intersectX, intersectY, intersectZ, intersectColor);
			intersect.setCameraPoint(new Point3DH(intersectCSX, intersectCSY, intersectCSZ));
			
			// inside out
			if  (start.getX() <= screenRight &&  end.getX() > screenRight) {
				outputChain.add(intersect);			
			}
			
			// outside in
			if (start.getX() > screenRight &&  end.getX() <= screenRight) {
				outputChain.add(intersect);
				outputChain.add(end);	
			}
			}
		
		}
		return outputChain;
	}
	
	public Chain clipBottomAfterProj (Chain verChain, int screenBottom) {
		
		
		Chain outputChain = new Chain();
		
		for (int i =0; i < verChain.length(); i++ ) {
			Vertex3D start 	= 	verChain.get(i);
			Vertex3D end	= 	verChain.get((i+1) % verChain.length());
			
			// both in
			if (start.getY() >= screenBottom && end.getY() >= screenBottom) {
				outputChain.add(end);					
			}
			
			// both out
			if (start.getY() < screenBottom && end.getY() < screenBottom) {
				// do nothing
			}	
			
			// intersection
			double deltaX = end.getX() - start.getX();
			double deltaY = end.getY() - start.getY();
			
			double deltaCSX = end.getCSX()/end.getCSZ() - start.getCSX()/start.getCSZ();
			double deltaCSY = end.getCSY()/end.getCSZ() - start.getCSY()/start.getCSZ();
			double deltaCSZ = 1/end.getCSZ() - 1/start.getCSZ();				
			double recDeltaZ = 1/end.getZ() - 1/start.getZ();		
			
			double intersectY = screenBottom;
			double ratio = (intersectY-start.getY())/(deltaY);				
			double intersectX = start.getX() + deltaX*ratio;						

			double intersectCSZ = 1/(1/start.getCSZ() + deltaCSZ*ratio);
			double intersectCSX = (start.getCSX()/start.getZ() + deltaCSX*ratio)*intersectCSZ;
			double intersectCSY = (start.getCSY()/start.getZ() + deltaCSY*ratio)*intersectCSZ;
			double intersectZ =   1/(1/start.getZ() + recDeltaZ*ratio);
			
			
			Color intersectColor =  start.getColor();
			Vertex3D intersect = new Vertex3D(intersectX, intersectY, intersectZ, intersectColor);
			intersect.setCameraPoint(new Point3DH(intersectCSX, intersectCSY, intersectCSZ));
			
			// inside out
			if  (start.getY() >= screenBottom &&  end.getY() < screenBottom) {
				outputChain.add(intersect);			
			}
			
			// outside in
			if (start.getY() < screenBottom &&  end.getY() >= screenBottom) {
				outputChain.add(intersect);
				outputChain.add(end);	
			}		
		}
		return outputChain;
	}
	
	
	public Chain clipTopAfterProj (Chain verChain, int screenTop) {
		
		Chain outputChain = new Chain();
		
		for (int i =0; i < verChain.length(); i++ ) {
			Vertex3D start 	= 	verChain.get(i);
			Vertex3D end	= 	verChain.get((i+1) % verChain.length());
			
			// both in
			if (start.getY() <= screenTop && end.getY() <= screenTop) {
				outputChain.add(end);					
			}
			
			// both out
			if (start.getY() > screenTop && end.getY() > screenTop) {
				// do nothing
			}	
			
			// intersection
		
			double deltaX = end.getX() - start.getX();
			double deltaY = end.getY() - start.getY();
			
			double deltaCSX = end.getCSX()/end.getCSZ() - start.getCSX()/start.getCSZ();
			double deltaCSY = end.getCSY()/end.getCSZ() - start.getCSY()/start.getCSZ();
			double deltaCSZ = 1/end.getCSZ() - 1/start.getCSZ();	
			double recDeltaZ = 1/end.getZ() - 1/start.getZ();
			
			double intersectY = screenTop;
			double ratio = (intersectY-start.getY())/(deltaY);				
			double intersectX = start.getX() + deltaX*ratio;
			
			double intersectCSZ = 1/(1/start.getCSZ() + deltaCSZ*ratio);
			double intersectCSX = (start.getCSX()/start.getZ() + deltaCSX*ratio)*intersectCSZ;
			double intersectCSY = (start.getCSY()/start.getZ() + deltaCSY*ratio)*intersectCSZ;
			double intersectZ = 1/( 1/start.getZ() + recDeltaZ*ratio);			

			
			Color intersectColor =  start.getColor();
			Vertex3D intersect = new Vertex3D(intersectX, intersectY, intersectZ, intersectColor);	
			intersect.setCameraPoint(new Point3DH(intersectCSX, intersectCSY, intersectCSZ));
			
			if  (start.getY() <= screenTop &&  end.getY() > screenTop) {
				outputChain.add(intersect);			
			}
			
			// outside in
			if (start.getY() > screenTop &&  end.getY() <= screenTop) {
				outputChain.add(intersect);
				outputChain.add(end);	
			}
		
		}
		return outputChain;
	}
	
	

	
	
	public Chain clipSideFront (Chain verChain) {
		
		Chain outputChain = new Chain();		
		
		for (int i =0; i < verChain.length(); i++ ) {
			Vertex3D start 	= 	verChain.get(i);
			Vertex3D end	= 	verChain.get((i+1) % verChain.length());
				
			double projectP = front;
			
			// both in
			if (start.getZ() <= projectP && end.getZ() <= projectP) {
				outputChain.add(end);					
			}
			
			// both out
			if (start.getZ() > projectP && end.getZ() > projectP) {
				// do nothing
			}			
			
			// intersection
			double deltaX = end.getX() - start.getX();
			double deltaY = end.getY() - start.getY();
			double deltaZ = end.getZ() - start.getZ();		
			
			double intersectZ = projectP;
			double ratio = (intersectZ-start.getZ())/(deltaZ);				
			double intersectX = start.getX() + deltaX*ratio;
			double intersectY = start.getY() + deltaY*ratio;
			
			Color intersectColor =  start.getColor();
			Vertex3D intersect = new Vertex3D(intersectX, intersectY, intersectZ, intersectColor);
			intersect.setCameraPoint(intersect);
			
			// inside out
			if  (start.getZ() <= projectP &&  end.getZ() > projectP) {
				outputChain.add(intersect);			
			}
			
			// outside in
			if (start.getZ() > projectP &&  end.getZ() <= projectP) {
				outputChain.add(intersect);
				outputChain.add(end);			
			}			
		}
		return outputChain;
	}
	
	
	public Chain clipSideEnd (Chain verChain) {
		
		Chain outputChain = new Chain();
		
		for (int i =0; i < verChain.length(); i++ ) {
			Vertex3D start 	= 	verChain.get(i);
			Vertex3D end	= 	verChain.get((i+1) % verChain.length());
			
			if (start.getZ() >= this.end && end.getZ() >= this.end) {
				outputChain.add(end);					
			}
			
			if (start.getZ() < this.end && end.getZ() < this.end) {
				// do nothing
			}			
			
			// intersection
			double deltaX = end.getX() - start.getX();
			double deltaY = end.getY() - start.getY();
			double deltaZ = end.getZ() - start.getZ();	
			
			double intersectZ = this.end;
			double ratio = (intersectZ-start.getZ())/(deltaZ);				
			double intersectX = start.getX() + deltaX*ratio;
			double intersectY = start.getY() + deltaY*ratio;
			
			Color intersectColor =  start.getColor();
			Vertex3D intersect = new Vertex3D(intersectX, intersectY, intersectZ, intersectColor);
			intersect.setCameraPoint(intersect);
			
			// inside out
			if  (start.getZ() >= this.end &&  end.getZ() < this.end) {
				outputChain.add(intersect);			
			}
			
			// outside in
			if (start.getZ() < this.end &&  end.getZ() >= this.end) {
				outputChain.add(intersect);
				outputChain.add(end);
			
			}			
		}
		return outputChain;
	}
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
