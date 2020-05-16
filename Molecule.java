import java.util.*;

public class Molecule {
	static Map<Integer, LinkedList<Integer>> molecule = new HashMap<>(); //stores the molecule as a graph -- using numbers for dfs purposes only
	static HashMap<Integer, Group> group = new HashMap<>(); //stores the group that corresponds with its number used in dfs
	
	static boolean[] visited;
	
	static int[] path; 
	static int longest = 0;	//length of longest carbon chain

	//call on this method for final name of molecule
	public static String name(String type) {
		String out = "";
		visited = new boolean[molecule.size() + 1];
		if (type.equals("single")) {
			findLongest(1, 0, new int[molecule.size() + 1], 0);
			longest++;
			path[longest-1] = molecule.get(path[longest-2]).getFirst();
			out += Nomenclature.oPrefixes.get(longest);
			out += "ane";
		}
		else if (type.equals("double")) {

		}
		else if (type.equals("triple")) {

		}
		return out;
	}

	public static void findLongest(int u, int counter, int[] arr, int arrCounter) {
		visited[u] = true;
		if (counter > longest) {
			longest = counter;
			path = Arrays.copyOf(arr, arr.length);
		}
		if (molecule.get(u) == null) {
			// base case u does not have any kids
			System.out.println("base case: " + u);
			longest++;
			path[arrCounter] = u;
			return;
		}
		for (int v : molecule.get(u)) {
			System.out.println("u: " + u + " child: " + v);
			if (!visited[v]) {
				visited[v] = true;
				arr[arrCounter] = u;
				System.out.println("added: " + u);
				//				if (!group.get(v).equals("carbon")) { 		<= deal with this later w/ input groups
				//					findLongest(v, counter);
				//				}
				//				else {
				findLongest(v, counter+1, arr, arrCounter+1);
				//				}
			}
		}
	}
}
