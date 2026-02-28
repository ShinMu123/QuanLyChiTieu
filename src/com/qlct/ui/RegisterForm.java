package com.qlct.ui;

import com.qlct.db.DBConnection;
import com.qlct.util.PasswordUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Form đăng ký tài khoản người dùng mới.
 */
public class RegisterForm extends JFrame {
    private static final String CHECK_USERNAME_SQL = "SELECT 1 FROM Users WHERE Username = ?";
    private static final String INSERT_USER_SQL = """
            INSERT INTO Users (Username, PasswordHash, FullName, CreatedAt, avatar)
            VALUES (?, ?, ?, GETDATE(), NULL)
            """;

    private final JTextField usernameField = new JTextField(22);
    private final JTextField fullNameField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);
    private final JPasswordField confirmPasswordField = new JPasswordField(22);

    public RegisterForm() {
        this(null);
    }

    public RegisterForm(JFrame parent) {
        super("Đăng ký tài khoản - Quản lý chi tiêu");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Khởi tạo giao diện với hero panel và form nhập liệu.
     */
    private void buildUI() {
        GradientPanel backdrop = new GradientPanel(new GradientPaint(0, 0, UITheme.BACKGROUND_COLOR,
                1, 1, UITheme.blend(UITheme.PRIMARY_DARK, UITheme.BACKGROUND_COLOR, 0.35f)));
        backdrop.setLayout(new GridBagLayout());
        backdrop.setBorder(new EmptyBorder(48, 56, 48, 56));

        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        JPanel surface = new JPanel(new GridBagLayout());
        UITheme.applyCardStyle(surface);
        surface.setBackground(UITheme.SURFACE_ELEVATED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        JPanel heroPanel = createHeroPanel();
        surface.add(heroPanel, gbc);

        gbc.gridx = 1;
        JPanel formPanel = createFormPanel();
        surface.add(formPanel, gbc);

        card.add(surface);
        backdrop.add(card, new GridBagConstraints());
        setContentPane(backdrop);
        JButton defaultButton = findRegisterButton(formPanel);
        if (defaultButton != null) {
            getRootPane().setDefaultButton(defaultButton);
        }
    }

    private JPanel createHeroPanel() {
        GradientPanel hero = new GradientPanel(new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                1, 1, UITheme.blend(UITheme.ACCENT_COLOR, UITheme.PRIMARY_LIGHT, 0.45f)));
        hero.setPreferredSize(new Dimension(320, 0));
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(40, 36, 40, 32));

        JLabel badge = new JLabel("QLCT Premium");
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        badge.setFont(UITheme.bodyFont(13f));
        badge.setForeground(UITheme.translucent(UITheme.TEXT_PRIMARY, 160));

        JLabel headline = new JLabel("<html>Tạo tài khoản mới<br>Kiểm soát chi tiêu hiệu quả</html>");
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);
        headline.setFont(UITheme.headerFont(28f));
        headline.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Theo dõi thu chi realtime, bảo mật SHA-256.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setFont(UITheme.bodyFont(14f));
        sub.setForeground(UITheme.translucent(UITheme.TEXT_PRIMARY, 180));

        hero.add(badge);
        hero.add(Box.createVerticalStrut(12));
        hero.add(headline);
        hero.add(Box.createVerticalStrut(18));
        hero.add(sub);
        hero.add(Box.createVerticalStrut(28));
        hero.add(createHeroBullet("Dữ liệu lưu tại SQL Server"));
        hero.add(Box.createVerticalStrut(12));
        hero.add(createHeroBullet("Mã hóa mật khẩu SHA-256"));
        hero.add(Box.createVerticalStrut(12));
        hero.add(createHeroBullet("Nhắc hạn mức thông minh"));
        hero.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("Liên hệ support@qlct.vn nếu cần trợ giúp");
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setFont(UITheme.bodyFont(12f));
        footer.setForeground(UITheme.translucent(UITheme.TEXT_PRIMARY, 150));
        hero.add(footer);
        return hero;
    }

    private JLabel createHeroBullet(String text) {
        JLabel label = new JLabel("• " + text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(UITheme.bodyFont(14f));
        label.setForeground(UITheme.TEXT_PRIMARY);
        return label;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(42, 48, 42, 48));

        JLabel title = new JLabel("Đăng ký tài khoản mới");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(UITheme.headerFont(30f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Điền thông tin bên dưới để bắt đầu.");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setFont(UITheme.bodyFont(14f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(subtitle);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createHelperLabel("• Tên đăng nhập viết liền, tối thiểu 4 ký tự."));
        formPanel.add(createHelperLabel("• Mật khẩu mạnh nên có chữ hoa, chữ thường và số."));
        formPanel.add(createHelperLabel("• Họ tên có thể bỏ trống nếu bạn muốn cập nhật sau."));
        formPanel.add(Box.createVerticalStrut(24));

        configureInput(usernameField, "Tên đăng nhập *");
        formPanel.add(usernameField);
        formPanel.add(createHelperLabel("Ví dụ: vananh92, không chứa khoảng trắng."));
        formPanel.add(Box.createVerticalStrut(18));

        configureInput(fullNameField, "Họ và tên");
        formPanel.add(fullNameField);
        formPanel.add(createHelperLabel("Tên thật giúp cá nhân hóa trải nghiệm."));
        formPanel.add(Box.createVerticalStrut(18));

        configurePasswordInput(passwordField, "Mật khẩu *");
        formPanel.add(passwordField);
        formPanel.add(createHelperLabel("Ít nhất 6 ký tự, nên pha trộn ký tự đặc biệt."));
        formPanel.add(Box.createVerticalStrut(12));

        configurePasswordInput(confirmPasswordField, "Nhập lại mật khẩu *");
        formPanel.add(confirmPasswordField);
        formPanel.add(createHelperLabel("Nhập lại giống mật khẩu để xác nhận."));
        formPanel.add(Box.createVerticalStrut(24));

        JButton registerButton = new JButton("Đăng ký");
        UITheme.stylePrimaryButton(registerButton);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegister());

        JButton backButton = new JButton("Quay lại đăng nhập");
        UITheme.styleGhostButton(backButton);
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> dispose());

        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(backButton);

        return formPanel;
    }

    private JLabel createHelperLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(UITheme.bodyFont(12f));
        label.setForeground(UITheme.TEXT_SECONDARY);
        return label;
    }

    private JButton findRegisterButton(JPanel formPanel) {
        for (Component component : formPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if ("Đăng ký".equals(button.getText())) {
                    return button;
                }
            }
        }
        return null;
    }

    /**
     * Submit thông tin đăng ký, kiểm tra dữ liệu và ghi xuống DB.
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập và mật khẩu không được để trống.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu xác nhận không khớp.",
                    "Sai mật khẩu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác.",
                    "Trùng tên đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = PasswordUtil.hashSHA256(password);
        if (insertUser(username, hashedPassword, fullName)) {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thành công! Vui lòng đăng nhập lại.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    /**
     * Kiểm tra trùng tên đăng nhập trước khi ghi dữ liệu.
     */
    private boolean usernameExists(String username) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(CHECK_USERNAME_SQL)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể kiểm tra tên đăng nhập: " + ex.getMessage(),
                    "Lỗi CSDL",
                    JOptionPane.ERROR_MESSAGE);
            return true;
        }
    }

    /**
     * Thực hiện lệnh INSERT Users với mật khẩu đã được hash.
     */
    private boolean insertUser(String username, String hashedPassword, String fullName) {
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_USER_SQL)) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            if (fullName == null || fullName.isBlank()) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, fullName.trim());
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể đăng ký tài khoản: " + ex.getMessage(),
                    "Lỗi CSDL",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Thiết lập style chung cho ô nhập dạng text.
     */
    private void configureInput(JTextField field, String placeholder) {
        UITheme.styleInput(field);
        field.setOpaque(true);
        field.setBackground(UITheme.INPUT_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(UITheme.createInputBorder(UITheme.PRIMARY_LIGHT));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(UITheme.createInputBorder(null));
            }
        });
    }

    /**
     * Thiết lập style chung cho ô nhập mật khẩu.
     */
    private void configurePasswordInput(JPasswordField field, String placeholder) {
        UITheme.styleInput(field);
        field.setOpaque(true);
        field.setBackground(UITheme.INPUT_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(UITheme.createInputBorder(UITheme.PRIMARY_LIGHT));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(UITheme.createInputBorder(null));
            }
        });
    }

    private static final class GradientPanel extends JPanel {
        private final GradientPaint paint;

        GradientPanel(GradientPaint paint) {
            this.paint = paint;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, paint.getColor1(), getWidth(), getHeight(), paint.getColor2()));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
