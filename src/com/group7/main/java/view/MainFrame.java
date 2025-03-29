package view;

import model.Board;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private GameBoardPanel gameBoardPanel;
    private PlayerInfoPanel playerInfoPanel;
    private CardPanel cardPanel;
    private ControlPanel controlPanel;
    private WaterLevelPanel waterLevelPanel;
    private ConsolePanel consolePanel;

    public MainFrame() {
        setTitle("Forbidden Island");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 700);
        setLayout(new BorderLayout());

        initComponents();
        addComponents();
    }

    /**
     * 初始化所有面板组件
     */
    private void initComponents() {
        gameBoardPanel = new GameBoardPanel(new Board());
        playerInfoPanel = new PlayerInfoPanel();
        cardPanel = new CardPanel();
        controlPanel = new ControlPanel();
        waterLevelPanel = new WaterLevelPanel();
        consolePanel = new ConsolePanel();
    }

    /**
     * 将所有面板添加到主窗口
     */
    private void addComponents() {
        // 创建左侧面板，包含水位面板和控制台面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(waterLevelPanel, BorderLayout.NORTH);
        leftPanel.add(consolePanel, BorderLayout.CENTER);

        // 创建中央面板，包含游戏板和卡片面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(gameBoardPanel, BorderLayout.CENTER);
        centerPanel.add(cardPanel, BorderLayout.SOUTH);

        // 添加所有面板到主窗口
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(playerInfoPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.NORTH);
    }

    public void updateWaterLevel(int level) {
        waterLevelPanel.setWaterLevel(level);
    }

    public void addConsoleMessage(String message) {
        consolePanel.addMessage(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
