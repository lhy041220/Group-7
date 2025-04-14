package view.gamePanel;

import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {

    private JPanel treasureCardPanel;
    private JPanel floodCardPanel;
    private JPanel playerHandPanel;

    public CardPanel() {
        setPreferredSize(new Dimension(1500, 150));
        setLayout(new BorderLayout());

        initComponents();
        addComponents();
    }

    /**
     * 初始化卡牌相关组件
     */
    private void initComponents() {
        treasureCardPanel = new JPanel();
        treasureCardPanel.setBackground(Color.ORANGE);
        treasureCardPanel.add(new JLabel("Treasure Cards"));

        floodCardPanel = new JPanel();
        floodCardPanel.setBackground(Color.CYAN);
        floodCardPanel.add(new JLabel("Flood Cards"));

        playerHandPanel = new JPanel();
        playerHandPanel.setBackground(Color.LIGHT_GRAY);
        playerHandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    /**
     * 添加卡牌相关组件到面板
     */
    private void addComponents() {
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(treasureCardPanel);
        topPanel.add(floodCardPanel);

        add(topPanel, BorderLayout.NORTH);
        add(playerHandPanel, BorderLayout.CENTER);
    }

    /**
     * 更新玩家手牌显示
     * @param cards 玩家手中的卡牌
     */
    public void updatePlayerHand(String[] cards) {
        playerHandPanel.removeAll();
        for (String card : cards) {
            JLabel cardLabel = new JLabel(card);
            cardLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            playerHandPanel.add(cardLabel);
        }
        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }
}