import controller.GameController;
import model.*;
import view.gamePanel.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ForbiddenIslandGame {
    private static void createAndShowCover(Runnable onGameStart) {
        JFrame coverFrame = new JFrame("Forbidden Island");
        coverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        coverFrame.setSize(800, 600);
        coverFrame.setLocationRelativeTo(null);

        // 创建自定义面板来绘制背景
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage backgroundImage = ImageIO.read(new File("src/com/group7/resources/images/TitleScreen.png"));
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                    setBackground(Color.BLACK); // 如果图片加载失败，使用黑色背景
                }
            }
        };
        backgroundPanel.setLayout(null);

        // 创建开始游戏按钮
        JButton startButton = new JButton("开始游戏");
        startButton.setFont(new Font("微软雅黑", Font.BOLD, 24));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(200, 120, 0));
        startButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        startButton.setFocusPainted(false);
        startButton.setBounds(300, 400, 200, 60);

        // 添加鼠标悬停效果
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(255, 150, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(200, 120, 0));
            }
        });

        startButton.addActionListener(e -> {
            coverFrame.dispose();
            onGameStart.run();
        });

        backgroundPanel.add(startButton);
        coverFrame.add(backgroundPanel);
        coverFrame.setVisible(true);
    }

    private static void startGame() {
        Game game = Game.getInstance();
        MainFrame mainFrame = MainFrame.getInstance();
        GameController gameController = new GameController(game, mainFrame);

        gameController.initializeViewFrame();
        String[] options = {"2人", "3人", "4人"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "请选择玩家人数",
                "游戏设置",
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
