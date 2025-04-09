package view.gamePanel;

import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel {
    private JTextArea consoleArea;

    public ConsolePanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        add(scrollPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(200, 300));

        observeAllEvents(mainFrame);
    }

    public void observeAllEvents(MainFrame mainFrame) {
        mainFrame.onMoveButtonClick.addListener(sender -> {
            addMessage("Player: moved action.");
        });
        mainFrame.onShoreUpButtonClick.addListener(sender -> {
            addMessage("Player: shore up action.");
        });
        mainFrame.onGiveCardButtonClick.addListener(sender -> {
            addMessage("Player: give card action.");
        });
        mainFrame.onCaptureTreasureButtonClick.addListener(sender -> {
            addMessage("Player: capture treasure action.");
        });
        mainFrame.onEndTurnButtonClick.addListener(sender -> {
            addMessage("Player: end turn action.");
        });
    }


    public void addMessage(String message) {
        consoleArea.append(message + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }
}
