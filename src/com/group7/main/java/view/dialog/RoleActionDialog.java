package view.dialog;

import model.*;
import model.card.TreasureCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoleActionDialog extends JDialog {
    private Player currentPlayer;
    private Game game;
    private JPanel mainPanel;
    private ActionListener actionListener;

    public interface ActionListener {
        void onTileSelected(Tile tile);
        void onPlayerSelected(Player targetPlayer);
        void onCardSelected(TreasureCard card);
        void onActionCancelled();
    }

    public RoleActionDialog(JFrame parent, Player currentPlayer, ActionListener listener) {
        super(parent, "Role Special Ability", true);
        this.currentPlayer = currentPlayer;
        this.game = Game.getInstance();
        this.actionListener = listener;

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        // Show the appropriate UI depending on the role's special action
        switch (currentPlayer.getRole()) {
            case ENGINEER:
                setupEngineerPanel();
                break;
            case NAVIGATOR:
                setupNavigatorPanel();
                break;
            case MESSENGER:
                setupMessengerPanel();
                break;
            default:
                // Other roles do not require a special interaction dialog
                dispose();
                return;
        }

        add(mainPanel);
        pack();
    }

    private void setupEngineerPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Select the second tile to shore up:");
        panel.add(label, BorderLayout.NORTH);

        // Get available flooded tiles
        List<Tile> floodedTiles = game.getBoard().getFloodedTiles();
        JList<Tile> tileList = new JList<>(floodedTiles.toArray(new Tile[0]));
        tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tileList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            Tile selectedTile = tileList.getSelectedValue();
            if (selectedTile != null) {
                actionListener.onTileSelected(selectedTile);
            }
            dispose();
        });

        cancelButton.addActionListener(e -> {
            actionListener.onActionCancelled();
            dispose();
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel);
    }

    private void setupNavigatorPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Step 1: Select the player to move
        JPanel playerSelectionPanel = new JPanel(new BorderLayout());
        JLabel playerLabel = new JLabel("Select a player to move:");
        List<Player> otherPlayers = game.getPlayers().stream()
                .filter(p -> p != currentPlayer)
                .toList();

        JComboBox<Player> playerComboBox = new JComboBox<>(otherPlayers.toArray(new Player[0]));
        playerSelectionPanel.add(playerLabel, BorderLayout.NORTH);
        playerSelectionPanel.add(playerComboBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            Player selectedPlayer = (Player) playerComboBox.getSelectedItem();
            if (selectedPlayer != null) {
                actionListener.onPlayerSelected(selectedPlayer);
            }
            dispose();
        });

        cancelButton.addActionListener(e -> {
            actionListener.onActionCancelled();
            dispose();
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        panel.add(playerSelectionPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel);
    }

    private void setupMessengerPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Step 1: Select the recipient player
        JPanel playerSelectionPanel = new JPanel(new BorderLayout());
        JLabel playerLabel = new JLabel("Select the player to give a card to:");
        List<Player> otherPlayers = game.getPlayers().stream()
                .filter(p -> p != currentPlayer)
                .toList();

        JComboBox<Player> playerComboBox = new JComboBox<>(otherPlayers.toArray(new Player[0]));
        playerSelectionPanel.add(playerLabel, BorderLayout.NORTH);
        playerSelectionPanel.add(playerComboBox, BorderLayout.CENTER);

        // Step 2: Select a treasure card to give
        JPanel cardSelectionPanel = new JPanel(new BorderLayout());
        JLabel cardLabel = new JLabel("Select the treasure card to give:");
        List<TreasureCard> treasureCards = currentPlayer.getGiveableTreasureCards();
        JList<TreasureCard> cardList = new JList<>(treasureCards.toArray(new TreasureCard[0]));
        cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cardSelectionPanel.add(cardLabel, BorderLayout.NORTH);
        cardSelectionPanel.add(new JScrollPane(cardList), BorderLayout.CENTER);

        JPanel selectionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        selectionPanel.add(playerSelectionPanel);
        selectionPanel.add(cardSelectionPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            Player selectedPlayer = (Player) playerComboBox.getSelectedItem();
            TreasureCard selectedCard = cardList.getSelectedValue();
            if (selectedPlayer != null && selectedCard != null) {
                // First notify selected player
                actionListener.onPlayerSelected(selectedPlayer);
                // Then notify selected card
                actionListener.onCardSelected(selectedCard);
            }
            dispose();
        });

        cancelButton.addActionListener(e -> {
            actionListener.onActionCancelled();
            dispose();
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        panel.add(selectionPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel);
    }
}
