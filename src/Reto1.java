import java.io.*;

/**
 * Solution to TUENTI Challenge 1 - Team Lunch.
 * @author Santiago Gil Begué.
 */
public class Reto1 {

	/**
	 * @param args Program needs one argument: the file
	 * 			   where cases and diners are specified
	 * 			   with the format specified in the challenge.
	 * 		
	 * @throws FileNotFoundException - File args[0] is not found.
	 * @throws IOException - File can't be read.
	 * @throws NumberFormatException - File has not the format specified.
	 */
	public static void main(String[] args)
			throws FileNotFoundException, IOException {
		// Reading from file.
		File archivo = new File(args[0]);
		FileReader fr = new FileReader(archivo);
		BufferedReader br = new BufferedReader(fr);
		br.close();
		// T = number of cases.
		int cases = Integer.parseInt(br.readLine());
		for (int i = 1; i <= cases; i++) {
			// N = numbers of diners.
			int diners = howManyTables(Integer.parseInt(br.readLine()));
			// Output is shown in screen.
			System.out.println("Case #" + i + ": " + diners);
		}
	}
	
	/**
	 * Return minimum number of table in which [diners] 
	 * diners are sat according to Challenge 1 - Team Lunch requisites.
	 * 
	 * @param diners number of diners.
	 * @return minimum number of table in which diners are sat.
	 */
	private static int howManyTables(int diners) {
		// There is no diners, so zero tables are needed.
		if (diners <= 0) return 0;
		// Four diners are sit in just one table.
		else if (diners <= 4) return 1;
		/* More than four diners, two are seated in the edges 
		 * (-2, but +1 to round .5 to next table) and the
		 * rest share one table two by two (/ 2). */
		else return (diners-1) / 2;
	}
}
