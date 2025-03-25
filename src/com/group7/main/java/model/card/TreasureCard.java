package model.card;

import model.enums.TreasureType;
import model.enums.CardType;

import lombok.Getter;

@Getter
public class TreasureCard extends HandCard {
    private TreasureType treasureType;

    public TreasureCard(String name, String description, TreasureType treasureType) {
        super(name, description, CardType.TREASURE);
        this.treasureType = treasureType;
    }
}
