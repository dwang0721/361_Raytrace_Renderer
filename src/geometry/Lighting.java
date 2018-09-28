package geometry;

import windowing.graphics.*;
import java.util.ArrayList;

public class Lighting {

	Color ambient;
	ArrayList<Light> LightList = new ArrayList<Light>();
	
	public Lighting (Color ambientLight, ArrayList<Light> lightList) {
		this.ambient = ambientLight;
		this.LightList = lightList;
	}
	
	// constructor
	public Color light(	Vertex3D cameraSpacePoint, Color kDiffuse, Halfplane3DH normal,	double kSpecular, double specularExponent) {
		
		Color ambientIntensity = kDiffuse.multiply(ambient);
		
		Color diffuseIntensity = Color.BLACK;
		
		for (Light i : LightList) {		
			// distance of the point to the light
			double deltaX = i.getCSX()- cameraSpacePoint.getX();
			double deltaY = i.getCSY()- cameraSpacePoint.getY();
			double deltaZ = i.getCSZ()- cameraSpacePoint.getZ();
			double distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
			
			// fatti
			double fatti = 1.0/(i.getFattiA()+i.getFattiB()*distance);			
			
			// normalize N
			Point3DH vecN = normal.normalizeVector();
			
			// normalize L
			double vecL_X = i.getCSX()-cameraSpacePoint.getCSX(); 
			double vecL_Y = i.getCSY()-cameraSpacePoint.getCSY(); 
			double vecL_Z = i.getCSZ()-cameraSpacePoint.getCSZ(); 			
			Point3DH vecL = uniVector (vecL_X , vecL_Y, vecL_Z); 			
			
			// normalize V
			double vecV_X = 0-cameraSpacePoint.getCSX(); 
			double vecV_Y = 0-cameraSpacePoint.getCSY(); 
			double vecV_Z = 0-cameraSpacePoint.getCSZ();	
			Point3DH vecV = uniVector (vecV_X , vecV_Y, vecV_Z); 	

			
			// normalize R, R = 2(NL)N-L
			double nL = vecN.dotProduct(vecL);
			Point3DH temp = vecN.scale(2*nL);
			Point3DH vecR = temp.subtract(vecL);
			vecR = uniVector (vecR);		
			
			// calculate (kd(NL)+ks(VR)^p)
			if(nL<0) {
				nL=0;
			}
			Color kd_NL = kDiffuse.scale(nL);
			Color ifkd_NL = i.getIntensity().multiply(kd_NL.scale(fatti));

			double vR = vecV.dotProduct(vecR);
			if(vR<0) {
				vR=0;
			}
			double ks_VR = kSpecular* Math.pow(vR, specularExponent);
			Color ifks_VR = i.getIntensity().scale(fatti*ks_VR);		
			
			Color intensity_i = ifkd_NL.add(ifks_VR);
			diffuseIntensity = diffuseIntensity.add(intensity_i);					
		}		
		
		
		Color totalIntensity = ambientIntensity.add(diffuseIntensity);
		
		
		return totalIntensity;		
	}
	
	
	
	private Point3DH uniVector (double x, double y, double z) {
		double length = Math.sqrt(x*x +y*y + z*z);
		if (length == 0) {
			return new Point3DH(0, 0, 0);
		}
		return new Point3DH(x/length, y/length, z/length);
	}
	
	private Point3DH uniVector (Point3DH vec) {
		double x = vec.getX();
		double y = vec.getY();
		double z = vec.getZ();
		double length = Math.sqrt(x*x +y*y + z*z);
		if (length == 0) {
			return new Point3DH(0, 0, 0);
		}
		return new Point3DH(x/length, y/length, z/length);
	}
	

}
