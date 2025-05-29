package view.gamePanel;

import model.Player;
import model.Tile;
import model.enums.TileType;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TileButton extends JButton {
    private Tile tile;
    private static final Color[] PLAYER_COLORS = {
            new Color(255, 0, 0, 180),    // 玩家1：红色
            new Color(0, 0, 255, 180),    // 玩家2：蓝色
            new Color(0, 255, 0, 180),    // 玩家3：绿色
            new Color(255, 255, 0, 180)   // 玩家4：黄色
    };

    public TileButton(Tile tile) {
        this.tile = tile;
        setPreferredSize(new Dimension(100, 100));
        updateAppearance();
    }

    public void updateAppearance() {
        // 设置基本背景色
        if (tile.isSunk()) {
            setBackground(Color.DARK_GRAY);
        } else if (tile.isFlooded()) {
            setBackground(new Color(135, 206, 235)); // 浅蓝色
        } else {
            setBackground(new Color(144, 238, 144)); // 浅绿色
        }

        // 设置板块名称
        setText("<html><center>" + tile.getType().getDisplayName() + "</center></html>");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 获取在此板块上的玩家
        List<Player> playersOnTile = tile.getPlayersOnTile();
        if (playersOnTile != null && !playersOnTile.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            int size = 20; // 玩家标记的大小
            int spacing = 5; // 玩家标记之间的间距

            // 在板块四个角落显示玩家标记
            for (int i = 0; i < playersOnTile.size(); i++) {
                Player player = playersOnTile.get(i);
                g2d.setColor(PLAYER_COLORS[player.getPlayerId() - 1]);

                // 根据玩家索引决定位置
                int x = (i % 2 == 0) ? spacing : getWidth() - size - spacing;
                int y = (i < 2) ? spacing : getHeight() - size - spacing;

                g2d.fillOval(x, y, size, size);

                // 在圆圈中间显示玩家编号
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String playerNum = String.valueOf(player.getPlayerId());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (size - fm.stringWidth(playerNum)) / 2;
                int textY = y + ((size + fm.getAscent()) / 2);
                g2d.drawString(playerNum, textX, textY);
            }
        }
    }

    public Tile getTile() {
        return tile;
    }
}