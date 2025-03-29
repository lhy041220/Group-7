package view;

import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel {
    private JTextArea consoleArea;

    public ConsolePanel() {
        setLayout(new BorderLayout());
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        add(scrollPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(200, 300));
    }

    public void addMessage(String message) {
        consoleArea.append(message + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }
}
