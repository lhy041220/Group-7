import controller.GameController;
import model.*;
import model.enums.*;
import view.MainFrame;

public class ForbiddenIslandGame {
    public static void main(String[] args) {

        Game game = Game.getInstance();
        MainFrame mainFrame = MainFrame.getInstance();

        GameController gameController = new GameController(game, mainFrame);

        gameController.initializeViewFrame();
        gameController.startGame();
    }
}
