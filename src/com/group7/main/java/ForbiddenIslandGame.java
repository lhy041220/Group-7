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

        mainFrame.onUseSpecialAbilityButtonClick.addListener(sender -> {
            // 这里暂时传null，后续可联动UI选择目标格
            gameController.handleUseSpecialAbility(null);
        });

        mainFrame.setTileClickEvent((row, col) -> {
            System.out.println("点击了地图格子：(" + row + ", " + col + ")");
            // 后续可联动GameController.handlePlayerMove等方法
        });
    }
}
