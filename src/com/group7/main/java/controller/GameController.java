package controller;

import lombok.Getter;
import model.*;
import model.card.*;
import view.gamePanel.MainFrame;
import model.enums.GameState;
import view.gamePanel.ControlPanel;

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
            mainFrame.updatePlayerHand(game.getCurrentPlayer().getHand());
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
        if (player.getRemainingActions() > 0 && player.moveToTile(destination)) {
            mainFrame.addConsoleMessage("玩家" + player.getPlayerId() + " 移动到了" + destination);
            player.useAction();
            checkAfterPlayerAction(player);
        }
    }

    // 处理排水
    public void handlePlayerShoreUp(Tile tile) {
        Player player = game.getCurrentPlayer();
        if (player.getRemainingActions() > 0 && tile.isFlooded() && !tile.isSunk()) {
            tile.shoreUp();
            mainFrame.addConsoleMessage("玩家 " + player.getPlayerId() + " 对 " + tile.getType().getDisplayName() + " 排水");
            player.useAction();
            checkAfterPlayerAction(player);
            mainFrame.updateBoard(game.getBoard());
        }
    }

    // 使用特殊卡
    public void handlePlayerUseCard(SpecialCard card) {
        Player player = game.getCurrentPlayer();
        if (card instanceof model.card.HelicopterLiftCard) {
            // 直升机卡：理论上应弹窗选择玩家和目标格子，这里先输出提示
            mainFrame.addConsoleMessage("使用直升机卡：请选择要移动的玩家和目标格子。");
            // 示例：如果所有玩家都在愚者着陆点且已集齐四宝，直接胜利
            if (game.checkWinCondition()) {
                announceGameEnd(true, "使用直升机卡，全员登船，胜利！");
                return;
            }
            // 其他情况可后续联动UI实现多人移动
        } else if (card instanceof model.card.SandbagCard) {
            // 沙袋卡：理论上应弹窗选择目标格子，这里先输出提示
            mainFrame.addConsoleMessage("使用沙袋卡：请选择要排水的格子。");
            // 后续可联动UI让玩家点选格子
        } else {
            // 其他特殊卡
            player.useSpecialCard(card);
            mainFrame.addConsoleMessage("玩家 " + player.getPlayerId() + " 使用了特殊卡: " + card.getName());
        }
        // 特殊卡用完记得检查是否要立刻进入下一步
        checkAfterPlayerAction(player);
    }

    // 使用职业特殊能力
    public void handleUseSpecialAbility(Tile destination) {
        Player player = game.getCurrentPlayer();
        boolean success = player.useSpecialAbility(destination);
        if (success) {
            mainFrame.addConsoleMessage("玩家" + player.getPlayerId() + "使用了职业特殊能力。");
            mainFrame.updateBoard(game.getBoard());
            checkAfterPlayerAction(player);
        } else {
            mainFrame.addConsoleMessage("职业能力使用失败：请检查目标是否合法或行动点是否足够。");
        }
    }

    // 每次行动后调用：用完行动点就进入抽牌/下阶段
    private void checkAfterPlayerAction(Player player) {
        if (player.getRemainingActions() <= 0) {
            mainFrame.addConsoleMessage("玩家" + player.getPlayerId() + " 行动完毕！进入新阶段。");
            handleDrawTreasurePhase();
        } else {
            mainFrame.addConsoleMessage("剩余行动：" + player.getRemainingActions());
        }
        mainFrame.updatePlayerHand(player.getHand());
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
        handleStartPlayerTurn(); // 切换新玩家，刷新主界面
    }

    // 新玩家回合开始
    private void handleStartPlayerTurn() {
        // 回合由TurnManager推进
        game.getTurnManager().nextPhase(); // 切换到下个玩家和阶段
        Player curr = game.getCurrentPlayer();
        curr.resetActionsForTurn();
        mainFrame.addConsoleMessage("轮到玩家 " + curr.getPlayerId());
        updateAllUI();
        mainFrame.updatePlayerHand(curr.getHand());
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
        mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());
        mainFrame.updatePlayerHand(game.getCurrentPlayer().getHand());
        // 动态设置按钮可用性
        ControlPanel controlPanel = mainFrame.getControlPanel();
        // 这里只做简单示例，实际可根据TurnManager阶段更细致控制
        controlPanel.setMoveButtonEnabled(true);
        controlPanel.setShoreUpButtonEnabled(true);
        controlPanel.setGiveCardButtonEnabled(true);
        controlPanel.setCaptureTreasureButtonEnabled(true);
        controlPanel.setCollectTreasureButtonEnabled(true);
        controlPanel.setUseSpecialAbilityButtonEnabled(true);
        controlPanel.setEndTurnButtonEnabled(true);
    }

    // 处理收集宝藏
    public void handleCollectTreasure() {
        Player player = game.getCurrentPlayer();
        boolean success = player.tryCollectTreasure(game);
        if (success) {
            mainFrame.addConsoleMessage("player" + player.getPlayerId() + "Successfully collect the treasure!");
            mainFrame.updateBoard(game.getBoard());
        } else {
            mainFrame.addConsoleMessage("Failed to collect treasure: Requires 4 corresponding treasure cards in the treasure tile and the treasure has not been collected.");
        }
        mainFrame.updatePlayerHand(player.getHand());
    }
}