package model.card;

import model.enums.CardType;

public abstract class HandCard extends Card{
    public HandCard(String name, String description, CardType type) {
        super(name, description, type);
    }
}
