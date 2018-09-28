package client;

import line.DDALineRenderer;
import line.LineRenderer;
import polygon.RainbowPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.WireframePolygonRenderer;

public class RendererTrio {

	public static LineRenderer getLineRenderer() {
		return DDALineRenderer.make();
	}
		
	public static PolygonRenderer getFilledRenderer() {
		return RainbowPolygonRenderer.make();
	}
	
	public static PolygonRenderer getWireframeRenderer() {
		return WireframePolygonRenderer.make();
	}

}