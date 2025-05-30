package model.enums;

public enum TileType {
    NONE("NONE","None"),
    FOOLS_LANDING("FOOLS_LANDING","Fools_ Landing@2x.png"),
    BRONZE_GATE("BRONZE_GATE", "Bronze Gate@2x.png"),
    COPPER_GATE("COPPER_GATE", "Copper Gate@2x.png"),
    SILVER_GATE("SILVER_GATE", "Silver Gate@2x.png"),
    GOLD_GATE("GOLD_GATE", "Gold Gate@2x.png"),
    CORAL_PALACE("CORAL_PALACE", "Coral Palace@2x.png"),
    TIDAL_PALACE("TIDAL_PALACE", "Tidal Palace@2x.png"),
    WHISPERING_GARDEN("WHISPERING_GARDEN", "Whispering Garden@2x.png"),
    HOWLING_GARDEN("HOWLING_GARDEN", "Howling Garden@2x.png"),
    TEMPLE_OF_THE_MOON("TEMPLE_OF_THE_MOON", "Temple of the Moon@2x.png"),
    TEMPLE_OF_THE_SUN("TEMPLE_OF_THE_SUN","Temple of the Sun@2x.png"),
    CAVE_OF_EMBERS("CAVE_OF_EMBERS", "Cave of Embers@2x.png"),
    CAVE_OF_SHADOWS("CAVE_OF_SHADOWS", "Cave of Shadows@2x.png"),
    MISTY_MARSH("MISTY_MARSH", "Misty Marsh@2x.png"),
    CRIMSON_FOREST("CRIMSON_FOREST", "Crimson Forest@2x.png"),
    OBSERVATORY("OBSERVATORY", "Observatory@2x.png"),
    PHANTOM_ROCK("PHANTOM_ROCK", "Phantom Rock@2x.png"),
    BREAKERS_BRIDGE("BREAKERS_BRIDGE", "Breakers Bridge@2x.png"),
    CLIFFS_OF_ABANDON("CLIFFS_OF_ABANDON", "Cliffs of Abandon@2x.png"),
    DUNES_OF_DECEPTION("DUNES_OF_DECEPTION", "Dunes of Deception@2x.png"),
    WATCHTOWER("WATCHTOWER", "Watchtower@2x.png"),
    LOST_LAGOON("LOST_LAGOON", "Lost Lagoon@2x.png"),
    TWILIGHT_HOLLOW("TWILIGHT_HOLLOW", "Twilight Hollow@2x.png"),
    IRON_GATE("IRON_GATE", "Iron Gate@2x.png");

    private final String displayName;
    private final String imagePath;

    TileType(String displayName, String imagePath) {
        this.displayName = displayName;
        this.imagePath = imagePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImagePath() {
        return imagePath;
    }
}