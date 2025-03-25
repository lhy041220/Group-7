package model.card;

import model.enums.TreasureType;

import lombok.Getter;

@Getter
public class TreasureCard extends Card {
    private TreasureType treasureType;

    public TreasureCard(String name, String description, TreasureType treasureType) {
        super(name, description);
        this.treasureType = treasureType;
    }
}
