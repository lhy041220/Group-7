package view.gamePanel;

import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {

    private JPanel treasureCardPanel;
    private JPanel floodCardPanel;
    private JPanel playerHandPanel;

    // Card display related constants
    private static final Color TREASURE_DECK_COLOR = new Color(255, 215, 0, 180); // Gold, semi-transparent
    private static final Color FLOOD_DECK_COLOR = new Color(0, 191, 255, 180);   // Deep sky blue, semi-transparent
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;
    private static final int CARD_SPACING = 10;

    public CardPanel() {
        setPreferredSize(new Dimension(0, 150));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(new Color(245, 245, 245)); // Light gray background

        initComponents();
        addComponents();
    }

    /**
     * Initialize card-related components
     */
    private void initComponents() {
        // Treasure deck panel
        treasureCardPanel = new JPanel(new BorderLayout());
        treasureCardPanel.setBackground(TREASURE_DECK_COLOR);
        treasureCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32), 2), // Dark golden border
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel treasureLabel = new JLabel("Treasure Deck");
        treasureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        treasureLabel.setFont(new Font("Arial", Font.BOLD, 14));
        treasureCardPanel.add(treasureLabel, BorderLayout.NORTH);

        // Flood deck panel
        floodCardPanel = new JPanel(new BorderLayout());
        floodCardPanel.setBackground(FLOOD_DECK_COLOR);
        floodCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2), // Steel blue border
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel floodLabel = new JLabel("Flood Deck");
        floodLabel.setHorizontalAlignment(SwingConstants.CENTER);
        floodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        floodCardPanel.add(floodLabel, BorderLayout.NORTH);

        // Player hand panel
        playerHandPanel = new JPanel();
        playerHandPanel.setLayout(new FlowLayout(FlowLayout.CENTER, CARD_SPACING, 5));
        playerHandPanel.setBackground(new Color(245, 245, 245));
        playerHandPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Player Hand",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));
    }

    /**
     * Add card-related components to the panel
     */
    private void addComponents() {
        // Create deck panel
        JPanel deckPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        deckPanel.setOpaque(false);

        // Add decks
        deckPanel.add(treasureCardPanel);
        deckPanel.add(floodCardPanel);

        // Add both deck panel and player hand panel to main panel
        add(deckPanel, BorderLayout.NORTH);
        add(playerHandPanel, BorderLayout.CENTER);
    }

    /**
     * Update the display of player's hand cards
     * @param cards The cards in player's hand
     */
    public void updatePlayerHand(String[] cards) {
        playerHandPanel.removeAll();

        for (String card : cards) {
            JPanel cardComponent = createCardComponent(card);
            playerHandPanel.add(cardComponent);
        }

        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }

    /**
     * Create a single card component
     */
    private JPanel createCardComponent(String cardName) {
        JPanel cardPanel = new JPanel();
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        cardPanel.setBackground(Color.WHITE);

        // Card name label
        JLabel nameLabel = new JLabel("<html><center>" + cardName + "</center></html>");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        cardPanel.add(nameLabel, BorderLayout.CENTER);

        return cardPanel;
    }
}
