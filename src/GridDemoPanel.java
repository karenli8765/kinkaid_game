import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GridDemoPanel extends JPanel implements MouseListener, KeyListener
{
	private Cell[][] theGrid;
	public final static int NUM_ROWS = 24;
	public final static int NUM_COLS = 24;
	public GridDemoFrame myParent;
	public int playerAColorID;
	public int playerBColorID;
	public int playerAScore;
	public int playerBScore;
	public String whosTurn;
	private ArrayList<Cell> playerATaken;
	private ArrayList<Cell> playerBTaken;
	private ArrayList<Cell> adjacentCells;

	
	public GridDemoPanel(GridDemoFrame parent)
	{
		super();
		rulesPanelDisplay();
		resetCells();
		playerATaken = new ArrayList<Cell>();
		playerBTaken = new ArrayList<Cell>();
		adjacentCells = new ArrayList<Cell>();
		theGrid[23][0].setDisplayMarker(true);
		theGrid[23][0].setMarker("A");
		theGrid[0][23].setDisplayMarker(true);
		theGrid[0][23].setMarker("B");
		setBackground(Color.BLACK);
		addMouseListener(this);
		myParent = parent;

		whosTurn = "A";
		playerAColorID = theGrid[23][0].getColorID();
		playerBColorID = theGrid[0][23].getColorID();
		while (playerAColorID == playerBColorID)
		{
			resetCells();
			playerAColorID = theGrid[23][0].getColorID();
			playerBColorID = theGrid[0][23].getColorID();
		}
		playerATaken.add(theGrid[23][0]);
		playerBTaken.add(theGrid[0][23]);
		System.out.println("Player A Color ID: " + playerAColorID);
		System.out.println("Player B Color ID: " + playerBColorID);
	}	
	
	/**
	 * makes a new board with random colors, completely filled in, and resets the score to zero.
	 */
	public void resetCells()
	{
		theGrid = new Cell[NUM_ROWS][NUM_COLS];
		for (int r = 0; r<NUM_ROWS; r++)
		{
			for (int c = 0; c < NUM_COLS; c++)
			{
				theGrid[r][c] = new Cell(r, c);
			}
		}
		for (int r = 0; r<NUM_ROWS; r++)
		{
			for (int c = 0; c < NUM_COLS; c++)
			{
				if (r != 0 && c != 0) {
					if (theGrid[r][c].getColorID() == theGrid[r - 1][c].getColorID() || theGrid[r][c].getColorID() == theGrid[r][c - 1].getColorID())
					{
						theGrid[r][c].cycleColorIDForward();
					}
					if (theGrid[r][c].getColorID() == theGrid[r - 1][c].getColorID() || theGrid[r][c].getColorID() == theGrid[r][c - 1].getColorID())
					{
						theGrid[r][c].cycleColorIDForward();
					}
				}
				if (r != 0 && c == 0)
				{
					if (theGrid[r][c].getColorID() == theGrid[r - 1][c].getColorID())
					{
						theGrid[r][c].cycleColorIDForward();
					}
				}
				if (r == 0 && c != 0)
				{
					if (theGrid[r][c].getColorID() == theGrid[r][c - 1].getColorID())
					{
						theGrid[r][c].cycleColorIDForward();
					}
				}
			}
		}
	}

	/**
	 * shows rules panel
	 */
	public void rulesPanelDisplay()
	{
		String rules = "";
		rules += "This game of Filler is similar to the iMessage game from Game Pigeon. The board is 24x24.\n";
		rules += "Player A starts with the cell in the lower left corner, and Player B starts with the cell in the upper right corner\n";
		rules += "When it is your turn, you must click a cell that is adjacent to your group of cells. All of your cells will change to that color.\n";
		rules += "You cannot chose cells that are the same color as your's opponent's group of cells.\n";
		rules += "In this game, adjacent cells must share a side, so touching corners do not count.\n";
		rules += "If necessary, you may chose a cell already in your own group, essentially skipping a turn.\n";
		rules += "The goal is to have the more cells in your group than your opponent.";
		rules += "The game is over when all cells are taken, or when one player has no more available adjacent cells.\n";
		rules += "The un-taken cells will go to the opposing player in that scenerio.";
		JOptionPane.showMessageDialog(this, rules);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c].drawSelf(g);
	}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * the mouse listener has detected a click, and it has happened on the cell in theGrid at row, col
	 * @param row
	 * @param col
	 */
	public void userClickedCell(int row, int col)
	{
		System.out.println("("+row+", "+col+")");
		int clickedCellColorID = theGrid[row][col].getColorID();
		//makes sure its an available color
		if (whosTurn == "A" && clickedCellColorID == playerBColorID)
		{
			System.out.println("You cannot pick this color. This is player B's color at the moment.");
			return;
		}
		if (whosTurn == "B" && clickedCellColorID == playerAColorID)
		{
			System.out.println("You cannot pick this color. This is player A's color at the moment.");
			return;
		}

		if (whosTurn == "A")
		{
			//check if its a adjacent to another cell of A
			if (checkIfAdjacentToGroup("A", row, col) == false && !playerATaken.contains(theGrid[row][col]))
			{
				System.out.println("Not adjacent");
				return;
			}
			if (!playerATaken.contains(theGrid[row][col]))
			{
				playerATaken.add(theGrid[row][col]);
				playerAColorID = clickedCellColorID;
				changeAllColorsToMatch("A");
				groupInMatchingColors(playerATaken);

			}
			else
			{
				System.out.println("You chose a cell you already had.");
			}

			whosTurn = "B";
			System.out.println("Player B's Turn");
			repaint();
			myParent.updateScore(playerATaken.size(), playerBTaken.size(), whosTurn);
			checkIfGameOver();
			return;
		}
		if (whosTurn == "B")
		{
			//check if its a adjacent to another cell of A
			if (checkIfAdjacentToGroup("B", row, col) == false && !playerBTaken.contains(theGrid[row][col]))
			{
				System.out.println("Not adjacent");
				return;
			}
			if (!playerBTaken.contains(theGrid[row][col]))
			{
				playerBTaken.add(theGrid[row][col]);
				playerBColorID = clickedCellColorID;
				changeAllColorsToMatch("B");
				groupInMatchingColors(playerBTaken);
			}
			else
			{
				System.out.println("You chose a cell you already had.");
			}

			whosTurn = "A";
			System.out.println("Player A's Turn");
			repaint();
			playerBScore = playerBTaken.size();
			myParent.updateScore(playerATaken.size(), playerBTaken.size(), whosTurn);
			checkIfGameOver();
			return;
		}
	}

	/**
	 * checks two cells to see if they're adjacent
	 * @param origRow
	 * @param origCol
	 * @param newRow
	 * @param newCol
	 * @return whether the cells are adjacent or not
	 */
	public boolean checkIfAdjacent(int origRow, int origCol, int newRow, int newCol)
	{
		if (origRow - 1 == newRow && origCol == newCol)
		{
			return true;
		}
		if (origRow + 1 == newRow && origCol == newCol)
		{
			return true;
		}
		if (origRow == newRow && origCol - 1 == newCol)
		{
			return true;
		}
		if (origRow == newRow && origCol + 1 == newCol)
		{
			return true;
		}
		return false;
	}

	/**
	 * check whether a cell is adjacent to the current player's group of cells
	 * @param whosTurn
	 * @param row
	 * @param col
	 * @return whether or not the cell is viable
	 */
	public boolean checkIfAdjacentToGroup(String whosTurn, int row, int col)
	{
		boolean adjacent = false;
		if (whosTurn == "A")
		{
			for (int i = 0; i < playerATaken.size(); i++)
			{
				if(checkIfAdjacent(playerATaken.get(i).getRow(), playerATaken.get(i).getCol(), row, col) == true)
				{
					adjacent = true;
				}
			}
		}
		if (whosTurn == "B")
		{
			for (int i = 0; i < playerBTaken.size(); i++)
			{
				if (checkIfAdjacent(playerBTaken.get(i).getRow(), playerBTaken.get(i).getCol(), row, col) == true)
				{
					adjacent = true;
				}
			}
		}
		return adjacent;
	}

	/**
	 * take all the
	 * @param whosTurn
	 */
	public void changeAllColorsToMatch(String whosTurn)
	{
		if (whosTurn == "A")
		{
			for (int i = 0; i < playerATaken.size(); i++)
			{
				playerATaken.get(i).setColorID(playerAColorID);
				playerATaken.get(i).setDisplayMarker(true);
				playerATaken.get(i).setMarker("A");
			}
		}
		if (whosTurn == "B")
		{
			for (int i = 0; i < playerBTaken.size(); i++)
			{
				playerBTaken.get(i).setColorID(playerBColorID);
				playerBTaken.get(i).setDisplayMarker(true);
				playerBTaken.get(i).setMarker("B");
			}
		}
	}

	/**
	 * if there are adjacent cells with matching colors, group them into the player's list of taken cells
	 * @param playersCells
	 */
	public void groupInMatchingColors(ArrayList<Cell> playersCells)
	{
		for (int i = 0; i < playersCells.size(); i++)
		{
			int row = playersCells.get(i).getRow();
			int col = playersCells.get(i).getCol();
			if (row != 0 && !playersCells.contains(theGrid[row - 1][col]))
			{
				adjacentCells.add(theGrid[row-1][col]);
			}
			if (row != 23 && !playersCells.contains(theGrid[row + 1][col]))
			{
				adjacentCells.add(theGrid[row+1][col]);
			}
			if (col != 0 && !playersCells.contains(theGrid[row][col - 1]))
			{
				adjacentCells.add(theGrid[row][col-1]);
			}
			if (col != 23 && !playersCells.contains(theGrid[row][col + 1]))
			{
				adjacentCells.add(theGrid[row][col+1]);
			}
		}

		int currentColorIDToMatch;
		if (whosTurn == "A")
		{
			currentColorIDToMatch = playerAColorID;
		}
		else
		{
			currentColorIDToMatch = playerBColorID;
		}

		for (int i = 0; i < adjacentCells.size(); i++)
		{
			adjacentCells.get(i).setDisplayMarker(true);
			if (adjacentCells.get(i).getColorID() == currentColorIDToMatch && !playersCells.contains(adjacentCells.get(i)))
			{
				playersCells.add(adjacentCells.get(i));
				adjacentCells.get(i).setMarker(whosTurn);
			}
		}
		repaint();
		adjacentCells.clear();
	}

	public void checkForAvailableAdjacentCells(ArrayList<Cell> playersCells, String whosTurn)
	{
		int opponentsColorID;
		if (whosTurn == "A")
		{
			opponentsColorID = playerBColorID;
		}
		else
		{
			opponentsColorID = playerAColorID;
		}
		for (int i = 0; i < playersCells.size(); i++)
		{
			int row = playersCells.get(i).getRow();
			int col = playersCells.get(i).getCol();
			if (row != 0 && !playersCells.contains(theGrid[row - 1][col]))
			{
				adjacentCells.add(theGrid[row-1][col]);
			}
			if (row != 23 && !playersCells.contains(theGrid[row + 1][col]))
			{
				adjacentCells.add(theGrid[row+1][col]);
			}
			if (col != 0 && !playersCells.contains(theGrid[row][col - 1]))
			{
				adjacentCells.add(theGrid[row][col-1]);
			}
			if (col != 23 && !playersCells.contains(theGrid[row][col + 1]))
			{
				adjacentCells.add(theGrid[row][col+1]);
			}
		}

		boolean isThereAvailableCells = false;
		for (int i = adjacentCells.size()-1; i >= 0; i--)
		{
			if (adjacentCells.get(i).getColorID() != opponentsColorID)
			{
				isThereAvailableCells = true;
			}
		}

		if (!isThereAvailableCells)
		{
			if (whosTurn == "A")
			{
				makeGameOverDialog(playerATaken.size(), 576 - playerATaken.size());
			}
			if (whosTurn == "B")
			{
				makeGameOverDialog(576 - playerBTaken.size(), playerBTaken.size());
			}
		}
		adjacentCells.clear();
	}
	/**
	 * prompts the end of game window if all the squares are taken
	 */
	public void checkIfGameOver()
	{
		if (playerATaken.size() + playerBTaken.size() >= 576) //24*24
		{
			makeGameOverDialog(playerATaken.size(), playerBTaken.size());
		}
		checkForAvailableAdjacentCells(playerATaken, "A");
		checkForAvailableAdjacentCells(playerBTaken, "B");
	}
	
	/**
	 * Here's an example of a simple dialog box with a message.
	 */
	public void makeGameOverDialog(int scoreA, int scoreB)
	{
		String winner = "";
		if (scoreA > scoreB)
		{
			winner = "A";
		}
		if (scoreB > scoreA)
		{
			winner = "B";
		}
		if (scoreA == scoreB)
		{
			winner = "tie";
			JOptionPane.showMessageDialog(this, "Both Players Final Score: "+scoreA+"\nGame Over. There was a tie");
		}
		if (scoreA != scoreB)
		{
			JOptionPane.showMessageDialog(this, "Game Over. Final Score A: "+scoreA+" Final Score B: "+scoreB+"\nThe winner is Player " + winner);
		}
		repaint();
	}
	
	//============================ Mouse Listener Overrides ==========================

	@Override
	// mouse was just released within about 1 pixel of where it was pressed.
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
		int col = e.getX()/Cell.CELL_SIZE;
		int row = e.getY()/Cell.CELL_SIZE;
		userClickedCell(row, col);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
				
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
		
	}

	@Override
	// mouse just entered this window
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	// mouse just left this window
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	//============================ Key Listener Overrides ==========================

	@Override
	/**
	 * user just pressed and released a key. (May also be triggered by autorepeat, if key is held down?
	 * @param e
	 */
	public void keyTyped(KeyEvent e)
	{
		char whichKey = e.getKeyChar();
		myParent.updateMessage("User just typed \""+whichKey+"\"" );
		System.out.println(whichKey);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// TODO Auto-generated method stub
		char whichKey = e.getKeyChar();
		System.out.println(whichKey);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	// ============================= animation stuff ======================================
	/**
	 * if you wish to have animation, you need to call this method from the GridDemoFrame AFTER you set the window visibility.
	 */
	public void initiateAnimationLoop()
	{
		Thread aniThread = new Thread( new AnimationThread(500)); // the number here is the number of milliseconds between steps.
		aniThread.start();
	}
	
	/**
	 * Modify this method to do what you want to have happen periodically.
	 * This method will be called on a regular basis, determined by the delay set in the thread.
	 * Note: By default, this will NOT get called unless you uncomment the code in the GridDemoFrame's constructor
	 * that creates a thread.
	 *
	 */
	public void animationStep()
	{
		theGrid[0][0].cycleColorIDBackward();
		repaint();
	}
	// ------------------------------- animation thread - internal class -------------------
	public class AnimationThread implements Runnable
	{
		long start;
		long timestep;
		public AnimationThread(long t)
		{
			timestep = t;
			start = System.currentTimeMillis();
		}
		@Override
		public void run()
		{
			long difference;
			while (true)
			{
				difference = System.currentTimeMillis() - start;
				if (difference >= timestep)
				{
					animationStep();
					start = System.currentTimeMillis();
				}
				try
				{	Thread.sleep(1);
				}
				catch (InterruptedException iExp)
				{
					System.out.println(iExp.getMessage());
					break;
				}
			}
			
		}
		
	}
}
