import java.util.*;
public class Group {
	String name;
	int row;
	int col;
	HashMap<String, Integer> elements = new HashMap<String, Integer>();
	
	//constructor for molecules you get from GUI
	public Group (int numHydrogen, int numCarbons, int x, int y) {
		name = Nomenclature.oPrefixes.get(numCarbons)+"yl";
		elements.put("hydrogen", numHydrogen);
		elements.put("carbon", numCarbons);
		
		/*
		 * insert some grade 9 math to calculate row/column of the group
		 */
	}
	
	//debugging constructor
	public Group(int h, int c) {
		name = Nomenclature.oPrefixes.get(c)+"yl";
		elements.put("hydrogen", h);
		elements.put("carbon", c);
	}
	
	//constructor for bonds
	public Group (String bondType) {
		name = bondType;
	}
	
	public String toString() {
		return name;
	}
}
