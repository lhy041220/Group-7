package model.enums;

public enum TreasureType {
    EARTH("地之石"),
    WIND("风之雕像"),
    FIRE("火焰水晶"),
    OCEAN("海洋圣杯"),
    NONE("无");

    private final String displayName;

    TreasureType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
