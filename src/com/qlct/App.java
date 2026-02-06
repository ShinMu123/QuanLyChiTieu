package com.qlct;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.swing.SwingUtilities;

import com.qlct.ui.LoginForm;
import com.qlct.ui.UIManagerConfig;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        Locale.setDefault(new Locale("vi", "VN"));
        warnIfNonUtf8Runtime();
        UIManagerConfig.apply();
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setLocationRelativeTo(null);
            loginForm.setVisible(true);
        });
    }

    private static void warnIfNonUtf8Runtime() {
        if (!Charset.defaultCharset().equals(StandardCharsets.UTF_8)) {
            System.err.println("Warning: JVM default charset is " + Charset.defaultCharset()
                    + ". Please launch with -Dfile.encoding=UTF-8 to avoid mojibake.");
        }
    }
}








