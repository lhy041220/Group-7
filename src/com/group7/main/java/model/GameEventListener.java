package model;

public interface GameEventListener {
    default void onTurnStart(Player currentPlayer, int turnNumber) {}
    default void onTurnEnd(Player currentPlayer, int turnNumber) {}
    default void onTreasureDeckReshuffled() {}
    default void onFloodDeckReshuffled() {}
    default void onGameFailure(String reason) {}
    default void onGameVictory() {}
}
