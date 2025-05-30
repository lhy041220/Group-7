package view.gamePanel;

import model.Player;
import model.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TileButton extends JButton {
    private Tile tile;
    // Player colors: player 1 (red), player 2 (blue), player 3 (green), player 4 (yellow)
    private static final Color[] PLAYER_COLORS = {
            new Color(255, 0, 0, 180),    // Player 1: Red
            new Color(0, 0, 255, 180),    // Player 2: Blue
            new Color(0, 255, 0, 180),    // Player 3: Green
            new Color(255, 255, 0, 180)   // Player 4: Yellow
    };

    public TileButton(Tile tile) {
        this.tile = tile;
        setPreferredSize(new Dimension(100, 100));
        updateAppearance();
    }

    public void updateAppearance() {
        // Set the basic background color
        if (tile.isSunk()) {
            setBackground(Color.DARK_GRAY);
        } else if (tile.isFlooded()) {
            setBackground(new Color(135, 206, 235)); // Light blue
        } else {
            setBackground(new Color(144, 238, 144)); // Light green
        }

        // Set tile name
        setText("<html><center>" + tile.getType().getDisplayName() + "</center></html>");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Get the players on this tile
        List<Player> playersOnTile = tile.getPlayersOnTile();
        if (playersOnTile != null && !playersOnTile.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g;
            int size = 20;    // Size of player marker
            int spacing = 5;  // Spacing between player markers

            // Show player markers at four corners of the tile
            for (int i = 0; i < playersOnTile.size(); i++) {
                Player player = playersOnTile.get(i);
                g2d.setColor(PLAYER_COLORS[player.getPlayerId() - 1]);

                // Decide position based on player index
                int x = (i % 2 == 0) ? spacing : getWidth() - size - spacing;
                int y = (i < 2) ? spacing : getHeight() - size - spacing;

                g2d.fillOval(x, y, size, size);

                // Draw the player number in the center of the circle
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
