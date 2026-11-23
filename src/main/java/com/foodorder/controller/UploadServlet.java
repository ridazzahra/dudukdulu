package com.foodorder.controller;

import com.foodorder.util.DbUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Menyajikan gambar dari folder upload (di luar webapp): GET /uploads/{namaFile}.
 * Jika default.png belum ada di folder upload, dipakai bawaan /assets/img/default.png.
 */
@WebServlet("/uploads/*")
public class UploadServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getPathInfo() == null ? "" : req.getPathInfo().substring(1);
        if (!name.matches("[A-Za-z0-9._-]+") || name.contains("..")) { // tolak path traversal
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Path file = DbUtil.getUploadDir().resolve(name).normalize();
        boolean inside = file.startsWith(DbUtil.getUploadDir());
        resp.setHeader("Cache-Control", "public, max-age=86400");
        resp.setContentType(mime(name));

        if (inside && Files.isRegularFile(file)) {
            resp.setContentLengthLong(Files.size(file));
            try (OutputStream out = resp.getOutputStream()) {
                Files.copy(file, out);
            }
            return;
        }
        // cadangan: gambar default bawaan aplikasi
        try (InputStream in = getServletContext().getResourceAsStream("/assets/img/default.png")) {
            if (in == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType("image/png");
            try (OutputStream out = resp.getOutputStream()) {
                in.transferTo(out);
            }
        }
    }

    private String mime(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        if (n.endsWith(".png")) return "image/png";
        if (n.endsWith(".gif")) return "image/gif";
        if (n.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }
}
