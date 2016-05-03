import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 4 - Hadouken!
 * @author Santiago Gil Begué.
 */
public class Reto4 {

	/* Different types of moves that Ryu can do. */
	final private static String R = "R";
	//final private static String RU = "RU";	Unnecessary.
	final private static String RD = "RD";
	final private static String D = "D";
	//final private static String U = "U";		Unnecessary.
	final private static String L = "L";
	//final private static String LU = "LU";	Unnecessary.
	final private static String LD = "LD";
	final private static String K = "K";
	final private static String P = "P";

	/**
	 * @param args Program needs one argument: the file which
	 * 			   provides the information about the different
	 * 			   training sessions that Ryu has performed.
	 * 
	 * @throws IOException - File can't be read.
	 */
	public static void main(String[] args) throws IOException {
		// Reading from input file.
		Scanner scan = new Scanner(new File(args[0]));
		// N = number of training sessions.
		// Int because up to 200.000, and short does not have so much range.
		int sessions = scan.nextInt();	scan.nextLine();
		for (int i = 1; i <= sessions; i++) {
			// Solution to each training session.
			System.out.println("Case #" + i + ": " +
					combosFailed(scan.nextLine().split("-")));
		}
		scan.close();
	}

	/**
	 * Return the number of times that Ryu does not perform a combo because
	 * the last move is missing, being moves managed in [moves].
	 * 
	 * Note: for an optimal solution in time execution, there should be to
	 * create a big state machine in basis to the combos and input moves.
	 * But state machine would be so big, that for simplicity the solution
	 * implemented is an agreement between time execution and memory.
	 * 
	 * @param moves of the training session.
	 * @return the number of times that Ryu does not perform a combo because
	 * the last move is missing, being moves managed in [moves].
	 */
	private static int combosFailed(String[] moves) {
		int fails = 0;	// Fails of a combo due to last move.
		// If process gets out of index, means that session has ended.
		try {
			// -2 because is the limit where a combo can be failed just
			// due to the last move. It must be at least 3 moves of size.
			for (int i = 0; i < moves.length - 2; i++) {
				switch (moves[i]) {
				// It can be first combo.
				case L:
					// If first combo continues, we skip first two moves
					// because if it fails, second case will count as a
					// a combo failed with combo2 (combo2 is suffix of combo1).
					if (moves[i+1].equals(LD)) i++;
					break;
				// It can be second and fourth combo.
				case D:
					// Second combo continues.
					if (moves[i+1].equals(RD)) {
						// RD can be skipped, because any combo
						// begins with this move.
						i++;
						if (moves[i+1].equals(R)) {
							// Last move is not P or training session has ended.
							try {
								if (!moves[i+2].equals(P)) fails++;
							} catch(IndexOutOfBoundsException e) { fails++; }
						}
					}
					// Fourth combo continues.
					else if(moves[i+1].equals(LD)) {
						// LD can be skipped, because any combo
						// begins with this move.
						i++;
						if (moves[i+1].equals(L)) {
							// Last move is not K or training session has ended.
							try {
								if (!moves[i+2].equals(K)) fails++;
							} catch(IndexOutOfBoundsException e) { fails++; }
						}
					}
					break;
				// It can be third and fifth combo.
				case R:
					// If fifth combo continues, we skip first two moves
					// because if it fails, this case will count as a
					// a combo failed with combo4 (combo4 is suffix of combo5).
					if (moves[i+1].equals(RD)) i++;
					// Third combo continues.
					else if (moves[i+1].equals(D) && moves[i+2].equals(RD)) {
						// Last move is not P or training session has ended.
						try {
							if (!moves[i+3].equals(P)) fails++;
						} catch(IndexOutOfBoundsException e) { fails++; }
					}
					break;
				// It does not begin with a combo move, go next move.
				default:
					break;
				}
			}
		} catch(IndexOutOfBoundsException e) { }
		return fails;
	}
}
