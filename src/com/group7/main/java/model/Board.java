package model;

import model.enums.TileType;
import model.enums.TreasureType;
import model.card.Card;
import model.card.TreasureCard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Represents the Forbidden Island game board.
 */
public class Board {
    private Tile[][] tileTable; // 2D array representing the game board
    private final int rows = 6; // Number of rows
    private final int cols = 6; // Number of columns
    private List<Tile> floodedTiles; // List of flooded tiles
    private List<Tile> sunkTiles; // List of sunk tiles
    private Random random;

    /**
     * Constructor, initializes the game board.
     */
    public Board() {
        tileTable = new Tile[rows][cols];
        floodedTiles = new ArrayList<>();
        sunkTiles = new ArrayList<>();
        random = new Random();
        initializeBoard();
    }

    /**
     * Initializes the game board and creates all tiles.
     */
    private void initializeBoard() {
        // Initialize all positions as null
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tileTable[i][j] = null;
            }
        }

        // Create specific tiles and set treasure locations
        // First row (2 tiles)
        tileTable[0][2] = Tile.createTileForType(TileType.WHISPERING_GARDEN, 0, 2, TreasureType.WIND);
        tileTable[0][3] = Tile.createTileForType(TileType.HOWLING_GARDEN, 0, 3);

        // Second row (4 tiles)
        tileTable[1][1] = Tile.createTileForType(TileType.TIDAL_PALACE, 1, 1, TreasureType.OCEAN);
        tileTable[1][2] = Tile.createTileForType(TileType.LOST_LAGOON, 1, 2);
        tileTable[1][3] = Tile.createTileForType(TileType.PHANTOM_ROCK, 1, 3);
        tileTable[1][4] = Tile.createTileForType(TileType.SILVER_GATE, 1, 4);

        // Third row (6 tiles)
        tileTable[2][0] = Tile.createTileForType(TileType.CRIMSON_FOREST, 2, 0, TreasureType.FIRE);
        tileTable[2][1] = Tile.createTileForType(TileType.DUNES_OF_DECEPTION, 2, 1);
        tileTable[2][2] = Tile.createTileForType(TileType.WATCHTOWER, 2, 2);
        tileTable[2][3] = Tile.createTileForType(TileType.OBSERVATORY, 2, 3);
        tileTable[2][4] = Tile.createTileForType(TileType.CAVE_OF_SHADOWS, 2, 4);
        tileTable[2][5] = Tile.createTileForType(TileType.CAVE_OF_EMBERS, 2, 5, TreasureType.FIRE);

        // Fourth row (6 tiles)
        tileTable[3][0] = Tile.createTileForType(TileType.TEMPLE_OF_THE_MOON, 3, 0);
        tileTable[3][1] = Tile.createTileForType(TileType.TEMPLE_OF_THE_SUN, 3, 1);
        tileTable[3][2] = Tile.createTileForType(TileType.CORAL_PALACE, 3, 2, TreasureType.OCEAN);
        tileTable[3][3] = Tile.createTileForType(TileType.WHISPERING_GARDEN, 3, 3, TreasureType.WIND);
        tileTable[3][4] = Tile.createTileForType(TileType.MISTY_MARSH, 3, 4);
        tileTable[3][5] = Tile.createTileForType(TileType.COPPER_GATE, 3, 5);

        // Fifth row (4 tiles)
        tileTable[4][1] = Tile.createTileForType(TileType.IRON_GATE, 4, 1, TreasureType.EARTH);
        tileTable[4][2] = Tile.createTileForType(TileType.BRONZE_GATE, 4, 2);
        tileTable[4][3] = Tile.createTileForType(TileType.IRON_GATE, 4, 3, TreasureType.EARTH);
        tileTable[4][4] = Tile.createTileForType(TileType.FOOLS_LANDING, 4, 4);

        // Sixth row (2 tiles)
        tileTable[5][2] = Tile.createTileForType(TileType.TWILIGHT_HOLLOW, 5, 2);
        tileTable[5][3] = Tile.createTileForType(TileType.WATCHTOWER, 5, 3);
    }

    /**
     * Checks if two tiles are adjacent. The same position is considered adjacent.
     */
    private static boolean isAdjacentTo(int row1, int col1, int row2, int col2) {
        int rowDiff = Math.abs(row1 - row2);
        int colDiff = Math.abs(col1 - col2);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1) || (rowDiff == 0 && colDiff == 0);
    }

    // Check if Fool's Landing (helipad) is sunk
    public boolean isHelipadSunk() {
        Tile helipad = getTileByType(TileType.FOOLS_LANDING);
        return helipad == null || helipad.isSunk();
    }

    // Check if all tiles for a specific treasure are sunk
    public boolean isAllTreasureTilesSunk(TreasureType treasureType) {
        int total = 0, sunk = 0;
        for (Tile tile : getAllTiles()) {
            if (tile.getTreasure() == treasureType) {
                total++;
                if (tile.isSunk()) sunk++;
            }
        }
        // Both tiles must be sunk (usually there are two tiles for each treasure)
        return total > 0 && sunk == total;
    }

    /**
     * Returns the tile at the specified coordinates.
     */
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= tileTable.length || col < 0 || col >= tileTable[0].length) return null;
        return tileTable[row][col];
    }

    /**
     * Returns the tile of a specific type.
     */
    public Tile getTileByType(TileType tileType) {
        for (int row = 0; row < tileTable.length; row++) {
            for (int col = 0; col < tileTable[row].length; col++) {
                if (tileTable[row][col] != null && tileTable[row][col].getType() == tileType) {
                    return tileTable[row][col];
                }
            }
        }
        return null;
    }

    /**
     * Returns all non-null tiles.
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
     * Returns all flooded, but not yet sunk, tiles.
     */
    public List<Tile> getFloodedTiles() {
        return new ArrayList<>(floodedTiles);
    }

    /**
     * Returns all sunk tiles.
     */
    public List<Tile> getSunkTiles() {
        return new ArrayList<>(sunkTiles);
    }

    /**
     * Floods a tile.
     */
    public void floodTile(Tile tile) {
        if (tile != null && !tile.isFlooded() && !tile.isSunk()) {
            tile.flood();
            floodedTiles.add(tile);
        }
    }

    /**
     * Sinks a tile.
     */
    public void sinkTile(Tile tile) {
        if (tile != null && tile.isFlooded()) {
            tile.sink();
            floodedTiles.remove(tile);
            sunkTiles.add(tile);
            // Optionally, set the tile to null in the 2D array (sunk tiles are not restored)
            // tileTable[tile.getRow()][tile.getCol()] = null;
        }
    }

    /**
     * Dries (shores up) a flooded tile.
     */
    public void dryTile(Tile tile) {
        if (tile != null && tile.isFlooded()) {
            tile.shoreUp();
            floodedTiles.remove(tile);
        }
    }

    /**
     * Returns all tiles adjacent (up, down, left, right) to the specified tile.
     */
    public List<Tile> getAdjacentTiles(Tile tile) {
        List<Tile> adjacentTiles = new ArrayList<>();
        if (tile == null) return adjacentTiles;

        int[] pos = getTilePosition(tile);
        if (pos == null) return adjacentTiles;

        int row = pos[0];
        int col = pos[1];

        // 检查上下左右四个方向
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol)) {
                Tile adjacentTile = tileTable[newRow][newCol];
                if (adjacentTile != null) {
                    adjacentTiles.add(adjacentTile);
                }
            }
        }

        return adjacentTiles;
    }

    /**
     * Returns all tiles adjacent, including diagonals, to the specified tile.
     */
    public List<Tile> getDiagonalAndOrthogonalTiles(Tile tile) {
        List<Tile> reachableTiles = new ArrayList<>();
        if (tile == null) return reachableTiles;

        int[] pos = getTilePosition(tile);
        if (pos == null) return reachableTiles;

        int row = pos[0];
        int col = pos[1];

        // 检查八个方向（上下左右和对角）
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // 上下左右
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // 对角
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol)) {
                Tile reachableTile = tileTable[newRow][newCol];
                if (reachableTile != null) {
                    reachableTiles.add(reachableTile);
                }
            }
        }

        return reachableTiles;
    }

    /**
     * Returns all reachable tiles for the Diver (i.e., can traverse flooded and sunk tiles).
     */
    public List<Tile> getReachableTilesForDiver(Tile tile) {
        List<Tile> reachableTiles = new ArrayList<>();
        if (tile == null) return reachableTiles;

        Set<Tile> visited = new HashSet<>();
        Queue<Tile> queue = new LinkedList<>();
        queue.add(tile);
        visited.add(tile);

        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            for (Tile adjacent : getAdjacentTiles(current)) {
                if (!visited.contains(adjacent) && (adjacent.isFlooded() || !adjacent.isSunk())) {
                    reachableTiles.add(adjacent);
                    visited.add(adjacent);
                    if (adjacent.isFlooded()) {
                        queue.add(adjacent);
                    }
                }
            }
        }

        return reachableTiles;
    }

    /**
     * Returns all tiles that are not sunk (for the Pilot).
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
     * Checks if the game is over (if specific important tiles are sunk).
     */
    public boolean isGameOver() {
        // TODO: Implement game over logic
        // Example: If the Fool's Landing tile is sunk, the game is over
        throw new UnsupportedOperationException("The method isGameOver() of the Board class is not implemented");
    }

    /**
     * Prints the current state of the board (for debugging).
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
     * Returns all tiles corresponding to the specified treasure type.
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
     * Checks if the player can collect treasure on the specified tile.
     */
    public boolean canCaptureTreasure(Tile tile, Player player) {
        if (tile == null || tile.getTreasure() == TreasureType.NONE || tile.isSunk()) {
            return false;
        }

        // Check if the player is on this tile
        if (player.getCurrentTile() != tile) {
            return false;
        }

        // Count the number of related treasure cards in the player's hand
        int treasureCardCount = 0;
        for (Card card : player.getHand()) {
            if (card instanceof TreasureCard) {
                TreasureCard treasureCard = (TreasureCard) card;
                if (treasureCard.getTreasureType() == tile.getTreasure()) {
                    treasureCardCount++;
                }
            }
        }

        // Need at least 4 of the same type of treasure card
        return treasureCardCount >= 4;
    }

    /**
     * Checks if the specified treasure is still collectible (i.e., has remaining non-sunk treasure tiles).
     */
    public boolean isTreasureAvailable(TreasureType treasureType) {
        for (Tile tile : getTreasureTiles(treasureType)) {
            if (!tile.isSunk()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the tile at the specified row and column.
     */
    public Tile getTileAt(int row, int col) {
        if (row >= 0 && row < 6 && col >= 0 && col < 6) {
            return tileTable[row][col];
        }
        return null;
    }

    public int[] getTilePosition(Tile tile) {
        for (int row = 0; row < tileTable.length; row++) {
            for (int col = 0; col < tileTable[row].length; col++) {
                if (tileTable[row][col] == tile) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    /**
     * 检查位置是否有效
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < tileTable.length && col >= 0 && col < tileTable[0].length;
    }

    /**
     * 获取玩家当前可移动的所有格子（只返回 tileTable 里的唯一对象）
     */
    public List<Tile> getMovableTilesForPlayer(Player player) {
        Tile current = player.getCurrentTile();
        if (current == null) return new ArrayList<>();
        List<Tile> result = new ArrayList<>();
        if (player.getRole() == model.enums.Role.EXPLORER) {
            result.addAll(getDiagonalAndOrthogonalTiles(current));
        } else if (player.getRole() == model.enums.Role.DIVER) {
            result.addAll(getReachableTilesForDiver(current));
        } else {
            result.addAll(getAdjacentTiles(current));
        }
        // 只保留未沉没的格子
        result.removeIf(tile -> tile == null || tile.isSunk());
        // 不能移动到自己当前格子
        result.remove(current);
        return result;
    }

    /**
     * 调试用：打印玩家可移动格子的类型和对象hash
     */
    public void debugPrintMovableTiles(Player player) {
        List<Tile> movable = getMovableTilesForPlayer(player);
        System.out.println("[DEBUG] movable tiles for player " + player.getPlayerId() + ":");
        for (Tile t : movable) {
            System.out.println("  " + t.getType().getDisplayName() + " hash=" + System.identityHashCode(t));
        }
    }
}
