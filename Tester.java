import java.util.*;
public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Nomenclature();
		Group[][] mol = new Group[15][15];
		for (int i = 0; i < 15; i++) {
			for (int k = 0; k < 15; k++) {
				if (i == 0 && (k == 2 || k == 6)) {
					mol[i][k] = new Group(3, 1);
				}
				if (i == 0 && (k == 3 || k == 5)) {
					mol[i][k] = new Group ("single");
				}
				if (i == 0 && k == 4) {
					mol[i][k] = new Group (2,1);
				}
				if (i == 1 && k == 4) {
					mol[i][k] = new Group ("single");
				}
				if (i == 2 && (k == 0 || k == 6)) {
					mol[i][k] = new Group (3, 1);
				}
				if (i == 2 && (k == 1 || k == 3 || k == 5)) {
					mol[i][k] = new Group ("single");
				}
				if (i == 2 && k == 2) {
					mol[i][k] = new Group (2, 1);
				}
				if (i == 2 && k == 4) {
					mol[i][k] = new Group (1, 1);
				}
//				System.out.print(mol[i][k] + " ");
			}
//			System.out.println();
		}
		ArrayList<Integer[]> arr = Molecule.findEdge(mol);
		Molecule.countCarbons(mol, arr, 0, 2, 0, 0);
		System.out.println(Molecule.longest);
//		System.out.println(Molecule.name(mol));
//		for (int i = 0; i < arr.size(); i++) {
//			for (int k = 0; k < 2; k++) {
//				System.out.print(arr.get(i)[k] + " ");
//			}
//			System.out.println();
//		}
	}

}
