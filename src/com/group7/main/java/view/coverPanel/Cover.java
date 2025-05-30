package view.coverPanel;

import view.gamePanel.RulesDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Cover extends JFrame {
    private static final String TITLE = "Forbidden Island";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String BACKGROUND_IMAGE_PATH = "src/com/group7/resources/images/TitleScreen.png";
    
    private BackgroundPanel backgroundPanel;
    private GameButton startButton;
    private GameButton rulesButton;
    private Runnable onGameStart;
    
    public Cover(Runnable onGameStart) {
        this.onGameStart = onGameStart;
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);
        
        initializeButtons();
        add(backgroundPanel);
    }
    
    private void initializeButtons() {
        startButton = new GameButton("Start Game", 300, 320);
        rulesButton = new GameButton("Game Rules", 300, 420);
        
        startButton.addActionListener(e -> {
            dispose();
            onGameStart.run();
        });
        
        rulesButton.addActionListener(e -> new RulesDialog(this));
        
        backgroundPanel.add(startButton);
        backgroundPanel.add(rulesButton);
    }
    
    public static void createAndShowCover(Runnable onGameStart) {
        SwingUtilities.invokeLater(() -> {
            Cover cover = new Cover(onGameStart);
            cover.setVisible(true);
        });
    }
    
    private static class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;
        
        public BackgroundPanel() {
            loadBackgroundImage();
        }
        
        private void loadBackgroundImage() {
            try {
                backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading background image: " + e.getMessage(),
                    "Resource Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                setBackground(Color.BLACK);
            }
        }
    }
    
    private static class GameButton extends JButton {
        private static final Color NORMAL_COLOR = new Color(200, 120, 0);
        private static final Color HOVER_COLOR = new Color(255, 150, 0);
        
        public GameButton(String text, int x, int y) {
            super(text);
            setupStyle();
            setBounds(x, y, 200, 60);
            setupHoverEffect();
        }
        
        private void setupStyle() {
            setFont(new Font("Arial", Font.BOLD, 24));
            setForeground(Color.WHITE);
            setBackground(NORMAL_COLOR);
            setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
            setFocusPainted(false);
        }
        
        private void setupHoverEffect() {
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(HOVER_COLOR);
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(NORMAL_COLOR);
                }
            });
        }
    }
}
