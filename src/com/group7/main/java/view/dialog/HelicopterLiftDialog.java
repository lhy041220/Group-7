package view.dialog;

import model.Player;
import model.Tile;
import model.Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HelicopterLiftDialog extends JDialog {
    private Game game;
    private Player currentPlayer;
    private List<JCheckBox> playerCheckBoxes;
    private JList<Tile> tileList;
    private ActionListener actionListener;

    public interface ActionListener {
        void onPlayersSelected(List<Player> selectedPlayers, Tile destinationTile);
        void onActionCancelled();
    }

    public HelicopterLiftDialog(JFrame parent, Player currentPlayer, ActionListener listener) {
        super(parent, "Helicopter Rescue", true);
        this.game = Game.getInstance();
        this.currentPlayer = currentPlayer;
        this.actionListener = listener;
        this.playerCheckBoxes = new ArrayList<>();

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Player selection panel
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBorder(BorderFactory.createTitledBorder("Select players to move"));

        // Add checkboxes for all players
        for (Player p : game.getPlayers()) {
            JCheckBox checkBox = new JCheckBox(String.format("Player %d (%s)",
                    p.getPlayerId(), p.getRole().getDisplayName()));
            checkBox.setSelected(p == currentPlayer); // By default, select the current player
            playerCheckBoxes.add(checkBox);
            playerPanel.add(checkBox);
        }

        // Tile selection panel
        JPanel tilePanel = new JPanel(new BorderLayout(5, 5));
        tilePanel.setBorder(BorderFactory.createTitledBorder("Select destination tile"));

        // Get all navigable tiles
        List<Tile> navigableTiles = game.getBoard().getAllNavigableTiles();
        tileList = new JList<>(navigableTiles.toArray(new Tile[0]));
        tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tileList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tile) {
                    Tile tile = (Tile) value;
                    setText(tile.getType().getDisplayName());
                }
                return this;
            }
        });

        tilePanel.add(new JScrollPane(tileList), BorderLayout.CENTER);

        mainPanel.add(playerPanel);
        mainPanel.add(tilePanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            Tile selectedTile = tileList.getSelectedValue();
            if (selectedTile != null) {
                List<Player> selectedPlayers = new ArrayList<>();
                for (int i = 0; i < playerCheckBoxes.size(); i++) {
                    if (playerCheckBoxes.get(i).isSelected()) {
                        selectedPlayers.add(game.getPlayers().get(i));
                    }
                }
                if (!selectedPlayers.isEmpty()) {
                    actionListener.onPlayersSelected(selectedPlayers, selectedTile);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Please select at least one player.",
                            "Notice",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a destination tile.",
                        "Notice",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            actionListener.onActionCancelled();
            dispose();
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog size
        setSize(400, 500);
    }

    public boolean isPlayerSelected(Player player) {
        int index = game.getPlayers().indexOf(player);
        return index >= 0 && index < playerCheckBoxes.size() &&
                playerCheckBoxes.get(index).isSelected();
    }
}
