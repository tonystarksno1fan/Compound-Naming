import java.util.*;

public class Molecule {
	Map<Integer, LinkedList<Integer>> molecule; //stores the molecule as a graph -- using numbers for dfs purposes only
	HashMap<Integer, Group> group; //stores the group that corresponds with its number used in dfs
	boolean[] visited;
	String bondType = "single";
	int[][] path; //an array of all possible paths
	int longest = 0;	//length of longest carbon chain
	ArrayList<Atom> atoms;
	ArrayList<Group> groups;
	ArrayList<Bond> bonds;

	public Molecule(String type) {
		new Nomenclature();
		molecule = new HashMap<>();
		group = new HashMap<>();
		atoms = new ArrayList<>();
		bonds = new ArrayList<>();
		bondType = type;
		groups = new ArrayList<>();
	}

	//call on this method for final name of molecule
	public String name() {
		String out = "";
		path = new int[molecule.size()][molecule.size()];
		if (bondType.equals("single")) {
			for (int i = 0; i < molecule.size(); i++) {
				visited = new boolean[molecule.size() + 1];
				findPaths(i, i+1, 0, new int[molecule.size()], 0);	//finds every possible carbon chain path
				longest++;
				path[i][longest-1] = molecule.get(path[i][longest-2]).getLast();
				/*
				 * run dfs twice
				 * create an arraylist of the farthest groups
				 */

				longest = 0;
			}
			ArrayList<Integer> paths = longestPath(path);		//narrows it down to only the longest paths
			System.out.println("longest paths: " + paths.size());
			if (paths.size() > 1) {
				int i = lowestNumerals(paths);
				out += findBranches(i);
			}
			else {
				out += findBranches(paths.get(0));
			}
			out += Nomenclature.oPrefixes.get(longest);
			out += "ane";
		}
		else if (bondType.equals("double")) {

		}
		else if (bondType.equals("triple")) {

		}
		return out;
	}

	public int lowestNumerals(ArrayList<Integer> arr) {	//finds the path with the lowest numeral branches
		int[] lowest = new int[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			lowest[i] = leastBranch(arr.get(i));
		}
		int out = 0;
		int counter = lowest[out];
		for (int i = 1; i < arr.size(); i++) {
			if (lowest[i] < counter) {
				out = i;
			}
		}
		return arr.get(out);
	}

	//finds the branches of the alkyl groups 
	public String findBranches(int n) {
		boolean contains = false;
		HashMap<Integer, String> map = new HashMap<>();	//tracks where the branch occurs and what the branch is called
		String out = "";
		for (int i = 0; i < path[n].length; i++) {		//cycles through the indices of the carbons on the longest carbon chain
			if (path[n][i] == 0) {
				break;
			}
			for (int k : molecule.get(path[n][i])) {		//checks each index's linkedlist for connected groups
				for (int l : path[n]) {		//checks if those connected groups are actually part of the longest chain; if 
					if (k == l) {			//not, that index + name of branch is added to map
						contains = true;
						break;
					}
				}
				if (!contains) {	//where i = current alkyl group and k is its branch
					map.put(i+1, getGroupName(i,k)); //only takes into account case where there's 1 alkyl branch
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
		if (out.length() > 0) {
			return out.substring(0, out.length()-1);
		}
		return "";
	}

	public int leastBranch(int p) {		//where int p = row of path
		boolean contains = false;
		ArrayList<Integer> arr = new ArrayList<>();	//tracks indices of branches
		for (int i = 0; i < path[p].length; i++) {		//cycles through the indices of the carbons on the longest carbon chain
			if (path[p][i] == 0) {
				break;
			}
			for (int k : molecule.get(path[p][i])) {		//checks each index's linkedlist for connect groups
				for (int l : path[p]) {		//checks if those connected groups are actually part of the longest chain; if 
					if (k == l) {			//not, that index + name of branch is added to map
						contains = true;
						break;
					}
				}
				if (!contains) {	//where i = current alkyl group and k is its branch
					arr.add(i+1);
				}
				else if (contains) {
					contains = false;
				}
			}
		}
		if (arr.size() >= 1) {
			Collections.sort(arr);
			return arr.get(0);
		}
		return 0;
	}

	//determines the longest path out of all the possible ones. outputs the row(s) of the longest chain
	public ArrayList<Integer> longestPath(int[][] arr) {
		ArrayList<Integer> out = new ArrayList<>();
		int l = 0;
		for (int i = 0; i < arr.length; i++) {	//goes through all rows of the array to determine the path of greatest
			int c = 0;							//length
			for (int k = 0; k < arr[i].length; k++) {
				if (arr[i][k] == 0) {
					break;
				}
				c++;
			}
			if (c > l) {
				l = c;
			}
		}
		longest = l;
		for (int i = 0; i < arr.length; i++) {	//finds the longest paths. takes care of case where there are 2 or more longest paths
			int c = 0;
			for (int k = 0; k < arr[i].length; k++) {
				if (arr[i][k] == 0) {
					break;
				}
				c++;
				if (c == longest) {
					out.add(i);
				}
			}
		}
		return out;
	}

	public String getGroupName(int temp, int cur) {
		int counter = 0;
		int previous = temp;
		int current = cur;

		for (int i : molecule.get(current)) {
			if (i != previous) {
				counter++;
				previous = current;
				current = i;
			}
		}

		return (Nomenclature.oPrefixes.get(counter) + "yl");
	}

	public void assemble() {
		for (int i = 0; i < groups.size(); i++) {	//puts key-value pair into group hashmap with indices of groups as
			group.put(i+1, groups.get(i));			//the key, the group itself as the value
		}

		for (Bond b : bonds) {
			int x = b.getG1();
			int y = b.getG2();
			System.out.println("x: " + x + " y: " + y);
			if (!molecule.containsKey(x)) {
				LinkedList<Integer> temp = new LinkedList<>();
				molecule.put(x, temp);
			}
			molecule.get(x).add(y); // map.get(x) is a LinkedList. Append y.
			if (!molecule.containsKey(y)) {
				LinkedList<Integer> temp = new LinkedList<>();
				molecule.put(y, temp);
			}
			molecule.get(y).add(x); // map.get(y) is a LinkedList. Append x.
		}
	}

	//where s is the start node, counter is the longest chain, arr is path, arrcounter tracks path
	public void findPaths(int i, int s, int counter, int[] arr, int arrCounter) {
		visited[s] = true;
		if (counter > longest) {
			longest = counter;
			path[i] = Arrays.copyOf(arr, arr.length);
		}
		if (molecule.get(s) == null) {
			// base case where u does not have any kids
			return;
		}
		for (int n : molecule.get(s)) {
			if (!visited[n]) {
				visited[n] = true;
				arr[arrCounter] = s;
				//				for (int p : arr) {
				//					System.out.print(p + " ");
				//				}
				//				System.out.println();
				//				if (!group.get(v).equals("carbon")) { 		<= deal with this later w/ input groups
				//					findLongest(v, counter);
				//				}
				//				else {
				findPaths(i, n, counter+1, arr, arrCounter+1);
				//				}
			}
		}
	}
}
