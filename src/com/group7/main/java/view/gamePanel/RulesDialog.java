package view.gamePanel;

import view.coverPanel.RuleImagePanel;

import javax.swing.*;
import java.awt.*;

public class RulesDialog extends JDialog {
    private static final int DIALOG_WIDTH = 900;
    private static final int DIALOG_HEIGHT = 700;
    
    public RulesDialog(JFrame parent) {
        super(parent, "Game Rules", true);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(parent);
        
        initializeComponents();
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Custom image panel
        RuleImagePanel imagePanel = new RuleImagePanel();
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton prevButton = createNavigationButton("Previous", e -> imagePanel.previousImage());
        JButton nextButton = createNavigationButton("Next", e -> imagePanel.nextImage());
        JButton closeButton = createNavigationButton("Close", e -> dispose());
        
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(closeButton);
        
        setLayout(new BorderLayout(10, 10));
        add(imagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createNavigationButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(100, 30));
        button.addActionListener(listener);
        return button;
    }
} 