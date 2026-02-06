package com.qlct.ui;

import com.qlct.dao.CategoryDAO;
import com.qlct.dao.TransactionDAO;
import com.qlct.model.Category;
import com.qlct.model.Summary;
import com.qlct.model.Transaction;
import com.qlct.model.User;
import com.qlct.service.ProfileUpdateService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainForm extends JFrame {
    private static final DateTimeFormatter HEADER_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", new Locale("vi", "VN"));
    private final User user;
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProfileUpdateService profileService = new ProfileUpdateService(Path.of("assets", "avatars"));

    private final SidebarPanel sidebarPanel = new SidebarPanel();
    private final SummaryPanel summaryPanel = new SummaryPanel();
    private final TransactionTablePanel tablePanel = new TransactionTablePanel();

    private final JLabel greetingLabel = new JLabel();
    private final JLabel periodLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final JLabel avatarLabel = new JLabel();
    private final JLabel userNameLabel = new JLabel();
    private final JLabel userMetaLabel = new JLabel();

    private int currentMonth;
    private int currentYear;

    public MainForm(User user) {
        this.user = user;
        LocalDate today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentYear = today.getYear();

        setTitle("Quản lý chi tiêu - " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        buildUI();
        bindEvents();
        loadUserInfo();
        applyFilter(currentMonth, currentYear);
    }

    private void buildUI() {
        GradientBackgroundPanel root = new GradientBackgroundPanel();
        root.setLayout(new BorderLayout(28, 28));
        root.setBorder(new EmptyBorder(28, 28, 28, 28));

        root.add(sidebarPanel, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(24, 24));
        content.setOpaque(false);

        content.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerStack = new JPanel(new BorderLayout(24, 24));
        centerStack.setOpaque(false);

        JPanel summaryContainer = new JPanel(new BorderLayout());
        summaryContainer.setOpaque(false);
        summaryContainer.add(summaryPanel, BorderLayout.CENTER);
        centerStack.add(summaryContainer, BorderLayout.NORTH);

        centerStack.add(buildTableContainer(), BorderLayout.CENTER);

        content.add(centerStack, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);

        sidebarPanel.setMonthYear(currentMonth, currentYear);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(24, 0));
        UITheme.applyCardStyle(header);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        greetingLabel.setForeground(UITheme.TEXT_PRIMARY);
        greetingLabel.setFont(UITheme.headerFont(30f));

        styleHeaderChip(periodLabel, UITheme.translucent(UITheme.PRIMARY_COLOR, 60), UITheme.TEXT_PRIMARY);
        periodLabel.setIcon(UITheme.createGlyphIcon("📆", 20, UITheme.TEXT_PRIMARY));
        periodLabel.setIconTextGap(10);

        styleHeaderChip(dateLabel, UITheme.translucent(UITheme.SURFACE_TINT, 120), UITheme.TEXT_SECONDARY);
        dateLabel.setIcon(UITheme.createGlyphIcon("🕒", 18, UITheme.TEXT_SECONDARY));
        dateLabel.setIconTextGap(10);

        JPanel chipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        chipRow.setOpaque(false);
        chipRow.add(periodLabel);
        chipRow.add(dateLabel);

        left.add(greetingLabel);
        left.add(Box.createVerticalStrut(12));
        left.add(chipRow);

        header.add(left, BorderLayout.CENTER);

        JButton createTransactionButton = new JButton("Giao dịch mới");
        createTransactionButton.setIcon(UITheme.createGlyphIcon("＋", 18, UITheme.TEXT_PRIMARY));
        createTransactionButton.setIconTextGap(10);
        UITheme.styleSecondaryButton(createTransactionButton, UITheme.ACCENT_COLOR, UITheme.TEXT_PRIMARY);
        createTransactionButton.addActionListener(e -> openTransactionForm());

        JButton profileButton = new JButton(UITheme.createGlyphIcon("⚙", 18, UITheme.TEXT_SECONDARY));
        UITheme.styleGhostButton(profileButton);
        profileButton.setText("Cấu hình");
        profileButton.setIconTextGap(8);
        profileButton.addActionListener(e -> openProfileDialog());

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setIcon(UITheme.createGlyphIcon("⏻", 16, UITheme.TEXT_SECONDARY));
        logoutButton.setIconTextGap(8);
        UITheme.styleGhostButton(logoutButton);
        logoutButton.setForeground(UITheme.TEXT_SECONDARY);
        logoutButton.addActionListener(e -> logout());

        avatarLabel.setPreferredSize(new Dimension(72, 72));
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(UITheme.SURFACE_TINT);
        avatarLabel.setBorder(BorderFactory.createLineBorder(UITheme.translucent(UITheme.PRIMARY_LIGHT, 160), 2, true));
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);
        avatarLabel.setVerticalAlignment(JLabel.CENTER);

        JPanel profileInfo = new JPanel();
        profileInfo.setOpaque(false);
        profileInfo.setLayout(new BoxLayout(profileInfo, BoxLayout.Y_AXIS));

        userNameLabel.setFont(UITheme.headerFont(18f));
        userNameLabel.setForeground(UITheme.TEXT_PRIMARY);
        userMetaLabel.setFont(UITheme.bodyFont(13f));
        userMetaLabel.setForeground(UITheme.TEXT_SECONDARY);

        profileInfo.add(userNameLabel);
        profileInfo.add(Box.createVerticalStrut(4));
        profileInfo.add(userMetaLabel);

        JPanel profileGroup = new JPanel();
        profileGroup.setOpaque(false);
        profileGroup.setLayout(new BoxLayout(profileGroup, BoxLayout.X_AXIS));
        profileGroup.add(profileInfo);
        profileGroup.add(Box.createHorizontalStrut(18));
        profileGroup.add(avatarLabel);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
        right.add(createTransactionButton);
        right.add(Box.createHorizontalStrut(18));
        right.add(profileGroup);
        right.add(Box.createHorizontalStrut(18));
        right.add(profileButton);
        right.add(Box.createHorizontalStrut(12));
        right.add(logoutButton);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTableContainer() {
        JPanel container = new JPanel(new BorderLayout(0, 18));
        UITheme.applyCardStyle(container);

        JPanel headerBar = new JPanel(new BorderLayout(12, 0));
        headerBar.setOpaque(false);

        JLabel historyLabel = new JLabel("Lịch sử giao dịch");
        historyLabel.setFont(UITheme.headerFont(22f));
        historyLabel.setForeground(UITheme.TEXT_PRIMARY);
        headerBar.add(historyLabel, BorderLayout.WEST);

        JButton refreshButton = new JButton("Tải lại");
        refreshButton.setIcon(UITheme.createGlyphIcon("⟳", 16, UITheme.TEXT_SECONDARY));
        refreshButton.setIconTextGap(6);
        UITheme.styleGhostButton(refreshButton);
        refreshButton.setForeground(UITheme.TEXT_SECONDARY);
        refreshButton.addActionListener(e -> applyFilter(currentMonth, currentYear));
        headerBar.add(refreshButton, BorderLayout.EAST);

        container.add(headerBar, BorderLayout.NORTH);
        container.add(tablePanel, BorderLayout.CENTER);
        return container;
    }

    private void bindEvents() {
        sidebarPanel.onApplyFilter(e -> applyFilter(sidebarPanel.getSelectedMonth(), sidebarPanel.getSelectedYear()));
        sidebarPanel.onReload(e -> applyFilter(currentMonth, currentYear));
        sidebarPanel.onAddTransaction(e -> openTransactionForm());
        sidebarPanel.onDeleteTransaction(e -> deleteSelectedTransaction());
    }

    private void loadUserInfo() {
        String displayName = user.getFullName() == null || user.getFullName().isBlank()
                ? user.getUsername()
                : user.getFullName();
        greetingLabel.setText("Xin chào, " + displayName);
        userNameLabel.setText(displayName);
        userMetaLabel.setText("@" + user.getUsername());
        String formattedDate = HEADER_DATE_FORMAT.format(LocalDate.now());
        if (!formattedDate.isEmpty()) {
            formattedDate = formattedDate.substring(0, 1).toUpperCase(Locale.ROOT) + formattedDate.substring(1);
        }
        dateLabel.setText(formattedDate);
        avatarLabel.setIcon(resolveAvatarIcon(user.getAvatar(), user.getFullName()));
    }

    private void applyFilter(int month, int year) {
        currentMonth = month;
        currentYear = year;
        sidebarPanel.setMonthYear(month, year);
        updatePeriodLabel();
        loadTransactions();
        updateSummary();
    }

    private void updatePeriodLabel() {
        periodLabel.setText(String.format("Tháng %02d / %d", currentMonth, currentYear));
    }

    private void loadTransactions() {
        List<Transaction> transactions = transactionDAO.getByMonth(user.getUserId(), currentMonth, currentYear);
        tablePanel.setTransactions(transactions);
        tablePanel.clearSelection();
    }

    private void updateSummary() {
        Summary summary = transactionDAO.calculateMonthlySummary(user.getUserId(), currentMonth, currentYear);
        summaryPanel.update(summary.getTotalIncome(), summary.getTotalExpense());
    }

    private void openTransactionForm() {
        List<Category> categories = categoryDAO.getByUser(user.getUserId());
        if (categories.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chưa có danh mục. Vui lòng tạo danh mục trước.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        TransactionForm form = new TransactionForm(this, user.getUserId(), categoryDAO, transaction -> {
            transaction.setUserId(user.getUserId());
            transactionDAO.insert(transaction);
            applyFilter(currentMonth, currentYear);
        });
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    private void deleteSelectedTransaction() {
        Transaction selected = tablePanel.getSelectedTransaction();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giao dịch để xóa.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn xóa giao dịch này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            transactionDAO.softDelete(selected.getTransId());
            applyFilter(currentMonth, currentYear);
        }
    }

    private void logout() {
        dispose();
        LoginForm loginForm = new LoginForm();
        loginForm.setLocationRelativeTo(null);
        loginForm.setVisible(true);
    }

    private void openProfileDialog() {
        ProfileDialog dialog = new ProfileDialog(this, user, profileService);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (dialog.isUpdated()) {
            loadUserInfo();
        }
    }

    private ImageIcon resolveAvatarIcon(String avatarPath, String fullName) {
        if (avatarPath != null && !avatarPath.isBlank()) {
            File file = new File(avatarPath);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        }
        return createDefaultAvatar(fullName);
    }

    private ImageIcon createDefaultAvatar(String fullName) {
        int size = 72;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UITheme.PRIMARY_DARK);
            g2.fillOval(0, 0, size, size);
            g2.setColor(Color.WHITE);
            String initials = initialsOf(fullName);
            g2.setFont(UITheme.HEADER_FONT.deriveFont(26f));
            FontMetrics metrics = g2.getFontMetrics();
            int x = (size - metrics.stringWidth(initials)) / 2;
            int y = (size - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.drawString(initials, x, y);
        } finally {
            g2.dispose();
        }
        return new ImageIcon(image);
    }

    private String initialsOf(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        String first = parts[0].substring(0, 1).toUpperCase();
        String last = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return first + last;
    }

    private void styleHeaderChip(JLabel label, Color background, Color foreground) {
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setBorder(new EmptyBorder(6, 14, 6, 14));
        label.setFont(UITheme.bodyFont(13f));
    }

    private static final class GradientBackgroundPanel extends JPanel {
        GradientBackgroundPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint paint = new GradientPaint(0, 0,
                    UITheme.blend(UITheme.BACKGROUND_COLOR, Color.BLACK, 0.15f),
                    getWidth(), getHeight(), UITheme.BACKGROUND_COLOR);
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}








