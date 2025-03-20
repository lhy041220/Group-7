package modelTest;

import model.Game;
import model.WaterLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class gameTest {
    private static Game game;
    private static WaterLevel waterLevel;

    @BeforeAll
    public static void testGame() {
        game = new Game();
        waterLevel = new WaterLevel();
    }
}
