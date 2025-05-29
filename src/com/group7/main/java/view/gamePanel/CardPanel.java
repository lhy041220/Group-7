package view.gamePanel;

import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {

    private JPanel treasureCardPanel;
    private JPanel floodCardPanel;
    private JPanel playerHandPanel;

    // 卡牌显示相关的常量
    private static final Color TREASURE_DECK_COLOR = new Color(255, 215, 0, 180); // 金色，半透明
    private static final Color FLOOD_DECK_COLOR = new Color(0, 191, 255, 180);   // 深天蓝色，半透明
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;
    private static final int CARD_SPACING = 10;

    public CardPanel() {
        setPreferredSize(new Dimension(0, 150));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(new Color(245, 245, 245)); // 浅灰色背景

        initComponents();
        addComponents();
    }

    /**
     * 初始化卡牌相关组件
     */
    private void initComponents() {
        // 宝藏牌堆面板
        treasureCardPanel = new JPanel(new BorderLayout());
        treasureCardPanel.setBackground(TREASURE_DECK_COLOR);
        treasureCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32), 2), // 暗金色边框
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel treasureLabel = new JLabel("宝藏牌堆");
        treasureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        treasureLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        treasureCardPanel.add(treasureLabel, BorderLayout.NORTH);

        // 洪水牌堆面板
        floodCardPanel = new JPanel(new BorderLayout());
        floodCardPanel.setBackground(FLOOD_DECK_COLOR);
        floodCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2), // 钢蓝色边框
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel floodLabel = new JLabel("洪水牌堆");
        floodLabel.setHorizontalAlignment(SwingConstants.CENTER);
        floodLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        floodCardPanel.add(floodLabel, BorderLayout.NORTH);

        // 玩家手牌面板
        playerHandPanel = new JPanel();
        playerHandPanel.setLayout(new FlowLayout(FlowLayout.CENTER, CARD_SPACING, 5));
        playerHandPanel.setBackground(new Color(245, 245, 245));
        playerHandPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "玩家手牌",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 12)
        ));
    }

    /**
     * 添加卡牌相关组件到面板
     */
    private void addComponents() {
        // 创建牌堆面板
        JPanel deckPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        deckPanel.setOpaque(false);

        // 添加牌堆
        deckPanel.add(treasureCardPanel);
        deckPanel.add(floodCardPanel);

        // 将牌堆面板和手牌面板添加到主面板
        add(deckPanel, BorderLayout.NORTH);
        add(playerHandPanel, BorderLayout.CENTER);
    }

    /**
     * 更新玩家手牌显示
     * @param cards 玩家手中的卡牌
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
     * 创建单个卡牌组件
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

        // 卡牌名称标签
        JLabel nameLabel = new JLabel("<html><center>" + cardName + "</center></html>");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cardPanel.add(nameLabel, BorderLayout.CENTER);

        return cardPanel;
    }
}