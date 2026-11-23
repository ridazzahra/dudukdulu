package com.foodorder.util;

/** Helper kecil untuk query. */
public final class Sql {

    private Sql() {
    }

    /** Bungkus kata kunci untuk LIKE '%kata%' dengan karakter spesial di-escape. */
    public static String like(String q) {
        String s = q == null ? "" : q.trim();
        s = s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + s + "%";
    }
}
