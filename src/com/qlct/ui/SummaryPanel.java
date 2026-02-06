package com.qlct.ui;

import com.qlct.util.CurrencyUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SummaryPanel extends JPanel {
    private final MetricCard incomeCard = new MetricCard("Thu nhập", "Nguồn thu tháng này", "⬆", UITheme.SUCCESS_COLOR);
    private final MetricCard expenseCard = new MetricCard("Chi tiêu", "Tổng chi đã ghi", "⬇", UITheme.DANGER_COLOR);
    private final MetricCard balanceCard = new MetricCard("Số dư", "Tài chính khả dụng", "Σ", UITheme.ACCENT_COLOR);

    public SummaryPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 3, 18, 0));
        add(incomeCard);
        add(expenseCard);
        add(balanceCard);
        update(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public void update(BigDecimal income, BigDecimal expense) {
        BigDecimal safeIncome = income == null ? BigDecimal.ZERO : income;
        BigDecimal safeExpense = expense == null ? BigDecimal.ZERO : expense;
        BigDecimal balance = safeIncome.subtract(safeExpense);

        incomeCard.setValue(CurrencyUtils.format(safeIncome));
        incomeCard.setStatus("Cập nhật tức thời", UITheme.translucent(UITheme.TEXT_PRIMARY, 200));

        expenseCard.setValue(CurrencyUtils.format(safeExpense));
        int spendingPercent = safeIncome.signum() == 0 ? 0
                : safeExpense.multiply(BigDecimal.valueOf(100))
                        .divide(safeIncome.max(BigDecimal.ONE), 0, RoundingMode.DOWN)
                        .intValue();
        expenseCard.setStatus("Đã dùng " + spendingPercent + "% ngân sách", UITheme.translucent(Color.WHITE, 200));

        balanceCard.setValue(CurrencyUtils.format(balance));
        if (balance.signum() < 0) {
            balanceCard.setAccent(UITheme.DANGER_COLOR);
            balanceCard.setStatus("Chi vượt " + CurrencyUtils.format(balance.abs()), UITheme.TEXT_PRIMARY);
            balanceCard.setValueColor(UITheme.TEXT_PRIMARY);
        } else {
            balanceCard.setAccent(UITheme.ACCENT_COLOR);
            balanceCard.setStatus("Khả dụng cho mục tiêu", UITheme.translucent(UITheme.TEXT_PRIMARY, 220));
            balanceCard.setValueColor(UITheme.TEXT_PRIMARY);
        }
    }

    private static final class MetricCard extends JPanel {
        private static final int RADIUS = 26;
        private Color accent;
        private Color gradientStart;
        private Color gradientEnd;
        private final JLabel valueLabel = new JLabel();
        private final JLabel statusLabel = new JLabel();
        private final JLabel glyphLabel;
        private final JLabel titleLabel;
        private final String glyph;

        MetricCard(String title, String subtitle, String glyph, Color accent) {
            this.glyph = glyph;
            this.accent = accent;
            recalcGradient();

            setLayout(new BorderLayout());
            setBorder(UITheme.createCardBorder());
            setOpaque(false);

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setBorder(new EmptyBorder(24, 24, 24, 24));
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

            glyphLabel = new JLabel(UITheme.createGlyphIcon(glyph, 30, UITheme.TEXT_PRIMARY));
            glyphLabel.setAlignmentX(LEFT_ALIGNMENT);

            titleLabel = new JLabel(title.toUpperCase(Locale.ROOT));
            titleLabel.setAlignmentX(LEFT_ALIGNMENT);
            titleLabel.setFont(UITheme.bodyFont(12f));
            titleLabel.setForeground(UITheme.translucent(UITheme.TEXT_PRIMARY, 180));

            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BorderLayout(12, 0));
            header.add(glyphLabel, BorderLayout.WEST);
            header.add(titleLabel, BorderLayout.CENTER);

            valueLabel.setAlignmentX(LEFT_ALIGNMENT);
            valueLabel.setFont(UITheme.headerFont(30f));
            valueLabel.setForeground(UITheme.TEXT_PRIMARY);

            statusLabel.setAlignmentX(LEFT_ALIGNMENT);
            statusLabel.setFont(UITheme.bodyFont(12f));
            statusLabel.setForeground(UITheme.TEXT_SECONDARY);
            statusLabel.setText(subtitle);

            content.add(header);
            content.add(Box.createVerticalStrut(16));
            content.add(valueLabel);
            content.add(Box.createVerticalStrut(12));
            content.add(statusLabel);

            add(content, BorderLayout.CENTER);
        }

        void setValue(String value) {
            valueLabel.setText(value);
        }

        void setStatus(String status, Color color) {
            statusLabel.setText(status);
            statusLabel.setForeground(color);
        }

        void setValueColor(Color color) {
            valueLabel.setForeground(color);
        }

        void setAccent(Color accent) {
            this.accent = accent;
            recalcGradient();
            glyphLabel.setIcon(UITheme.createGlyphIcon(glyph, 30, UITheme.TEXT_PRIMARY));
            repaint();
        }

        private void recalcGradient() {
            gradientStart = UITheme.blend(accent, Color.BLACK, 0.2f);
            gradientEnd = UITheme.blend(accent, Color.BLACK, 0.45f);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            Insets insets = getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = getWidth() - insets.left - insets.right;
            int height = getHeight() - insets.top - insets.bottom;
            GradientPaint paint = new GradientPaint(0, 0, gradientStart, 0, height, gradientEnd);
            g2.setPaint(paint);
            g2.fillRoundRect(x, y, width, height, RADIUS, RADIUS);
            g2.setColor(new Color(255, 255, 255, 35));
            g2.drawRoundRect(x, y, width - 1, height - 1, RADIUS, RADIUS);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}








