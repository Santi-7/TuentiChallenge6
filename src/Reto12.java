import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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
		// To write in output file.
		File output = new File("[Ch.12]testOutput.txt");
		output.createNewFile();
		PrintWriter pw = new PrintWriter(output);
		for (short i = 1; i <= viruses; i++) {
			// Construct the spread-tree of each virus.
			City origin = constructSpreadVirus(scan, cities);
			ArrayList<String> relation = areVirusEquivalent(initial, origin);
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
			pw.println("Case #" + i + ": " + relations);
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
	 * Returns null if virus are not equivalent or a list with the relations
	 * of the cities if virus spread the same manner.
	 * @param a - City where first virus began.
	 * @param b - City where second virus began.
	 * @return null if virus are not equivalent or a list with the relations
	 * of the cities if virus spread the same manner.
	 */
	private static ArrayList<String> areVirusEquivalent(City a, City b) {
		ArrayList<String> relations = new ArrayList<String>();
		// For all levels.
		for (int i = a.getHigh(); i >= 0; i--) {
			// Cities of the level [i] of virus a.
			ArrayList<City> citiesA = a.citiesOfLevel(new ArrayList<City>(), i);
			Collections.sort(citiesA);
			// Cities of the level [i] of virus b.
			ArrayList<City> citiesB = b.citiesOfLevel(new ArrayList<City>(), i);
			Collections.sort(citiesB);
			// Different number of cities of same level, virus are not equivalent.
			if (citiesA.size() != citiesB.size()) return null;
			/// Arrays are ordered, so ai will bi related to bj < bk.
			for (City cityA : citiesA) {
				int j;
				for (j = 0; j < citiesB.size(); j++) {
					String relation = cityA.isEquivalent(citiesB.get(j));
					// cityA and cityB are equivalents.
					if (relation != null) {
						citiesB.remove(j--);
						relations.add(relation);
						break;
					}
				}
				// cityA has not an equivalent, so virus are not equivalent.
				if (j > citiesB.size()) return null;
			}
		}
		return relations;
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
	private static class City implements Comparable<Object> {

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
		 * Returns all the cities with [level] jumps from this city.
		 * @param level jumps from this city.
		 * @return all the cities with [level] jumps from this city.
		 */
		public ArrayList<City> citiesOfLevel(ArrayList<City> cities, int level) {
			// Level reached, add the city.
			if (level == 0) {
				cities.add(this);
			// Go deeper.
			} else {
				for (City child : infected) {
					cities.addAll(child.citiesOfLevel(new ArrayList<City>(), level-1));
				}
			}
			return cities;
		}

		/**
		 * Returns null if the virus originated in the city
		 * is not equivalent to the virus originated in
		 * the city [city], or a string that indicates the
		 * cities are equivalent.
		 * 
		 * @param null if the virus originated in the city
		 * is not equivalent to the virus originated in
		 * the city [city], or a string that indicates the
		 * cities are equivalent.
		 */
		public String isEquivalent(City city) {
			// Relation of the city with [city].
			String relation = null;
			// Infected cities from [city].
			ArrayList<City> cityInfections = city.getInfected();
			// They have same number of infected cities.
			if (infected.size() == cityInfections.size()) {
				for (City child : infected) {
					// Mark if a child from city is equivalent
					// to a child from this (value true).
					boolean encounteredEquivalent = false;
					short i = 0;
					while (i < cityInfections.size() && !encounteredEquivalent) {
						City c = cityInfections.get(i);
						// [child] is equivalent to [c].
						if (child.isEquivalent(c) != null) {
							encounteredEquivalent = true;
							cityInfections.remove(i);
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
				relation = name + "/" + city.getName();
			}
			return relation;
		}

		/**
		 * Rerturns the high of the spread-tree.
		 * @return the high of the spread-tree.
		 */
		public int getHigh() {
			return getHighRec(0);
		}

		public int getHighRec(int current) {
			int maxChild = 0;
			for (City child: infected) {
				maxChild = Math.max(maxChild, child.getHighRec(current+1));
			}
			return Math.max(current, maxChild);
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

		/**
		 * Overrides compareTo with the name of the city.
		 * @param city to compare.
		 */
		private int compareTo(String city) {
			return name.compareTo(city);
		}

		/**
		 * Overrides compareTo with the the city.
		 * @param city to compare.
		 */
		@Override
		public int compareTo(Object city) {
			if (city == null) return 1;
			return name.compareTo(((City) city).getName());
		}
	}
}
