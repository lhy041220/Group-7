package model.card;

import model.Player;
import model.Tile;

public class SandbagCard extends SpecialCard {
    public SandbagCard() {
        super("Sandbag", "Shore up any one tile on the board");
    }

    @Override
    public void useCard(Player player) {
        // 这里可以弹窗让玩家选择一块格子进行排水，暂时留空，后续与UI联动
    }
} 