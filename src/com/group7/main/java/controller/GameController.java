package controller;

import lombok.Getter;
import model.*;
import model.card.*;
import view.gamePanel.MainFrame;
import model.enums.GameState;

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
    public void handlePlayerMove(Player player, Tile destination) {
        if (player.getRemainingActions() > 0 && player.moveToTile(destination)) {
            mainFrame.addConsoleMessage("玩家 " + player.getPlayerId() + " 移动到了 " + destination);
            player.useAction();
            checkAfterPlayerAction(player);
        }
    }

    // 处理排水
    public void handlePlayerShoreUp(Player player, Tile tile) {
        if (player.getRemainingActions() > 0 && tile.isFlooded() && !tile.isSunk()) {
            tile.shoreUp();
            mainFrame.addConsoleMessage("玩家 " + player.getPlayerId() + " 对 " + tile.getType().getDisplayName() + " 排水");
            player.useAction();
            checkAfterPlayerAction(player);
            mainFrame.updateBoard(game.getBoard());
        }
    }

    // 使用特殊卡
    public void handlePlayerUseCard(Player player, SpecialCard card) {
        player.useSpecialCard(card);
        mainFrame.addConsoleMessage("玩家 " + player.getPlayerId() + " 使用了特殊卡: " + card.getName());
        // 特殊卡用完记得检查是否要立刻进入下一步
        checkAfterPlayerAction(player);
    }

    // 每次行动后调用：用完行动点就进入抽牌/下阶段
    private void checkAfterPlayerAction(Player player) {
        if (player.getRemainingActions() <= 0) {
            mainFrame.addConsoleMessage("玩家" + player.getPlayerId() + " 行动完毕！进入新阶段。");
            handleDrawTreasurePhase();
        } else {
            mainFrame.addConsoleMessage("剩余行动：" + player.getRemainingActions());
        }
    }


    /************ 回合、阶段主流程 ************/
    // 抽宝藏卡阶段
    private void handleDrawTreasurePhase() {
        Player curr = game.getCurrentPlayer();
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
        int count = game.getWaterLevel().getFloodCardsCount();
        for (int i = 0; i < count; i++) {
            game.drawFloodCards(); // 内部已处理flood状态与联动
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
        // 正常进行下一轮
        handleStartPlayerTurn();
    }

    // 新玩家回合开始
    private void handleStartPlayerTurn() {
        // 回合由TurnManager推进
        game.getTurnManager().nextPhase(); // 切换到下个玩家和阶段
        Player curr = game.getCurrentPlayer();
        curr.resetActionsForTurn();
        mainFrame.addConsoleMessage("轮到玩家 " + curr.getPlayerId());
        updateAllUI();
    }

    // 结束玩家回合（如由按钮控制）
    public void endPlayerTurn() {
        mainFrame.addConsoleMessage("玩家 " + game.getCurrentPlayer().getPlayerId() + " 主动结束本回合。");
        handleDrawTreasurePhase(); // 直接进入抽宝藏卡阶段
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
        // 可拓展：更新玩家信息面板、卡牌面板等
    }



}