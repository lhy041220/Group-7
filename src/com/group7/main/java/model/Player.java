package model;

import lombok.Getter;
import model.card.*;
import model.enums.Role; 

import java.util.ArrayList;
import java.util.List;

@Getter
public class Player {

    private int playerId;
    private Tile currentTile;

    private int actionsPerTurn = 3; // 每回合可执行的行动数
    private int remainingActions;   // 当前回合剩余的行动数
    private List<Card> hand;
    private final int MAX_HAND_SIZE = 5;
    private Role role; // 新增角色字段  


    public Player(int playerId, Tile startingTile) {
        this.playerId = playerId;
        this.currentTile = startingTile;
        this.hand = new ArrayList<>();
    }

    /**
     * 重置玩家的行动点数
     */
    public void resetActionsForTurn() {
        this.remainingActions = actionsPerTurn;
    }

    /**
     * 使用一个行动点数
     */
    public void useAction() {
        if (remainingActions > 0) {
            remainingActions--;
        }
    }

    /**
     * 添加卡牌到手中
     */
    public void addCardToHand(Card card) {
        hand.add(card);
    }

    /**
     * 检查手牌是否超出上限
     */
    public boolean handExceedsLimit() {
        return hand.size() > MAX_HAND_SIZE;
    }

    /**
     * 丢弃卡牌
     */
    public void discardCard(Card card) {
        hand.remove(card);
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

    public void useSpecialCard(SpecialCard card) {
        // 实现使用特殊卡牌的逻辑
        if (hand.contains(card)) {
            card.useCard(this);
            hand.remove(card);
        }
    }
     
    public void useSpecialAbility(Tile destinationTile) {  
        // 执行当前角色的特殊能力 (新)
        this.role.useSpecialAbility(this, destinationTile); // 调用角色的特殊能力  
        useAction(); // 使用一次行动点  
    }  
}  

