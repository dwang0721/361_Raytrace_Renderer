package polygon;

import java.util.ArrayList;
import java.util.List;

import geometry.Vertex3D;

public class Chain {

	protected int numVertices = 0;
	protected List<Vertex3D> vertices = new ArrayList<Vertex3D>();

	// initialVertices are rounded to prevent rounding errors in interpolation.
	public Chain(Vertex3D... initialVertices) {
		super();
		
		for(Vertex3D vertex: initialVertices) {
			add(vertex);
		}
	}

	public void add(Vertex3D vertex) {
		vertices.add(vertex);
		numVertices++;
	}
	
	public Vertex3D[] getVerticeArray() {
		Vertex3D[] vertexArray = new Vertex3D[vertices.size()];
		return vertices.toArray(vertexArray); 
	}

	public int length() {
		return numVertices;
	}
	
	public Vertex3D get(int i) {
		return vertices.get(i);
	}
	
	public void print() {
		for (int i=0; i< numVertices; i++) {
			vertices.get(i).print();
		}
		System.out.println();
	}
	
	public String toString() {
		String result = "Chain[";
		for(Vertex3D vertex: vertices) {
			result = result + vertex.getPoint().toString() + " ";
		}
		
		return result.substring(0, result.length()-1) + "]";
	}
}