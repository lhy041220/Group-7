package model.card;

import model.Player;

public class HelicopterLiftCard extends SpecialCard {
    public HelicopterLiftCard() {
        super("Helicopter Lift", "Move all players to any tile on the board");
    }

    @Override
    public void useCard(Player player) {
        // 实现直升机起飞卡牌的功能
    }
}