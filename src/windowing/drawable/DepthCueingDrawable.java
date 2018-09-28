package windowing.drawable;

import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator {

	private double farClip ;
	private double nearClip ;
	private int col, row;
	private  Color depthColor;
	public double[][] zBuffer;
	
	public DepthCueingDrawable(Drawable delegate, int nc, int fc, Color color) {
		super(delegate);
		
		// transform delegate dimension into 2d array col and row;
		this.row=delegate.getHeight()+1;
		this.col=delegate.getWidth()+1;
		this.farClip = fc;
		this.nearClip = nc;
		this.depthColor=color;
		zBuffer=new double[row][col];
		resetZbuffer();
	}	
	
	@Override
	public double getZValue(int x, int y) {
		return zBuffer[y][x];
	}	
	
	@Override
	public void setPixel(int x, int y, double csz, int argbColor) {		
		
		int trueColor = argbColor;
		
		Color objColor = Color.fromARGB(argbColor);
		
		if (x >=0 && x< col && y>= 0 && y< row) {
			
			
			if (csz >= nearClip) {
				trueColor = argbColor;
			}else if (csz < farClip) {
				trueColor = depthColor.asARGB();
			}else if ( csz>farClip && csz < nearClip ) {	// far start, 0 |  near, end, 1				
				double ratio = (csz-farClip)/(nearClip-farClip); // how close to depth color;				
				Color trueColorObj = objColor.scale(ratio).add(depthColor.scale(1-ratio));
//				Color trueColorObj = Color.BLUE.scale(ratio).add(Color.RED.scale(1-ratio));
				trueColor = trueColorObj.asARGB();
				if (csz>zBuffer[y][x] && csz < nearClip) {
					delegate.setPixel(x, y, csz, trueColor);
				}				
			}		
		
		}
		
	}			
	
	
//	public void setPixel(int x, int y, double csz, int argbColor) {		
//		if (x >=0 && x< col && y>= 0 && y< row) {
//			
//			if ( csz>=zBuffer[y][x] && csz <= nearClip ) {				
//				zBuffer[y][x] = csz;
//				double scaleFac = (backClip-csz)/backClip;
//				delegate.setPixel(x, y, csz, c.scale(scaleFac).asARGB());
//			}
//		}			
//	}
	
	@Override
	public void clear() {
		fill(ARGB_BLACK, Double.MAX_VALUE);
		resetZbuffer();
	}
	
	private void resetZbuffer(){		
		for (int i=0; i< row; i++ ) {
			for (int j=0; j< col; j++) {
				zBuffer[i][j]=farClip;
			}
		}
	}
	
	
}
