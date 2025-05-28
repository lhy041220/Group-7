package controller;

import lombok.Getter;
import model.*;
import model.card.*;
import view.gamePanel.MainFrame;
import model.enums.GameState;
import model.TurnManager.ActionType;

import javax.swing.*;

public class GameController {
    @Getter
    private Game game;
    private MainFrame mainFrame;


    public GameController(Game game, MainFrame mainFrame) {
        // 默认构造函数
        this.game = game;
        this.mainFrame = mainFrame;
    }

    public void initializeViewFrame() {
        if (mainFrame != null) {
            SwingUtilities.invokeLater(() -> {
                mainFrame.setVisible(true);
            });
        }
    }

    public void startGame() {
        if (game != null && mainFrame != null) {
            game.startGame(getPlayerNum());
            mainFrame.updateBoard(game.getBoard());
            // 通知回合开始
            handleStartPlayerTurn();
        }
    }

    private int getPlayerNum() {
        // TODO: 从mainFame中获取玩家数量
        if (mainFrame != null) {

        }
        return 3;
    }

    // 处理玩家主动移动
    public void handlePlayerMove(Tile destination) {
        Player player = game.getCurrentPlayer();
        if (player.getRemainingActions() > 0) {
            // 直接调用player的移动方法
            if (player.moveToTile(destination)) {
                mainFrame.addConsoleMessage("玩家" + player.getPlayerId() + " 移动到了" + destination);
                player.useAction();
                mainFrame.updateBoard(game.getBoard());
                checkAfterPlayerAction(player);
            }
        }
    }

    // 处理排水
    public void handlePlayerShoreUp(Tile tile) {
        Player player = game.getCurrentPlayer();
        if (player.getRemainingActions() > 0 && tile.isFlooded() && !tile.isSunk()) {
            // 直接调用player的排水方法
            if (player.shoreUp(tile)) {
                mainFrame.addConsoleMessage("玩家 " + player.getPlayerId() + " 对 " + tile.getType().getDisplayName() + " 排水");
                mainFrame.updateBoard(game.getBoard());
                checkAfterPlayerAction(player);
            }
        }
    }

    // 使用特殊卡
    public void handlePlayerUseCard(SpecialCard card) {
        Player player = game.getCurrentPlayer();
        if (card.use(player)) {
            mainFrame.addConsoleMessage("玩家 " + player.getPlayerId() + " 使用了特殊卡: " + card.getName());
            checkAfterPlayerAction(player);
        }
    }

    // 每次行动后调用：用完行动点就进入抽牌/下阶段
    private void checkAfterPlayerAction(Player player) {
        // 更新UI显示
        mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());

        // 打印调试信息
        System.out.println("检查行动后状态 - 玩家" + player.getPlayerId() + "剩余行动点：" + player.getRemainingActions());

        if (player.getRemainingActions() <= 0) {
            mainFrame.addConsoleMessage("玩家" + player.getPlayerId() + " 行动完毕！");
            // 弹窗通知
            JOptionPane.showMessageDialog(mainFrame,
                    "玩家" + player.getPlayerId() + "行动点已用完，即将进入抽牌阶段",
                    "回合阶段提示",
                    JOptionPane.INFORMATION_MESSAGE);
            // 进入下一阶段
            handleDrawTreasurePhase();
        } else {
            mainFrame.addConsoleMessage("剩余行动点：" + player.getRemainingActions());
        }
    }


    /************ 回合、阶段主流程 ************/
    // 抽宝藏卡阶段
    private void handleDrawTreasurePhase() {
        Player curr = game.getCurrentPlayer();

        // 弹窗通知进入抽牌阶段
        JOptionPane.showMessageDialog(mainFrame,
                "玩家" + curr.getPlayerId() + "进入抽宝藏卡阶段",
                "回合阶段提示",
                JOptionPane.INFORMATION_MESSAGE);

        boolean mustDiscard = false;
        for (int i = 0; i < 2; i++) {
            HandCard card = game.drawTreasureCard();
            if (card == null) {
                announceGameEnd(false, "宝藏牌抽光，游戏失败。");
                return;
            }
            curr.addCardToHand(card);
            mainFrame.addConsoleMessage("抽到了卡牌：" + card.getName());
            // 特殊卡立即处理
            if (card instanceof SpecialCard) {
                mainFrame.addConsoleMessage("请立即处理特殊卡：" + card.getName());
                // 你可在这里处理卡牌即时效果
            }
        }

        // 检查手牌上限
        if (curr.handExceedsLimit()) {
            // 让玩家丢卡，这里只打印日志，可扩展弹窗交互
            mainFrame.addConsoleMessage("玩家"+curr.getPlayerId()+"手牌超限，请丢弃到5张。");
            mustDiscard = true;
            // 需配合UI完成丢弃流程，完成后进入下一阶段
        }

        if (!mustDiscard) {
            handleFloodPhase();
        }
    }

    // 洪水阶段
    private void handleFloodPhase() {
        // 弹窗通知进入洪水阶段
        JOptionPane.showMessageDialog(mainFrame,
                "进入洪水阶段",
                "回合阶段提示",
                JOptionPane.INFORMATION_MESSAGE);

        int count = game.getWaterLevel().getFloodCardsCount();
        for (int i = 0; i < count; i++) {
            game.drawFloodCards();// 内部已处理flood状态与联动
        }
        mainFrame.updateBoard(game.getBoard());

        // 判定胜负
        if (game.checkWinCondition()) {
            announceGameEnd(true, "4宝+全员登船+直升机，胜利！");
            return;
        }
        if (game.checkLoseCondition()) {
            announceGameEnd(false, "游戏失败条件达成！");
            return;
        }
        // 正常进行下一轮：切换玩家并开始新回合
        advanceToNextPlayerTurn();
    }

    /**
     * 推进到下一个玩家
     */
    private void advanceToNextPlayerTurn() {
        // 让Game更新当前玩家索引
        int nextIndex = (game.getCurrentPlayerIndex() + 1) % game.getPlayers().size();
        game.setCurrentPlayerIndex(nextIndex);

        // 弹窗通知玩家更换
        JOptionPane.showMessageDialog(mainFrame,
                "轮到玩家 " + (nextIndex + 1) + " 的回合",
                "玩家更换提示",
                JOptionPane.INFORMATION_MESSAGE);

        handleStartPlayerTurn();
    }

    // 新玩家回合开始
    private void handleStartPlayerTurn() {
        Player curr = game.getCurrentPlayer();
        curr.resetActionsForTurn();
        mainFrame.addConsoleMessage("轮到玩家 " + curr.getPlayerId());
        updateAllUI();
        // 通知游戏回合开始
        game.notifyTurnStarted(curr);
    }

    // 结束玩家回合（如由按钮控制）
    public void endPlayerTurn() {
        mainFrame.addConsoleMessage("玩家 " + game.getCurrentPlayer().getPlayerId() + " 主动结束本回合。");
        game.getTurnManager().nextPhase(); // 进入下一阶段
    }

    /************ 工具&流程方法 ************/
    // 游戏结束
    private void announceGameEnd(boolean win, String reason) {
        if (win) {
            game.setGameState(GameState.WON);
            JOptionPane.showMessageDialog(mainFrame, "游戏胜利！ " + reason);
        } else {
            game.setGameState(GameState.LOST);
            JOptionPane.showMessageDialog(mainFrame, "游戏失败！ " + reason);
        }
    }

    // 刷新所有与UI相关内容
    private void updateAllUI() {
        mainFrame.updateBoard(game.getBoard());
        mainFrame.updateWaterLevel(game.getWaterLevel().getCurrentLevel());
        mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());
        // 可拓展：更新玩家信息面板、卡牌面板等
    }
}