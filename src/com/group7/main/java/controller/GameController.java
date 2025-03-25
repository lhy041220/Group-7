package controller;

import lombok.Getter;
import model.*;
import model.card.*;

public class GameController {
    @Getter
    private Game game;

    public GameController() {
        // 默认构造函数
    }

    public void initializeGame(int numPlayers) {
        game = new Game(numPlayers);
    }

    public void startGame() {
        if (game != null) {
            game.startGame();
        }
    }

    public void handlePlayerMove(Player player, Tile destination) {
        // 处理玩家移动
        player.moveToTile(destination);
    }

    public void handlePlayerShoreUp(Player player, Tile tile) {
        // 处理玩家排水
        if (tile.isFlooded() && !tile.isSunk()) {
            tile.shoreUp();
        }
    }

    public void handlePlayerUseCard(Player player, SpecialCard card) {
        // 处理玩家使用特殊卡牌
        player.useSpecialCard(card);
    }

    public void endPlayerTurn() {
        // 结束当前玩家的回合，抽取卡牌并执行洪水阶段
        Player currentPlayer = game.getCurrentPlayer();
        game.playTurn(currentPlayer);
    }
}