package com.qlct.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * Simple circular avatar renderer so any supplied image is displayed as a circle.
 */
public final class CircularAvatarView extends JComponent {
    private static final int BORDER_WIDTH = 2;
    private BufferedImage image;

    public CircularAvatarView(int diameter) {
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
        setOpaque(false);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            Shape circle = new Ellipse2D.Double(x, y, size, size);
            g2.setColor(UITheme.NAV_BACKGROUND);
            g2.fill(circle);
            if (image != null) {
                g2.setClip(circle);
                g2.drawImage(image, x, y, size, size, null);
                g2.setClip(null);
            }
            g2.setStroke(new BasicStroke(BORDER_WIDTH));
            g2.setColor(UITheme.translucent(UITheme.PRIMARY_LIGHT, 160));
            g2.draw(circle);
        } finally {
            g2.dispose();
        }
    }
}
