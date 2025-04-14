package modelTest;

import model.Game;
import org.junit.jupiter.api.Test;

public class gameTest {
    private static Game game;
    @Test
    public void gameInitialTest() {
        // Assume there will be 4 players
        Game game = Game.getInstance();
        game.startGame(4);
    }
}
