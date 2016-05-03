import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 5 - Hangman.
 * @author Santiago Gil Begué.
 */
public class Reto5 {

	/* Server direction. */
	final private static String IP = "52.49.91.111";
	final private static short port = 9988;

	/* Size of the alphabet. */
	final private static short TAM_ABC = 'Z' - 'A' + 1;

	/* After these number of errors, input guessing is considered a failure. */
	final private static short ERRORS = 6;
	
	/* Maximum level, this means, maximum length of an input word. */
	final private static short MAXLEVEL = 17;

	/* Current level, this means, length of input word. */
	private static short level = 4;

	/* All hangman's dictionary. */
	private static ArrayList<String> words;

	/* Words of current level, this means, with same length. */
	private static ArrayList<String> levelWords;

	/**
	 * @param args - Program needs one argument: the file which
	 * 			     provides the information about all the words
	 * 				 of the hangman's dictionary.
	 * 
	 * @throws IOException - Could not connect to server.
	 * @throws UnknownHostException - Server is unknown.
	 */
	public static void main(String[] args)
			throws UnknownHostException, IOException {
		// Charges all the words of the hangman's dictionary.
		Scanner file = new Scanner(new File(args[0]));
		words = new ArrayList<String>();
		levelWords = new ArrayList<String>();
		while (file.hasNext()) {
			String word = file.next();
			words.add(word);
			// Charges the words of initial level.
			if (word.length() == level) levelWords.add(word);
		}
		file.close();
		// If we get more than [ERRORS] errors in a level, restart with
		// a new connection to the server.
		while (true) {
			// Connection to server.
			Socket socket = new Socket(IP, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			// Solve solution, or if it gets six errors, begins again.
			while (level < MAXLEVEL) {
				// Enter to continue.
				out.println();
				// Skips image of the "hangman".
				for (int i = 0; i < 11; i++) in.readLine();
				// To indicate which letter contains the word.
				String lettersUsed = "";
				// Letters left to divine.
				short left = level;
				// Progress of errors, up to [ERRORS].
				short currentErrors = 0;
				// Initial word (eg. _ _ _ _)
				String word = in.readLine();
				// Solve and go next level.
				while (currentErrors < ERRORS && left > 0) {
					// Letter with more probability to be part of the solution.
					char best = bestLetter(lettersUsed);
					out.println(best);
					// Skips image of the "hangman".
					in.readLine();
					// Special case: when last of all levels is managed, the
					// challenge does not return final word, but the submit token.
					if (in.readLine().contains("Congratulations, Master!")) break;
					for (int i = 0; i < 8; i++) in.readLine();
					// Word received from server (eg. _ A _ _)
					String nextWord = in.readLine();
					if (nextWord.contains("Congratulations, Master!")) break;
					// Same word, so the letter is not in the solution.
					if (word.equals(nextWord)) {
						currentErrors++;	// An error +.
						filter(best); // This letter is not part of the solution.
					// Letter [best] is in the solution.
					} else {
						lettersUsed += best;
						for (short i = 0; i < word.length(); i+=2) {
							// It is one of the letter solved. There
							// can be more than one.
							if (word.charAt(i) != nextWord.charAt(i)) {
								left--;	// One less.
								attach(i/2, best);
							}
						}
					}
					// Next try.
					word = nextWord;
				}
				// Result has failed: reconnect with server.
				if (currentErrors == ERRORS) {
					socket.close();
					// Begins again with first level.
					level = 4;
					filterLevel();
					break;
				}
				// Result has encountered the solution: go next level.
				in.readLine();
				// Shows submit token and ends. -1 because level++ is after.
				if (level == MAXLEVEL-1) {
					System.out.println(in.readLine());
					socket.close();
					return;
				}
				// Shows test token.
				if (level == 4) {
					in.readLine(); in.readLine();
					System.out.println(in.readLine());
				}
				// Enter to continue.
				out.println();
				// Skips image of the "hangman".
				for (int i = 0; i < 11; i++) in.readLine();
				// Next level.
				level++;
				filterLevel();
			}
		}
	}

	/**
	 * Returns the letter distinct to characters in the string [invalid]
	 * with more probability to be in a word of this hangman dictionary.
	 * 
	 * @param invalid letters to return.
	 * @return the letter distinct to characters in the string [invalid]
	 * with more probability to be in a word of this hangman dictionary.
	 */
	private static char bestLetter(String invalid) {
		// +1 because there is also char "-".
		// Frequencies for each letter in the dictionary. Frequency of
		// "A" is in index 0, frequency of index "B" is in index 1, etc.
		int[] freqs = new int[TAM_ABC + 1];
		// If a letter is more than once in a word, we just have to count
		// plus 1 its frequency, so we need this boolean values to manage it.
		boolean[] alreadyAdded = new boolean[TAM_ABC + 1];
		// For each word in dictionary.
		for (int i = 0; i < levelWords.size(); i++) {
			String word = levelWords.get(i);
			// Add frequencies for each letter of the word.
			for (short j = 0; j < word.length(); j++) {
				// Check if character is "-".
				int index = word.charAt(j) - 'A';
				if (index < 0) index = TAM_ABC;
				// If this character hasn't yet be added and is valid,
				// we add +1 its frequency.
				if (!alreadyAdded[index] &&
						!invalid.contains(word.charAt(j) + "")) {
					freqs[index]++;
					alreadyAdded[index] = true;
				}
			}
			// Reset booleans. Efficiency: only letters of the word
			// are with a true value.
			for (short j = 0; j < word.length(); j++) {
				int index = word.charAt(j) - 'A';
				if (index < 0) index = TAM_ABC;
				alreadyAdded[index] = false;
			}
		}
		// Return character with most frequency.
		if (maxIndex(freqs) == TAM_ABC) return '-';
		else return (char) (maxIndex(freqs) + 'A');
	}

	/**
	 * Returns the index which manages the highest value of [array].
	 *
	 * @param array to return the index with highest value.
	 * @return the index which manages the highest value of [array].
	 */
	private static short maxIndex(int[] array) {
		short maxI = 0;
		// Check each value.
		for (short i = 1; i < array.length; i++) {
			if (array[i] > array[maxI]) maxI = i;
		}
		return maxI;
	}

	/**
	 * Attaches that in letter [index], the solution of
	 * the current hangman level has a letter [letter].
	 * 
	 * @param index where letter is attached.
	 * @param letter to attach.
	 */
	private static void attach(int index, char letter) {
		Iterator<String> it = levelWords.iterator();
		while (it.hasNext()) {
			// Words wichh have not [letter] letter in
			// [index] index are removed.
			if (it.next().charAt(index) != letter) {
				it.remove();
			}
		}
	}

	/**
	 * Filters the words that have the letter [letter],
	 * because after call to hangman with this letter,
	 * we know that solution has not [letter] letter.
	 * 
	 * @param letter to discard.
	 */
	private static void filter(char letter) {
		Iterator<String> it = levelWords.iterator();
		while (it.hasNext()) {
			// All words with [letter] letter are removed.
			if (it.next().contains(letter + "")) {
				it.remove();
			}
		}
	}

	/**
	 * Gets all the words of the current level, this means,
	 * have the same size of the level value.
	 */
	private static void filterLevel() {
		levelWords = new ArrayList<String>();
		for (int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			if (word.length() == level) levelWords.add(word);
		}
	}
}
