package com.foodorder.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final Map<String, String> errors = new LinkedHashMap<>();

    public Validator required(String field, String value, String label) {
        if (value == null || value.isBlank()) {
            error(field, label + " is required.");
        }
        return this;
    }

    public Validator maxLength(String field, String value, int max, String label) {
        if (value != null && value.length() > max) {
            error(field, label + " must be at most " + max + " characters.");
        }
        return this;
    }

    public Validator minLength(String field, String value, int min, String label) {
        if (value != null && !value.isEmpty() && value.length() < min) {
            error(field, label + " must be at least " + min + " characters.");
        }
        return this;
    }

    public Validator email(String field, String value) {
        if (value != null && !value.isBlank() && !EMAIL.matcher(value).matches()) {
            error(field, "Invalid email format.");
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


    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static Integer toInt(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static BigDecimal toMoney(String s) {
        if (s == null || !s.trim().matches("^\\d{1,13}(\\.\\d{1,2})?$")) {
            return null;
        }
        return new BigDecimal(s.trim());
    }
}