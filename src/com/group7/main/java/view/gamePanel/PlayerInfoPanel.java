package view.gamePanel;

import model.Player;

import javax.swing.*;
import java.awt.*;

import java.util.List;

public class PlayerInfoPanel extends JPanel {

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
            JLabel label = new JLabel("Player " + p.getPlayerId()
                    + ((i == currentIdx) ? "（行动中）" : ""));
            if (i == currentIdx) label.setFont(label.getFont().deriveFont(Font.BOLD));
            this.add(label);
        }
        this.revalidate();
        this.repaint();
    }

}
