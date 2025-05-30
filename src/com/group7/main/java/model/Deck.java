package model;

import model.card.Card;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

/**
 * Generic deck class for handling cards and discard pile.
 */
public class Deck<T extends Card> {
    private Queue<T> cards;
    private List<T> discardPile;

    public Deck() {
        this.cards = new LinkedList<>();
        this.discardPile = new ArrayList<>();
    }

    /**
     * Add a card to the deck.
     */
    public void addCard(T card) {
        cards.offer(card);
    }

    /**
     * Draw a card from the deck.
     * If the deck is empty, reshuffle the discard pile.
     * @return the drawn card, or null if no cards are available.
     */
    public T drawCard() {
        // If the deck is empty, try to reshuffle discard pile.
        if (cards.isEmpty()) {
            reshuffleDiscardPile();
            // If still empty after reshuffling, return null.
            if (cards.isEmpty()) {
                return null;
            }
        }
        return cards.poll();
    }

    /**
     * Add a card to the discard pile.
     */
    public void discard(T card) {
        discardPile.add(card);
    }

    /**
     * Shuffle the deck.
     */
    public void shuffle() {
        List<T> tempList = new ArrayList<>(cards);
        Collections.shuffle(tempList);
        cards.clear();
        cards.addAll(tempList);
    }

    /**
     * Reshuffle the discard pile and add it back to the top of the deck.
     */
    public void reshuffleDiscardPile() {
        if (discardPile.isEmpty()) {
            return;
        }

        // Shuffle the discard pile
        Collections.shuffle(discardPile);

        // Add shuffled cards to the deck
        cards.addAll(discardPile);

        // Clear the discard pile
        discardPile.clear();
    }

    /**
     * Get the number of cards remaining in the deck.
     */
    public int getRemainingCards() {
        return cards.size();
    }

    /**
     * Get the number of cards in the discard pile.
     * @return discard pile size
     */
    public int getDiscardPileSize() {
        return discardPile.size();
    }

    /**
     * Check if the deck is empty.
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Get an unmodifiable view of the discard pile.
     */
    public List<T> getDiscardPile() {
        return Collections.unmodifiableList(discardPile);
    }
}

