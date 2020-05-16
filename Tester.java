import java.util.*;
import java.io.*;

public class Tester {

	public static void main(String[] args) throws IOException{
		new Nomenclature();
		
		Map<Integer, LinkedList<Integer>> map = new HashMap<>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String dummy = br.readLine();
		
		while (!dummy.equals("")) {
			int x = Integer.parseInt(dummy.substring(0, 1));
			int y = Integer.parseInt(dummy.substring(2));

			if (!map.containsKey(x)) {
				LinkedList<Integer> temp = new LinkedList<>();
				map.put(x, temp);
			}
			map.get(x).add(y); // map.get(x) is a LinkedList. Append y.
			if (!map.containsKey(y)) {
				LinkedList<Integer> temp = new LinkedList<>();
				map.put(y, temp);
			}
			map.get(y).add(x); // map.get(y) is a LinkedList. Append x.
			dummy = br.readLine();
		}

		Molecule.molecule = new HashMap<>(map);
		Molecule.visited = new boolean[map.size() + 1];
//		Molecule.findLongest(1, 0, new int[map.size() + 1], 0);
		
		Molecule.name("single");
		System.out.println(Molecule.longest);
		for (int i : Molecule.path) {
			System.out.print(i + " ");
		}

		/*
		 * used this input to test:
1 2
2 3
2 6
4 5
5 6
6 7
		 */
	}

}
