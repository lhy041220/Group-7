package model.card;

import model.Player;
import model.enums.CardType;
import model.Game;
import model.Tile;

public abstract class SpecialCard extends HandCard {
    protected boolean canBeUsedAnytime = true;  // 默认特殊卡可以随时使用
    protected boolean canBeUsedAfterFlood = true;  // 默认可以在看到洪水卡后使用
    public SpecialCard(String name, String description) {
        super(name, description, CardType.SPECIAL);
    }

    /**
     * 检查卡牌是否可以在当前时机使用
     * @param player 使用卡牌的玩家
     * @return 是否可以使用
     */
    public boolean canBeUsedNow(Player player) {
        Game game = Game.getInstance();

        // 如果游戏已经结束，不能使用卡牌
        if (game.isGameOver()) {
            return false;
        }

        // 如果玩家手牌中没有这张卡，不能使用
        if (!player.getHand().contains(this)) {
            return false;
        }

        // 检查是否是在抽到洪水卡之后（仅对沙袋卡有影响）
        if (!canBeUsedAfterFlood && game.isAfterFloodCardDrawn()) {
            return false;
        }

        return canBeUsedAnytime;
    }

    /**
     * 使用卡牌的基础方法
     * @param player 使用卡牌的玩家
     * @return 是否成功使用
     */
    public boolean use(Player player) {
        if (!canBeUsedNow(player)) {
            return false;
        }

        // 执行卡牌效果
        useCard(player);

        // 将卡牌放入弃牌堆
        Game.getInstance().getTreasureDeck().discard(this);

        return true;
    }

    public boolean use(Player player, Tile targetTile) {
        return use(player);
    }

    public abstract void useCard(Player player);
}

