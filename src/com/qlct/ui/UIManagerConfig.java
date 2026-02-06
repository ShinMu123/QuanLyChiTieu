package com.qlct.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

public final class UIManagerConfig {
    private UIManagerConfig() {
    }

    public static void apply() {
        System.setProperty("java.awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        setupLookAndFeel();
        installTypography();
        installColors();
        installComboRenderer();
    }

    private static void setupLookAndFeel() {
        try {
            Class<?> flatClass = Class.forName("com.formdev.flatlaf.themes.FlatDarkLaf");
            Constructor<?> ctor = flatClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            LookAndFeel laf = (LookAndFeel) ctor.newInstance();
            UIManager.setLookAndFeel(laf);
            UIManager.put("Component.arc", 18);
            UIManager.put("Button.arc", 18);
            UIManager.put("TextComponent.arc", 18);
            UIManager.put("ComboBox.arc", 16);
            UIManager.put("ToolTip.border", new EmptyBorder(8, 12, 8, 12));
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fallback to system theme if FlatLaf absent
            }
        }
    }

    private static void installTypography() {
        FontUIResource body = new FontUIResource(UITheme.BODY_FONT);
        FontUIResource button = new FontUIResource(UITheme.BUTTON_FONT);
        FontUIResource caption = new FontUIResource(UITheme.CAPTION_FONT);
        FontUIResource header = new FontUIResource(UITheme.HEADER_FONT.deriveFont(Font.BOLD));

        UIManager.put("defaultFont", body);
        UIManager.put("Label.font", body);
        UIManager.put("ComboBox.font", body);
        UIManager.put("List.font", body);
        UIManager.put("Table.font", body);
        UIManager.put("TableHeader.font", header);
        UIManager.put("Tree.font", body);
        UIManager.put("Button.font", button);
        UIManager.put("ToolTip.font", caption);

        installGlobalFont(UITheme.BODY_FONT);
    }

    private static void installColors() {
        Color primarySelection = UITheme.translucent(UITheme.PRIMARY_COLOR, 150);
        Color mutedSelection = UITheme.translucent(UITheme.PRIMARY_DARK, 110);

        UIManager.put("control", UITheme.BACKGROUND_COLOR);
        UIManager.put("Panel.background", UITheme.BACKGROUND_COLOR);
        UIManager.put("Separator.foreground", UITheme.SURFACE_TINT);

        UIManager.put("Label.foreground", UITheme.TEXT_PRIMARY);

        UIManager.put("TextComponent.background", UITheme.INPUT_BACKGROUND);
        UIManager.put("TextComponent.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("TextComponent.caretForeground", UITheme.PRIMARY_LIGHT);
        UIManager.put("TextComponent.selectionBackground", primarySelection);
        UIManager.put("TextComponent.selectionForeground", UITheme.TEXT_PRIMARY);
        UIManager.put("TextField.background", UITheme.INPUT_BACKGROUND);
        UIManager.put("TextField.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("PasswordField.background", UITheme.INPUT_BACKGROUND);
        UIManager.put("PasswordField.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("Spinner.background", UITheme.INPUT_BACKGROUND);
        UIManager.put("Spinner.foreground", UITheme.TEXT_PRIMARY);

        UIManager.put("ComboBox.background", UITheme.INPUT_BACKGROUND);
        UIManager.put("ComboBox.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", primarySelection);
        UIManager.put("ComboBox.selectionForeground", UITheme.TEXT_PRIMARY);
        UIManager.put("ComboBox.buttonBackground", UITheme.INPUT_BACKGROUND);
        UIManager.put("ComboBox.popupBackground", UITheme.SURFACE_TINT);
        UIManager.put("ComboBox.popupInsets", new EmptyBorder(8, 8, 8, 8));

        UIManager.put("List.background", UITheme.SURFACE_TINT);
        UIManager.put("List.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("List.selectionBackground", primarySelection);
        UIManager.put("List.selectionForeground", UITheme.TEXT_PRIMARY);

        UIManager.put("PopupMenu.background", UITheme.SURFACE_TINT);
        UIManager.put("Menu.background", UITheme.SURFACE_TINT);
        UIManager.put("MenuItem.background", UITheme.SURFACE_TINT);
        UIManager.put("MenuItem.selectionBackground", primarySelection);
        UIManager.put("MenuItem.selectionForeground", UITheme.TEXT_PRIMARY);

        UIManager.put("ScrollPane.background", UITheme.SURFACE_ELEVATED);
        UIManager.put("ScrollPane.border", new EmptyBorder(0, 0, 0, 0));
        UIManager.put("ScrollBar.thumb", UITheme.translucent(UITheme.PRIMARY_COLOR, 130));
        UIManager.put("ScrollBar.track", UITheme.translucent(Color.BLACK, 24));

        UIManager.put("OptionPane.background", UITheme.SURFACE_ELEVATED);
        UIManager.put("OptionPane.messageForeground", UITheme.TEXT_PRIMARY);
        UIManager.put("OptionPane.messageFont", UITheme.BODY_FONT);
        UIManager.put("OptionPane.buttonFont", UITheme.BUTTON_FONT);

        UIManager.put("ToolTip.background", UITheme.SURFACE_TINT);
        UIManager.put("ToolTip.foreground", UITheme.TEXT_PRIMARY);

        UIManager.put("Button.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("Button.background", UITheme.PRIMARY_COLOR);

        UIManager.put("Table.background", UITheme.SURFACE_ELEVATED);
        UIManager.put("Table.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("Table.selectionBackground", primarySelection);
        UIManager.put("Table.selectionForeground", UITheme.TEXT_PRIMARY);
        UIManager.put("Table.alternateRowColor", UITheme.blend(UITheme.SURFACE_ELEVATED, Color.BLACK, 0.18f));
        UIManager.put("Table.gridColor", UITheme.translucent(Color.WHITE, 28));
        UIManager.put("Table.focusCellHighlightBorder", new EmptyBorder(0, 0, 0, 0));

        UIManager.put("TableHeader.background", UITheme.SURFACE_TINT);
        UIManager.put("TableHeader.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("TableHeader.focusCellBackground", mutedSelection);

        UIManager.put("Tree.background", UITheme.SURFACE_ELEVATED);
        UIManager.put("Tree.foreground", UITheme.TEXT_PRIMARY);
        UIManager.put("Tree.selectionBackground", primarySelection);
        UIManager.put("Tree.selectionForeground", UITheme.TEXT_PRIMARY);
    }

    private static void installComboRenderer() {
        UIManager.put("ComboBox.renderer", new DarkComboRenderer());
    }

    private static void installGlobalFont(Font font) {
        FontUIResource base = new FontUIResource(font);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource existing) {
                UIManager.put(key, new FontUIResource(base.deriveFont(existing.getStyle(), existing.getSize())));
            }
        }
    }

    private static final class DarkComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            comp.setFont(UITheme.BODY_FONT);
            if (isSelected) {
                comp.setBackground(UITheme.translucent(UITheme.PRIMARY_COLOR, 160));
                comp.setForeground(UITheme.TEXT_PRIMARY);
            } else {
                comp.setBackground(UITheme.SURFACE_TINT);
                comp.setForeground(UITheme.TEXT_SECONDARY);
            }
            if (comp instanceof javax.swing.JComponent jc) {
                jc.setBorder(new EmptyBorder(6, 12, 6, 12));
            }
            return comp;
        }
    }
}
