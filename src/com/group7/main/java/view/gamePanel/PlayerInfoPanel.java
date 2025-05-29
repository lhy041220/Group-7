package view.gamePanel;

import model.Player;

import javax.swing.*;
import java.awt.*;

import java.util.List;

public class PlayerInfoPanel extends JPanel {

    private java.util.Set<String> lastTreasures = new java.util.HashSet<>();

    public PlayerInfoPanel() {
        setPreferredSize(new Dimension(300, 600));
        setBackground(Color.WHITE);  // 临时背景色
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addPlayerInfos();
    }

    /**
     * 添加玩家信息组件
     */
    private void addPlayerInfos() {
        // TODO: 为每个玩家添加信息面板
        add(new JLabel("Player 1"));
        add(new JLabel("Player 2"));
        // ... 添加更多玩家
    }

    public void updatePlayerInfos(List<Player> players, int currentIdx) {
        this.removeAll();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            StringBuilder info = new StringBuilder();
            info.append("Player ").append(p.getPlayerId());
            info.append(" | 职业: ").append(p.getRole());
            info.append(" | 剩余行动: ").append(p.getRemainingActions());
            info.append(" | 手牌: ").append(p.getHand().size());
            if (p.getCurrentTile() != null) {
                info.append(" | 位置: ").append(p.getCurrentTile().getType().getDisplayName());
            }
            JLabel label = new JLabel(info.toString());
            if (i == currentIdx) {
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setForeground(Color.BLUE);
            }
            this.add(label);
            // 展示手牌内容
            JPanel handPanel = new JPanel();
            handPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            for (model.card.Card card : p.getHand()) {
                JLabel cardLabel = new JLabel(card.getName());
                cardLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                handPanel.add(cardLabel);
            }
            this.add(handPanel);
        }
        this.revalidate();
        this.repaint();
    }

    public void updateCollectedTreasures(java.util.Set<model.enums.TreasureType> treasures) {
        lastTreasures.clear();
        for (model.enums.TreasureType t : treasures) {
            if (t != model.enums.TreasureType.NONE) lastTreasures.add(t.getDisplayName());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!lastTreasures.isEmpty()) {
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("已收集宝藏: " + String.join(", ", lastTreasures), 10, 20);
        }
    }

}
