package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("serial")
public class TitleWindow extends JFrame {
	private BufferedImage logoImage;
	Sound sound = new Sound();

    public TitleWindow() {        
        setTitle("Simple Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        add(new TitlePanel());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class TitlePanel extends JPanel {
        public static final int WIDTH = 800;
        public static final int HEIGHT = 870;

        public TitlePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.BLACK);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // Load logo once
            try {
                logoImage = ImageIO.read(getClass().getResource("/logo.png"));
                logoImage = resize(logoImage, 200, 150);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Top flexible space to center vertically
            add(Box.createVerticalGlue());

            // Logo panel
            JPanel logoPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (logoImage != null) {
                        int x = (getWidth() - logoImage.getWidth()) / 2;
                        int y = (getHeight() - logoImage.getHeight()) / 2;
                        g.drawImage(logoImage, x, y, null);
                    }
                }
            };
            logoPanel.setPreferredSize(new Dimension(200, 200));
            logoPanel.setMaximumSize(new Dimension(200, 200));
            logoPanel.setOpaque(false);
            add(logoPanel);

            add(Box.createRigidArea(new Dimension(0, 30)));

            // Buttons
            JButton btnNewGame = new JButton("New Game");
            JButton btnInstruction = new JButton("Instructions");
            JButton btnExit = new JButton("Exit");

            styleButton(btnNewGame);
            styleButton(btnInstruction);
            styleButton(btnExit);

            add(btnNewGame);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(btnInstruction);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(btnExit);

            // Bottom flexible space to center vertically
            add(Box.createVerticalGlue());

            // Button actions
            btnNewGame.addActionListener(e -> {
            	stopMusic();
            	playSoundEffect(4);
                dispose();
                Main.launchGame();
            });

            btnInstruction.addActionListener(e -> {
                playSoundEffect(4);
                JOptionPane.showMessageDialog(
                        this,
                        "Move the pieces like normal chess.\nCheckmate your opponent.",
                        "Instructions",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });

            btnExit.addActionListener(e -> {
                playSoundEffect(4);
            	System.exit(0);            	
            });
            

    	    playMusic(0);
        }

        private void styleButton(JButton btn) {
            btn.setAlignmentX(CENTER_ALIGNMENT);
            btn.setPreferredSize(new Dimension(200, 35));
            btn.setMaximumSize(new Dimension(200, 35));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(false);
            btn.setForeground(Color.GRAY);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setForeground(Color.GRAY);
                }
            });
        }

        private BufferedImage resize(BufferedImage img, int newW, int newH) {
            Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.drawImage(tmp, 0, 0, null);
            g2.dispose();
            return resized;
        }
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
