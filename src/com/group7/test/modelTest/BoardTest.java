package modelTest;

import model.Board;
import model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private static Board board;

    @BeforeAll
    public static void testBoardConstructor() {
        board = new Board();
    }

    @Test
    public void testGetTile() {
        Tile tile = board.getTile(0, 0);
        assert tile == null;
    }

    @Test
    public void printBoardTest() {
        board.floodTile(board.getTile(3, 3));
        board.floodTile(board.getTile(3, 1));
        board.sinkTile(board.getTile(3, 1));
        board.printBoard();
    }
}
