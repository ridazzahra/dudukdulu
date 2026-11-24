package com.foodorder.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    private int id;
    private int orderId;
    private int paymentMethodId;
    private BigDecimal amount = BigDecimal.ZERO;
    private String status = PaymentStatus.PENDING;
    private Timestamp paidAt;

    private String methodName;
    private String methodType;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(int paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }

    public String getStatusLabel() { return PaymentStatus.label(status); }
    public String getStatusBadge() { return PaymentStatus.badge(status); }
    public boolean isPaid() { return PaymentStatus.PAID.equals(status); }
    public boolean isPending() { return PaymentStatus.PENDING.equals(status); }
    public boolean isCanConfirm() { return PaymentStatus.canConfirm(status); }
}