package com.foodorder.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PaymentStatus {

    public static final String PENDING = "pending";
    public static final String PAID = "paid";
    public static final String FAILED = "failed";

    public static final Map<String, String> STATUSES;

    private static final Map<String, String> BADGES;

    static {
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put(PENDING, "Pending");
        labels.put(PAID, "Paid");
        labels.put(FAILED, "Failed");
        STATUSES = Collections.unmodifiableMap(labels);

        Map<String, String> badges = new LinkedHashMap<>();
        badges.put(PENDING, "text-bg-warning");
        badges.put(PAID, "text-bg-success");
        badges.put(FAILED, "text-bg-danger");
        BADGES = Collections.unmodifiableMap(badges);
    }

    private PaymentStatus() {
    }

    public static boolean isValid(String status) {
        return status != null && STATUSES.containsKey(status);
    }

    public static String label(String status) {
        if (status == null) {
            return "-";
        }
        return STATUSES.getOrDefault(status, status);
    }

    public static String badge(String status) {
        return BADGES.getOrDefault(status, "text-bg-secondary");
    }

    public static boolean canConfirm(String status) {
        return PENDING.equals(status);
    }
}