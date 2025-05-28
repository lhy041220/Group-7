package view.gamePanel;

import javax.swing.*;
import java.awt.*;

public class WaterLevelPanel extends JPanel {
    private int waterLevel;
    private final int MAX_WATER_LEVEL = 10;
    private final Color[] LEVEL_COLORS = {
        new Color(173, 216, 230), // 浅蓝色 - 安全
        new Color(135, 206, 235), // 天蓝色 - 安全
        new Color(100, 149, 237), // 矢车菊蓝 - 警告
        new Color(65, 105, 225),  // 品蓝 - 警告
        new Color(0, 0, 205)      // 深蓝 - 危险
    };

    private final String[] LEVEL_DESCRIPTIONS = {
            "安全", "安全", "警告", "警告", "危险"
    };

    public WaterLevelPanel() {
        setPreferredSize(new Dimension(200, 400));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        waterLevel = 0;
    }

    public void setWaterLevel(int level) {
        waterLevel = Math.min(Math.max(level, 0), MAX_WATER_LEVEL);
        repaint();
    }

    private Color getLevelColor(int level) {
        // 将0-10的水位映射到0-4的颜色索引
        int colorIndex = Math.min(level / 2, LEVEL_COLORS.length - 1);
        return LEVEL_COLORS[colorIndex];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 绘制标题
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        String title = "水位指示器";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (width - titleWidth) / 2, 25);

        // 计算水位计的尺寸和位置
        int meterTop = 40;
        int meterBottom = height - 20;
        int meterHeight = meterBottom - meterTop;
        int tokenHeight = meterHeight / (MAX_WATER_LEVEL + 1);
        int meterLeft = 30;
        int meterRight = width - 30;
        int meterWidth = meterRight - meterLeft;

        // 绘制水位计背景
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);

        // 绘制水位刻度
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        for (int i = MAX_WATER_LEVEL; i >= 0; i--) {
            int y = meterTop + (MAX_WATER_LEVEL - i) * tokenHeight;

            // 绘制水位颜色块
            if (i <= waterLevel) {
                g2d.setColor(getLevelColor(i));
                g2d.fillRect(meterLeft, y, meterWidth, tokenHeight);
            }

            // 绘制刻度线和数字
            g2d.setColor(Color.BLACK);
            g2d.drawRect(meterLeft, y, meterWidth, tokenHeight);

            // 绘制水位数字
            String levelText = String.valueOf(i);
            int textWidth = fm.stringWidth(levelText);
            g2d.drawString(levelText, meterLeft - textWidth - 5, y + tokenHeight / 2 + 5);

            // 在右侧添加描述文本
            if (i % 2 == 0) {
                String description = LEVEL_DESCRIPTIONS[Math.min(i / 2, LEVEL_DESCRIPTIONS.length - 1)];
                g2d.drawString(description, meterRight + 5, y + tokenHeight / 2 + 5);
            }
        }

        // 绘制当前水位指示器
        if (waterLevel >= 0) {
            int indicatorY = meterTop + (MAX_WATER_LEVEL - waterLevel) * tokenHeight;
            g2d.setColor(Color.RED);
            int[] xPoints = {meterLeft - 10, meterLeft, meterLeft};
            int[] yPoints = {indicatorY + tokenHeight / 2, indicatorY + tokenHeight / 2 - 5, indicatorY + tokenHeight / 2 + 5};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }

        // 绘制底部说明
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        g2d.setColor(Color.DARK_GRAY);
        String info = "每回合抽取洪水卡数：" + (waterLevel >= 0 ? (waterLevel / 2 + 2) : 2);
        int infoWidth = fm.stringWidth(info);
        g2d.drawString(info, (width - infoWidth) / 2, height - 5);
    }
}