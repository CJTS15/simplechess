package main;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        new TitleWindow();
    }

    // Called by the title window when "New Game" is clicked
    public static void launchGame() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Simple Chess");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setLayout(new BorderLayout());

            StatusPanel statusPanel = new StatusPanel();
            GamePanel gp = new GamePanel(statusPanel);
            MenuPanel menuPanel = new MenuPanel(gp, window);

            window.add(menuPanel, BorderLayout.NORTH);
            window.add(gp, BorderLayout.CENTER);
            window.add(statusPanel, BorderLayout.SOUTH);

            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            gp.launchGame();
        });
    }
}
