package view;

import javax.swing.*;
import java.awt.*;

public class ForbiddenIslandGameView extends JFrame {

    private JPanel gameBoard;
    private JPanel playerPanel;
    private JPanel controlPanel;
    private JPanel infoPanel;

    public ForbiddenIslandGameView() {
        // 基本窗口设置
        setTitle("Forbidden Island");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout(5, 5));

        // 创建顶部面板
        createTopPanel();

        // 创建游戏主面板
        createGameBoard();

        // 创建右侧信息面板
        createInfoPanel();

        // 创建底部玩家面板
        createPlayerPanel();

        // 设置整体背景色
        getContentPane().setBackground(new Color(50, 50, 65));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(40, 40, 55));
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel titleLabel = new JLabel("FORBIDDEN ISLAND");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel waterLevelPanel = new JPanel();
        waterLevelPanel.setBackground(new Color(30, 30, 45));
        waterLevelPanel.setPreferredSize(new Dimension(60, 50));

        JLabel waterLabel = new JLabel("水位");
        waterLabel.setForeground(Color.WHITE);
        waterLevelPanel.add(waterLabel);

        JPanel levelPanel = new JPanel(new GridLayout(6, 1));
        levelPanel.setBackground(new Color(20, 70, 120));

        for (int i = 5; i > 0; i--) {
            JLabel level = new JLabel(String.valueOf(i));
            level.setForeground(Color.WHITE);
            level.setHorizontalAlignment(JLabel.CENTER);
            level.setOpaque(true);

            if (i >= 4) {
                level.setBackground(new Color(0, 150, 255));
            } else if (i >= 2) {
                level.setBackground(new Color(0, 100, 200));
            } else {
                level.setBackground(new Color(0, 50, 150));
            }

            levelPanel.add(level);
        }

        JLabel dangerLabel = new JLabel("危险");
        dangerLabel.setForeground(Color.WHITE);
        dangerLabel.setHorizontalAlignment(JLabel.CENTER);
        dangerLabel.setBackground(new Color(180, 0, 0));
        dangerLabel.setOpaque(true);
        levelPanel.add(dangerLabel);

        waterLevelPanel.add(levelPanel);
        topPanel.add(waterLevelPanel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createGameBoard() {
        gameBoard = new JPanel(new GridLayout(6, 6, 3, 3));
        gameBoard.setBackground(new Color(20, 60, 100));
        gameBoard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建宝藏丢弃区
        JPanel treasureDiscard = new JPanel();
        treasureDiscard.setBackground(new Color(180, 90, 70));
        treasureDiscard.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "Treasure Discard",
                0, 0, null, Color.WHITE));
        gameBoard.add(treasureDiscard);

        // 添加游戏板块
        for (int i = 0; i < 24; i++) {
            JPanel tile = createTile("Tile " + (i + 1));
            gameBoard.add(tile);
        }

        // 创建洪水丢弃区
        JPanel floodDiscard = new JPanel();
        floodDiscard.setBackground(new Color(70, 90, 180));
        floodDiscard.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "Flood Discard",
                0, 0, null, Color.WHITE));
        gameBoard.add(floodDiscard);

        // 填充其余位置
        for (int i = 0; i < 10; i++) {
            JPanel emptyTile = new JPanel();
            emptyTile.setBackground(new Color(30, 30, 45));
            gameBoard.add(emptyTile);
        }

        add(gameBoard, BorderLayout.CENTER);
    }

    private JPanel createTile(String name) {
        JPanel tile = new JPanel(new BorderLayout());
        tile.setBackground(new Color(50, 120, 50));
        tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 添加标题
        JLabel titleLabel = new JLabel(name);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBackground(new Color(40, 40, 40));
        titleLabel.setOpaque(true);
        tile.add(titleLabel, BorderLayout.SOUTH);

        // 中心区域，可放置玩家棋子
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(new Color(70, 140, 70));

        // 随机添加1-2个玩家棋子
        if (Math.random() > 0.7) {
            JPanel redPlayer = new JPanel();
            redPlayer.setBackground(Color.RED);
            redPlayer.setPreferredSize(new Dimension(15, 15));
            centerPanel.add(redPlayer);
        }

        if (Math.random() > 0.8) {
            JPanel bluePlayer = new JPanel();
            bluePlayer.setBackground(Color.BLUE);
            bluePlayer.setPreferredSize(new Dimension(15, 15));
            centerPanel.add(bluePlayer);
        }

        tile.add(centerPanel, BorderLayout.CENTER);

        return tile;
    }

    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(200, getHeight()));
        infoPanel.setBackground(new Color(40, 40, 55));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 信息区域
        JPanel infoArea = new JPanel();
        infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
        infoArea.setBackground(new Color(50, 50, 65));
        infoArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Info",
                0, 0, null, Color.WHITE));

        JLabel gameInfo = new JLabel("FORBIDDEN ISLAND");
        gameInfo.setForeground(Color.WHITE);
        gameInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoArea.add(gameInfo);

        JLabel createdBy = new JLabel("CREATED BY:");
        createdBy.setForeground(Color.WHITE);
        createdBy.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoArea.add(createdBy);

        JLabel author = new JLabel("Matt Leacock");
        author.setForeground(Color.LIGHT_GRAY);
        author.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoArea.add(author);

        infoPanel.add(infoArea);

        // 卡片区域
        JPanel cardsArea = new JPanel();
        cardsArea.setLayout(new BoxLayout(cardsArea, BoxLayout.Y_AXIS));
        cardsArea.setBackground(new Color(50, 50, 65));
        cardsArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Cards",
                0, 0, null, Color.WHITE));

        // 卡片滚动面板
        JPanel cardList = new JPanel();
        cardList.setLayout(new BoxLayout(cardList, BoxLayout.Y_AXIS));
        cardList.setBackground(new Color(40, 40, 55));

        for (int i = 1; i <= 5; i++) {
            JPanel card = new JPanel();
            card.setPreferredSize(new Dimension(180, 60));
            card.setBackground(new Color(60, 60, 75));
            card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            JLabel cardLabel = new JLabel("Card " + i);
            cardLabel.setForeground(Color.WHITE);
            card.add(cardLabel);

            cardList.add(card);
            cardList.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JScrollPane scrollPane = new JScrollPane(cardList);
        scrollPane.setPreferredSize(new Dimension(180, 200));
        scrollPane.setBorder(null);
        cardsArea.add(scrollPane);

        infoPanel.add(cardsArea);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 5, 5));
        buttonPanel.setBackground(new Color(50, 50, 65));

        String[] buttonLabels = {"Move To", "Shore Up", "Pass To", "Capture", "Lift Off", "Special Actions", "Next"};

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setBackground(new Color(70, 70, 90));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            buttonPanel.add(button);
        }

        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(buttonPanel);

        add(infoPanel, BorderLayout.EAST);
    }

    private void createPlayerPanel() {
        playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        playerPanel.setBackground(new Color(40, 40, 55));
        playerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        // 创建玩家手牌
        for (int i = 0; i < 5; i++) {
            JPanel card = new JPanel();
            card.setPreferredSize(new Dimension(50, 70));

            if (i % 2 == 0) {
                card.setBackground(new Color(160, 60, 60));
            } else {
                card.setBackground(new Color(60, 60, 160));
            }

            card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            playerPanel.add(card);
        }

        // 添加玩家标记
        JPanel redPlayer = new JPanel();
        redPlayer.setBackground(Color.RED);
        redPlayer.setPreferredSize(new Dimension(30, 30));
        playerPanel.add(redPlayer);

        JPanel bluePlayer = new JPanel();
        bluePlayer.setBackground(Color.BLUE);
        bluePlayer.setPreferredSize(new Dimension(30, 30));
        playerPanel.add(bluePlayer);

        JPanel greenPlayer = new JPanel();
        greenPlayer.setBackground(Color.GREEN);
        greenPlayer.setPreferredSize(new Dimension(30, 30));
        playerPanel.add(greenPlayer);

        // 添加控制按钮
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(new Color(40, 40, 55));

        JButton discardButton = new JButton("Discard");
        discardButton.setBackground(new Color(70, 70, 90));
        discardButton.setForeground(Color.WHITE);

        JButton chatButton = new JButton("Chat");
        chatButton.setBackground(new Color(70, 70, 90));
        chatButton.setForeground(Color.WHITE);

        controlPanel.add(discardButton);
        controlPanel.add(chatButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(40, 40, 55));
        bottomPanel.add(playerPanel, BorderLayout.CENTER);
        bottomPanel.add(controlPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForbiddenIslandGameView());
    }
}
