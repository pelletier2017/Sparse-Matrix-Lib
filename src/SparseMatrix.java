import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SparseMatrix {

	
	public static void main(String[] args) {
		//testMatrixMultiply();
		testDotProduct();
	}
	
	private static void testDotProduct() {
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
		
		System.out.println(dotProduct(A, B));
	}
	
	private static void testMatrixMultiply() {
		double[][] A = generateSparseMatrix(1000);
		double[][] B = generateSparseMatrix(1000);
		
		HashMap<Integer, HashMap<Integer, Double>> mapA = matrixToMap(A);
		HashMap<Integer, HashMap<Integer, Double>> mapB = matrixToMap(B);
		HashMap<Integer, HashMap<Integer, Double>> result = matrixMultiply(mapA, mapB);
		printHashMap(result);
	}

	private static HashMap<Integer, HashMap<Integer, Double>> matrixToMap(double[][] matrix) {
		HashMap<Integer, HashMap<Integer, Double>> nonZeroRows =  new HashMap<>();
		
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				double val = matrix[row][col];
				if (val != 0) {
					if (nonZeroRows.get(row) == null) {
						nonZeroRows.put(row, new HashMap<Integer, Double>());
					}
					
					HashMap<Integer, Double> columnsInRow = nonZeroRows.get(row);
					columnsInRow.put(col, val);
				}
			}
		}
		return nonZeroRows;
	}
	
	private static void printHashMap(HashMap<Integer, HashMap<Integer, Double>> nonZeroRows) {
		int nonZeroCount = 0;
		for (int row : nonZeroRows.keySet()) {
			HashMap<Integer, Double> nonZeroColsInRow = nonZeroRows.get(row);
			for (int col : nonZeroColsInRow.keySet()) {
				double val = nonZeroColsInRow.get(col);
				System.out.println(String.format("row: %d, col: %d, val: %.2f", row, col, val));
				nonZeroCount++;
			}
		}
		System.out.println("Non-Zero vals: " + nonZeroCount);
	}
	
	private static HashMap<Integer, HashMap<Integer, Double>> 
	matrixMultiply(HashMap<Integer, HashMap<Integer, Double>> nonZeroRowsA, 
				   HashMap<Integer, HashMap<Integer, Double>> nonZeroRowsB) {
		
		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<>();
		
		for (int rowA : nonZeroRowsA.keySet()) {
			HashMap<Integer, Double> nonZeroColsInRowA = nonZeroRowsA.get(rowA);
			
			for (int colA : nonZeroColsInRowA.keySet()) {
				
				// every (rowA, colA) is nonzero 
				// find corresponding row in B
				HashMap<Integer, Double> nonZeroColsInRowB = nonZeroRowsB.get(colA);
				
				if (nonZeroColsInRowB == null) {
					// if this corresponding row is empty,
					// current (rowA, colB) will not be used
					continue;
				}
				
				for (int colB : nonZeroColsInRowB.keySet()) {
					double valA = nonZeroColsInRowA.get(colA);
					double valB = nonZeroColsInRowB.get(colB);
					
					if (result.get(colA) == null) {
						result.put(colA, new HashMap<Integer, Double>());
					}
					
					HashMap<Integer, Double> resultColsInRow = result.get(colA);
					
					Double oldVal = resultColsInRow.get(colB);
					if (oldVal == null) {
						resultColsInRow.put(colB, valA * valB);
					} else {
						resultColsInRow.put(colB, oldVal + valA * valB);
					}
					
				}
			}
		}
		
		return result;
	}
	
	private static int dotProduct(HashMap<Integer, Double> A, HashMap<Integer, Double> B) {
		Set<Integer> matchingPairs = new HashSet<>();
		
		// creates set of integers contained in both sets
		matchingPairs.addAll(A.keySet());
		matchingPairs.retainAll(B.keySet());
		
		int total = 0;
		for (int k : matchingPairs) {
			total += A.get(k) * B.get(k);
		}
		return total;
	}
	
	private static double[][] generateSparseMatrix(int size) {
		double[][] matrix = new double[size][size];
		
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Random random = new Random();
				int val = 1 + random.nextInt(100);
				if (val <= 5) {
					matrix[row][col] = (double) val;
				}
			}
		}
			
		return matrix;
	}
}
