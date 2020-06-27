import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GUI implements ActionListener, KeyListener, MouseListener {
	public static final int height = 700;	//Frame dimensions
	public static final int width = 1000; 	

	public static Molecule mol = new Molecule("single");

	public static int mouseX;
	public static int mouseY;

	public static boolean snapRotation = false;
	public static boolean rotation = false;

	public static Atom selected;	//Current Atom object that is "selected" by the user

	public static ArrayList<Atom> atomList = new ArrayList<Atom>();				//Will draw contents onto main panel (see comments at the JSplitPane declaration)
	public static ArrayList<Atom> placeboList = new ArrayList<Atom>();			//Will draw contents onto right side-panel

	public static ArrayList<ArrayList<Atom>> groupList = new ArrayList<ArrayList<Atom>>();		//List of groups (not for the Group class)

	public static HashMap<Integer, Group> export = new HashMap<>();				//HashMap to be exported to Molecule
	public static Map<Integer, LinkedList<Integer>> map = new HashMap<>();

	public static int hydrogenCount = 0;										//To be used for compiling and exporting
	public static int carbonCount = 0;

	public static JFrame frame;
	public static final JSplitPane splitPane = new JSplitPane();	//Used to combine two JPanels side by side in a single JFrame
	public static final JSplitPane splitPaneTop = new JSplitPane();	//Top controls

	private static final JPanel panel = new Canvas();				//Main panel where the molecule stuff happens
	private static final JPanel menu = new CanvasTwo();				//Right side-panel for dragging components from
	private static final JPanel controls = new JPanel();			//Top panel use for controls

	public GUI() {
		frame = new JFrame("Compound Naming");	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setResizable(false);
		frame.addKeyListener(this);
		frame.setFocusable(true);
		frame.requestFocus();

		panel.setLayout(null);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(motionListener);
		panel.setName("panel");

		menu.addMouseListener(this);
		menu.addMouseMotionListener(motionListener);
		menu.setName("controls");

		JButton compile = new JButton("Compile");
		compile.addActionListener(this);
		compile.setActionCommand("compile");
		controls.add(compile);

		JButton detach = new JButton("Detach");
		detach.addActionListener(this);
		detach.setActionCommand("detach");
		controls.add(detach);

		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(width-240);
		//splitPane.setDividerSize(0);
		splitPane.setEnabled(false);
		splitPane.setRightComponent(menu);
		splitPane.setLeftComponent(panel);

		splitPaneTop.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneTop.setDividerLocation(40);
		//splitPaneTop.setDividerSize(0);
		splitPaneTop.setEnabled(false);
		splitPaneTop.setTopComponent(controls);
		splitPaneTop.setBottomComponent(splitPane);

		frame.getContentPane().setLayout(new GridLayout());
		frame.getContentPane().add(splitPaneTop);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		

		placeboList.add(new Atom("C", 20, 20, 20, 20, "atom"));
		placeboList.add(new Atom("H", 90, 20, 20, 20, "atom"));
		placeboList.add(new Atom("single", 50, 20, 30, 10, "bond"));
		placeboList.add(new Atom("double", 120, 20, 30, 10, "bond"));
		placeboList.add(new Atom("triple", 20, 50, 30, 10, "bond"));

		Thread animationThread = new Thread(new Runnable() {	//The main loop
			public void run() {
				while(true) {
					panel.repaint();
					menu.repaint();
					try {Thread.sleep(20);} catch (Exception ex) {}	//20 millisecond delay between each refresh
				}
			}
		});			
		animationThread.start();	//Start the main loop
	}

	public void clearAll() {		//Clear all components from panel
		panel.removeAll();
		panel.revalidate();
		panel.repaint();
	}

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {	
		if(e.getKeyCode() == KeyEvent.VK_SHIFT && selected != null) {										//Hold shift to snap rotations
			snapRotation = true;
		}
	}

	public void keyReleased(KeyEvent e) {									
		if(selected != null) {
			if(e.getKeyCode() == KeyEvent.VK_SHIFT) snapRotation = false;				//Release shit to stop snapping rotations
		}
	}

	MouseMotionListener motionListener = new MouseAdapter() {
		public void mouseDragged(MouseEvent e) {										//Responsible for updating the location of the dragged object
			mouseX = e.getX();															//Dragged objects are automatically considered "selected"
			mouseY = e.getY();

			if(SwingUtilities.isLeftMouseButton(e) && !rotation) {						//Drag with left mouse (move object)

				if(selected == null) {
					if(((JPanel)e.getSource()).getName().equals("panel")) {
						for(int i=0; i<atomList.size(); i++) {
							Atom temp = atomList.get(i);			
							if(mouseX>temp.getXPos() && mouseX<(temp.getXPos()+temp.getWidth()) && mouseY>temp.getYPos() && mouseY<(temp.getYPos()+temp.getHeight())) {
								selected = temp;
							}
						}
					}
					else {
						for(int j=0; j<placeboList.size(); j++) {
							Atom temp = placeboList.get(j);
							if(mouseX>temp.getXPos() && mouseX<(temp.getXPos()+temp.getWidth()) && mouseY>temp.getYPos() && mouseY<(temp.getYPos()+temp.getHeight())) {
								Atom temp2 = new Atom(temp.getName(), (width-240)+temp.getXPos(), temp.getYPos(), temp.getWidth(), temp.getHeight(), temp.getType());
								atomList.add(temp2);
								selected = temp2;
								String type = temp2.getType();
								if (type.equals("atom")) {
									selected.setGroup(mol.groups.size()+1);				//assigns the atom a group
									mol.atoms.add(selected);							//adds it to the arraylist of atoms in molecule class
									mol.groups.add(new Group(selected));				//adding an empty group to the group hashmap
								}
								else if (type.equals("bond")) {
									selected.bondNumber = mol.bonds.size()+1;
									mol.bonds.add(new Bond(temp2.getName(), mol.bonds.size()+1));
								}
								//remember to delete the above if user right clicks or releases out of bounds
							}
						}
					}
				}
				else {
					if(((JPanel)e.getSource()).getName().equals("controls")) selected.updateLocation(mouseX+width-240, mouseY);
					else selected.updateLocation(mouseX, mouseY);
				}
			}
			else if(SwingUtilities.isRightMouseButton(e) && selected != null && selected.getType().equals("bond")) {	//Drag with right mouse (rotate selected)
				int referenceAxis = 0;																			//Axis to use sin with
				double angle = 0;																				//Calculated angle
				rotation = true;

				int mouseX = (int) (e.getX() - (selected.getXPos()+selected.getWidth()/2));						//Set mouse coordinates relative to center of 
				int mouseY = (int) (e.getY() - (selected.getYPos()+selected.getHeight()/2));					//selected as the origin

				if((mouseX > 0 && mouseY > 0) || (mouseX < 0 && mouseY < 0)) referenceAxis = mouseY;			//Determine with axis to use as the "opposite"
				else referenceAxis = mouseX;

				if(mouseX == 0 || mouseY == 0) 																//Use cos when one of the values is 0, use sin otherwise
					angle = Math.toDegrees(Math.acos(referenceAxis / (Math.sqrt((Math.pow(mouseX, 2))+(Math.pow(mouseY, 2))))));
				else 
					angle = Math.toDegrees(Math.asin(referenceAxis / (Math.sqrt((Math.pow(mouseX, 2))+(Math.pow(mouseY, 2))))));	//opposite over hypotenuse

				if(!(mouseX > 0)) angle = -angle;
				
				if(mouseX < 0 && mouseY > 0) angle += 90;
				else if(mouseX < 0 && mouseY < 0) angle += 180;
				else if(mouseX > 0 && mouseY < 0) angle += 270;
								
				if(!snapRotation || (snapRotation && Math.abs((int)(angle)%30) == 0))		//Rotate, if snapRotation is on then rotate only every 30 degrees
					selected.rotate(angle);
			}
		}

		public void mouseMoved(MouseEvent e) {}
	};

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {			//Responsible for selecting an object, or deselecting if mouse is pressed in the void of space
		Atom original = selected;

		if(!rotation) {
			if(((JPanel)e.getSource()).getName().equals("panel")) {
				for(int i=0; i<atomList.size(); i++) {
					Atom temp = atomList.get(i);

					if(e.getX()>temp.getXPos() && e.getX()<(temp.getXPos()+temp.getWidth()) && e.getY()>temp.getYPos() && e.getY()<(temp.getYPos()+temp.getHeight())) 
						selected = temp;
				}
				if(!SwingUtilities.isRightMouseButton(e) && selected == original) selected = null;
			}
			else selected = null;
		}
	}

	public void mouseReleased(MouseEvent e) {		
		if(SwingUtilities.isRightMouseButton(e)) rotation = false;	//Stop rotation mode
		
		if(selected != null) {										//Responsible for attaching objects and groups to each other once they have been "dropped"
			Atom temp = selected.objectCollision();

			boolean connect = true;

			if(temp != null && (!(selected.getType().equals("bond") && temp.getType().equals("bond")))) {					//bonds can't attach to bonds

				if(temp.getType().equals("atom") && selected.getType().equals("atom") &&									//Selected is on the right
						selected.getXPos()+selected.getWidth()/2 >= temp.getXPos()+temp.getWidth()/2 &&							
						Math.abs((selected.getXPos()+selected.getWidth()/2)-(temp.getXPos()+temp.getWidth()/2)) >
						Math.abs((selected.getYPos()+selected.getHeight()/2)-(temp.getYPos()+temp.getHeight()/2))) {

					selected.updateLocation(temp.getXPos()+temp.getWidth(), 
							(temp.getYPos()+temp.getHeight()/2) - (selected.getYPos()+selected.getHeight()/2) + selected.getYPos());
				}

				else if(temp.getType().equals("atom") && selected.getType().equals("atom") &&								//Selected is on the left
						selected.getXPos()+selected.getWidth()/2 < temp.getXPos()+temp.getWidth()/2 && 						
						Math.abs((selected.getXPos()+selected.getWidth()/2)-(temp.getXPos()+temp.getWidth()/2)) >
				Math.abs((selected.getYPos()+selected.getHeight()/2)-(temp.getYPos()+temp.getHeight()/2))) {

					selected.updateLocation(temp.getXPos()-selected.getWidth(), 
							(temp.getYPos()+temp.getHeight()/2) - (selected.getYPos()+selected.getHeight()/2) + selected.getYPos());
				}

				else if(temp.getType().equals("atom") && selected.getType().equals("atom")								//Selected is below
						&& selected.getYPos()+selected.getHeight()/2 >= temp.getYPos()+temp.getHeight()/2) {

					selected.updateLocation(temp.getXPos(), temp.getYPos()+temp.getHeight());
				}

				else if(temp.getType().equals("atom") && selected.getType().equals("atom")								//Selected is above
						&& selected.getYPos()+selected.getHeight()/2 < temp.getYPos()+temp.getHeight()/2) {

					selected.updateLocation(temp.getXPos(), temp.getYPos()-selected.getHeight());
				}
				else if(!(temp.getType().equals("atom") && selected.getType().equals("atom"))) {

					int direction = 1;							//Direction inverter

					boolean left = false;						//Snapped to the left, meaning negative delta x
					boolean up = false;							//Snapped up, meaning negative delta y
					
					double tempAngle = Math.toDegrees(temp.angle);				//Temporary angle used for calculating snap for Atoms to bonds

					if(tempAngle > 180 && tempAngle < 270) tempAngle = Math.toRadians(tempAngle+180);		//Keep angles in sectors 1 and 2
					else if(tempAngle > 90 && tempAngle < 180) tempAngle = Math.toRadians(tempAngle+180);
					else tempAngle = Math.toRadians(tempAngle);
					
					if(selected.getType().equals("bond")) {		//Snap settings for bonds

						if(selected.angle == Math.PI || selected.angle == 0) {			//Check if bond should be snapped right or left relative to Atom
							selected.angle = 0;											//

							if(selected.getXPos()+selected.getWidth()/2 < temp.getXPos()+temp.getWidth()/2)
								left = true;
						}

						else if(selected.angle == Math.PI/2 || selected.angle == Math.PI/2*3) {		//Check if bond should be snapped up or down
							selected.angle = Math.PI/2;

							if(selected.getYPos()+selected.getHeight()/2 < temp.getYPos()+temp.getHeight()/2)
								up = true;
						}

						else if((selected.getYPos()+selected.getHeight()/2 > temp.getYPos()+temp.getHeight()/2 &&	//For quadrants 1 and 3 relative to Atom
								selected.getXPos()+selected.getWidth()/2 < temp.getXPos()+temp.getWidth()/2) ||
								(selected.getYPos()+selected.getHeight()/2 < temp.getYPos()+temp.getHeight()/2 && 
										selected.getXPos()+selected.getWidth()/2 > temp.getXPos()+temp.getWidth()/2)) {

							if((selected.angle < Math.PI && selected.getXPos()+selected.getWidth()/2 > temp.getXPos()+temp.getWidth()/2) ||
									(selected.angle > Math.PI && selected.getXPos()+selected.getWidth()/2 < temp.getXPos()+temp.getWidth()/2)) {

								direction = -1;
							}
						}
						else {																						//For quadrants 2 and 4 relative to Atom
							if((selected.angle > Math.PI && selected.getXPos()+selected.getWidth()/2 > temp.getXPos()+temp.getWidth()/2) ||
									(selected.angle < Math.PI && selected.getXPos()+selected.getWidth()/2 < temp.getXPos()+temp.getWidth()/2)) {

								direction = -1;
							}
						}
					}
					else {																							//Snapping settings for Atoms
						if(temp.angle == Math.PI/2 || temp.angle == Math.PI/2*3) {
							temp.angle = Math.PI/2;

							if(selected.getYPos()+selected.getHeight()/2 < temp.getYPos()+temp.getWidth()/2) 
								direction = -1;
						}
						else if(temp.angle == Math.PI) temp.angle = 0;

						else 
							if(selected.getXPos()+selected.getWidth()/2 < temp.getXPos()+temp.getWidth()/2) 
								direction = -1;
					}

					//Move center of selected to center of temp
					double x = (temp.getXPos()+temp.getWidth()/2) - (selected.getXPos()+selected.getWidth()/2);
					double y = (temp.getYPos()+temp.getHeight()/2) - (selected.getYPos()+selected.getHeight()/2);

					selected.updateLocation(selected.getXPos()+x, selected.getYPos()+y);		

					//Move selected to edge of temp based on trig calculations
					if(selected.getType().equals("bond")) {						
						x = (Math.cos(selected.angle) * temp.getWidth()/2) + (Math.cos(selected.angle) * selected.getWidth()/2);
						y = (Math.sin(selected.angle) * temp.getWidth()/2) + (Math.sin(selected.angle) * selected.getWidth()/2);

						if(left) x = -x;
						else if(up) y = -y;

					}
					else {
						x = (Math.cos(tempAngle) * temp.getWidth()/2) + (Math.cos(tempAngle) * selected.getWidth()/2);
						y = (Math.sin(tempAngle) * temp.getWidth()/2) + (Math.sin(tempAngle) * selected.getWidth()/2);
					}
					
					selected.updateLocation(selected.getXPos()+x*direction, selected.getYPos()+y*direction);
				}
				else connect = false;

				//if selected component collides with another component on the screen 
				if(connect) {
					/*
					 * add my atoms to group here
					 */

					//when 2 atoms collide with each other
					if(selected.getType().equals("atom") && temp.getType().equals("atom")) {
						int g = temp.getGroup();		
						//mol.groups.remove(selected.groupNumber-1);
						//selected.setGroup(g);
						//mol.groups.get(g-1).addAtom(selected);
					}
					//when an atom collides with a bond
					else if (selected.getType().equals("atom") && temp.getType().equals("bond")) {
						if (temp.bondNumber != 0) {
							Bond b = mol.bonds.get(temp.bondNumber-1);
							b.setGroup(selected.groupNumber);
						}
					}
					//when a bond collides with an atom -- edge case is if a bond collides with a bond, but take 
					//care of that later
					else if (selected.getType().equals("bond") && temp.getType().equals("atom")) {
						mol.bonds.get(selected.bondNumber-1).setGroup(temp.groupNumber);
					}

					if(temp.group>=0) {
						if(selected.group>=0 && selected.group>temp.group) {
							int index = selected.group;

							for(int i=0; i<groupList.get(index).size(); i++) {
								groupList.get(temp.group).add(groupList.get(index).get(i));
								groupList.get(index).get(i).group = temp.group;
							}

							groupList.remove(index);
						}

						else if(selected.group<0) {								
							groupList.get(temp.group).add(selected);
							selected.group = temp.group;
						}
					}
					else if(temp.group<0) {
						if(selected.group>=0) {
							temp.group = selected.group;
							groupList.get(selected.group).add(temp);
						}
						else {
							groupList.add(new ArrayList<Atom>(Arrays.asList(selected, temp)));
							selected.group = groupList.size()-1;
							temp.group = groupList.size()-1;
						}
					}
				}
				/*
				 * must remember to remove this dropped atom in the case that it gets attached to a group!
				 */
			}
		}

		Iterator<Atom> iter = atomList.iterator();												//Iterate through atomList to check if any should be removed

		while(iter.hasNext()) {
			Atom dropTest = iter.next();

			if(dropTest.getXPos() > width-240) {	

				/*if(dropTest.getType().equals("atom")) 
					mol.groups.remove(dropTest.groupNumber);

				else if(dropTest.getType().equals("bond")) 
					mol.bonds.remove(dropTest.bondNumber);*/

				if(dropTest.group >= 0)
					groupList.get(dropTest.group).remove(dropTest);

				iter.remove();
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("compile")) {	
			System.out.println("atoms: " + mol.atoms.size() + " bonds: " + mol.bonds.size() + " groups: " 
					+ mol.groups.size());
			mol.assemble();											//fix assemble function
			//			Set<Integer> molecule = mol.molecule.keySet();
			//			for (Integer i : molecule) {
			//				System.out.println(mol.molecule.get(i));
			//			}
			System.out.println(mol.name());
		}
		if(e.getActionCommand().equals("detach")) {
			groupList.get(selected.group).remove(selected);
			selected.group = -1;
		}

		frame.requestFocus();
	}

	public static class Canvas extends JPanel {		//Responsible for drawing onto the main screen (the portion that does not contain the controls)
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	

			if(rotation) g.drawString(Integer.toString((int)(Math.toDegrees(selected.angle))) + "\u00B0", mouseX+10, mouseY);

			//g.drawString("This is a rotation demo", (width-240)/2, height/2);
			g.drawString("Hold down right mouse and drag with a selected bond to rotate it", (width-240)/2, height/2+20);
			g.drawString("Hold down shift while dragging to automatically snap to 30 degrees", (width-240)/2, height/2+40);

			//g.drawString("An effective collision detection and connection snapping system ", (width-240)/2, height/2+80);
			//g.drawString("is currently in the works", (width-240)/2, height/2+100);

			//g.drawString("Added a new \"detach\" button to detach the selected atom from", (width-240)/2, height/2+140);
			//g.drawString("its group so that it can move freely", (width-240)/2, height/2+160);

			for(int i=0; i<atomList.size(); i++)
				atomList.get(i).draw(g);
		}
	}

	public static class CanvasTwo extends JPanel {		//Responsible for drawing onto the portion with the placebo objects (where you draw objects from)
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	

			for(int i=0; i<placeboList.size(); i++)
				placeboList.get(i).draw(g);
		}
	}	

	public static void main(String[] args) {
		new GUI();
	}
}