import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main implements ActionListener, KeyListener, MouseListener {
	public static final int height = 700;	//Frame dimensions
	public static final int width = 1000; 	

	public static int mouseX;
	public static int mouseY;

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

	public Main() {
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
		placeboList.add(new Atom("Single Bond", 50, 20, 30, 10, "bond"));
		placeboList.add(new Atom("Double Bond", 120, 20, 30, 10, "bond"));

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

	public void clearAll() {	//Clear all components from panel
		panel.removeAll();
		panel.revalidate();
		panel.repaint();
	}

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {									//A to rotate selected object left, B to rotate right
		if(selected != null) {
			if(e.getKeyCode() == KeyEvent.VK_A) selected.rotateLeft();
			else if(e.getKeyCode() == KeyEvent.VK_D) selected.rotateRight();
		}
	}

	MouseMotionListener motionListener = new MouseAdapter() {
		public void mouseDragged(MouseEvent e) {							//Responsible for updating the location of the dragged object
			mouseX = e.getX();												//Dragged objects are automatically considered "selected"
			mouseY = e.getY();

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
						}
					}
				}
			}
			else {
				if(((JPanel)e.getSource()).getName().equals("controls")) selected.updateLocation(mouseX+width-240, mouseY);
				else 
					selected.updateLocation(mouseX, mouseY);
			}
		}

		public void mouseMoved(MouseEvent e) {}
	};

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {			//Responsible for selecting an object, or deselecting if mouse is pressed in the void of space
		Atom original = selected;

		if(((JPanel)e.getSource()).getName().equals("panel")) {
			for(int i=0; i<atomList.size(); i++) {
				Atom temp = atomList.get(i);
				if(e.getX()>temp.getX() && e.getX()<(temp.getX()+temp.getWidth()) && e.getY()>temp.getY() && e.getY()<(temp.getY()+temp.getHeight())) {
					selected = temp;
				}
			}
			if(selected == original) selected = null;
		}
		else selected = null;
	}

	public void mouseReleased(MouseEvent e) {		//Responsible for attaching objects and groups to each other once they have been "dropped"
		if(selected != null) {
			Atom temp = selected.objectCollision(selected.getX(), selected.getY());

			boolean connect = true;

			if(temp != null) {		
				if(selected.getX()+selected.getWidth()/2 >= temp.getX()+temp.getWidth()/2 &&							//Component is on the right
						Math.abs((selected.getX()+selected.getWidth()/2)-(temp.getX()+temp.getWidth()/2)) >
				Math.abs((selected.getY()+selected.getHeight()/2)-(temp.getY()+temp.getHeight()/2)) && 
				(selected.angle == Math.PI || selected.angle == 0) && temp.bondedElements[1] == null && selected.bondedElements[3] == null) {	

					temp.bondedElements[1] = selected;
					selected.bondedElements[3] = temp;

					selected.updateLocation(temp.getX()+temp.getWidth(), (temp.getY()+temp.getHeight()/2) - (selected.getY()+selected.getHeight()/2) + selected.getY());
				}

				else if(selected.getX()+selected.getWidth()/2 < temp.getX()+temp.getWidth()/2 && 						//Component is on the left
						Math.abs((selected.getX()+selected.getWidth()/2)-(temp.getX()+temp.getWidth()/2)) >
				Math.abs((selected.getY()+selected.getHeight()/2)-(temp.getY()+temp.getHeight()/2)) && 
				(selected.angle == Math.PI || selected.angle == 0) && temp.bondedElements[3] == null && selected.bondedElements[1] == null) {

					temp.bondedElements[3] = selected;
					selected.bondedElements[1] = temp;

					selected.updateLocation(temp.getX()-selected.getWidth(), (temp.getY()+temp.getHeight()/2) - (selected.getY()+selected.getHeight()/2) + selected.getY());
				}

				else if(temp.getType().equalsIgnoreCase("atom") && selected.getType().equalsIgnoreCase("atom")		//Below
						&& selected.getY()+selected.getHeight()/2 >= temp.getY()+temp.getHeight()/2 && temp.bondedElements[2] == null && selected.bondedElements[0] == null) {

					temp.bondedElements[2] = selected;
					selected.bondedElements[0] = temp;

					selected.updateLocation(temp.getX(), temp.getY()+temp.getHeight());
				}

				else if(temp.getType().equalsIgnoreCase("atom") && selected.getType().equalsIgnoreCase("atom")		//Above
						&& selected.getY()+selected.getHeight()/2 < temp.getY()+temp.getHeight()/2 && temp.bondedElements[0] == null && selected.bondedElements[2] == null) {

					temp.bondedElements[0] = selected;
					selected.bondedElements[2] = temp;

					selected.updateLocation(temp.getX(), temp.getY()-selected.getHeight());
				}

				else connect = false;

				if(!selected.getType().equalsIgnoreCase("atom")) {		//Only apply these transformations for bonds
					if(selected.getY() >= temp.getY()+temp.getHeight()/2 && selected.angle != 0 && selected.angle != Math.PI
							&& temp.bondedElements[2] == null && selected.bondedElements[0] == null) {	//Rotated component is below target		

						int dx = (temp.getX()+temp.getWidth()/2-selected.getHeight()/2) - (selected.getX()+selected.getWidth()/2-selected.getHeight()/2);
						int dy = (temp.getY()+temp.getHeight()) - (selected.getY()+selected.getHeight()/2-selected.getWidth()/2);

						temp.bondedElements[2] = selected;
						selected.bondedElements[0] = temp;

						selected.updateLocation(selected.getX()+dx, selected.getY()+dy);
					}
					else if(selected.getY() < temp.getY()+temp.getHeight()/2 && selected.angle != 0 && selected.angle != Math.PI
							&& temp.bondedElements[0] == null && selected.bondedElements[2] == null) {	//Rotated component is above target			

						int dx = (temp.getX()+temp.getWidth()/2-selected.getHeight()/2) - (selected.getX()+selected.getWidth()/2-selected.getHeight()/2);
						int dy = (temp.getY()-selected.getWidth()) - (selected.getY()+selected.getHeight()/2-selected.getWidth()/2);

						temp.bondedElements[0] = selected;
						selected.bondedElements[2] = temp;

						selected.updateLocation(selected.getX()+dx, selected.getY()+dy);
					}
				} 
				else if(selected.getType().equalsIgnoreCase("atom") && !temp.getType().equalsIgnoreCase("atom") && temp.angle != 0 && temp.angle != Math.PI) {
					if(selected.getY()+selected.getHeight()/2 < temp.getY()+temp.getHeight()/2 && temp.bondedElements[0] == null && selected.bondedElements[2] == null) {

						temp.bondedElements[0] = selected;
						selected.bondedElements[2] = temp;

						selected.updateLocation(temp.getX()+temp.getWidth()/2-selected.getWidth()/2, temp.getY()+temp.getHeight()/2-temp.getWidth()/2-selected.getHeight());
					}
					else if(temp.bondedElements[2] == null && selected.bondedElements[0] == null)

						temp.bondedElements[2] = selected;
					selected.bondedElements[0] = temp;

					selected.updateLocation(temp.getX()+temp.getWidth()/2-selected.getWidth()/2, temp.getY()+temp.getHeight()/2+temp.getWidth()/2);
				}

				if(connect) {
					if(temp.group>=0) {
						if(selected.group>=0) {
							if(selected.group > temp.group) {
								int index = selected.group;

								for(int i=0; i<groupList.get(index).size(); i++) {
									groupList.get(temp.group).add(groupList.get(index).get(i));
									groupList.get(index).get(i).group = temp.group;
									groupList.get(index).get(i).node += temp.nodeTotal;
									groupList.get(index).get(i).nodeTotal += temp.nodeTotal;
								}
								for(int j=0; j<groupList.get(temp.group).size(); j++) 
									groupList.get(temp.group).get(j).nodeTotal = selected.nodeTotal;
								
								groupList.remove(index);
							}
							else {
								int index = temp.group;

								for(int i=0; i<groupList.get(index).size(); i++) {
									groupList.get(selected.group).add(groupList.get(index).get(i));
									groupList.get(index).get(i).group = selected.group;
									groupList.get(index).get(i).node += selected.nodeTotal;
									groupList.get(index).get(i).nodeTotal += selected.nodeTotal;
								}
								for(int j=0; j<groupList.get(selected.group).size(); j++) 
									groupList.get(selected.group).get(j).nodeTotal = temp.nodeTotal;
								
								groupList.remove(index);
							}
						}

						else if(selected.group<0) {								
							groupList.get(temp.group).add(selected);
							selected.group = temp.group;

						}
					}
					else if(temp.group<0) {
						if(selected.group>0) {
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

				if(selected.getX() > width-240) atomList.remove(selected);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("compile")) {				
			if(selected == null || selected.group < 0 || selected.getType().equalsIgnoreCase("bond")) {
				JOptionPane.showMessageDialog(null, "Please select an Atom within a group first");
				return;
			}

			export.clear();													//Reset export hashmap

			compile(selected, new ArrayList<Atom>(), new ArrayList<Atom>());

			//System.out.println(export.size() + "\n");

			//System.out.println(map.size());

			export.entrySet().forEach(entry->{													//Prints Group class with the number of hydrogens and carbons
				System.out.println(entry.getKey() + " : " + entry.getValue().getNums());  		//export is the temp HashMap from Tester
			});																					//map is map
			System.out.println();

			map.entrySet().forEach(aa->{														//Prints which nodes are connected to each other
				int num = aa.getKey();
				LinkedList<Integer> temp = aa.getValue();
				for(int m=0; m<temp.size(); m++) {
					System.out.println(num + " : " + temp.get(m));
				}
			});

			new Nomenclature();																	//This is just copied from Tester pretty much

			Molecule mol = new Molecule(map, export, "single");
			System.out.println(mol.name());
		}

		frame.requestFocus();
	}

	public static void compile(Atom atom, ArrayList<Atom> bonds, ArrayList<Atom> checked) {

	}

	public static class Canvas extends JPanel {		//Responsible for drawing onto the main screen (the portion that does not contain the controls)
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	

			g.drawString("Click on a component to select it", (width-240)/2, height/2);
			g.drawString("press A to rotate left and D to rotate right", (width-240)/2, height/2+20);
			g.drawString("Drag and drop time", (width-240)/2, height/2+60);
			g.drawString("Just make sure the components overlap before dropping", (width-240)/2, height/2+80);

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
		new Main();
	}
}
