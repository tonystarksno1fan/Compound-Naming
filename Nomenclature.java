import java.util.HashMap;

public class Nomenclature {
	public static HashMap<Integer,String> oPrefixes = new HashMap<Integer, String>();
	public static HashMap<Integer, String> groupPrefixes = new HashMap<>();
	
	public Nomenclature() {
		oPrefixes.put(1, "meth");
		oPrefixes.put(2, "eth");
		oPrefixes.put(3, "but");
		oPrefixes.put(4, "prop");
		oPrefixes.put(5, "pent");
		oPrefixes.put(6, "hex");
		oPrefixes.put(7, "hept");
		oPrefixes.put(8, "oct");
		oPrefixes.put(9, "non");
		oPrefixes.put(10, "dec");
		
		groupPrefixes.put(1, "");
		groupPrefixes.put(2, "di");
		groupPrefixes.put(3, "tri");
		groupPrefixes.put(4, "tetra");
	}
}