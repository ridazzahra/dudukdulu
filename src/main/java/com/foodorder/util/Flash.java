package com.foodorder.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Pesan sukses/error satu kali tampil (satu utilitas untuk seluruh tim).
 *
 * Sebelum redirect  : Flash.success(req, "Tersimpan.");  Flash.error(req, "Gagal.");
 * Tanpa redirect    : Flash.successNow(req, ...) / Flash.errorNow(req, ...)  (untuk forward)
 * Di JSP            : ${flashSuccess} dan ${flashError} (sudah ditampilkan oleh layout/header.jsp)
 */
public final class Flash {

    public static final String SUCCESS = "flash_success";
    public static final String ERROR = "flash_error";
    public static final String SUCCESS_ATTR = "flashSuccess";
    public static final String ERROR_ATTR = "flashError";

    private Flash() {
    }

    public static void success(HttpServletRequest req, String message) {
        req.getSession().setAttribute(SUCCESS, message);
    }

    public static void error(HttpServletRequest req, String message) {
        req.getSession().setAttribute(ERROR, message);
    }

    public static void successNow(HttpServletRequest req, String message) {
        req.setAttribute(SUCCESS_ATTR, message);
    }

    public static void errorNow(HttpServletRequest req, String message) {
        req.setAttribute(ERROR_ATTR, message);
    }
}
