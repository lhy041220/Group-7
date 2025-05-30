package model;

import lombok.Getter;

import model.enums.TileState;
import model.enums.TileType;
import model.enums.TreasureType;
import java.util.ArrayList;
import java.util.List;

/***
 * Represents each cell on the island.
 * Includes type, state (normal/flooded/sunk), position and treasure info.
 * Provides flood and shoreUp methods.
 */
@Getter
public class Tile {
    private TileType type;
    private TileState state;
    private int row;
    private int col;
    private TreasureType treasure;
    private boolean flooded;
    private boolean sunk;
    private List<Player> playersOnTile;

    private Tile(TileType type, int row, int col, TreasureType treasure) {
        this.type = type;
        this.state = TileState.NORMAL;
        this.row = row;
        this.col = col;
        this.treasure = treasure;
        this.flooded = false;
        this.sunk = false;
        this.playersOnTile = new ArrayList<>();
    }

    public static Tile createTileForType(TileType type, int row, int col, TreasureType treasure) {
        return new Tile(type, row, col, treasure);
    }

    public static Tile createTileForType(TileType type, int row, int col) {
        return new Tile(type, row, col, TreasureType.NONE);
    }

    /**
     * Flood this tile.
     */
    public void flood() {
        state = TileState.FLOODED;
        flooded = true;
    }

    /**
     * Sink this tile.
     */
    public void sink() {
        state = TileState.SUNK;
        sunk = true;
    }

    /**
     * Shore up this tile (remove water).
     */
    public void shoreUp() {
        state = TileState.NORMAL;
        flooded = false;
        sunk = false;
    }

    public boolean isFlooded() {
        return state == TileState.FLOODED;
    }

    public boolean isSunk() {
        return state == TileState.SUNK;
    }

    /**
     * Whether the tile is navigable (not sunk).
     */
    public boolean isNavigable() {
        return state != TileState.SUNK;
    }

    @Override
    public String toString() {
        return type.getDisplayName() + "(" + state + ")";
    }

    /**
     * Check if this tile has the given treasure type.
     */
    public boolean hasTreasure(TreasureType treasureType) {
        return this.treasure == treasureType;
    }

    /**
     * Get tile name.
     */
    public String getName() {
        return type.getDisplayName();
    }

    public List<Player> getPlayersOnTile() {
        return playersOnTile;
    }

    public void addPlayer(Player player) {
        if (!playersOnTile.contains(player)) {
            playersOnTile.add(player);
        }
    }

    public void removePlayer(Player player) {
        playersOnTile.remove(player);
    }
}