import java.util.*;

public class Group {
	String name;
	
	int row;
	int col;
	
	HashMap<String, Integer> elements = new HashMap<String, Integer>();
	
	//constructor for molecules you get from GUI
	public Group (int numHydrogen, int numCarbons) {
		name = Nomenclature.oPrefixes.get(numCarbons)+"yl";
		elements.put("hydrogen", numHydrogen);
		elements.put("carbon", numCarbons);
	}
	//constructor for bonds
	public Group (String bondType) {
		name = bondType;
	}
	
	public String toString() {
		return name;
	}
}
