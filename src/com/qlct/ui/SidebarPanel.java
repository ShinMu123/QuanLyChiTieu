package com.qlct.ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.time.Month;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

public class SidebarPanel extends JPanel {
    private static final String GLYPH_OVERVIEW = "🏠";
    private static final String GLYPH_TRANSACTIONS = "📊";
    private static final String GLYPH_CATEGORIES = "🗂";
    private final JButton overviewButton;
    private final JButton transactionsButton;
    private final JButton categoriesButton;
    private final JComboBox<String> monthCombo;
    private final JSpinner yearSpinner;
    private final JButton applyFilterButton = new JButton("Áp dụng bộ lọc");
    private final JButton addButton = new JButton("Thêm giao dịch");
    private final JButton deleteButton = new JButton("Xóa giao dịch");
    private final JButton reloadButton = new JButton("Tải lại dữ liệu");

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 0));
        setBackground(UITheme.NAV_BACKGROUND);
        setBorder(new EmptyBorder(28, 24, 28, 20));

        monthCombo = new JComboBox<>(buildMonthModel());
        yearSpinner = new JSpinner(new SpinnerNumberModel(java.time.LocalDate.now().getYear(), 2000, 2100, 1));

        overviewButton = createNavButton("Tổng quan", GLYPH_OVERVIEW);
        transactionsButton = createNavButton("Giao dịch", GLYPH_TRANSACTIONS);
        categoriesButton = createNavButton("Danh mục", GLYPH_CATEGORIES);

        overviewButton.addActionListener(e -> setActiveNav(overviewButton));
        transactionsButton.addActionListener(e -> setActiveNav(transactionsButton));
        categoriesButton.addActionListener(e -> setActiveNav(categoriesButton));

        setActiveNav(overviewButton);

        add(buildBrandSection());
        add(Box.createVerticalStrut(24));
        add(buildNavigationSection());
        add(Box.createVerticalStrut(20));
        add(buildFilterCard());
        add(Box.createVerticalStrut(18));
        add(buildActionCard());
        add(Box.createVerticalGlue());
    }

    public void setMonthYear(int month, int year) {
        monthCombo.setSelectedIndex(Math.max(0, Math.min(11, month - 1)));
        yearSpinner.setValue(year);
    }

    public int getSelectedMonth() {
        return monthCombo.getSelectedIndex() + 1;
    }

    public int getSelectedYear() {
        return ((Number) yearSpinner.getValue()).intValue();
    }

    public void onApplyFilter(ActionListener listener) {
        applyFilterButton.addActionListener(listener);
    }

    public void onAddTransaction(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void onDeleteTransaction(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void onReload(ActionListener listener) {
        reloadButton.addActionListener(listener);
    }

    private JPanel buildBrandSection() {
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("QLCT.");
        logo.setAlignmentX(LEFT_ALIGNMENT);
        logo.setFont(UITheme.headerFont(30f));
        logo.setForeground(UITheme.TEXT_PRIMARY);

        JLabel tagline = new JLabel("Personal Finance Studio");
        tagline.setAlignmentX(LEFT_ALIGNMENT);
        tagline.setFont(UITheme.bodyFont(12f));
        tagline.setForeground(UITheme.TEXT_SECONDARY);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(6));
        brand.add(tagline);
        return brand;
    }

    private JPanel buildNavigationSection() {
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        nav.add(overviewButton);
        nav.add(Box.createVerticalStrut(8));
        nav.add(transactionsButton);
        nav.add(Box.createVerticalStrut(8));
        nav.add(categoriesButton);

        return nav;
    }

    private JPanel buildFilterCard() {
        JPanel container = new JPanel();
        UITheme.applyCardStyle(container);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Bộ lọc thời gian");
        title.setAlignmentX(LEFT_ALIGNMENT);
        title.setFont(UITheme.headerFont(18f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel hint = new JLabel("Tùy chỉnh phạm vi dữ liệu");
        hint.setAlignmentX(LEFT_ALIGNMENT);
        hint.setFont(UITheme.bodyFont(12f));
        hint.setForeground(UITheme.TEXT_SECONDARY);

        UITheme.styleInput(monthCombo);
        monthCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        monthCombo.setAlignmentX(LEFT_ALIGNMENT);

        configureSpinner(yearSpinner);
        yearSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        yearSpinner.setAlignmentX(LEFT_ALIGNMENT);

        Icon lightning = UITheme.createGlyphIcon("⚡", 18, UITheme.TEXT_PRIMARY);
        applyFilterButton.setIcon(lightning);
        applyFilterButton.setIconTextGap(10);
        UITheme.stylePrimaryButton(applyFilterButton);
        applyFilterButton.setAlignmentX(LEFT_ALIGNMENT);
        applyFilterButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        container.add(title);
        container.add(Box.createVerticalStrut(4));
        container.add(hint);
        container.add(Box.createVerticalStrut(18));
        container.add(monthCombo);
        container.add(Box.createVerticalStrut(14));
        container.add(yearSpinner);
        container.add(Box.createVerticalStrut(18));
        container.add(applyFilterButton);

        return container;
    }

    private String[] buildMonthModel() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Month.of(i + 1).getDisplayName(java.time.format.TextStyle.FULL, new Locale("vi"));
        }
        return months;
    }

    private void configureSpinner(JSpinner spinner) {
        UITheme.styleInput(spinner);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0000");
        spinner.setEditor(editor);
        editor.getTextField().setHorizontalAlignment(JTextField.LEFT);
    }

    private JPanel buildActionCard() {
        JPanel container = new JPanel();
        UITheme.applyCardStyle(container);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Tác vụ nhanh");
        title.setAlignmentX(LEFT_ALIGNMENT);
        title.setFont(UITheme.headerFont(18f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        Icon addIcon = UITheme.createGlyphIcon("＋", 18, UITheme.TEXT_PRIMARY);
        Icon deleteIcon = UITheme.createGlyphIcon("✖", 18, UITheme.TEXT_PRIMARY);
        Icon reloadIcon = UITheme.createGlyphIcon("⟳", 18, UITheme.TEXT_SECONDARY);

        addButton.setIcon(addIcon);
        addButton.setIconTextGap(10);
        UITheme.styleSecondaryButton(addButton, UITheme.PRIMARY_COLOR, UITheme.TEXT_PRIMARY);
        addButton.setAlignmentX(LEFT_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        deleteButton.setIcon(deleteIcon);
        deleteButton.setIconTextGap(10);
        UITheme.styleSecondaryButton(deleteButton, UITheme.DANGER_COLOR, UITheme.TEXT_PRIMARY);
        deleteButton.setAlignmentX(LEFT_ALIGNMENT);
        deleteButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        reloadButton.setIcon(reloadIcon);
        reloadButton.setIconTextGap(8);
        UITheme.styleGhostButton(reloadButton);
        reloadButton.setAlignmentX(LEFT_ALIGNMENT);
        reloadButton.setForeground(UITheme.TEXT_SECONDARY);
        reloadButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        container.add(title);
        container.add(Box.createVerticalStrut(18));
        container.add(addButton);
        container.add(Box.createVerticalStrut(12));
        container.add(deleteButton);
        container.add(Box.createVerticalStrut(12));
        container.add(reloadButton);

        return container;
    }

    private JButton createNavButton(String text, String glyph) {
        JButton button = new JButton(text);
        Icon icon = UITheme.createGlyphIcon(glyph, 20, UITheme.TEXT_SECONDARY);
        UITheme.styleSidebarButton(button, icon);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setIconTextGap(14);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        return button;
    }

    private void setActiveNav(JButton active) {
        UITheme.setSidebarButtonActive(overviewButton, active == overviewButton);
        UITheme.setSidebarButtonActive(transactionsButton, active == transactionsButton);
        UITheme.setSidebarButtonActive(categoriesButton, active == categoriesButton);

        overviewButton.setIcon(UITheme.createGlyphIcon(GLYPH_OVERVIEW, 20,
                active == overviewButton ? UITheme.TEXT_PRIMARY : UITheme.TEXT_SECONDARY));
        transactionsButton.setIcon(UITheme.createGlyphIcon(GLYPH_TRANSACTIONS, 20,
                active == transactionsButton ? UITheme.TEXT_PRIMARY : UITheme.TEXT_SECONDARY));
        categoriesButton.setIcon(UITheme.createGlyphIcon(GLYPH_CATEGORIES, 20,
                active == categoriesButton ? UITheme.TEXT_PRIMARY : UITheme.TEXT_SECONDARY));
    }
}








