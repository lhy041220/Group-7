package controller;

import lombok.Getter;
import model.*;
import model.card.*;
import view.gamePanel.MainFrame;
import model.enums.GameState;

import javax.swing.*;
import java.io.InputStream;
import java.util.List;

/**
 * Controller for managing the main game process, player actions, and UI updates.
 */
public class GameController {
    @Getter
    private Game game;
    private MainFrame mainFrame;


    private final String IMAGE_DIR = "/images/Tiles/";

    public GameController(Game game, MainFrame mainFrame) {
        // Default constructor
        this.game = game;
        this.mainFrame = mainFrame;
        // Set reference to MainFrame in Game instance
        game.setMainFrame(mainFrame);
    }

    public void initializeViewFrame() {
        if (mainFrame != null) {
            SwingUtilities.invokeLater(() -> {
                mainFrame.setVisible(true);
            });
        }
    }

    public void startGame(int playerNum) {
        if (game != null && mainFrame != null) {
            game.startGame(playerNum);
            mainFrame.updateBoard(game.getBoard());
            // Notify turn start
            handleStartPlayerTurn();
        }
    }

    private int getPlayerNum() {
        // TODO: Retrieve player number from mainFrame if needed
        if (mainFrame != null) {

        }
        return 3;
    }

    /**
     * 处理玩家移动到指定格子的请求
     */
    public void handlePlayerMove(Tile destination) {
        Player player = game.getCurrentPlayer();
        if (player == null || destination == null) return;
        List<Tile> movable = game.getBoard().getMovableTilesForPlayer(player);
        if (!movable.contains(destination)) {
            mainFrame.addConsoleMessage("Cannot move to that tile!");
            return;
        }
        if (player.moveToTile(destination)) {
            player.useAction();
            mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " moved to " + destination.getType().getDisplayName());
            mainFrame.updateBoard(game.getBoard());
            checkAfterPlayerAction(player);
        } else {
            mainFrame.addConsoleMessage("Move failed!");
        }
    }

    /**
     * Handle player's shore up action on the specified tile.
     *
     * @param tile The tile to shore up
     */
    public void handlePlayerShoreUp(Tile tile) {
        Player player = game.getCurrentPlayer();
        if (player.getRemainingActions() > 0 && tile.isFlooded() && !tile.isSunk()) {
            // Call player's shore up method
            if (player.shoreUp(tile)) {
                mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " shored up " + tile.getType().getDisplayName() + " drainage");
                mainFrame.updateBoard(game.getBoard());
                checkAfterPlayerAction(player);
            }
        }
    }

    /**
     * Handle the use of a special card by the player.
     *
     * @param card The special card used
     */
    public void handlePlayerUseCard(SpecialCard card) {
        Player player = game.getCurrentPlayer();
        if (card.use(player)) {
            mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " used special card: " + card.getName());
            checkAfterPlayerAction(player);
        }
    }

    /**
     * Called after each player action. If no remaining actions, triggers next phase.
     *
     * @param player The player who performed the action
     */
    private void checkAfterPlayerAction(Player player) {
        // Update UI
        mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());

        // Debug info
        System.out.println("Post-action check - Player " + player.getPlayerId() + " remaining actions: " + player.getRemainingActions());

        if (player.getRemainingActions() <= 0) {
            mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " has finished all actions!");
            JOptionPane.showMessageDialog(mainFrame,
                    "Player " + player.getPlayerId() + " has no actions left. Moving to treasure draw phase.",
                    "Turn Phase Notification",
                    JOptionPane.INFORMATION_MESSAGE);
            // Proceed to next phase
            handleDrawTreasurePhase();
        } else {
            mainFrame.addConsoleMessage("Remaining actions: " + player.getRemainingActions());
        }
    }


    /************ Main Turn/Phase Logic ************/

    /**
     * Treasure draw phase for the current player.
     */
    private void handleDrawTreasurePhase() {
        Player curr = game.getCurrentPlayer();

        // Notify phase change
        JOptionPane.showMessageDialog(mainFrame,
                "Player " + curr.getPlayerId() + " is now drawing treasure cards.",
                "Turn Phase Notification",
                JOptionPane.INFORMATION_MESSAGE);

        // Draw two treasure cards
        game.drawTreasureCardsForTurn(curr);

        // Check for hand limit
        if (curr.handExceedsLimit()) {
            // Notify the game to handle discard
            game.notifyPlayerMustDiscard(curr);
        } else {
            handleFloodPhase();
        }
    }

    /**
     * Flood phase: handle flood cards and board update.
     */
    private void handleFloodPhase() {
        JOptionPane.showMessageDialog(mainFrame,
                "Entering the flood phase.",
                "Turn Phase Notification",
                JOptionPane.INFORMATION_MESSAGE);

        int count = game.getWaterLevel().getFloodCardsCount();
        for (int i = 0; i < count; i++) {
            game.drawFloodCards(); // Internally manages flood state and interactions
        }
        mainFrame.updateBoard(game.getBoard());

        // Check for win/lose conditions
        if (game.checkWinCondition()) {
            announceGameEnd(true, "Collected 4 treasures, all players on helipad, win by helicopter lift!");
            return;
        }
        if (game.checkLoseCondition()) {
            announceGameEnd(false, "Game lost condition met!");
            return;
        }
        // Proceed to next player turn as usual
        advanceToNextPlayerTurn();
    }

    /**
     * Advance to the next player's turn.
     */
    private void advanceToNextPlayerTurn() {
        // Update current player index in Game
        int nextIndex = (game.getCurrentPlayerIndex() + 1) % game.getPlayers().size();
        game.setCurrentPlayerIndex(nextIndex);

        // Notify player change
        JOptionPane.showMessageDialog(mainFrame,
                "It's now Player " + (nextIndex + 1) + "'s turn.",
                "Player Change Notification",
                JOptionPane.INFORMATION_MESSAGE);

        handleStartPlayerTurn();
    }

    /**
     * Start the current player's turn.
     */
    private void handleStartPlayerTurn() {
        Player curr = game.getCurrentPlayer();
        curr.resetActionsForTurn();
        mainFrame.addConsoleMessage("It's Player " + curr.getPlayerId());
        updateAllUI();
        // Notify game logic
        game.notifyTurnStarted(curr);
    }

    /**
     * End the current player's turn on manual input (e.g., via button).
     */
    public void endPlayerTurn() {
        mainFrame.addConsoleMessage("Player " + game.getCurrentPlayer().getPlayerId() + " ended their turn manually.");
        game.getTurnManager().nextPhase(); // Advance to next phase
    }

    /************ Utility and Helper Methods ************/

    /**
     * Announce game end with the specified result and reason.
     *
     * @param win Whether the game was won
     * @param reason The reason for game end
     */
    private void announceGameEnd(boolean win, String reason) {
        if (win) {
            game.setGameState(GameState.WON);
            JOptionPane.showMessageDialog(mainFrame, "Game won! " + reason);
        } else {
            game.setGameState(GameState.LOST);
            JOptionPane.showMessageDialog(mainFrame, "Game lost! " + reason);
        }
    }

    /**
     * Refresh all game and player info in the UI.
     */
    private void updateAllUI() {
        mainFrame.updateBoard(game.getBoard());
        mainFrame.updateWaterLevel(game.getWaterLevel().getCurrentLevel());
        mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());
        // Extendable: update player info panel, card panel, etc.
    }

    public void handleCollectTreasure() {
        Player player = game.getCurrentPlayer();
        Tile tile = player.getCurrentTile();
        if (player.getRemainingActions() <= 0) {
            mainFrame.addConsoleMessage("Not enough action points!");
            return;
        }
        if (tile == null || tile.getTreasure() == null || tile.getTreasure().toString().equals("NONE")) {
            mainFrame.addConsoleMessage("No treasure to collect at the current location!");
            return;
        }
        // Check hand cards
        int count = 0;
        for (Card card : player.getHand()) {
            if (card instanceof model.card.TreasureCard && ((model.card.TreasureCard) card).getTreasureType() == tile.getTreasure()) {
                count++;
            }
        }
        if (count < 4) {
            mainFrame.addConsoleMessage("Not enough matching treasure cards (need 4)!");
            return;
        }
        // Collect treasure
        if (player.captureTreasure(tile.getTreasure())) {
            player.useAction();
            mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " successfully collected the treasure: " + tile.getTreasure().getDisplayName());
            mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());
            checkAfterPlayerAction(player);
        } else {
            mainFrame.addConsoleMessage("Failed to collect the treasure!");
        }
    }

    public void handleUseSpecialAbility(Tile targetTile) {
        Player player = game.getCurrentPlayer();
        if (player.getRemainingActions() > 0) {
            player.useSpecialAbility(targetTile);
            mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " used a special ability.");
            checkAfterPlayerAction(player);
        }
    }

    public void handleTileClick(int row, int col) {
        if (game.getGameState() == model.enums.GameState.RUNNING) {
            Tile clickedTile = game.getBoard().getTileAt(row, col);
            if (clickedTile != null) {
                handlePlayerMove(clickedTile);
            }
        }
    }

    public void handlePlayerUseHelicopterLift(model.card.SpecialCard card, int row, int col) {
        Player player = game.getCurrentPlayer();
        Tile targetTile = game.getBoard().getTileAt(row, col);
        if (targetTile != null && card instanceof model.card.HelicopterLiftCard) {
            if (((model.card.HelicopterLiftCard) card).use(player, targetTile)) {
                mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " used Helicopter Lift to move to " + targetTile.getType().getDisplayName());
                checkAfterPlayerAction(player);
            }
        }
    }

    public void handlePlayerUseSandbag(model.card.SpecialCard card, int row, int col) {
        Player player = game.getCurrentPlayer();
        Tile targetTile = game.getBoard().getTileAt(row, col);
        if (targetTile != null && card instanceof model.card.SandbagCard) {
            if (((model.card.SandbagCard) card).use(player, targetTile)) {
                mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " used Sandbag on " + targetTile.getType().getDisplayName());
                checkAfterPlayerAction(player);
            }
        }
    }

    public void enterShoreUpMode() {
        game.getTurnManager().setCurrentAction(model.TurnManager.ActionType.SHORE_UP);
        mainFrame.addConsoleMessage("Entered shore up mode.");
    }

    public void enterNavigatorMode() {
        game.getTurnManager().setCurrentAction(model.TurnManager.ActionType.NAVIGATOR);
        mainFrame.addConsoleMessage("Entered navigator mode.");
    }

    /**
     * 处理玩家给卡操作，消耗行动点，刷新UI
     */
    public void handleGiveCard(Player from, Player to, Card card) {
        if (from.getRemainingActions() <= 0) {
            mainFrame.addConsoleMessage("Not enough action points!");
            return;
        }
        // 信使可以远程给卡，其他角色只能同格
        boolean canGive = false;
        if (from.getRole() != null && from.getRole().getDisplayName().equals("Messenger")) {
            canGive = true;
        } else if (from.getCurrentTile() == to.getCurrentTile()) {
            canGive = true;
        }
        if (!canGive) {
            mainFrame.addConsoleMessage("Can only give cards within the same tile, Messenger can give cards remotely.");
            return;
        }
        if (from.giveCardToPlayer(to, card)) {
            from.useAction();
            mainFrame.addConsoleMessage("Player " + from.getPlayerId() + " gave a card to Player " + to.getPlayerId());
            mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());
            checkAfterPlayerAction(from);
        } else {
            mainFrame.addConsoleMessage("Failed to give the card!");
        }
    }
}