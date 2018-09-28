package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {
	protected Point3DH point;
	protected Color color;
	
	protected Color shaderColor;	
	protected Point3DH cameraPoint;
	protected boolean isForPhongShading;
	private boolean hasNormal;
	protected Halfplane3DH normal;
	protected boolean isObj;
	
	
	public Vertex3D(Point3DH point, Color color) {
		super();
		this.isObj = false;
		this.point = point;
		this.cameraPoint = point;
		this.color = color;
		this.hasNormal = false;
		this.isForPhongShading=false;
		this.shaderColor = color;
		this.normal =  new Halfplane3DH (0,0,0);
	}
	
	
	public Vertex3D makeCopyOfVertex() {
		Vertex3D newVert = new Vertex3D(point.getX(), point.getY(),point.getZ(), new Color(color.getR(), color.getG(), color.getB()));
		newVert.setVertexShaderColor(new Color(shaderColor.getR(), shaderColor.getG(), shaderColor.getB()));
		newVert.setCameraPoint(new Point3DH(cameraPoint.getX(), cameraPoint.getY(), cameraPoint.getIntZ()));
//		newVert.copyIsForPhongShading(this);
		newVert.isForPhongShading = this.isForPhongShading;
		newVert.hasNormal = this.hasNormal;
		newVert.normal = new Point3DH(normal.getX(),normal.getY(), normal.getZ()).toHalfPlaneNormal();
		newVert.isObj = this.isObj;
		
		return newVert;
	}
	
	private void copyIsForPhongShading(Vertex3D p) {
		this.isForPhongShading = p.isForPhongShading();
	}
	
	public Vertex3D(double x, double y, double z, Color color) {
		this(new Point3DH(x, y, z), color);
	}

	public Vertex3D() {
	}
	
	public boolean hasNormal() {
		return this.hasNormal;
	}
	
	public boolean isForPhongShading() {
		return this.isForPhongShading;
	}
	
	public void setToPhongShading() {
		this.isForPhongShading = true;
	}
	
	public Halfplane3DH getNormal() {
		return normal;
	}
	
	public void setHasNormalToTrue() {
		this.hasNormal = true;
	}
	
	public void setHasNormalToFalse() {
		this.hasNormal = false;
	}
	
	public void copyAtt_CSP_Norm_Phong_VertexC(Vertex3D vert) {
		this.isForPhongShading = vert.isForPhongShading();
		this.hasNormal = vert.hasNormal();
		this.normal=vert.getNormal();
		this.shaderColor = vert.getVertexShaderColor();
		this.cameraPoint = vert.getCameraSpacePoint();
	}
	
	public void setNormal(Halfplane3DH n) {
		this.normal = new Halfplane3DH(n.getX(),n.getY(),n.getZ());
		this.hasNormal = true;
	}
	
	public void setNormal(Point3DH n) {
		this.normal = new Halfplane3DH(n.getX(),n.getY(),n.getZ());
		this.hasNormal = true;
	}
	
	public void setThisToObj() {
		this.isObj=true;
	}
	
	public boolean isObj() {
		return this.isObj;
	}
	
	public void setIsObj(Vertex3D p) {
		this.isObj = p.isObj;
	}
	
	
	public void setCameraPoint(Point3DH csPoint) {
		// TODO Auto-generated method stub
		this.cameraPoint =  csPoint;
	}
	
	public void setCameraPoint (Vertex3D cameraVertex) {
		this.cameraPoint =  new Point3DH(cameraVertex.getX(), cameraVertex.getY(), cameraVertex.getZ());
	}
	
	public double getCSX() {
		return cameraPoint.getX();
	}
	public double getCSY() {
		return cameraPoint.getY();
	}
	public double getCSZ() {
		return cameraPoint.getZ();
	}	
	
	
	public double getX() {
		return point.getX();
	}
	public double getY() {
		return point.getY();
	}
	public double getZ() {
		return point.getZ();
	}
	
		
	public double getCameraSpaceZ() {
		return getZ();
	}
	public Point getPoint() {
		return point;
	}
	public Point3DH getPoint3D() {
		return point;
	}
	public Point3DH getCameraSpacePoint() {
		return cameraPoint;
	}
	
	public void resetXYZ(double x, double y, double z) {
		// TODO Auto-generated method stub
		this.point = new Point3DH(x, y, z);
	}
	
	public int getIntX() {
		return (int) Math.round(getX());
	}
	public int getIntY() {
		return (int) Math.round(getY());
	}
	public int getIntZ() {
		return (int) Math.round(getZ());
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		this.color = c;
	}
	
	public void setVertexShaderColor(Color c) {
		this.shaderColor = c;
	}
	
	public Color getVertexShaderColor() {
		return this.shaderColor ;
	}
	
	public Vertex3D rounded() {
		return new Vertex3D(point.round(), color);
	}
	public Vertex3D add(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.add(other3D.getPoint()),
				            color.add(other3D.getColor()));
	}
	public Vertex3D subtract(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()));
	}
	public Vertex3D scale(double scalar) {
		return new Vertex3D(point.scale(scalar),
				            color.scale(scalar));
	}
	public Vertex3D replacePoint(Point3DH newPoint) {
		return new Vertex3D(newPoint, color);
	}
	public Vertex3D replaceColor(Color newColor) {
		return new Vertex3D(point, newColor);
	}
	public Vertex3D euclidean() {
		Point3DH euclidean = getPoint3D().euclidean();
		return replacePoint(euclidean);
	}
	
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	public String toIntString() {
		return "(" + getIntX() + ", " + getIntY() + getIntZ() + ", " + ", " + getColor().toIntString() + ")";
	}
	
	public void print() {
		System.out.printf("vertexInfo: %7.3f, %7.3f, %7.3f\n", point.getX(),point.getY(), point.getZ());
		if ((cameraPoint != null)) {
		System.out.printf("csVertInfo: %7.3f, %7.3f, %7.3f\n", getCSX(),getCSY(), getCSZ());
		}
	}





}
