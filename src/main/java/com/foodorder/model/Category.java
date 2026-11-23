package com.foodorder.model;

public class Category {
    private int id;
    private String name;
    private int menuCount; // jumlah menu yang memakai kategori ini (hanya terisi di daftar admin)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getMenuCount() { return menuCount; }
    public void setMenuCount(int menuCount) { this.menuCount = menuCount; }
}
