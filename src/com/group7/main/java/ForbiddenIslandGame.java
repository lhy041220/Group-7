import controller.GameController;
import model.*;
import view.gamePanel.MainFrame;

public class ForbiddenIslandGame {
    public static void main(String[] args) {

        Game game = Game.getInstance();
        MainFrame mainFrame = MainFrame.getInstance();

        GameController gameController = new GameController(game, mainFrame);

        gameController.initializeViewFrame();
        gameController.startGame();

        mainFrame.onCollectTreasureButtonClick.addListener(sender -> {
            gameController.handleCollectTreasure();
        });
    }
}
