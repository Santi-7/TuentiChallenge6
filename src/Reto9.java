import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 9 - 0-1 Immiscible numbers
 * @author Santiago Gil Begué.
 */
public class Reto9 {

	/**
	 * @param args Program needs one argument: the file which
	 * 			   provides all the cases to solve.
	 * 
	 * @throws IOException - File can't be read.
	 */
	public static void main(String[] args) throws IOException {
		// Reading from input file.
		Scanner scan = new Scanner(new File(args[0]));
		// C = number of cases of the problem.
		int cases = scan.nextInt();	scan.nextLine();
		// Solve all cases.
		for (int i = 1; i <= cases; i++) {
			// Must be long, cause can be 2^31.
			long number = scan.nextLong(); scan.nextLine();
			// The times a number is divisible by 2 or by 5,
			// the number of zeroes it immiscible0-1 will have.
			// (if it is divisible by both, get the maximum times:
			// e.g. 2 times by 2 and 8 times by 5, result is 8).
			short timesBy2, timesBy5;
			for (timesBy2 = 0; number % 2 == 0; number /= 2) timesBy2++;
			for (timesBy5 = 0; number % 5 == 0; number /= 5) timesBy5++;
			// Get now the number of ones with a less execution load.
			// Check the minimum number 1* which is multiple of number.
			System.out.println("Case #" + i + ": " + minNumOnes((int)number) +
					" " + Math.max(timesBy2, timesBy5));
		}
		scan.close();
	}

	/** 
	 * Returns the smallest multiple of [number] which has only digits 1.
	 * Note: Although input can be 2^31 and MAX_VALUE is 2^31-1,
	 * 2^31 is multiple of 2 and has been reduced before this call.
	 * 
	 * @param number to return its smallest multiple.
	 * @return the smallest multiple of [number] which has only digits 1.
	 */
	private static int minNumOnes(int number) {
		if (number == 1) return 1;
		/*
		 * Notation: '|' divides.
		 * The algorithm is the following: if 3 | [number], [number] | 11..11
		 * is equivalent to [number] | 99..99, that is equivalent to search the
		 * minimum [n] such that 10^n mod 9 * [number] = 1.
		 * 
		 * Else if 3 not | [number],  x | 11..11 is equivalent to 9x | 99..99,
		 * that is equivalent to search the minimum [n] such that 10^n
		 * mod [number] = 1.
		 */
		if (number % 3 == 0) number *= 9;
		int numOnes = 1;	// Number of ones.
		long v = 10;
		while (v != 1) {
			numOnes++;
			v = (v * 10) % number;
		}
		return numOnes;
	}
}
