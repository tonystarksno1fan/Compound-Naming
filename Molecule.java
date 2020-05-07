import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Molecule extends JPanel {
	private String name;	//Molecule name

	private String type;

	private int objectW;
	private int objectH;

	private int lastX;	//Current X value
	private int lastY;	//Current Y value

	public Molecule[] attached = new Molecule[4];

	public Molecule(String name, int x, int y, int width, int height, String type) {	
		this.name = name;

		this.type = type;

		this.objectW = width;
		this.objectH = height;

		this.lastX = x;
		this.lastY = y;
	}

	public void draw(Graphics g) {	//The object's own draw method (this is what canvas from the physics class calls to draw onto panel)
		Graphics2D gg = (Graphics2D) g;

		if(type.equalsIgnoreCase("molecule")) gg.drawOval(lastX, lastY, objectW, objectH);
		else if(type.equalsIgnoreCase("singleBond")) gg.drawRect(lastX, lastY, objectW, objectH);
	}

	public Molecule objectCollision(int lastX, int lastY) {
		for(int i=0; i<Main.moleculeList.size(); i++) {
			if(Main.moleculeList.get(i) != this) {
				Molecule temp = Main.moleculeList.get(i);
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

		Molecule temp = objectCollision(lastX, lastY);

		if(temp != null) {
			if(temp.getX()<lastX) {
				attached[0] = temp;
				temp.attached[2] = this;
			}
			else if(temp.getY()<lastY) {
				attached[1] = temp;
				temp.attached[3] = this;
			}
			else if(temp.getX()>lastX) {
				attached[2] = temp;
				temp.attached[0] = this;
			}
			else if(temp.getY()>lastY) {
				attached[3] = temp;
				temp.attached[1] = this;
			}
		}

		moveGroup(new ArrayList<Molecule>(Arrays.asList(this)), dx, dy);
	}

	public void moveGroup(ArrayList<Molecule> moved, int dx, int dy) {
		for(int i=0; i<4; i++) {
			if(attached[i] != null && !matchList(attached[i], moved)) {
				attached[i].lastX+=dx;
				attached[i].lastY+=dy;
				moved.add(attached[i]);
				attached[i].moveGroup(moved, dx, dy);
			}
		}
	}
	
	public boolean matchList(Molecule mole, ArrayList<Molecule> list) {
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
}
