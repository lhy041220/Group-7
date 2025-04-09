package model;

import lombok.Setter;
import model.card.*;
import model.enums.*;

import lombok.Getter;
import model.enums.GameState;

import java.util.List;
import java.util.ArrayList;

@Getter
public class Game {

    private static Game instance;
    public static Game getInstance() {
        return instance == null ? new Game() : instance;
    }

    private Board board;
    private WaterLevel waterLevel;
    private List<Player> players;
    private boolean gameOver;
    private Deck<HandCard> treasureDeck;

    private Deck<FloodCard> floodDeck;
    private TurnManager turnManager;

    private int currentPlayerIndex;

    @Setter
    private GameState gameState;

    private Game() {
        board = new Board();
        waterLevel = new WaterLevel();
        players = new ArrayList<>();
        treasureDeck = new Deck<>();
        floodDeck = new Deck<>();
        currentPlayerIndex = 0;
        gameOver = false;
        turnManager = new TurnManager(this, players);

        // 初始化牌库
        initializeDecks();
    }

    private void initializeDecks() {
        // 初始化宝藏牌库
        // 初始化特殊卡牌库
        // 初始化洪水牌库
    }

    private void initializePlayers(int numPlayers) {
        // 创建玩家并安置到起始瓦片
    }

    public void startGame(int numPlayers) {
        // 初始化玩家位置
        if (1 < numPlayers && numPlayers < 5) {
            initializePlayers(numPlayers);
        } else {
            throw new IllegalArgumentException("Invalid number of players. Must be between 1 and 4.");
        }

        gameState = GameState.RUNNING;

        // 初始化玩家list
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player(i + 1, board.getTileByType(TileType.HOWLING_GARDEN));
            players.add(player);
        }

        // 洗牌
        treasureDeck.shuffle();
        floodDeck.shuffle();

        // 初始洪水
        drawInitialFloodCards();

        // 给玩家发初始卡牌
        dealInitialCards();

        turnManager.startPlayerTurn();
    }

    /**
     * 回合通知方法
     */
    public void notifyTurnStarted(Player player) {
        // 通知GameController更新UI
    }

    public void notifyPhaseChanged(TurnManager.TurnPhase phase) {
        // 通知GameController更新UI
    }

    public void notifyPlayerMustDiscard(Player player) {
        // 通知GameController让玩家选择丢弃的卡牌
    }

    /**
     * 检查游戏胜利条件
     */
    public boolean checkWinCondition() {
        // 实现胜利条件检查逻辑
        return false;
    }

    /**
     * 检查游戏失败条件
     */
    public boolean checkLoseCondition() {
        // 实现失败条件检查逻辑
        return false;
    }

    /**
     * 淹没板块
     */
    public void floodTile(Tile tile) {
        // 实现瓦片淹没逻辑
    }

    private void drawInitialFloodCards() {
        // 抽取初始的洪水卡牌并淹没对应的瓦片
    }

    private void dealInitialCards() {
        // 给每个玩家发初始卡牌
    }

    public void playTurn(Player player) {
        // 1. 行动阶段（玩家可以移动、排水、交换卡牌等）
        // 2. 抽取宝藏卡牌
        // 3. 抽取洪水卡牌
        // 4. 水位可能上升

        // 检查游戏结束条件
        checkGameEnd();
    }

    /**
     * 抽取一张宝藏卡
     */
    public HandCard drawTreasureCard() {
        return treasureDeck.drawCard();
    }

    /**
     * 根据当前水位抽取对应数量的洪水卡牌
     */
    public void drawFloodCards() {
        int cardsToDrawCount = waterLevel.getFloodCardsCount();
        for (int i = 0; i < cardsToDrawCount; i++) {
            FloodCard card = floodDeck.drawCard();
            if (card != null) {
                // 处理洪水卡牌效果
                Tile floodedTile = board.getTileByType(card.getTileType());
                floodedTile.flood();

                // 检查瓦片上是否有玩家，可能需要救援
                checkPlayersOnFloodedTile(floodedTile);
            }
        }
    }

    private void checkPlayersOnFloodedTile(Tile floodedTile) {
        // 检查并处理在被淹没瓦片上的玩家
    }

    private void checkGameEnd() {
        // 检查胜利条件：是否收集了所有宝藏并且全部玩家到达直升机起飞点

        // 检查失败条件
        // 1. Fool's Landing被淹没
        // 2. 任何玩家溺水且无法被救援
        // 3. 水位达到最高点
        // 4. 某种宝藏的全部收集卡都无法获得
    }

    public void increaseWaterLevel() {
        boolean isGameOver = !waterLevel.tryRise();
        // 检查水位上升后是否导致游戏结束
        if (isGameOver) {
            gameOver = true;
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
}
