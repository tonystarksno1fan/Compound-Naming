import java.util.*;
import java.io.*;

public class Tester {

	public static void main(String[] args) throws IOException{
		new Nomenclature();
		
		Map<Integer, LinkedList<Integer>> map = new HashMap<>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String dummy = br.readLine();
		
		while (!dummy.equals("")) {
			int x = Integer.parseInt(dummy.substring(0, dummy.indexOf(" ")));
			int y = Integer.parseInt(dummy.substring(dummy.indexOf(" ") + 1));

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
		
		HashMap<Integer, Group> temp = new HashMap<>();
//		temp.put(1, new Group(3,1));
//		temp.put(2, new Group(2,1));
//		temp.put(3, new Group(3,1));
//		temp.put(4, new Group(3,1));
//		temp.put(5, new Group(2,1));
//		temp.put(6, new Group(1,1));
//		temp.put(7, new Group(2,1));
//		temp.put(8, new Group(2,1));
//		temp.put(9, new Group(2,1));
//		temp.put(10, new Group(2,1));
//		temp.put(11, new Group(3,1));
		
		Molecule mol = new Molecule(map, temp, "single");
		System.out.println(mol.name());
		
//		Molecule.group = new HashMap<>(temp);

//		Molecule.molecule = new HashMap<>(map);
//		Molecule.visited = new boolean[map.size() + 1];
//		Molecule.path = new int[map.size()][map.size()];
////		Molecule.findLongest(1, 0, new int[map.size() + 1], 0);
//		
//		String name = Molecule.name("single");
//		for (int i : Molecule.path) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		System.out.println(name);
		
//		System.out.println(Molecule.longest);
//		for (int i : Molecule.path) {
//			System.out.print(i + " ");
//		}
		/*
		 * used this input to test:
1 2
2 3
2 6
4 5
5 6
6 7

second test set
1 2
2 3
2 6
4 5
5 6
6 7
7 8
8 9
9 10
10 11
		 */
	}

}
