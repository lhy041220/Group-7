package view.gamePanel;

import javax.swing.*;
import java.awt.*;

public class WaterLevelPanel extends JPanel {
    private int waterLevel;
    private final int MAX_WATER_LEVEL = 10;
    private final Color[] LEVEL_COLORS = {
            new Color(173, 216, 230), // 浅蓝色
            new Color(135, 206, 235), // 天蓝色
            new Color(0, 191, 255),   // 深天蓝
            new Color(30, 144, 255),  // 道奇蓝
            new Color(0, 0, 255)      // 蓝色
    };

    public WaterLevelPanel() {
        setPreferredSize(new Dimension(100, 400));
        waterLevel = 0;
    }

    public void setWaterLevel(int level) {
        waterLevel = Math.min(Math.max(level, 0), MAX_WATER_LEVEL);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int height = getHeight();
        int width = getWidth();

        // 绘制背景
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);

        // 绘制水位刻度
        int tokenHeight = height / (MAX_WATER_LEVEL + 1);
        for (int i = MAX_WATER_LEVEL; i >= 0; i--) {
            int y = height - (i + 1) * tokenHeight;
            if (i < waterLevel) {
                g2d.setColor(LEVEL_COLORS[Math.min(i / 2, LEVEL_COLORS.length - 1)]);
                g2d.fillRect(10, y, width - 20, tokenHeight);
            }
            g2d.setColor(Color.BLACK);
            g2d.drawRect(10, y, width - 20, tokenHeight);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(String.valueOf(i), width / 2 - 5, y + tokenHeight / 2 + 5);
        }

        // 绘制水位指示器
        int indicatorY = height - (waterLevel + 1) * tokenHeight - 10;
        g2d.setColor(Color.RED);
        int[] xPoints = {0, 10, width - 10, width};
        int[] yPoints = {indicatorY, indicatorY - 10, indicatorY - 10, indicatorY};
        g2d.fillPolygon(xPoints, yPoints, 4);
    }
}