package main;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class MenuPanel extends JPanel {

    private JButton btnNewGame;
    private JButton btnExit;
    private JButton btnInstruction;
	Sound sound = new Sound();

    @SuppressWarnings("unused")
    private GamePanel gamePanel;
    public MenuPanel(GamePanel gamePanel, JFrame parentFrame) {
    	this.gamePanel = gamePanel;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 35));

        // Create buttons
        btnNewGame = new JButton("New Game");
        btnInstruction = new JButton("Instruction");
        btnExit = new JButton("Exit");

        // Style buttons
        styleButton(btnNewGame);
        styleButton(btnInstruction);
        styleButton(btnExit);

        // Add buttons to panel
        add(btnNewGame);
        add(btnInstruction);
        add(btnExit);

        btnNewGame.addActionListener(e -> {
        	playSoundEffect(4);
        	gamePanel.resetGame(); 
        	JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gamePanel);
            frame.dispose();
            new TitleWindow();  
        });



        btnInstruction.addActionListener(e -> {
        	playSoundEffect(4);
            JOptionPane.showMessageDialog(
                    parentFrame, // center dialog on parent frame
                    "Move the pieces like normal chess.\nCheckmate your opponent.",
                    "Instructions",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        btnExit.addActionListener(e -> {
        	playSoundEffect(4);
        	System.exit(0);	
        });
    }

	// Button hover + minimal styling
    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.DARK_GRAY);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.DARK_GRAY);
            }
        });
    }

    // Expose buttons if needed elsewhere
    public JButton getNewGameButton() {
        return btnNewGame;
    }

    public JButton getInstructionButton() {
        return btnInstruction;
    }

    public JButton getExitButton() {
        return btnExit;
    }
    
	public void playMusic(int i) {
		sound.setFile(i);
		sound.play();
		sound.loop();
	}
	
	public void stopMusic() {
		sound.stop();
	}
	
	public void playSoundEffect(int i) {
		sound.setFile(i);
		sound.play();
	}
}
