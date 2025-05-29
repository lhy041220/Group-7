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
    // 自定义图片面板类
    private static class RuleImagePanel extends JPanel {
        private BufferedImage currentImage;
        private int currentIndex = 0;
        private final List<BufferedImage> images = new ArrayList<>();

        public RuleImagePanel() {
            setLayout(new BorderLayout());
            // 加载所有规则图片
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
                // 计算图片的缩放比例，保持宽高比
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
        JDialog rulesDialog = new JDialog(parent, "游戏规则", true);
        rulesDialog.setSize(900, 700);
        rulesDialog.setLocationRelativeTo(parent);

        // 创建自定义图片面板
        RuleImagePanel imagePanel = new RuleImagePanel();

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton prevButton = new JButton("上一页");
        JButton nextButton = new JButton("下一页");
        JButton closeButton = new JButton("关闭");

        // 设置按钮样式
        for (JButton button : new JButton[]{prevButton, nextButton, closeButton}) {
            button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
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

        // 创建游戏规则按钮
        JButton rulesButton = new JButton("游戏规则");
        rulesButton.setFont(new Font("微软雅黑", Font.BOLD, 24));
        rulesButton.setForeground(Color.WHITE);
        rulesButton.setBackground(new Color(200, 120, 0));
        rulesButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        rulesButton.setFocusPainted(false);
        rulesButton.setBounds(300, 450, 200, 60);

        // 添加鼠标悬停效果
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
