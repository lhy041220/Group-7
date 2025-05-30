package model.enums;

/**
 * The types of actions that players can perform
 */
public enum ActionType {
    MOVE("Move"),
    SHORE_UP("Drain water"),
    GIVE_CARD("Give cards"),
    CAPTURE_TREASURE("Obtain the treasure"),
    USE_SPECIAL_ABILITY("Use special abilities"),
    USE_SPECIAL_CARD("Use special cards");

    private final String displayName;

    ActionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}