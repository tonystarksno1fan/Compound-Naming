import java.util.*;
public class Molecule {
	/*
	 * only works for completely flat molecules (1D)
	 */
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
			ArrayList<Integer> indicesToCheck = findEdge(arr);
			for (Integer i : indicesToCheck) {
				findSingle(arr, indicesToCheck.get(i), 0);
			}
			int c = countCarbons(arr);
			out += Nomenclature.oPrefixes.get(c);
			out += "ane";
		}
		return out;
	}
	
	private static ArrayList<Integer> findEdge(ArrayList<Atom> arr) {
		ArrayList<Integer> out = new ArrayList<>();
		int min = arr.get(0).getBonds();	//stores minimum number of group bonds
		for (Atom a : arr) {
			if (a.getBonds() < min) {
				min = a.getBonds();
			}
		}
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getBonds() == min) {
				out.add(i);
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
