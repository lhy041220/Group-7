package model.card;

import model.Player;
import model.Game;
import model.enums.CardType;
import model.GameEventListener;

public class WaterRiseCard extends SpecialCard {
    public WaterRiseCard() {
        super("The water level rises.", "When the water level rises by one notch, shuffle the flood card discard pile and place it on top of the flood card deck");
        this.canBeUsedAnytime = false; // The water level rise card cannot be used actively
    }

    @Override
    public void useCard(Player player) {
        Game game = Game.getInstance();

        // The water level rises
        game.getWaterLevel().tryRise();

        // 2. Shuffle the flood card discard pile and place it on the top of the deck
        game.getFloodDeck().reshuffleDiscardPile();

        // 3. Notify the listener
        for (GameEventListener listener : game.getEventListeners()) {
            listener.onFloodDeckReshuffled();
        }
    }

    @Override
    public boolean canBeUsedNow(Player player) {
        return false; // The water level rise card can never be used actively
    }
}