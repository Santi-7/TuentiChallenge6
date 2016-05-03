import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 7 - Tiles.
 * @author Santiago Gil Begué.
 */
public class Reto7 {

	/* Current matrix to solve. */
	private static byte[][] matrix;
	
	/**
	 * @param args - program needs one argument: the file which
	 * 			     provides the information about all the cases
	 * 				 and the matrixes to solve.
	 * 
	 * @throws FileNotFoundException - File args[0] is not found.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Scanner file = new Scanner(new File(args[0]));
		// C = number of cases of the problem.
		short cases = file.nextShort(); file.nextLine();
		// Solve all the cases.
		for (short c = 1; c <= cases; c++) {
			// Dimensions of the matrix carved on the tiles.
			short n = file.nextShort();
			short m = file.nextShort();
			file.nextLine();
			// Construct matrix as specified.
			matrix = new byte[n][m];
			for (short i = 0; i < n; i++) {
				String line = file.nextLine();
				for (short j = 0; j < m; j++) {
					matrix[i][j] = code(line.charAt(j));
				}
			}
			// Solve the problem.
			String maxValue = solveMaxValue() + "";
			if (maxValue.equals("null")) maxValue = "INFINITY";
			System.out.println("Case #" + c + ": " + maxValue);
		}
		file.close();
	}
	
	/**
	 * Codes the char b as specifies the challenge.
	 * 
	 * @param b - char to code.
	 * @return codification of the char [b] as specifies the challenge.
	 */
	private static byte code(char b) {
		if (b == '.') return 0;
		// Capital letters have a lower ascii value than lower case
		// letters, so with just one condition it is enough.
		else if (b >= 'a') return (byte) ((b - 'a' + 1) * -1);
		else return (byte) (b - 'A' + 1) ;
	}
	
	/**
	 * Returns the highest value of the values' sum of a sub-matrix
	 * of [matrix], if it would be infinite copied. For an infinite
	 * result, null is returned.
	 * 
	 * @return the highest value of the values' sum of a sub-matrix
	 * of [matrix], if it would be infinite copied. For an infinite
	 * result, null is returned.
	 */
	private static Short solveMaxValue() {
		if (detectInfinite()) return null;
		// Maximum sum of a sub-matrix. Initial to 0, equivalent to
		// a sub-matrix of 0 elements.
		short maximumSum = 0;
		// For all sub-matrixes.
		for (short up = 0; up < matrix.length; up++) {
			short bot = up;
			// Row accumulate of the values from up rows to bot rows.
			short[] acc = new short[matrix[0].length];
			do {
				// Accumulate the new row.
				acc = plusVectors(acc, matrix[bot]);
				// Current highest sum of all the sub-matrixes from
				// row [up] to row [bot].
				short currentSum = kadane(acc);
				if (currentSum > maximumSum) maximumSum = currentSum;
				bot = (short) ((bot+1) % matrix.length);
			}
			// Do it matrix.length - 2 times. (Efficiency: It does not
			// need to accumulate all the rows, this means, all the matrix,
			// because if the entire matrix is the highest value > 0, this
			// means that return value is infinity, and it is already
			// detected in detectInfinite call).
			while(up != ((bot+1) % matrix.length));
		}
		return maximumSum;
	}
	
	/**
	 * Detect if the sum of a row or a column of [matrix] is greater
	 * than zero,  so if the matrix is infinite copied, the maximum
	 * value of a sub-matrix would be infinity.
	 * 
	 * @return true if the sum of the values of a row or a column of
	 * 		   [matrix] is greater than zero. False in another case.
	 */
	private static boolean detectInfinite() {
		// Horizontal loops.
		for (short i = 0; i < matrix.length; i++) {
			if (sumHorizontal(i) > 0) return true;
		}
		// Vertical loops.
		for (short i = 0; i < matrix[0].length; i++) {
			if (sumVertical(i) > 0) return true;
		}
		return false;
	}
	
	/**
	 * Precondition: [a] and [b] have same dimensions. Returns an array
	 * equivalent to the addition of both arrays [a] and [b].
	 * 
	 * @param a - First addend.
	 * @param b - Second addend.
	 * @return an array equivalent to the addition
	 * 		   of both arrays [a] and [b].
	 */
	private static short[] plusVectors(short[] a, byte[] b) {
		for (short i = 0; i < a.length; i++) {
			a[i] += b[i];
		}
		return a;
	}
	
	/**
	 * Returns the sum of the values of the row [row] of [matrix].
	 * 
	 * @param row - number of row of the matrix to return its sum.
	 * @return the sum of the values of the row [row] of [matrix].
	 */
	private static int sumHorizontal(short row) {
		int sum = 0;
		for (short i = 0; i < matrix[0].length; i++) {
			sum += matrix[row][i];
		}
		return sum;
	}
	
	/**
	 * Returns the sum of the values of the column [column] of [matrix].
	 * 
	 * @param column - number of column of the matrix to return its sum.
	 * @return the sum of the values of the column [column] of [matrix].
	 */
	private static int sumVertical(short column) {
		int sum = 0;
		for (short i = 0; i < matrix.length; i++) {
			sum += matrix[i][column];
		}
		return sum;
	}
	
	/**
	 * Kadane's algorithm applied to array [a : a].
	 */
	private static short kadane(short[] a) {
		short max_ending_here = 0, max_so_far = 0;
		// Note: *2 to apply the algorithm to [a : a].
		// -1 because no need to get the sum from all the row
		// (if it is max (and this means > 0), detectInfinite
		// will return infinite before this call).
		for (short i = 0; i < a.length * 2 - 1; i++) {
			max_ending_here =
				(short) Math.max(0, max_ending_here + a[i % a.length]);
			max_so_far = (short) Math.max(max_so_far, max_ending_here);
		}
		return max_so_far;
	}
}