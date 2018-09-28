package client;


import geometry.Point2D;

import line.AlternatingLineRenderer;
import line.LineRenderer;
import line.BresenhamLineRenderer;
import line.DDALineRenderer;
import line.AntialiasingLineRenderer;

//import client.testPages.ParallelogramTest;
//import client.testPages.RandomLineTest;
//import client.testPages.StarburstLineTest;
import client.testPages.StarburstPolygonTest;
import client.testPages.RandomPolygonTest;
import client.testPages.CenteredTriangleTest;
import client.testPages.MeshPolygonTest;
import client.testPages.OnePolygonTest;
//import client.testPages.ShowOffPolygonTest;
//import client.testPages.ShowOffPolygonTest2;
import client.testPages.CenteredTriangleTest;
import client.interpreter.SimpInterpreter;

import polygon.FilledPolygonRenderer;
import polygon.WireframePolygonRenderer;
import polygon.RainbowPolygonRenderer;
import polygon.PolygonRenderer;

import windowing.drawable.ColoredDrawable;
import windowing.PageTurner;
import windowing.drawable.Drawable;
//import windowing.drawable.GhostWritingDrawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;
import windowing.drawable.ZbufferDrawable;
import windowing.drawable.DepthCueingDrawable;

public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;
	
	private static final int NUM_PAGES = 16;
	protected static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 1;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);
	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable[] panels;
	private Drawable fullPanel;
	
//	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer;
	private PolygonRenderer testRenderer;
	private PolygonRenderer wireframeRenderer;
	private RendererTrio renderers;
	private boolean hasArgument;
	private SimpInterpreter interpreter;
	private String InputFileName;
	
	public Client(Drawable drawable, String arg) {
		this.drawable = drawable;
		this.InputFileName = arg;
		hasArgument = (arg==null)? false : true;
		if(hasArgument) {
			choosePageNumber(arg);
//			char pageAlpha = InputFileName.charAt(InputFileName.length()-1);
//			System.out.println(pageAlpha-'A');
//			this.pageNumber = pageAlpha-'A';
		}
		createDrawables();
		createRenderers();
	}

	private void choosePageNumber(String page) {
		int pageNum = 1;
		switch(page){
		case "page-a1": pageNum=1; break;
		case "page-a2": pageNum=2; break;
		case "page-a3": pageNum=3; break;
		case "page-b1": pageNum=4; break;
		case "page-b2": pageNum=5; break;
		case "page-b3": pageNum=6; break;
		case "page-c1": pageNum=7; break;
		case "page-c2": pageNum=8; break;
		case "page-c3": pageNum=9; break;
		case "page-d": pageNum=10; break;
		case "page-e": pageNum=11; break;
		case "page-f1": pageNum=12; break;
		case "page-f2": pageNum=13; break;
		case "page-g": pageNum=14; break;
		case "page-h": pageNum=15; break;
		case "page-i": pageNum=0; break;
		default : pageNum=1;
		}
		
		this.pageNumber = pageNum;
		System.out.println(pageNum);
	}
	
	
	public void createDrawables() {
		
		// back white panel
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);
		
		// front black panel
		fullPanel = new TranslatingDrawable(image, point(50, 50),  dimensions(650, 650));
		fullPanel = new ZbufferDrawable(fullPanel);
		createPanels();
		
	}

	public void createPanels() {
		panels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
		panels[index] = new TranslatingDrawable(image, point(50, 50), PANEL_SIZE);
		}		
	}
	
	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}
	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	
	private void createRenderers() {		
		wireframeRenderer =  WireframePolygonRenderer.make();
		testRenderer = FilledPolygonRenderer.make();
		polygonRenderer =  RainbowPolygonRenderer.make();
	}
	
	@Override
	public void nextPage() {
		if(hasArgument) {
			argumentNextPage();
			hasArgument=false;
			pageNumber = (pageNumber + 1) % NUM_PAGES;
		}
		else {
			noArgumentNextPage();
			System.out.println("No arguments from cmd");
		}
	}

	private void argumentNextPage() {
		image.clear();
		fullPanel.clear();			
		if (InputFileName.equalsIgnoreCase("Test1")) {	
			
			System.out.println("Loading: Test1 Page, This page has no depth");	
			new OnePolygonTest(fullPanel, testRenderer);
			
		}else if (InputFileName.equalsIgnoreCase("Test2")) {
			
			System.out.println("Loading: Test2 Page, This page has depth");		
			Drawable depthCueingDrawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.GREEN);
			interpreter = new SimpInterpreter("diPage4" + ".simp", depthCueingDrawable, renderers);
			interpreter.interpret();
			
		}else {
			
			System.out.println("Loading:" + InputFileName + ".simp");
			interpreter = new SimpInterpreter(InputFileName + ".simp", fullPanel, renderers);
			interpreter.interpret();			
		}	
		
	}
	
	public void noArgumentNextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;
		
		image.clear();
		fullPanel.clear();
		String filename;

		switch(pageNumber) {
		case 1:  filename = "Page-a1"; 	break;
		case 2:  filename = "page-a2";	 break;
		case 3:	 filename = "page-a3";	 break;
		case 4:  filename = "page-b1";	 break;
		case 5:  filename = "page-b2";	 break;
		case 6:  filename = "page-b3";	 break;
		case 7:  filename = "page-c1";	 break;
		case 8:  filename = "page-c2";	 break;
		case 9:  filename = "page-c3";	 break;
		case 10:  filename = "Page-d";	 break;
		case 11:  filename = "Page-e";	 break;
		case 12:  filename = "Page-f1";	 break;
		case 13:  filename = "Page-f2";	 break;
		case 14:  filename = "Page-g";	 break;
		case 15:  filename = "Page-h";	 break;
		case 0:  filename = "Page-i";	 break;

		default: defaultPage();
				 return;
		}		

		System.out.println("No Input, Directs to: " + filename + ".simp");
		interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);		
		interpreter.interpret();
	}
		
	@FunctionalInterface
	private interface TestPerformer {
		public void perform(Drawable drawable, LineRenderer renderer);
	}
	
	
	public void polygonDrawerTestPage(Drawable[] panelArray) {
		image.clear();
		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
			panel.clear();
		}		
		
		new OnePolygonTest(panelArray[0], polygonRenderer);
	}
	
	public void polygonDrawerPage(Drawable[] panelArray) {
		image.clear();
		for(Drawable panel: panels) {	// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
			panel.clear();
		}
			
		new StarburstPolygonTest(panelArray[0], wireframeRenderer);
		new MeshPolygonTest(panelArray[1], wireframeRenderer, MeshPolygonTest.NO_PERTURBATION);
		new MeshPolygonTest(panelArray[2], wireframeRenderer, MeshPolygonTest.USE_PERTURBATION);
		new RandomPolygonTest(panelArray[3], wireframeRenderer);
	}

	private void defaultPage() {
		image.clear();
		fullPanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}
}
