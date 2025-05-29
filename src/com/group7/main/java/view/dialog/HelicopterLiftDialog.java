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
        super(parent, "直升机救援", true);
        this.game = Game.getInstance();
        this.currentPlayer = currentPlayer;
        this.actionListener = listener;
        this.playerCheckBoxes = new ArrayList<>();

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 创建主面板
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 玩家选择面板
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBorder(BorderFactory.createTitledBorder("选择要移动的玩家"));

        // 添加所有玩家的复选框
        for (Player p : game.getPlayers()) {
            JCheckBox checkBox = new JCheckBox(String.format("玩家 %d (%s)",
                    p.getPlayerId(), p.getRole().getDisplayName()));
            checkBox.setSelected(p == currentPlayer); // 默认选中当前玩家
            playerCheckBoxes.add(checkBox);
            playerPanel.add(checkBox);
        }

        // 目标板块选择面板
        JPanel tilePanel = new JPanel(new BorderLayout(5, 5));
        tilePanel.setBorder(BorderFactory.createTitledBorder("选择目标板块"));

        // 获取所有可通行的板块
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

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认");
        JButton cancelButton = new JButton("取消");

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
                            "请至少选择一个玩家",
                            "提示",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "请选择目标板块",
                        "提示",
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

        // 设置对话框大小
        setSize(400, 500);
    }

    public boolean isPlayerSelected(Player player) {
        int index = game.getPlayers().indexOf(player);
        return index >= 0 && index < playerCheckBoxes.size() &&
                playerCheckBoxes.get(index).isSelected();
    }
}