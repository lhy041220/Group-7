package model;

import lombok.Getter;
import model.card.*;
import model.enums.Role;
import model.enums.TreasureType;
import model.enums.ActionType;

import java.util.ArrayList;
import java.util.List;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Player {

    private int playerId;
    private Tile currentTile;

    private int actionsPerTurn = 3; // 每回合可执行的行动数
    private int remainingActions;   // 当前回合剩余的行动数
    private List<Card> hand;
    private final int MAX_HAND_SIZE = 5;
    private Role role; // 新增角色字段

    private Set<TreasureType> collectedTreasures;
    private boolean hasUsedSpecialAbility; // 用于追踪特殊能力的使用情况（比如飞行员每回合只能用一次）


    public Player(int playerId, Tile startingTile) {
        this.playerId = playerId;
        this.currentTile = startingTile;
        this.hand = new ArrayList<>();
        this.collectedTreasures = new HashSet<>();
        this.hasUsedSpecialAbility = false;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * 重置玩家的回合状态
     */
    public void resetActionsForTurn() {
        this.remainingActions = actionsPerTurn;
        this.hasUsedSpecialAbility = false;
    }

    /**
     * 使用一个行动点数
     */
    public void useAction() {
        if (remainingActions > 0) {
            remainingActions--;
            // 打印调试信息
            System.out.println("玩家 " + playerId + " 使用了一个行动点，剩余 " + remainingActions + " 点");
        }
    }

    /**
     * 获取剩余行动点数
     */
    public int getRemainingActions() {
        return remainingActions;
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

    public boolean moveToTile(Tile destinationTile) {
        // 检查移动是否合法
        if (canMoveTo(destinationTile)) {
            // 从当前板块移除玩家
            if (this.currentTile != null) {
                this.currentTile.removePlayer(this);
            }
            // 更新当前位置
            this.currentTile = destinationTile;
            // 添加到新板块
            this.currentTile.addPlayer(this);
            return true;
        }
        return false;
    }

    /**
     * 检查是否可以移动到目标板块
     */
    public boolean canMoveTo(Tile destinationTile) {
        if (destinationTile == null || !destinationTile.isNavigable()) {
            return false;
        }

        // 如果是探险家，可以斜向移动
        if (role == Role.EXPLORER) {
            List<Tile> reachableTiles = Game.getInstance().getBoard().getDiagonalAndOrthogonalTiles(currentTile);
            return reachableTiles.contains(destinationTile);
        }

        // 如果是潜水员，可以穿过被淹没的板块
        if (role == Role.DIVER) {
            List<Tile> reachableTiles = Game.getInstance().getBoard().getReachableTilesForDiver(currentTile);
            return reachableTiles.contains(destinationTile);
        }

        // 如果是飞行员且未使用过特殊能力，可以飞到任何地方
        if (role == Role.PILOT && !hasUsedSpecialAbility) {
            return true;
        }

        // 普通移动规则：只能移动到相邻的板块
        List<Tile> adjacentTiles = Game.getInstance().getBoard().getAdjacentTiles(currentTile);
        return adjacentTiles.contains(destinationTile);
    }

    /**
     * 使用特殊卡牌
     */
    public void useSpecialCard(SpecialCard card) {
        if (hand.contains(card)) {
            card.useCard(this);
            hand.remove(card);
        }
    }

    /**
     * 给其他玩家传递卡牌
     */
    public boolean giveCardToPlayer(Player targetPlayer, Card card) {
        // 信使可以在任何位置给卡
        if (role == Role.MESSENGER) {
            if (hand.contains(card)) {
                hand.remove(card);
                targetPlayer.addCardToHand(card);
                return true;
            }
        }
        // 其他角色需要在同一个板块才能给卡
        else if (currentTile == targetPlayer.getCurrentTile() && hand.contains(card)) {
            hand.remove(card);
            targetPlayer.addCardToHand(card);
            return true;
        }
        return false;
    }

    /**
     * 检查是否收集了特定类型的宝藏
     */
    public boolean hasCollectedTreasure(TreasureType treasureType) {
        return collectedTreasures.contains(treasureType);
    }

    /**
     * 添加收集到的宝藏
     */
    public void addCollectedTreasure(TreasureType treasureType) {
        collectedTreasures.add(treasureType);
    }

    /**
     * 使用特殊能力
     */
    public void useSpecialAbility(Tile destinationTile) {
        if (role != null && remainingActions > 0) {
            role.useSpecialAbility(this, destinationTile);
            if (role == Role.PILOT) {
                hasUsedSpecialAbility = true;
            }
            useAction();
        }
    }

    /**
     * 获取玩家当前可以给出的宝藏卡
     */
    public List<TreasureCard> getGiveableTreasureCards() {
        List<TreasureCard> giveableCards = new ArrayList<>();
        for (Card card : hand) {
            if (card instanceof TreasureCard) {
                giveableCards.add((TreasureCard) card);
            }
        }
        return giveableCards;
    }

    /**
     * 尝试排水行动
     * @param tile 目标板块
     * @return 是否成功排水
     */
    public boolean shoreUp(Tile tile) {
        if (canShoreUp(tile) && remainingActions > 0) {
            tile.shoreUp();
            useAction();
            return true;
        }
        return false;
    }

    /**
     * 检查是否可以在目标板块排水
     */
    public boolean canShoreUp(Tile tile) {
        if (tile == null || !tile.isFlooded()) {
            return false;
        }

        // 工程师可以一次行动排干两个板块的水
        if (role == Role.ENGINEER) {
            List<Tile> reachableTiles = Game.getInstance().getBoard().getAdjacentTiles(currentTile);
            return reachableTiles.contains(tile) || tile == currentTile;
        }

        // 探险家可以对角线排水
        if (role == Role.EXPLORER) {
            List<Tile> reachableTiles = Game.getInstance().getBoard().getDiagonalAndOrthogonalTiles(currentTile);
            return reachableTiles.contains(tile) || tile == currentTile;
        }

        // 普通角色只能对相邻或当前板块排水
        List<Tile> adjacentTiles = Game.getInstance().getBoard().getAdjacentTiles(currentTile);
        return adjacentTiles.contains(tile) || tile == currentTile;
    }

    /**
     * 尝试获取宝藏
     * @param treasureType 宝藏类型
     * @return 是否成功获取宝藏
     */
    public boolean captureTreasure(TreasureType treasureType) {
        if (canCaptureTreasure(treasureType) && remainingActions > 0) {
            // 移除4张对应的宝藏卡
            int count = 0;
            List<Card> cardsToRemove = new ArrayList<>();

            for (Card card : hand) {
                if (card instanceof TreasureCard && ((TreasureCard) card).getTreasureType() == treasureType) {
                    cardsToRemove.add(card);
                    count++;
                    if (count == 4) break;
                }
            }

            if (count == 4) {
                hand.removeAll(cardsToRemove);
                addCollectedTreasure(treasureType);
                useAction();
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否可以获取宝藏
     */
    public boolean canCaptureTreasure(TreasureType treasureType) {
        if (treasureType == null || treasureType == TreasureType.NONE) {
            return false;
        }

        // 检查是否在对应的宝藏板块上
        if (!currentTile.hasTreasure(treasureType)) {
            return false;
        }

        // 检查是否有足够的宝藏卡
        int treasureCardCount = 0;
        for (Card card : hand) {
            if (card instanceof TreasureCard && ((TreasureCard) card).getTreasureType() == treasureType) {
                treasureCardCount++;
            }
        }

        return treasureCardCount >= 4;
    }

    /**
     * 获取当前可执行的所有行动
     */
    public List<PossibleAction> getAvailableActions() {
        List<PossibleAction> actions = new ArrayList<>();

        // 检查可移动的位置
        for (Tile tile : Game.getInstance().getBoard().getAdjacentTiles(currentTile)) {
            if (canMoveTo(tile)) {
                actions.add(new PossibleAction(ActionType.MOVE, tile));
            }
        }

        // 检查可排水的位置
        for (Tile tile : Game.getInstance().getBoard().getAdjacentTiles(currentTile)) {
            if (canShoreUp(tile)) {
                actions.add(new PossibleAction(ActionType.SHORE_UP, tile));
            }
        }

        // 检查是否可以获取宝藏
        for (TreasureType treasureType : TreasureType.values()) {
            if (canCaptureTreasure(treasureType)) {
                actions.add(new PossibleAction(ActionType.CAPTURE_TREASURE, treasureType));
            }
        }

        // 检查是否可以给其他玩家传递宝藏卡
        List<TreasureCard> giveableCards = getGiveableTreasureCards();
        if (!giveableCards.isEmpty()) {
            for (Player otherPlayer : Game.getInstance().getPlayers()) {
                if (otherPlayer != this && (role == Role.MESSENGER || currentTile == otherPlayer.getCurrentTile())) {
                    actions.add(new PossibleAction(ActionType.GIVE_CARD, otherPlayer));
                }
            }
        }

        return actions;
    }
}  

