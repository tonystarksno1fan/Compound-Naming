import java.util.*;

public class Molecule {
	/*
	 * only works for completely flat molecules (1D)
	 */
	static private Group[][] molecule = new Group[15][15];
	static private Nomenclature nom = new Nomenclature();

	static int longest = 0;	//length of longest carbon chain

	public static String findType(Group[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int k = 0; k < arr[0].length; k++) {
				if (arr[i][k] != null) {
					if (arr[i][k].toString().equals("triple")) {
						return "triple";
					}
					else if (arr[i][k].toString().equals("double")) {
						return "double";
					}
				}
			}
		}
		return "single";
	}

	//call on this method for final name of molecule
	public static String name(Group[][] arr) {
		String type = findType(arr);
		String out = "";

		if (type.equals("single")) {
			ArrayList<Integer[]> indicesToCheck = findEdge(arr);
			int longestIndex = 0;
			int counter = 0;
			int carbonChain = 0;
			for (Integer[] i : indicesToCheck) {
				//				carbonChain = countCarbons(molecule, i[0], i[1], 0, 0);
				if (carbonChain > longest) {
					longest = carbonChain;
					longestIndex = counter;
				}
				counter++;
			}
			System.out.println(carbonChain);
			out += Nomenclature.oPrefixes.get(carbonChain);
			out += "ane";
		}
		return out;
	}

	//finds branches of molecule
	//	public static String findBranches(Group[][] arr, int row, int col) {
	//		
	//	}

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
	
	public static void countCarbons(Group[][] arr, ArrayList<Integer[]> ends, int row, int col, int counter, int l) {
		if (counter > longest) {		//updates length of longest carbon chain
			longest = counter;			//only accounts for case where there's 1 definite longest chain
		}
		if (row == arr.length || row == -1 || col == arr[0].length || col == -1) {	//return when out of bounds
			return ;
		}
		if (arr[row][col] == null) {		//return when hits an empty index
			return ;
		}
		if (arr[row][col].elements.get("carbon") != null) {	
			System.out.println("row: " + row + " col: " + col + " counter: " + counter);
			counter++;
			arr[row][col] = null;
		}
		for (int i = 0; i < ends.size(); i++) {
			if (i != l && row == ends.get(i)[0] && col == ends.get(i)[1]) {
				return;
			}
		}
		countCarbons(arr, ends, row, col+1, counter, longest);
		countCarbons(arr, ends, row+1, col, counter, longest);
		countCarbons(arr, ends, row, col-1, counter, longest);
		countCarbons(arr, ends, row-1, col, counter, longest);
	}
}
