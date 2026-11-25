package com.foodorder.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DiningTable {
    public static final String AVAILABLE = "available";
    public static final String OCCUPIED = "occupied";
    public static final String RESERVED = "reserved";

    public static final Map<String, String> STATUSES = new LinkedHashMap<>();
    static {
        STATUSES.put(AVAILABLE, "Available");
        STATUSES.put(OCCUPIED, "Occupied");
        STATUSES.put(RESERVED, "Reserved");
    }

    private int id;
    private int number;
    private int capacity = 2;
    private String status = AVAILABLE;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusLabel() { return STATUSES.getOrDefault(status, status); }
    public boolean isAvailableNow() { return AVAILABLE.equals(status); }
}
