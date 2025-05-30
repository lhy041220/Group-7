package view.gamePanel;

import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlayerInfoPanel extends JPanel {
    private static final Color ACTIVE_PLAYER_COLOR = new Color(230, 230, 250); // Light purple for active player
    private static final int PLAYER_PANEL_HEIGHT = 120; // Height increased to fit more info

    public PlayerInfoPanel() {
        setPreferredSize(new Dimension(250, 600));
        setBackground(new Color(245, 245, 245)); // Light gray background
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title
        JLabel titleLabel = new JLabel("Player Info");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(10));
    }

    public void updatePlayerInfos(List<Player> players, int currentIdx) {
        // Keep the title at the top
        Component titleLabel = getComponent(0);
        Component strut = getComponent(1);
        removeAll();
        add(titleLabel);
        add(strut);

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            boolean isCurrentPlayer = (i == currentIdx);

            // Create player info panel
            JPanel playerPanel = createPlayerInfoPanel(p, isCurrentPlayer);
            add(playerPanel);
            add(Box.createVerticalStrut(10));
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

        // Player ID and current turn status
        JLabel nameLabel = new JLabel(String.format("Player %d %s",
                player.getPlayerId(),
                isCurrentPlayer ? "(Current Turn)" : ""));
        nameLabel.setFont(new Font("Arial", isCurrentPlayer ? Font.BOLD : Font.PLAIN, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Role information
        JLabel roleLabel = new JLabel(String.format("Role: %s",
                player.getRole() != null ? player.getRole().getDisplayName() : "Unassigned"));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Current location
        JLabel locationLabel = new JLabel(String.format("Location: %s",
                player.getCurrentTile() != null ? player.getCurrentTile().getType().getDisplayName() : "Unknown"));
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Remaining actions (only for current player)
        if (isCurrentPlayer) {
            JLabel actionLabel = new JLabel(String.format("Remaining Actions: %d", player.getRemainingActions()));
            actionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            actionLabel.setForeground(new Color(0, 100, 0)); // Dark green
            panel.add(actionLabel);
        }

        // Hand size
        JLabel handLabel = new JLabel(String.format("Hand Size: %d", player.getHand().size()));
        handLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add all labels
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
