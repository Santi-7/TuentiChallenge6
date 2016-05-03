import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 3 - YATM Microservice.
 * @author Santiago Gil Begué.
 */
public class Reto3 {

	/* Machine state with all information of the input states. */
	private static HashMap<String, State> machine;
	
	/**
	 * @param args Program needs one argument: the file
	 * 			   where state machine and tapes are declared
	 * 			   with the format described in the challenge.
	 * 
	 * @throws IOException - File can't be read.
	 */
	public static void main(String[] args)
			throws IOException {
		// Reading from input file.
		Scanner scan = new Scanner(new File(args[0]));
		scan.nextLine(); scan.nextLine();
		// Machine state.
		machine = new HashMap<String, State>();
		// Make the machine code.
		addStates(scan);
		// Output for each tape.
		String tape = scan.next();
		while (!tape.equals("...")) {
			// Id of tape not necessary to be [1..n] (not specified).
			System.out.println(processTape(scan.next(), machine.get("start"), tape));
			// Prepare to start with next tape.
			scan.nextLine();
			// Next tape.
			tape = scan.next();
		}
		scan.close();		
	}
	
	/**
	 * Creates the state machine managed in Scanner [s].
	 * @param s - scanner where information of state machine is managed.
	 */
	private static void addStates(Scanner s) {
		State state = null;		// Like last action from each state is added in
		State stateAnt = null;	// first action of next state, we need a
		boolean lastRule = false; // second state to avoid this problem.
		// Fields of actions.
		String input = null;
		char write = 0;
		String nextState = null;
		String nextStateAnt = null;
		int direction = 0;
		// It finish when "tapes:" is read, this means,
		// state machine is already defined.
		while (true) {
			String token = s.next();
			switch (token) {
			// Move rule.
			case "move:":
				// 1 cursor goes right, -1 cursor goes left.
				if (s.next().equals("right")) direction = 1;
				else direction = -1;
				break;
			// Write rule.
			case "write:":
				write = s.next().charAt(1);	// To avoid ['] chars
				break;
			// State rule.
			case "state:":
				// State is last rule, so add the action.
				nextState = s.next();
				break;
			// End of machine state definition.
			case "tapes:":
				// Last action.
				state.addAction(input, new Action(write, nextState, direction));
				return;
			// Definition of new action or state.
			default:
				// A new action of current state begins.
				if (token.charAt(0) == '\'') {
					// First iteration will throw NullPointer because there
					// is no  state yet to add its actions.
					try {
						// We control if it is the last rule of the previous
						// state, and adding it to the correct state.
						if (lastRule) {
							stateAnt.addAction(input,
									new Action(write, nextStateAnt, direction));
						} else {
							state.addAction(input,
									new Action(write, nextState, direction));
						}
					} catch (Exception e) { }
					// Reset of values. Input, write and nextState
					// are not necessary, because are directly written.
					direction = 0;
					lastRule = false;
					// New action in state [name] with input [input].
					try {
						input = token.charAt(1) + "";
					// Done this because scan skips spaces.
					} catch (StringIndexOutOfBoundsException e) {
						input = " ";
					}
					// If there is no "state" rule, [input] will written.
					write = input.charAt(0);
				// A definition o a new state begins.
				} else {
					stateAnt = state;		// To control problems explained before.
					nextStateAnt = nextState;
					// New state.
					state = new State();
					// Control if [state] rule is not defined, then it doesn't
					// change state. That's why name of state and next state
					// is shared in same variable.		// -1 to avoid [:] char
					nextState = token.substring(0, token.length()-1);
					machine.put(nextState, state);
					lastRule = true;	// Next rule will be the first one of this state.
				}
			}
			s.nextLine();
		}
	}
	
	/**
	 * Returns the result to input [input] to the tape [tape] according to
	 * the state machine created, which has the [initial] initial state.
	 * @param input to process.
	 * @param initial state of the state machine.
	 * @param tape - name of the tape.
	 * @return the result to input [input] to the tape [tape] according to
	 * the state machine created, which has the [initial] initial state.
	 */
	private static String processTape(String input, State initial, String tape) {
		// Input to execute.
		input = input.substring(1, input.length()-1);	// To avoid ['] chars
		// Current char input and state.
		int i = 0;
		State actual = initial;
		Action a = null;
		// Make code efficient. Explained after.
		int addBefore = 0, addEnd = 0;
		// Process of the tape.
		do {
			String currentChar;
			try {
				currentChar = input.charAt(i) + "";
			// Index has been exceed, so input is " ".
			} catch (IndexOutOfBoundsException e) {
				currentChar = " ";
				// Saturate index and mark addition of new char.
				if (i < 0) {
					i = 0;
					addBefore = -1;	// With this we make code efficient.
				}					// It just means when take first or
				else addEnd = -1;	// last character in substring.
			}
			a = actual.getActions().get(currentChar);
			// Efficient way to manage all cases: addition of a new char in
			// the beginning or at the end, and replacement of a character.
			input = input.substring(0, i) + a.getChar() + 
					input.substring(i+1+addBefore+addEnd);
			// Reset variables for next iteration.
			addBefore = 0;
			addEnd = 0;
			// Move cursor.
			i += a.getDirection();
			// Write associated to action.
			actual = machine.get(a.getNextState());
		// "end" is not in the state machine, so it will return null.
		} while (actual != null);
		return "Tape #" + tape + " " + input;
	}
	
	/**
	 * This class manages a state of the state machine.
	 */
	static private class State {
		
		/* Actions associated to this state. An action is realized
		 * when it receives its corresponding input. */
		private HashMap<String, Action> actions;
		
		/**
		 * State constructor.
		 */
		public State() { actions = new HashMap<String, Action>(); }
		
		/**
		 * Add the action associated to input [input] to the state.
		 * @param input that marks the action.
		 * @param action to realize after receiving [input].
		 */
		public void addAction(String input, Action action) {
			actions.put(input, action);
		}
		
		/**
		 * Returns the actions associated to the state.
		 * @return the actions associated to the state.
		 */
		public HashMap<String, Action> getActions() {
			return actions;
		}
	}
	
	/**
	 * This class manages an action associated to a state
	 * whit same input managed in the state machine. The action
	 * writes a character in the current cursor position, moves
	 * this cursor left, right or keeps it in the current position,
	 * and goes to the next state, if it proceeds.
	 */
	static private class Action {
		
		/* Character to write. */
		private char write;
		/* Next state to visit. */
		private String nextState;
		/* Direction to move the cursor: -1 left, 0 keep, 1 right. */
		private int direction;
		
		/**
		 * Action constructor.
		 * @param write - Character to write.
		 * @param next state to visit.
		 * @param direction to move the cursor.
		 */
		public Action(char write, String next, int direction) {
			this.write = write;
			nextState = next;
			this.direction = direction;
		}
		
		/**
		 * Returns the character to write
		 * @return the character to write..
		 */
		public char getChar() {
			return write;
		}
		
		/**
		 * Returns the next state to visit.
		 * @return the next state to visit.
		 */
		public String getNextState() {
			return nextState;
		}
		
		/**
		 * Returns the direction to move the cursor.
		 * @return the direction to move the cursor.
		 */
		public int getDirection() {
			return direction;
		}
	}
}
