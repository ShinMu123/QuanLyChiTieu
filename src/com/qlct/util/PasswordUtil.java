package com.qlct.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiện ích mã hóa mật khẩu sử dụng thuật toán SHA-256.
 */
public final class PasswordUtil {
    private PasswordUtil() {
    }

    /**
     * Hash chuỗi đầu vào với SHA-256 và trả về kết quả dạng hex.
     */
    public static String hashSHA256(String rawValue) {
        String safeValue = rawValue == null ? "" : rawValue;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(safeValue.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Không hỗ trợ SHA-256", e);
        }
    }
}
