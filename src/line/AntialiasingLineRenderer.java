package line;


import geometry.Vertex3D;
import polygon.Polygon;
import polygon.shading.PixelShader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class AntialiasingLineRenderer implements LineRenderer{
	
	private AntialiasingLineRenderer() {}
	private double lineWidth = 0.65; 
	private double r = 0.5; // radius of a round pixel;
	private double maxBrightness = 0.95; // the maximum brightness of line produce by DDA;
	private double minBrightness = 0.80; // the minimum brightness of line produce by DDA;
	private double randomizeInitial = 0.8;
	private double randomizeRange = 1-randomizeInitial;

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Polygon poly, Drawable drawable, PixelShader pixelShader) {
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		
		double slope = deltaY / deltaX;
		Color argbColor = p1.getColor();
		
		double y = p1.getIntY(), y_up, y_up2, y_down, y_down2;
		double d, d_up, d_up2, d_down, d_down2;	
		
		// set up a b c for ax+by+c=0		
		double a = slope;
		double b = -1.0; 
		double c = p2.getIntY() - slope * p2.getIntX();	
		
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) { // back up code
			y_up = y+1;
			y_up2 = y+2;
			y_down = y-1;
			y_down2 = y-2;
			
			d 		= distanceToLine(x, (int)Math.round(y), a, b, c); 
			d_up 	= distanceToLine(x, (int)Math.round(y_up), a, b, c)-lineWidth;
			d_up2 	= distanceToLine(x, (int)Math.round(y_up2), a, b, c)-lineWidth;
			d_down 	= distanceToLine(x, (int)Math.round(y_down), a, b, c)-lineWidth;
			d_down2 = distanceToLine(x, (int)Math.round(y_down2), a, b, c)-lineWidth;	
			
			// randommize the distance by multiple of 0.9-1.0 
			d 		= d*( Math.random()*randomizeRange + randomizeInitial);
			d_up 	= d_up*( Math.random()*randomizeRange + randomizeInitial);
			d_up2 	= d_up2*( Math.random()*randomizeRange + randomizeInitial);
			d_down 	= d_down*( Math.random()*randomizeRange + randomizeInitial);
			d_down2 = d_down2*( Math.random()*randomizeRange + randomizeInitial);						
			
 			// (x, y) pixels
			fillCenter(x, (int)Math.round(y), drawable, argbColor, d);
			fillMargin(x, (int)Math.round(y_up), drawable, argbColor, d_up);
			fillMargin(x, (int)Math.round(y_down), drawable, argbColor, d_down);
			fillMargin(x, (int)Math.round(y_up2), drawable, argbColor, d_up2);
			fillMargin(x, (int)Math.round(y_down2), drawable, argbColor, d_down2);
			
			y =  y + slope;
		}
	}
	
	
	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new AntialiasingLineRenderer());
	}
	
	private void fillCenter(int x, int y, Drawable canvas, Color argbColor, double distance) {		
		int oldColor = canvas.getPixel(x, y);
		int newColor = Math.max(oldColor, centerLineColorAsARGB(distance, argbColor, r).asARGB());
		canvas.setPixel(x, (int)Math.round(y), 0.0, newColor);
	}
	
	private void fillMargin(int x, int y, Drawable canvas, Color argbColor, double distance) {		
		int oldColor = canvas.getPixel(x, y);
		int newColor = Math.max(oldColor, antiAliasColorAsARGB(distance, argbColor, r).asARGB());
		canvas.setPixel(x, (int)Math.round(y), 0.0, newColor);
	}

	private double distanceToLine(int px, int py, double a, double b, double c) {
		return Math.abs(a*px+b*py+c)/Math.sqrt(a*a+b*b);
	}
	
	private Color antiAliasColorAsARGB(double d, Color color, double r) {
		if(Math.abs(d/r)>1) {
			return color.scale(0);
		}
		double theta = Math.acos(d/r);		
		double brightness =   1.0 - ((1.0 - theta/Math.PI) + d*(Math.sqrt(r*r-d*d)/(Math.PI*r*r)));
		//System.out.println(brightness+ " | " + d);
		return color.scale(brightness);		
	}
	
	private Color centerLineColorAsARGB(double d, Color color, double r) {
		
		if ((r+d)<lineWidth) { // pixel inside the line width, output max color
			return color.scale(maxBrightness);
		}
		double h = lineWidth-d;
		double theta = Math.acos(h/r);		
		double brightness = 1 - theta/Math.PI + h*(Math.sqrt(r*r-h*h)/(Math.PI*r*r)) ;
		brightness = (brightness>minBrightness)? brightness: minBrightness;
		return color.scale(brightness).clamp();
	}
	
}
