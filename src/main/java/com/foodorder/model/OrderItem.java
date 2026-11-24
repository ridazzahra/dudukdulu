package com.foodorder.model;

import java.math.BigDecimal;

public class OrderItem {
    private int id;
    private int orderId;
    private int menuId;
    private int quantity;
    private BigDecimal price = BigDecimal.ZERO;

    private String menuName;
    private String menuImage;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuImage() { return menuImage; }
    public void setMenuImage(String menuImage) { this.menuImage = menuImage; }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}