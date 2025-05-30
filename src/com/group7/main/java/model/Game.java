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
import model.enums.Role;

@Getter
public class Game {

    private static Game instance;
    private MainFrame mainFrame;
    private boolean afterFloodCardDrawn = false;  // Marker for whether a flood card was just drawn

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

        // Initialize decks
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
        // Initialize treasure deck and special card deck
        initializeTreasureDeck();
        // Initialize flood deck
        initializeFloodDeck();
    }

    /**
     * Initialize the treasure deck
     * Includes: treasure cards of each type and special cards (helicopter, sandbag, water rise)
     */
    private void initializeTreasureDeck() {
        // Add treasure cards
        for (TreasureType type : TreasureType.values()) {
            if (type != TreasureType.NONE) {
                // 5 cards for each treasure type
                for (int i = 0; i < 5; i++) {
                    treasureDeck.addCard(new TreasureCard(
                            type.name() + " Card",
                            "A treasure card of type " + type.name(),
                            type
                    ));
                }
            }
        }

        // Add special cards
        // 3 Helicopter Lift cards
        for (int i = 0; i < 3; i++) {
            treasureDeck.addCard(new HelicopterLiftCard());
        }

        // 2 Sandbag cards
        for (int i = 0; i < 2; i++) {
            treasureDeck.addCard(new SandbagCard());
        }

        // 3 Water Rise cards
        for (int i = 0; i < 3; i++) {
            treasureDeck.addCard(new WaterRiseCard());
        }

        // Shuffle the deck
        treasureDeck.shuffle();
    }

    /**
     * Initialize the flood deck
     * Each tile has a corresponding flood card
     */
    private void initializeFloodDeck() {
        // For each tile, create corresponding flood card if it actually exists on the board
        for (TileType tileType : TileType.values()) {
            if (tileType != TileType.NONE) {
                Tile tile = board.getTileByType(tileType);
                if (tile != null) {
                    floodDeck.addCard(new FloodCard(tileType, tileType.name()));
                }
            }
        }
        // Shuffle the deck
        floodDeck.shuffle();
    }

    /**
     * Deal initial cards to players.
     * Each player gets 2 cards.
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
     * Handle the event of drawing a Water Rise card.
     * @param card The Water Rise card drawn
     * @param isSecondCard Whether it's the second Water Rise card drawn this turn
     */
    private void handleWaterRiseCard(WaterRiseCard card, boolean isSecondCard) {
        // If it's the second Water Rise card in the turn, only increase water level, no reshuffle
        if (isSecondCard) {
            waterLevel.tryRise();
        } else {
            // Normally, perform all Water Rise effects
            card.useCard(players.get(currentPlayerIndex));
        }
        // Add card to discard pile
        treasureDeck.discard(card);
    }

    /**
     * Draw a treasure card and handle special cases (such as Water Rise cards).
     * @return The drawn card, or null if it's a Water Rise card.
     */
    public HandCard drawTreasureCard() {
        HandCard card = treasureDeck.drawCard();
        if (card == null) {
            return null;
        }

        // Handle Water Rise card
        if (card instanceof WaterRiseCard) {
            handleWaterRiseCard((WaterRiseCard) card, false);
            return null;
        }

        return card;
    }
    /**
     * Reset the state of flood card drawing.
     * Should be called at the start of each player's turn.
     */
    public void resetFloodCardState() {
        this.afterFloodCardDrawn = false;
    }

    /**
     * Draw two treasure cards for the player's turn.
     * @param player The current player.
     */
    public void drawTreasureCardsForTurn(Player player) {
        // Reset flood card state
        resetFloodCardState();
        boolean drewWaterRiseFirst = false;

        // Draw first card
        HandCard firstCard = drawTreasureCard();
        if (firstCard instanceof WaterRiseCard) {
            drewWaterRiseFirst = true;
        } else if (firstCard != null) {
            player.addCardToHand(firstCard);
        }

        // Draw second card
        HandCard secondCard = drawTreasureCard();
        if (secondCard instanceof WaterRiseCard && drewWaterRiseFirst) {
            // If it's the second Water Rise card in this turn, handle specially
            handleWaterRiseCard((WaterRiseCard) secondCard, true);
        } else if (secondCard != null) {
            player.addCardToHand(secondCard);
        }

        // Check hand limit
        if (player.handExceedsLimit()) {
            // TODO: Trigger discard dialog
            notifyPlayerMustDiscard(player);
        }
    }

    private void initializePlayers(int numPlayers) {
        // Create and position players at starting tiles
    }

    public void startGame(int numPlayers) {
        // Initialize player positions
        if (1 < numPlayers && numPlayers < 5) {
            initializePlayers(numPlayers);
        } else {
            throw new IllegalArgumentException("Invalid number of players. Must be between 1 and 4.");
        }

        gameState = GameState.RUNNING;

        // Initialize players list
        players.clear();
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player(i + 1, board.getTileByType(TileType.CORAL_PALACE));
            players.add(player);
        }

        // 自动分配不同的角色
        java.util.List<Role> allRoles = new java.util.ArrayList<>();
        Collections.addAll(allRoles, Role.values());
        // 去除未定义的NONE等角色（如果有）
        allRoles.removeIf(r -> r.name().equalsIgnoreCase("NONE"));
        Collections.shuffle(allRoles);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setRole(allRoles.get(i));
        }

        // Dialog to select starting player
        selectStartingPlayer();
        // Shuffle decks
        treasureDeck.shuffle();
        floodDeck.shuffle();

        // Initial flood
        drawInitialFloodCards();

        // Deal initial cards
        dealInitialCards();

        turnManager = new TurnManager(this, players);
        turnManager.startPlayerTurn();
    }

    /**
     * Dialog for selecting starting player and adjusting player order
     */
    private void selectStartingPlayer() {
        String[] playerNames = players.stream().map(p -> "Player " + p.getPlayerId()).toArray(String[]::new);
        int startIdx = JOptionPane.showOptionDialog(
                null, "Please select the starting player", "Select Starting Player",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, playerNames, playerNames[0]
        );
        if (startIdx >= 0) {
            // Adjust player order so the selected player is first
            Collections.rotate(players, -startIdx);
            currentPlayerIndex = 0;
        }
    }

    /**
     * Notifies listeners of turn start and updates UI.
     */
    public void notifyTurnStarted(Player player) {
        if (mainFrame != null) {
            mainFrame.getPlayerInfoPanel().updatePlayerInfos(players, currentPlayerIndex);
            mainFrame.addConsoleMessage("Player " + player.getPlayerId() + "'s turn started");
            mainFrame.updateBoard(getBoard());
        }
    }

    public void notifyPhaseChanged(TurnManager.TurnPhase phase) {
        // Notify GameController to update UI
        if (mainFrame != null) {
            mainFrame.updateBoard(getBoard());
        }
    }

    /**
     * Handle when a player's hand exceeds the limit.
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
                    // Use the special card
                    card.use(player);
                }
            });
            dialog.setVisible(true);
        });
    }

    /**
     * Player discards a card
     */
    public void playerDiscardHandCard(Player player, HandCard cardToDiscard) {
        // If it's a special card, allow player to use it before discarding
        if (cardToDiscard instanceof SpecialCard) {
            SpecialCard specialCard = (SpecialCard) cardToDiscard;
            if (specialCard.canBeUsedNow(player)) {
                specialCard.use(player);
                return; // Used special card will be discarded automatically
            }
        }

        player.discardCard(cardToDiscard);
        treasureDeck.discard(cardToDiscard);

        // Notify event listeners
        for (GameEventListener listener : eventListeners) {
            listener.onCardDiscarded(player, cardToDiscard);
        }
    }

    // Check if a specific treasure has been obtained
    private boolean hasTreasure(TreasureType treasureType) {
        // No global collection of obtained treasures; check all players' hands
        for (Player player : players) {
            if (player.hasCollectedTreasure(treasureType)) return true;
        }
        return false;
    }

    // Check if all players are at Fool's Landing
    private boolean allPlayersAtHelipad() {
        for (Player player : players) {
            Tile curr = player.getCurrentTile();
            if (curr == null || curr.getType() != TileType.FOOLS_LANDING) return false;
        }
        return true;
    }

    // Check if any player has a helicopter card
    private boolean anyPlayerHasHelicopter() {
        for (Player player : players) {
            for (Card card : player.getHand()) {
                if (card instanceof HelicopterLiftCard) return true;
            }
        }
        return false;
    }

    /**
     * Check for win condition:
     * - All four treasures obtained
     * - All players at Fool's Landing
     * - At least one Helicopter card in hand
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
     * Check for lose condition.
     */
    public boolean checkLoseCondition() {
        // 1. Fool's Landing is sunk
        if (board.isHelipadSunk()) {
            triggerGameFailure("Fool's Landing is sunk");
            return true;
        }

        // 2. A treasure cannot be obtained and both related tiles are sunk
        for (TreasureType type : TreasureType.values()) {
            if (type == TreasureType.NONE) continue;
            if (!hasTreasure(type) && board.isAllTreasureTilesSunk(type)) {
                triggerGameFailure("Treasure " + type + " can no longer be obtained");
                return true;
            }
        }

        // 3. Any player is trapped (cannot move). Add diver exceptions as needed.
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!canPlayerEscape(player)) {
                triggerGameFailure("Player " + (i + 1) + " [" + player.getRole() + "] is trapped - Game Over!");
                return true;
            }
        }

        // 4. Water level reaches the top
        if (waterLevel.getCurrentLevel() >= 9) {
            triggerGameFailure("Water level reached maximum");
            return true;
        }

        // 5. No treasure cards left (both deck and discard) and not won yet
        if (treasureDeck.getRemainingCards() == 0 && treasureDeck.getDiscardPileSize() == 0) {
            triggerGameFailure("All treasure cards depleted and discard pile empty");
            return true;
        }

        return false;
    }

    /**
     * Broadcast a failure event to all listeners.
     */
    private void triggerGameFailure(String reason) {
        for (GameEventListener listener : eventListeners) {
            listener.onGameFailure(reason);
        }
        this.gameOver = true;
    }

    /**
     * Flood (submerge) a tile.
     */
    public void floodTile(Tile tile) {
        // Implement flood tile logic here.
    }

    private void drawInitialFloodCards() {
        // Draw initial flood cards and flood the corresponding tiles.
    }

    public void playTurn(Player player) {
        // 1. Trigger turn start event for listeners
        for (GameEventListener listener : eventListeners) {
            listener.onTurnStart(player, round);
        }
        // 2. Action phase (move, shore up, exchange cards, etc.)
        // 3. Draw treasure cards
        for (int i = 0; i < 2; i++) {
            drawTreasureCard();
        }

        // 4. Draw flood cards
        drawFloodCards();
        // 5. Water level may rise

        // 6. Check game end conditions
        checkGameEnd();

        // Notify end of turn
        for (GameEventListener listener : eventListeners) {
            listener.onTurnEnd(player, round);
        }
    }

    /**
     * Draw flood cards according to the current water level.
     */
    public void drawFloodCards() {
        int cardsToDrawCount = waterLevel.getFloodCardsCount();
        for (int i = 0; i < cardsToDrawCount; i++) {
            FloodCard card = floodDeck.drawCard();
            if (card == null) {
                triggerGameFailure("All flood cards and discard pile exhausted - Game Over!");
                break;
            }

            // Set state to indicate a flood card was drawn
            this.afterFloodCardDrawn = true;

            // Process flood card effect
            Tile floodedTile = board.getTileByType(card.getTileType());
            floodedTile.flood();

            // Check if there are any players on the flooded tile and handle possible rescue
            checkPlayersOnFloodedTile(floodedTile);

            // Add the drawn flood card to the discard pile
            floodDeck.discard(card);
        }
    }

    private void checkPlayersOnFloodedTile(Tile floodedTile) {
        // Check and handle players located on the flooded tile
    }

    private void checkGameEnd() {
        // Check win condition: all treasures collected and all players at Fool's Landing

        // Check lose conditions:
        // 1. Fool's Landing flooded
        // 2. Any player drowns and can't be rescued
        // 3. Water level reaches the top
        // 4. All cards for a treasure become unavailable

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
        // Check if rising water level leads to game over
        if (isGameOver) {
            triggerGameFailure("Water level has reached maximum - Game Over");
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
     * Determines if a player can escape according to their role ability.
     * @param player The player to check.
     * @return true if the player can escape, false if trapped.
     */
    public boolean canPlayerEscape(Player player) {
        Role role = player.getRole();
        Tile currTile = player.getCurrentTile();

        if (currTile == null || !currTile.isNavigable()) {
            // The player's current tile has sunk or is not passable, so is trapped
            return false;
        }

        // Pilot: Can fly to any non-sunk tile
        if (role == Role.PILOT) {
            for (Tile tile : board.getAllTiles()) {
                if (tile != null && tile.isNavigable()) {
                    return true;
                }
            }
            return false;
        }

        // Diver: Use BFS to find if can reach a navigable tile through sunk/flooded tiles
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
                    // Can move through flooded and sunk tiles
                    if (adj.isFlooded() || adj.isSunk()) {
                        queue.add(adj);
                        visited.add(adj);
                    }
                }
            }
            return false;
        }

        // Explorer: Can move diagonally (if you implemented this feature for Role.EXPLORER, uncomment and adjust)
        /*
        if (role == Role.EXPLORER) {
            for (Tile adj : board.getDiagonalAndOrthogonalTiles(currTile)) {
                if (adj != null && adj.isNavigable()) {
                    return true;
                }
            }
            return false;
        }
        */

        // Other roles: Only need at least one orthogonally adjacent navigable tile to escape
        for (Tile adj : board.getAdjacentTiles(currTile)) {
            if (adj != null && adj.isNavigable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Triggers game over with the specified message.
     * @param message The reason for game over or win information.
     */
    public void triggerGameOver(String message) {
        gameOver = true;

        // Set game state
        if (message.contains("Congratulations")) {
            gameState = GameState.WON;
        } else {
            gameState = GameState.LOST;
        }

        // Notify all listeners that the game is over
        for (GameEventListener listener : eventListeners) {
            listener.onGameOver(message);
        }

        // Notify the main UI to update
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

    public Board getBoard() {
        return board;
    }
}
