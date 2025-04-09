package model;

import lombok.Getter;
import model.card.*;
import model.enums.CardType;
import model.enums.GameState;

import java.util.List;

// 回合管理
public class TurnManager {
    private Game game;
    private List<Player> players;
    private int currentPlayerIndex;

    @Getter
    private int roundNumber;
    @Getter
    private TurnPhase currentPhase;

    public enum TurnPhase {
        ACTIONS,         // 玩家行动阶段
        DRAW_TREASURE,   // 抽取宝藏卡阶段
        DRAW_FLOOD,      // 抽取洪水卡阶段
        END              // 回合结束阶段
    }

    public TurnManager(Game game, List<Player> players) {
        this.game = game;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.roundNumber = 1;
        this.currentPhase = TurnPhase.ACTIONS;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // 开始一个玩家的回合
    public void startPlayerTurn() {
        Player currentPlayer = getCurrentPlayer();
        currentPhase = TurnPhase.ACTIONS;
        // 重置玩家的行动点数等
        currentPlayer.resetActionsForTurn();

        // 通知GameController更新UI或通知玩家
        game.notifyTurnStarted(currentPlayer);
    }

    // 玩家执行行动
    public void performAction(String actionType) {
        Player currentPlayer = getCurrentPlayer();

        // 根据actionType执行不同的行动
        switch (actionType) {
            case "MOVE":
                // 移动逻辑
                currentPlayer.useAction();
                break;
            case "SHORE_UP":
                // 加固逻辑
                currentPlayer.useAction();
                break;
            case "GIVE_CARD":
                // 给予卡牌逻辑
                currentPlayer.useAction();
                break;
            case "CAPTURE_TREASURE":
                // 获取宝藏逻辑
                currentPlayer.useAction();
                break;
            // 可以添加其他行动类型
        }

        // 检查玩家是否用完了行动点或选择结束行动阶段
        if (currentPlayer.getRemainingActions() <= 0) {
            nextPhase();
        }
    }

    // 进入下一个阶段
    public void nextPhase() {
        switch (currentPhase) {
            case ACTIONS:
                currentPhase = TurnPhase.DRAW_TREASURE;
                drawTreasureCards();
                break;

            case DRAW_TREASURE:
                currentPhase = TurnPhase.DRAW_FLOOD;
                drawFloodCards();
                break;

            case DRAW_FLOOD:
                currentPhase = TurnPhase.END;
                endTurn();
                break;

            case END:
                // 移动到下一个玩家
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

                // 如果回到第一个玩家，回合数+1
                if (currentPlayerIndex == 0) {
                    roundNumber++;
                }

                // 开始新玩家的回合
                startPlayerTurn();
                break;
        }

        // 通知GameController阶段改变
        game.notifyPhaseChanged(currentPhase);
    }

    // 抽取宝藏卡
    private void drawTreasureCards() {
        Player currentPlayer = getCurrentPlayer();

        // 假设每回合抽2张宝藏卡
        int cardsToDraw = 2;
        for (int i = 0; i < cardsToDraw; i++) {
            Card card = game.drawTreasureCard();
            if (card != null) {
                currentPlayer.addCardToHand(card);
                // 处理特殊卡牌效果
                if (card.getType() == CardType.SPECIAL) {
                    // 处理特殊卡牌逻辑
                }
            }
        }

        // 检查手牌上限
        if (currentPlayer.handExceedsLimit()) {
            // 让玩家丢弃卡牌，可能需要通过GameController进行交互
            game.notifyPlayerMustDiscard(currentPlayer);
            // 此时可能需要暂停回合流程，等待玩家选择丢弃
            // 丢弃完成后再调用nextPhase()
        } else {
            nextPhase();
        }
    }

    /**
     * 根据当前水位确定抽取的洪水卡数量
     */
    private void drawFloodCards() {
        int cardsToDraw = game.getWaterLevel().getFloodCardsCount();
        for (int i = 0; i < cardsToDraw; i++) {
            game.drawFloodCards();
        }
        nextPhase();
    }

    // 结束当前回合
    private void endTurn() {
        // 回合结束时的各种检查
        // 1. 检查游戏胜利条件
        if (game.checkWinCondition()) {
            game.setGameState(GameState.WON);
            return;
        }

        // 2. 检查游戏失败条件
        if (game.checkLoseCondition()) {
            game.setGameState(GameState.LOST);
            return;
        }

        // 继续游戏
        nextPhase();
    }

    // 立即结束当前玩家的回合（可用于某些特殊卡牌效果）
    public void forceEndTurn() {
        currentPhase = TurnPhase.END;
        endTurn();
    }

}
