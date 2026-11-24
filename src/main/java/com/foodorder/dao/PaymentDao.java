package com.foodorder.dao;

import com.foodorder.model.Payment;
import com.foodorder.model.PaymentStatus;
import com.foodorder.util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PaymentDao {

    private static final String SELECT =
            "SELECT p.id, p.order_id, p.payment_method_id, p.amount, p.status, p.paid_at, "
          + "pm.name AS method_name, pm.type AS method_type "
          + "FROM payments p JOIN payment_methods pm ON pm.id = p.payment_method_id ";

    private Payment map(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id"));
        p.setOrderId(rs.getInt("order_id"));
        p.setPaymentMethodId(rs.getInt("payment_method_id"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setStatus(rs.getString("status"));
        p.setPaidAt(rs.getTimestamp("paid_at"));
        p.setMethodName(rs.getString("method_name"));
        p.setMethodType(rs.getString("method_type"));
        return p;
    }

    public int create(Connection c, Payment p) throws SQLException {
        String sql = "INSERT INTO payments (order_id, payment_method_id, amount, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getOrderId());
            ps.setInt(2, p.getPaymentMethodId());
            ps.setBigDecimal(3, p.getAmount());
            ps.setString(4, p.getStatus() == null ? PaymentStatus.PENDING : p.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public Payment findByOrder(int orderId) throws SQLException {
        try (Connection c = DbUtil.getConnection()) {
            return findByOrder(c, orderId);
        }
    }

    public Payment findByOrder(Connection c, int orderId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE p.order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean markPaid(Connection c, int orderId) throws SQLException {
        String sql = "UPDATE payments SET status = ?, paid_at = CURRENT_TIMESTAMP WHERE order_id = ? AND status = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, PaymentStatus.PAID);
            ps.setInt(2, orderId);
            ps.setString(3, PaymentStatus.PENDING);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean markFailed(Connection c, int orderId) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE order_id = ? AND status = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, PaymentStatus.FAILED);
            ps.setInt(2, orderId);
            ps.setString(3, PaymentStatus.PENDING);
            return ps.executeUpdate() > 0;
        }
    }
}