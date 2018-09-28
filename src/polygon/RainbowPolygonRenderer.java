package polygon;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import line.DDALineRenderer;
import line.LineRenderer;
import polygon.shading.FaceShader;
import polygon.shading.PixelShader;
import polygon.shading.VertexShader;
import geometry.Halfplane3DH;
import geometry.Point3DH;
import geometry.Vertex3D;

public class RainbowPolygonRenderer implements PolygonRenderer{

	private RainbowPolygonRenderer() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable panel, FaceShader faceShader, VertexShader vertexShader, PixelShader pixelShader ) {
		
		LineRenderer DDAdrawer = DDALineRenderer.make();
		
//		for(int i=0; i< polygon.length(); i++) {
//			polygon.get(i).getNormal().printNormal();
//			System.out.println(polygon.get(i).hasNormal());
//		}
		
		
		
		Polygon orderVertices = faceShader.shade(polygon); 
		
		
		Chain leftChain = orderVertices.leftChain();		
		Chain rightChain = orderVertices.rightChain();
		
		Vertex3D p_top = rightChain.get(0); // top point		

		
		// catch corner case and do nothing.
		if(rightChain.length()== 1 && leftChain.length()==1) {			 
			return;
		}
		
		Vertex3D p_left = (leftChain.length()>=2)? leftChain.get(1):rightChain.get(2);
		Vertex3D p_right = (leftChain.length()>=2)? rightChain.get(1):leftChain.get(2); 
				
		// Shade Vertices
		
		p_top =  vertexShader.shade(polygon, p_top);
		p_left =  vertexShader.shade(polygon, p_left);
		p_right =  vertexShader.shade(polygon, p_right);
		

		// Shade pixel colors, SHOULD decide if interpolate normal here or or not. 		
		// if it is phong or flat we should get color now from pixelShader
		// else we get color and interpolate the normal. 
		
		
		
		// diffuse Color		
		Color p_top_Color, p_left_Color,p_right_Color;	
		
		if (!p_top.isForPhongShading()) {
			p_top_Color = pixelShader.shade(polygon, p_top);			
			p_left_Color = pixelShader.shade(polygon, p_left);
			p_right_Color = pixelShader.shade(polygon, p_right);
		}else {
			p_top_Color = p_top.getColor();
			p_left_Color = p_left.getColor();
			p_right_Color = p_right.getColor();
		}
		

		
		
		// normal 
		Halfplane3DH normal_top = new Halfplane3DH(0,0,0);  
		Halfplane3DH normal_left = new Halfplane3DH(0,0,0);   
		Halfplane3DH normal_right = new Halfplane3DH(0,0,0);  
		
		if (p_top.hasNormal()) {			
			normal_top = p_top.getNormal();
		}
		
		if(p_left.hasNormal()) {
			normal_left = p_left.getNormal();
		}

		if(p_right.hasNormal()) {
			normal_right = p_right.getNormal();
		}
			
		
		
		
		////// left dda //////////////
		double deltaXL = p_top.getIntX()-p_left.getIntX();
		double deltaYL = p_top.getIntY()-p_left.getIntY();		
		double mL = deltaXL/deltaYL;	
		
		double recDeltaZL = 1.0/p_top.getZ()-1.0/p_left.getZ();
		double rec_mzL = recDeltaZL/deltaYL;
		
		// color
		double delta_red_L =  	(p_top_Color.getR()/p_top.getZ()  - p_left_Color.getR()/p_left.getZ());
		double delta_green_L =  (p_top_Color.getG()/p_top.getZ()  - p_left_Color.getG()/p_left.getZ());
		double delta_blue_L =  	(p_top_Color.getB()/p_top.getZ()  - p_left_Color.getB()/p_left.getZ());
		
		double m_red_L = 	delta_red_L  /deltaYL;
		double m_green_L = 	delta_green_L /deltaYL;
		double m_blue_L = 	delta_blue_L  /deltaYL;	
		
		// cam space point
		double deltaCSX_L = p_top.getCSX()/p_top.getCSZ() - p_left.getCSX()/p_left.getCSZ();
		double deltaCSY_L = p_top.getCSY()/p_top.getCSZ() - p_left.getCSY()/p_left.getCSZ();
		double deltaCSZ_L = 1/p_top.getCSZ() - 1/p_left.getCSZ();
		
		double m_CSX_L = deltaCSX_L/deltaYL;
		double m_CSY_L = deltaCSY_L/deltaYL;
		double m_CSZ_L = deltaCSZ_L/deltaYL;
		
		// normal
		double deltaNormX_L = normal_top.getX()/p_top.getCSZ() - normal_left.getX()/p_left.getCSZ();
		double deltaNormY_L = normal_top.getY()/p_top.getCSZ() - normal_left.getY()/p_left.getCSZ();
		double deltaNormZ_L = normal_top.getZ()/p_top.getCSZ() - normal_left.getZ()/p_left.getCSZ();
		
		double m_NormX_L = deltaNormX_L/deltaYL;
		double m_NormY_L = deltaNormY_L/deltaYL;
		double m_NormZ_L = deltaNormZ_L/deltaYL;
		
		
		
		
		
		
		
		
		////// right dda /////////////////
		double deltaXR = p_right.getIntX()-p_top.getIntX();
		double deltaYR = p_right.getIntY()-p_top.getIntY();		
		double mR = deltaXR/deltaYR;
		
		double recDeltaZR = 1.0/p_right.getZ()-1.0/p_top.getZ();
		double rec_mzR = recDeltaZR/deltaYR;
		
		// color
		double delta_red_R =  	(p_right_Color.getR()/p_right.getZ() - p_top_Color.getR()/p_top.getZ());
		double delta_green_R =  (p_right_Color.getG()/p_right.getZ() - p_top_Color.getG()/p_top.getZ());
		double delta_blue_R =  	(p_right_Color.getB()/p_right.getZ() - p_top_Color.getB()/p_top.getZ());
		
		double m_red_R = 	delta_red_R/deltaYR;
		double m_green_R = 	delta_green_R/deltaYR;
		double m_blue_R = 	delta_blue_R/deltaYR;	
		
		// cam space point
		double deltaCSX_R = p_right.getCSX()/p_right.getCSZ() - p_top.getCSX()/p_top.getCSZ();
		double deltaCSY_R = p_right.getCSY()/p_right.getCSZ() - p_top.getCSY()/p_top.getCSZ();
		double deltaCSZ_R = 1/p_right.getCSZ() - 1/p_top.getCSZ();
		
		double m_CSX_R = deltaCSX_R/deltaYR;
		double m_CSY_R = deltaCSY_R/deltaYR;
		double m_CSZ_R = deltaCSZ_R/deltaYR;
		
		// normal
		double deltaNormX_R = normal_right.getX()/p_right.getCSZ() - normal_top.getX()/p_top.getCSZ();
		double deltaNormY_R = normal_right.getY()/p_right.getCSZ() - normal_top.getY()/p_top.getCSZ();
		double deltaNormZ_R = normal_right.getZ()/p_right.getCSZ() - normal_top.getZ()/p_top.getCSZ();
		
		double m_NormX_R = deltaNormX_R/deltaYR;
		double m_NormY_R = deltaNormY_R/deltaYR;
		double m_NormZ_R = deltaNormZ_R/deltaYR;
		
		
		
		
		
		
		
		
		
		
		
		
		////// lower dda ///////////////
		double deltaXLower = p_right.getIntX()- p_left.getIntX();
		double deltaYLower = p_right.getIntY()- p_left.getIntY();		
		double mLower = deltaXLower/deltaYLower;
		

		double recDeltaZLower = 1.0/p_right.getZ()-1.0/p_left.getZ();
		double rec_mzLower = recDeltaZLower/deltaYLower;
		
		// color
		double delta_red_Lower =  	(p_right_Color.getR()/p_right.getZ() - p_left_Color.getR()/p_left.getZ());
		double delta_green_Lower =  (p_right_Color.getG()/p_right.getZ()- p_left_Color.getG()/p_left.getZ());
		double delta_blue_Lower =  	(p_right_Color.getB()/p_right.getZ() - p_left_Color.getB()/p_left.getZ());	
		
		double m_red_Lower = 	delta_red_Lower/deltaYLower;
		double m_green_Lower =	delta_green_Lower/deltaYLower;
		double m_blue_Lower = 	delta_blue_Lower/deltaYLower;		
		
		// cam space 
		double deltaCSX_Lower = p_right.getCSX()/p_right.getCSZ() - p_left.getCSX()/p_left.getCSZ();
		double deltaCSY_Lower = p_right.getCSY()/p_right.getCSZ() - p_left.getCSY()/p_left.getCSZ();
		double deltaCSZ_Lower = 1/p_right.getCSZ() - 1/p_left.getCSZ();
		
		double m_CSX_Lower = deltaCSX_Lower/deltaYLower;
		double m_CSY_Lower = deltaCSY_Lower/deltaYLower;
		double m_CSZ_Lower = deltaCSZ_Lower/deltaYLower;
		
		// normal
		double deltaNormX_Lower = normal_right.getX()/p_right.getCSZ() - normal_left.getX()/p_left.getCSZ();
		double deltaNormY_Lower = normal_right.getY()/p_right.getCSZ() - normal_left.getY()/p_left.getCSZ();
		double deltaNormZ_Lower = normal_right.getZ()/p_right.getCSZ() - normal_left.getZ()/p_left.getCSZ();
		
		double m_NormX_Lower = deltaNormX_Lower/deltaYLower;
		double m_NormY_Lower = deltaNormY_Lower/deltaYLower;
		double m_NormZ_Lower = deltaNormZ_Lower/deltaYLower;
		
		
		
		
		
		
		
		
		///////////////// find the y_bottom and y_mid //////////////////////
		int y_bottom = (int) (Math.round(Math.min (p_left.getIntY(), p_right.getIntY())));
		int y_mid = (int) (Math.round(Math.max (p_left.getIntY(), p_right.getIntY())));
		
		
		
		// xl and xr
		double fxl; 
		double fxr;
		
		double rec_fzl, rec_fzr;
		
		// rgb
		double f_red_l, f_red_r, f_green_l, f_green_r, f_blue_l, f_blue_r;
		
		// spaceSpoint
		double csX_l, csX_r, csY_l, csY_r, csZ_l, csZ_r;
		
		// normal
		double normX_l, normX_r, normY_l, normY_r, normZ_l, normZ_r;
		
		
		
		
		
		if (p_top.getIntY()-p_left.getIntY()!=0 && p_top.getIntY()-p_right.getIntY()!=0) { // top is not flat, normal triangle		
			
			// initialize right and left x for line drawings. 
			fxl = fxr = p_top.getIntX(); 			
			rec_fzl = rec_fzr = 1/p_top.getZ();			
			
			// initialize the color			
			f_red_l 	= f_red_r 	= p_top_Color.getR()/p_top.getZ();
			f_green_l	= f_green_r = p_top_Color.getG()/p_top.getZ();
			f_blue_l 	= f_blue_r 	= p_top_Color.getB()/p_top.getZ();
			
			// initialize camSC Point
			csX_l 	= csX_r 	= p_top.getCSX()/p_top.getZ();
			csY_l	= csY_r 	= p_top.getCSY()/p_top.getZ();
			csZ_l 	= csZ_r 	= p_top.getCSZ()/p_top.getZ();			
			
			// initialize normal
			normX_l = normX_r 	= normal_top.getX()/p_top.getZ();
			normY_l	= normY_r 	= normal_top.getY()/p_top.getZ();
			normZ_l = normZ_r 	= normal_top.getZ()/p_top.getZ();			
			 

			for (double y = p_top.getIntY(); y> y_bottom; y--) {				
				if(y>y_mid ) { // corner case will not enter here 
					fxl -= mL;
					fxr -= mR;
					
					rec_fzl -= rec_mzL;
					rec_fzr -= rec_mzR;
					
					f_red_l -= m_red_L;
					f_red_r -= m_red_R ;
					f_green_l -= m_green_L ;
					f_green_r -= m_green_R ;
					f_blue_l -= m_blue_L ;
					f_blue_r -= m_blue_R ;		
					
					csX_l -= m_CSX_L;
					csX_r -= m_CSX_R;
					csY_l -= m_CSY_L;
					csY_r -= m_CSY_R;
					csZ_l -= m_CSZ_L;
					csZ_r -= m_CSZ_R;
					
					normX_l -= m_NormX_L;
					normX_r -= m_NormX_R;
					normY_l -= m_NormY_L;
					normY_r -= m_NormY_R;	
					normZ_l -= m_NormZ_L;
					normZ_r -= m_NormZ_R;
					
				}				
				
				// mid point left, switch left ddl to lower ddl 
				if(y<=y_mid && p_left.getIntY() > p_right.getIntY() ) { 
					
					fxl -= mLower;
					fxr -= mR;
					
					rec_fzl -= rec_mzLower;
					rec_fzr -= rec_mzR;
					
					f_red_l -= 	m_red_Lower ;
					f_red_r -=  m_red_R ;
					f_green_l -= m_green_Lower ;
					f_green_r -= m_green_R ;
					f_blue_l -= m_blue_Lower ;
					f_blue_r -= m_blue_R ;	
					
					csX_l -= m_CSX_Lower;
					csX_r -= m_CSX_R;
					csY_l -= m_CSY_Lower;
					csY_r -= m_CSY_R;
					csZ_l -= m_CSZ_Lower;
					csZ_r -= m_CSZ_R;
					
					normX_l -= m_NormX_Lower;
					normX_r -= m_NormX_R;
					normY_l -= m_NormY_Lower;
					normY_r -= m_NormY_R;	
					normZ_l -= m_NormZ_Lower;
					normZ_r -= m_NormZ_R;
					
					
				}			
				
				// mid point on right, switch the right ddl
				if (y<=y_mid && p_left.getIntY() < p_right.getIntY()){
					
					fxl -= mL;
					fxr -= mLower;
					
					rec_fzl -= rec_mzL;
					rec_fzr -= rec_mzLower;
					
					f_red_l -= m_red_L ;
					f_red_r -= m_red_Lower ;
					f_green_l -= m_green_L ;
					f_green_r -= m_green_Lower ;
					f_blue_l -= m_blue_L ;
					f_blue_r -= m_blue_Lower ;
					
					csX_l -= m_CSX_L;
					csX_r -= m_CSX_Lower;
					csY_l -= m_CSY_L;
					csY_r -= m_CSY_Lower;
					csZ_l -= m_CSZ_L;
					csZ_r -= m_CSZ_Lower;
					
					normX_l -= m_NormX_L;
					normX_r -= m_NormX_Lower;
					normY_l -= m_NormY_L;
					normY_r -= m_NormY_Lower;	
					normZ_l -= m_NormZ_L;
					normZ_r -= m_NormZ_Lower;					
					
				}	
				
				
				if((int)Math.round(fxr-1) >= (int)Math.round(fxl)) {
					
					// color recover
					Color c_left = new Color(f_red_l /rec_fzl, f_green_l /rec_fzl, f_blue_l /rec_fzl);
					Color c_right = new Color(f_red_r /rec_fzr, f_green_r /rec_fzr, f_blue_r /rec_fzr);		
					
					// camera space recover
					Point3DH csPointLeft  = new Point3DH(csX_l /rec_fzl, csY_l /rec_fzl, csZ_l /rec_fzl);
					Point3DH csPointRight = new Point3DH(csX_r /rec_fzr, csY_r /rec_fzr, csZ_r /rec_fzr);					
					
					// normal recover
					Halfplane3DH normalLeft  =  new Halfplane3DH (normX_l /rec_fzl, normY_l /rec_fzl, normZ_l/rec_fzl);
					Halfplane3DH normalRight =  new Halfplane3DH (normX_r /rec_fzr, normY_r /rec_fzr, normZ_r/rec_fzr);
					
					Vertex3D pL = new Vertex3D ((int)Math.round(fxl), y, 1.0/rec_fzl,  c_left);
					Vertex3D pR = new Vertex3D ((int)Math.round(fxr-1), y, 1.0/rec_fzr,  c_right);  	
					
					pL.setCameraPoint(csPointLeft);
					pR.setCameraPoint(csPointRight);
					
					pL.setNormal(normalLeft);
					pR.setNormal(normalRight);
					
					pL.setIsObj(p_top);
					pR.setIsObj(p_top);					
					
					if (p_top.isForPhongShading()) {
						pL.setToPhongShading();
						pR.setToPhongShading();					
					}
					
					DDAdrawer.drawLine(pL, pR, polygon , panel, pixelShader);					
					
					}
				}			
			}  		
			

		
		
		
		
		
		
		
		
		
		
		
		
//////////////////// top flat, left point and top point construct a flat line	left------->top			
			if (p_top.getIntY()-p_left.getIntY() == 0 ) {
				
				
				fxl = p_left.getIntX(); 
				fxr = p_top.getIntX();	
				
				rec_fzl = 1.0/p_left.getZ();
				rec_fzr = 1.0/p_top.getZ();					
				
				//f_red_l 	= f_red_r 	= p_top_Color.getR()/p_top.getZ();
				f_red_l = p_left_Color.getR()/p_left.getZ();
				f_red_r = p_top_Color.getR()/p_top.getZ();
				f_green_l = p_left_Color.getG()/p_left.getZ();
				f_green_r = p_top_Color.getG()/p_top.getZ();
				f_blue_l = p_left_Color.getB()/p_left.getZ();
				f_blue_r = p_top_Color.getB()/p_top.getZ();
				
				// initialize camSC Point
				csX_l = p_left.getCSX()/p_left.getZ();
				csX_r = p_top.getCSX()/p_top.getZ();
				csY_l = p_left.getCSY()/p_left.getZ();
				csY_r = p_top.getCSY()/p_top.getZ();
				csZ_l = p_left.getCSZ()/p_left.getZ();
				csZ_r = p_top.getCSZ()/p_top.getZ();			
				
				// initialize normal
				normX_l = normal_left.getX()/p_left.getZ();
				normX_r = normal_top.getX()/p_top.getZ();
				normY_l = normal_left.getY()/p_left.getZ();
				normY_r = normal_top.getY()/p_top.getZ();	
				normZ_l = normal_left.getZ()/p_left.getZ();
				normZ_r = normal_top.getZ()/p_top.getZ();	
				
				
				for (double y = p_top.getIntY(); y> y_bottom; y--) {
					fxl -= mLower;
					fxr -= mR;
					
					rec_fzl -= rec_mzLower;
					rec_fzr -= rec_mzR;
					
					f_red_l -= m_red_Lower;
					f_red_r -= m_red_R ;
					f_green_l -= m_green_Lower;
					f_green_r -= m_green_R;
					f_blue_l -= m_blue_Lower;
					f_blue_r -= m_blue_R;					
					
					csX_l -= m_CSX_Lower;
					csX_r -= m_CSX_R;
					csY_l -= m_CSY_Lower;
					csY_r -= m_CSY_R;
					csZ_l -= m_CSZ_Lower;
					csZ_r -= m_CSZ_R;
					
					normX_l -= m_NormX_Lower;
					normX_r -= m_NormX_R;
					normY_l -= m_NormY_Lower;
					normY_r -= m_NormY_R;	
					normZ_l -= m_NormZ_Lower;
					normZ_r -= m_NormZ_R;					
					
					
					
					if((int)Math.round(fxr-1) >= (int)Math.round(fxl)) {
						
						Color c_left = new Color(f_red_l /rec_fzl, f_green_l /rec_fzl, f_blue_l /rec_fzl);
						Color c_right = new Color(f_red_r /rec_fzr, f_green_r /rec_fzr, f_blue_r /rec_fzr);
						
						// camera space recover
						Point3DH csPointLeft  = new Point3DH(csX_l /rec_fzl, csY_l /rec_fzl, csZ_l /rec_fzl);
						Point3DH csPointRight = new Point3DH(csX_r /rec_fzr, csY_r /rec_fzr, csZ_r /rec_fzr);					
						
						// normal recover
						Halfplane3DH normalLeft  =  new Halfplane3DH (normX_l /rec_fzl, normY_l /rec_fzl, normZ_l/rec_fzl);
						Halfplane3DH normalRight =  new Halfplane3DH (normX_r /rec_fzr, normY_r /rec_fzr, normZ_r/rec_fzr);
						
						Vertex3D pL = new Vertex3D ((int)Math.round(fxl), y, 1.0/rec_fzl,  c_left);
						Vertex3D pR = new Vertex3D ((int)Math.round(fxr-1), y, 1.0/rec_fzr,  c_right);	
						
						pL.setCameraPoint(csPointLeft);
						pR.setCameraPoint(csPointRight);
						
						pL.setNormal(normalLeft);
						pR.setNormal(normalRight);
						
						pL.setIsObj(p_top);
						pR.setIsObj(p_top);		
						
						if (p_top.isForPhongShading()) {
							pL.setToPhongShading();
							pR.setToPhongShading();					
						}
						
						DDAdrawer.drawLine(pL, pR, polygon, panel, pixelShader);
					}
				}					
			}
			
			
			
			
			
			
////////////////////top flat, top point and right point construct a flat line	top--------> right	
			
			if (p_top.getIntY()-p_right.getIntY() == 0 ) {
				
				fxl = p_top.getIntX(); 
				fxr = p_right.getIntX();
				rec_fzl = 1.0/p_top.getZ(); 
				rec_fzr = 1.0/p_right.getZ();

				
				// initialize color
				f_red_l = p_top_Color.getR()/p_top.getZ();
				f_red_r = p_right_Color.getR()/p_right.getZ();
				f_green_l = p_top_Color.getG()/p_top.getZ();
				f_green_r = p_right_Color.getG()/p_right.getZ();
				f_blue_l = p_top_Color.getB()/p_top.getZ();
				f_blue_r = p_right_Color.getB()/p_right.getZ();	
				
				// initialize camSC Point
				csX_l = p_top.getCSX()/p_top.getZ();
				csX_r = p_right.getCSX()/p_right.getZ();
				csY_l = p_top.getCSY()/p_top.getZ();
				csY_r = p_right.getCSY()/p_right.getZ();
				csZ_l = p_top.getCSZ()/p_top.getZ();
				csZ_r = p_right.getCSZ()/p_right.getZ();			
				
				// initialize normal
				normX_l = normal_top.getX()/p_top.getZ();
				normX_r = normal_right.getX()/p_right.getZ();
				normY_l = normal_top.getY()/p_top.getZ();
				normY_r = normal_right.getY()/p_right.getZ();	
				normZ_l = normal_top.getZ()/p_top.getZ();
				normZ_r = normal_right.getZ()/p_right.getZ();				
				
				
				
				for (double y = p_top.getIntY(); y> y_bottom; y--) {
					fxl -= mL;
					fxr -= mLower;
					
					rec_fzl -= rec_mzL;
					rec_fzr -= rec_mzLower;					
					
					f_red_l -= m_red_L ;
					f_red_r -= m_red_Lower ;
					f_green_l -= m_green_L ;
					f_green_r -= m_green_Lower ;
					f_blue_l -= m_blue_L ;
					f_blue_r -= m_blue_Lower ;	
					
					csX_l -= m_CSX_L;
					csX_r -= m_CSX_Lower;
					csY_l -= m_CSY_L;
					csY_r -= m_CSY_Lower;
					csZ_l -= m_CSZ_L;
					csZ_r -= m_CSZ_Lower;
					
					normX_l -= m_NormX_L;
					normX_r -= m_NormX_Lower;
					normY_l -= m_NormY_L;
					normY_r -= m_NormY_Lower;	
					normZ_l -= m_NormZ_L;
					normZ_r -= m_NormZ_Lower;	
					
					
					if( (int)Math.round(fxr-1) >= (int)Math.round(fxl) ) {
						
						Color c_left = new Color(f_red_l /rec_fzl, f_green_l /rec_fzl, f_blue_l /rec_fzl);
						Color c_right = new Color(f_red_r /rec_fzr, f_green_r /rec_fzr, f_blue_r /rec_fzr);
						
						// camera space recover
						Point3DH csPointLeft  = new Point3DH(csX_l /rec_fzl, csY_l /rec_fzl, csZ_l /rec_fzl);
						Point3DH csPointRight = new Point3DH(csX_r /rec_fzr, csY_r /rec_fzr, csZ_r /rec_fzr);					
						
						// normal recover
						Halfplane3DH normalLeft  =  new Halfplane3DH (normX_l /rec_fzl, normY_l /rec_fzl, normZ_l/rec_fzl);
						Halfplane3DH normalRight =  new Halfplane3DH (normX_r /rec_fzr, normY_r /rec_fzr, normZ_r/rec_fzr);
						
						Vertex3D pL = new Vertex3D ((int)Math.round(fxl), y, 1.0/rec_fzl,  c_left);
						Vertex3D pR = new Vertex3D ((int)Math.round(fxr-1), y, 1.0/rec_fzr,  c_right);	
						
						pL.setCameraPoint(csPointLeft);
						pR.setCameraPoint(csPointRight);
						
						pL.setNormal(normalLeft);
						pR.setNormal(normalRight);
						
						pL.setIsObj(p_top);
						pR.setIsObj(p_top);		
						
						if (p_top.isForPhongShading()) {
							pL.setToPhongShading();
							pR.setToPhongShading();					
						}
						
						DDAdrawer.drawLine(pL, pR, polygon, panel, pixelShader);
					}
				}		
			}	
		
	}

	public static PolygonRenderer make() {
		return new RainbowPolygonRenderer();	
	}
	
}
