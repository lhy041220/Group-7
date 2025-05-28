package model;

import lombok.Getter;
import model.enums.ActionType;

/**
 * 表示一个可执行的行动及其目标
 */
@Getter
public class PossibleAction {
    private final ActionType actionType;
    private final Object target; // 可以是Tile、Player、TreasureType等

    public PossibleAction(ActionType actionType, Object target) {
        this.actionType = actionType;
        this.target = target;
    }

    /**
     * 获取行动的描述文本
     */
    public String getDescription() {
        String baseDescription = actionType.getDisplayName();
        switch (actionType) {
            case MOVE:
                return baseDescription + "到" + ((Tile) target).getName();
            case SHORE_UP:
                return baseDescription + ((Tile) target).getName();
            case GIVE_CARD:
                return baseDescription + "给玩家" + ((Player) target).getPlayerId();
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