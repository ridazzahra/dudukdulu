package com.foodorder.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentMethod {
    public static final Map<String, String> TYPES = new LinkedHashMap<>();
    static {
        TYPES.put("cash", "Cash");
        TYPES.put("qris", "QRIS");
        TYPES.put("transfer", "Bank Transfer");
        TYPES.put("ewallet", "E-Wallet");
    }

    private int id;
    private String name;
    private String type = "cash";
    private String note = "";
    private boolean active = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getTypeLabel() { return TYPES.getOrDefault(type, type); }
}