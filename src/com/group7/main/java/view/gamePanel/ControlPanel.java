package view.gamePanel;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/***
 * 玩家行动面板
 */
@Getter
public class ControlPanel extends JPanel {

    private JButton moveButton;
    private JButton shoreUpButton;
    private JButton giveCardButton;
    private JButton captureTreasureButton;
    private JButton collectTreasureButton;
    private JButton endTurnButton;

    public ControlPanel() {
        setPreferredSize(new Dimension(1500, 50));
        setBackground(new Color(18 , 45, 66));  // 临时背景色
        setLayout(new FlowLayout(FlowLayout.CENTER));

        this.moveButton = new JButton("Move");
        this.shoreUpButton = new JButton("Shore Up");
        this.giveCardButton = new JButton("Give Card");
        this.captureTreasureButton = new JButton("Capture Treasure");
        this.collectTreasureButton = new JButton("Collect Treasure");
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
        add(collectTreasureButton);
        add(endTurnButton);
    }
}
