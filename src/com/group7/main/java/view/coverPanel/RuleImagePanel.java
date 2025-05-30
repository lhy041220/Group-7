package view.coverPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom panel class for displaying rule images
 */
public class RuleImagePanel extends JPanel {
    private static final String RULES_IMAGE_PATH = "src/com/group7/resources/images/rules/";
    private final List<BufferedImage> ruleImages;
    private int currentImageIndex;
    
    public RuleImagePanel() {
        ruleImages = new ArrayList<>();
        currentImageIndex = 0;
        loadRuleImages();
    }
    
    private void loadRuleImages() {
        File rulesDir = new File(RULES_IMAGE_PATH);
        if (!rulesDir.exists() || !rulesDir.isDirectory()) {
            showError("Rules directory not found");
            return;
        }
        
        File[] imageFiles = rulesDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".png") || 
            name.toLowerCase().endsWith(".jpg"));
            
        if (imageFiles == null || imageFiles.length == 0) {
            showError("No rule images found");
            return;
        }
        
        for (File file : imageFiles) {
            try {
                BufferedImage image = ImageIO.read(file);
                ruleImages.add(image);
            } catch (IOException e) {
                showError("Error loading rule image: " + file.getName());
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Resource Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public void nextImage() {
        if (!ruleImages.isEmpty() && currentImageIndex < ruleImages.size() - 1) {
            currentImageIndex++;
            repaint();
        }
    }
    
    public void previousImage() {
        if (!ruleImages.isEmpty() && currentImageIndex > 0) {
            currentImageIndex--;
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!ruleImages.isEmpty()) {
            BufferedImage currentImage = ruleImages.get(currentImageIndex);
            if (currentImage != null) {
                // Calculate dimensions to maintain aspect ratio
                Dimension scaledDim = getScaledDimension(
                    new Dimension(currentImage.getWidth(), currentImage.getHeight()),
                    new Dimension(getWidth(), getHeight())
                );
                
                int x = (getWidth() - scaledDim.width) / 2;
                int y = (getHeight() - scaledDim.height) / 2;
                
                g.drawImage(currentImage, x, y, scaledDim.width, scaledDim.height, this);
            }
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String message = "No rule images available";
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(message);
            g.drawString(message, (getWidth() - messageWidth) / 2, getHeight() / 2);
        }
    }
    
    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        double widthRatio = boundary.getWidth() / imgSize.getWidth();
        double heightRatio = boundary.getHeight() / imgSize.getHeight();
        double ratio = Math.min(widthRatio, heightRatio);
        
        return new Dimension(
            (int) (imgSize.width * ratio),
            (int) (imgSize.height * ratio)
        );
    }
}

