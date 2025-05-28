package model.enums;

/**
 * 玩家可以执行的行动类型
 */
public enum ActionType {
    MOVE("移动"),
    SHORE_UP("排水"),
    GIVE_CARD("给予卡牌"),
    CAPTURE_TREASURE("获取宝藏"),
    USE_SPECIAL_ABILITY("使用特殊能力"),
    USE_SPECIAL_CARD("使用特殊卡牌");

    private final String displayName;

    ActionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}