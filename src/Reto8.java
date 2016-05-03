import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Solution to TUENTI Challenge 8 - Labyrinth.
 * @author Santiago Gil Begué.
 */
public class Reto8 {

	/* Server direction. */
	final private static String IP = "52.49.91.111";
	final private static short port = 1986;

	/* Possible movements in the labyrinth. */
	final private static char RIGHT = 'r';
	final private static char UP = 'u';
	final private static char LEFT = 'l';
	final private static char DOWN = 'd';	
	final private static char[] movements = { RIGHT, UP, LEFT, DOWN };

	/* Numeric values associated to the movements. */
	final private static short R = 0;
	final private static short U = 1;
	final private static short L = 2;
	final private static short D = 3;

	/* Representation of the wall of the labyrinth. */
	final private static char WALL = '#';

	/* Current direction. Initially at right. */
	private static int rightWall = R;

	/* Number of steps to move. */
	final private static int MAX = 7500;

	/* Map of the labyrinth. */
	private static char[][] map = new char[83][83];

	/* Current position in labyrinth. Initially in the middle. */
	private static short x = (short) (map.length / 2), y = x;

	/**
	 * @param args - No arguments needed.
	 * 
	 * @throws IOException - Could not connect to server.
	 * @throws UnknownHostException - Server is unknown.
	 */
	public static void main(String[] args)
			throws UnknownHostException, IOException {
		// Connection to server.
		Socket socket = new Socket(IP, port);
		PrintStream out = new PrintStream(socket.getOutputStream());
		BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		// Initializes the map with the first 7x7 tiles known.
		initMap(in);
		// Move [MAX] steps.
		for (short i = 0; i < MAX; i++) {
			// Next move.
			char move = nextMove();
			out.print(move + "\n"); out.flush();
			// Refresh map with new information and our position.
			refreshMap(in, move);
		}
		// Key of the challenge is in the borders of the map.
		printCode();
		// Displays the labyrinth's map gone across.
		//printMap(); Removed to gain time execution.
		socket.close();
	}

	/**
	 * Returns the next movement to realize. The algorithm consists in
	 * going always where the right wall goes (follow it).
	 * 
	 * @return the next movement to realize.
	 */
	private static char nextMove() {
		// 0 Right, 1 Up, 2 Left, 3 Down. Boolean to mark if we can
		// move (value true) in specified direction.
		boolean[] canDoMove = new boolean[movements.length];
		canDoMove[R] = map[y][x+1] != WALL;
		canDoMove[U] = map[y-1][x] != WALL;
		canDoMove[L] = map[y][x-1] != WALL;
		canDoMove[D] = map[y+1][x] != WALL;
		// Decide movement. We want to go to the right wall direction.
		int direction = rightWall;
		while (true) {
			// If can move direction desired, move that way.
			if (canDoMove[direction]) {
				char movement = movements[direction];
				// Update where is the right wall
				// (in the right of our direction move).
				rightWall = (movements.length + direction - 1) % movements.length;
				return movement;
				// Turn counterclockwise, always with the same wall in the right.
			} else direction = (direction + 1) % movements.length;
		}
	}

	/**
	 * Initializes the map with the first 7x7 tiles.
	 * 
	 * @param in - BufferedReader to read from server.
	 * @throws IOException - Could not read from server.
	 */
	private static void initMap(BufferedReader in) throws IOException {
		for (int i = -3; i <= 3; i++) {
			String line = in.readLine();
			for (int j = -3; j <= 3; j++) {
				map[x+i][y+j] = line.charAt(j+3);
			}
		}
	}

	/**
	 * Refresh the map with the new information received due to the
	 * move done, and updates the position in the map.
	 * 
	 * @param in - BufferedReader to read from server.
	 * @param direction where we moved last time.
	 * @throws IOException  Could not read from server.
	 */
	private static void refreshMap(BufferedReader in, char direction)
			throws IOException {
		switch (direction) {
		// Last move was right.
		case RIGHT:
			// Updates position.
			x++;
			// Add right column to map.
			for (short i = -3; i <= 3; i++) {
				map[y+i][x+3] = in.readLine().charAt(6);
			}
			break;
		// Last move was up.
		case UP:
			// Updates position.
			y--;
			// Add top row to map.
			String newLine = in.readLine();
			for (short i = -3; i <= 3; i++) {
				map[y-3][x+i] = newLine.charAt(i+3);
			}
			// Skips already processed information.
			for (short i = 0; i < 6; i++) in.readLine();
			break;
		// Last move was left.
		case LEFT:
			// Updates position.
			x--;
			// Add right column to map.
			for (short i = -3; i <= 3; i++) {
				map[y+i][x-3] = in.readLine().charAt(0);
			}
			break;
		// Last move was down.
		case DOWN:
			// Updates position.
			y++;
			// Skips already processed information.
			for (short i = 0; i < 6; i++) in.readLine();
			// Add bottom row to map.
			newLine = in.readLine();
			for (short i = -3; i <= 3; i++) {
				map[y+3][x+i] = newLine.charAt(i+3);
			}
			break;
		}
	}

	/**
	 * Prints the key of the challenge.
	 */
	private static void printCode() {
		String code = "";
		// Top part of the code.
		for (short i = 5; i < map[0].length - 1; i++) {
			code += map[1][i];
		}
		// Left and right part of the code.
		String left = "", right = "";
		for (short i = 2; i < map.length - 2; i++) {
			right += map[i][map[0].length-2];
			left = map[i][1] + left;
		}
		// Remove last #.
		left = left.substring(0, left.length()-1);
		// Bottom part of the code
		for (short i = (short) (map[0].length - 2); i >= 1; i--) {
			right += map[map.length-2][i];
		}
		// Displays code on screen.
		System.out.println(code + right + left);
	}
	
	/**
	 * Prints the final map.
	 */
	/*private static void printMap() {
		for (short i = 0; i < map.length; i++) {
			for (short j = 0; j < map[0].length; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}*/
}
