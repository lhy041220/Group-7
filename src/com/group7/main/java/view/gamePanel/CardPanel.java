package view.gamePanel;

import javax.swing.*;
import java.awt.*;
import model.card.Card;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import model.card.SpecialCard;

public class CardPanel extends JPanel {

    private JPanel treasureCardPanel;
    private JPanel floodCardPanel;
    private JPanel playerHandPanel;
    private MainFrame mainFrame;

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

    public void setMainFrame(MainFrame mainFrame) { this.mainFrame = mainFrame; }

    /**
     * 更新玩家手牌显示（新版，支持Card对象列表）
     */
    public void updatePlayerHand(List<Card> cards) {
        playerHandPanel.removeAll();
        for (Card card : cards) {
            JLabel cardLabel = new JLabel(card.getName() + " [" + card.getType() + "]");
            cardLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            // 支持点击手牌
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (card instanceof SpecialCard && mainFrame != null) {
                        mainFrame.handleSpecialCardClick((SpecialCard) card);
                    } else {
                        System.out.println("点击了手牌：" + card.getName());
                    }
                }
            });
            playerHandPanel.add(cardLabel);
        }
        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }
}