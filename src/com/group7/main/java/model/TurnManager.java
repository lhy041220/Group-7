package model;

import lombok.Getter;
import model.card.*;
import model.enums.Role;
import model.enums.TreasureType;
import java.util.List;
import java.util.ArrayList;

// Turn management
public class TurnManager {
    private Game game;
    private List<Player> players;

    @Getter
    private int roundNumber;
    @Getter
    private TurnPhase currentPhase;

    private ActionType currentAction;

    public enum TurnPhase {
        ACTIONS,         // Player action phase
        DRAW_TREASURE,   // Draw treasure cards phase
        DRAW_FLOOD,      // Draw flood cards phase
        END              // End of turn phase
    }

    public enum ActionType {
        MOVE,             // Move
        SHORE_UP,         // Shore up
        GIVE_CARD,        // Give card
        CAPTURE_TREASURE, // Capture treasure
        NAVIGATOR         // Navigator action
    }

    public TurnManager(Game game, List<Player> players) {
        this.game = game;
        this.players = players;
        this.roundNumber = 1;
        this.currentPhase = TurnPhase.ACTIONS;
    }

    public Player getCurrentPlayer() {
        return players.get(game.getCurrentPlayerIndex());
    }

    // Start a player's turn
    public void startPlayerTurn() {
        currentPhase = TurnPhase.ACTIONS;
        Player currentPlayer = getCurrentPlayer();
        currentPlayer.resetActionsForTurn();
        game.notifyTurnStarted(currentPlayer);
    }

    // Player performs an action
    public boolean performAction(ActionType actionType, Object... params) {
        Player currentPlayer = getCurrentPlayer();
        boolean actionSuccessful = false;

        if (currentPlayer.getRemainingActions() <= 0) {
            return false;
        }

        switch (actionType) {
            case MOVE:
                if (params.length >= 1 && params[0] instanceof Tile) {
                    Tile destinationTile = (Tile) params[0];
                    actionSuccessful = movePlayer(currentPlayer, destinationTile);
                }
                break;

            case SHORE_UP:
                if (params.length >= 1 && params[0] instanceof Tile) {
                    Tile tileToShoreUp = (Tile) params[0];
                    actionSuccessful = shoreUpTile(currentPlayer, tileToShoreUp);
                }
                break;

            case GIVE_CARD:
                if (params.length >= 2 && params[0] instanceof Player && params[1] instanceof Card) {
                    Player targetPlayer = (Player) params[0];
                    Card cardToGive = (Card) params[1];
                    actionSuccessful = giveCard(currentPlayer, targetPlayer, cardToGive);
                }
                break;
        }

        if (actionSuccessful) {
            currentPlayer.useAction();
            // If player runs out of action points, automatically go to the next phase
            if (currentPlayer.getRemainingActions() <= 0) {
                nextPhase();
            }
            // Notify that game state changed
            game.notifyTurnStarted(currentPlayer);
        }

        return actionSuccessful;
    }

    // Move player
    private boolean movePlayer(Player player, Tile destinationTile) {
        if (destinationTile == null || destinationTile.isSunk()) {
            return false;
        }

        Tile currentTile = player.getCurrentTile();
        List<Tile> reachableTiles;

        // Get reachable tiles based on player's role
        if (player.getRole() == Role.EXPLORER) {
            reachableTiles = game.getBoard().getDiagonalAndOrthogonalTiles(currentTile);
        } else if (player.getRole() == Role.DIVER) {
            reachableTiles = game.getBoard().getReachableTilesForDiver(currentTile);
        } else {
            reachableTiles = game.getBoard().getAdjacentTiles(currentTile);
        }

        if (reachableTiles.contains(destinationTile)) {
            return player.moveToTile(destinationTile);
        }

        return false;
    }

    // Shore up tile
    private boolean shoreUpTile(Player player, Tile tile) {
        if (tile == null || !tile.isFlooded() || tile.isSunk()) {
            return false;
        }

        // Engineer can shore up two tiles in one action
        if (player.getRole() == Role.ENGINEER) {
            // Special case for Engineer
            tile.shoreUp();
            return true;
        }

        // Explorer can shore up diagonally
        if (player.getRole() == Role.EXPLORER) {
            List<Tile> reachableTiles = game.getBoard().getDiagonalAndOrthogonalTiles(player.getCurrentTile());
            if (reachableTiles.contains(tile)) {
                game.getBoard().dryTile(tile);
                return true;
            }
        } else {
            // Other roles can only shore up adjacent tiles
            List<Tile> reachableTiles = game.getBoard().getAdjacentTiles(player.getCurrentTile());
            if (reachableTiles.contains(tile)) {
                tile.shoreUp();
                return true;
            }
        }

        return false;
    }

    // Give card to another player
    private boolean giveCard(Player fromPlayer, Player toPlayer, Card card) {
        // Messenger can give cards anywhere
        if (fromPlayer.getRole() == Role.MESSENGER) {
            return fromPlayer.giveCardToPlayer(toPlayer, card);
        }
        // Other roles must be on the same tile
        if (fromPlayer.getCurrentTile() == toPlayer.getCurrentTile()) {
            return fromPlayer.giveCardToPlayer(toPlayer, card);
        }
        return false;
    }

    // Capture treasure
    private boolean captureTreasure(Player player, Tile treasureTile) {
        if (!game.getBoard().canCaptureTreasure(treasureTile, player)) {
            return false;
        }

        // Find 4 corresponding treasure cards and discard them
        TreasureType treasureType = treasureTile.getTreasure();
        List<TreasureCard> cardsToDiscard = new ArrayList<>();

        for (Card card : player.getHand()) {
            if (card instanceof TreasureCard) {
                TreasureCard treasureCard = (TreasureCard) card;
                if (treasureCard.getTreasureType() == treasureType) {
                    cardsToDiscard.add(treasureCard);
                    if (cardsToDiscard.size() == 4) {
                        break;
                    }
                }
            }
        }

        // Discard treasure cards and add the treasure to player's collection
        for (TreasureCard card : cardsToDiscard) {
            game.playerDiscardHandCard(player, card);
        }
        player.addCollectedTreasure(treasureType);

        // Trigger treasure captured event
        for (GameEventListener listener : game.getEventListeners()) {
            listener.onTreasureCaptured(player, treasureType);
        }
        return true;
    }

    // Go to the next phase
    public void nextPhase() {
        switch (currentPhase) {
            case ACTIONS:
                currentPhase = TurnPhase.DRAW_TREASURE;
                drawTreasureCards();
                break;

            case DRAW_TREASURE:
                currentPhase = TurnPhase.DRAW_FLOOD;
                drawFloodCards();
                break;

            case DRAW_FLOOD:
                currentPhase = TurnPhase.END;
                endTurn();
                break;

            case END:
                // Switch to next player
                int nextIndex = (game.getCurrentPlayerIndex() + 1) % players.size();
                game.setCurrentPlayerIndex(nextIndex);
                if (nextIndex == 0) {
                    roundNumber++;
                }
                // Start the next player's turn
                currentPhase = TurnPhase.ACTIONS;  // Reset to action phase
                startPlayerTurn();
                break;
        }
        // Notify GameController that phase changed
        game.notifyPhaseChanged(currentPhase);
    }

    // Draw treasure cards
    private void drawTreasureCards() {
        Player currentPlayer = getCurrentPlayer();
        boolean waterRiseCardDrawn = false;

        // Draw two treasure cards
        for (int i = 0; i < 2; i++) {
            HandCard card = game.drawTreasureCard();

            // If Water Rise card is drawn
            if (card instanceof WaterRiseCard) {
                if (waterRiseCardDrawn) {
                    // Second Water Rise card, only increase water level
                    game.increaseWaterLevel();
                } else {
                    // First Water Rise card, perform full effect
                    waterRiseCardDrawn = true;
                    ((WaterRiseCard) card).useCard(currentPlayer);
                }
                continue;
            }
            // Add normal card to hand
            if (card != null) {
                currentPlayer.addCardToHand(card);

                // Check hand limit
                if (currentPlayer.handExceedsLimit()) {
                    game.notifyPlayerMustDiscard(currentPlayer);
                    return; // Wait for player to discard before continuing
                }
            }
        }
        if (!currentPlayer.handExceedsLimit()) {
            nextPhase();
        }
    }

    // Draw flood cards
    private void drawFloodCards() {
        // Determine number of flood cards to draw based on water level
        int cardsToDrawCount = game.getWaterLevel().getFloodCardsCount();

        for (int i = 0; i < cardsToDrawCount; i++) {
            FloodCard card = game.getFloodDeck().drawCard();
            if (card != null) {
                Tile tileToFlood = game.getBoard().getTileByType(card.getTileType());

                if (tileToFlood != null) {
                    if (tileToFlood.isFlooded()) {
                        // If already flooded, sink it
                        tileToFlood.sink();
                        // Check if any player is on the sunk tile
                        checkPlayersOnSunkTile(tileToFlood);
                    } else {
                        // If not yet flooded, flood it
                        tileToFlood.flood();
                    }
                }
                // Discard the flood card
                game.getFloodDeck().discard(card);
            }
        }
        // Enter end of turn phase
        nextPhase();
    }

    private void checkPlayersOnSunkTile(Tile sunkTile) {
        for (Player player : players) {
            if (player.getCurrentTile() == sunkTile) {
                // Diver can swim to an adjacent tile
                if (player.getRole() == Role.DIVER) {
                    List<Tile> reachableTiles = game.getBoard().getReachableTilesForDiver(sunkTile);
                    if (!reachableTiles.isEmpty()) {
                        player.moveToTile(reachableTiles.get(0));
                        continue;
                    }
                }
                // Check for available adjacent tiles
                List<Tile> adjacentTiles = game.getBoard().getAdjacentTiles(sunkTile);
                List<Tile> availableTiles = new ArrayList<>();

                for (Tile tile : adjacentTiles) {
                    if (tile.isNavigable()) {
                        availableTiles.add(tile);
                    }
                }

                // If there is any available adjacent tile, move to the first one
                if (!availableTiles.isEmpty()) {
                    player.moveToTile(availableTiles.get(0));
                } else {
                    // If there is nowhere to go, game over!
                    game.triggerGameOver("A player was unable to escape from a sinking tile!");
                }
            }
        }
    }

    // End the current turn
    private void endTurn() {
        // Check if the game has ended
        if (game.checkWinCondition()) {
            game.triggerGameOver("Congratulations! You have collected all the treasures and escaped the island!");
            return;
        }
        if (game.checkLoseCondition()) {
            return; // Game has ended in checkLoseCondition
        }
        // Move to the next player's turn
        nextPhase();
    }

    // Immediately end the current player's turn (for special cards or effects)
    public void forceEndTurn() {
        currentPhase = TurnPhase.END;
        endTurn();
    }

    public void setCurrentAction(ActionType action) {
        this.currentAction = action;
    }

    public ActionType getCurrentAction() {
        return currentAction;
    }
}
