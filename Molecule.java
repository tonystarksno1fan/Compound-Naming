import java.util.*;
public class Molecule {
	/*
	 * only works for completely flat molecules (1D)
	 */
	static private Group[][] molecule = new Group[15][15];

	static int longest = 0;	//length of longest carbon chain
	public static String findType(ArrayList<Atom> arr) {
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getName().equals("Double Bond")) {
				return "double";
			}
			else if (arr.get(i).getName().equals("Triple Bond")) {
				return "triple";
			}
		}
		return "single";
	}

	//call on this method for final name of molecule
	public static String name(ArrayList<Atom> arr) {
		new Nomenclature();
		String type = findType(arr);
		String out = "";
		if (type.equals("single")) {
			ArrayList<Integer[]> indicesToCheck = findEdge(molecule);
			for (Integer[] i: indicesToCheck) {
				System.out.println(i);
			}
			//			for (Integer i : indicesToCheck) {
			//				findSingle(arr, indicesToCheck.get(i), 0);
			//			}
			int c = countCarbons(arr);
			out += Nomenclature.oPrefixes.get(c);
			out += "ane";
		}
		return out;
	}

	public static ArrayList<Integer[]> findEdge(Group[][] arr) {
		ArrayList<Integer[]> out = new ArrayList<>();
		int bondCount = 0;
		for (int i = 0; i < arr.length; i++) {
			for (int k = 0; k < arr[0].length; k++) {
				if (arr[i][k] != null) {
					try {											//checks index to the left of current group
						if (arr[i][k-1] != null) {
							bondCount++;
						}
					}
					catch (IndexOutOfBoundsException e) {}
					try {											//checks index to the right of current group
						if (arr[i][k+1] != null) {
							bondCount++;
						}
					}
					catch (IndexOutOfBoundsException e) {}
					try {											//checks index above current group
						if (arr[i+1][k] != null) {
							bondCount++;
						}
					}
					catch (IndexOutOfBoundsException e) {}
					try {											//checks index below current group
						if (arr[i-1][k] != null) {
							bondCount++;
						}
					}
					catch (IndexOutOfBoundsException e) {}
					if (bondCount == 1) {							//adds row and column of edge group to arraylist
						System.out.println("row: " + i + "col: " + k);
						out.add(new Integer[]{i, k});
					}
					bondCount = 0;
				}
			}
		}
		return out;
	}

	private static void findSingle(ArrayList<Atom> arr, int index, int counter) {
		if (index == arr.size() || index == -1) {
			return;
		}
		if (arr.get(index).getName().equals("Carbon")) {
			counter++;
		}
		if (counter > longest) {
			longest = counter;
		}
		findSingle(arr, index+1, counter);
		findSingle(arr, index-1, counter);
	}

	private static int countCarbons(ArrayList<Atom> arr) {
		int out = 0;
		for (Atom a : arr) {
			if (a.getName().equals("Carbon")) {
				out++;
			}
		}
		return out;
	}
}
