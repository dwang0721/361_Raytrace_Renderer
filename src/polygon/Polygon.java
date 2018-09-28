package polygon;

import java.util.ArrayList;
import java.util.List;

import geometry.Halfplane3DH;
import geometry.Point3DH;
import geometry.Vertex;
import geometry.Vertex3D;
import windowing.graphics.Color;

public class Polygon extends Chain {
	private static final int INDEX_STEP_FOR_CLOCKWISE = -1;
	private static final int INDEX_STEP_FOR_COUNTERCLOCKWISE = 1;
	private Color polyColor;
	
	private Polygon(Vertex3D... initialVertices) {
		super(initialVertices);
		polyColor = Color.BLACK;
		if(length() < 3) {
			throw new IllegalArgumentException("Not enough vertices to construct a polygon");
		}
	}
	
	public void setPolyColor(Color color) {
		polyColor = color; 
	}
	
	public Color getPolyColor() {
		return polyColor;
	}
	
	public void floodPolyColorToVetx() {
		for(int index = 0; index < vertices.size(); index++) { 			
			vertices.get(index).setColor(polyColor);			
		}
	}
		
	public void printPolyColor() {
		System.out.println("PolyColor: " + polyColor);
	}
	
	// the EmptyMarker is to distinguish this constructor from the one above (when there are no initial vertices).
	private enum EmptyMarker { MARKER; };
	private Polygon(EmptyMarker ignored) {
		super();
	}
	
	public static Polygon makeEmpty() {
		return new Polygon(EmptyMarker.MARKER);
	}

	public static Polygon make(Vertex3D... initialVertices) {
		return new Polygon(initialVertices);
	}
	
//	public static Polygon make2(Chain chain) {
//		Vertex3D... mychain = chain.getVertices();
//		return new Polygon(mychain);
//	}
	
	public static Polygon makeEnsuringClockwise(Vertex3D... initialVertices) {
		if(isClockwise(initialVertices[0], initialVertices[1], initialVertices[2])) {
			return new Polygon(reverseArray(initialVertices));
		}
		return new Polygon(initialVertices);
	}


	public static <V extends Vertex> boolean isClockwise(Vertex3D a, Vertex3D b, Vertex3D c) {
		Vertex3D vector1 = b.subtract(a);
		Vertex3D vector2 = c.subtract(a);
		
		double term1 = vector1.getX() * vector2.getY();
		double term2 = vector2.getX() * vector1.getY();
		double cross = term1 - term2;
		
		return cross < 0;
	}
	
	private static <V extends Vertex> V[] reverseArray(V[] initialVertices) {
		int length = initialVertices.length;
		List<V> newVertices = new ArrayList<V>();
		
		for(int index = 0; index < length; index++) {
			newVertices.add(initialVertices[index]);
		}
		for(int index = 0; index < length; index++) {
			initialVertices[index] = newVertices.get(length - 1 - index);
		}
		return initialVertices;
	}
	
	/** 
	 * The Polygon is a circular Chain and
	 *  the index given will be taken modulo the number
	 *  of vertices in the Chain. 
	 *  
	 * @param index
	 * @return
	 */
	public Vertex3D get(int index) {
		int realIndex = wrapIndex(index);
		return vertices.get(realIndex);
	}
	/**
	 *  Wrap the indices for the list vertices.
	 *  
	 *  @param index any integer
	 *  @return the number n such that n is equivalent 
	 *  to the given index modulo the number of vertices.
	 */
	private int wrapIndex(int index) {
		return ((index % numVertices) + numVertices) % numVertices;
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// methods for dividing a y-monotone polygon into a left chain and a right chain.

	/**
	 * returns the left-hand chain of the polygon, ordered from top to bottom.
	 */
	public Chain leftChain() {
		return sideChain(INDEX_STEP_FOR_COUNTERCLOCKWISE);
	}
	/**
	 * returns the right-hand chain of the polygon, ordered from top to bottom.
	 */
	public Chain rightChain() {
		return sideChain(INDEX_STEP_FOR_CLOCKWISE);
	}
	
	private Chain sideChain(int indexStep) {
		int topIndex = topIndex();
		int bottomIndex = bottomIndex();
		
		Chain chain = new Chain();
		for(int index = topIndex; wrapIndex(index) != bottomIndex; index += indexStep) {
			chain.add(get(index));
		}
		chain.add(get(bottomIndex));
		
		return chain;
	}
	
	private int topIndex() {
		int maxIndex = 0;
		double maxY = get(0).getY();
		
		for(int index = 1; index < vertices.size(); index++) {
			if(get(index).getY() > maxY) {
				maxY = get(index).getY();
				maxIndex = index;
			}
		}
		return maxIndex;
	}
	private int bottomIndex() {
		int minIndex = 0;
		double minY = get(0).getY();
		
		for(int index = 1; index < vertices.size(); index++) {
			if(get(index).getY() < minY) {
				minY = get(index).getY();
				minIndex = index;
			}
		}
		return minIndex;
	}
	
	public String toString() {
		return "Polygon[" + super.toString() + "]";
	}
	
	public Point3DH getFaceNormal() {
		Vertex3D p1 = this.vertices.get(0);
		Vertex3D p2 = this.vertices.get(1);
		Vertex3D p3 = this.vertices.get(2);		
		
		Point3DH faceNormal;
		Point3DH v2_v1 = new Point3DH(p2.getCSX()-p1.getCSX(), p2.getCSY()-p1.getCSY(), p2.getCSZ()-p1.getCSZ());
		Point3DH v3_v1 = new Point3DH(p3.getCSX()-p1.getCSX(), p3.getCSY()-p1.getCSY(), p3.getCSZ()-p1.getCSZ());
		faceNormal = v2_v1.crossProduct(v3_v1);
		faceNormal = faceNormal.normalizeVector();
		
		return faceNormal;		
	}
	
	public void printFaceNormalFromVertex() {
		getFaceNormal().printNormal();
	}
	
	public Vertex3D makeCameraSpacePoint() {
		Vertex3D p1 = this.vertices.get(0);
		Vertex3D p2 = this.vertices.get(1);
		Vertex3D p3 = this.vertices.get(2);		
		
		double cX= (p1.getX()+p2.getX()+p3.getX())/3.0;
		double cY= (p1.getY()+p2.getY()+p3.getY())/3.0;
		double cZ= (p1.getZ()+p2.getZ()+p3.getZ())/3.0;
		double centerX= (p1.getCSX()+p2.getCSX()+p3.getCSX())/3.0;
		double centerY= (p1.getCSY()+p2.getCSY()+p3.getCSY())/3.0;
		double centerZ= (p1.getCSZ()+p2.getCSZ()+p3.getCSZ())/3.0;
		
		Vertex3D cameraSpacePoint = new Vertex3D(cX, cY, cZ, p1.getColor());
		cameraSpacePoint.setCameraPoint(new Vertex3D(centerX, centerY, centerZ, p1.getColor()));
	
		return cameraSpacePoint;
	}
	

}
