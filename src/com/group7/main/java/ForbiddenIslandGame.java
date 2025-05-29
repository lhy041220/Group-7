import controller.GameController;
import model.*;
import view.gamePanel.MainFrame;

public class ForbiddenIslandGame {
    public static void main(String[] args) {

        Game game = Game.getInstance();
        MainFrame mainFrame = MainFrame.getInstance();

        GameController gameController = new GameController(game, mainFrame);

        gameController.initializeViewFrame();
        // 游戏启动时选择玩家人数
        String[] options = {"2", "3", "4"};
        String numStr = (String) javax.swing.JOptionPane.showInputDialog(null, "请选择玩家人数：", "玩家人数", javax.swing.JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        int playerNum = 3;
        try { if (numStr != null) playerNum = Integer.parseInt(numStr); } catch (Exception e) {}
        gameController.startGame(playerNum);

        mainFrame.onCollectTreasureButtonClick.addListener(sender -> {
            gameController.handleCollectTreasure();
        });

        mainFrame.onUseSpecialAbilityButtonClick.addListener(sender -> {
            // 这里暂时传null，后续可联动UI选择目标格
            gameController.handleUseSpecialAbility(null);
        });

        mainFrame.setTileClickEvent((row, col) -> {
            gameController.handleTileClick(row, col);
        });

        mainFrame.setSpecialCardCallback(new MainFrame.SpecialCardCallback() {
            @Override
            public void onHelicopterLift(model.card.SpecialCard card, int row, int col) {
                gameController.handlePlayerUseHelicopterLift(card, row, col);
            }
            @Override
            public void onSandbag(model.card.SpecialCard card, int row, int col) {
                gameController.handlePlayerUseSandbag(card, row, col);
            }
        });

        mainFrame.setShoreUpCallback(() -> gameController.enterShoreUpMode());

        mainFrame.setSpecialAbilityCallback(() -> gameController.enterNavigatorMode());
    }
}
