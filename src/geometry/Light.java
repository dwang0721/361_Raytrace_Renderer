package geometry;

import windowing.graphics.Color;

public class Light {
	private Color intensity;
	private double fattiA;
	private double fattiB;
	Point3DH cameraSpaceLocation;
	
	public Light(double r, double g, double b, double fA, double fB, Point3DH csLocation) {
		intensity = new Color(r, g, b);
		cameraSpaceLocation = csLocation;
		fattiA = fA;
		fattiB = fB;		
	}
	
	public Light(Color color, double fA, double fB, Point3DH csLocation) {
		intensity = color;
		fattiA = fA;
		fattiB = fB;
		cameraSpaceLocation = csLocation;
	}
	
	public double getCSX() {
		return cameraSpaceLocation.getX();
	}
	
	public double getCSY() {
		return cameraSpaceLocation.getY();
	}
	
	public double getCSZ() {
		return cameraSpaceLocation.getZ();
	}
	
	
	public double getR() {
		return intensity.getR();
	}
	
	public double getG() {
		return intensity.getG();
	}
	
	public double getB() {
		return intensity.getB();
	}
	
	public Color getIntensity() {
		return intensity;
	}
	
	public double getFattiA() {
		return fattiA;
	}
	
	public double getFattiB() {
		return fattiB;
	}
	
	public void print() {
		System.out.printf("light info: r:%f, g:%f, b:%f | fatti: %f, %f | point: %f, %f,%f.\n", intensity.getR(), intensity.getG(), intensity.getB(), fattiA, fattiB, cameraSpaceLocation.getX(), cameraSpaceLocation.getY(), cameraSpaceLocation.getZ());
	}
}
