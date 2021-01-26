import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/*
 * Name: William Chung
 * 
 * This program displays a  4 by 4 grid has unique numbers of 1 
 * to 15 and dedede. A keyboard direction arrow can move 
 * dedede in bounds and swap numbers and he game ends 
 * when the numbers are in ascending order by row and 
 * dedede is in the bottom right corner
 */

public class DededeSlider extends JFrame implements KeyListener {

	private BufferedImage image;

	private PicPanel[][] allPanels;

	private int dededeRow; // location of dedede
	private int dededeCol;
	private int dededeMoves;

	public DededeSlider() {

		//makes the frame
		setSize(375, 375);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Tile Slider");
		getContentPane().setBackground(Color.black);

		//makes the matrix of panels and the grid layout
		allPanels = new PicPanel[4][4];
		setLayout(new GridLayout(4, 4, 2, 2));
		setBackground(Color.black);

		//gets the dedede image
		try {
			image = ImageIO.read(new File("dedede.jpg"));
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not read in the pic");
			System.exit(0);
		}

		
		int[] spots = new int[16];
		
		for (int i = 1; i <= 16; i++) {
		
			//adds all 16 spots in the matrix to a list that will eventually be used randomly get a spot for a unique number
			spots[i-1]=i;
			
			//while also constructing and adding the pic panels
			int row = (i - 1) / 4;
			int col = (i - 1) % 4;
			allPanels[row][col] = new PicPanel();
			add(allPanels[row][col]);
		}
		
		

		/* 
		 * Randomly gives 15 spots in the pic panel matrix a unique number from 1 to 15
		 * num both represents the pointer in the list of the last index in the list of
		 * spots that haven't been given and also the number panel 
		 * that is going to be assigned in the interation to the psot
		 */
		for (int num = 15; num >= 1; num--) {
			

			//randomly picks one of the 16 spots that have not been picked before
			int random = (int) (Math.random() * (num+1));
			int temp = spots[random];
			spots[random]=spots[num];
			spots[num]=temp;
			
			
			//converts that spot index to matrix index
			int randRow = (spots[num] - 1) / 4;
			int randCol = (spots[num] - 1) % 4;
			
			//assigns that spot a unique number
			allPanels[randRow][randCol].setNumber(num);
			
		}

		//find the only spot that has not gotten a unique number and makes that matrix location dedede's row and col
		int dededeLoc = spots[0];
		dededeRow = (dededeLoc - 1) / 4;
		dededeCol = (dededeLoc - 1) % 4;

		//adds keylistener and makes the frame visible
		this.addKeyListener(this);
		setVisible(true);
	}

	//when a key direction arrow is pressed, the game swaps to the correct location if it is in bounds and checks if the game ends
	public void keyPressed(KeyEvent arg0) {

		int keyVal = arg0.getKeyCode(); // you will use this in the next project

		//when corresponding key is pressed, dedede's spot swaps the corresponding spot's number with dedede
		//it also checks if the swapping will make dedede stay in bounds
		if ((keyVal == KeyEvent.VK_LEFT) && (dededeCol > 0)) {
			swap(dededeRow, dededeCol - 1);
		} 
		else if ((keyVal == KeyEvent.VK_RIGHT) && (dededeCol < 3)) {
			swap(dededeRow, dededeCol + 1);
		}
		else if ((keyVal == KeyEvent.VK_UP) && (dededeRow > 0)) {
			swap(dededeRow - 1, dededeCol);
		} 
		else if ((keyVal == KeyEvent.VK_DOWN) && (dededeRow < 3)) {
			swap(dededeRow + 1, dededeCol);
		}
		
		
	}

	//swaps dedede's location and given location with the dedede and the number. also checks if the game ends
	private void swap(int row, int col) {

		//the previous dedede picpanel location gets the given locations number
		allPanels[dededeRow][dededeCol].setNumber(allPanels[row][col].number);
		
		//the given, new dedede pic panel location gets dedede
		allPanels[row][col].removeNumber();
	
		//also counts dedede's moves
		dededeMoves++;

		//checks if the game is over and pops out a messgebox congratulating them and telling the number of moves
		if(gameOver()) {
			
			JOptionPane.showMessageDialog(null, "CONGRATULATIONS! YOU WIN!! Moves Before Winning: " + dededeMoves);
			//dedede does not move once the game is over
			removeKeyListener(this);
			
		} 
		
		//updates dedede's location's row and col if the game is not over with the new current location
		else {		
			dededeRow = row;
			dededeCol = col;
		}
		
	}
	
	//checks if the player wins by getting all the numbers and dedede in the right location
	private boolean gameOver() {

		//first checks if dedede is in the bottom right corner of the board first
		if (allPanels[3][3].number != -1) {
			return false;
		}
		
		//checks if all numbers now in ascending orders [1 through 15]
		for (int i = 0; i < 15; i++) {
			
			if (allPanels[i / 4][i % 4].number != i+1) {
				return false;
			}

		}
		
		return true;

	}
	
	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

	class PicPanel extends JPanel {

		private int width = 76;
		private int height = 80; // dimensions of the Panel

		private int number = -1; // -1 when dedede is at that position.
		private JLabel text;

		public PicPanel() {

			setBackground(Color.white);
			setLayout(null);

		}

		// changes the panel to have the given number
		public void setNumber(int num) {

			number = num;
			text = new JLabel("" + number, SwingConstants.CENTER);
			text.setFont(new Font("Calibri", Font.PLAIN, 55));
			text.setBounds(0, 35, 70, 60);
			this.add(text);

			repaint();
		}

		// replaces the number with dedede
		public void removeNumber() {
			this.remove(text);
			number = -1;
			repaint();
		}

		public Dimension getPreferredSize() {
			return new Dimension(width, height);
		}

		// this will draw the image or the number
		// called by repaint and when the panel is initially drawn
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (number == -1)
				g.drawImage(image, 8, 0, this);
		}

	}

	//runs the program
	public static void main(String[] args) {
		new DededeSlider();
	}

}
