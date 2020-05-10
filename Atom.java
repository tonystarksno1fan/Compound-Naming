import java.util.*;
import java.awt.*;
import javax.swing.*;
/*
 * new things: 
 * changed the class name to ATOM for lack of confusion in the future
 * added a toString method for debugging
 * added hashmap that contains other atoms bonded to current atom + appropriate methods
 */
public class Atom extends JPanel {
	private String name;	//Atom name
	
	//stores atoms bonded to current atom object
	private HashMap<String, Integer> bondedElements = new HashMap<String, Integer>();

	private String type;

	private int objectW;
	private int objectH;

	private int lastX;	//Current X value
	private int lastY;	//Current Y value

	public int group = -1;

	public Atom(String name, int x, int y, int width, int height, String type) {	
		this.name = name;

		this.type = type;

		this.objectW = width;
		this.objectH = height;

		this.lastX = x;
		this.lastY = y;
	}

	public void draw(Graphics g) {	//The object's own draw method (this is what canvas from the physics class calls to draw onto panel)
		Graphics2D gg = (Graphics2D) g;

		if(type.equalsIgnoreCase("Atom")) gg.drawOval(lastX, lastY, objectW, objectH);
		else if(type.equalsIgnoreCase("singleBond")) gg.drawRect(lastX, lastY, objectW, objectH);
	}

	public Atom objectCollision(int lastX, int lastY) {
		for(int i=0; i<Main.atomList.size(); i++) {
			if(Main.atomList.get(i) != this) {
				Atom temp = Main.atomList.get(i);
				if((lastY <= temp.lastY + temp.objectH && lastY >= temp.lastY)||(lastY + objectH <= temp.lastY + temp.objectH && lastY+ objectH >= temp.lastY))
					if((lastX >= temp.lastX && lastX <= temp.lastX + temp.objectW) ||(lastX + objectW >= temp.lastX && lastX + objectW <= temp.lastX + temp.objectW)) {
						return temp;
					}
			}
		}
		return null;
	}

	public void updateLocation(int x, int y) {

		int dx = x - lastX;
		int dy = y - lastY;

		lastX = x;
		lastY = y;

		Atom temp = objectCollision(lastX, lastY);

		if(temp != null) {
			if(temp.group>=0) {
				Main.groupList.get(temp.group).add(this);
				group = temp.group;
			}
			else if(temp.group<0) {
				if(Main.groupList.size()>0) {
					group = Main.groupList.size()-1;
					temp.group = Main.groupList.size()-1;
				}
				else {
					group = 0;
					temp.group = 0;
				}

				Main.groupList.add(new ArrayList<Atom>());

				Main.groupList.get(group).add(this);
				Main.groupList.get(group).add(temp);
			}
		}
		if(Main.groupList.size()>0 && group>=0) 
			moveGroup(group, new ArrayList<Atom>(Arrays.asList(this)), dx, dy);
	}

	public void moveGroup(int move, ArrayList<Atom> moved ,int dx, int dy) {
		for(int i=0; i<Main.groupList.get(move).size(); i++) {
			Atom temp = Main.groupList.get(move).get(i);
			if(temp != this && !matchList(temp, moved)) {
				temp.lastX += dx;
				temp.lastY += dy;
				moved.add(temp);
			}
		}
	}

	public boolean matchList(Atom mole, ArrayList<Atom> list) {
		for(int i=0; i<list.size(); i++) 
			if(mole == list.get(i)) return true;
		return false;		
	}

	public int getX() {
		return lastX;
	}

	public int getY() {
		return lastY;
	}

	public int getWidth() {
		return objectW;
	}

	public int getHeight() {
		return objectH;
	}

	public String getName() {
		return name;
	}
	
	public Integer getElement(String name) {
		return bondedElements.get(name);
	}
	
	public void addElement(String name) {
		if (bondedElements.containsKey(name)) {
			bondedElements.replace(name, bondedElements.get(name)+1);
		}
		else {
			bondedElements.put(name, 1);
		}
	}
	
	public String toString() {
		return name;
	}
}
