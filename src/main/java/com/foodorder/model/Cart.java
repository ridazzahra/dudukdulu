package com.foodorder.model;

import java.math.BigDecimal;

public class Cart {
    private int id;
    private int userId;
    private int menuId;
    private int quantity = 1;

    private String menuName;
    private BigDecimal menuPrice = BigDecimal.ZERO;
    private String menuImage;
    private boolean menuAvailable = true;
    private String categoryName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public BigDecimal getMenuPrice() { return menuPrice; }
    public void setMenuPrice(BigDecimal menuPrice) { this.menuPrice = menuPrice; }
    public String getMenuImage() { return menuImage; }
    public void setMenuImage(String menuImage) { this.menuImage = menuImage; }
    public boolean isMenuAvailable() { return menuAvailable; }
    public void setMenuAvailable(boolean menuAvailable) { this.menuAvailable = menuAvailable; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getSubtotal() {
        return menuPrice.multiply(BigDecimal.valueOf(quantity));
    }
}