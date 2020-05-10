import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
/*
 * added individual buttons for carbon and hydrogen
 */
public class Main implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
	public static final int height = 600;	
	public static final int width = 900;
	
	public static int mouseX; 
	public static int mouseY;
	
	public Atom selected;
		
	//Put any and all objects you create into an ArrayList, the Canvas method will draw their contents onto the panel
	public static ArrayList<Atom> atomList = new ArrayList<Atom>();
	
	public static ArrayList<ArrayList<Atom>> groupList = new ArrayList<ArrayList<Atom>>();
		
	public static JFrame frame;
	public static final JSplitPane splitPane = new JSplitPane();
	public static final JPanel panel = new Canvas();
	private static final JPanel controls = new JPanel();
		
	public Main() {
		frame = new JFrame("Wowowowowow");	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(width, height));
		//frame.setResizable(false);
		frame.addKeyListener(this);
		frame.setFocusable(true);
		frame.requestFocus();
		
		panel.setLayout(null);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
				
		frame.getContentPane().setLayout(new GridLayout());
		frame.getContentPane().add(splitPane);
		
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(40);
		splitPane.setDividerSize(0);
		splitPane.setTopComponent(controls);
		splitPane.setBottomComponent(panel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		
		
		//start of new stuff
		JButton Carbon = new JButton ("C");
		Carbon.setActionCommand("addCarbon");
		Carbon.addActionListener(this);
		Carbon.setOpaque(false);
		Carbon.setContentAreaFilled(false);
		Carbon.setBorderPainted(false);
		controls.add(Carbon);
		
		JButton Hydrogen = new JButton ("H");
		Hydrogen.setActionCommand("addHydrogen");
		Hydrogen.addActionListener(this);
		Hydrogen.setOpaque(false);
		Hydrogen.setContentAreaFilled(false);
		Hydrogen.setBorderPainted(false);
		controls.add(Hydrogen);
		//end of new stuff
		
		JButton Atom = new JButton("Atom");
		Atom.setActionCommand("addAtom");
		Atom.addActionListener(this);
		controls.add(Atom);
		
		JButton singleBond = new JButton("Single Bond");
		singleBond.setActionCommand("addSingleBond");
		singleBond.addActionListener(this);
		controls.add(singleBond);
		
		Thread closeThread = new Thread(new Runnable() {
			public void run() {
				while(true) {
					try { Thread.sleep(10);} catch (InterruptedException e) {}
				}
			}
		});
		Thread animationThread = new Thread(new Runnable() {	//The main loop
			public void run() {
				while(true) {
						panel.repaint();
						try {Thread.sleep(10);} catch (Exception ex) {}	//10 millisecond delay between each refresh
				}
			}
		});			
		closeThread.start();
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
		if(e.getKeyCode() == KeyEvent.VK_1) 
			atomList.add(new Atom("Atom", 250, 25, 20, 20, "Atom"));
		else if(e.getKeyCode() == KeyEvent.VK_2) 
			atomList.add(new Atom("Single Bond", 250, 25, 20, 10, "singleBond"));
		
		for (int i = 0; i < Main.atomList.size(); i++) {
			System.out.println(Main.atomList.get(i));
		}
	}
	
	public static void main(String[] args) {
		new Main();
	}

	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
		for(int i=0; i<atomList.size(); i++) {
			Atom temp = atomList.get(i);			
			if(mouseX>temp.getX() && mouseX<(temp.getX()+temp.getWidth()) && 
					mouseY>temp.getY() && mouseY<(temp.getY()+temp.getHeight())) {
				selected = temp;
			}
		}
		if(selected != null) selected.updateLocation(mouseX, mouseY);
	}

	public void mouseMoved(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {
		selected = null;
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("addAtom")) {
			atomList.add(new Atom("Atom", 250, 25, 20, 20, "Atom"));
		}
		else if (e.getActionCommand().equals("addCarbon")) {
			atomList.add(new Atom("Carbon", 250, 25, 20, 20, "Atom"));
		}
		else if (e.getActionCommand().equals("addHydrogen")) {
			atomList.add(new Atom("Hydrogen", 250, 25, 20, 20, "Atom"));
		}
		else if(e.getActionCommand().equals("addSingleBond")) {
			atomList.add(new Atom("Single Bond", 250, 25, 20, 10, "singleBond"));
		}
		frame.requestFocus();
	}

	public static class Canvas extends JPanel {		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	
			
			g.drawString("Press 1 to add a circle (Atom), 2 to add a rectangle (single bond, for now)", 
					width/2 - 20, height/2);
			
			for(int i=0; i<atomList.size(); i++) {
				atomList.get(i).draw(g);
//				System.out.println(atomList.get(i).toString());
			}
		}
	}
	
}