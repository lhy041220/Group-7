package model.enums;

public enum TreasureType {
    EARTH("EARTH"),
    WIND("WIND"),
    FIRE("FIRE"),
    OCEAN("OCEAN"),
    NONE("NONE");

    private final String displayName;

    TreasureType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
