import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class Atom extends JPanel {
	private String name;		//Atom name

	private String type;		//E.g. atom, singleBond, doubleBond

	private int objectW;	
	private int objectH;

	private double lastX;			//Current X value
	private double lastY;			//Current Y value

	public double angle = 0;	//Current rotation angle. Only applies to non-circular objects like bonds

	private Area area;

	private double dx;				//Delta X and Y is how much the coordinates changed from the previous location. Used to move the group this Atom is attached to
	private double dy;

	public int group = -1;

	public int groupNumber = 0;
	public int bondNumber = 0;

	public Atom(String name, double x, double y, int width, int height, String type) {
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

		AffineTransform at = new AffineTransform();

		at.translate(lastX, lastY);
		at.rotate(angle, objectW/2, objectH/2);

		GeneralPath path = new GeneralPath();

		if(type.equalsIgnoreCase("atom")) {													//Draw atoms
			Ellipse2D circle = new Ellipse2D.Double(0, 0, objectW, objectH);
			String name = this.name;
			gg.drawString(name, (float) lastX+objectW/4+1, (float) lastY+objectH-5);

			path.append(circle.getPathIterator(at), true);
		}
		else if(name.equalsIgnoreCase("single")) {										//Draw single bond
			Rectangle rect = new Rectangle(0, objectH/2, objectW, 1);

			path.append(rect.getPathIterator(at), true);
		}
		else if(name.equalsIgnoreCase("double")) {										//Draw double bond
			Rectangle rect = new Rectangle(0, objectH/3, objectW, 1);
			Rectangle rect2 = new Rectangle(0, objectH/3*2, objectW, 1);

			path.append(rect.getPathIterator(at), true);
			path.append(rect2.getPathIterator(at), true);
		}
		else if(name.equalsIgnoreCase("triple")) {										//Draw triple bond
			Rectangle rect = new Rectangle(0, objectH/4, objectW, 1);
			Rectangle rect2 = new Rectangle(0, objectH/4*2, objectW, 1);
			Rectangle rect3 = new Rectangle(0, objectH/4*3, objectW, 1);

			path.append(rect.getPathIterator(at), true);
			path.append(rect2.getPathIterator(at), true);
			path.append(rect3.getPathIterator(at), true);
		}

		if(type.equals("atom")) gg.draw(path);
		else gg.fill(path);

		area = new Area(path);

		if(GUI.selected == this) {										//Draw the yellow "selected" halo
			gg.setColor(Color.yellow);
			gg.setStroke(new BasicStroke(4));

			if(type.equalsIgnoreCase("atom")) gg.draw(new Ellipse2D.Double(lastX-2, lastY-2, objectW+4,objectH+4));
			else {
				gg.rotate(angle, lastX+objectW/2, lastY+objectH/2);
				gg.draw(new Rectangle2D.Double(lastX-2, lastY-2, objectW+4,objectH+4));
			}
		}

		dx = 0;
		dy = 0;

		gg.dispose();
	}

	public Atom objectCollision(Atom exception) {			//Goes through atomList in GUI to check for a collision between Atom objects, return if not exception
		for(Atom temp : GUI.atomList) {
			Area tempArea = new Area(temp.area);
			
			tempArea.intersect(area);
			
			if((!tempArea.isEmpty() || (temp.lastX == lastX && temp.lastY == lastY)) && temp!=this && temp!=exception && temp.group!=group) 								
				return temp;
		}
		return null;
	}

	public void updateLocation(double x, double y) {		//Change the location of this Atom. Also calls moveGroup method
		dx = x - lastX;
		dy = y - lastY;

		lastX = x;
		lastY = y;

		if(GUI.groupList.size()>0 && group>=0) moveGroup(group, new ArrayList<Atom>(Arrays.asList(this)), dx, dy);
	}

	public void moveGroup(int move, ArrayList<Atom> moved, double dx, double dy) {	//Move the atoms and bonds of the group this atom is attached to
		for(int i=0; i<GUI.groupList.get(move).size(); i++) {
			Atom temp = GUI.groupList.get(move).get(i);

			if(temp != this && !matchList(temp, moved)) {
				temp.lastX += dx;
				temp.lastY += dy;
				moved.add(temp);
			}
		}
	}

	public void rotate(double angle) {
		if(type.equals("atom")) return;								//Don't rotate atoms

		this.angle = Math.toRadians(angle);
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
	public double getXPos() {
		return lastX;
	}

	public double getYPos() {
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
