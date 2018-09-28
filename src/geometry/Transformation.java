package geometry;

public class Transformation {
	
	public double[][] m;
	
	public Transformation(){
		this.m = new double[4][4];		
		makeIdentity();
	}	
	
	public void makeIdentity() {
		// TODO Auto-generated method stub
		for (int i =0; i< 4; i++) {
			for (int j=0; j< 4; j++) {
				m[i][j] = (i==j)? 1:0;
			}			
		}		
	}	
	
	public void translate (double tx, double ty, double tz) {		
		double[][]  translate = {	{1,0,0,tx},
									{0,1,0,ty},
									{0,0,1,tz},
									{0,0,0,1} };		
		m = preMultiply (translate, m);		
	}	
	public void post_translate (double tx, double ty, double tz) {		
		double[][]  translate = {	{1,0,0,tx},
									{0,1,0,ty},
									{0,0,1,tz},
									{0,0,0,1} };		
		m = preMultiply ( m, translate);		
	}
	
	public void scale (double sx, double sy, double sz) {
		double[][]  scale = {	{sx,0, 0, 0},
								{0, sy,0, 0},
								{0, 0, sz,0},
								{0, 0, 0, 1} };		
		m = preMultiply (scale, m);
	}	
	public void post_scale (double sx, double sy, double sz) {
		double[][]  scale = {	{sx,0, 0, 0},
								{0, sy,0, 0},
								{0, 0, sz,0},
								{0, 0, 0, 1} };		
		m = preMultiply (m ,scale);
	}
	
	
	// rotate Z
	public void rotateZ ( double rz ) {
		double[][]  rotate = {		{Math.cos(rz),	-Math.sin(rz),		0,	0},
									{Math.sin(rz),	 Math.cos(rz),		0,	0},
									{0,				0,					1,	0},
									{0,				0,					0,	1} };		
		m = preMultiply (rotate, m);
	}	
	public void post_rotateZ ( double rz ) {
		double[][]  rotate = {		{Math.cos(rz),	-Math.sin(rz),		0,	0},
									{Math.sin(rz),	 Math.cos(rz),		0,	0},
									{0,				0,					1,	0},
									{0,				0,					0,	1} };		
		m = preMultiply (m, rotate);
	}
	
	
	// rotateX
	public void rotateX (double rx) {		
		double[][]  rotate = {		{1,				0,					0,	0},
									{0,	 Math.cos(rx),		-Math.sin(rx),	0},
									{0,	 Math.sin(rx),		 Math.cos(rx),	0},
									{0,				0,					0,	1} };
		m = preMultiply (rotate, m);
	}	
	public void post_rotateX (double rx) {		
		double[][]  rotate = {		{1,				0,					0,	0},
									{0,	 Math.cos(rx),		-Math.sin(rx),	0},
									{0,	 Math.sin(rx),		 Math.cos(rx),	0},
									{0,				0,					0,	1} };
		m = preMultiply (m, rotate);
	}
	
	
	// rotateY
	public void rotateY (double ry) {
		double[][]  rotate = {		{ Math.cos(ry),		0,	 Math.sin(ry),	0},
									{0,	 				1,				0,	0},
									{-Math.sin(ry),	 	0,	 Math.cos(ry),	0},
									{0,					0,				0,	1} };
		m = preMultiply (rotate, m);
	}	
	public void post_rotateY (double ry) {
		double[][]  rotate = {		{ Math.cos(ry),		0,	 Math.sin(ry),	0},
									{0,	 				1,				0,	0},
									{-Math.sin(ry),	 	0,	 Math.cos(ry),	0},
									{0,					0,				0,	1} };
		m = preMultiply (m, rotate);
	}
	
	
	public double[][] preMultiply (double [][] a , double[][] b ) {		
		double[][]  new_matrix = {	{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0} };		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) {
					new_matrix[i][j] += a[i][k]* b[k][j]; 
				}
			}
		}		
		return new_matrix;		
	}
	
	public Transformation preMultiplyTransformation(Transformation matrix) {
		double[][] 	b = matrix.getTransformMatrix();
		double[][]  new_matrix = {	{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0} };
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) {
					new_matrix[i][j] += m[i][k]* b[k][j]; 
				}
			}
		}		
		Transformation trans = Transformation.identity();
		trans.setTransformMatrix(new_matrix);	
		return trans;
	}
	
	public double[][] getTransformMatrix(){
		return m;
	}
	
	public void setTransformMatrix(double [][] matrix) {
		this.m = matrix;
	}
	
	/**
	 * pre-multiply a vertex (column matrix) by CTM/Transform Node
	 * @param Vertext3D
	 * @return Vertext3D
	 */
	public Vertex3D transformVertex3D (Vertex3D point){
		double[][] point_matrix = {{point.getX()}, {point.getY()}, {point.getZ()}, {1}};
		double[][] new_matrix = {{0}, {0}, {0}, {0}};
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<1; j++) {
				for (int k=0; k<4; k++) {
					new_matrix[i][j] += m[i][k]* point_matrix[k][j]; 
				}
			}
		}
//		Vertex3D transformedVertex = new Vertex3D(new_matrix[0][0], new_matrix[1][0], new_matrix[2][0], point.getColor());
		point.resetXYZ(new_matrix[0][0], new_matrix[1][0], new_matrix[2][0]);
		Vertex3D transformedVertex =  point;
		return transformedVertex;
	}
	
	public Point3DH transformPoint3DH (Point3DH  point){
		double[][] point_matrix = {{point.getX()}, {point.getY()}, {point.getZ()}, {1}};
		double[][] new_matrix = {{0}, {0}, {0}, {0}};
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<1; j++) {
				for (int k=0; k<4; k++) {
					new_matrix[i][j] += m[i][k]* point_matrix[k][j]; 
				}
			}
		}				
		return new Point3DH(new_matrix[0][0], new_matrix[1][0], new_matrix[2][0]);
	}
	
	// invert 4x4 matrix
	// reference: https://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
	// https://github.com/rchen8/Algorithms/blob/master/Matrix.java
	public void invert() {
		double[][] ivt = new double[4][4]; 
		ivt[0][0] = m[1][2]*m[2][3]*m[3][1] - m[1][3]*m[2][2]*m[3][1] + m[1][3]*m[2][1]*m[3][2] - m[1][1]*m[2][3]*m[3][2] - m[1][2]*m[2][1]*m[3][3] + m[1][1]*m[2][2]*m[3][3]; 
		ivt[0][1] = m[0][3]*m[2][2]*m[3][1] - m[0][2]*m[2][3]*m[3][1] - m[0][3]*m[2][1]*m[3][2] + m[0][1]*m[2][3]*m[3][2] + m[0][2]*m[2][1]*m[3][3] - m[0][1]*m[2][2]*m[3][3];
		ivt[0][2] = m[0][2]*m[1][3]*m[3][1] - m[0][3]*m[1][2]*m[3][1] + m[0][3]*m[1][1]*m[3][2] - m[0][1]*m[1][3]*m[3][2] - m[0][2]*m[1][1]*m[3][3] + m[0][1]*m[1][2]*m[3][3];
		ivt[0][3] = m[0][3]*m[1][2]*m[2][1] - m[0][2]*m[1][3]*m[2][1] - m[0][3]*m[1][1]*m[2][2] + m[0][1]*m[1][3]*m[2][2] + m[0][2]*m[1][1]*m[2][3] - m[0][1]*m[1][2]*m[2][3];
		ivt[1][0] = m[1][3]*m[2][2]*m[3][0] - m[1][2]*m[2][3]*m[3][0] - m[1][3]*m[2][0]*m[3][2] + m[1][0]*m[2][3]*m[3][2] + m[1][2]*m[2][0]*m[3][3] - m[1][0]*m[2][2]*m[3][3];
		ivt[1][1] = m[0][2]*m[2][3]*m[3][0] - m[0][3]*m[2][2]*m[3][0] + m[0][3]*m[2][0]*m[3][2] - m[0][0]*m[2][3]*m[3][2] - m[0][2]*m[2][0]*m[3][3] + m[0][0]*m[2][2]*m[3][3];
		ivt[1][2] = m[0][3]*m[1][2]*m[3][0] - m[0][2]*m[1][3]*m[3][0] - m[0][3]*m[1][0]*m[3][2] + m[0][0]*m[1][3]*m[3][2] + m[0][2]*m[1][0]*m[3][3] - m[0][0]*m[1][2]*m[3][3];
		ivt[1][3] = m[0][2]*m[1][3]*m[2][0] - m[0][3]*m[1][2]*m[2][0] + m[0][3]*m[1][0]*m[2][2] - m[0][0]*m[1][3]*m[2][2] - m[0][2]*m[1][0]*m[2][3] + m[0][0]*m[1][2]*m[2][3];
		ivt[2][0] = m[1][1]*m[2][3]*m[3][0] - m[1][3]*m[2][1]*m[3][0] + m[1][3]*m[2][0]*m[3][1] - m[1][0]*m[2][3]*m[3][1] - m[1][1]*m[2][0]*m[3][3] + m[1][0]*m[2][1]*m[3][3];
		ivt[2][1] = m[0][3]*m[2][1]*m[3][0] - m[0][1]*m[2][3]*m[3][0] - m[0][3]*m[2][0]*m[3][1] + m[0][0]*m[2][3]*m[3][1] + m[0][1]*m[2][0]*m[3][3] - m[0][0]*m[2][1]*m[3][3];
		ivt[2][2] = m[0][1]*m[1][3]*m[3][0] - m[0][3]*m[1][1]*m[3][0] + m[0][3]*m[1][0]*m[3][1] - m[0][0]*m[1][3]*m[3][1] - m[0][1]*m[1][0]*m[3][3] + m[0][0]*m[1][1]*m[3][3];
		ivt[2][3] = m[0][3]*m[1][1]*m[2][0] - m[0][1]*m[1][3]*m[2][0] - m[0][3]*m[1][0]*m[2][1] + m[0][0]*m[1][3]*m[2][1] + m[0][1]*m[1][0]*m[2][3] - m[0][0]*m[1][1]*m[2][3];
		ivt[3][0] = m[1][2]*m[2][1]*m[3][0] - m[1][1]*m[2][2]*m[3][0] - m[1][2]*m[2][0]*m[3][1] + m[1][0]*m[2][2]*m[3][1] + m[1][1]*m[2][0]*m[3][2] - m[1][0]*m[2][1]*m[3][2];
		ivt[3][1] = m[0][1]*m[2][2]*m[3][0] - m[0][2]*m[2][1]*m[3][0] + m[0][2]*m[2][0]*m[3][1] - m[0][0]*m[2][2]*m[3][1] - m[0][1]*m[2][0]*m[3][2] + m[0][0]*m[2][1]*m[3][2];
		ivt[3][2] = m[0][2]*m[1][1]*m[3][0] - m[0][1]*m[1][2]*m[3][0] - m[0][2]*m[1][0]*m[3][1] + m[0][0]*m[1][2]*m[3][1] + m[0][1]*m[1][0]*m[3][2] - m[0][0]*m[1][1]*m[3][2];
		ivt[3][3] = m[0][1]*m[1][2]*m[2][0] - m[0][2]*m[1][1]*m[2][0] + m[0][2]*m[1][0]*m[2][1] - m[0][0]*m[1][2]*m[2][1] - m[0][1]*m[1][0]*m[2][2] + m[0][0]*m[1][1]*m[2][2];
	
		double det = determinant();
		for (int i =0; i< 4; i++) {
			for (int j=0; j<4; j++) {
				m[i][j]=ivt[i][j]/det;
			}
		}
	}
	
	 private double determinant() {
		   double value;
		   value =
		   m[0][3]*m[1][2]*m[2][1]*m[3][0] - m[0][2]*m[1][3]*m[2][1]*m[3][0] - m[0][3]*m[1][1]*m[2][2]*m[3][0] + m[0][1]*m[1][3]*m[2][2]*m[3][0]+
		   m[0][2]*m[1][1]*m[2][3]*m[3][0] - m[0][1]*m[1][2]*m[2][3]*m[3][0] - m[0][3]*m[1][2]*m[2][0]*m[3][1] + m[0][2]*m[1][3]*m[2][0]*m[3][1]+
		   m[0][3]*m[1][0]*m[2][2]*m[3][1] - m[0][0]*m[1][3]*m[2][2]*m[3][1] - m[0][2]*m[1][0]*m[2][3]*m[3][1] + m[0][0]*m[1][2]*m[2][3]*m[3][1]+
		   m[0][3]*m[1][1]*m[2][0]*m[3][2] - m[0][1]*m[1][3]*m[2][0]*m[3][2] - m[0][3]*m[1][0]*m[2][1]*m[3][2] + m[0][0]*m[1][3]*m[2][1]*m[3][2]+
		   m[0][1]*m[1][0]*m[2][3]*m[3][2] - m[0][0]*m[1][1]*m[2][3]*m[3][2] - m[0][2]*m[1][1]*m[2][0]*m[3][3] + m[0][1]*m[1][2]*m[2][0]*m[3][3]+
		   m[0][2]*m[1][0]*m[2][1]*m[3][3] - m[0][0]*m[1][2]*m[2][1]*m[3][3] - m[0][1]*m[1][0]*m[2][2]*m[3][3] + m[0][0]*m[1][1]*m[2][2]*m[3][3];
		   return value;
		   }
	 
	 
	public void printMatrix() {
		for (int i =0; i< 4; i++) {
			for (int j=0; j< 4; j++) {
				System.out.print(m[i][j]+"	");
			}	
			System.out.println();
		}		
	}	

	public void copyTransform(Transformation CTM) {
		for (int i =0; i< 4; i++) {
			for (int j=0; j<4; j++) {
				m[i][j]=CTM.m[i][j];
			}
		}
	}
	
	public Halfplane3DH normalPostMultiplyThisTransform(Halfplane3DH normal) {
		double A = normal.getX();
		double B = normal.getY();
		double C = normal.getZ();
		double D = 1;
		
		double newA = A* m[0][0]+ B*m[1][0]+ C*m[2][0] + D*m[3][0];
		double newB = A* m[0][1]+ B*m[1][1]+ C*m[2][1] + D*m[3][1];
		double newC = A* m[0][2]+ B*m[1][2]+ C*m[2][2] + D*m[3][2];
		
		return new Halfplane3DH (newA, newB, newC);
	}

	public static Transformation identity() {
		// TODO Auto-generated method stub
		return new Transformation();
	}
	
	

}
