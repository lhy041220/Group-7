package view.gamePanel;

import lombok.Setter;
import model.Tile;
import model.Board;
import model.enums.TreasureType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoardPanel extends JPanel {

    private JPanel[][] tilePanels;
    private final int TILE_SIZE = 70;
    private final int GRID_SIZE = 6;
    private final int GAP = 5;

    // Colors for different tile states
    private final Color NORMAL_COLOR = new Color(144, 238, 144);   // Light green
    private final Color FLOODED_COLOR = new Color(135, 206, 250);  // Light blue
    private final Color SUNKEN_COLOR = new Color(25, 25, 112);     // Dark blue
    private final Color EMPTY_COLOR = new Color(0, 0, 0, 0);       // Transparent (for empty corners)

    private final int HORIZONTAL_MARGIN = 150; // Horizontal margin to center the board

    @Setter
    private TileClickListener tileClickListener;
    public interface TileClickListener {
        void onTileClicked(int row, int col);
    }

    private java.util.Set<String> highlightedTiles = new java.util.HashSet<>();
    public void highlightTiles(java.util.List<int[]> positions) {
        highlightedTiles.clear();
        for (int[] pos : positions) {
            highlightedTiles.add(pos[0] + "," + pos[1]);
        }
        updateBoardHighlight();
    }
    public void clearHighlight() {
        highlightedTiles.clear();
        updateBoardHighlight();
    }
    private void updateBoardHighlight() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JPanel tilePanel = tilePanels[row][col];
                if (highlightedTiles.contains(row + "," + col)) {
                    tilePanel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                } else {
                    tilePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                }
            }
        }
        repaint();
    }

    public enum Mode { NORMAL, MOVE }
    private Mode currentMode = Mode.NORMAL;
    public void setMode(Mode mode) { this.currentMode = mode; }
    public Mode getMode() { return currentMode; }

    public GameBoardPanel() {
        this.tilePanels = new JPanel[GRID_SIZE][GRID_SIZE];

        setLayout(null); // Use absolute layout
        // Panel width increased to accommodate horizontal margin
        setPreferredSize(new Dimension(GRID_SIZE * (TILE_SIZE + GAP) + GAP + HORIZONTAL_MARGIN * 2,
                GRID_SIZE * (TILE_SIZE + GAP) + GAP));
        setBackground(new Color(0, 50, 100)); // Deep blue ocean background
        initializeTilePanels();
    }

    /**
     * Initialize all tile panels
     */
    private void initializeTilePanels() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = HORIZONTAL_MARGIN + GAP + col * (TILE_SIZE + GAP);
                int y = GAP + row * (TILE_SIZE + GAP);

                JPanel tilePanel = new JPanel();
                tilePanel.setLayout(new BorderLayout());
                tilePanel.setBounds(x, y, TILE_SIZE, TILE_SIZE);
                tilePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

                final int finalRow = row;
                final int finalCol = col;
                tilePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (currentMode == Mode.MOVE && tileClickListener != null) {
                            tileClickListener.onTileClicked(finalRow, finalCol);
                            setMode(Mode.NORMAL); // Exit move mode automatically after moving
                        } else if (tileClickListener != null) {
                            tileClickListener.onTileClicked(finalRow, finalCol);
                        }
                    }
                });

                tilePanels[row][col] = tilePanel;
                add(tilePanel);
            }
        }
    }

    /**
     * Update the game board display according to the Board object
     */
    public void updateBoard(Board board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                try {
                    Tile tile = board.getTile(row, col);
                    updateTilePanel(row, col, tile);
                } catch (IllegalArgumentException e) {
                    // Handle empty tiles (corners have no tile)
                    tilePanels[row][col].setBackground(EMPTY_COLOR);
                    tilePanels[row][col].setOpaque(false);
                    tilePanels[row][col].removeAll();
                }
            }
        }
        updateBoardHighlight();
        revalidate();
        repaint();
    }

    /**
     * Update the display for a single tile panel
     */
    private void updateTilePanel(int row, int col, Tile tile) {
        JPanel tilePanel = tilePanels[row][col];
        tilePanel.removeAll();

        if (tile == null) {
            tilePanel.setBackground(EMPTY_COLOR);
            tilePanel.setOpaque(false);
            return;
        }

        // Set color based on tile state
        if (tile.isSunk()) {
            tilePanel.setBackground(SUNKEN_COLOR);
            tilePanel.setOpaque(true);
        } else if (tile.isFlooded()) {
            tilePanel.setBackground(FLOODED_COLOR);
            tilePanel.setOpaque(true);
        } else {
            tilePanel.setBackground(NORMAL_COLOR);
            tilePanel.setOpaque(true);
        }

        // Load and display the tile image
        try {
            String imagePath = "src/com/group7/resources/images/Tiles/" + tile.getType().getImagePath();
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tilePanel.add(imgLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // Ignore if the image fails to load
        }

        // Add the tile type name label
        JLabel nameLabel = new JLabel(tile.getType().getDisplayName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(tile.isSunk() ? Color.WHITE : Color.BLACK); // Use white for sunken tiles
        tilePanel.add(nameLabel, BorderLayout.SOUTH);

        // Add tile state label
        JLabel stateLabel = new JLabel();
        if (tile.isFlooded()) {
            stateLabel.setText("Flooded");
        } else if (tile.isSunk()) {
            stateLabel.setText("Sunken");
        }
        stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stateLabel.setForeground(tile.isSunk() ? Color.WHITE : Color.BLACK);
        tilePanel.add(stateLabel, BorderLayout.NORTH);

        // Show treasure info if present
        if (tile.getTreasure() != null && tile.getTreasure() != TreasureType.NONE) {
            JLabel treasureLabel = new JLabel(tile.getTreasure().toString());
            treasureLabel.setHorizontalAlignment(SwingConstants.CENTER);
            treasureLabel.setForeground(Color.RED);
            tilePanel.add(treasureLabel, BorderLayout.WEST);
        }

        // Show players on this tile
        java.util.List<model.Player> players = tile.getPlayersOnTile();
        if (players != null && !players.isEmpty()) {
            JPanel playerPanel = new JPanel();
            playerPanel.setOpaque(false);
            playerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};
            for (model.Player p : players) {
                JLabel playerDot = new JLabel("●");
                playerDot.setForeground(colors[(p.getPlayerId() - 1) % colors.length]);
                playerDot.setToolTipText("Player " + p.getPlayerId());
                playerPanel.add(playerDot);
            }
            tilePanel.add(playerPanel, BorderLayout.EAST);
        }
    }

    /**
     * Get the tile panel at a specific position
     */
    public JPanel getTilePanel(int row, int col) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            return tilePanels[row][col];
        }
        return null;
    }

    public boolean isTileHighlighted(int row, int col) {
        return highlightedTiles.contains(row + "," + col);
    }
}

