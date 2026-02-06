package com.qlct.ui;

import com.qlct.dao.UserDAO;
import com.qlct.model.User;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame {
    private JButton loginButton;
    private final IconTextField usernameField = new IconTextField(
            UITheme.createGlyphIcon("👤", 22, UITheme.TEXT_SECONDARY));
    private final IconPasswordField passwordField = new IconPasswordField(
            UITheme.createGlyphIcon("🔒", 20, UITheme.TEXT_SECONDARY));
    private final UserDAO userDAO = new UserDAO();

    public LoginForm() {
        setTitle("Đăng nhập - Quản lý chi tiêu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setMinimumSize(new Dimension(960, 600));
        buildUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        GradientPanel backdrop = new GradientPanel(
                new GradientPaint(0, 0, UITheme.BACKGROUND_COLOR,
                        1, 1, UITheme.blend(UITheme.PRIMARY_DARK, UITheme.BACKGROUND_COLOR, 0.35f)));
        backdrop.setLayout(new GridBagLayout());
        backdrop.setBorder(new EmptyBorder(48, 56, 48, 56));

        JPanel card = createLoginCard();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        backdrop.add(card, gbc);

        setContentPane(backdrop);
        getRootPane().setDefaultButton(loginButton);
    }

    private JPanel createLoginCard() {
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
        return card;
    }

    private JPanel createHeroPanel() {
        GradientPanel hero = new GradientPanel(
                new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                        1, 1, UITheme.blend(UITheme.ACCENT_COLOR, UITheme.PRIMARY_LIGHT, 0.45f)));
        hero.setPreferredSize(new Dimension(320, 0));
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(40, 36, 40, 32));

        JLabel badge = new JLabel("QLCT Premium");
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        badge.setFont(UITheme.bodyFont(13f));
        badge.setForeground(UITheme.translucent(UITheme.TEXT_PRIMARY, 160));

        JLabel headline = new JLabel("<html>Kiểm soát chi tiêu<br>ở tầm chuyên nghiệp</html>");
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);
        headline.setFont(UITheme.headerFont(28f));
        headline.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Trải nghiệm dashboard xịn, bảo mật chuẩn fintech.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setFont(UITheme.bodyFont(14f));
        sub.setForeground(UITheme.translucent(UITheme.TEXT_PRIMARY, 180));

        hero.add(badge);
        hero.add(Box.createVerticalStrut(12));
        hero.add(headline);
        hero.add(Box.createVerticalStrut(18));
        hero.add(sub);
        hero.add(Box.createVerticalStrut(28));
        hero.add(createHeroBullet("Ảnh tổng quan realtime"));
        hero.add(Box.createVerticalStrut(12));
        hero.add(createHeroBullet("Theo dõi hạn mức nhắc nhở"));
        hero.add(Box.createVerticalStrut(12));
        hero.add(createHeroBullet("Phân tích xu hướng thông minh"));
        hero.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("Dữ liệu được mã hóa AES-256");
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

        JLabel welcome = new JLabel("Chào mừng trở lại");
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcome.setFont(UITheme.headerFont(30f));
        welcome.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Đăng nhập để tiếp tục kiểm soát dòng tiền");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setFont(UITheme.bodyFont(14f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        formPanel.add(welcome);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(subtitle);
        formPanel.add(Box.createVerticalStrut(28));

        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setColumns(20);
        configureInput(usernameField, "Tên đăng nhập");
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(18));

        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        configureInput(passwordField, "Mật khẩu");
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(16));

        Icon loginIcon = UITheme.createGlyphIcon("🔐", 22, UITheme.TEXT_PRIMARY);
        Icon forgotIcon = UITheme.createGlyphIcon("🧠", 18, UITheme.TEXT_SECONDARY);
        Icon registerIcon = UITheme.createGlyphIcon("✳", 18, UITheme.TEXT_SECONDARY);

        loginButton = new JButton("Đăng nhập");
        UITheme.stylePrimaryButton(loginButton);
        loginButton.setIcon(loginIcon);
        loginButton.setIconTextGap(12);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginButton.addActionListener(e -> authenticate());
        formPanel.add(loginButton);

        formPanel.add(Box.createVerticalStrut(12));

        JButton forgotButton = new JButton("Quên mật khẩu");
        UITheme.styleGhostButton(forgotButton);
        forgotButton.setIcon(forgotIcon);
        forgotButton.setIconTextGap(10);
        forgotButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgotButton.addActionListener(e -> openForgotPassword());
        formPanel.add(forgotButton);

        formPanel.add(Box.createVerticalStrut(8));

        JButton registerButton = new JButton("Đăng ký");
        UITheme.styleGhostButton(registerButton);
        registerButton.setIcon(registerIcon);
        registerButton.setIconTextGap(10);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setForeground(UITheme.TEXT_SECONDARY);
        registerButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Liên hệ quản trị hệ thống để được cấp quyền sử dụng.",
                "Thông tin",
                JOptionPane.INFORMATION_MESSAGE));
        formPanel.add(registerButton);

        formPanel.add(Box.createVerticalStrut(24));

        JLabel support = new JLabel("Hỗ trợ 24/7 • support@qlct.vn");
        support.setAlignmentX(Component.LEFT_ALIGNMENT);
        support.setFont(UITheme.bodyFont(12f));
        support.setForeground(UITheme.TEXT_SECONDARY);
        formPanel.add(support);

        return formPanel;
    }

    private void configureInput(JTextField field, String placeholder) {
        UITheme.styleInput(field);
        field.setOpaque(true);
        field.setBackground(UITheme.INPUT_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);
        field.putClientProperty("JTextField.placeholderText", placeholder);
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

    private void configureInput(JPasswordField field, String placeholder) {
        UITheme.styleInput(field);
        field.setOpaque(true);
        field.setBackground(UITheme.INPUT_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);
        field.putClientProperty("JTextField.placeholderText", placeholder);
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

    private void openForgotPassword() {
        ForgotPasswordForm dialog = new ForgotPasswordForm(this);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MainForm mainForm = new MainForm(user);
        mainForm.setLocationRelativeTo(this);
        mainForm.setVisible(true);
        dispose();
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

    private static final class IconTextField extends JTextField {
        private static final int ICON_PADDING = 6;
        private static final int ICON_TEXT_GAP = 12;
        private final Icon icon;

        IconTextField(Icon icon) {
            this.icon = icon;
            setColumns(22);
        }

        @Override
        public Insets getInsets() {
            Insets base = super.getInsets();
            if (icon != null) {
                base.left += icon.getIconWidth() + ICON_PADDING + ICON_TEXT_GAP;
            }
            return base;
        }

        @Override
        public Insets getInsets(Insets insets) {
            Insets base = super.getInsets(insets);
            if (icon != null) {
                base.left += icon.getIconWidth() + ICON_PADDING + ICON_TEXT_GAP;
            }
            return base;
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            if (icon != null) {
                Insets borderInsets = new Insets(0, 0, 0, 0);
                super.getInsets(borderInsets);
                int x = borderInsets.left + ICON_PADDING;
                int y = (getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g, x, y);
            }
        }
    }

    private static final class IconPasswordField extends JPasswordField {
        private static final int ICON_PADDING = 6;
        private static final int ICON_TEXT_GAP = 12;
        private final Icon icon;

        IconPasswordField(Icon icon) {
            this.icon = icon;
            setColumns(22);
        }

        @Override
        public Insets getInsets() {
            Insets base = super.getInsets();
            if (icon != null) {
                base.left += icon.getIconWidth() + ICON_PADDING + ICON_TEXT_GAP;
            }
            return base;
        }

        @Override
        public Insets getInsets(Insets insets) {
            Insets base = super.getInsets(insets);
            if (icon != null) {
                base.left += icon.getIconWidth() + ICON_PADDING + ICON_TEXT_GAP;
            }
            return base;
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            if (icon != null) {
                Insets borderInsets = new Insets(0, 0, 0, 0);
                super.getInsets(borderInsets);
                int x = borderInsets.left + ICON_PADDING;
                int y = (getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g, x, y);
            }
        }
    }
}








