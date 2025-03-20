package model;

/***
 * 管理游戏水位
 * 控制每回合抽取洪水卡的数量
 * 提供水位上升方法和危险等级判断
 */
public class WaterLevel {
    // 不同水位对应的抽取洪水卡数量
    private static final int[] LEVELS = {2, 2, 3, 3, 3, 4, 4, 5, 5, 6};

    private int level;

    public WaterLevel() {
        this.level = 0;  // 初始水位
    }

    /**
     * 尝试返回水位上升。返回false时一般是游戏失败的时候
     */
    public boolean tryRise() {
        if (level < LEVELS.length - 1) {
            level++;
            return true;
        }
        return false;  // 水位到达最高点
    }

    /**
     * 获取当前水位下需要抽取的洪水卡数量
     */
    public int getFloodCardsCount() {
        return LEVELS[level];
    }

    /**
     * 获取当前水位级别
     */
    public int getCurrentLevel() {
        return level;
    }
}
