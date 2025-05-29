package view.gamePanel;

import lombok.Getter;
import model.Board;
import util.Event;
import model.card.SpecialCard;
import model.card.HelicopterLiftCard;
import model.card.SandbagCard;

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
        controlPanel.getShoreUpButton().addActionListener(e -> {
            onShoreUpButtonClick.invoke(this, null);
            if (shoreUpCallback != null) shoreUpCallback.onShoreUpMode();
        });
        controlPanel.getGiveCardButton().addActionListener(e -> onGiveCardButtonClick.invoke(this, null));
        controlPanel.getCaptureTreasureButton().addActionListener(e -> onCaptureTreasureButtonClick.invoke(this, null));
        controlPanel.getCollectTreasureButton().addActionListener(e -> onCollectTreasureButtonClick.invoke(this, null));
        controlPanel.getUseSpecialAbilityButton().addActionListener(e -> {
            onUseSpecialAbilityButtonClick.invoke(this, null);
            if (specialAbilityCallback != null) specialAbilityCallback.onSpecialAbility();
        });
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

    public void handleSpecialCardClick(SpecialCard card) {
        if (card instanceof HelicopterLiftCard) {
            // 弹窗选择目标玩家和目标格子（这里只做简单演示，后续可美化）
            String input = JOptionPane.showInputDialog(this, "请输入目标格子的行列（如3,4）：");
            if (input != null && input.contains(",")) {
                String[] arr = input.split(",");
                try {
                    int row = Integer.parseInt(arr[0].trim());
                    int col = Integer.parseInt(arr[1].trim());
                    // 这里只传递目标格子，实际可扩展为多玩家
                    if (specialCardCallback != null) specialCardCallback.onHelicopterLift(card, row, col);
                } catch (Exception e) { JOptionPane.showMessageDialog(this, "输入格式错误"); }
            }
        } else if (card instanceof SandbagCard) {
            String input = JOptionPane.showInputDialog(this, "请输入要排水的格子的行列（如3,4）：");
            if (input != null && input.contains(",")) {
                String[] arr = input.split(",");
                try {
                    int row = Integer.parseInt(arr[0].trim());
                    int col = Integer.parseInt(arr[1].trim());
                    if (specialCardCallback != null) specialCardCallback.onSandbag(card, row, col);
                } catch (Exception e) { JOptionPane.showMessageDialog(this, "输入格式错误"); }
            }
        }
    }
    // 回调接口
    public interface SpecialCardCallback {
        void onHelicopterLift(SpecialCard card, int row, int col);
        void onSandbag(SpecialCard card, int row, int col);
    }
    private SpecialCardCallback specialCardCallback;
    public void setSpecialCardCallback(SpecialCardCallback cb) { this.specialCardCallback = cb; }

    public void highlightBoardTiles(java.util.List<int[]> positions) {
        gameBoardPanel.highlightTiles(positions);
    }
    public void clearBoardHighlight() {
        gameBoardPanel.clearHighlight();
    }

    public interface ShoreUpCallback { void onShoreUpMode(); }
    private ShoreUpCallback shoreUpCallback;
    public void setShoreUpCallback(ShoreUpCallback cb) { this.shoreUpCallback = cb; }

    public interface SpecialAbilityCallback { void onSpecialAbility(); }
    private SpecialAbilityCallback specialAbilityCallback;
    public void setSpecialAbilityCallback(SpecialAbilityCallback cb) { this.specialAbilityCallback = cb; }

    public CardPanel getCardPanel() { return cardPanel; }

}
