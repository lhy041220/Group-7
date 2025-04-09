package viewTest;

import view.gamePanel.MainFrame;

import javax.swing.*;

public class MainFrameTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = MainFrame.getInstance();
            frame.setVisible(true);
        });
    }

}
