import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
	public static final int height = 600;	
	public static final int width = 900;
	
	public static int mouseX;
	public static int mouseY;
	
	public Molecule selected;
		
	//Put any and all objects you create into an ArrayList, the Canvas method will draw their contents onto the panel
	public static ArrayList<Molecule> moleculeList = new ArrayList<Molecule>();
		
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
		
		JButton molecule = new JButton("Molecule");
		molecule.setActionCommand("addMolecule");
		molecule.addActionListener(this);
		controls.add(molecule);
		
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
						try {Thread.sleep(30);} catch (Exception ex) {}	//10 millisecond delay between each refresh
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
		if(e.getKeyCode() == KeyEvent.VK_1) moleculeList.add(new Molecule("Molecule", 250, 25, 20, 20, "molecule"));
		else if(e.getKeyCode() == KeyEvent.VK_2) moleculeList.add(new Molecule("Single Bond", 250, 25, 20, 10, "singleBond"));
	}
	
	public static void main(String[] args) {
		new Main();
	}

	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
		for(int i=0; i<moleculeList.size(); i++) {
			Molecule temp = moleculeList.get(i);			
			if(mouseX>temp.getX() && mouseX<(temp.getX()+temp.getWidth()) && mouseY>temp.getY() && mouseY<(temp.getY()+temp.getHeight())) {
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
		if(e.getActionCommand().equals("addMolecule")) moleculeList.add(new Molecule("Molecule", 250, 25, 20, 20, "molecule"));
		
		else if(e.getActionCommand().equals("addSingleBond")) moleculeList.add(new Molecule("Single Bond", 250, 25, 20, 10, "singleBond"));
	
		frame.requestFocus();
	}

	public static class Canvas extends JPanel {		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);	
			
			g.drawString("Press 1 to add a circle (molecule), 2 to add a rectangle (single bond, for now)", width/2 - 20, height/2);
			g.drawString("If you put two molecules together they will move as a group, although this gets", width/2 - 20, height/2 + 60);
			g.drawString("more unstable as the number of molecules in one group increases (I'm fiXinG iT dW)", width/2 - 20, height/2 + 80);
			g.drawString("Prob gonna add rotation and more legit connections next", width/2 - 20, height/2 + 120);

			for(int i=0; i<moleculeList.size(); i++)
				moleculeList.get(i).draw(g);
		}
	}	
}
