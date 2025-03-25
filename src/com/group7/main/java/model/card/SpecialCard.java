package model.card;


import model.Player;

/***
 * 特殊卡牌
 */
public abstract class SpecialCard extends Card {
    public SpecialCard(String name, String description) {
        super(name, description);
    }

    public abstract void useCard(Player player);
}

