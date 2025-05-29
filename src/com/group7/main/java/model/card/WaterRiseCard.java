package model.card;

import model.Player;
import model.Game;
import model.enums.CardType;
import model.GameEventListener;

public class WaterRiseCard extends SpecialCard {
    public WaterRiseCard() {
        super("水位上升", "水位上升一格，并将洪水牌弃牌堆洗牌后放在洪水牌库顶部");
        this.canBeUsedAnytime = false; // 水位上升卡不能主动使用
    }

    @Override
    public void useCard(Player player) {
        Game game = Game.getInstance();

        // 1. 水位上升
        game.getWaterLevel().tryRise();

        // 2. 洗牌洪水牌弃牌堆并放到牌库顶部
        game.getFloodDeck().reshuffleDiscardPile();

        // 3. 通知监听器
        for (GameEventListener listener : game.getEventListeners()) {
            listener.onFloodDeckReshuffled();
        }
    }

    @Override
    public boolean canBeUsedNow(Player player) {
        return false; // 水位上升卡永远不能主动使用
    }
}