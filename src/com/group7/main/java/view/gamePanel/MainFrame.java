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
    public final Event onCollectTreasureButtonClick;
    public final Event onEndTurnButtonClick;
    public final Event onUseSpecialAbilityButtonClick;

    private GameBoardPanel gameBoardPanel;
    @Getter
    private PlayerInfoPanel playerInfoPanel;
    private CardPanel cardPanel;
    private ControlPanel controlPanel;
    private WaterLevelPanel waterLevelPanel;
    private ConsolePanel consolePanel;

    public interface TileClickEvent {
        void onTileClicked(int row, int col);
    }
    private TileClickEvent tileClickEvent;
    public void setTileClickEvent(TileClickEvent event) {
        this.tileClickEvent = event;
        gameBoardPanel.setTileClickListener((row, col) -> {
            if (tileClickEvent != null) tileClickEvent.onTileClicked(row, col);
        });
    }

    private MainFrame() {
        setTitle("Forbidden Island");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 700);
        setLayout(new BorderLayout());

        onMoveButtonClick = new Event();
        onShoreUpButtonClick = new Event();
        onGiveCardButtonClick = new Event();
        onCaptureTreasureButtonClick = new Event();
        onCollectTreasureButtonClick = new Event();
        onEndTurnButtonClick = new Event();
        onUseSpecialAbilityButtonClick = new Event();

        initComponents();
        addComponents();
        addAllListeners();
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

    private void addAllListeners() {
        controlPanel.getMoveButton().addActionListener(e -> onMoveButtonClick.invoke(this, null));
        controlPanel.getShoreUpButton().addActionListener(e -> onShoreUpButtonClick.invoke(this, null));
        controlPanel.getGiveCardButton().addActionListener(e -> onGiveCardButtonClick.invoke(this, null));
        controlPanel.getCaptureTreasureButton().addActionListener(e -> onCaptureTreasureButtonClick.invoke(this, null));
        controlPanel.getCollectTreasureButton().addActionListener(e -> onCollectTreasureButtonClick.invoke(this, null));
        controlPanel.getUseSpecialAbilityButton().addActionListener(e -> onUseSpecialAbilityButtonClick.invoke(this, null));
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

    public void updatePlayerHand(java.util.List<model.card.Card> cards) {
        cardPanel.updatePlayerHand(cards);
    }

    public ControlPanel getControlPanel() { return controlPanel; }

}
