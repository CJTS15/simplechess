package main;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel {

    private JLabel label;

    public StatusPanel() {
        setLayout(new BorderLayout());
        label = new JLabel("Ready");
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        add(label, BorderLayout.WEST);
        setPreferredSize(new Dimension(0, 35));
    }

    public void setMessage(String message) {
        label.setText(message);
    }
    
    public void setTextColor(Color c) {
        label.setForeground(c);
    }
}

