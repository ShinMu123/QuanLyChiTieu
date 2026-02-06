package com.qlct.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.JTextComponent;

public final class UITheme {
    private static final String BASE_FONT_FAMILY = "Segoe UI";
    private static final int BUTTON_RADIUS = 20;
    private static final int CARD_RADIUS = 24;
    private static final Font ICON_FONT = new Font("Segoe UI Symbol", Font.PLAIN, 16);

    public static final Color BACKGROUND_COLOR = new Color(10, 14, 26);
    public static final Color SURFACE_COLOR = new Color(18, 26, 44);
    public static final Color SURFACE_ELEVATED = new Color(24, 33, 56);
    public static final Color SURFACE_TINT = new Color(34, 45, 70);
    public static final Color SHADOW_COLOR = new Color(5, 8, 16, 160);
    public static final Color SURFACE_BORDER_COLOR = new Color(54, 68, 103);

    public static final Color PRIMARY_COLOR = new Color(99, 102, 241);
    public static final Color PRIMARY_DARK = new Color(79, 70, 229);
    public static final Color PRIMARY_LIGHT = new Color(129, 140, 248);
    public static final Color ACCENT_COLOR = new Color(34, 211, 238);
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    public static final Color WARNING_COLOR = new Color(234, 179, 8);
    public static final Color DANGER_COLOR = new Color(239, 68, 68);

    public static final Color TEXT_PRIMARY = new Color(210, 220, 235);
    public static final Color TEXT_SECONDARY = new Color(162, 176, 197);
    public static final Color TEXT_MUTED = new Color(132, 146, 170);
    public static final Color TEXT_DISABLED = new Color(94, 109, 135);

    public static final Color INPUT_BACKGROUND = new Color(25, 36, 60);
    public static final Color INPUT_BORDER = new Color(52, 66, 97);
    public static final Color NAV_BACKGROUND = new Color(15, 21, 35);
    public static final Color NAV_ACTIVE = new Color(36, 46, 72);
    public static final Color NAV_HOVER = translucent(PRIMARY_COLOR, 60);

    public static final Font HEADER_FONT = new Font(BASE_FONT_FAMILY, Font.BOLD, 22);
    public static final Font BODY_FONT = new Font(BASE_FONT_FAMILY, Font.PLAIN, 15);
    public static final Font CAPTION_FONT = new Font(BASE_FONT_FAMILY, Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font(BASE_FONT_FAMILY, Font.BOLD, 15);

    private static final Border CARD_BORDER = new CompoundBorder(new SoftDropShadowBorder(CARD_RADIUS, 18),
            new EmptyBorder(24, 28, 24, 28));

    private UITheme() {
    }

    public static Font bodyFont(float size) {
        return BODY_FONT.deriveFont(size);
    }

    public static Font headerFont(float size) {
        return HEADER_FONT.deriveFont(size);
    }

    public static Border createCardBorder() {
        return CARD_BORDER;
    }

    public static Border createRoundedBorder(Color borderColor, int radius, int padding) {
        Border line = new RoundedLineBorder(borderColor, radius, 1);
        Border gap = new EmptyBorder(padding, padding, padding, padding);
        return new CompoundBorder(line, gap);
    }

    public static void applyCardStyle(JComponent component) {
        component.setOpaque(true);
        component.setBackground(SURFACE_ELEVATED);
        component.setBorder(createCardBorder());
    }

    public static void stylePrimaryButton(AbstractButton button) {
        styleButton(button,
                PRIMARY_COLOR,
                PRIMARY_LIGHT,
                blend(PRIMARY_LIGHT, Color.WHITE, 0.25f),
                blend(PRIMARY_DARK, Color.BLACK, 0.2f),
                TEXT_PRIMARY);
    }

    public static void styleSecondaryButton(AbstractButton button, Color base, Color foreground) {
        Color start = blend(base, Color.WHITE, 0.12f);
        Color end = base;
        Color hoverStart = blend(base, Color.WHITE, 0.25f);
        Color hoverEnd = blend(base, Color.WHITE, 0.05f);
        styleButton(button, start, end, hoverStart, hoverEnd, foreground);
    }

    public static void styleGhostButton(AbstractButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_SECONDARY);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(new CompoundBorder(new RoundedLineBorder(new Color(255, 255, 255, 35), BUTTON_RADIUS, 1),
                new EmptyBorder(10, 18, 10, 18)));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(TEXT_PRIMARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(TEXT_SECONDARY);
            }
        });
    }

    private static void styleButton(AbstractButton button,
            Color baseStart,
            Color baseEnd,
            Color hoverStart,
            Color hoverEnd,
            Color foreground) {
        button.setFont(BUTTON_FONT);
        button.setForeground(foreground);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 22, 12, 22));
        button.setOpaque(false);
        button.setUI(new ModernButtonUI(baseStart, baseEnd, hoverStart, hoverEnd, BUTTON_RADIUS));
    }

    public static void styleSidebarButton(AbstractButton button, Icon icon) {
        button.setFont(BODY_FONT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIcon(icon);
        button.setForeground(TEXT_SECONDARY);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 18, 12, 14));
        button.setOpaque(false);
        button.setUI(new SidebarButtonUI());
        button.putClientProperty("sidebar.active", Boolean.FALSE);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(TEXT_PRIMARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boolean active = Boolean.TRUE.equals(button.getClientProperty("sidebar.active"));
                button.setForeground(active ? TEXT_PRIMARY : TEXT_SECONDARY);
            }
        });
    }

    public static void setSidebarButtonActive(AbstractButton button, boolean active) {
        button.putClientProperty("sidebar.active", active);
        button.setForeground(active ? TEXT_PRIMARY : TEXT_SECONDARY);
        button.repaint();
    }

    public static void styleInput(JComponent component) {
        component.setFont(BODY_FONT);
        component.setOpaque(true);
        component.setBackground(INPUT_BACKGROUND);
        component.setForeground(TEXT_PRIMARY);
        component.setBorder(createInputBorder(null));

        if (component instanceof JTextComponent textComponent) {
            textComponent.setCaretColor(PRIMARY_LIGHT);
            textComponent.setSelectionColor(translucent(PRIMARY_LIGHT, 120));
        }
    }

    public static Border createInputBorder(Color outlineColor) {
        Color borderColor = outlineColor != null ? outlineColor : INPUT_BORDER;
        return new CompoundBorder(new RoundedLineBorder(borderColor, 18, 1),
                new EmptyBorder(12, 16, 12, 16));
    }

    public static Icon createGlyphIcon(String glyph, int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font iconFont = ICON_FONT.deriveFont((float) size * 0.75f);
            g2.setFont(iconFont);
            FontMetrics metrics = g2.getFontMetrics();
            int textWidth = metrics.stringWidth(glyph);
            int textHeight = metrics.getAscent();
            int x = Math.max(0, (size - textWidth) / 2);
            int y = Math.max(textHeight, (size + textHeight) / 2 - 2);
            g2.setColor(color);
            g2.drawString(glyph, x, y);
        } finally {
            g2.dispose();
        }
        return new ImageIcon(image);
    }

    public static void installScrollPaneStyle(JScrollPane scrollPane) {
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(SURFACE_ELEVATED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        scrollPane.getVerticalScrollBar().setUI(new MinimalScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new MinimalScrollBarUI());
    }

    public static Color translucent(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color blend(Color base, Color overlay, float ratio) {
        float clamped = Math.max(0f, Math.min(1f, ratio));
        int r = Math.round(base.getRed() * (1 - clamped) + overlay.getRed() * clamped);
        int g = Math.round(base.getGreen() * (1 - clamped) + overlay.getGreen() * clamped);
        int b = Math.round(base.getBlue() * (1 - clamped) + overlay.getBlue() * clamped);
        return new Color(r, g, b);
    }

    private static class ModernButtonUI extends BasicButtonUI {
        protected final Color baseStart;
        protected final Color baseEnd;
        protected final Color hoverStart;
        protected final Color hoverEnd;
        private final int arc;
        private boolean hovering;
        private boolean pressing;

        ModernButtonUI(Color baseStart, Color baseEnd, Color hoverStart, Color hoverEnd, int arc) {
            this.baseStart = baseStart;
            this.baseEnd = baseEnd;
            this.hoverStart = hoverStart;
            this.hoverEnd = hoverEnd;
            this.arc = arc;
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.addMouseListener(listener);
        }

        @Override
        public void uninstallUI(JComponent c) {
            c.removeMouseListener(listener);
            super.uninstallUI(c);
        }

        private final MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                e.getComponent().repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                pressing = false;
                e.getComponent().repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressing = true;
                e.getComponent().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressing = false;
                e.getComponent().repaint();
            }
        };

        protected Color getBaseStart(AbstractButton button) {
            return baseStart;
        }

        protected Color getBaseEnd(AbstractButton button) {
            return baseEnd;
        }

        protected Color getHoverStart(AbstractButton button) {
            return hoverStart;
        }

        protected Color getHoverEnd(AbstractButton button) {
            return hoverEnd;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = c.getWidth();
                int height = c.getHeight();
                Color start = hovering ? getHoverStart(button) : getBaseStart(button);
                Color end = hovering ? getHoverEnd(button) : getBaseEnd(button);
                if (pressing) {
                    start = blend(start, Color.BLACK, 0.25f);
                    end = blend(end, Color.BLACK, 0.35f);
                }
                g2.setPaint(new GradientPaint(0, 0, start, 0, height, end));
                g2.fillRoundRect(0, 0, width, height, arc, arc);
                g2.setColor(new Color(255, 255, 255, hovering ? 40 : 28));
                g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);
            } finally {
                g2.dispose();
            }
            super.paint(g, c);
        }
    }

    private static final class SidebarButtonUI extends ModernButtonUI {
        SidebarButtonUI() {
            super(new Color(255, 255, 255, 0), new Color(255, 255, 255, 0),
                    translucent(PRIMARY_COLOR, 45), translucent(PRIMARY_DARK, 35), BUTTON_RADIUS);
        }

        @Override
        protected Color getBaseStart(AbstractButton button) {
            if (Boolean.TRUE.equals(button.getClientProperty("sidebar.active"))) {
                return translucent(PRIMARY_COLOR, 70);
            }
            return super.getBaseStart(button);
        }

        @Override
        protected Color getBaseEnd(AbstractButton button) {
            if (Boolean.TRUE.equals(button.getClientProperty("sidebar.active"))) {
                return translucent(PRIMARY_DARK, 55);
            }
            return super.getBaseEnd(button);
        }

        @Override
        protected Color getHoverStart(AbstractButton button) {
            if (Boolean.TRUE.equals(button.getClientProperty("sidebar.active"))) {
                return blend(translucent(PRIMARY_COLOR, 90), Color.WHITE, 0.08f);
            }
            return super.getHoverStart(button);
        }

        @Override
        protected Color getHoverEnd(AbstractButton button) {
            if (Boolean.TRUE.equals(button.getClientProperty("sidebar.active"))) {
                return blend(translucent(PRIMARY_DARK, 80), Color.WHITE, 0.08f);
            }
            return super.getHoverEnd(button);
        }
    }

    private static final class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                RoundRectangle2D rect = new RoundRectangle2D.Float(x + 0.5f, y + 0.5f,
                        width - 1f, height - 1f, radius, radius);
                g2.setStroke(new java.awt.BasicStroke(thickness));
                g2.draw(rect);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int pad = Math.max(thickness, radius / 6);
            return new Insets(pad, pad, pad, pad);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            Insets computed = getBorderInsets(c);
            insets.top = computed.top;
            insets.left = computed.left;
            insets.bottom = computed.bottom;
            insets.right = computed.right;
            return insets;
        }
    }

    private static final class SoftDropShadowBorder extends AbstractBorder {
        private final int arc;
        private final int shadowSize;

        SoftDropShadowBorder(int arc, int shadowSize) {
            this.arc = arc;
            this.shadowSize = shadowSize;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowPadding = shadowSize / 2;
                for (int i = shadowSize; i > 0; i--) {
                    float alpha = (float) i / shadowSize;
                    Color color = new Color(SHADOW_COLOR.getRed(), SHADOW_COLOR.getGreen(), SHADOW_COLOR.getBlue(),
                            Math.min(160, Math.round(SHADOW_COLOR.getAlpha() * alpha)));
                    g2.setColor(color);
                    g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), arc, arc);
                }
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(x + shadowPadding, y + shadowPadding,
                        width - 1 - (shadowPadding * 2), height - 1 - (shadowPadding * 2), arc, arc);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int inset = shadowSize / 2 + 2;
            return new Insets(inset, inset, inset + 4, inset);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            Insets computed = getBorderInsets(c);
            insets.top = computed.top;
            insets.left = computed.left;
            insets.bottom = computed.bottom;
            insets.right = computed.right;
            return insets;
        }
    }

    private static final class MinimalScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = translucent(PRIMARY_COLOR, 120);
            trackColor = translucent(Color.BLACK, 20);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, java.awt.Rectangle bounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
            } finally {
                g2.dispose();
            }
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, java.awt.Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setColor(trackColor);
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
            } finally {
                g2.dispose();
            }
        }

        @Override
        protected javax.swing.JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected javax.swing.JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private javax.swing.JButton zeroButton() {
            javax.swing.JButton button = new javax.swing.JButton();
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
    }
}








