package model;

import model.card.Card;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

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

    /** 批量加入 */
    public void addCards(Collection<T> cardsToAdd) {
        cards.addAll(cardsToAdd);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public T drawCard() {
        if (cards.isEmpty()) {
            // 如果牌库为空，洗混弃牌堆并重新放入牌库
            if (discardPile.isEmpty()) return null; // 如果弃牌堆也为空，返回null
                // 否则将弃牌堆洗入主牌堆
            reshuffleFromDiscardPile();
        }
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }

    public void discard(T card) {
        discardPile.add(card);
    }


    /** 弃牌堆洗回主牌堆并清空弃牌堆 */
    public void reshuffleFromDiscardPile() {
        cards.addAll(discardPile);
        discardPile.clear();
        shuffle();
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

    /** 清空（重开新牌局用） */
    public void clear() {
        cards.clear();
        discardPile.clear();
    }
}
