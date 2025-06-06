import controller.GameController;
import model.*;
import view.coverPanel.Cover;
import view.gamePanel.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ForbiddenIslandGame {
    private static void startGame() {
        Game game = Game.getInstance();
        MainFrame mainFrame = MainFrame.getInstance();
        GameController gameController = new GameController(game, mainFrame);

        gameController.initializeViewFrame();
        String[] options = {"2 Players", "3 Players", "4 Players"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Please select the number of players",
                "Game Settings",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice != JOptionPane.CLOSED_OPTION) {
            int playerNum = choice + 2;
            gameController.startGame(playerNum);

            mainFrame.onCollectTreasureButtonClick.addListener(sender -> {
                gameController.handleCollectTreasure();
            });

            mainFrame.onUseSpecialAbilityButtonClick.addListener(sender -> {
                gameController.handleUseSpecialAbility(null);
            });

            // Main button event bindings for core actions
            mainFrame.onMoveButtonClick.addListener(sender -> {
                MainFrame mf = MainFrame.getInstance();
                mf.addConsoleMessage("Please select the tile to move to.");
                mf.getGameBoardPanel().setMode(view.gamePanel.GameBoardPanel.Mode.MOVE);
                Player player = game.getCurrentPlayer();
                List<Tile> movableTiles = game.getBoard().getMovableTilesForPlayer(player);
                List<int[]> positions = new ArrayList<>();
                for (Tile t : movableTiles) {
                    int[] pos = game.getBoard().getTilePosition(t);
                    if (pos != null) positions.add(pos);
                }
                mf.getGameBoardPanel().highlightTiles(positions);
            });

            mainFrame.setTileClickEvent((row, col) -> {
                if (mainFrame.getGameBoardPanel().getMode() == view.gamePanel.GameBoardPanel.Mode.MOVE) {
                    Board board = game.getBoard();
                    Tile target = null;
                    try { target = board.getTile(row, col); } catch (Exception ignored) {}
                    Player player = game.getCurrentPlayer();
                    board.debugPrintMovableTiles(player);
                    if (target != null) {
                        System.out.println("[DEBUG] click target " + target.getType().getDisplayName() + " hash=" + System.identityHashCode(target));
                    }
                    List<Tile> movable = board.getMovableTilesForPlayer(player);
                    if (target != null && movable.contains(target)) {
                        gameController.handlePlayerMove(target);
                        mainFrame.getGameBoardPanel().clearHighlight();
                    } else {
                        mainFrame.addConsoleMessage("无法移动到该格子！");
                    }
                }
            });

            mainFrame.onShoreUpButtonClick.addListener(sender -> {
                Player player = game.getCurrentPlayer();
                List<Tile> shoreableTiles = new ArrayList<>();
                // Find all shore-up targets
                for (Tile tile : game.getBoard().getAllTiles()) {
                    if (player.canShoreUp(tile)) {
                        shoreableTiles.add(tile);
                    }
                }
                if (shoreableTiles.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "No tiles can be shored up!", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Tile selected = (Tile) JOptionPane.showInputDialog(
                        mainFrame,
                        "Select a tile to shore up:",
                        "Shore Up",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        shoreableTiles.toArray(),
                        shoreableTiles.get(0)
                );
                if (selected != null) {
                    gameController.handlePlayerShoreUp(selected);
                }
            });
            mainFrame.onGiveCardButtonClick.addListener(sender -> {
                Player player = game.getCurrentPlayer();
                List<Player> others = new ArrayList<>();
                for (Player p : game.getPlayers()) {
                    if (p != player) {
                        if (player.getRole() != null && player.getRole().getDisplayName().equals("Messenger")) {
                            others.add(p);
                        } else if (p.getCurrentTile() == player.getCurrentTile()) {
                            others.add(p);
                        }
                    }
                }
                List<model.card.TreasureCard> giveableCards = player.getGiveableTreasureCards();
                if (others.isEmpty() || giveableCards.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "No available players or treasure cards to give!", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Player target = (Player) JOptionPane.showInputDialog(
                        mainFrame,
                        "Select a player to give a card to:",
                        "Give Card",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        others.toArray(),
                        others.get(0)
                );
                if (target == null) return;
                model.card.TreasureCard card = (model.card.TreasureCard) JOptionPane.showInputDialog(
                        mainFrame,
                        "Select a treasure card to give:",
                        "Give Card",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        giveableCards.toArray(),
                        giveableCards.get(0)
                );
                if (card == null) return;
                gameController.handleGiveCard(player, target, card);
            });
            mainFrame.onCaptureTreasureButtonClick.addListener(sender -> {
                gameController.handleCollectTreasure();
            });
            mainFrame.onEndTurnButtonClick.addListener(sender -> {
                gameController.endPlayerTurn();
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

            mainFrame.getControlPanel().getViewTreasureDiscardButton().addActionListener(e -> {
                List<? extends model.card.Card> discardPile = game.getTreasureDeck().getDiscardPile();
                if (discardPile.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "Treasure discard pile is empty.", "Treasure Discard Pile", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (model.card.Card card : discardPile) {
                    sb.append(card.getName()).append("\n");
                }
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(300, 200));
                JOptionPane.showMessageDialog(mainFrame, scrollPane, "Treasure Discard Pile", JOptionPane.INFORMATION_MESSAGE);
            });
            mainFrame.getControlPanel().getViewFloodDiscardButton().addActionListener(e -> {
                List<? extends model.card.Card> discardPile = game.getFloodDeck().getDiscardPile();
                if (discardPile.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "Flood discard pile is empty.", "Flood Discard Pile", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (model.card.Card card : discardPile) {
                    sb.append(card.getName()).append("\n");
                }
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(300, 200));
                JOptionPane.showMessageDialog(mainFrame, scrollPane, "Flood Discard Pile", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Cover.createAndShowCover(ForbiddenIslandGame::startGame);
        });
    }
}
