package view.gamePanel;

import model.Player;
import model.enums.Role;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlayerInfoPanel extends JPanel {
    private static final Color ACTIVE_PLAYER_COLOR = new Color(230, 230, 250); // 淡紫色背景
    private static final int PLAYER_PANEL_HEIGHT = 120; // 增加玩家面板高度以容纳更多信息

    public PlayerInfoPanel() {
        setPreferredSize(new Dimension(250, 600));
        setBackground(new Color(245, 245, 245)); // 浅灰色背景
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 添加标题
        JLabel titleLabel = new JLabel("玩家信息");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(10));
    }

    public void updatePlayerInfos(List<Player> players, int currentIdx) {
        // 保留标题
        Component titleLabel = getComponent(0);
        Component strut = getComponent(1);
        removeAll();
        add(titleLabel);
        add(strut);

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            boolean isCurrentPlayer = (i == currentIdx);

            // 创建玩家信息面板
            JPanel playerPanel = createPlayerInfoPanel(p, isCurrentPlayer);
            add(playerPanel);
            add(Box.createVerticalStrut(10)); // 面板之间的间距
        }

        revalidate();
        repaint();
    }

    private JPanel createPlayerInfoPanel(Player player, boolean isCurrentPlayer) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(isCurrentPlayer ? ACTIVE_PLAYER_COLOR : Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, PLAYER_PANEL_HEIGHT));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 玩家ID和当前状态
        JLabel nameLabel = new JLabel(String.format("玩家 %d %s",
                player.getPlayerId(),
                isCurrentPlayer ? "（行动中）" : ""));
        nameLabel.setFont(new Font("微软雅黑", isCurrentPlayer ? Font.BOLD : Font.PLAIN, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 角色信息
        JLabel roleLabel = new JLabel(String.format("角色：%s",
                player.getRole() != null ? player.getRole().getDisplayName() : "未分配"));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 当前位置
        JLabel locationLabel = new JLabel(String.format("位置：%s",
                player.getCurrentTile() != null ? player.getCurrentTile().getType().getDisplayName() : "未知"));
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 剩余行动点数（仅显示当前玩家的）
        if (isCurrentPlayer) {
            JLabel actionLabel = new JLabel(String.format("剩余行动点：%d", player.getRemainingActions()));
            actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            actionLabel.setForeground(new Color(0, 100, 0)); // 深绿色
            panel.add(actionLabel);

        }
        // 手牌数量
        JLabel handLabel = new JLabel(String.format("手牌数量：%d", player.getHand().size()));
        handLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 添加所有标签
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(roleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(locationLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(handLabel);

        return panel;
    }
}
