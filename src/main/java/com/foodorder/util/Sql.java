package com.foodorder.util;

public final class Sql {

    private Sql() {
    }

    public static String like(String q) {
        String s = q == null ? "" : q.trim();
        s = s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + s + "%";
    }
}
