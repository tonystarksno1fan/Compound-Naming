public class Bond {
	private int g1;
	private int g2;
	private String type;
	private int bondNum;

	public Bond(String t) {
		type = t;
	}
	
	public Bond(String t, int bn) {
		type = t;
		bondNum = bn;
	}

	public Bond(String t, int g1, int g2) {
		type = t;
		this.g1 = g1;
		this.g2 = g2;
	}
	
	public boolean equals(Bond a) {
		return (a.g1 == g1 && a.g2 == g2 && a.bondNum == bondNum);
	}

	//getters
	public String getType() {
		return type;
	}

	public int[] getGroup() {
		return new int[] {g1,g2};
	}

	public int getG1() {
		return g1;
	}

	public int getG2() {
		return g2;
	}
	
	public int getBN() {
		return bondNum;
	}

	//setters
	public void setType(String t) {
		type = t;
	}

	public void setGroup(int[] arr) {
		g1 = arr[0];
		g2 = arr[1];
	}

	public void setGroup(int i) {
		if (g1 == 0 && g2 == 0) {
			g1 = i;
		}
		else if (g1 == 0) {
			g1 = i;
		}
		else if (g2 == 0) {
			g2 = i;
		}
	}

	public void setG2(int i) {
		g2 = i;
	}
	
	public void setBN(int i) {
		bondNum = i;
	}
}