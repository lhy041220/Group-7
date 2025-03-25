package model;

import model.card.Card;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Deck<T extends Card> {
    private List<T> cards;
    private List<T> discardPile;

    public Deck() {
        this.cards = new ArrayList<>();
        this.discardPile = new ArrayList<>();
    }

    public void addCard(T card) {
        cards.add(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public T drawCard() {
        if (cards.isEmpty()) {
            // 如果牌库为空，洗混弃牌堆并重新放入牌库
            if (discardPile.isEmpty()) {
                return null; // 如果弃牌堆也为空，返回null
            }
            cards.addAll(discardPile);
            discardPile.clear();
            shuffle();
        }
        return cards.remove(cards.size() - 1);
    }

    public void discard(T card) {
        discardPile.add(card);
    }

    /**
     * @return 剩余的牌数量
     */
    public int getRemainingCards() {
        return cards.size();
    }

    /**
     * @return 剩余的牌数量
     */
    public int getDiscardedCards() {
        return discardPile.size();
    }
}
