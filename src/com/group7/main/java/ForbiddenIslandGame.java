import controller.GameController;
import model.*;
import view.gamePanel.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ForbiddenIslandGame {
    // Custom panel class for displaying rule images
    private static class RuleImagePanel extends JPanel {
        private BufferedImage currentImage;
        private int currentIndex = 0;
        private final List<BufferedImage> images = new ArrayList<>();

        public RuleImagePanel() {
            setLayout(new BorderLayout());
            // Load all rule images
            for (int i = 1; i <= 8; i++) {
                try {
                    BufferedImage img = ImageIO.read(new File("src/com/group7/resources/images/Rules/rule" + i + ".png"));
                    images.add(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!images.isEmpty()) {
                currentImage = images.get(0);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentImage != null) {
                // Scale image to fit the panel, maintaining aspect ratio
                double scale = Math.min(
                        (double) (getWidth() - 40) / currentImage.getWidth(),
                        (double) (getHeight() - 40) / currentImage.getHeight()
                );
                int width = (int) (currentImage.getWidth() * scale);
                int height = (int) (currentImage.getHeight() * scale);
                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;

                g.drawImage(currentImage, x, y, width, height, this);
            }
        }

        public void nextImage() {
            if (!images.isEmpty()) {
                currentIndex = (currentIndex + 1) % images.size();
                currentImage = images.get(currentIndex);
                repaint();
            }
        }

        public void previousImage() {
            if (!images.isEmpty()) {
                currentIndex = (currentIndex - 1 + images.size()) % images.size();
                currentImage = images.get(currentIndex);
                repaint();
            }
        }
    }

    private static void showRulesDialog(JFrame parent) {
        JDialog rulesDialog = new JDialog(parent, "Game Rules", true);
        rulesDialog.setSize(900, 700);
        rulesDialog.setLocationRelativeTo(parent);

        // Custom image panel
        RuleImagePanel imagePanel = new RuleImagePanel();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton closeButton = new JButton("Close");

        // Button styles
        for (JButton button : new JButton[]{prevButton, nextButton, closeButton}) {
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setPreferredSize(new Dimension(100, 30));
        }

        prevButton.addActionListener(e -> imagePanel.previousImage());
        nextButton.addActionListener(e -> imagePanel.nextImage());
        closeButton.addActionListener(e -> rulesDialog.dispose());

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(closeButton);

        rulesDialog.setLayout(new BorderLayout(10, 10));
        rulesDialog.add(imagePanel, BorderLayout.CENTER);
        rulesDialog.add(buttonPanel, BorderLayout.SOUTH);
        rulesDialog.setVisible(true);
    }

    private static void createAndShowCover(Runnable onGameStart) {
        JFrame coverFrame = new JFrame("Forbidden Island");
        coverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        coverFrame.setSize(800, 600);
        coverFrame.setLocationRelativeTo(null);

        // Custom panel for background drawing
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage backgroundImage = ImageIO.read(new File("src/com/group7/resources/images/TitleScreen.png"));
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                    setBackground(Color.BLACK); // Fallback to black background
                }
            }
        };
        backgroundPanel.setLayout(null);

        // "Start Game" button
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(200, 120, 0));
        startButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        startButton.setFocusPainted(false);
        startButton.setBounds(300, 320, 200, 60);

        // "Rules" button
        JButton rulesButton = new JButton("Game Rules");
        rulesButton.setFont(new Font("Arial", Font.BOLD, 24));
        rulesButton.setForeground(Color.WHITE);
        rulesButton.setBackground(new Color(200, 120, 0));
        rulesButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        rulesButton.setFocusPainted(false);
        rulesButton.setBounds(300, 420, 200, 60);

        // Mouse hover effect
        java.awt.event.MouseAdapter hoverEffect = new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ((JButton)evt.getSource()).setBackground(new Color(255, 150, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                ((JButton)evt.getSource()).setBackground(new Color(200, 120, 0));
            }
        };
        startButton.addMouseListener(hoverEffect);
        rulesButton.addMouseListener(hoverEffect);

        startButton.addActionListener(e -> {
            coverFrame.dispose();
            onGameStart.run();
        });

        rulesButton.addActionListener(e -> showRulesDialog(coverFrame));

        backgroundPanel.add(startButton);
        backgroundPanel.add(rulesButton);
        coverFrame.add(backgroundPanel);
        coverFrame.setVisible(true);
    }

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
                // Highlight movable tiles
                Player player = game.getCurrentPlayer();
                java.util.List<Tile> movableTiles = new java.util.ArrayList<>();
                for (Tile tile : game.getBoard().getAllTiles()) {
                    if (player.canMoveTo(tile) && tile != player.getCurrentTile()) {
                        movableTiles.add(tile);
                    }
                }
                java.util.List<int[]> positions = new java.util.ArrayList<>();
                for (Tile t : movableTiles) {
                    int[] pos = ((model.Board)game.getBoard()).getTilePosition(t);
                    if (pos != null) positions.add(pos);
                }
                mf.getGameBoardPanel().highlightTiles(positions);
            });

            // Set board click event
            mainFrame.setTileClickEvent((row, col) -> {
                if (mainFrame.getGameBoardPanel().getMode() == view.gamePanel.GameBoardPanel.Mode.MOVE) {
                    if (!mainFrame.getGameBoardPanel().isTileHighlighted(row, col)) return;
                    Board board = game.getBoard();
                    Tile target = null;
                    try { target = board.getTile(row, col); } catch (Exception ignored) {}
                    if (target != null) {
                        gameController.handlePlayerMove(target);
                        mainFrame.getGameBoardPanel().clearHighlight();
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
                        // Messenger may ignore tile position; others must be on same tile
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
                if (player.giveCardToPlayer(target, card)) {
                    mainFrame.addConsoleMessage("Player " + player.getPlayerId() + " gave a card to Player " + target.getPlayerId());
                    mainFrame.getPlayerInfoPanel().updatePlayerInfos(game.getPlayers(), game.getCurrentPlayerIndex());
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to give the card! Check the rules.", "Notice", JOptionPane.WARNING_MESSAGE);
                }
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
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowCover(() -> startGame());
        });
    }
}
