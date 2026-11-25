package com.foodorder.util;

import java.util.List;

public class Page<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalItems;

    public Page(List<T> items, int page, int size, long totalItems) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
    }

    public static int clampPage(int requested, int size, long totalItems) {
        int totalPages = (int) Math.max(1, (totalItems + size - 1) / size);
        return Math.min(Math.max(requested, 1), totalPages);
    }

    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalItems() { return totalItems; }
    public int getTotalPages() { return (int) Math.max(1, (totalItems + size - 1) / size); }
    public boolean isHasPrev() { return page > 1; }
    public boolean isHasNext() { return page < getTotalPages(); }
    public boolean isEmpty() { return items == null || items.isEmpty(); }
}
