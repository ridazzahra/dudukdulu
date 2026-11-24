package com.foodorder.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private int tableId;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private String status = OrderStatus.PENDING;
    private String note = "";
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp completedAt;

    private String userName;
    private String userEmail;
    private int tableNumber;
    private int itemCount;
    private boolean reviewed;
    private List<OrderItem> items = new ArrayList<>();
    private Payment payment;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note == null ? "" : note; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public boolean isReviewed() { return reviewed; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public String getStatusLabel() { return OrderStatus.label(status); }
    public String getStatusBadge() { return OrderStatus.badge(status); }
    public String getNextStatus() { return OrderStatus.next(status); }
    public String getNextActionLabel() { return OrderStatus.nextActionLabel(status); }
    public boolean isCanAdminCancel() { return OrderStatus.canAdminCancel(status); }
    public boolean isCanCustomerCancel() { return OrderStatus.canCustomerCancel(status); }
    public boolean isCanBeReviewed() { return OrderStatus.canBeReviewed(status) && !reviewed; }
    public boolean isFinalStatus() { return OrderStatus.isFinal(status); }
}