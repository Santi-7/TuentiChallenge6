import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Solution to TUENTI Challenge 12 - Pika Virus.
 * @author Santiago Gil Begué.
 */
public class Reto12 {

	/**
	 * @param args Program needs one argument: the file which
	 * 			   provides all the viruses to compare.
	 * 
	 * @throws IOException - File can't be read.
	 */
	public static void main(String[] args) throws IOException {
		// Reading from input file.
		Scanner scan = new Scanner(new File(args[0]));
		// N = number of cities infected by the original virus.
		short cities = scan.nextShort(); scan.nextLine();
		// Create the spread-tree of the virus 0.
		City initial = constructSpreadVirus(scan, cities);
		// Then, compare it with the rest of the viruses.
		short viruses = scan.nextShort(); scan.nextLine();
		for (short i = 1; i <= viruses; i++) {
			// Construct the spread-tree of each virus.
			City origin = constructSpreadVirus(scan, cities);
			ArrayList<String> relation = initial.isEquivalent(origin);
			String relations;
			// If relation is null, print NO.
			if (relation == null) relations = "NO";
			// Else, write the list of relations.
			else {
				/* Like insertions have been made linearly, to get the
				 * result ordered is slower. It can be easily changed,
				 * but like it is not the objective of the challenge,
				 * I have decided to get it this way done in order
				 * to can realize more challenges. */
				relations = getPrintOrdered(relation);
			}
			System.out.println("Case #" + i + ": " + relations);
		}
		scan.close();
	}

	/**
	 * Construct the spread-tree of the virus.
	 * @param scan to read information about the virus.
	 * @param cities - number of cities the virus has infected.
	 * @return the initial city origin of the virus.
	 */
	private static City constructSpreadVirus(Scanner scan, short cities) {
		// Create the origin of the virus.
		City initial = new City(scan.next());
		initial.addInfected(initial.getName(), scan.next());
		scan.nextLine();
		// Then, create the spread-tree of the virus.
		for (short i = 1; i < cities - 1; i++) {
			initial.addInfected(scan.next(), scan.next());
			scan.nextLine();
		}
		return initial;
	}

	/**
	 * Returns the elements of [array] ordered in a string.
	 * @param array to get its elements ordered.
	 * @return the elements of [array] ordered in a string.
	 */
	private static String getPrintOrdered(ArrayList<String> array) {
		String ordered = "";
		short size = (short) array.size();
		for (short i = 0; i < size; i++) {
			short min = 0;
			// Get the next String following the order sequence.
			for (short j = 1; j < size - i; j++) {
				if (array.get(j).compareTo(array.get(min)) < 0) {
					min = j;
				}
			}
			ordered += array.remove(min) + " ";
		}
		// Remove last " " space.
		return ordered.substring(0, ordered.length() - 1);
	}

	/**
	 * This class represents a city.
	 */
	private static class City {

		/* Name of the city. */
		private String name;

		/* Cities infected from current city. */
		private ArrayList<City> infected;

		/**
		 * Constructor.
		 * @param name of the city.
		 */
		public City (String name) {
			this.name = name;
			infected = new ArrayList<City>();
		}

		/**
		 * Adds a city with name [city] to the infected cities of
		 * the city [origin]. [origin] can be the current city,
		 * one of the cities infected from it, or none.
		 * 
		 * @param origin from where [city] was infected.
		 * @param city to be added.
		 */
		public void addInfected (String origin, String city) {
			/* Origin is the current city, add to infected list. */
			if (origin.equals(name)) {
				// Add it in order to its name. Could be as said
				// faster with binary search log(size(infected)).
				short i;
				for (i = 0; i < infected.size(); i++) {
					if (infected.get(i).compareTo(city) > 0) break;
				}
				infected.add(i, new City(city));
			}
			/* Else, search origin in the infected list. */
			else {
				for (City child : infected) {
					child.addInfected(origin, city);
				}
			}
		}

		/**
		 * Overrides compareTo with the name of the city.
		 * @param city to compare.
		 */
		private int compareTo(String city) {
			return name.compareTo(city);
		}

		/**
		 * Returns null if the virus originated in the city
		 * is not equivalent to the virus originated in
		 * the city [city], or the list as specified in the
		 * challenge if it is equivalent.
		 * 
		 * @param city origin of the virus to compare.
		 * @return null if the virus originated in the city
		 * is not equivalent to the virus originated in
		 * the city [city], or the list as specified in the
		 * challenge if it is equivalent.
		 */
		private ArrayList<String> isEquivalent(City city) {
			/* Comparisons of the cities are made linearly. They could
			 * be done in log(size(relations)) by a binary search,
			 * but it is not the objective of the challenge. */
			ArrayList<String> relations = null;
			// Infected cities from [city].
			ArrayList<City> cityInfections = city.getInfected();
			// They have same number of infected cities.
			if (infected.size() == cityInfections.size()) {
				relations = new ArrayList<String>();
				for (City child : infected) {
					//System.out.println(child.getName());
					// Mark if a child from city is equivalent
					// to a child from this (value true).
					boolean encounteredEquivalent = false;
					short i = 0;
					/* If a city with the original virus (ai) has more than
					 * one equivalent from the cities with another virus (bj and
					 * bk where bj < bk), it will be related to the first city
					 * according to alphabetical order: (ai/bj):
					 * DONE because ArrayList is ordered.
					 */
					while (i < cityInfections.size() && !encounteredEquivalent) {
						City c = cityInfections.get(i);
						ArrayList<String> relatChild = child.isEquivalent(c);
						// [child] is equivalent to [c].
						if (relatChild != null) {
							encounteredEquivalent = true;
							cityInfections.remove(i);
							relations.addAll(relatChild);
						}
						i++;
					}
					// [child] has not equivalent, so the virus
					// is neither equivalent.
					if (!encounteredEquivalent) {
						return null;
					}
				}
				// Each child has an equivalent, so this is equivalent to [city].
				relations.add(name + "/" + city.getName());
			}
			return relations;
		}

		/**
		 * Returns the name of the city.
		 */
		public String getName() { return name; }

		/**
		 * Returns the infected cities from the city.
		 */
		public ArrayList<City> getInfected() {
			// Return a copy of the list.
			ArrayList<City> infected = new ArrayList<City>();
			infected.addAll(this.infected);
			return infected;
		}

		@Override
		public String toString() {
			String s = "City " + name + " Children: ";
			for (City child : infected) {
				s += child.getName() + " ";
			}
			s += "\n";
			for (City child : infected) {
				s += child.toString();
			}
			return s;
		}
	}
}
