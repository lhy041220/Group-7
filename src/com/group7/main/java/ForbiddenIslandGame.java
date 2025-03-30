import controller.GameController;
import model.*;
import model.enums.*;
import view.MainFrame;

public class ForbiddenIslandGame {
    public static void main(String[] args) {
        Game game = new Game();
        MainFrame mainFrame = new MainFrame();
        GameController gameController = new GameController(game, mainFrame);

        gameController.initializeViewFrame();
        gameController.startGame();
    }
}
