package com.foodorder.util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Simpan gambar upload ke folder DbUtil.getUploadDir() (di luar webapp).
 * Nilai yang dikembalikan ("uploads/xxxx.jpg") disimpan di kolom image_link dan
 * ditampilkan lewat URL ${ctx}/uploads/xxxx.jpg (dilayani UploadServlet).
 */
public final class ImageUploader {

    public static final String DEFAULT_IMAGE = "uploads/default.png";
    private static final long MAX_BYTES = 2L * 1024 * 1024;
    private static final Set<String> EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private ImageUploader() {
    }

    /** true bila form benar-benar mengirim file. */
    public static boolean hasFile(Part part) {
        return part != null && part.getSize() > 0
                && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isBlank();
    }

    /** Pesan error validasi, atau null bila file layak. */
    public static String validate(Part part) throws IOException {
        if (part.getSize() > MAX_BYTES) {
            return "Ukuran gambar maksimal 2 MB.";
        }
        String ext = extension(part.getSubmittedFileName());
        if (!EXT.contains(ext)) {
            return "Format gambar harus JPG, PNG, GIF, atau WEBP.";
        }
        byte[] head = new byte[12];
        try (InputStream in = part.getInputStream()) {
            int n = in.readNBytes(head, 0, head.length);
            if (n < 4 || !looksLikeImage(head)) {
                return "Isi file bukan gambar yang valid.";
            }
        }
        return null;
    }

    /** Simpan file (sudah divalidasi) dan kembalikan path relatif untuk kolom image_link. */
    public static String save(Part part) throws IOException {
        String ext = extension(part.getSubmittedFileName());
        String name = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dir = DbUtil.getUploadDir();
        Files.createDirectories(dir);
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return "uploads/" + name;
    }

    /** Hapus file lama (kecuali gambar default). Gagal hapus diabaikan. */
    public static void delete(String imageLink) {
        if (imageLink == null || imageLink.equals(DEFAULT_IMAGE) || !imageLink.startsWith("uploads/")) {
            return;
        }
        String name = imageLink.substring("uploads/".length());
        if (!name.matches("[A-Za-z0-9._-]+")) {
            return;
        }
        try {
            Files.deleteIfExists(DbUtil.getUploadDir().resolve(name));
        } catch (IOException ignored) {
            // file yatim tidak fatal
        }
    }

    private static String extension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean looksLikeImage(byte[] h) {
        boolean jpeg = (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8;
        boolean png = (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
        boolean gif = h[0] == 'G' && h[1] == 'I' && h[2] == 'F';
        boolean webp = h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
        return jpeg || png || gif || webp;
    }
}
