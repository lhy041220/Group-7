package model;

import lombok.Getter;
import lombok.Setter;
import model.card.*;
import model.enums.Role; 
import model.enums.TileState;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;

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
        // 默认设置为EXPLORER角色，防止空指针异常
        this.role = Role.EXPLORER;
        this.remainingActions = actionsPerTurn;
    }
    
    /**
     * 创建玩家并指定角色
     */
    public Player(int playerId, Tile startingTile, Role role) {
        this(playerId, startingTile);
        this.role = role;
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
     * @return 是否成功使用行动点
     */
    public boolean useAction() {
        if (remainingActions > 0) {
            remainingActions--;
            return true;
        }
        return false;
    }

    /**
     * 添加卡牌到手中
     */
    public void addCardToHand(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    /**
     * 检查手牌是否超出上限
     */
    public boolean handExceedsLimit() {
        return hand.size() > MAX_HAND_SIZE;
    }

    /**
     * 丢弃卡牌
     * @return 是否成功丢弃
     */
    public boolean discardCard(Card card) {
        if (card != null && hand.contains(card)) {
            hand.remove(card);
            return true;
        }
        return false;
    }

    /**
     * 将卡牌交给另一个玩家
     * @return 是否成功交换卡牌
     */
    public boolean giveCardToPlayer(Card card, Player targetPlayer) {
        if (card == null || targetPlayer == null) {
            return false;
        }
        
        // 检查是否可以交换卡牌
        if (!canGiveCardTo(targetPlayer)) {
            return false;
        }
        
        if (hand.contains(card)) {
            hand.remove(card);
            targetPlayer.addCardToHand(card);
            return true;
        }
        return false;
    }

    /**
     * 移动到另一个板块
     * @return 是否成功移动
     */
    public boolean moveToTile(Tile destinationTile) {
        // 检查移动是否合法
        if (canMoveTo(destinationTile)) {
            this.currentTile = destinationTile;
            return true;
        }
        return false;
    }

    /**
     * 检查是否可以移动到目标板块
     */
    public boolean canMoveTo(Tile destinationTile) {
        // 如果板块已沉没或为null，则不能移动到该位置
        if (destinationTile == null || destinationTile.isSunk()) {
            return false;
        }
        
        // 自身位置空值检查
        if (currentTile == null) {
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
            // 使用广度优先搜索算法检查是否可达
            return canDiverReachTile(destinationTile);
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
     * 潜水员路径检查算法 - 检查是否可以通过沉没/淹没板块到达目标
     * 这需要游戏中的板块信息，实际使用时应传入完整的板块网格
     */
    private boolean canDiverReachTile(Tile destinationTile) {
        // 获取游戏板实例
        Board board = Game.getInstance().getBoard();
        if (board == null) {
            return false;
        }
        
        // 如果目标就是当前位置，直接返回true
        if (currentTile.equals(destinationTile)) {
            return true;
        }
        
        // 广度优先搜索算法
        Queue<Tile> queue = new LinkedList<>();
        Set<Tile> visited = new HashSet<>();
        
        // 添加起始点
        queue.offer(currentTile);
        visited.add(currentTile);
        
        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            
            // 获取当前板块的相邻板块，使用Board类提供的方法
            List<Tile> neighbors = board.getAdjacentTiles(current);
            
            for (Tile neighbor : neighbors) {
                // 如果是目标板块且可导航，返回true
                if (neighbor.equals(destinationTile) && neighbor.isNavigable()) {
                    return true;
                }
                
                // 潜水员可以穿过淹没或沉没的板块，但不能停在沉没的板块上
                boolean canPassThrough = neighbor.isFlooded() || neighbor.isSunk();
                
                // 如果板块未访问过且潜水员可以通过
                if (!visited.contains(neighbor) && (canPassThrough || neighbor.isNavigable())) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        
        // 没有找到路径
        return false;
    }

    /**
     * 排水(翻转板块)操作
     * @return 是否消耗行动点
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
        if (tile == null || !tile.isFlooded() || currentTile == null) {
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
     * @return 是否成功使用卡牌
     */
    public boolean useSpecialCard(SpecialCard card) {
        if (card == null) {
            return false;
        }
        
        // 实现使用特殊卡牌的逻辑
        if (hand.contains(card)) {
            card.useCard(this);
            hand.remove(card);
            return true;
        }
        return false;
    }
     
    /**
     * 使用角色特殊能力
     * @return 是否成功使用特殊能力
     */
    public boolean useSpecialAbility(Tile destinationTile) {
        // 检查角色是否已设置
        if (role == null) {
            return false;
        }
        
        // 检查是否有足够的行动点
        if (remainingActions <= 0) {
            return false;
        }
        
        // 根据角色类型使用特殊能力
        switch (role) {
            case PILOT:
                // 飞行员特殊能力：飞到任意板块
                if (!pilotAbilityUsed && destinationTile != null && destinationTile.isNavigable()) {
                    currentTile = destinationTile;
                    pilotAbilityUsed = true;
                    useAction(); // 消耗一个行动点
                    return true;
                }
                break;
                
            case ENGINEER:
                // 工程师特殊能力已在shoreUpTile方法中实现
                if (destinationTile != null && destinationTile.isFlooded()) {
                    return shoreUpTile(destinationTile);
                }
                break;
                
            case NAVIGATOR:
                // 领航员特殊能力：移动其他玩家
                try {
                    // 需要在控制器层实现具体逻辑
                    role.useSpecialAbility(this, destinationTile);
                    useAction();
                    return true;
                } catch (Exception e) {
                    System.err.println("导航员能力使用异常: " + e.getMessage());
                    return false;
                }
                
            case DIVER:
                // 潜水员特殊能力：穿越沉没板块移动
                if (destinationTile != null && canDiverReachTile(destinationTile)) {
                    currentTile = destinationTile;
                    useAction();
                    return true;
                }
                break;
                
            case MESSENGER:
                // 信使特殊能力：跨板块传递宝藏卡
                try {
                    // 需要在控制器层实现具体逻辑
                    role.useSpecialAbility(this, destinationTile);
                    useAction();
                    return true;
                } catch (Exception e) {
                    System.err.println("信使能力使用异常: " + e.getMessage());
                    return false;
                }
                
            case EXPLORER:
                // 探险家特殊能力：对角线移动和排水
                // 根据目标板块状态决定是移动还是排水
                if (destinationTile != null) {
                    if (destinationTile.isFlooded() && canShoreUp(destinationTile)) {
                        boolean actionUsed = shoreUpTile(destinationTile);
                        return !actionUsed || useAction(); // 如果消耗行动点，则使用一个
                    } else if (destinationTile.isNavigable() && canMoveTo(destinationTile)) {
                        currentTile = destinationTile;
                        useAction();
                        return true;
                    }
                }
                break;
        }
        
        return false;
    }
    
    /**
     * 检查是否可以给另一个玩家传递卡牌
     */
    public boolean canGiveCardTo(Player targetPlayer) {
        if (targetPlayer == null || currentTile == null || targetPlayer.getCurrentTile() == null) {
            return false;
        }
        
        // 信使可以在任何位置传递卡牌
        if (role == Role.MESSENGER) {
            return true;
        }
        
        // 普通玩家只能在同一板块传递卡牌
        return isSameTile(currentTile, targetPlayer.getCurrentTile());
    }
    
    /**
     * 安全比较两个板块是否为同一板块
     */
    private boolean isSameTile(Tile tile1, Tile tile2) {
        if (tile1 == null || tile2 == null) {
            return false;
        }
        
        // 比较坐标而不是引用，避免可能的equals问题
        return tile1.getRow() == tile2.getRow() && tile1.getCol() == tile2.getCol();
    }
    
    /**
     * 获取可以移动到的所有板块
     */
    public List<Tile> getPossibleMoves(Board board) {
        if (board == null || currentTile == null) {
            return new ArrayList<>();
        }
        
        List<Tile> possibleMoves = new ArrayList<>();
        
        // 使用Board.getAllTiles()获取所有板块
        List<Tile> allTiles = board.getAllTiles();
        
        for (Tile tile : allTiles) {
            if (canMoveTo(tile)) {
                possibleMoves.add(tile);
            }
        }
        
        return possibleMoves;
    }
    
    /**
     * 获取可以排水的所有板块
     */
    public List<Tile> getPossibleShoreUps(Board board) {
        if (board == null || currentTile == null) {
            return new ArrayList<>();
        }
        
        List<Tile> possibleShoreUps = new ArrayList<>();
        
        // 使用Board.getAllTiles()获取所有板块
        List<Tile> allTiles = board.getAllTiles();
        
        for (Tile tile : allTiles) {
            if (canShoreUp(tile)) {
                possibleShoreUps.add(tile);
            }
        }
        
        return possibleShoreUps;
    }

    public boolean moveOtherPlayer(Player targetPlayer, Tile destinationTile) {
        if (role != Role.NAVIGATOR || targetPlayer == null || destinationTile == null) {
            return false;
        }
        
        // 检查移动距离是否在1-2步内
        int distance = calculateDistance(targetPlayer.getCurrentTile(), destinationTile);
        if (distance > 0 && distance <= 2 && destinationTile.isNavigable()) {
            targetPlayer.setCurrentTile(destinationTile);
            useAction();
            return true;
        }
        return false;
    }
    
    private int calculateDistance(Tile from, Tile to) {
        if (from == null || to == null) return -1;
        return Math.abs(from.getRow() - to.getRow()) + Math.abs(from.getCol() - to.getCol());
    }

    public boolean rescuePlayer(Player sinkingPlayer) {
        // 检查是否在同一板块
        if (!isSameTile(currentTile, sinkingPlayer.getCurrentTile())) {
            return false;
        }
        
        // 寻找可移动的安全板块
        Board board = Game.getInstance().getBoard();
        List<Tile> possibleTiles = new ArrayList<>();
        
        for (Tile tile : board.getAdjacentTiles(currentTile)) {
            if (tile.isNavigable()) {
                possibleTiles.add(tile);
            }
        }
        
        // 如果有安全板块，随机选择一个
        if (!possibleTiles.isEmpty()) {
            Tile safeTile = possibleTiles.get(0);
            sinkingPlayer.setCurrentTile(safeTile);
            useAction();
            return true;
        }
        
        return false;
    }

    public boolean useHelicopterLiftCard(HelicopterLiftCard card, List<Player> playersToLift, Tile destinationTile) {
        if (!hand.contains(card) || destinationTile == null || !destinationTile.isNavigable()) {
            return false;
        }
        
        // 移动所有玩家到目标板块
        for (Player player : playersToLift) {
            player.setCurrentTile(destinationTile);
        }
        
        // 使用并丢弃卡牌
        hand.remove(card);
        return true;
    }
}
//asd

