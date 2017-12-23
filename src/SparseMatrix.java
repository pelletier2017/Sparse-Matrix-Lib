import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

//TODO verify there's no problems with references when working with nested arrays
// Look into high performance maps with primitives, HPPC? Trove? Colt? FastUtil?

// Great speed comparison chart
// http://b010.blogspot.com/2009/05/speed-comparison-of-1-javas-built-in.html
// looks like Colt is fastest, worth profiling ourselves

public class SparseMatrix {
	
	private HashMap<Integer, HashMap<Integer, Double>> nonZeroRowMap;
	private HashMap<Integer, HashMap<Integer, Double>> nonZeroColMap;
	
	private int numRows;
	private int numCols;

	public SparseMatrix(double[][] matrix, int numRows, int numCols) {
		nonZeroRowMap = matrixToRowMap(matrix);
		nonZeroColMap = matrixToColMap(matrix);
		this.numRows = numRows;
		this.numCols = numCols;
	}
	
	public static void main(String[] args) {
		
	}

	public static HashMap<Integer, HashMap<Integer, Double>> matrixToRowMap(double[][] matrix) {
		HashMap<Integer, HashMap<Integer, Double>> nonZeroRows =  new HashMap<>();
		
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				double val = matrix[row][col];
				if (val != 0) {
					if (nonZeroRows.get(row) == null) {
						nonZeroRows.put(row, new HashMap<Integer, Double>());
					}
					
					HashMap<Integer, Double> NonZeroColumnsInRow = nonZeroRows.get(row);
					NonZeroColumnsInRow.put(col, val);
				}
			}
		}
		return nonZeroRows;
	}
	
	public static HashMap<Integer, HashMap<Integer, Double>> matrixToColMap(double[][] matrix) {
		HashMap<Integer, HashMap<Integer, Double>> nonZeroCols =  new HashMap<>();
		
		for (int col = 0; col < matrix.length; col++) {
			for (int row = 0; row < matrix.length; row++) {
				double val = matrix[row][col];
				if (val != 0) {
					if (nonZeroCols.get(col) == null) {
						nonZeroCols.put(col, new HashMap<Integer, Double>());
					}
					
					HashMap<Integer, Double> NonZeroRowsInColumn = nonZeroCols.get(col);
					NonZeroRowsInColumn.put(row, val);
				}
			}
		}
		return nonZeroCols;
	}
	
	public double[][] toMatrix() {
		double[][] matrix = new double[numRows][numCols];
		for (int row : nonZeroRowMap.keySet()) {
			HashMap<Integer, Double> nonZeroColsInRow = nonZeroRowMap.get(row);
			
			for (int col : nonZeroColsInRow.keySet()) {
				double val = nonZeroColsInRow.get(col);
				matrix[row][col] = val;
			}
		}
		
		return matrix;
	}
	
	public static void printMatrixMap(HashMap<Integer, HashMap<Integer, Double>> nonZeroRows) {
		//TODO make this into a toString() method
		//needs stringbuilder
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
	
	public HashMap<Integer, HashMap<Integer, Double>> matrixMultiply(SparseMatrix B) {
		
		//TODO throw exception if cols of A doesn't match rows of B, cant matrix multiply
		
		HashMap<Integer, HashMap<Integer, Double>> nonZeroRowsA = this.nonZeroRowMap;
		HashMap<Integer, HashMap<Integer, Double>> nonZeroRowsB = B.getNonZeroRowMap();
		
		
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
					//System.out.println(String.format("rowA %d, colA %d, colB", rowA, colA, colB));
					double valA = nonZeroColsInRowA.get(colA);
					double valB = nonZeroColsInRowB.get(colB);
					
					if (result.get(colA) == null) {
						result.put(colA, new HashMap<Integer, Double>());
					}
					
					HashMap<Integer, Double> resultColsInRow = result.get(rowA);
					
					// TODO add abstraction to add value to matrixMap
					Double oldVal = resultColsInRow.get(colB);
					if (oldVal == null) {
						//System.out.println(String.format("add %.2f to (%d, %d) by using (%.2f * %.2f)", 
						//		valA * valB, colA, colB, valA, valB));
						
						resultColsInRow.put(colB, valA * valB);
					} else {
						resultColsInRow.put(colB, oldVal + valA * valB);
						//System.out.println(String.format("add %.2f to (%d, %d) by using (%.2f * %.2f)",
						//		valA * valB, colA, colB, valA, valB));
					}
					
				}
			}
		}
		
		return result;
	}
	
	public static int dotProduct(HashMap<Integer, Double> A, HashMap<Integer, Double> B) {
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
	
	public static double[][] generateSparseMatrix(int size) {
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
	
	public boolean equals(Object otherObject) {
		if (otherObject == null) {
			return false;
		}
		
		if (this == otherObject) {
			return true;
		}
		
		if (getClass() != otherObject.getClass()) {
			return false;
		}
		
		//TODO LAST WORKING HERE -----------------------------------------------------------------------
		// not done yet
		return false;
	}
	
	public HashMap<Integer, HashMap<Integer, Double>> getNonZeroRowMap() {
		return nonZeroRowMap;
	}
	
	public HashMap<Integer, HashMap<Integer, Double>> getNonZeroColMap() {
		return nonZeroColMap;
	}
}
