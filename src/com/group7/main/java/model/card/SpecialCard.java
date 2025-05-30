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
     * Check whether the card can be used at the current time
     * @param player A player who uses cards
     */
    public boolean canBeUsedNow(Player player) {
        Game game = Game.getInstance();

        // If the game has ended, the cards cannot be used
        if (game.isGameOver()) {
            return false;
        }

        // If this card is not in the player's hand, it cannot be used
        if (!player.getHand().contains(this)) {
            return false;
        }

        // Check whether it is after drawing the flood card (only affecting the sandbag card)
        if (!canBeUsedAfterFlood && game.isAfterFloodCardDrawn()) {
            return false;
        }

        return canBeUsedAnytime;
    }

    /**
     * The basic method of using cards
     * @param player 使用卡牌的玩家
     * @return Whether it was used successfully
     */
    public boolean use(Player player) {
        if (!canBeUsedNow(player)) {
            return false;
        }

        // Execute the card effect
        useCard(player);

        // Put the cards into the discard pile
        Game.getInstance().getTreasureDeck().discard(this);

        return true;
    }

    public boolean use(Player player, Tile targetTile) {
        return use(player);
    }

    public abstract void useCard(Player player);
}

