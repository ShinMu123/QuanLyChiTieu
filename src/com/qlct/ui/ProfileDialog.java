package com.qlct.ui;

import com.qlct.model.User;
import com.qlct.service.ProfileUpdateService;
import com.qlct.service.ProfileUpdateService.AvatarData;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Dialog cho phép người dùng đổi họ tên và ảnh đại diện.
 */
public final class ProfileDialog extends JDialog {
    private final User user;
    private final ProfileUpdateService profileService;
    private final CircularAvatarView avatarView = new CircularAvatarView(140);
    private final JTextField fullNameField = new JTextField(24);
    private final JTextField avatarPathField = new JTextField(24);
    private String currentAvatarPath;
    private boolean updated;

    public ProfileDialog(JFrame owner, User user, ProfileUpdateService profileService) {
        super(owner, "Đổi thông tin cá nhân", true);
        this.user = user;
        this.profileService = profileService;
        this.currentAvatarPath = user.getAvatar();
        fullNameField.setText(user.getFullName());
        avatarPathField.setText(user.getAvatar() == null ? "" : user.getAvatar());
        avatarPathField.setEditable(false);
        avatarPathField.setToolTipText("Đường dẫn ảnh đang dùng");
        UITheme.styleInput(fullNameField);
        UITheme.styleInput(avatarPathField);
        loadInitialAvatar();
        buildUI();
        pack();
        setMinimumSize(new Dimension(420, 520));
        setLocationRelativeTo(owner);
    }

    public boolean isUpdated() {
        return updated;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.setBackground(UITheme.BACKGROUND_COLOR);

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setOpaque(true);
        card.setBackground(UITheme.SURFACE_COLOR);
        card.setBorder(UITheme.createCardBorder());

        JLabel title = new JLabel("Hồ sơ cá nhân");
        title.setFont(UITheme.headerFont(20f));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(6, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel avatarPanel = new JPanel();
        avatarPanel.setOpaque(false);
        avatarPanel.add(avatarView);
        content.add(avatarPanel, gbc);

        gbc.gridy++;
        JButton chooseButton = new JButton("Chọn ảnh");
        UITheme.styleSecondaryButton(chooseButton, UITheme.ACCENT_COLOR, UITheme.TEXT_PRIMARY);
        chooseButton.addActionListener(e -> chooseAvatar());
        content.add(chooseButton, gbc);

        gbc.gridy++;
        JLabel nameLabel = new JLabel("Họ và tên");
        nameLabel.setFont(UITheme.bodyFont(13f));
        nameLabel.setForeground(UITheme.TEXT_MUTED);
        content.add(nameLabel, gbc);

        gbc.gridy++;
        content.add(fullNameField, gbc);

        gbc.gridy++;
        JLabel avatarLabel = new JLabel("Đường dẫn ảnh");
        avatarLabel.setFont(UITheme.bodyFont(13f));
        avatarLabel.setForeground(UITheme.TEXT_MUTED);
        content.add(avatarLabel, gbc);

        gbc.gridy++;
        content.add(avatarPathField, gbc);

        card.add(content, BorderLayout.CENTER);

        JButton closeButton = new JButton("Đóng");
        UITheme.styleGhostButton(closeButton);
        closeButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Lưu thay đổi");
        UITheme.stylePrimaryButton(saveButton);
        saveButton.addActionListener(e -> saveChanges());

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.add(closeButton);
        actions.add(saveButton);

        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void chooseAvatar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Ảnh (PNG, JPG, JPEG)", "png", "jpg", "jpeg"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        try {
            AvatarData data = profileService.storeAvatar(user, path);
            currentAvatarPath = data.storedPath();
            avatarPathField.setText(currentAvatarPath);
            avatarView.setImage(data.previewImage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể lưu ảnh: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges() {
        String name = fullNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập họ và tên hợp lệ",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            profileService.applyChanges(user, name, currentAvatarPath);
            updated = true;
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể lưu hồ sơ: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInitialAvatar() {
        BufferedImage image = null;
        if (profileService != null) {
            try {
                image = profileService.loadAvatar(user.getAvatar());
            } catch (IOException ignored) {
                // Nếu ảnh bị lỗi, fallback sẽ được dùng
            }
        }
        if (image == null) {
            image = createDefaultAvatar(user.getFullName());
        }
        avatarView.setImage(image);
    }

    private static BufferedImage createDefaultAvatar(String fullName) {
        int size = 128;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = image.createGraphics();
        try {
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UITheme.PRIMARY_DARK);
            g2.fillOval(0, 0, size, size);
            g2.setColor(UITheme.TEXT_PRIMARY);
            String initials = initialsOf(fullName);
            g2.setFont(UITheme.headerFont(32f));
            java.awt.FontMetrics metrics = g2.getFontMetrics();
            int x = (size - metrics.stringWidth(initials)) / 2;
            int y = (size - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.drawString(initials, x, y);
        } finally {
            g2.dispose();
        }
        return image;
    }

    private static String initialsOf(String fullName) {
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
}








