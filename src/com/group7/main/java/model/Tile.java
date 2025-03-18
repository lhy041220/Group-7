package model;

import model.enums.TileState;
import model.enums.TileType;
import model.enums.TreasureType;

public class Tile {
    private TileType type;
    private TileState state;
    private int row;
    private int col;
    private TreasureType treasure;

    public Tile(TileType type, int row, int col, TreasureType treasure) {
        this.type = type;
        this.state = TileState.NORMAL;
        this.row = row;
        this.col = col;
        this.treasure = treasure;
    }

    public Tile(TileType type, int row, int col) {
        this(type, row, col, TreasureType.NONE);
    }

    /**
     * 淹没格子，返回是否导致格子沉没
     */
    public boolean flood() {
        if (state == TileState.NORMAL) {
            state = TileState.FLOODED;
            return false;
        } else if (state == TileState.FLOODED) {
            state = TileState.SUNK;
            return true;
        }
        return false;
    }

    /**
     * 排水，返回操作是否成功
     */
    public boolean shoreUp() {
        if (state == TileState.FLOODED) {
            state = TileState.NORMAL;
            return true;
        }
        return false;
    }

    /**
     * 判断格子是否可通行
     */
    public boolean isNavigable() {
        return state != TileState.SUNK;
    }

    @Override
    public String toString() {
        return type.getDisplayName() + "(" + state + ")";
    }

    // Getters and setters
    public TileType getType() {
        return type;
    }

    public TileState getState() {
        return state;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public TreasureType getTreasure() {
        return treasure;
    }
}
