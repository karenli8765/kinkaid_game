import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class GridDemoFrame extends JFrame
{
	GridDemoPanel thePanel;
	JLabel scoreLabel, messageLabel;
	public GridDemoFrame()
	{
		super("Grid Demo");
		
		setSize(600,600+24+16);
		
		this.getContentPane().setLayout(new BorderLayout());
		thePanel = new GridDemoPanel(this);
		scoreLabel = new JLabel("It's Player A's turn \t Player A Score: 1 \t Player B Score: 1");
		messageLabel = new JLabel("");
		Box southPanel = Box.createHorizontalBox();
		
		this.getContentPane().add(thePanel,BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		southPanel.add(Box.createHorizontalStrut(10));
		southPanel.add(scoreLabel, BorderLayout.SOUTH);
		southPanel.add(Box.createGlue());
		southPanel.add(messageLabel, BorderLayout.SOUTH);
		southPanel.add(Box.createHorizontalStrut(10));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);	
	//	thePanel.initiateAnimationLoop(); // uncomment this line if your program uses animation.
	}
	
	public void updateMessage(String message)
	{
		messageLabel.setText(message);
		messageLabel.repaint();
	}
	
	public void updateScore(int scoreA, int scoreB, String whosTurn)
	{
		scoreLabel.setText("It's Player "+whosTurn+"'s turn \t Player A Score: "+scoreA+" \t Player B Score: "+scoreB);
		scoreLabel.repaint();
	}
}
