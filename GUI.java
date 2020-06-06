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

	public static int startPos;
	public int referencePos;

	public static boolean snapRotation = false;

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
		frame = new JFrame("Wowowowowow");	
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
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {										//Hold shift to snap rotations
			snapRotation = true;					
			selected.angle = Math.toRadians((int) (30*(Math.round(Math.toDegrees(selected.angle)/30))));
		}
	}

	public void keyReleased(KeyEvent e) {									
		if(selected != null) {
			if(e.getKeyCode() == KeyEvent.VK_A) selected.rotateLeft();					//A to rotate selected object left, B to rotate right
			else if(e.getKeyCode() == KeyEvent.VK_D) selected.rotateRight();

			if(e.getKeyCode() == KeyEvent.VK_SHIFT) snapRotation = false;				//Release shit to stop snapping rotations
		}
	}

	MouseMotionListener motionListener = new MouseAdapter() {
		public void mouseDragged(MouseEvent e) {										//Responsible for updating the location of the dragged object
			mouseX = e.getX();															//Dragged objects are automatically considered "selected"
			mouseY = e.getY();

			if(SwingUtilities.isLeftMouseButton(e)) {

				if(selected == null) {
					if(((JPanel)e.getSource()).getName().equals("panel")) {
						for(int i=0; i<atomList.size(); i++) {
							Atom temp = atomList.get(i);			
							if(mouseX>temp.getX() && mouseX<(temp.getX()+temp.getWidth()) && mouseY>temp.getY() && mouseY<(temp.getY()+temp.getHeight())) {
								selected = temp;
							}
						}
					}
					else {
						for(int j=0; j<placeboList.size(); j++) {
							Atom temp = placeboList.get(j);
							if(mouseX>temp.getX() && mouseX<(temp.getX()+temp.getWidth()) && mouseY>temp.getY() && mouseY<(temp.getY()+temp.getHeight())) {
								Atom temp2 = new Atom(temp.getName(), (width-240)+temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight(), temp.getType());
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
			else if(SwingUtilities.isRightMouseButton(e) && selected != null && selected.getType().equals("bond")) {
				referencePos = e.getX()-startPos;

				if(snapRotation && Math.abs(referencePos)>=24) {
					selected.rotate(30 * (referencePos/Math.abs(referencePos)));
					startPos = e.getX();
				}
				else if(!snapRotation) {
					selected.rotate(referencePos);
					startPos = e.getX();
				}
			}
		}


		public void mouseMoved(MouseEvent e) {}
	};

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {			//Responsible for selecting an object, or deselecting if mouse is pressed in the void of space
		Atom original = selected;

		startPos = e.getX();

		if(((JPanel)e.getSource()).getName().equals("panel")) {
			for(int i=0; i<atomList.size(); i++) {
				Atom temp = atomList.get(i);

				if(e.getX()>temp.getX() && e.getX()<(temp.getX()+temp.getWidth()) && e.getY()>temp.getY() && e.getY()<(temp.getY()+temp.getHeight())) 
					selected = temp;
			}
			if(!SwingUtilities.isRightMouseButton(e) && selected == original) selected = null;
		}
		else selected = null;
	}

	public void mouseReleased(MouseEvent e) {		//Responsible for attaching objects and groups to each other once they have been "dropped"
		if(selected != null) {
			Atom temp = selected.objectCollision(selected.getX(), selected.getY());

			boolean connect = false;

			if(temp != null && ((selected.getType().equals("bond") ^ temp.getType().equals("bond")) || 					//math to snap atoms into place
					(!selected.getType().equals("bond") && !temp.getType().equals("bond")))) {							//bonds can't attach to bonds

				if(selected.getX()+selected.getWidth()/2 >= temp.getX()+temp.getWidth()/2 &&							//Selected is on the right
						Math.abs((selected.getX()+selected.getWidth()/2)-(temp.getX()+temp.getWidth()/2)) >
				Math.abs((selected.getY()+selected.getHeight()/2)-(temp.getY()+temp.getHeight()/2)) 
				&& (selected.angle == Math.PI || selected.angle == 0) && temp.bondedElements[1] == null 
				&& selected.bondedElements[3] == null) {										

					connect = true;

					temp.bondedElements[1] = selected;
					selected.bondedElements[3] = temp;

					selected.updateLocation(temp.getX()+temp.getWidth(), 
							(temp.getY()+temp.getHeight()/2) - (selected.getY()+selected.getHeight()/2) + selected.getY());
				}

				else if(selected.getX()+selected.getWidth()/2 < temp.getX()+temp.getWidth()/2 && 						//Selected is on the left
						Math.abs((selected.getX()+selected.getWidth()/2)-(temp.getX()+temp.getWidth()/2)) >
				Math.abs((selected.getY()+selected.getHeight()/2)-(temp.getY()+temp.getHeight()/2))  
				&& (selected.angle == Math.PI || selected.angle == 0) && temp.bondedElements[3] == null 
				&& selected.bondedElements[1] == null) {

					connect = true;

					temp.bondedElements[3] = selected;
					selected.bondedElements[1] = temp;

					selected.updateLocation(temp.getX()-selected.getWidth(), 
							(temp.getY()+temp.getHeight()/2) - (selected.getY()+selected.getHeight()/2) + selected.getY());
				}

				else if(temp.getType().equals("atom") && selected.getType().equals("atom")								//Selected is below
						&& selected.getY()+selected.getHeight()/2 >= temp.getY()+temp.getHeight()/2
						&& temp.bondedElements[2] == null && selected.bondedElements[0] == null) {

					connect = true;

					temp.bondedElements[2] = selected;
					selected.bondedElements[0] = temp;

					selected.updateLocation(temp.getX(), temp.getY()+temp.getHeight());
				}

				else if(temp.getType().equals("atom") && selected.getType().equals("atom")								//Selected is above
						&& selected.getY()+selected.getHeight()/2 < temp.getY()+temp.getHeight()/2
						&& temp.bondedElements[0] == null && selected.bondedElements[2] == null) {

					connect = true;

					temp.bondedElements[0] = selected;
					selected.bondedElements[2] = temp;

					selected.updateLocation(temp.getX(), temp.getY()-selected.getHeight());
				}

				if(selected.getType().equals("bond")) {													//Only apply these transformations for bonds
					if(selected.getY() >= temp.getY()+temp.getHeight()/2 
							&& selected.angle != 0 && selected.angle != Math.PI
							&& temp.bondedElements[2] == null && selected.bondedElements[0] == null) {	//Rotated component is below target		

						int dx = (temp.getX()+temp.getWidth()/2-selected.getHeight()/2) - (selected.getX()+selected.getWidth()/2-selected.getHeight()/2);
						int dy = (temp.getY()+temp.getHeight()) - (selected.getY()+selected.getHeight()/2-selected.getWidth()/2);

						connect = true;

						temp.bondedElements[2] = selected;
						selected.bondedElements[0] = temp;

						selected.updateLocation(selected.getX()+dx, selected.getY()+dy);
					}
					else if(selected.getY() < temp.getY()+temp.getHeight()/2 && selected.angle != 0 && selected.angle != Math.PI
							&& temp.bondedElements[0] == null && selected.bondedElements[2] == null) {	//Rotated component is above target		

						int dx = (temp.getX()+temp.getWidth()/2-selected.getHeight()/2) - (selected.getX()+selected.getWidth()/2-selected.getHeight()/2);
						int dy = (temp.getY()-selected.getWidth()) - (selected.getY()+selected.getHeight()/2-selected.getWidth()/2);

						connect = true;

						temp.bondedElements[0] = selected;
						selected.bondedElements[2] = temp;

						selected.updateLocation(selected.getX()+dx, selected.getY()+dy);
					}
				} 

				else if(selected.getType().equals("atom") && temp.getType().equals("bond") && temp.angle != 0 && temp.angle != Math.PI) {				
					if(selected.getY()+selected.getHeight()/2 <= temp.getY()+temp.getHeight()/2 && 
							temp.bondedElements[0] == null && selected.bondedElements[2] == null) {									//Atom above bond

						connect = true;

						temp.bondedElements[0] = selected;
						selected.bondedElements[2] = temp;

						selected.updateLocation(temp.getX()+temp.getWidth()/2-selected.getWidth()/2, 
								temp.getY()+temp.getHeight()/2-temp.getWidth()/2-selected.getHeight());
					}
					else if(selected.getY()+selected.getHeight()/2 >= temp.getY()+temp.getHeight()/2 &&
							temp.bondedElements[2] == null && selected.bondedElements[0] == null) {									//Atom below bond

						connect = true;

						temp.bondedElements[2] = selected;
						selected.bondedElements[0] = temp;

						selected.updateLocation(temp.getX()+temp.getWidth()/2-selected.getWidth()/2, temp.getY()+temp.getHeight()/2+temp.getWidth()/2);
					}
				}

				//if selected component collides with another component on the screen 
				if(connect) {
					/*
					 * add my atoms to group here
					 */

					//when 2 atoms collide with each other
					if(selected.getType().equals("atom") && temp.getType().equals("atom")) {
						int g = temp.getGroup();		
						mol.groups.remove(selected.groupNumber-1);
						selected.setGroup(g);
						mol.groups.get(g-1).addAtom(selected);
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

			if(dropTest.getX() > width-240) {	

				System.out.println(dropTest);

				/*if(dropTest.getType().equals("atom")) 
					mol.groups.remove(dropTest.groupNumber);

				else if(dropTest.getType().equals("bond")) 
					mol.bonds.remove(dropTest.bondNumber);*/

				if(dropTest.group >= 0)
					groupList.get(dropTest.group).remove(dropTest);

				if(dropTest.bondedElements[0] != null)
					dropTest.bondedElements[0].bondedElements[2] = null;

				if(dropTest.bondedElements[1] != null)
					dropTest.bondedElements[1].bondedElements[3] = null;

				if(dropTest.bondedElements[2] != null)
					dropTest.bondedElements[2].bondedElements[0] = null;

				if(dropTest.bondedElements[3] != null)
					dropTest.bondedElements[3].bondedElements[1] = null;

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
			try {
				selected.bondedElements[0].bondedElements[2] = null;
				selected.bondedElements[0] = null;

				selected.bondedElements[1].bondedElements[3] = null;
				selected.bondedElements[1] = null;

				selected.bondedElements[2].bondedElements[0] = null;
				selected.bondedElements[2] = null;

				selected.bondedElements[3].bondedElements[1] = null;
				selected.bondedElements[3] = null;
			} catch(NullPointerException e3) {};

			groupList.get(selected.group).remove(selected);
			selected.group = -1;
		}

		frame.requestFocus();
	}

	public static class Canvas extends JPanel {		//Responsible for drawing onto the main screen (the portion that does not contain the controls)
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	

			g.drawString("This is a rotation demo", (width-240)/2, height/2);
			g.drawString("Hold down right mouse and drag with a selected bond to rotate it", (width-240)/2, height/2+20);
			g.drawString("Hold down shift while dragging to automatically snap to 30 degrees", (width-240)/2, height/2+40);
			
			g.drawString("An effective collision detection and connection snapping system ", (width-240)/2, height/2+80);
			g.drawString("is currently in the works", (width-240)/2, height/2+100);
			
			g.drawString("Added a new \"detach\" button to detach the selected atom from", (width-240)/2, height/2+140);
			g.drawString("its group so that it can move freely", (width-240)/2, height/2+160);
			
			g.drawString("Will improve this feature by making it so detaching an atom", (width-240)/2, height/2+200);
			g.drawString("located in the centre of a group slipts the group instead of", (width-240)/2, height/2+220);
			g.drawString("only detaching the one atom", (width-240)/2, height/2+240);

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