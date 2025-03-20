package model;

import lombok.Getter;

import lombok.NoArgsConstructor;
import model.enums.TileState;
import model.enums.TileType;
import model.enums.TreasureType;

/***
 * 表示岛屿上的每个格子
 * 包含类型、状态(正常/已淹没/完全沉没)、位置和宝藏信息
 * 提供淹没(flood)和排水(shore_up)方法
 */
@Getter
public class Tile {
    private TileType type;
    private TileState state;
    private int row;
    private int col;
    private TreasureType treasure;

    private Tile(TileType type, int row, int col, TreasureType treasure) {
        this.type = type;
        this.state = TileState.NORMAL;
        this.row = row;
        this.col = col;
        this.treasure = treasure;
    }

    public static Tile createTileForType(TileType type, int row, int col, TreasureType treasure) {
        return new Tile(type, row, col, treasure);
    }

    public static Tile createTileForType(TileType type, int row, int col) {
        return new Tile(type, row, col, TreasureType.NONE);
    }

    /**
     * 淹没格子
     */
    public void flood() {
        state = TileState.FLOODED;
    }

    /**
     * 沉没格子
     */
    public void sink() {
        state = TileState.SUNK;
    }

    /**
     * 使格子排水
     */
    public void shoreUp() {
        state = TileState.NORMAL;
    }


    public boolean isFlooded() {
        return state == TileState.FLOODED;
    }

    public boolean isSunk() {
        return state == TileState.SUNK;
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
}
