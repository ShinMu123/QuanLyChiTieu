package com.qlct.ui;

import com.qlct.model.Transaction;
import com.qlct.util.CurrencyUtils;
import com.qlct.util.DateUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TransactionTablePanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private List<Transaction> transactions = Collections.emptyList();
    private int hoveredRow = -1;

    public TransactionTablePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        model = new DefaultTableModel(new Object[] {"Ngày", "Loại", "Danh mục", "Số tiền", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(44);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 48));
        table.getTableHeader().setFont(UITheme.bodyFont(13f).deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(UITheme.SURFACE_TINT);
        table.getTableHeader().setForeground(UITheme.TEXT_PRIMARY);
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        table.setFont(UITheme.bodyFont(14f));
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setShowGrid(false);
        table.setBackground(UITheme.SURFACE_ELEVATED);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowMargin(0);

        StripeRenderer stripeRenderer = new StripeRenderer();
        table.setDefaultRenderer(Object.class, stripeRenderer);

        DefaultTableCellRenderer amountRenderer = new StripeRenderer();
        amountRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(amountRenderer);

        table.getColumnModel().getColumn(1).setCellRenderer(new TypeTagRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                hoveredRow = table.rowAtPoint(point);
                table.repaint();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                table.repaint();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.SURFACE_ELEVATED);
        UITheme.installScrollPaneStyle(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions == null ? Collections.emptyList() : List.copyOf(transactions);
        model.setRowCount(0);
        for (Transaction transaction : this.transactions) {
            model.addRow(new Object[] {
                    DateUtils.format(transaction.getTransactionDate()),
                    transaction.getCategory() != null ? transaction.getCategory().getType() : "",
                    transaction.getCategory() != null ? transaction.getCategory().getName() : "",
                    CurrencyUtils.format(transaction.getAmount()),
                    transaction.getNote()
            });
        }
    }

    public Transaction getSelectedTransaction() {
        int index = table.getSelectedRow();
        if (index < 0 || index >= transactions.size()) {
            return null;
        }
        return transactions.get(index);
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public void clear() {
        setTransactions(Collections.emptyList());
    }

    private Color resolveRowBackground(int row, boolean isSelected) {
        if (isSelected) {
            return UITheme.translucent(UITheme.PRIMARY_COLOR, 150);
        }
        if (row == hoveredRow) {
            return UITheme.translucent(UITheme.PRIMARY_COLOR, 70);
        }
        return row % 2 == 0
                ? UITheme.blend(UITheme.SURFACE_ELEVATED, Color.BLACK, 0.12f)
                : UITheme.blend(UITheme.SURFACE_ELEVATED, Color.BLACK, 0.22f);
    }

    private class StripeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            label.setForeground(UITheme.TEXT_PRIMARY);
            label.setBackground(resolveRowBackground(row, isSelected));
            label.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
            return label;
        }
    }

    private final class TypeTagRenderer extends StripeRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(UITheme.bodyFont(13f));
            if (isSelected) {
                label.setForeground(UITheme.TEXT_PRIMARY);
            } else if ("Thu".equalsIgnoreCase(String.valueOf(value))) {
                label.setBackground(UITheme.translucent(UITheme.SUCCESS_COLOR, 45));
                label.setForeground(UITheme.SUCCESS_COLOR.brighter());
            } else {
                label.setBackground(UITheme.translucent(UITheme.DANGER_COLOR, 45));
                label.setForeground(UITheme.DANGER_COLOR.brighter());
            }
            return label;
        }
    }

    private static final class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setText(value == null ? "" : value.toString().toUpperCase(Locale.ROOT));
            label.setForeground(UITheme.TEXT_PRIMARY);
            label.setBackground(UITheme.SURFACE_TINT);
            label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            return label;
        }
    }
}








