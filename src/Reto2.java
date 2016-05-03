import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 2 - The Voynich Manuscript.
 * @author Santiago Gil Begué.
 */
public class Reto2 {

	/* List with all data of the "Voynich" manuscript. */
	private static ArrayList<String> voynich;
	
	/**
	 * @param args Program needs one argument: the file
	 * 			   where cases and intervals are specified
	 * 			   with the format specified in the challenge.
	 * 		
	 * @throws FileNotFoundException - File args[0] is not found.
	 * @throws IOException - File can't be read.
	 */
	public static void main(String[] args)
			throws FileNotFoundException, IOException {
		// Reading from "Voynich" manuscript the necessary data.
		// It charges every data in RAM, ++speed.
		Scanner scanVoynich = new Scanner(new File("Voynich.txt"));
		readVoynich(scanVoynich);
		scanVoynich.close();
		// Reading from input file.
		File archivo = new File(args[0]);
		FileReader fr = new FileReader(archivo);
		BufferedReader br = new BufferedReader(fr);
		// Solution for each case.
		int cases = Integer.parseInt(br.readLine());
		Scanner line = null;
		File output = new File("[Ch.02]testOutput.txt");
		output.createNewFile();
		FileWriter fw = new FileWriter(output);
        PrintWriter pw = new PrintWriter(fw);
		for (int i = 1; i <= cases; i++) {
			// A and B interval limits of this case.
			line = new Scanner(br.readLine());
			// Solution for A and B the limits of the interval.
			String mostFreqs = get3Maximum(line.nextInt(), (line.nextInt()));
			// Output is shown in screen.
			pw.println("Case #" + i + ": " + mostFreqs);
		}
		fw.close();
		br.close();
		if (line != null) line.close();
	}
	
	/**
	 * Reads all "Voynich" manuscript into global variable [voynich].
	 * 
	 * @param s Scanner to read "Voynich" manuscript file.
	 */
	private static void readVoynich(Scanner s) {
		voynich = new ArrayList<String>();
		while (s.hasNext()) {
			voynich.add(s.next());
		}
	}
	
	/**
	 * Returns String with the solution of the Challenge 2, getting
	 * 		   the 3 words with most frequency in interval[begin, end].
	 * 
	 * @param begin of the interval
	 * @param end of the interval
	 * @return String with the solution of the Challenge 2, getting
	 * 		   the 3 words with most frequency in interval[begin, end].
	 */
	private static String get3Maximum(int begin, int end) {
		// Data structure to increment string frequency in a constant time.
		HashMap<String, Integer> values = new HashMap<String, Integer>();
		for (int i = begin; i <= end; i++) {
			// Increment value in 1 if exists, or add with 1 appearance
			Integer currentValue = values.get(voynich.get(i-1));
			if (currentValue != null) {
				values.put(voynich.get(i-1), currentValue + 1);
			} else {
				values.put(voynich.get(i-1), 1);
			}
		}
		// Get the 3 maximum frequencies.
		String[] sMostFreqs = new String[3];
		int[] vMostFreqs = new int[3];	
		Object list[] = values.keySet().toArray();
		for (Object s : list) {
			int value = values.get(s);
			// Greater than the third one.
			if (value > vMostFreqs[2]) {
				// Reorganize 3 maximums.
				if (value > vMostFreqs[0]) {
					// Value.
					int aux = value;
					value = vMostFreqs[0];
					vMostFreqs[0] = aux;
					// Word.
					Object aux2 = s;
					s = sMostFreqs[0];	// s saved for next iteration.
					sMostFreqs[0] = (String) aux2;
				}
				if (value > vMostFreqs[1]) {
					// Value.
					int aux = value;
					value = vMostFreqs[1];
					vMostFreqs[1] = aux;
					// Word.
					Object aux2 = s;	// s saved for next assignment.
					s = sMostFreqs[1];
					sMostFreqs[1] = (String) aux2;
				}
				vMostFreqs[2] = value;
				sMostFreqs[2] = (String) s;
			}
		}
		// Specified format.
		return sMostFreqs[0] + " " + vMostFreqs[0] + "," + 
			   sMostFreqs[1] + " " + vMostFreqs[1] + "," + 
			   sMostFreqs[2] + " " + vMostFreqs[2];
	}
}
