/*
	Snappier component connections (next step: rotations + connection orientation)
	JAVA HAS ANTI-ALIASING?????????? These circles now be looking fineeee
		- Used 16% of my GPU tho lmao
		- That should probably be a settings option
		
	Bugs: 
		Attaching a circle to a rectangle does not always work on the first try
*/

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Atom extends JPanel {
	private String name;	//Atom name

	private String type;

	private int objectW;
	private int objectH;

	private int lastX;	//Current X value
	private int lastY;	//Current Y value

	private int dx;
	private int dy;

	public boolean positioning;

	private HashMap<String, Integer> bondedElements = new HashMap<String, Integer>(); //atoms bonded to current atom object

	private int groupBonds = 0;
	public int group = -1;

	public Atom(String name, int x, int y, int width, int height, String type) {	
		this.name = name;

		this.type = type;

		this.objectW = width;
		this.objectH = height;

		this.lastX = x;
		this.lastY = y;
	}

	public void draw(Graphics g) {	//The object's own draw method (this is what canvas from the main class calls to draw onto panel)
		Graphics2D gg = (Graphics2D) g;

		if(Main.selected == this) {
			Atom temp = objectCollision(lastX, lastY);

			if(temp != null && !positioning) {
				updateLocation(temp.lastX+temp.objectW, (temp.lastY+temp.objectH/2) - (lastY+objectH/2) + lastY);
				
				if(temp.group>=0) {
					if(group>=0 && group>temp.group) {
						int index = group;

						for(int i=0; i<Main.groupList.get(index).size(); i++) {
							Main.groupList.get(temp.group).add(Main.groupList.get(index).get(i));
							Main.groupList.get(index).get(i).group = temp.group;
						}

						Main.groupList.remove(index);
					}

					else if(group<0) {								
						Main.groupList.get(temp.group).add(this);
						group = temp.group;

					}
				}
				else if(temp.group<0) {
					if(group>0) {
						temp.group = group;
						Main.groupList.get(group).add(temp);
					}
					else {						
						Main.groupList.add(new ArrayList<Atom>(Arrays.asList(this, temp)));
						group = Main.groupList.size()-1;
						temp.group = Main.groupList.size()-1;
					}
				}
			}
		}

		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		if(type.equalsIgnoreCase("atom")) {
			gg.drawOval(lastX, lastY, objectW, objectH);
			g.drawString(name, lastX+objectW/4+1, lastY+objectH-5);
		}
		else if(type.equalsIgnoreCase("singleBond")) gg.drawRect(lastX, lastY, objectW, objectH);

		dx = 0;
		dy = 0;
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
		dx = x - lastX;
		dy = y - lastY;

		lastX = x;
		lastY = y;

		if(Main.groupList.size()>0 && group>=0) moveGroup(group, new ArrayList<Atom>(Arrays.asList(this)), dx, dy);
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

	public boolean matchList(Atom atom, ArrayList<Atom> list) {
		for(int i=0; i<list.size(); i++) 
			if(atom == list.get(i)) return true;
		return false;		
	}

	public void addElement(String name) {
		if (bondedElements.containsKey(name)) {
			bondedElements.replace(name, bondedElements.get(name)+1);
		}
		else {
			bondedElements.put(name, 1);
		}
	}

	public Integer getElement(String name) {
		return bondedElements.get(name);
	}

	public int getBonds() {
		return groupBonds;
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

	public String getType() {
		return type;
	}

	public String toString() {
		return name;
	}
}
