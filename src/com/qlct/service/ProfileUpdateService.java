package com.qlct.service;

import com.qlct.dao.UserDAO;
import com.qlct.model.User;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * Handles file-system work for profile updates so the UI can remain thin.
 */
public final class ProfileUpdateService {
    private static final int AVATAR_SIZE = 128;
    private final UserDAO userDAO;
    private final Path avatarDirectory;

    public ProfileUpdateService(UserDAO userDAO, Path avatarDirectory) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO");
        this.avatarDirectory = Objects.requireNonNull(avatarDirectory, "avatarDirectory");
    }

    public AvatarData storeAvatar(User user, Path sourcePath) throws IOException {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(sourcePath, "sourcePath");
        BufferedImage original = ImageIO.read(sourcePath.toFile());
        if (original == null) {
            throw new IOException("Không thể đọc ảnh: định dạng không được hỗ trợ");
        }
        BufferedImage resized = resizeToAvatar(original);
        Path baseDir = avatarDirectory.toAbsolutePath();
        Files.createDirectories(baseDir);
        String fileName = buildAvatarFileName(user);
        Path target = baseDir.resolve(fileName).normalize();
        ImageIO.write(resized, "png", target.toFile());
        String storedPath = baseDir.relativize(target).toString();
        return new AvatarData(storedPath, resized);
    }

    public BufferedImage loadAvatar(String storedPath) throws IOException {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }
        Path path = Path.of(storedPath);
        if (!path.isAbsolute()) {
            path = avatarDirectory.toAbsolutePath().resolve(path).normalize();
        }
        if (!Files.exists(path)) {
            return null;
        }
        return ImageIO.read(path.toFile());
    }

    public void applyChanges(User user, String newFullName, String avatarPath) {
        Objects.requireNonNull(user, "user");
        String safeName = newFullName == null ? "" : newFullName.trim();
        String nameToPersist = safeName.isEmpty() ? user.getFullName() : safeName;
        String avatarToPersist = avatarPath != null && !avatarPath.isBlank() ? avatarPath : user.getAvatar();
        userDAO.updateProfile(user.getUserId(), nameToPersist, avatarToPersist);
        user.setFullName(nameToPersist);
        user.setAvatar(avatarToPersist);
    }

    private static BufferedImage resizeToAvatar(BufferedImage original) {
        BufferedImage resized = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(original, 0, 0, AVATAR_SIZE, AVATAR_SIZE, null);
        } finally {
            g2.dispose();
        }
        return resized;
    }

    private static String buildAvatarFileName(User user) {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ROOT));
        return "user-" + user.getUserId() + "-" + stamp + ".png";
    }

    public record AvatarData(String storedPath, BufferedImage previewImage) {
    }
}
