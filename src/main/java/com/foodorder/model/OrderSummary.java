package com.foodorder.model;

import java.math.BigDecimal;
import java.util.Date;

public class OrderSummary {
    private int id;
    private String status;
    private BigDecimal total;
    private Date createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}