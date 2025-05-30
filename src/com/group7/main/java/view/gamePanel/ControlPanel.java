package view.gamePanel;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Game;
import model.card.Card;

/***
 * Player action panel
 */
@Getter
public class ControlPanel extends JPanel {

    private JButton moveButton;
    private JButton shoreUpButton;
    private JButton giveCardButton;
    private JButton captureTreasureButton;
    private JButton endTurnButton;
    private JButton viewTreasureDiscardButton;
    private JButton viewFloodDiscardButton;

    // Button style related constants
    private static final Color BUTTON_BACKGROUND = new Color(51, 122, 183);   // Blue
    private static final Color BUTTON_HOVER = new Color(40, 96, 144);         // Dark blue
    private static final Color END_TURN_BACKGROUND = new Color(217, 83, 79);  // Red
    private static final Color END_TURN_HOVER = new Color(172, 66, 63);       // Dark red
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Dimension BUTTON_SIZE = new Dimension(140, 35);
    private static final int BUTTON_SPACING = 10;

    public ControlPanel() {
        setPreferredSize(new Dimension(0, 60));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new FlowLayout(FlowLayout.CENTER, BUTTON_SPACING, 0));

        initializeButtons();
        addControlButtons();
    }

    private void initializeButtons() {
        moveButton = createActionButton("Move", BUTTON_BACKGROUND, BUTTON_HOVER);
        shoreUpButton = createActionButton("Shore Up", BUTTON_BACKGROUND, BUTTON_HOVER);
        giveCardButton = createActionButton("Give Card", BUTTON_BACKGROUND, BUTTON_HOVER);
        captureTreasureButton = createActionButton("Capture Treasure", BUTTON_BACKGROUND, BUTTON_HOVER);
        endTurnButton = createActionButton("End Turn", END_TURN_BACKGROUND, END_TURN_HOVER);
        viewTreasureDiscardButton = new JButton("View Treasure Discard Pile");
        viewFloodDiscardButton = new JButton("View Flood Discard Pile");
        viewTreasureDiscardButton.setFont(BUTTON_FONT);
        viewFloodDiscardButton.setFont(BUTTON_FONT);
    }

    private JButton createActionButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(hoverColor);
                } else if (getModel().isRollover()) {
                    g2d.setColor(hoverColor);
                } else {
                    g2d.setColor(normalColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                FontMetrics fm = g2d.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(this.getText(), g2d).getBounds();

                int textX = (getWidth() - textRect.width) / 2;
                int textY = (getHeight() - textRect.height) / 2 + fm.getAscent();

                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), textX, textY);
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(BUTTON_SIZE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        return button;
    }

    /**
     * Add control buttons to panel
     */
    private void addControlButtons() {
        add(moveButton);
        add(shoreUpButton);
        add(giveCardButton);
        add(captureTreasureButton);
        add(Box.createHorizontalStrut(20)); // Add spacing before End Turn button
        add(endTurnButton);
        add(viewTreasureDiscardButton);
        add(viewFloodDiscardButton);

        // Add discard pile button events
        viewTreasureDiscardButton.addActionListener(e -> {
            List<? extends Card> handCards = Game.getInstance().getTreasureDeck().getDiscardPile();
            showDiscardPile("Treasure Discard Pile", new java.util.ArrayList<Card>(handCards));
        });
        viewFloodDiscardButton.addActionListener(e -> {
            List<? extends Card> floodCards = Game.getInstance().getFloodDeck().getDiscardPile();
            showDiscardPile("Flood Discard Pile", new java.util.ArrayList<Card>(floodCards));
        });
    }

    private void showDiscardPile(String title, List<Card> discardPile) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), title, true);
        JList<Card> cardList = new JList<>(discardPile.toArray(new Card[0]));
        dialog.add(new JScrollPane(cardList));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Enable or disable all control buttons
     * @param enabled whether to enable buttons
     */
    public void setButtonsEnabled(boolean enabled) {
        moveButton.setEnabled(enabled);
        shoreUpButton.setEnabled(enabled);
        giveCardButton.setEnabled(enabled);
        captureTreasureButton.setEnabled(enabled);
        endTurnButton.setEnabled(enabled);
        viewTreasureDiscardButton.setEnabled(enabled);
        viewFloodDiscardButton.setEnabled(enabled);
    }
}
