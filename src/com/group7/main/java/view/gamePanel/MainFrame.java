package view.gamePanel;

import lombok.Getter;
import model.Board;
import util.Event;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public static MainFrame instance;
    public static MainFrame getInstance() {
        return instance == null ? instance = new MainFrame() : instance;
    }

    public final Event onMoveButtonClick;
    public final Event onShoreUpButtonClick;
    public final Event onGiveCardButtonClick;
    public final Event onCaptureTreasureButtonClick;
    public final Event onEndTurnButtonClick;
    public final Event onCollectTreasureButtonClick;
    public final Event onUseSpecialAbilityButtonClick;

    private GameBoardPanel gameBoardPanel;
    @Getter
    private PlayerInfoPanel playerInfoPanel;
    private CardPanel cardPanel;
    private ControlPanel controlPanel;
    private WaterLevelPanel waterLevelPanel;
    private ConsolePanel consolePanel;

    private MainFrame() {
        setTitle("Forbidden Island");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // 添加组件间距

        onMoveButtonClick = new Event();
        onShoreUpButtonClick = new Event();
        onGiveCardButtonClick = new Event();
        onCaptureTreasureButtonClick = new Event();
        onEndTurnButtonClick = new Event();
        onCollectTreasureButtonClick = new Event();
        onUseSpecialAbilityButtonClick = new Event();

        initComponents();
        addComponents();
        addAllListeners();

        // 调整窗口大小以适应内容，并居中显示
        pack();
        setLocationRelativeTo(null);

        // 设置最小窗口大小
        setMinimumSize(new Dimension(1200, 800));
    }

    /**
     * 初始化所有面板组件
     */
    private void initComponents() {
        gameBoardPanel = new GameBoardPanel();
        playerInfoPanel = new PlayerInfoPanel();
        cardPanel = new CardPanel();
        controlPanel = new ControlPanel();
        waterLevelPanel = new WaterLevelPanel();
        consolePanel = new ConsolePanel(this);

        // 设置面板的首选大小
        waterLevelPanel.setPreferredSize(new Dimension(200, 300));
        consolePanel.setPreferredSize(new Dimension(200, 300));
        playerInfoPanel.setPreferredSize(new Dimension(200, 0));
        cardPanel.setPreferredSize(new Dimension(0, 150));
    }

    /**
     * 将所有面板添加到主窗口
     */
    private void addComponents() {
        // 创建左侧面板，包含水位面板和控制台面板
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(waterLevelPanel, BorderLayout.NORTH);
        leftPanel.add(consolePanel, BorderLayout.CENTER);

        // 创建中央面板，包含游戏板和卡片面板
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建一个包装游戏板的面板，用于居中显示
        JPanel gameBoardWrapper = new JPanel(new GridBagLayout());
        gameBoardWrapper.add(gameBoardPanel);
        centerPanel.add(gameBoardWrapper, BorderLayout.CENTER);
        centerPanel.add(cardPanel, BorderLayout.SOUTH);

        // 创建右侧面板
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(playerInfoPanel, BorderLayout.CENTER);

        // 添加所有面板到主窗口
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.NORTH);
    }

    private void addAllListeners() {
        controlPanel.getMoveButton().addActionListener(e -> onMoveButtonClick.invoke(this, null));
        controlPanel.getShoreUpButton().addActionListener(e -> onShoreUpButtonClick.invoke(this, null));
        controlPanel.getGiveCardButton().addActionListener(e -> onGiveCardButtonClick.invoke(this, null));
        controlPanel.getCaptureTreasureButton().addActionListener(e -> onCaptureTreasureButtonClick.invoke(this, null));
        controlPanel.getEndTurnButton().addActionListener(e -> onEndTurnButtonClick.invoke(this, null));
    }

    public PlayerInfoPanel getPlayerInfoPanel() {
        return playerInfoPanel;
    }

    public void updateBoard(Board board) {
        gameBoardPanel.updateBoard(board);
    }

    public void updateWaterLevel(int level) {
        waterLevelPanel.setWaterLevel(level);
    }

    public void addConsoleMessage(String message) {
        consolePanel.addMessage(message);
    }

    /**
     * 显示游戏结束对话框
     * @param message 游戏结束的原因或胜利信息
     */
    public void showGameOverDialog(String message) {
        // 禁用所有控制按钮
        controlPanel.setButtonsEnabled(false);

        // 创建游戏结束对话框
        JDialog dialog = new JDialog(this, "游戏结束", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // 创建消息面板
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加消息标签
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("关闭游戏");
        closeButton.addActionListener(e -> {
            dialog.dispose();
            System.exit(0);
        });
        buttonPanel.add(closeButton);

        // 添加面板到对话框
        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 设置对话框属性
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public void setTileClickEvent(GameBoardPanel.TileClickListener listener) {
        gameBoardPanel.setTileClickListener(listener);
    }

    public CardPanel getCardPanel() {
        return cardPanel;
    }

    public interface SpecialCardCallback {
        void onHelicopterLift(model.card.SpecialCard card, int row, int col);
        void onSandbag(model.card.SpecialCard card, int row, int col);
    }

    private SpecialCardCallback specialCardCallback;

    public void setSpecialCardCallback(SpecialCardCallback callback) {
        this.specialCardCallback = callback;
    }

    public void setShoreUpCallback(Runnable callback) {
        controlPanel.getShoreUpButton().addActionListener(e -> callback.run());
    }

    public void setSpecialAbilityCallback(Runnable callback) {
        controlPanel.getGiveCardButton().addActionListener(e -> callback.run());
    }

}
