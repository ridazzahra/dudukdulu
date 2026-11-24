package com.foodorder.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OrderStatus {

    public static final String PENDING = "pending";
    public static final String PROCESSING = "processing";
    public static final String READY = "ready";
    public static final String COMPLETED = "completed";
    public static final String CANCELLED = "cancelled";

    public static final Map<String, String> STATUSES;

    public static final List<String> ACTIVE = List.of(PENDING, PROCESSING, READY);

    private static final Map<String, String> NEXT;
    private static final Map<String, String> BADGES;

    static {
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put(PENDING, "Pending");
        labels.put(PROCESSING, "Processing");
        labels.put(READY, "Ready to Serve");
        labels.put(COMPLETED, "Completed");
        labels.put(CANCELLED, "Cancelled");
        STATUSES = Collections.unmodifiableMap(labels);

        Map<String, String> next = new LinkedHashMap<>();
        next.put(PENDING, PROCESSING);
        next.put(PROCESSING, READY);
        next.put(READY, COMPLETED);
        NEXT = Collections.unmodifiableMap(next);

        Map<String, String> badges = new LinkedHashMap<>();
        badges.put(PENDING, "text-bg-warning");
        badges.put(PROCESSING, "text-bg-primary");
        badges.put(READY, "text-bg-info");
        badges.put(COMPLETED, "text-bg-success");
        badges.put(CANCELLED, "text-bg-danger");
        BADGES = Collections.unmodifiableMap(badges);
    }

    private OrderStatus() {
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

    public static String next(String status) {
        return NEXT.get(status);
    }

    public static String nextActionLabel(String status) {
        String n = next(status);
        if (n == null) {
            return null;
        }
        return switch (n) {
            case PROCESSING -> "Proses Order";
            case READY -> "Tandai Siap";
            case COMPLETED -> "Selesaikan";
            default -> label(n);
        };
    }

    public static boolean canAdminCancel(String status) {
        return PENDING.equals(status) || PROCESSING.equals(status);
    }

    public static boolean canCustomerCancel(String status) {
        return PENDING.equals(status);
    }

    public static boolean isFinal(String status) {
        return COMPLETED.equals(status) || CANCELLED.equals(status);
    }

    public static boolean isActive(String status) {
        return ACTIVE.contains(status);
    }

    public static boolean canBeReviewed(String status) {
        return COMPLETED.equals(status);
    }
}