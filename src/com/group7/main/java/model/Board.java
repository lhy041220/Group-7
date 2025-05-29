package model;

import model.enums.TileType;
import model.enums.TreasureType;
import model.card.Card;
import model.card.TreasureCard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;

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
        //初始化所有位置为null
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tileTable[i][j] = null;
            }
        }

        // 创建特定位置的板块，并设置宝藏位置
        // 第一行（2个板块）
        tileTable[0][2] = Tile.createTileForType(TileType.WHISPERING_GARDEN, 0, 2, TreasureType.WIND);
        tileTable[0][3] = Tile.createTileForType(TileType.HOWLING_GARDEN, 0, 3);

        // 第二行（4个板块）
        tileTable[1][1] = Tile.createTileForType(TileType.TIDAL_PALACE, 1, 1, TreasureType.OCEAN);
        tileTable[1][2] = Tile.createTileForType(TileType.LOST_LAGOON, 1, 2);
        tileTable[1][3] = Tile.createTileForType(TileType.PHANTOM_ROCK, 1, 3);
        tileTable[1][4] = Tile.createTileForType(TileType.SILVER_GATE, 1, 4);

        // 第三行（6个板块）
        tileTable[2][0] = Tile.createTileForType(TileType.CRIMSON_FOREST, 2, 0, TreasureType.FIRE);
        tileTable[2][1] = Tile.createTileForType(TileType.DUNES_OF_DECEPTION, 2, 1);
        tileTable[2][2] = Tile.createTileForType(TileType.WATCHTOWER, 2, 2);
        tileTable[2][3] = Tile.createTileForType(TileType.OBSERVATORY, 2, 3);
        tileTable[2][4] = Tile.createTileForType(TileType.CAVE_OF_SHADOWS, 2, 4);
        tileTable[2][5] = Tile.createTileForType(TileType.CAVE_OF_EMBERS, 2, 5, TreasureType.FIRE);

        // 第四行（6个板块）
        tileTable[3][0] = Tile.createTileForType(TileType.TEMPLE_OF_THE_MOON, 3, 0);
        tileTable[3][1] = Tile.createTileForType(TileType.TEMPLE_OF_THE_SUN, 3, 1);
        tileTable[3][2] = Tile.createTileForType(TileType.CORAL_PALACE, 3, 2, TreasureType.OCEAN);
        tileTable[3][3] = Tile.createTileForType(TileType.WHISPERING_GARDEN, 3, 3, TreasureType.WIND);
        tileTable[3][4] = Tile.createTileForType(TileType.MISTY_MARSH, 3, 4);
        tileTable[3][5] = Tile.createTileForType(TileType.COPPER_GATE, 3, 5);

        // 第五行（4个板块）
        tileTable[4][1] = Tile.createTileForType(TileType.IRON_GATE, 4, 1, TreasureType.EARTH);
        tileTable[4][2] = Tile.createTileForType(TileType.BRONZE_GATE, 4, 2);
        tileTable[4][3] = Tile.createTileForType(TileType.IRON_GATE, 4, 3, TreasureType.EARTH);
        tileTable[4][4] = Tile.createTileForType(TileType.FOOLS_LANDING, 4, 4);

        // 第六行（2个板块）
        tileTable[5][2] = Tile.createTileForType(TileType.TWILIGHT_HOLLOW, 5, 2);
        tileTable[5][3] = Tile.createTileForType(TileType.WATCHTOWER, 5, 3);
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
     * 获取与指定瓦片相邻的所有瓦片（上下左右）
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
     * 获取与指定瓦片相邻的所有瓦片（包括对角线）
     */
    public List<Tile> getDiagonalAndOrthogonalTiles(Tile tile) {
        List<Tile> allTiles = new ArrayList<>();
        int row = tile.getRow();
        int col = tile.getCol();

        // 遍历3x3范围内的所有格子（除了中心点自己）
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // 跳过中心点

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
                        && tileTable[newRow][newCol] != null) {
                    allTiles.add(tileTable[newRow][newCol]);
                }
            }
        }
        return allTiles;
    }

    /**
     * 获取所有可到达的瓦片（用于潜水员）
     */
    public List<Tile> getReachableTilesForDiver(Tile startTile) {
        List<Tile> reachableTiles = new ArrayList<>();
        Set<Tile> visited = new HashSet<>();
        Queue<Tile> queue = new LinkedList<>();

        queue.add(startTile);
        visited.add(startTile);

        while (!queue.isEmpty()) {
            Tile current = queue.poll();

            for (Tile adjacent : getAdjacentTiles(current)) {
                if (!visited.contains(adjacent)) {
                    if (adjacent.isNavigable() || adjacent.isFlooded() || adjacent.isSunk()) {
                        reachableTiles.add(adjacent);
                        // 如果是被淹没或沉没的瓦片，继续搜索
                        if (adjacent.isFlooded() || adjacent.isSunk()) {
                            queue.add(adjacent);
                        }
                    }
                    visited.add(adjacent);
                }
            }
        }
        return reachableTiles;
    }

    /**
     * 获取所有未沉没的瓦片（用于飞行员）
     */
    public List<Tile> getAllNavigableTiles() {
        List<Tile> navigableTiles = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (tileTable[i][j] != null && tileTable[i][j].isNavigable()) {
                    navigableTiles.add(tileTable[i][j]);
                }
            }
        }
        return navigableTiles;
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

    /**
     * 获取指定宝藏类型的所有板块
     */
    public List<Tile> getTreasureTiles(TreasureType treasureType) {
        List<Tile> treasureTiles = new ArrayList<>();
        for (Tile tile : getAllTiles()) {
            if (tile.getTreasure() == treasureType) {
                treasureTiles.add(tile);
            }
        }
        return treasureTiles;
    }

    /**
     * 检查是否可以在指定板块获取宝藏
     */
    public boolean canCaptureTreasure(Tile tile, Player player) {
        if (tile == null || tile.getTreasure() == TreasureType.NONE || tile.isSunk()) {
            return false;
        }

        // 检查玩家是否在该板块上
        if (player.getCurrentTile() != tile) {
            return false;
        }

        // 计算玩家手中对应类型的宝藏卡数量
        int treasureCardCount = 0;
        for (Card card : player.getHand()) {
            if (card instanceof TreasureCard) {
                TreasureCard treasureCard = (TreasureCard) card;
                if (treasureCard.getTreasureType() == tile.getTreasure()) {
                    treasureCardCount++;
                }
            }
        }

        // 需要4张相同类型的宝藏卡
        return treasureCardCount >= 4;
    }

    /**
     * 检查指定宝藏是否还可以获得（是否有未沉没的宝藏板块）
     */
    public boolean isTreasureAvailable(TreasureType treasureType) {
        for (Tile tile : getTreasureTiles(treasureType)) {
            if (!tile.isSunk()) {
                return true;
            }
        }
        return false;
    }
}

