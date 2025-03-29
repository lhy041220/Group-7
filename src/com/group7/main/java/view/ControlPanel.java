package view;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    public ControlPanel() {
        setPreferredSize(new Dimension(1500, 50));
        setBackground(new Color(18 , 45, 66));  // 临时背景色
        setLayout(new FlowLayout(FlowLayout.CENTER));

        addControlButtons();
    }

    /**
     * 添加控制按钮
     */
    private void addControlButtons() {
        add(new JButton("Move"));
        add(new JButton("Shore Up"));
        add(new JButton("Give Card"));
        add(new JButton("Capture Treasure"));
        add(new JButton("End Turn"));
    }
}
