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
	public int counted = 1;

	public int group = -1;
	
	public int groupNumber;
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
		else if(name.equalsIgnoreCase("single bond")) {										//Draw single bond
			gg.rotate(angle, lastX+objectW/2, lastY+objectH/2);

			gg.drawLine(lastX, lastY+objectH/2, lastX+objectW, lastY+objectH/2);
		}
		else if(name.equalsIgnoreCase("double bond")) {										//Draw double bond
			gg.rotate(angle, lastX+objectW/2, lastY+objectH/2);

			gg.drawLine(lastX, lastY+objectH/3, lastX+objectW, lastY+objectH/3);
			gg.drawLine(lastX+objectW/8, lastY+objectH/3 * 2, lastX+objectW/8 * 9, lastY+objectH/3 * 2);
		}

		if(Main.selected == this) {															//Draw the yellow "selected" halo
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
		
		if(Main.atomList.size() > 0)
			System.out.println(Main.atomList.size() + ", " + Main.atomList.get(0));
		else 
			System.out.println(Main.atomList.size());
	}

	public Atom objectCollision(int lastX, int lastY) {		//Goes through atomList in Main to check for a collision between Atom objects and the given X and Y 
		//if(Main.atomList.size() <= 1) return null;

		if(angle != 0 && angle != Math.PI) {
			lastX = lastX+objectW/2-objectH/2;
			lastY = lastY+objectH/2-objectW/2;
		}

		for(int i=0; i<Main.atomList.size(); i++) {
			if(Main.atomList.get(i) != this) {
				Atom temp = Main.atomList.get(i);
				
				int tempX = temp.lastX;
				int tempY = temp.lastY;
				
				if(temp.angle != 0 && temp.angle != Math.PI) {
					tempX = tempX+objectW/2-objectH/2;
					tempY = tempY+objectH/2-objectW/2;
				}

				if(temp.group < 0 || temp.group != group) {
					if(		(lastY <= tempY+temp.objectH && lastY >= tempY) || 
							(lastY+objectH <= tempY+temp.objectH && lastY+objectH >= tempY) ||
							(lastY >= tempY && lastY+objectH <= tempY+temp.objectH) ||
							(lastY <= tempY && lastX+objectH >= tempY+temp.objectH) ||
							(angle!=0 && angle!=Math.PI && lastY+objectH <= tempY+temp.objectH && lastY+objectW >= tempY)) {

						if(		(lastX>=tempX && lastX<=tempX+temp.objectW) || 
								(lastX+objectW >= tempX && lastX+objectW <= tempX+temp.objectW)) {
							
							/*System.out.println("\n----------------\nUp: " + bondedElements[0]);
							System.out.println("Right: " + bondedElements[1]);
							System.out.println("Down: " + bondedElements[2]);
							System.out.println("Left: " + bondedElements[3] + "\n");
							
							System.out.println("Up: " + temp.bondedElements[0]);
							System.out.println("Right: " + temp.bondedElements[1]);
							System.out.println("Down: " + temp.bondedElements[2]);
							System.out.println("Left: " + temp.bondedElements[3] + "\n");*/
							
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

		if(Main.groupList.size()>0 && group>=0) moveGroup(group, new ArrayList<Atom>(Arrays.asList(this)), dx, dy);
	}

	public void moveGroup(int move, ArrayList<Atom> moved ,int dx, int dy) {	//Move the atoms and bonds of the group this atom is attached to
		for(int i=0; i<Main.groupList.get(move).size(); i++) {
			Atom temp = Main.groupList.get(move).get(i);

			if(temp != this && !matchList(temp, moved)) {
				temp.lastX += dx;
				temp.lastY += dy;
				moved.add(temp);
			}
		}
	}

	public void rotateRight() {
		if(type.equalsIgnoreCase("atom")) return;

		if(angle + Math.PI/4 >= Math.PI*2) angle = 0;
		else angle += Math.PI/4;
	}

	public void rotateLeft() {
		if(type.equalsIgnoreCase("atom")) return;

		if(angle - Math.PI/4 <= Math.PI*-2) angle = 0;
		else angle -= Math.PI/4;
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
