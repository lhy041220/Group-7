package view.gamePanel;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Game;
import model.card.Card;

/***
 * 玩家行动面板
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

    // 按钮样式相关的常量
    private static final Color BUTTON_BACKGROUND = new Color(51, 122, 183);  // 蓝色
    private static final Color BUTTON_HOVER = new Color(40, 96, 144);       // 深蓝色
    private static final Color END_TURN_BACKGROUND = new Color(217, 83, 79); // 红色
    private static final Color END_TURN_HOVER = new Color(172, 66, 63);     // 深红色
    private static final Font BUTTON_FONT = new Font("微软雅黑", Font.BOLD, 14);
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
        moveButton = createActionButton("移动", BUTTON_BACKGROUND, BUTTON_HOVER);
        shoreUpButton = createActionButton("排水", BUTTON_BACKGROUND, BUTTON_HOVER);
        giveCardButton = createActionButton("给予卡牌", BUTTON_BACKGROUND, BUTTON_HOVER);
        captureTreasureButton = createActionButton("获取宝藏", BUTTON_BACKGROUND, BUTTON_HOVER);
        endTurnButton = createActionButton("结束回合", END_TURN_BACKGROUND, END_TURN_HOVER);
        viewTreasureDiscardButton = new JButton("查看宝藏卡弃牌堆");
        viewFloodDiscardButton = new JButton("查看洪水卡弃牌堆");
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
     * 添加控制按钮
     */
    private void addControlButtons() {
        add(moveButton);
        add(shoreUpButton);
        add(giveCardButton);
        add(captureTreasureButton);
        add(Box.createHorizontalStrut(20)); // 在结束回合按钮前添加一些间距
        add(endTurnButton);
        add(viewTreasureDiscardButton);
        add(viewFloodDiscardButton);

        // 为弃牌堆按钮添加事件
        viewTreasureDiscardButton.addActionListener(e -> {
            List<? extends Card> handCards = Game.getInstance().getTreasureDeck().getDiscardPile();
            showDiscardPile("宝藏卡弃牌堆", new java.util.ArrayList<Card>(handCards));
        });
        viewFloodDiscardButton.addActionListener(e -> {
            List<? extends Card> floodCards = Game.getInstance().getFloodDeck().getDiscardPile();
            showDiscardPile("洪水卡弃牌堆", new java.util.ArrayList<Card>(floodCards));
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
     * 设置所有按钮的启用状态
     * @param enabled 是否启用按钮
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
