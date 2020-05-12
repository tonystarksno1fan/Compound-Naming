import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main implements ActionListener, KeyListener, MouseListener {
	public static final int height = 700;	
	public static final int width = 1000;

	public static int mouseX;
	public static int mouseY;

	public static Atom selected;

	//Put any and all objects you create into an ArrayList, the Canvas method will draw their contents onto the panel
	public static ArrayList<Atom> atomList = new ArrayList<Atom>();
	public static ArrayList<Atom> placeboList = new ArrayList<Atom>();

	public static ArrayList<ArrayList<Atom>> groupList = new ArrayList<ArrayList<Atom>>();

	public static JFrame frame;
	public static final JSplitPane splitPane = new JSplitPane();
	public static final JPanel panel = new Canvas();
	private static final JPanel controls = new CanvasTwo();

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

		/*JButton atom = new JButton("Atom");
		atom.setActionCommand("addAtom");
		atom.addActionListener(this);
		controls.add(atom);

		JButton singleBond = new JButton("Single Bond");
		singleBond.setActionCommand("addSingleBond");
		singleBond.addActionListener(this);
		controls.add(singleBond);*/

		placeboList.add(new Atom("C", 20, 20, 20, 20, "atom"));
		placeboList.add(new Atom("H", 80, 20, 20, 20, "atom"));
		placeboList.add(new Atom("Single Bond", 50, 20, 20, 10, "singleBond"));

		Thread animationThread = new Thread(new Runnable() {	//The main loop
			public void run() {
				while(true) {
					panel.repaint();
					controls.repaint();
					try {Thread.sleep(10);} catch (Exception ex) {}	//20 millisecond delay between each refresh
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
		if(e.getKeyCode() == KeyEvent.VK_1) atomList.add(new Atom("C", 250, 25, 20, 20, "atom"));
		else if(e.getKeyCode() == KeyEvent.VK_2) atomList.add(new Atom("Single Bond", 250, 25, 20, 10, "singleBond"));
	}

	MouseMotionListener motionListener = new MouseAdapter() {
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
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
	public void mousePressed(MouseEvent e) {
		selected = null;
	}

	public void mouseReleased(MouseEvent e) {
		if(selected != null) {

			Atom temp = selected.objectCollision(selected.getX(), selected.getY());

			if(temp != null) {
				selected.updateLocation(temp.getX()+temp.getWidth(), (temp.getY()+temp.getHeight()/2) - (selected.getY()+selected.getHeight()/2) + selected.getY());

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

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("addAtom")) atomList.add(new Atom("C", 250, 25, 20, 20, "atom"));

		else if(e.getActionCommand().equals("addSingleBond")) atomList.add(new Atom("Single Bond", 250, 25, 20, 10, "singleBond"));

		frame.requestFocus();
	}

	public static class Canvas extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	

			g.drawString("Press 1 to add a circle (atom), 2 to add a rectangle (single bond, for now)", (width-240)/2, height/2);
			g.drawString("Drag and drop time", (width-240)/2, height/2+40);
			g.drawString("Just make sure the components overlap before dropping", (width-240)/2, height/2+60);

			for(int i=0; i<atomList.size(); i++)
				atomList.get(i).draw(g);
		}
	}

	public static class CanvasTwo extends JPanel {
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
