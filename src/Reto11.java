import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 11 - Toast.
 * @author Santiago Gil Begué.
 */
public class Reto11 {

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
		int cases = scan.nextInt(); scan.nextLine();
		// Solve all cases.
		for (int i = 1; i <= cases; i++) {
			// N = piles of toast in the table.
			long piles = scan.nextLong();
			// M = slices of toast in each pile.
			long slices = scan.nextLong();
			// K = target of slices to reach on the table.
			long target = scan.nextLong();
			scan.nextLine();
			// Solve the case.
			Integer seconds = getSeconds(piles, slices, target);
			String secs;	// Check if there is no solution.
			if (seconds == null) secs = "IMPOSSIBLE";
			else secs = seconds + "";
			System.out.println("Case #" + i + ": " + secs);
		}
		scan.close();
	}

	/**
	 * Returns the seconds with N = piles, M = slices and K = target,
	 * or null if it is impossible.
	 * 
	 * @param piles of toast in the table.
	 * @param slices of toast in each pile.
	 * @param target of slices to reach on the table.
	 * @return the seconds with N = piles, M = slices and K = target,
	 * or null if it is impossible.
	 */
	private static Integer getSeconds(long piles, long slices, long target) {
		// With 0 piles/slices we can't reach [target] > 0 slices.
		if ((piles == 0 | slices == 0) && target > 0) return null;
		// With piles and slices > 0 we can't reach target 0.
		if (target == 0) return null;
		// Total of slices in table.
		long numSlices = piles * slices;
		// We can just create slices, so we can't reach [target] < numSlices.
		if (numSlices > target) return null;
		// There are already [target] slices -> 0 seconds.
		else if (numSlices == target) return 0;
		// Target - (piles-1)*slices must be multiple of slices. The algorithm
		// will just duplicate one of the pile, and the rest will get intact.
		// Thus we can ignore (piles-1)*slices subtracting its value to [target].
		target -= (piles-1) * slices;
		if (target % slices != 0) return null;
		/* Else, a solution is guaranteed. */
		// We change to basis 2 [target / slices]. This is because slices only
		// can be duplicated (basis 2). [Powers] will have an appearance
		// 1100101. An 1 marks that the tile must be duplicated in another tile
		// (except first 1, that means target as been reached). From a digit
		// to another (from end to begin) means that the tile has been
		// duplicated in the same tile.
		String powers = changeToBasis2(target/slices);
		// Seconds to solve the case.
		int seconds = powers.length() - 1;
		// Increment +1 each time the tile needs to duplicated in another tile.
		// All except the first 1, because there, [target] has been reached.
		for (int i = 1; i < powers.length(); i++) {
			seconds += Integer.parseInt(powers.charAt(i) + "");
		}
		return seconds;
	}

	/**
	 * Changes [number] to basis 2.
	 * @return returns [number] in basis 2.
	 */
	private static String changeToBasis2(long number) {
		// Recursivity.
		if (number == 0) {
			return "";
		} else {
			return changeToBasis2(number/2) + (number % 2);
		}
	}
}
