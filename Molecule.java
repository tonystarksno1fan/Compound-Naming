import java.util.*;
public class Molecule {
	static Map<Integer, LinkedList<Integer>> molecule = new HashMap<>(); //stores the molecule as a graph -- using numbers for dfs purposes only
	static HashMap<Integer, Group> group; //stores the group that corresponds with its number used in dfs
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
			out += findBranches();
			out += Nomenclature.oPrefixes.get(longest);
			out += "ane";
		}
		else if (type.equals("double")) {

		}
		else if (type.equals("triple")) {

		}
		return out;
	}

	//finds the branches of the alkyl groups 
	public static String findBranches() {
		boolean contains = false;
		HashMap<Integer, String> map = new HashMap<>();	//tracks where the branch occurs and what the branch is called
		String out = "";
		for (int i = 0; i < path.length; i++) {		//cycles through the indices of the carbons on the longest carbon chain
			if (path[i] == 0) {
				break;
			}
			for (int k : molecule.get(path[i])) {		//checks each index's linkedlist for connect groups
				for (int l : path) {		//checks if those connected groups are actually part of the longest chain; if 
					if (k == l) {			//not, that index + name of branch is added to map
						contains = true;
						break;
					}
				}
				if (!contains) {
					map.put(i+1, group.get(k).name);
				}
				else if (contains) {
					contains = false;
				}
			}
		}
		Set<Integer> set = map.keySet();
		for (int i : set) {
			out += i + "-" + map.get(i) + "-";
		}
		return out.substring(0, out.length()-1);
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
			if (!visited[v]) {
				visited[v] = true;
				arr[arrCounter] = u;
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
