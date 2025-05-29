package model;

import model.card.Card;
import model.enums.TreasureType;

public interface GameEventListener {
    default void onTurnStart(Player currentPlayer, int turnNumber) {}
    default void onTurnEnd(Player currentPlayer, int turnNumber) {}
    default void onTreasureDeckReshuffled() {}
    default void onFloodDeckReshuffled() {}
    default void onGameFailure(String reason) {}
    default void onGameVictory() {}
    default void onTreasureCaptured(Player player, TreasureType treasureType) {}
    void onCardDiscarded(Player player, Card card);
    void onGameOver(String message);
}
