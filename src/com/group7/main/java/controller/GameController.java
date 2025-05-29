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

    public enum OperationMode { NONE, MOVE, SHORE_UP, SPECIAL_ABILITY, USE_CARD, NAVIGATOR_SELECT_PLAYER, NAVIGATOR_SELECT_TILE }
    private OperationMode currentMode = OperationMode.NONE;
    private int navigatorTargetPlayerIdx = -1;

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

    public void startGame(int numPlayers) {
        if (game != null && mainFrame != null) {
            game.startGame(numPlayers);
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
        // 检查手牌超限
        if (player.handExceedsLimit()) {
            // 弹窗选择丢弃卡牌
            java.util.List<model.card.Card> hand = player.getHand();
            String[] options = new String[hand.size()];
            for (int i = 0; i < hand.size(); i++) {
                options[i] = hand.get(i).getName();
            }
            String discard = (String) javax.swing.JOptionPane.showInputDialog(null, "手牌超限，请选择要丢弃的卡牌：", "丢弃卡牌", javax.swing.JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (discard != null) {
                for (model.card.Card card : hand) {
                    if (card.getName().equals(discard)) {
                        player.discardCard(card);
                        mainFrame.addConsoleMessage("玩家" + player.getPlayerId() + "丢弃了卡牌：" + card.getName());
                        break;
                    }
                }
                mainFrame.updatePlayerHand(player.getHand());
            }
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
        highlightMovableTiles();
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
        int res = JOptionPane.showConfirmDialog(mainFrame, "是否重新开始新游戏？", "重新开始", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            // 重新启动
            String[] options = {"2", "3", "4"};
            String numStr = (String) JOptionPane.showInputDialog(null, "请选择玩家人数：", "玩家人数", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            int playerNum = 3;
            try { if (numStr != null) playerNum = Integer.parseInt(numStr); } catch (Exception e) {}
            game.startGame(playerNum);
            updateAllUI();
        }
    }

    // 刷新所有与UI相关内容
    private void updateAllUI() {
        mainFrame.updateBoard(game.getBoard());
        mainFrame.updateWaterLevel(game.getWaterLevel().getCurrentLevel());
        mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());
        mainFrame.updatePlayerHand(game.getCurrentPlayer().getHand());
        mainFrame.getPlayerInfoPanel().updateCollectedTreasures(game.getCollectedTreasures());
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

    // 处理直升机卡
    public void handlePlayerUseHelicopterLift(model.card.SpecialCard card, int row, int col) {
        Player player = game.getCurrentPlayer();
        model.Board board = game.getBoard();
        model.Tile dest = null;
        try { dest = board.getTile(row, col); } catch (Exception e) {}
        if (dest != null && dest.isNavigable()) {
            // 简化：只移动当前玩家，实际可扩展为多人
            player.setCurrentTile(dest);
            player.discardCard(card);
            mainFrame.addConsoleMessage("使用直升机卡，玩家" + player.getPlayerId() + "移动到(" + row + "," + col + ")");
            mainFrame.updateBoard(game.getBoard());
            mainFrame.updatePlayerHand(player.getHand());
            // 检查胜利条件
            if (game.checkWinCondition()) {
                announceGameEnd(true, "使用直升机卡，全员登船，胜利！");
            }
        } else {
            mainFrame.addConsoleMessage("直升机卡目标无效或不可达");
        }
    }
    // 处理沙袋卡
    public void handlePlayerUseSandbag(model.card.SpecialCard card, int row, int col) {
        model.Board board = game.getBoard();
        model.Tile dest = null;
        try { dest = board.getTile(row, col); } catch (Exception e) {}
        if (dest != null && dest.isFlooded()) {
            dest.shoreUp();
            game.getCurrentPlayer().discardCard(card);
            mainFrame.addConsoleMessage("使用沙袋卡，排水(" + row + "," + col + ")");
            mainFrame.updateBoard(game.getBoard());
            mainFrame.updatePlayerHand(game.getCurrentPlayer().getHand());
        } else {
            mainFrame.addConsoleMessage("沙袋卡目标无效或该格不可排水");
        }
    }

    // 高亮可移动格子
    public void highlightMovableTiles() {
        Player player = game.getCurrentPlayer();
        model.Board board = game.getBoard();
        java.util.List<model.Tile> moves = player.getPossibleMoves(board);
        java.util.List<int[]> positions = new java.util.ArrayList<>();
        for (model.Tile t : moves) {
            positions.add(new int[]{t.getRow(), t.getCol()});
        }
        mainFrame.highlightBoardTiles(positions);
    }
    // 清除高亮
    public void clearBoardHighlight() {
        mainFrame.clearBoardHighlight();
    }

    // 高亮可排水格子
    public void highlightShoreUpTiles() {
        Player player = game.getCurrentPlayer();
        model.Board board = game.getBoard();
        java.util.List<model.Tile> shoreUps = player.getPossibleShoreUps(board);
        java.util.List<int[]> positions = new java.util.ArrayList<>();
        for (model.Tile t : shoreUps) {
            positions.add(new int[]{t.getRow(), t.getCol()});
        }
        mainFrame.highlightBoardTiles(positions);
    }
    // 进入排水模式
    public void enterShoreUpMode() {
        currentMode = OperationMode.SHORE_UP;
        highlightShoreUpTiles();
    }
    // 进入移动模式
    public void enterMoveMode() {
        currentMode = OperationMode.MOVE;
        highlightMovableTiles();
    }
    // 进入领航员移动他人模式
    public void enterNavigatorMode() {
        currentMode = OperationMode.NAVIGATOR_SELECT_PLAYER;
        mainFrame.addConsoleMessage("请选择要移动的目标玩家");
        // 可在UI高亮其他玩家
    }
    // 高亮目标玩家可移动格
    public void highlightNavigatorTargetTiles(Player target) {
        java.util.List<model.Tile> moves = target.getPossibleMoves(game.getBoard());
        java.util.List<int[]> positions = new java.util.ArrayList<>();
        for (model.Tile t : moves) {
            positions.add(new int[]{t.getRow(), t.getCol()});
        }
        mainFrame.highlightBoardTiles(positions);
    }
    // 处理地图点击
    public void handleTileClick(int row, int col) {
        if (currentMode == OperationMode.NAVIGATOR_SELECT_PLAYER) {
            // 判断是否点击了某玩家所在格
            for (int i = 0; i < game.getPlayers().size(); i++) {
                Player p = game.getPlayers().get(i);
                if (p != game.getCurrentPlayer() && p.getCurrentTile() != null && p.getCurrentTile().getRow() == row && p.getCurrentTile().getCol() == col) {
                    navigatorTargetPlayerIdx = i;
                    currentMode = OperationMode.NAVIGATOR_SELECT_TILE;
                    highlightNavigatorTargetTiles(p);
                    mainFrame.addConsoleMessage("请选择目标格子");
                    return;
                }
            }
        } else if (currentMode == OperationMode.NAVIGATOR_SELECT_TILE) {
            if (navigatorTargetPlayerIdx >= 0) {
                Player target = game.getPlayers().get(navigatorTargetPlayerIdx);
                java.util.List<int[]> highlights = new java.util.ArrayList<>();
                for (model.Tile t : target.getPossibleMoves(game.getBoard())) {
                    highlights.add(new int[]{t.getRow(), t.getCol()});
                }
                for (int[] pos : highlights) {
                    if (pos[0] == row && pos[1] == col) {
                        model.Tile dest = game.getBoard().getTile(row, col);
                        target.moveToTile(dest);
                        mainFrame.addConsoleMessage("领航员将玩家" + target.getPlayerId() + "移动到(" + row + "," + col + ")");
                        mainFrame.updateBoard(game.getBoard());
                        clearBoardHighlight();
                        currentMode = OperationMode.NONE;
                        navigatorTargetPlayerIdx = -1;
                        return;
                    }
                }
            }
        } else if (currentMode == OperationMode.MOVE) {
            java.util.List<int[]> highlights = new java.util.ArrayList<>();
            for (model.Tile t : game.getCurrentPlayer().getPossibleMoves(game.getBoard())) {
                highlights.add(new int[]{t.getRow(), t.getCol()});
            }
            for (int[] pos : highlights) {
                if (pos[0] == row && pos[1] == col) {
                    model.Tile dest = game.getBoard().getTile(row, col);
                    handlePlayerMove(dest);
                    highlightMovableTiles();
                    return;
                }
            }
        } else if (currentMode == OperationMode.SHORE_UP) {
            java.util.List<int[]> highlights = new java.util.ArrayList<>();
            for (model.Tile t : game.getCurrentPlayer().getPossibleShoreUps(game.getBoard())) {
                highlights.add(new int[]{t.getRow(), t.getCol()});
            }
            for (int[] pos : highlights) {
                if (pos[0] == row && pos[1] == col) {
                    model.Tile dest = game.getBoard().getTile(row, col);
                    handlePlayerShoreUp(dest);
                    highlightShoreUpTiles();
                    return;
                }
            }
        }
        // 其他模式可扩展
    }
}