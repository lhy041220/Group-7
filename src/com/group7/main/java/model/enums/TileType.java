package model.enums;

public enum TileType {
    FOOLS_LANDING("愚者着陆点"),
    BRONZE_GATE("青铜门"),
    COPPER_GATE("铜门"),
    SILVER_GATE("银门"),
    GOLD_GATE("金门"),
    CORAL_PALACE("珊瑚宫"),
    TIDAL_PALACE("潮汐宫"),
    WHISPERING_GARDEN("低语花园"),
    HOWLING_GARDEN("咆哮花园"),
    TEMPLE_OF_THE_MOON("月亮神庙"),
    TEMPLE_OF_THE_SUN("太阳神庙"),
    CAVE_OF_EMBERS("余烬洞窟"),
    CAVE_OF_SHADOWS("暗影洞窟"),
    MISTY_MARSH("迷雾沼泽"),
    CRIMSON_FOREST("赤红森林"),
    OBSERVATORY("天文台"),
    PHANTOM_ROCK("幻影岩石"),
    BREAKERS_BRIDGE("破浪桥"),
    CLIFFS_OF_ABANDON("绝望悬崖"),
    DUNES_OF_DECEPTION("欺诈沙丘"),
    WATCHTOWER("了望塔"),
    LOST_LAGOON("迷失泻湖"),
    TWILIGHT_HOLLOW("暮光谷"),
    IRON_GATE("铁门");

    private final String displayName;

    TileType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}