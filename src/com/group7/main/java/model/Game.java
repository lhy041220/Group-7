package model;

import lombok.Setter;
import model.card.*;
import model.enums.*;
import model.card.HelicopterLiftCard;
import view.gamePanel.MainFrame;
import view.dialog.DiscardDialog;


import lombok.Getter;
import model.enums.GameState;
import java.util.Queue;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.util.Collections;

@Getter
public class Game {

    private static Game instance;
    private MainFrame mainFrame;
    private boolean afterFloodCardDrawn = false;  // 标记是否刚抽取了洪水卡

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

    @Setter
    private int currentPlayerIndex;

    @Setter
    private GameState gameState;


    private final List<GameEventListener> eventListeners = new ArrayList<>();

    private int round = 1;

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

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void addGameEventListener(GameEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeGameEventListener(GameEventListener listener) {
        eventListeners.remove(listener);
    }

    private void initializeDecks() {
        // 初始化宝藏牌库
        // 初始化特殊卡牌库
        initializeTreasureDeck();
        // 初始化洪水牌库
        initializeFloodDeck();
    }

    /**
     * 初始化宝藏牌库
     * 包括：各种宝藏卡、特殊卡（直升机、沙袋、水位上升）
     */
    private void initializeTreasureDeck() {
        // 添加宝藏卡
        for (TreasureType type : TreasureType.values()) {
            if (type != TreasureType.NONE) {
                // 每种宝藏5张牌
                for (int i = 0; i < 5; i++) {
                    treasureDeck.addCard(new TreasureCard(
                            type.name() + " Card",
                            "A treasure card of type " + type.name(),
                            type
                    ));
                }
            }
        }

        // 添加特殊卡
        // 3张直升机救援卡
        for (int i = 0; i < 3; i++) {
            treasureDeck.addCard(new HelicopterLiftCard());
        }

        // 2张沙袋卡
        for (int i = 0; i < 2; i++) {
            treasureDeck.addCard(new SandbagCard());
        }

        // 3张水位上升卡
        for (int i = 0; i < 3; i++) {
            treasureDeck.addCard(new WaterRiseCard());
        }

        // 洗牌
        treasureDeck.shuffle();
    }

    /**
     * 初始化洪水牌库
     * 每个板块对应一张洪水卡
     */
    private void initializeFloodDeck() {
        // 遍历所有板块类型，为每个板块创建对应的洪水卡
        for (TileType tileType : TileType.values()) {
            if (tileType != TileType.NONE) {
                // 只为实际存在的板块创建洪水卡
                Tile tile = board.getTileByType(tileType);
                if (tile != null) {
                    floodDeck.addCard(new FloodCard(tileType, tileType.name()));
                }
            }
        }
        // 洗牌
        floodDeck.shuffle();
    }

    /**
     * 给每个玩家发初始卡牌
     * 每个玩家2张牌
     */
    private void dealInitialCards() {
        for (Player player : players) {
            for (int i = 0; i < 2; i++) {
                HandCard card = drawTreasureCard();
                if (card != null) {
                    player.addCardToHand(card);
                }
            }
        }
    }

    /**
     * 处理抽到水位上升卡的情况
     * @param card 抽到的水位上升卡
     * @param isSecondCard 是否是同一回合抽到的第二张水位上升卡
     */
    private void handleWaterRiseCard(WaterRiseCard card, boolean isSecondCard) {
        // 如果是第二张水位上升卡，只上升水位，不重洗洪水牌
        if (isSecondCard) {
            waterLevel.tryRise();
        } else {
            // 正常执行水位上升卡效果
            card.useCard(players.get(currentPlayerIndex));
        }
        // 将卡牌放入弃牌堆
        treasureDeck.discard(card);
    }

    /**
     * 抽取宝藏卡并处理特殊情况（如水位上升卡）
     * @return 抽到的卡牌，如果是水位上升卡则返回null
     */
    public HandCard drawTreasureCard() {
        HandCard card = treasureDeck.drawCard();
        if (card == null) {
            return null;
        }

        // 处理水位上升卡
        if (card instanceof WaterRiseCard) {
            handleWaterRiseCard((WaterRiseCard) card, false);
            return null;
        }

        return card;
    }
    /**
     * 重置洪水卡抽取状态
     * 在每个玩家回合开始时调用
     */
    public void resetFloodCardState() {
        this.afterFloodCardDrawn = false;
    }

    /**
     * 玩家回合抽取两张宝藏卡
     * @param player 当前玩家
     */
    public void drawTreasureCardsForTurn(Player player) {
        // 重置洪水卡状态
        resetFloodCardState();
        boolean drewWaterRiseFirst = false;

        // 抽第一张牌
        HandCard firstCard = drawTreasureCard();
        if (firstCard instanceof WaterRiseCard) {
            drewWaterRiseFirst = true;
        } else if (firstCard != null) {
            player.addCardToHand(firstCard);
        }

        // 抽第二张牌
        HandCard secondCard = drawTreasureCard();
        if (secondCard instanceof WaterRiseCard && drewWaterRiseFirst) {
            // 如果是第二张水位上升卡，特殊处理
            handleWaterRiseCard((WaterRiseCard) secondCard, true);
        } else if (secondCard != null) {
            player.addCardToHand(secondCard);
        }

        // 检查手牌上限
        if (player.handExceedsLimit()) {
            // TODO: 触发弃牌对话框
            notifyPlayerMustDiscard(player);
        }
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
        players.clear(); // 清除之前的玩家
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player(i + 1, board.getTileByType(TileType.HOWLING_GARDEN));
            players.add(player);
        }

        // 弹窗选择起始玩家
        selectStartingPlayer();
        // 洗牌
        treasureDeck.shuffle();
        floodDeck.shuffle();

        // 初始洪水
        drawInitialFloodCards();

        // 给玩家发初始卡牌
        dealInitialCards();

        turnManager = new TurnManager(this, players);
        turnManager.startPlayerTurn();
    }

    /**
     * 弹窗选择起始玩家，并调整玩家顺序     */
    private void selectStartingPlayer() {
        String[] playerNames = players.stream().map(p -> "玩家" + p.getPlayerId()).toArray(String[]::new);
        int startIdx = JOptionPane.showOptionDialog(
                null, "请选择起始玩家", "起始玩家选择",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, playerNames, playerNames[0]
        );
        if (startIdx >= 0) {
            // 调整玩家顺序，使选中的玩家成为第一个
            Collections.rotate(players, -startIdx);
            currentPlayerIndex = 0;
        }
    }

    /**
     * 回合通知方法
     */
    public void notifyTurnStarted(Player player) {
        if (mainFrame != null) {
            mainFrame.getPlayerInfoPanel().updatePlayerInfos(players, currentPlayerIndex);
            mainFrame.addConsoleMessage("轮到玩家" + player.getPlayerId() + "行动");
            mainFrame.updateBoard(getBoard()); // 同步刷新游戏板
        }
    }

    public void notifyPhaseChanged(TurnManager.TurnPhase phase) {
        // 通知GameController更新UI
        if (mainFrame != null) {
            mainFrame.updateBoard(getBoard()); // 同步刷新游戏板
        }
    }

    /**
     * 处理玩家手牌超限的情况
     */
    public void notifyPlayerMustDiscard(Player player) {
        if (mainFrame == null) return;

        SwingUtilities.invokeLater(() -> {
            DiscardDialog dialog = new DiscardDialog(mainFrame, player, new DiscardDialog.ActionListener() {
                @Override
                public void onCardDiscarded(Card card) {
                    playerDiscardHandCard(player, (HandCard)card);
                }

                @Override
                public void onSpecialCardUsed(SpecialCard card) {
                    // 使用特殊卡
                    card.use(player);
                }
            });
            dialog.setVisible(true);
        });
    }

    /**
     * 玩家弃掉一张手牌
     */
    public void playerDiscardHandCard(Player player, HandCard cardToDiscard) {
        // 如果是特殊卡，允许玩家在弃牌前使用
        if (cardToDiscard instanceof SpecialCard) {
            SpecialCard specialCard = (SpecialCard) cardToDiscard;
            if (specialCard.canBeUsedNow(player)) {
                specialCard.use(player);
                return; // 使用后卡牌会自动进入弃牌堆
            }
        }

        player.discardCard(cardToDiscard);
        treasureDeck.discard(cardToDiscard);

        // 通知监听器
        for (GameEventListener listener : eventListeners) {
            listener.onCardDiscarded(player, cardToDiscard);
        }
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
        if (board.isHelipadSunk()) {
            triggerGameFailure("愚者着陆点沉没");
            return true;
        }

        // 2. 某种宝藏未获得且两块宝藏板块全部沉没
        for (TreasureType type : TreasureType.values()) {
            if (type == TreasureType.NONE) continue;
            if (!hasTreasure(type) && board.isAllTreasureTilesSunk(type)) {
                triggerGameFailure("宝藏 " + type + " 已不可获得");
                return true;
            }
        }

        // 3. 有玩家无法移动（被困，不可达，也可补充diver特例）
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!canPlayerEscape(player)) {
                    triggerGameFailure("玩家" + (i + 1) + " [" + player.getRole() + "] 已被困，游戏失败！");
                    return true;
                }
        }

        // 4. 水位到顶
        if (waterLevel.getCurrentLevel() >= 9) {
            triggerGameFailure("水位到顶");
            return true;
        }

        // 5. 宝藏牌抽光且弃牌堆也空，并且还没赢
        if (treasureDeck.getRemainingCards() == 0 && treasureDeck.getDiscardPileSize() == 0) {
            triggerGameFailure("宝藏牌抽光且弃牌堆也已用完");
            return true;
        }

        return false;
    }

    /**
     * 失败事件钩子分发
     */
    private void triggerGameFailure(String reason) {
        for (GameEventListener listener : eventListeners) {
            listener.onGameFailure(reason);
        }
        this.gameOver = true;
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


    public void playTurn(Player player) {
        // 1. 回合开始前触发钩子
        for (GameEventListener listener : eventListeners) {
            listener.onTurnStart(player, round);//假如你有round变量，否则传当前下标或0
        }
        // 2. 行动阶段（玩家可以移动、排水、交换卡牌等）
        // 3. 抽取宝藏卡牌
        for (int i = 0; i < 2; i++) {
            drawTreasureCard();
        }

        // 4. 抽取洪水卡牌
        drawFloodCards();
        // 5. 水位可能上升

        // 6. 检查游戏结束条件
        checkGameEnd();

        // 通知回合结束
        for (GameEventListener listener : eventListeners) {
            listener.onTurnEnd(player, round);
        }
    }

    /**
     * 根据当前水位抽取对应数量的洪水卡牌
     */
    public void drawFloodCards() {
        int cardsToDrawCount = waterLevel.getFloodCardsCount();
        for (int i = 0; i < cardsToDrawCount; i++) {
            FloodCard card = floodDeck.drawCard();
            if (card == null) {
                triggerGameFailure("洪水牌和弃牌堆都用光，游戏失败！");
                break;
            }

            // 设置状态为已抽取洪水卡
            this.afterFloodCardDrawn = true;

            // 处理洪水卡牌效果
            Tile floodedTile = board.getTileByType(card.getTileType());
            floodedTile.flood();

            // 检查瓦片上是否有玩家，可能需要救援
            checkPlayersOnFloodedTile(floodedTile);

            // 把本次抽的洪水牌丢进弃牌堆
            floodDeck.discard(card);
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
        if (checkWinCondition()) {
            for (GameEventListener listener : eventListeners) {
                listener.onGameVictory();
            }
            this.gameOver = true;
        }

        if (!this.gameOver) {
            checkLoseCondition();
        }
    }

    public void increaseWaterLevel() {
        boolean isGameOver = !waterLevel.tryRise();
        // 检查水位上升后是否导致游戏结束
        if (isGameOver) {
            triggerGameFailure("水位已达顶，游戏结束");
            gameOver = true;
        }
    }

    public Player getCurrentPlayer() {
        if (currentPlayerIndex >= 0 && currentPlayerIndex < players.size()) {
            return players.get(currentPlayerIndex);
        }
        return null;
    }

    /**
     * 细化职业能力的"玩家是否还能逃脱"的判定。适配所有职业。
     * @param player 检查的玩家
     * @return true=还能逃脱；false=被困
     */
    public boolean canPlayerEscape(Player player) {
        Role role = player.getRole();
        Tile currTile = player.getCurrentTile();

        if (currTile == null || !currTile.isNavigable()) {
            // 玩家当前板块已沉没或不可通行，直接认为被困
            return false;
        }

        // 飞行家：任意未沉没格都能飞
        if (role == Role.PILOT) {
            for (Tile tile : board.getAllTiles()) {
                if (tile != null && tile.isNavigable()) {
                    return true;
                }
            }
            return false;
        }

        // 潜水员：BFS所有相连的sunk/flooded格，能爬出去就未被困
        if (role == Role.DIVER) {
            Set<Tile> visited = new HashSet<>();
            Queue<Tile> queue = new LinkedList<>();
            queue.add(currTile);
            visited.add(currTile);

            while (!queue.isEmpty()) {
                Tile t = queue.poll();
                for (Tile adj : board.getAdjacentTiles(t)) {
                    if (adj == null || visited.contains(adj)) continue;
                    if (adj.isNavigable()) {
                        return true;
                    }
                    // 能穿过被水淹和沉没的格
                    if (adj.isFlooded() || adj.isSunk()) {
                        queue.add(adj);
                        visited.add(adj);
                    }
                }
            }
            return false;
        }

        // 探险家：可斜向移动（如果你Role. EXPLORER加了这个功能，放开此分支扩展）
    /*
    if (role == Role.EXPLORER) {
        for (Tile adj : board.getDiagonalAndOrthogonalTiles(currTile)) { // 你文档10未定义该方法，如有自己实现
            if (adj != null && adj.isNavigable()) {
                return true;
            }
        }
        return false;
    }
    */

        // 其他职业：只要正方向有一格能走，就算没被困
        for (Tile adj : board.getAdjacentTiles(currTile)) {
            if (adj != null && adj.isNavigable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 触发游戏结束
     * @param message 游戏结束的原因或胜利信息
     */
    public void triggerGameOver(String message) {
        gameOver = true;

        // 设置游戏状态
        if (message.contains("恭喜")) {
            gameState = GameState.WON;
        } else {
            gameState = GameState.LOST;
        }

        // 通知所有监听器游戏结束
        for (GameEventListener listener : eventListeners) {
            listener.onGameOver(message);
        }

        // 通知主界面更新
        if (mainFrame != null) {
            SwingUtilities.invokeLater(() -> {
                mainFrame.showGameOverDialog(message);
            });
        }
    }

    public Tile getTileAt(int row, int col) {
        return board.getTileAt(row, col);
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public void setTurnManager(TurnManager turnManager) {
        this.turnManager = turnManager;
    }
}
