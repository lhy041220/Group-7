package view;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private JButton moveButton;
    private JButton shoreUpButton;
    private JButton giveCardButton;
    private JButton captureTreasureButton;
    private JButton endTurnButton;

    public ControlPanel() {
        setPreferredSize(new Dimension(1500, 50));
        setBackground(new Color(18 , 45, 66));  // 临时背景色
        setLayout(new FlowLayout(FlowLayout.CENTER));

        this.moveButton = new JButton("Move");
        this.shoreUpButton = new JButton("Shore Up");
        this.giveCardButton = new JButton("Give Card");
        this.captureTreasureButton = new JButton("Capture Treasure");
        this.endTurnButton = new JButton("End Turn");

        addControlButtons();
    }

    /**
     * 添加控制按钮
     */
    private void addControlButtons() {
        add(moveButton);
        add(shoreUpButton);
        add(giveCardButton);
        add(captureTreasureButton);
        add(endTurnButton);
    }
}
