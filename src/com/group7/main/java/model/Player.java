package model;

import lombok.Getter;
import lombok.Setter;
import model.card.*;
import model.enums.Role; 

import java.util.ArrayList;
import java.util.List;

@Getter
public class Player {

    private int playerId;
    @Setter
    private Tile currentTile;

    private int actionsPerTurn = 3; // 每回合可执行的行动数
    private int remainingActions;   // 当前回合剩余的行动数
    private List<Card> hand;
    private final int MAX_HAND_SIZE = 5;
    @Setter
    private Role role; // 角色字段
    
    // 飞行员特殊能力使用状态
    private boolean pilotAbilityUsed = false;
    // 工程师特殊能力状态：是否已使用过第一次排水
    private boolean engineerFirstShoreUpDone = false;

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
        // 重置飞行员特殊能力使用状态
        this.pilotAbilityUsed = false;
        // 重置工程师特殊能力状态
        this.engineerFirstShoreUpDone = false;
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

    /**
     * 将卡牌交给另一个玩家
     */
    public void giveCardToPlayer(Card card, Player targetPlayer) {
        if (hand.contains(card)) {
            hand.remove(card);
            targetPlayer.addCardToHand(card);
        }
    }

    /**
     * 移动到另一个板块
     */
    public void moveToTile(Tile destinationTile) {
        // 检查移动是否合法
        if (canMoveTo(destinationTile)) {
            this.currentTile = destinationTile;
        }
    }

    /**
     * 检查是否可以移动到目标板块
     */
    public boolean canMoveTo(Tile destinationTile) {
        // 如果板块已沉没，则不能移动到该位置
        if (destinationTile == null || destinationTile.isSunk()) {
            return false;
        }

        // 探险家特殊规则：可以对角线移动
        if (role == Role.EXPLORER) {
            int rowDiff = Math.abs(currentTile.getRow() - destinationTile.getRow());
            int colDiff = Math.abs(currentTile.getCol() - destinationTile.getCol());

            // 对角线移动 或 正常移动(上下左右)
            if ((rowDiff <= 1 && colDiff <= 1) && !(rowDiff == 0 && colDiff == 0)) {
                return true;
            }
        }
        // 潜水员特殊规则：可以穿过沉没的板块
        else if (role == Role.DIVER) {
            // 潜水员可以穿过沉没板块的复杂逻辑需要在游戏控制器中实现
            // 这里简化处理，认为可以移动到更远的地方
            return true;
        }
        // 飞行员特殊规则：每回合可以飞到任何板块一次
        else if (role == Role.PILOT && !pilotAbilityUsed) {
            // 飞行员特殊能力逻辑在useSpecialAbility中处理
            return true;
        }
        // 普通移动规则：只能移动到相邻且未沉没的板块
        else {
            int rowDiff = Math.abs(currentTile.getRow() - destinationTile.getRow());
            int colDiff = Math.abs(currentTile.getCol() - destinationTile.getCol());

            // 正常移动只能上下左右移动一格
            return (rowDiff + colDiff == 1);
        }

        return false;
    }

    /**
     * 排水(翻转板块)操作
     */
    public boolean shoreUpTile(Tile tile) {
        // 检查板块是否可以排水
        if (canShoreUp(tile)) {
            tile.shoreUp();

            // 如果是工程师且是第一次使用排水，不消耗行动点并标记已经使用过一次
            if (role == Role.ENGINEER && !engineerFirstShoreUpDone) {
                engineerFirstShoreUpDone = true;
                return false; // 不消耗行动点
            }

            return true; // 消耗行动点
        }
        return false; // 操作失败，不消耗行动点
    }

    /**
     * 检查是否可以对指定板块进行排水
     */
    public boolean canShoreUp(Tile tile) {
        if (tile == null || !tile.isFlooded()) {
            return false;
        }

        // 探险家特殊规则：可以对角线排水
        if (role == Role.EXPLORER) {
            int rowDiff = Math.abs(currentTile.getRow() - tile.getRow());
            int colDiff = Math.abs(currentTile.getCol() - tile.getCol());

            // 对角线排水 或 正常排水(上下左右)
            return (rowDiff <= 1 && colDiff <= 1) && !(rowDiff == 0 && colDiff == 0);
        }
        // 普通排水规则：只能对相邻板块或自己所在板块排水
        else {
            int rowDiff = Math.abs(currentTile.getRow() - tile.getRow());
            int colDiff = Math.abs(currentTile.getCol() - tile.getCol());

            // 可以排水当前板块或相邻板块
            return (rowDiff + colDiff <= 1);
        }
    }

    /**
     * 使用特殊卡牌
     */
    public void useSpecialCard(SpecialCard card) {
        // 实现使用特殊卡牌的逻辑
        if (hand.contains(card)) {
            card.useCard(this);
            hand.remove(card);
        }
    }

    /**
     * 使用角色特殊能力
     */
    public void useSpecialAbility(Tile destinationTile) {
        // 根据角色类型使用特殊能力
        switch (role) {
            case PILOT:
                // 飞行员特殊能力：飞到任意板块
                if (!pilotAbilityUsed && destinationTile != null && destinationTile.isNavigable()) {
                    currentTile = destinationTile;
                    pilotAbilityUsed = true;
                    useAction(); // 消耗一个行动点
                }
                break;

            case ENGINEER:
                // 工程师特殊能力已在shoreUpTile方法中实现
                break;

            case NAVIGATOR:
                // 领航员特殊能力：移动其他玩家
                // 需要在控制器层实现具体逻辑
                role.useSpecialAbility(this, destinationTile);
                useAction();
                break;

            case DIVER:
                // 潜水员特殊能力：穿越沉没板块移动
                if (destinationTile != null && destinationTile.isNavigable()) {
                    currentTile = destinationTile;
                    useAction();
                }
                break;

            case MESSENGER:
                // 信使特殊能力：跨板块传递宝藏卡
                // 需要在控制器层实现具体逻辑
                role.useSpecialAbility(this, destinationTile);
                useAction();
                break;

            case EXPLORER:
                // 探险家特殊能力：对角线移动和排水
                // 已在canMoveTo和canShoreUp方法中实现
                break;
        }
    }

    /**
     * 检查是否可以给另一个玩家传递卡牌
     */
    public boolean canGiveCardTo(Player targetPlayer) {
        // 信使可以在任何位置传递卡牌
        if (role == Role.MESSENGER) {
            return true;
        }

        // 普通玩家只能在同一板块传递卡牌
        return currentTile.equals(targetPlayer.getCurrentTile());
    }
}


