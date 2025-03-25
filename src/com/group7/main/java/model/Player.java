package model;

import model.card.SpecialCard;
import model.card.TreasureCard;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Tile currentTile;
    private List<TreasureCard> treasureCards;
    private List<SpecialCard> specialCards;

    public Player(String name, Tile startingTile) {
        this.name = name;
        this.currentTile = startingTile;
        this.treasureCards = new ArrayList<>();
        this.specialCards = new ArrayList<>();
    }

    public void moveToTile(Tile destinationTile) {
        // 检查移动是否合法
        if (canMoveTo(destinationTile)) {
            this.currentTile = destinationTile;
        }
    }

    private boolean canMoveTo(Tile destinationTile) {
        // 实现移动规则检查
        // 例如，检查目标瓦片是否相邻，是否未被淹没等
        return true; // 临时返回值，需要实现实际逻辑
    }

    public void addCard(TreasureCard card) {
        treasureCards.add(card);
    }

    public void addCard(SpecialCard card) {
        specialCards.add(card);
    }

    public void useSpecialCard(SpecialCard card) {
        // 实现使用特殊卡牌的逻辑
        if (specialCards.contains(card)) {
            card.useCard(this);
            specialCards.remove(card);
        }
    }

    // Getter和Setter方法
    public String getName() {
        return name;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public List<TreasureCard> getTreasureCards() {
        return treasureCards;
    }

    public List<SpecialCard> getSpecialCards() {
        return specialCards;
    }
}