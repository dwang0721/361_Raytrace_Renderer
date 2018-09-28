package windowing.drawable;

public class ZbufferDrawable extends DrawableDecorator {

	private double backClip = -200;
	private int col, row;
	public double[][] zBuffer;
	
	public ZbufferDrawable(Drawable delegate) {
		super(delegate);
		
		// transform delegate dimension into 2d array col and row;
		row=delegate.getHeight()+1;
		col=delegate.getWidth()+1;
		zBuffer=new double[row][col];
		resetZbuffer();
	}	
	
	@Override
	public double getZValue(int x, int y) {
		return zBuffer[y][x];
	}	
	
	@Override
	public void setPixel(int x, int y, double z, int argbColor) {	
//		System.out.println(x + " | " + y + " | " + z + " | " + zBuffer[y][x]);
		if (x >=0 && x< col && y>= 0 && y< row) {
			
			if ( z>=zBuffer[y][x] ) {				
				zBuffer[y][x] = z;
				delegate.setPixel(x, y, z, argbColor);
			}
		}			
	}
	
	
	
	@Override
	public void clear() {
		fill(ARGB_BLACK, Double.MAX_VALUE);
		resetZbuffer();
	}
	
	private void resetZbuffer(){		
		for (int i=0; i< row; i++ ) {
			for (int j=0; j< col; j++) {
				zBuffer[i][j]=backClip;
			}
		}
	}
	
	
}
