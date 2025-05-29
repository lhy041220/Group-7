package view.dialog;

import model.*;
import model.card.TreasureCard;
import model.enums.Role;

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
        super(parent, "角色特殊能力", true);
        this.currentPlayer = currentPlayer;
        this.game = Game.getInstance();
        this.actionListener = listener;

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        // 根据不同角色显示不同的交互界面
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
                // 其他角色不需要特殊交互
                dispose();
                return;
        }

        add(mainPanel);
        pack();
    }

    private void setupEngineerPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("选择第二个要排水的板块：");
        panel.add(label, BorderLayout.NORTH);

        // 获取当前可以排水的板块
        List<Tile> floodedTiles = game.getBoard().getFloodedTiles();
        JList<Tile> tileList = new JList<>(floodedTiles.toArray(new Tile[0]));
        tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tileList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");

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

        // 第一步：选择要移动的玩家
        JPanel playerSelectionPanel = new JPanel(new BorderLayout());
        JLabel playerLabel = new JLabel("选择要移动的玩家：");
        List<Player> otherPlayers = game.getPlayers().stream()
                .filter(p -> p != currentPlayer)
                .toList();

        JComboBox<Player> playerComboBox = new JComboBox<>(otherPlayers.toArray(new Player[0]));
        playerSelectionPanel.add(playerLabel, BorderLayout.NORTH);
        playerSelectionPanel.add(playerComboBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");

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

        // 第一步：选择目标玩家
        JPanel playerSelectionPanel = new JPanel(new BorderLayout());
        JLabel playerLabel = new JLabel("选择要给予卡牌的玩家：");
        List<Player> otherPlayers = game.getPlayers().stream()
                .filter(p -> p != currentPlayer)
                .toList();

        JComboBox<Player> playerComboBox = new JComboBox<>(otherPlayers.toArray(new Player[0]));
        playerSelectionPanel.add(playerLabel, BorderLayout.NORTH);
        playerSelectionPanel.add(playerComboBox, BorderLayout.CENTER);

        // 第二步：选择要给出的宝藏卡
        JPanel cardSelectionPanel = new JPanel(new BorderLayout());
        JLabel cardLabel = new JLabel("选择要给出的宝藏卡：");
        List<TreasureCard> treasureCards = currentPlayer.getGiveableTreasureCards();
        JList<TreasureCard> cardList = new JList<>(treasureCards.toArray(new TreasureCard[0]));
        cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cardSelectionPanel.add(cardLabel, BorderLayout.NORTH);
        cardSelectionPanel.add(new JScrollPane(cardList), BorderLayout.CENTER);

        JPanel selectionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        selectionPanel.add(playerSelectionPanel);
        selectionPanel.add(cardSelectionPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");

        confirmButton.addActionListener(e -> {
            Player selectedPlayer = (Player) playerComboBox.getSelectedItem();
            TreasureCard selectedCard = cardList.getSelectedValue();
            if (selectedPlayer != null && selectedCard != null) {
                // 先通知选择的玩家
                actionListener.onPlayerSelected(selectedPlayer);
                // 再通知选择的卡牌
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