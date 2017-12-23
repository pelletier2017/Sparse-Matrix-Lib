import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class SparseMatrixTest {

	@Test
	public void testdotProduct() {
		HashMap<Integer, Double> A = new HashMap<>();
		A.put(0, 3.0);
		A.put(1, 8.0);
		A.put(2, 7.0);
		A.put(4, 18.0);
		
		HashMap<Integer, Double> B = new HashMap<>();
		B.put(0, 14.0);
		B.put(1, 11.0);
		B.put(2, 17.0);
		B.put(3, 5.0);
		
		double result = SparseMatrix.dotProduct(A, B);
		assertEquals(result, 249.0, 0.0000000001);
	}
	
	@Test
	public void testToMatrix() {
		double[][] A = {{2, 4, 6},
						{9, 15, 12},
						{18, 14, 12}};
		SparseMatrix m = new SparseMatrix(A, 3, 3);
		double[][] B = m.toMatrix();
		assertTrue(A.equals(B));
	}
	
	@Test
	public void testIntegerMatrixMultiply() {
		
		double[][] A = {{2, 4, 6},
				   		{9, 15, 12},
				   		{18, 14, 12}};
		
		SparseMatrix matrixA = new SparseMatrix(A, 3, 3);
		
		HashMap<Integer, HashMap<Integer, Double>> result = matrixA.matrixMultiply(matrixA);
		//SparseMatrix.printMatrixMap(result);
	}
	
}
