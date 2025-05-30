package model;

import lombok.Getter;
import model.card.*;
import model.enums.Role;
import model.enums.TreasureType;
import model.enums.ActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Player {

    private int playerId;
    private Tile currentTile;

    private int actionsPerTurn = 3; // Number of actions per turn
    private int remainingActions;   // Remaining actions in current turn
    private List<Card> hand;
    private final int MAX_HAND_SIZE = 5;
    private Role role; // Player's role

    private Set<TreasureType> collectedTreasures;
    private boolean hasUsedSpecialAbility; // Tracks special ability use, e.g. the Pilot uses once per turn

    public Player(int playerId, Tile startingTile) {
        this.playerId = playerId;
        this.currentTile = startingTile;
        this.hand = new ArrayList<>();
        this.collectedTreasures = new HashSet<>();
        this.hasUsedSpecialAbility = false;
        if (startingTile != null) {
            startingTile.addPlayer(this);
        }
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Reset the player's turn state.
     */
    public void resetActionsForTurn() {
        this.remainingActions = actionsPerTurn;
        this.hasUsedSpecialAbility = false;
    }

    /**
     * Use one action point.
     */
    public void useAction() {
        if (remainingActions > 0) {
            remainingActions--;
            // Debug info
            System.out.println("Player " + playerId + " used one action, " + remainingActions + " actions left.");
        }
    }

    /**
     * Get remaining action points.
     */
    public int getRemainingActions() {
        return remainingActions;
    }

    /**
     * Add a card to hand.
     */
    public void addCardToHand(Card card) {
        hand.add(card);
    }

    /**
     * Check if hand size exceeds limit.
     */
    public boolean handExceedsLimit() {
        return hand.size() > MAX_HAND_SIZE;
    }

    /**
     * Discard a card from hand.
     */
    public void discardCard(Card card) {
        hand.remove(card);
    }

    public boolean moveToTile(Tile destinationTile) {
        if (destinationTile == null || destinationTile.isSunk() || remainingActions <= 0) return false;
        Tile current = this.currentTile;
        if (current == null) return false;
        // 只允许上下左右移动
        int dr = Math.abs(current.getRow() - destinationTile.getRow());
        int dc = Math.abs(current.getCol() - destinationTile.getCol());
        if ((dr == 1 && dc == 0) || (dr == 0 && dc == 1)) {
            current.removePlayer(this);
            this.currentTile = destinationTile;
            this.currentTile.addPlayer(this);
            return true;
        }
        return false;
    }

    public boolean canMoveTo(Tile destinationTile) {
        if (destinationTile == null || destinationTile.isSunk() || remainingActions <= 0) return false;
        Tile current = this.currentTile;
        if (current == null) return false;
        int dr = Math.abs(current.getRow() - destinationTile.getRow());
        int dc = Math.abs(current.getCol() - destinationTile.getCol());
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1);
    }

    /**
     * Use a special card.
     */
    public void useSpecialCard(SpecialCard card) {
        if (hand.contains(card)) {
            card.useCard(this);
            hand.remove(card);
        }
    }

    /**
     * Give a card to another player.
     */
    public boolean giveCardToPlayer(Player targetPlayer, Card card) {
        // Messenger can give cards anywhere
        if (role == Role.MESSENGER) {
            if (hand.contains(card)) {
                hand.remove(card);
                targetPlayer.addCardToHand(card);
                return true;
            }
        }
        // Other roles must be on the same tile to give cards
        else if (currentTile == targetPlayer.getCurrentTile() && hand.contains(card)) {
            hand.remove(card);
            targetPlayer.addCardToHand(card);
            return true;
        }
        return false;
    }

    /**
     * Check if a specific treasure has been collected.
     */
    public boolean hasCollectedTreasure(TreasureType treasureType) {
        return collectedTreasures.contains(treasureType);
    }

    /**
     * Add collected treasure.
     */
    public void addCollectedTreasure(TreasureType treasureType) {
        collectedTreasures.add(treasureType);
    }

    /**
     * Use role's special ability.
     */
    public void useSpecialAbility(Tile destinationTile) {
        if (role != null && remainingActions > 0) {
            // Pilot每回合只能用一次特殊能力
            if (role == Role.PILOT && hasUsedSpecialAbility) {
                System.out.println("Pilot can only use special ability once per turn.");
                return;
            }
            role.useSpecialAbility(this, destinationTile);
            if (role == Role.PILOT) {
                hasUsedSpecialAbility = true;
            }
            useAction();
        }
    }

    /**
     * Get all treasure cards that can be given.
     */
    public List<TreasureCard> getGiveableTreasureCards() {
        List<TreasureCard> giveableCards = new ArrayList<>();
        for (Card card : hand) {
            if (card instanceof TreasureCard) {
                giveableCards.add((TreasureCard) card);
            }
        }
        return giveableCards;
    }

    /**
     * Try to shore up a tile.
     * @param tile Target tile
     * @return Whether shore up is successful
     */
    public boolean shoreUp(Tile tile) {
        if (canShoreUp(tile) && remainingActions > 0) {
            tile.shoreUp();
            useAction();
            return true;
        }
        return false;
    }

    /**
     * Check if the player can shore up the target tile.
     */
    public boolean canShoreUp(Tile tile) {
        if (tile == null || !tile.isFlooded()) {
            return false;
        }

        // Engineer can shore up two tiles with one action
        if (role == Role.ENGINEER) {
            List<Tile> reachableTiles = Game.getInstance().getBoard().getAdjacentTiles(currentTile);
            return reachableTiles.contains(tile) || tile == currentTile;
        }

        // Explorer can shore up diagonally
        if (role == Role.EXPLORER) {
            List<Tile> reachableTiles = Game.getInstance().getBoard().getDiagonalAndOrthogonalTiles(currentTile);
            return reachableTiles.contains(tile) || tile == currentTile;
        }

        // Other roles can shore up only adjacent or current tile
        List<Tile> adjacentTiles = Game.getInstance().getBoard().getAdjacentTiles(currentTile);
        return adjacentTiles.contains(tile) || tile == currentTile;
    }

    /**
     * Attempt to capture a treasure.
     * @param treasureType The type of treasure
     * @return Whether the treasure was successfully captured
     */
    public boolean captureTreasure(TreasureType treasureType) {
        if (canCaptureTreasure(treasureType) && remainingActions > 0) {
            // Remove 4 corresponding treasure cards
            int count = 0;
            List<Card> cardsToRemove = new ArrayList<>();

            for (Card card : hand) {
                if (card instanceof TreasureCard && ((TreasureCard) card).getTreasureType() == treasureType) {
                    cardsToRemove.add(card);
                    count++;
                    if (count == 4) break;
                }
            }

            if (count == 4) {
                hand.removeAll(cardsToRemove);
                addCollectedTreasure(treasureType);
                useAction();
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the treasure can be captured.
     */
    public boolean canCaptureTreasure(TreasureType treasureType) {
        if (treasureType == null || treasureType == TreasureType.NONE) {
            return false;
        }

        // Check if on the corresponding treasure tile
        if (!currentTile.hasTreasure(treasureType)) {
            return false;
        }

        // Check if there are enough treasure cards
        int treasureCardCount = 0;
        for (Card card : hand) {
            if (card instanceof TreasureCard && ((TreasureCard) card).getTreasureType() == treasureType) {
                treasureCardCount++;
            }
        }

        return treasureCardCount >= 4;
    }

    /**
     * Get all possible actions player can currently execute.
     */
    public List<PossibleAction> getAvailableActions() {
        List<PossibleAction> actions = new ArrayList<>();

        // Check possible moves
        for (Tile tile : Game.getInstance().getBoard().getAdjacentTiles(currentTile)) {
            if (canMoveTo(tile)) {
                actions.add(new PossibleAction(ActionType.MOVE, tile));
            }
        }

        // Check possible shore up actions
        for (Tile tile : Game.getInstance().getBoard().getAdjacentTiles(currentTile)) {
            if (canShoreUp(tile)) {
                actions.add(new PossibleAction(ActionType.SHORE_UP, tile));
            }
        }

        // Check if can capture treasure
        for (TreasureType treasureType : TreasureType.values()) {
            if (canCaptureTreasure(treasureType)) {
                actions.add(new PossibleAction(ActionType.CAPTURE_TREASURE, treasureType));
            }
        }

        // Check if can give treasure cards to other players
        List<TreasureCard> giveableCards = getGiveableTreasureCards();
        if (!giveableCards.isEmpty()) {
            for (Player otherPlayer : Game.getInstance().getPlayers()) {
                if (otherPlayer != this && (role == Role.MESSENGER || currentTile == otherPlayer.getCurrentTile())) {
                    actions.add(new PossibleAction(ActionType.GIVE_CARD, otherPlayer));
                }
            }
        }

        return actions;
    }

    /**
     * Check if the player can collect a treasure on current tile.
     */
    public boolean canCollectTreasure() {
        // Check if current tile has a treasure
        if (currentTile == null || currentTile.getTreasure() == TreasureType.NONE) {
            return false;
        }

        // Check if there are enough treasure cards
        TreasureType tileType = currentTile.getTreasure();
        int count = 0;
        for (Card card : hand) {
            if (card instanceof TreasureCard && ((TreasureCard) card).getTreasureType() == tileType) {
                count++;
            }
        }
        return count >= 4;
    }

    /**
     * Collect the treasure on the current tile.
     */
    public boolean collectTreasure() {
        if (!canCollectTreasure()) {
            return false;
        }

        TreasureType tileType = currentTile.getTreasure();
        List<Card> cardsToRemove = new ArrayList<>();
        int count = 0;

        // Find the cards to remove
        for (Card card : hand) {
            if (card instanceof TreasureCard && ((TreasureCard) card).getTreasureType() == tileType) {
                cardsToRemove.add(card);
                count++;
                if (count >= 4) {
                    break;
                }
            }
        }

        // Remove cards and add collected treasure
        if (count >= 4) {
            hand.removeAll(cardsToRemove);
            addCollectedTreasure(tileType);
            return true;
        }

        return false;
    }
}


