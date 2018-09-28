package client.testPages;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import polygon.Polygon;
import polygon.PolygonRenderer;

import java.util.Random;

import geometry.Vertex3D;

public class MeshPolygonTest {
	private static final int ROW_NUM = 10;
	private static final int COL_NUM = 10;
	private static final double PERTURBATION = 0.3;
	public static final String NO_PERTURBATION = "false";
	public static final String USE_PERTURBATION = "true";
	
	private final PolygonRenderer renderer;
	private final Drawable panel;
	private double pertValue = 0;
	private  Vertex3D[][] grid = new Vertex3D[ROW_NUM][COL_NUM]; 
	Vertex3D start;
	
	static long seed = (int)(Math.random()*100000);
	
	public MeshPolygonTest(Drawable panel, PolygonRenderer renderer, String perturbation) {
		this.panel = panel;
		this.renderer = renderer;
		
		if (perturbation.equals(NO_PERTURBATION)) {
			pertValue = 0;
		}else {
			pertValue = PERTURBATION ;
		}
		
		makeStart();
		makeGrid();
		render();
	}
	
	
	private void render() {
		
		for(int i=0; i<ROW_NUM-1; i++ ) {
			for (int j=0; j< COL_NUM-1; j++) {
				Vertex3D pLT = grid[i][j];
				Vertex3D pRT = grid[i+1][j];
				Vertex3D pLB = grid[i][j+1];
				Vertex3D pRB = grid[i+1][j+1];
				
				Polygon polygonT = Polygon.make(pLT, pLB, pRB);
				polygonT = Polygon.makeEnsuringClockwise(polygonT.get(0),polygonT.get(1),polygonT.get(2));
				Polygon polygonB = Polygon.make(pLT, pRT, pRB);
				polygonB = Polygon.makeEnsuringClockwise(polygonB.get(0),polygonB.get(1),polygonB.get(2));
				
				renderer.drawPolygon(polygonT,  panel);	
				renderer.drawPolygon(polygonB,  panel);					
				}
			}
	}
	
	
	private void makeStart() {
		int x = panel.getWidth()/((ROW_NUM) * 2);
		int y = panel.getWidth()/((COL_NUM) * 2);
		start = new Vertex3D(x, y, 0, Color.random());
	}
	
	private void makeGrid() {
		
	    // create random object
	    Random rnd = new Random();

	    // setting seed
	    rnd.setSeed(seed);
		
		int gridWidth = panel.getWidth() / (COL_NUM);
		int gridHeight = panel.getHeight() / (COL_NUM);
		double pertX=0, pertY=0; 
		double x, y;
		
		for(int i=0; i<ROW_NUM; i++ ) {
			for (int j=0; j<COL_NUM; j++) {
				pertX = (int)((rnd.nextDouble()*2-1)*gridWidth*pertValue);
				pertY = (int)((rnd.nextDouble()*2-1)*gridHeight*pertValue);
				//System.out.println(pertX+" | "+pertY);
				x = start.getX() + i*gridWidth  + pertX;
				y = start.getY() + j*gridHeight + pertY;
				grid[i][j] = new Vertex3D((int)Math.round(x), (int)Math.round(y), 0.0, Color.random());
			}
		}
	}
	
}
