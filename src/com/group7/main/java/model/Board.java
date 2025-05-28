package model;

import model.enums.TileType;
import model.enums.TreasureType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 代表禁闭岛游戏板
 */
public class Board {
    private Tile[][] tileTable; // 二维数组表示游戏板
    private final int rows = 6; // 行数
    private final int cols = 6; // 列数
    private List<Tile> floodedTiles; // 已经被淹没的Tile
    private List<Tile> sunkTiles; // 已经沉没的Tile
    private Random random;

    /**
     * 构造函数，初始化游戏板
     */
    public Board() {
        tileTable = new Tile[rows][cols];
        floodedTiles = new ArrayList<>();
        sunkTiles = new ArrayList<>();
        random = new Random();
        initializeBoard();
    }

    /**
     * 初始化游戏板，创建所有的Tile
     */
    private void initializeBoard() {
        // 创建不同的Tile
        List<TileType> tileTypeList = Arrays.asList(TileType.values());

        // 随机打乱列表
        for (int i = tileTypeList.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            TileType temp = tileTypeList.get(i);
            tileTypeList.set(i, tileTypeList.get(j));
            tileTypeList.set(j, temp);
        }

        // 创建36个Tile (6x6)
        int tileIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // 跳过四个角
                if (isAdjacentTo(row, col, 0, 0) || isAdjacentTo(row, col, 0, cols - 1) ||
                        isAdjacentTo(row, col, rows - 1, 0) || isAdjacentTo(row, col, rows - 1, cols - 1)) {
                    tileTable[row][col] = null;
                } else {
                    TileType tileType = tileTypeList.get(tileIndex++);
                    tileTable[row][col] = Tile.createTileForType(tileType, row, col);
                }
            }
        }
    }

    /**
     * 判断两个Tile是否相邻，同一个点会被认作是相邻的
     */
    private static boolean isAdjacentTo(int row1, int col1, int row2, int col2) {
        int rowDiff = Math.abs(row1 - row2);
        int colDiff = Math.abs(col1 - col2);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1) || (rowDiff == 0 && colDiff == 0);
    }

    // 检查愚者着陆点是否沉没
    public boolean isHelipadSunk() {
        Tile helipad = getTileByType(TileType.FOOLS_LANDING);
        return helipad == null || helipad.isSunk();
    }

    // 检查某个宝藏的两块板是否全部沉没
    public boolean isAllTreasureTilesSunk(TreasureType treasureType) {
        int total = 0, sunk = 0;
        for (Tile tile : getAllTiles()) {
            if (tile.getTreasure() == treasureType) {
                total++;
                if (tile.isSunk()) sunk++;
            }
        }
        // 只要两块全沉即可，通常有两块
        return total > 0 && sunk == total;
    }

    /**
     * 根据坐标获取Tile
     */
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IllegalArgumentException("错误的tile的坐标");
        }
        return tileTable[row][col];
    }

    /**
     * 获取特定名称的Tile
     */
    public Tile getTileByType(TileType tileType) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (tileTable[row][col] != null && tileTable[row][col].getType() == tileType) {
                    return tileTable[row][col];
                }
            }
        }
        return null;
    }

    /**
     * 获取所有非null的Tile
     */
    public List<Tile> getAllTiles() {
        List<Tile> allTiles = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (tileTable[row][col] != null) {
                    allTiles.add(tileTable[row][col]);
                }
            }
        }
        return allTiles;
    }

    /**
     * 获取所有已经被淹没但未沉没的Tile
     */
    public List<Tile> getFloodedTiles() {
        return new ArrayList<>(floodedTiles);
    }

    /**
     * 获取所有已经沉没的Tile
     */
    public List<Tile> getSunkTiles() {
        return new ArrayList<>(sunkTiles);
    }

    /**
     * 淹没一个Tile
     */
    public void floodTile(Tile tile) {
        if (tile != null && !tile.isFlooded() && !tile.isSunk()) {
            tile.flood();
            floodedTiles.add(tile);
        }
    }

    /**
     * 使一个Tile沉没
     */
    public void sinkTile(Tile tile) {
        if (tile != null && tile.isFlooded()) {
            tile.sink();
            floodedTiles.remove(tile);
            sunkTiles.add(tile);
            // 在二维数组中将此Tile设置为null（沉没的tile不会被恢复）
            // tileTable[tile.getRow()][tile.getCol()] = null;
        }
    }

    /**
     * 使一个已经被淹没的Tile恢复正常
     */
    public void dryTile(Tile tile) {
        if (tile != null && tile.isFlooded()) {
            tile.shoreUp();
            floodedTiles.remove(tile);
        }
    }

    /**
     * 获取一个Tile的相邻Tile（上下左右）
     */
    public List<Tile> getAdjacentTiles(Tile tile) {
        List<Tile> adjacentTiles = new ArrayList<>();
        int row = tile.getRow();
        int col = tile.getCol();

        // 上
        if (row > 0 && tileTable[row-1][col] != null) {
            adjacentTiles.add(tileTable[row-1][col]);
        }
        // 下
        if (row < rows-1 && tileTable[row+1][col] != null) {
            adjacentTiles.add(tileTable[row+1][col]);
        }
        // 左
        if (col > 0 && tileTable[row][col-1] != null) {
            adjacentTiles.add(tileTable[row][col-1]);
        }
        // 右
        if (col < cols-1 && tileTable[row][col+1] != null) {
            adjacentTiles.add(tileTable[row][col+1]);
        }

        return adjacentTiles;
    }

    /**
     * 检查游戏是否结束（特定的重要Tile是否沉没）
     */
    public boolean isGameOver() {
        // 这里可以实现检查游戏结束的逻辑
        // 例如，如果"愚者宝藏"Tile已经沉没，游戏结束
        throw new UnsupportedOperationException("Board类的方法isGameOver()没有实现");
    }

    /**
     * 打印游戏板的当前状态（用于调试）
     */
    public void printBoard() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (tileTable[row][col] == null) {
                    System.out.print("   ");
                } else if (tileTable[row][col].isSunk()) {
                    System.out.print("X  ");
                } else if (tileTable[row][col].isFlooded()) {
                    System.out.print("~  ");
                } else {
                    System.out.print("O  ");
                }
            }
            System.out.println();
        }
    }
}

