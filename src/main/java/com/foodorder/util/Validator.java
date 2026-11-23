package com.foodorder.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validasi input sisi server. Pesan error per field, ditampilkan di JSP lewat ${errors.namaField}.
 * Hanya pesan pertama per field yang disimpan.
 */
public class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final Map<String, String> errors = new LinkedHashMap<>();

    public Validator required(String field, String value, String label) {
        if (value == null || value.isBlank()) {
            error(field, label + " wajib diisi.");
        }
        return this;
    }

    public Validator maxLength(String field, String value, int max, String label) {
        if (value != null && value.length() > max) {
            error(field, label + " maksimal " + max + " karakter.");
        }
        return this;
    }

    public Validator minLength(String field, String value, int min, String label) {
        if (value != null && !value.isEmpty() && value.length() < min) {
            error(field, label + " minimal " + min + " karakter.");
        }
        return this;
    }

    public Validator email(String field, String value) {
        if (value != null && !value.isBlank() && !EMAIL.matcher(value).matches()) {
            error(field, "Format email tidak valid.");
        }
        return this;
    }

    public Validator matches(String field, String value, String regex, String message) {
        if (value != null && !value.isBlank() && !value.matches(regex)) {
            error(field, message);
        }
        return this;
    }

    public Validator error(String field, String message) {
        errors.putIfAbsent(field, message);
        return this;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    // ---- helper parsing (tidak melempar exception) ----

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** Integer atau null bila kosong/tidak valid. */
    public static Integer toInt(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Harga: hanya angka dengan maksimal 2 desimal (tanpa notasi ilmiah). null bila tidak valid. */
    public static BigDecimal toMoney(String s) {
        if (s == null || !s.trim().matches("^\\d{1,13}(\\.\\d{1,2})?$")) {
            return null;
        }
        return new BigDecimal(s.trim());
    }
}
