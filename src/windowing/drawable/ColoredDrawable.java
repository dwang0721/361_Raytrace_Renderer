package windowing.drawable;

public class ColoredDrawable extends DrawableDecorator {

	private int color;
	
	public ColoredDrawable(Drawable delegate, int RgbaColor) {
		super(delegate); // calls the super class DrawableDecorator constructor
		this.color = RgbaColor; // get the RGBA color		
		// this.delegate = delegate;  pass the delegate to DrawableDecorator 
	}	
	
	@Override
	public void clear() { // from Drawable.fill()
		fill(color, Double.MAX_VALUE);
	}


}
