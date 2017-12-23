import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// Look into high performance maps with primitives, HPPC? Trove? Colt? FastUtil?

// Great speed comparison chart
// http://b010.blogspot.com/2009/05/speed-comparison-of-1-javas-built-in.html
// looks like Colt is fastest, worth profiling ourselves

// Should equals() method be true for SparseMatrix.equals(double[][])?

// Methods to add:
// Transpose
// RREF
// EF
// getRowVector
// getColumnVector

// Classes to add:
// SparseVector
// SparseRow extends SparseVector
// SparseCol extends SparseVector
// rowMap HashMap<Integer, SparseCol> (extends HashMap?)
// colMap HashMap<Integer, SparseRow>

// Questions:
// what are use-cases for a library like this?, some goal for the user?
// could you share data set?

public class SparseMatrix {
	
	private HashMap<Integer, HashMap<Integer, Double>> nonZeroRowMap;
	private HashMap<Integer, HashMap<Integer, Double>> nonZeroColMap;
	
	private int numRows;
	private int numCols;

	public SparseMatrix(double[][] matrix) {
		
		if (matrix.length == 0) {
			throw new IllegalArgumentException("Matrix must have more than 0 rows");
		}
		
		this.numRows = matrix.length;
		this.numCols = matrix[0].length;
		
		if (numCols == 0) {
			throw new IllegalArgumentException("Matrix must have more than 0 columns");
		}
		
		for (int i = 1; i < numRows; i++) {
			if (matrix[i].length != numCols) {
				throw new IllegalArgumentException("Matrix does not have consistent number of columns");
			}
		}
		
		nonZeroRowMap = matrixToRowMap(matrix);
		nonZeroColMap = matrixToColMap(matrix);
	}

	public SparseMatrix(HashMap<Integer, HashMap<Integer, Double>> sparseRowMap, int numRows, int numCols) {
		// TODO Auto-generated constructor stub
		// Think about SparseColMap and SparseRowMap objects, to get more obvious constructor
		// used at the end of matrixMultiply
		
		
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
		
		for (int col = 0; col < matrix[0].length; col++) {
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
	
	public static HashMap<Integer, HashMap<Integer, Double>> rowMapToColMap(HashMap<Integer, HashMap<Integer, Double>> rowMap) {
		HashMap<Integer, HashMap<Integer, Double>> colMap = new HashMap<>();
		for (int row : rowMap.keySet()) {
			HashMap<Integer, Double> nonZeroColsInRow = rowMap.get(row);
			for (int col : nonZeroColsInRow.keySet()) {
				//TODO colMap.setEntry(row, col, val);
			}
		}
		return colMap;
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
	
	public SparseMatrix matrixMultiply(SparseMatrix other) {
		
		//TODO add test for this
		if (this.numCols != other.numRows) {
			throw new IllegalArgumentException(String.format("%d columns of first matrix don't match %d rows of second matrix", 
											this.numCols, other.numRows));
		}
		
		HashMap<Integer, HashMap<Integer, Double>> nonZeroRowsA = this.nonZeroRowMap;
		HashMap<Integer, HashMap<Integer, Double>> nonZeroRowsB = other.getNonZeroRowMap();
		
		
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
					
					HashMap<Integer, Double> resultColsInRow = result.get(rowA);
					
					// TODO add abstraction to add value to matrixMap
					Double oldVal = resultColsInRow.get(colB);
					if (oldVal == null) {						
						resultColsInRow.put(colB, valA * valB);
					} else {
						resultColsInRow.put(colB, oldVal + valA * valB);
					}
					
				}
			}
		}
		
		return new SparseMatrix(result, this.numRows, other.numCols);
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
	
	@Override
	public String toString() {	
		int nonZeroCount = 0;
		StringBuilder builder = new StringBuilder();
		
		for (int row : nonZeroRowMap.keySet()) {
			HashMap<Integer, Double> nonZeroColsInRow = nonZeroRowMap.get(row);
			for (int col : nonZeroColsInRow.keySet()) {
				double val = nonZeroColsInRow.get(col);
				builder.append(String.format("row: %d, col: %d, val: %.3f\n", row, col, val));
				nonZeroCount++;
			}
		}
		builder.append("Non-Zero vals: " + nonZeroCount);
		return builder.toString();
	}
	
	@Override
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
		
		SparseMatrix other = (SparseMatrix) otherObject;
		
		return this.nonZeroRowMap.equals(other.nonZeroRowMap) && this.numRows == other.numRows && this.numCols == other.numCols;
	}
	
	public double getEntry(int row, int col) {
		if (nonZeroRowMap.get(row) == null) {
			return 0.0;
		}
		
		HashMap<Integer, Double> nonZeroColsInRow = nonZeroRowMap.get(row);
		if (nonZeroColsInRow.get(col) == null) {
			return 0.0;
		} else {
			return nonZeroColsInRow.get(col);
		}
	}
	
	//TODO may need to be refactored
	//TODO add test cases covering everything past val == 0
	public void setEntry(int row, int col, double val) {
		
		if (row < 0 || row >= numRows) {
			throw new IllegalArgumentException("invalid row:" + row);
		}
		
		if (col < 0 || col >= numCols) {
			throw new IllegalArgumentException("invalid col:" + col);
		}
		
		if (val == 0) {
			if (nonZeroRowMap.get(row) == null) {
				// already is set to zero
				return;
			}
			HashMap<Integer, Double> nonZeroColsInRow = nonZeroRowMap.get(row);
			
			if (nonZeroColsInRow.get(col) == null) {
				// already set to zero
				return;
			} else {
				nonZeroColsInRow.remove(col);
				if (nonZeroColsInRow.isEmpty()) {
					nonZeroRowMap.remove(row);
				}
			}
			
		} else {
			// value non-zero
			if (nonZeroRowMap.get(row) == null) {
				nonZeroRowMap.put(row, new HashMap<Integer, Double>());
			}
			HashMap<Integer, Double> nonZeroColsInRow = nonZeroRowMap.get(row);
			nonZeroColsInRow.put(col, val);
		}
	}
	
	public HashMap<Integer, HashMap<Integer, Double>> getNonZeroRowMap() {
		return nonZeroRowMap;
	}
	
	public HashMap<Integer, HashMap<Integer, Double>> getNonZeroColMap() {
		return nonZeroColMap;
	}
	
	
}
