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
        setLayout(new BorderLayout(10, 10)); // Add spacing between components

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

        // Adjust window size to fit content and center it
        pack();
        setLocationRelativeTo(null);

        // Set minimum window size
        setMinimumSize(new Dimension(1200, 800));
    }

    /**
     * Initialize all panel components
     */
    private void initComponents() {
        gameBoardPanel = new GameBoardPanel();
        playerInfoPanel = new PlayerInfoPanel();
        cardPanel = new CardPanel();
        controlPanel = new ControlPanel();
        waterLevelPanel = new WaterLevelPanel();
        consolePanel = new ConsolePanel(this);

        // Set preferred sizes of panels
        waterLevelPanel.setPreferredSize(new Dimension(200, 300));
        consolePanel.setPreferredSize(new Dimension(200, 300));
        playerInfoPanel.setPreferredSize(new Dimension(200, 0));
        cardPanel.setPreferredSize(new Dimension(0, 150));
    }

    /**
     * Add all panels to the main window
     */
    private void addComponents() {
        // Create left panel with water level and console panels
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(waterLevelPanel, BorderLayout.NORTH);
        leftPanel.add(consolePanel, BorderLayout.CENTER);

        // Create center panel with game board and card panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Wrap game board for centering
        JPanel gameBoardWrapper = new JPanel(new GridBagLayout());
        gameBoardWrapper.add(gameBoardPanel);
        centerPanel.add(gameBoardWrapper, BorderLayout.CENTER);
        centerPanel.add(cardPanel, BorderLayout.SOUTH);

        // Create right panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(playerInfoPanel, BorderLayout.CENTER);

        // Add all panels to the main frame
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
     * Show game over dialog
     * @param message Reason for game over or victory message
     */
    public void showGameOverDialog(String message) {
        // Disable all control buttons
        controlPanel.setButtonsEnabled(false);

        // Create game over dialog
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Message panel
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add message label
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close Game");
        closeButton.addActionListener(e -> {
            dialog.dispose();
            System.exit(0);
        });
        buttonPanel.add(closeButton);

        // Add panels to dialog
        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Dialog properties
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

    public GameBoardPanel getGameBoardPanel() {
        return gameBoardPanel;
    }

}

