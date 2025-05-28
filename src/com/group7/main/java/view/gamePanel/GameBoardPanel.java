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
    private final int TILE_SIZE = 80;
    private final int GRID_SIZE = 6;
    private final int GAP = 10;
    private final int PANEL_WIDTH = GRID_SIZE * (TILE_SIZE + GAP) + GAP;
    private final int PANEL_HEIGHT = GRID_SIZE * (TILE_SIZE + GAP) + GAP;

    // 定义不同状态的颜色
    private final Color NORMAL_COLOR = new Color(144, 238, 144); // 浅绿色
    private final Color FLOODED_COLOR = new Color(135, 206, 250); // 浅蓝色
    private final Color SUNKEN_COLOR = new Color(25, 25, 112);    // 深蓝色
    private final Color EMPTY_COLOR = new Color(0, 0, 0, 0);      // 透明（用于角落的空位）


    @Setter
    private TileClickListener tileClickListener;
    public interface TileClickListener {
        void onTileClicked(int row, int col);
    }

    public GameBoardPanel() {
        this.tilePanels = new JPanel[GRID_SIZE][GRID_SIZE];
        setLayout(null); // 使用绝对布局
        // 使用GridBagLayout来实现灵活的布局
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(0, 50, 100)); // 深蓝色背景代表海洋
        initializeTilePanels();
    }

    /**
     * 初始化所有瓦片面板
     */
    private void initializeTilePanels() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JPanel tilePanel = new JPanel(new BorderLayout());
                tilePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

                final int finalRow = row;
                final int finalCol = col;
                tilePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (tileClickListener != null) {
                            tileClickListener.onTileClicked(finalRow, finalCol);
                        }
                    }
                });

                tilePanels[row][col] = tilePanel;

                // 计算面板位置
                int x = GAP + col * (TILE_SIZE + GAP);
                int y = GAP + row * (TILE_SIZE + GAP);

                // 根据行数调整水平位置
                if (row == 0 || row == 5) { // 第一行和最后一行只有中间两个板块
                    if (col < 2 || col > 3) { // 不是中间两列的不显示
                        tilePanel.setVisible(false);
                    }
                } else if (row == 1 || row == 4) { // 第二行和倒数第二行有四个板块
                    if (col < 1 || col > 4) { // 最边上的不显示
                        tilePanel.setVisible(false);
                    }
                }

                tilePanel.setBounds(x, y, TILE_SIZE, TILE_SIZE);
                add(tilePanel);
            }
        }
    }

    /**
     * 根据Board对象更新游戏板显示
     */
    public void updateBoard(Board board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JPanel tilePanel = tilePanels[row][col];

                // 检查是否是应该显示的位置
                boolean shouldBeVisible = true;
                if (row == 0 || row == 5) { // 第一行和最后一行
                    shouldBeVisible = (col == 2 || col == 3);
                } else if (row == 1 || row == 4) { // 第二行和倒数第二行
                    shouldBeVisible = (col >= 1 && col <= 4);
                }

                if (!shouldBeVisible) {
                    tilePanel.setVisible(false);
                    continue;
                }

                try {
                    Tile tile = board.getTile(row, col);
                    updateTilePanel(row, col, tile);
                    tilePanel.setVisible(true);
                } catch (IllegalArgumentException e) {
                    tilePanel.setVisible(false);
                }
            }
        }
        revalidate();
        repaint();
    }

    /**
     * 更新单个瓦片面板的显示
     */
    private void updateTilePanel(int row, int col, Tile tile) {
        JPanel tilePanel = tilePanels[row][col];
        tilePanel.removeAll();

        if (tile == null) {
            tilePanel.setBackground(EMPTY_COLOR);
            tilePanel.setOpaque(false);
            tilePanel.setVisible(false);
            return;
        }

        // 根据瓦片状态设置颜色
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

        // 添加瓦片类型名称标签
        JLabel nameLabel = new JLabel(tile.getType().getDisplayName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(tile.isSunk() ? Color.WHITE : Color.BLACK);
        tilePanel.add(nameLabel, BorderLayout.CENTER);

        // 添加瓦片状态标签
        JLabel stateLabel = new JLabel();
        if (tile.isFlooded()) {
            stateLabel.setText("已淹没");
        } else if (tile.isSunk()) {
            stateLabel.setText("已沉没");
        }
        stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stateLabel.setForeground(tile.isSunk() ? Color.WHITE : Color.BLACK);
        tilePanel.add(stateLabel, BorderLayout.SOUTH);

        // 如果有宝藏类型且不是NONE，显示宝藏信息
        if (tile.getTreasure() != null && tile.getTreasure() != TreasureType.NONE) {
            JLabel treasureLabel = new JLabel(tile.getTreasure().getDisplayName());
            treasureLabel.setHorizontalAlignment(SwingConstants.CENTER);
            treasureLabel.setForeground(Color.RED);
            tilePanel.add(treasureLabel, BorderLayout.NORTH);
        }
    }

    /**
     * 获取特定位置的瓦片面板
     */
    public JPanel getTilePanel(int row, int col) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            return tilePanels[row][col];
        }
        return null;
    }
}
