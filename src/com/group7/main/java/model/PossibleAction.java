package model;

import lombok.Getter;
import model.enums.ActionType;

/**
 * Represents an executable action and its target.
 */
@Getter
public class PossibleAction {
    private final ActionType actionType;
    private final Object target; // Can be Tile, Player, TreasureType, etc.

    public PossibleAction(ActionType actionType, Object target) {
        this.actionType = actionType;
        this.target = target;
    }

    /**
     * Get a description of this action.
     */
    public String getDescription() {
        String baseDescription = actionType.getDisplayName();
        switch (actionType) {
            case MOVE:
                return baseDescription + " to " + ((Tile) target).getName();
            case SHORE_UP:
                return baseDescription + " " + ((Tile) target).getName();
            case GIVE_CARD:
                return baseDescription + " to Player " + ((Player) target).getPlayerId();
            case CAPTURE_TREASURE:
                return baseDescription + ": " + target.toString();
            case USE_SPECIAL_ABILITY:
            case USE_SPECIAL_CARD:
                return baseDescription + ": " + target.toString();
            default:
                return baseDescription;
        }
    }

    @Override
    public String toString() {
        return getDescription();
    }
}