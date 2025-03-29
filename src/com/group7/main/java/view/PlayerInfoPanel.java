package view;

import javax.swing.*;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {

    public PlayerInfoPanel() {
        setPreferredSize(new Dimension(300, 600));
        setBackground(Color.WHITE);  // 临时背景色
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addPlayerInfos();
    }

    /**
     * 添加玩家信息组件
     */
    private void addPlayerInfos() {
        // TODO: 为每个玩家添加信息面板
        add(new JLabel("Player 1"));
        add(new JLabel("Player 2"));
        // ... 添加更多玩家
    }
}
