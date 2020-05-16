import java.util.*;
public class Molecule {
	static Map<Integer, LinkedList<Integer>> molecule = new HashMap<>(); //stores the molecule as a graph -- using numbers for dfs purposes only
	static HashMap<Integer, String> group = new HashMap<>(); //stores the name of the group that corresponds with its number
	static boolean[] visited;
	static int longest = 0;	//length of longest carbon chain

	//call on this method for final name of molecule
	public static String name(String type) {
		String out = "";
		visited = new boolean[molecule.size()];
		findLongest(1, 0);
		if (type.equals("single")) {
			findLongest(1, 0);
			out += Nomenclature.oPrefixes.get(longest);
			out += "ane";
		}
		else if (type.equals("double")) {

		}
		else if (type.equals("triple")) {

		}
		return out;
	}

	public static void findLongest(int u, int counter) {
		visited[u] = true;
		if (counter > longest) {
			longest = counter;
		}
		if (molecule.get(u) == null) {
			// base case u does not have any kids
			return;
		}
		for (int v : molecule.get(u)) {
			try {
				if (!visited[v]) {
					visited[v] = true;
					//				if (!group.get(v).equals("carbon")) {
					//					findLongest(v, counter);
					//				}
					//				else {
					findLongest(v, counter+1);
					//				}
				}
			} catch (IndexOutOfBoundsException e) {}
		}
	}
}
