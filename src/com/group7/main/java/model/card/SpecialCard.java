package model.card;

import model.Player;
import model.enums.CardType;

public abstract class SpecialCard extends HandCard {
    public SpecialCard(String name, String description) {
        super(name, description, CardType.SPECIAL);
    }

    public abstract void useCard(Player player);
}

