import java.util.*;

public class Group {
	String name;
	
	int row;
	int col;
	
	int h;
	int c;
	
	int number;
	
	HashMap<String, Integer> elements = new HashMap<String, Integer>();
	
	//constructor for molecules you get from GUI
	public Group (int numHydrogen, int numCarbons, int number) {
		name = Nomenclature.oPrefixes.get(numCarbons)+"yl";
		elements.put("hydrogen", numHydrogen);
		elements.put("carbon", numCarbons);
		this.number = number;
		
		h = numHydrogen;
		c = numCarbons;
	}
	
	public Group(Atom a) {
		if (a.getName().equals("C")) {
			c++;
		}
		else if (a.getName().equals("H")) {
			h++;
		}
	}
	
	//constructor for bonds
	public Group (String bondType) {
		name = bondType;
	}
	
	//getters
	public String getNums() {
		return h + ", " + c;
	}
	
	public String getName() {
		return Nomenclature.oPrefixes.get(c)+"yl";
	}
	
	//setters	
	public void setName() {
		name = Nomenclature.oPrefixes.get(c)+"yl";
	}
	
	public void addAtom(Atom a) {
		if (a.getName().equals("C")) {
			c++;
		}
		else if (a.getName().equals("H")) {
			h++;
		}
	}
	
	public String toString() {
		return name;
	}
}
