package model;

import model.card.Card;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

public class Deck<T extends Card> {
    private Queue<T> cards;
    private List<T> discardPile;

    public Deck() {
        this.cards = new LinkedList<>();
        this.discardPile = new ArrayList<>();
    }

    /**
     * 添加卡牌到牌库
     */
    public void addCard(T card) {
        cards.offer(card);
    }

    /**
     * 抽取一张卡牌
     */
    public T drawCard() {
        // 如果牌库为空，尝试重组
        if (cards.isEmpty()) {
            reshuffleDiscardPile();
            // 如果重组后仍为空，返回null
            if (cards.isEmpty()) {
                return null;
            }
        }
        return cards.poll();
    }

    /**
     * 将卡牌放入弃牌堆
     */
    public void discard(T card) {
        discardPile.add(card);
    }

    /**
     * 洗牌
     */
    public void shuffle() {
        List<T> tempList = new ArrayList<>(cards);
        Collections.shuffle(tempList);
        cards.clear();
        cards.addAll(tempList);
    }

    /**
     * 重组弃牌堆
     * 将弃牌堆洗牌后放到牌库顶部
     */
    public void reshuffleDiscardPile() {
        if (discardPile.isEmpty()) {
            return;
        }

        // 洗牌弃牌堆
        Collections.shuffle(discardPile);

        // 将洗好的牌放到牌库顶部
        cards.addAll(discardPile);

        // 清空弃牌堆
        discardPile.clear();
    }

    /**
     * 获取牌库中剩余的卡牌数量
     */
    public int getRemainingCards() {
        return cards.size();
    }

    /**
     * @return 剩余的牌数量
     */
    public int getDiscardPileSize() {
        return discardPile.size();
    }

    /**
     * 检查牌库是否为空
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * 获取弃牌堆
     */
    public List<T> getDiscardPile() {
        return Collections.unmodifiableList(discardPile);
    }
}
