package model;

/***
 * Manages the game's water level.
 * Controls how many flood cards to draw each turn.
 * Provides methods for rising water level and danger threshold detection.
 */
public class WaterLevel {
    // Number of flood cards to draw at each water level
    private static final int[] LEVELS = {2, 2, 3, 3, 3, 4, 4, 5, 5, 6};

    private int level;

    public WaterLevel() {
        this.level = 0;  // Initial water level
    }

    /**
     * Try to increase water level. Returns false when reaching the maximum (game over condition).
     */
    public boolean tryRise() {
        if (level < LEVELS.length - 1) {
            level++;
            return true;
        }
        return false;  // Water level has reached the maximum
    }

    /**
     * Get the number of flood cards to draw at the current water level.
     */
    public int getFloodCardsCount() {
        return LEVELS[level];
    }

    /**
     * Get the current water level index.
     */
    public int getCurrentLevel() {
        return level;
    }
}