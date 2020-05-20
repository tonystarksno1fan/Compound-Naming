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
	
	public Group() {
		
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
	public void addC() {
		c++;
	}
	
	public void addC(int n) {
		c += n;
	}
	
	public void addH() {
		h++;
	}
	
	public void addH(int n) {
		h += n;
	}
	
	public void setName() {
		name = Nomenclature.oPrefixes.get(c)+"yl";
	}
	
	public String toString() {
		return name;
	}
}
