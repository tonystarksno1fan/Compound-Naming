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

	public static ArrayList<ArrayList<Atom>> groupList = new ArrayList<ArrayList<Atom>>();
	
	public static HashMap<Integer, Group> groupMap = new HashMap<Integer, Group>();
	public static int groupX;
	public static int groupY;

	public static JFrame frame;
	public static final JSplitPane splitPane = new JSplitPane();	//Used to combine two JPanels side by side in a single JFrame
	public static final JPanel panel = new Canvas();				//Main panel where the molecule stuff happens
	private static final JPanel controls = new CanvasTwo();			//Right side-panel for dragging components from

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

		controls.addMouseListener(this);
		controls.addMouseMotionListener(motionListener);
		controls.setName("controls");

		frame.getContentPane().setLayout(new GridLayout());
		frame.getContentPane().add(splitPane);

		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(width-240);
		//splitPane.setDividerSize(0);
		splitPane.setEnabled(false);
		splitPane.setRightComponent(controls);
		splitPane.setLeftComponent(panel);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		

		placeboList.add(new Atom("C", 20, 20, 20, 20, "atom"));
		placeboList.add(new Atom("H", 90, 20, 20, 20, "atom"));
		placeboList.add(new Atom("Single Bond", 50, 20, 30, 10, "singleBond"));
		placeboList.add(new Atom("Double Bond", 120, 20, 30, 10, "doubleBond"));

		Thread animationThread = new Thread(new Runnable() {	//The main loop
			public void run() {
				while(true) {
					panel.repaint();
					controls.repaint();
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

	public void keyReleased(KeyEvent e) {
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
				else selected.updateLocation(mouseX, mouseY);
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

			if(temp != null) {		
				if(selected.getX()+selected.getWidth()/2 >= temp.getX()+temp.getWidth()/2 &&							//Component is on the right
						Math.abs((selected.getX()+selected.getWidth()/2)-(temp.getX()+temp.getWidth()/2)) >
						Math.abs((selected.getY()+selected.getHeight()/2)-(temp.getY()+temp.getHeight()/2)) && 
						(selected.angle == Math.PI || selected.angle == 0)) 										

					selected.updateLocation(temp.getX()+temp.getWidth(), (temp.getY()+temp.getHeight()/2) - (selected.getY()+selected.getHeight()/2) + selected.getY());

				else if(selected.getX()+selected.getWidth()/2 < temp.getX()+temp.getWidth()/2 && 						//Component is on the left
						Math.abs((selected.getX()+selected.getWidth()/2)-(temp.getX()+temp.getWidth()/2)) >
						Math.abs((selected.getY()+selected.getHeight()/2)-(temp.getY()+temp.getHeight()/2)) && 
						(selected.angle == Math.PI || selected.angle == 0))
					
					selected.updateLocation(temp.getX()-selected.getWidth(), (temp.getY()+temp.getHeight()/2) - (selected.getY()+selected.getHeight()/2) + selected.getY());
				
				else if(temp.getType().equalsIgnoreCase("atom") && selected.getType().equalsIgnoreCase("atom")		//Above
						&& selected.getY()+selected.getHeight()/2 >= temp.getY()+temp.getHeight()/2)
					
					selected.updateLocation(temp.getX(), temp.getY()+temp.getHeight());

				else if(temp.getType().equalsIgnoreCase("atom") && selected.getType().equalsIgnoreCase("atom")		//Below
						&& selected.getY()+selected.getHeight()/2 < temp.getY()+temp.getHeight()/2)
					
					selected.updateLocation(temp.getX(), temp.getY()-selected.getHeight());

				if(!selected.getType().equalsIgnoreCase("atom")) {		//Only apply these transformations for bonds
					if(selected.getY() >= temp.getY()+temp.getHeight()/2 && selected.angle != 0 && selected.angle != Math.PI) {	//Rotated component is below target					
						int dx = (temp.getX()+temp.getWidth()/2-selected.getHeight()/2) - (selected.getX()+selected.getWidth()/2-selected.getHeight()/2);
						int dy = (temp.getY()+temp.getHeight()) - (selected.getY()+selected.getHeight()/2-selected.getWidth()/2);

						selected.updateLocation(selected.getX()+dx, selected.getY()+dy);
					}
					else if(selected.getY() < temp.getY()+temp.getHeight()/2 && selected.angle != 0 && selected.angle != Math.PI) {	//Rotated component is above target					
						int dx = (temp.getX()+temp.getWidth()/2-selected.getHeight()/2) - (selected.getX()+selected.getWidth()/2-selected.getHeight()/2);
						int dy = (temp.getY()-selected.getWidth()) - (selected.getY()+selected.getHeight()/2-selected.getWidth()/2);

						selected.updateLocation(selected.getX()+dx, selected.getY()+dy);
					}
				} 
				else if(selected.getType().equalsIgnoreCase("atom") && !temp.getType().equalsIgnoreCase("atom") && temp.angle != 0 && temp.angle != Math.PI) {
					if(selected.getY()+selected.getHeight()/2 < temp.getY()+temp.getHeight()/2)
						selected.updateLocation(temp.getX()+temp.getWidth()/2-selected.getWidth()/2, temp.getY()+temp.getHeight()/2-temp.getWidth()/2-selected.getHeight());
					else 
						selected.updateLocation(temp.getX()+temp.getWidth()/2-selected.getWidth()/2, temp.getY()+temp.getHeight()/2+temp.getWidth()/2);
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

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {		//A to rotate selected object left, B to rotate right
		if(e.getActionCommand().equals("addAtom")) atomList.add(new Atom("C", 250, 25, 20, 20, "atom"));

		else if(e.getActionCommand().equals("addSingleBond")) atomList.add(new Atom("Single Bond", 250, 25, 20, 10, "singleBond"));

		frame.requestFocus();
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