import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Atom extends JPanel {
	private String name;		//Atom name

	private String type;		//E.g. atom, singleBond, doubleBond

	private int objectW;	
	private int objectH;

	private int lastX;			//Current X value
	private int lastY;			//Current Y value

	public double angle = 0;	//Current rotation angle. Only applies to non-circular objects like bonds

	private int dx;				//Delta X and Y is how much the coordinates changed from the previous location. Used to move the group this Atom is attached to
	private int dy;

	public Atom[] bondedElements = new Atom[] {null, null, null, null};

	public int group = -1;

	public int groupNumber = 0;
	public int bondNumber = 0;

	public Atom(String name, int x, int y, int width, int height, String type) {
		this.name = name;
		groupNumber = 0;
		this.type = type;

		this.objectW = width;
		this.objectH = height;

		this.lastX = x;
		this.lastY = y;
	}

	public boolean equals(Object o) {
		Atom a = (Atom) o;

		return (this.name.equals(a.name) && this.type.equals(a.type) && this.lastX == a.lastX && this.lastY == a.lastY
				&& this.groupNumber == a.groupNumber);
	}

	public void draw(Graphics g) {	//The object's own draw method (this is what canvas from the main class calls to draw onto panel)		
		Graphics2D gg = (Graphics2D) g.create();

		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		Stroke defaultStroke = gg.getStroke();

		if(type.equalsIgnoreCase("atom")) {													//Draw atoms
			gg.drawOval(lastX, lastY, objectW, objectH);
			g.drawString(name, lastX+objectW/4+1, lastY+objectH-5);
		}
		else if(name.equalsIgnoreCase("single")) {										//Draw single bond
			gg.rotate(angle, lastX+objectW/2, lastY+objectH/2);

			gg.drawLine(lastX, lastY+objectH/2, lastX+objectW, lastY+objectH/2);
		}
		else if(name.equalsIgnoreCase("double")) {										//Draw double bond
			gg.rotate(angle, lastX+objectW/2, lastY+objectH/2);

			gg.drawLine(lastX, lastY+objectH/3, lastX+objectW, lastY+objectH/3);
			gg.drawLine(lastX, lastY+objectH/3 * 2, lastX+objectW, lastY+objectH/3 * 2);
		}
		else if(name.equalsIgnoreCase("triple")) {										//Draw triple bond
			gg.rotate(angle, lastX+objectW/2, lastY+objectH/2);
			
			gg.drawLine(lastX, lastY+objectH/4, lastX+objectW, lastY+objectH/4);
			gg.drawLine(lastX, lastY+objectH/4 * 2, lastX+objectW, lastY+objectH/4 * 2);
			gg.drawLine(lastX, lastY+objectH/4 * 3, lastX+objectW, lastY+objectH/4 * 3);
		}

		if(GUI.selected == this) {															//Draw the yellow "selected" halo
			gg.setColor(Color.yellow);
			gg.setStroke(new BasicStroke(4));

			if(type.equalsIgnoreCase("atom")) gg.drawOval(lastX-2, lastY-2, objectW+4,objectH+4);
			else gg.drawRect(lastX-2, lastY-2, objectW+4,objectH+4);
		}

		gg.setStroke(defaultStroke);
		gg.setColor(Color.black);

		dx = 0;
		dy = 0;

		gg.dispose();
	}

	public Atom objectCollision(int lastX, int lastY) {		//Goes through atomList in Main to check for a collision between Atom objects and the given X and Y 
		//if(GUI.atomList.size() <= 1) return null;
		
		int objectW = this.objectW;
		int objectH = this.objectH;

		if(angle != 0 && angle != Math.PI) {
			lastX = lastX+objectW/2-objectH/2;
			lastY = lastY+objectH/2-objectW/2;
			
			objectW = this.objectH;
			objectH = this.objectW;
		}

		for(int i=0; i<GUI.atomList.size(); i++) {
			if(GUI.atomList.get(i) != this) {
				Atom temp = GUI.atomList.get(i);

				int tempX = temp.lastX;
				int tempY = temp.lastY;
				
				int tempW = temp.objectW;
				int tempH = temp.objectH;

				if(temp.angle != 0 && temp.angle != Math.PI) {
					tempX = tempX+tempW/2-tempH/2;
					tempY = tempY+tempH/2-tempW/2;
					
					tempW = temp.objectH;
					tempH = temp.objectW;
				}

				if(temp.group < 0 || temp.group != group) {
					if(		(lastY+objectH <= tempY+tempH && lastY+objectH >= tempY) ||
							(lastY >= tempY && lastY <= tempY+objectH) ||
							(tempY+tempH <= lastY+objectH && tempY+tempH >= lastY) ||
							(tempY >= lastY && tempY <= lastY+objectH)) {
						
						if(		(!(type.equals("bond") && temp.getType().equals("bond")) && lastX >= tempX && lastX <= tempX+tempW) || 
								(!(type.equals("bond") && temp.getType().equals("bond")) && lastX+objectW >= tempX && lastX+objectW <= tempX+tempW) ||
								(!(type.equals("bond") && temp.getType().equals("bond")) && lastX >= tempX && lastX+objectW <= tempX+tempW) ||
								(!(type.equals("bond") && temp.getType().equals("bond")) && lastX <= tempX && lastX+objectW >= tempX+tempW)) {
							
							return temp;
						}
					}
				}
			}
		}
		return null;
	}

	public void updateLocation(int x, int y) {		//Change the location of this Atom. Also calls moveGroup method
		dx = x - lastX;
		dy = y - lastY;

		lastX = x;
		lastY = y;

		if(GUI.groupList.size()>0 && group>=0) moveGroup(group, new ArrayList<Atom>(Arrays.asList(this)), dx, dy);
	}

	public void moveGroup(int move, ArrayList<Atom> moved ,int dx, int dy) {	//Move the atoms and bonds of the group this atom is attached to
		for(int i=0; i<GUI.groupList.get(move).size(); i++) {
			Atom temp = GUI.groupList.get(move).get(i);

			if(temp != this && !matchList(temp, moved)) {
				temp.lastX += dx;
				temp.lastY += dy;
				moved.add(temp);
			}
		}
	}

	public void rotateRight() {
		if(type.equals("atom")) return;

		if(angle + Math.PI/4 >= Math.PI*2) angle = 0;
		else angle += Math.PI/4;
	}

	public void rotateLeft() {
		if(type.equals("atom")) return;

		if(angle - Math.PI/4 <= Math.PI*-2) angle = 0;
		else angle -= Math.PI/4;
	}
	
	public void rotate(int angle) {
		this.angle += Math.toRadians(angle);
		//this.angle += angle;
	}

	public boolean matchList(Atom atom, ArrayList<Atom> list) {		//Returns true if given ArrayList contains the given Atom object
		for(int i=0; i<list.size(); i++) 
			if(atom == list.get(i)) return true;
		return false;		
	}

	public boolean equals(Atom atom) {
		if(name.equals(atom.name) && angle == atom.angle && lastX == atom.lastX && lastY == atom.lastY) 
			return true;
		return false;
	}

	//getters
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

	public int getGroup() {
		return groupNumber;
	}

	//setters
	public void setGroup(int n) {
		groupNumber = n;
	}

	public String toString() {
		return name;
	}
}
