package geometry;

public class Halfplane3DH extends Point3DH{	
	
	public Halfplane3DH (double a, double b, double c) {
		super(a, b, c);
	}
	
	public Halfplane3DH (Point3DH p) {
		super(p.getX(), p.getY(), p.getZ());
	}

}
