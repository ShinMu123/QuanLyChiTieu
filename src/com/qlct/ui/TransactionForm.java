package com.qlct.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.qlct.dao.CategoryDAO;
import com.qlct.model.Category;
import com.qlct.model.Transaction;

public class TransactionForm extends JDialog {
    private static final String[] TYPE_OPTIONS = { "Thu", "Chi" };

    private final CategoryDAO categoryDAO;
    private final int userId;
    private final JComboBox<String> typeCombo = new JComboBox<>(TYPE_OPTIONS);
    private final DefaultComboBoxModel<Category> categoryModel = new DefaultComboBoxModel<>();
    private final JComboBox<Category> categoryCombo = new JComboBox<>(categoryModel);
    private final JTextField amountField = new JTextField(15);
    private final JSpinner dateSpinner = new JSpinner(
            new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    private final JTextArea noteArea = new JTextArea(4, 20);
    private final Consumer<Transaction> onSaved;
    private boolean suppressTypeListener;

    public TransactionForm(JFrame owner, int userId, CategoryDAO categoryDAO, Consumer<Transaction> onSaved) {
        super(owner, "Thêm giao dịch", true);
        this.userId = userId;
        this.categoryDAO = categoryDAO;
        this.onSaved = onSaved;
        setResizable(false);
        buildUI();
        typeCombo.addActionListener(e -> {
            if (suppressTypeListener) {
                return;
            }
            if (!reloadCategories(null)) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy danh mục phù hợp. Vui lòng tạo danh mục trước.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        initializeCategories();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.setBackground(UITheme.BACKGROUND_COLOR);

        JPanel card = new JPanel(new BorderLayout(16, 24));
        card.setOpaque(true);
        card.setBackground(UITheme.SURFACE_COLOR);
        card.setBorder(UITheme.createCardBorder());

        JLabel title = new JLabel("Thông tin giao dịch");
        title.setFont(UITheme.headerFont(20f));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        addField(formPanel, gbc, "Loại giao dịch", typeCombo);
        UITheme.styleInput(typeCombo);

        categoryCombo.setEditable(true);
        categoryCombo.setMaximumRowCount(8);
        addField(formPanel, gbc, "Danh mục", categoryCombo);
        UITheme.styleInput(categoryCombo);
        java.awt.Component editor = categoryCombo.getEditor().getEditorComponent();
        if (editor instanceof JTextField editorField) {
            editorField.setFont(UITheme.bodyFont(14f));
        }

        addField(formPanel, gbc, "Số tiền", amountField);
        UITheme.styleInput(amountField);
        amountField.setHorizontalAlignment(JTextField.RIGHT);

        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        addField(formPanel, gbc, "Ngày giao dịch", dateSpinner);
        UITheme.styleInput(dateSpinner);

        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setFont(UITheme.bodyFont(14f));
        noteArea.setBackground(UITheme.SURFACE_COLOR);
        noteArea.setBorder(null);
        JScrollPane noteScroll = new JScrollPane(noteArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        noteScroll.setBorder(UITheme.createRoundedBorder(UITheme.SURFACE_BORDER_COLOR, 12, 12));
        noteScroll.setPreferredSize(new Dimension(260, 120));
        noteScroll.getViewport().setBackground(UITheme.SURFACE_COLOR);
        addField(formPanel, gbc, "Ghi chú", noteScroll);

        card.add(formPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        JButton saveButton = new JButton("Lưu giao dịch");
        UITheme.stylePrimaryButton(saveButton);
        saveButton.addActionListener(e -> saveTransaction());

        JButton cancelButton = new JButton("Hủy bỏ");
        UITheme.styleSecondaryButton(cancelButton, UITheme.ACCENT_COLOR, UITheme.TEXT_PRIMARY);
        cancelButton.addActionListener(e -> dispose());

        actions.add(cancelButton);
        actions.add(saveButton);

        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void initializeCategories() {
        suppressTypeListener = true;
        boolean loaded = false;
        try {
            typeCombo.setSelectedIndex(0);
            loaded = reloadCategories(null);
            if (!loaded) {
                for (String typeOption : TYPE_OPTIONS) {
                    if (typeOption.equals(typeCombo.getSelectedItem())) {
                        continue;
                    }
                    typeCombo.setSelectedItem(typeOption);
                    loaded = reloadCategories(null);
                    if (loaded) {
                        break;
                    }
                }
            }
        } finally {
            suppressTypeListener = false;
        }
        if (!loaded) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy danh mục cho người dùng này. Vui lòng tạo danh mục trước.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean reloadCategories(Integer preferredCategoryId) {
        categoryModel.removeAllElements();
        String type = (String) typeCombo.getSelectedItem();
        if (type == null) {
            categoryCombo.setSelectedItem(null);
            return false;
        }
        int indexToSelect = -1;
        int index = 0;
        for (Category category : categoryDAO.getByUserAndType(userId, type)) {
            categoryModel.addElement(category);
            if (preferredCategoryId != null && category.getCategoryId() == preferredCategoryId) {
                indexToSelect = index;
            }
            index++;
        }
        if (categoryModel.getSize() == 0) {
            categoryCombo.setSelectedItem(null);
            return false;
        }
        if (indexToSelect >= 0) {
            categoryCombo.setSelectedIndex(indexToSelect);
        } else {
            categoryCombo.setSelectedIndex(0);
        }
        return true;
    }

    private boolean reloadCategories() {
        return reloadCategories(null);
    }

    private void saveTransaction() {
        Object categorySelection = categoryCombo.getSelectedItem();
        Category category;
        if (categorySelection instanceof Category existingCategory) {
            category = existingCategory;
        } else if (categorySelection != null && !categorySelection.toString().trim().isEmpty()) {
            String type = (String) typeCombo.getSelectedItem();
            try {
                Category created = categoryDAO.findOrCreate(userId, type, categorySelection.toString().trim());
                boolean reloaded = reloadCategories(created.getCategoryId());
                category = reloaded ? (Category) categoryCombo.getSelectedItem() : created;
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this,
                        "Không thể tạo danh mục mới: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc nhập danh mục", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("amount must be positive");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate transactionDate = ((Date) dateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate();
        String note = noteArea.getText().trim();

        Transaction transaction = new Transaction();
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setTransactionDate(transactionDate);
        transaction.setNote(note);
        transaction.setDeleted(false);

        onSaved.accept(transaction);
        dispose();
    }

    private void addField(JPanel container, GridBagConstraints gbc, String labelText, java.awt.Component component) {
        JLabel label = new JLabel(labelText + ":", SwingConstants.LEFT);
        label.setFont(UITheme.bodyFont(13f));
        label.setForeground(UITheme.TEXT_MUTED);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        container.add(label, gbc);

        gbc.gridy++;
        container.add(component, gbc);

        gbc.gridy++;
    }
}








