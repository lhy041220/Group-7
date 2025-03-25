package model.card;

import lombok.Getter;
import model.Tile;
import model.enums.TileType;

@Getter
public class FloodCard extends Card {
    private TileType tileType;

    public FloodCard(TileType tileType, String tileName) {
        super(tileName + " Flood Card", "Flood the " + tileName + " tile.");
        this.tileType = tileType;
    }
}