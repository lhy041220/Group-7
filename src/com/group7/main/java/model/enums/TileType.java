package model.enums;

public enum TileType {
    NONE("无","None"),
    FOOLS_LANDING("愚者着陆点", "Fools_ Landing@2x.png"),
    BRONZE_GATE("青铜门", "Bronze Gate@2x.png"),
    COPPER_GATE("铜门", "Copper Gate@2x.png"),
    SILVER_GATE("银门", "Silver Gate@2x.png"),
    GOLD_GATE("金门", "Gold Gate@2x.png"),
    CORAL_PALACE("珊瑚宫", "Coral Palace@2x.png"),
    TIDAL_PALACE("潮汐宫", "Tidal Palace@2x.png"),
    WHISPERING_GARDEN("低语花园", "Whispering Garden@2x.png"),
    HOWLING_GARDEN("咆哮花园", "Howling Garden@2x.png"),
    TEMPLE_OF_THE_MOON("月亮神庙", "Temple of the Moon@2x.png"),
    TEMPLE_OF_THE_SUN("太阳神庙", "Temple of the Sun@2x.png"),
    CAVE_OF_EMBERS("余烬洞窟", "Cave of Embers@2x.png"),
    CAVE_OF_SHADOWS("暗影洞窟", "Cave of Shadows@2x.png"),
    MISTY_MARSH("迷雾沼泽", "Misty Marsh@2x.png"),
    CRIMSON_FOREST("赤红森林", "Crimson Forest@2x.png"),
    OBSERVATORY("天文台", "Observatory@2x.png"),
    PHANTOM_ROCK("幻影岩石", "Phantom Rock@2x.png"),
    BREAKERS_BRIDGE("破浪桥", "Breakers Bridge@2x.png"),
    CLIFFS_OF_ABANDON("绝望悬崖", "Cliffs of Abandon@2x.png"),
    DUNES_OF_DECEPTION("欺诈沙丘", "Dunes of Deception@2x.png"),
    WATCHTOWER("了望塔", "Watchtower@2x.png"),
    LOST_LAGOON("迷失泻湖", "Lost Lagoon@2x.png"),
    TWILIGHT_HOLLOW("暮光谷", "Twilight Hollow@2x.png"),
    IRON_GATE("铁门", "Iron Gate@2x.png");

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