package model.card;

import lombok.Getter;
import model.enums.CardType;
import model.enums.TileType;

@Getter
public class FloodCard extends Card {
    private TileType tileType;

    public FloodCard(TileType tileType, String tileName) {
        super(tileName + " Flood Card", "Flood the " + tileName + " tile.", CardType.FLOOD);
        this.tileType = tileType;
    }
}