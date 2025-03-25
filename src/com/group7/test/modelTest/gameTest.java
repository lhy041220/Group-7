package modelTest;

import model.Game;
import model.WaterLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class gameTest {
    private static Game game;
    @Test
    public void testGame() {
        // Assume there will be 4 players
        Game game = new Game(4);
    }
}
