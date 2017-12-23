import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class SparseMatrixTest {

	
	@Test
	public void testConstructorNoRows() {
		double[][] arr = {};
		
		try {
			SparseMatrix A = new SparseMatrix(arr);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void testConstructorNoColumns() {
		double[][] arr = {{}};
		
		try {
			SparseMatrix A = new SparseMatrix(arr);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void testConstructorInconsistentColumns() {
		double[][] arr = {{1, 2, 3},
						  {4, 5}};
		
		try {
			SparseMatrix A = new SparseMatrix(arr);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void testConstructorSingleRow() {
		double[][] arr = {{1, 2, 3}};

		SparseMatrix A = new SparseMatrix(arr);
	}
	
	@Test
	public void testConstructorSingleColumn() {
		double[][] arr = {{1},
						  {2},
						  {3}};

		SparseMatrix A = new SparseMatrix(arr);
	}
	
	@Test
	public void testDotProduct() {
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
		assertEquals(result, 249.0, 0.0001);
	}
	
	@Test
	public void testToMatrix() {
		double[][] A = {{2, 4, 6},
						{9, 15, 12},
						{18, 14, 12}};
		SparseMatrix matrixA = new SparseMatrix(A);
		double[][] B = matrixA.toMatrix();
		SparseMatrix matrixB = new SparseMatrix(B);
		
		assertArrayEquals(A, B);
		assertEquals(matrixA, matrixB);
	}
	
	@Test
	public void testMatrixMultiply() {
		
		double[][] A = {{2, 4, 6},
				   		{9, 15, 12},
				   		{18, 14, 12}};
		
		SparseMatrix matrixA = new SparseMatrix(A);
		
		SparseMatrix result = matrixA.matrixMultiply(matrixA);
		//TODO add assert here
		//ADD more methods testing matrix multiply including invalid dimensions
	}
	
	@Test
	public void testEqualsNull() {
		double[][] arr = {{2, 3, 5},
						  {7, 9, 12},
						  {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr);
		assertFalse(A.equals(null));
	}
	
	@Test
	public void testEqualsObject() {
		double[][] arr = {{2, 3, 5},
						  {7, 9, 12},
						  {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr);
		assertFalse(A.equals(new HashMap<Integer, Double>()));
	}
	
	@Test
	public void testEqualsDifMatrix() {
		double[][] arr1 = {{2, 3, 5},
						  {7, 9, 12},
						  {15, 16, 17}};
		
		double[][] arr2 = {{3, 3, 5},
				  {7, 9, 12},
				  {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr1);
		SparseMatrix B = new SparseMatrix(arr2);
		
		assertFalse(A.equals(B));
	}
	
	@Test
	public void testEqualsTrue() {
		double[][] arr1 = {{2, 3, 5},
						  {7, 9, 12},
						  {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr1);
		SparseMatrix B = new SparseMatrix(arr1);
		
		assertTrue(A.equals(B));
		assertTrue(B.equals(A));
	}
	
	@Test
	public void testEqualsDifSize() {
		double[][] arr1 = {{2, 3, 5},
						  {7, 9, 12},
						  {15, 16, 17}};
		
		double[][] arr2 = {{2, 3, 5, 0},
				  		   {7, 9, 12, 0},
				  		   {15, 16, 17, 0}};
		
		double[][] arr3 = {{2, 3, 5},
		  		   		   {7, 9, 12},
		  		   		   {15, 16, 17},
		  		   		   {0, 0, 0}};
		
		SparseMatrix A = new SparseMatrix(arr1);
		SparseMatrix B = new SparseMatrix(arr2);
		SparseMatrix C = new SparseMatrix(arr3);
		
		assertFalse(A.equals(B));
		assertFalse(A.equals(C));
		assertFalse(B.equals(C));
	}
	
	@Test
	public void testSetEntry() {
		double[][] arr1 = {{2, 3, 5},
				  		   {7, 9, 12},
				  		   {15, 16, 17}};
		
		double[][] arr2 = {{0, 3, 5},
		  		   		   {7, 9, 12},
		  		   		   {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr1);
		A.setEntry(0, 0, 0);
		SparseMatrix B = new SparseMatrix(arr2);
		assertTrue(A.equals(B));
	}
	
	@Test
	public void testSetEntrySameArray() {
		double[][] arr = {{2, 3, 5},
				  		   {7, 9, 12},
				  		   {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr);
		SparseMatrix B = new SparseMatrix(arr);
		A.setEntry(0, 0, 0);
		assertFalse(A.equals(B));
		
		A.setEntry(0, 0, 2);
		assertTrue(A.equals(B));
	}
	
	@Test
	public void testSetEntryInvalid() {
		double[][] arr = {{2, 3, 5},
				  		   {7, 9, 12},
				  		   {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr);
		
		// negative row
		try {
			A.setEntry(-1, 0, 0);
			fail();
		} catch (IllegalArgumentException e) {}
		
		// negative col
		try {
			A.setEntry(0, -1, 0);
			fail();
		} catch (IllegalArgumentException e) {}
		
		// out of bounds row
		try {
			A.setEntry(3, 0, 0);
			fail();
		} catch (IllegalArgumentException e) {}
		
		// out of bounds column
		try {
			A.setEntry(0, 3, 0);
			fail();
		} catch (IllegalArgumentException e) {}

	}
	
	@Test
	public void testGetEntry() {
		double[][] arr = {{2, 3, 5},
				  		   {7, 9, 12},
				  		   {15, 16, 17}};
		
		SparseMatrix A = new SparseMatrix(arr);
		assertTrue(A.getEntry(1, 1) == 9);
		A.setEntry(1, 1, 7);
		assertTrue(A.getEntry(1, 1) == 7);
	}
	
}
