package com.qlct.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ForgotPasswordForm extends JDialog {
    private final JTextField usernameField = new JTextField(24);
    private final JTextArea guidanceArea = new JTextArea(4, 24);

    public ForgotPasswordForm(JFrame parent) {
        super(parent, "Quên mật khẩu", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(24, 24));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.setBackground(UITheme.BACKGROUND_COLOR);
        root.add(createCard(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout(16, 20));
        card.setOpaque(true);
        card.setBackground(UITheme.SURFACE_COLOR);
        card.setBorder(UITheme.createCardBorder());

        JPanel header = createHeader();
        JPanel form = createForm();
        JPanel actions = createActions();

        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);
        JLabel titleLabel = new JLabel("Khôi phục mật khẩu");
        titleLabel.setFont(UITheme.headerFont(20f));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        guidanceArea.setText("Nhập tên đăng nhập hoặc email để chúng tôi thông báo với quản trị viên hỗ trợ bạn.");
        guidanceArea.setFont(UITheme.bodyFont(14f));
        guidanceArea.setWrapStyleWord(true);
        guidanceArea.setLineWrap(true);
        guidanceArea.setEditable(false);
        guidanceArea.setOpaque(false);
        guidanceArea.setForeground(UITheme.TEXT_MUTED);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(guidanceArea, BorderLayout.CENTER);
        return header;
    }

    private JPanel createForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel usernameLabel = new JLabel("Tên đăng nhập / Email");
        usernameLabel.setFont(UITheme.bodyFont(13f));
        usernameLabel.setForeground(UITheme.TEXT_MUTED);
        formPanel.add(usernameLabel, gbc);

        gbc.gridy++;
        usernameField.setFont(UITheme.bodyFont(14f));
        usernameField.setPreferredSize(new Dimension(260, 32));
        UITheme.styleInput(usernameField);
        formPanel.add(usernameField, gbc);
        return formPanel;
    }

    private JPanel createActions() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionPanel.setOpaque(false);

        JButton cancelButton = new JButton("Hủy");
        UITheme.styleGhostButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());

        JButton sendButton = new JButton("Gửi yêu cầu");
        UITheme.stylePrimaryButton(sendButton);
        sendButton.addActionListener(e -> handleSubmit());

        actionPanel.add(cancelButton);
        actionPanel.add(sendButton);
        return actionPanel;
    }

    private void handleSubmit() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập hoặc email.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Yêu cầu đặt lại mật khẩu cho tài khoản '" + username + "' đã được ghi nhận.\nQuản trị viên sẽ liên hệ bạn sớm nhất có thể.",
                "Đã gửi yêu cầu",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}








