package model;

import lombok.Setter;
import model.card.*;
import model.enums.*;
import model.card.HelicopterLiftCard;

import lombok.Getter;
import model.enums.GameState;

import java.util.List;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

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

    // 检查是否获得某个宝藏
    private boolean hasTreasure(TreasureType treasureType) {
        // 你的代码没有全局已获得宝藏集合，所以遍历所有玩家手中手牌
        for (Player player : players) {
            if (player.hasCollectedTreasure(treasureType)) return true;
        }
        return false;
    }

    // 检查所有玩家是否都在愚者着陆点
    private boolean allPlayersAtHelipad() {
        for (Player player : players) {
            Tile curr = player.getCurrentTile();
            if (curr == null || curr.getType() != TileType.FOOLS_LANDING) return false;
        }
        return true;
    }

    // 任一玩家是否有直升机牌
    private boolean anyPlayerHasHelicopter() {
        for (Player player : players) {
            for (Card card : player.getHand()) {
                if (card instanceof HelicopterLiftCard) return true;
            }
        }
        return false;
    }

    /**
     * 检查游戏胜利条件（有四宝、全员到愚者着陆点、手里有直升机卡）
     */
    public boolean checkWinCondition() {
        return hasTreasure(TreasureType.EARTH) &&
                hasTreasure(TreasureType.WIND) &&
                hasTreasure(TreasureType.FIRE) &&
                hasTreasure(TreasureType.OCEAN) &&
                allPlayersAtHelipad() &&
                anyPlayerHasHelicopter();
    }

    /**
     * 检查游戏失败条件
     */
    public boolean checkLoseCondition() {
        // 1. 愚者着陆点沉没
        if (board.isHelipadSunk()) return true;

        // 2. 某种宝藏未获得且两块宝藏板块全部沉没
        for (TreasureType type : TreasureType.values()) {
            if (type == TreasureType.NONE) continue;
            if (!hasTreasure(type) && board.isAllTreasureTilesSunk(type)) return true;
        }

        // 3. 有玩家无法移动（被困，不可达，也可补充diver特例）
        for (Player player : players) {
            if (player.getCurrentTile() == null || !player.getCurrentTile().isNavigable()) {
                // 检查相邻可通行Tile
                boolean canEscape = false;
                for (Tile adj : board.getAdjacentTiles(player.getCurrentTile())) {
                    if (adj != null && adj.isNavigable()) {
                        canEscape = true; break;
                    }
                }
                if (!canEscape) return true;
            }
        }

        // 4. 水位到顶
        if (waterLevel.getCurrentLevel() >= 9) return true;

        // 5. 宝藏牌抽光且弃牌堆也空，并且还没赢
        if (treasureDeck.getRemainingCards() == 0 && treasureDeck.getDiscardedCards() == 0) return true;

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
