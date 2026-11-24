package com.foodorder.dao;

import com.foodorder.model.Order;
import com.foodorder.model.OrderItem;
import com.foodorder.model.OrderStatus;
import com.foodorder.model.Payment;
import com.foodorder.util.DbUtil;
import com.foodorder.util.Page;
import com.foodorder.util.Sql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderDao {

    private static final String SELECT =
            "SELECT o.id, o.user_id, o.table_id, o.total_price, o.status, o.note, "
          + "o.created_at, o.updated_at, o.completed_at, "
          + "u.name AS user_name, u.email AS user_email, t.number AS table_number, "
          + "(SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.id) AS item_count, "
          + "(SELECT COUNT(*) FROM reviews r WHERE r.order_id = o.id) AS review_count, "
          + "p.id AS payment_id, p.payment_method_id, p.amount AS payment_amount, p.status AS payment_status, "
          + "p.paid_at, pm.name AS method_name, pm.type AS method_type "
          + "FROM orders o "
          + "JOIN users u ON u.id = o.user_id "
          + "JOIN dining_tables t ON t.id = o.table_id "
          + "LEFT JOIN payments p ON p.order_id = o.id "
          + "LEFT JOIN payment_methods pm ON pm.id = p.payment_method_id ";

    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setTableId(rs.getInt("table_id"));
        o.setTotalPrice(rs.getBigDecimal("total_price"));
        o.setStatus(rs.getString("status"));
        o.setNote(rs.getString("note"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        o.setCompletedAt(rs.getTimestamp("completed_at"));
        o.setUserName(rs.getString("user_name"));
        o.setUserEmail(rs.getString("user_email"));
        o.setTableNumber(rs.getInt("table_number"));
        o.setItemCount(rs.getInt("item_count"));
        o.setReviewed(rs.getInt("review_count") > 0);
        int paymentId = rs.getInt("payment_id");
        if (!rs.wasNull()) {
            Payment p = new Payment();
            p.setId(paymentId);
            p.setOrderId(o.getId());
            p.setPaymentMethodId(rs.getInt("payment_method_id"));
            p.setAmount(rs.getBigDecimal("payment_amount"));
            p.setStatus(rs.getString("payment_status"));
            p.setPaidAt(rs.getTimestamp("paid_at"));
            p.setMethodName(rs.getString("method_name"));
            p.setMethodType(rs.getString("method_type"));
            o.setPayment(p);
        }
        return o;
    }

    private OrderItem mapItem(ResultSet rs) throws SQLException {
        OrderItem it = new OrderItem();
        it.setId(rs.getInt("id"));
        it.setOrderId(rs.getInt("order_id"));
        it.setMenuId(rs.getInt("menu_id"));
        it.setQuantity(rs.getInt("quantity"));
        it.setPrice(rs.getBigDecimal("price"));
        it.setMenuName(rs.getString("menu_name"));
        it.setMenuImage(rs.getString("image_link"));
        return it;
    }

    public int create(Connection c, Order o) throws SQLException {
        String sql = "INSERT INTO orders (user_id, table_id, total_price, status, note) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getUserId());
            ps.setInt(2, o.getTableId());
            ps.setBigDecimal(3, o.getTotalPrice());
            ps.setString(4, o.getStatus() == null ? OrderStatus.PENDING : o.getStatus());
            ps.setString(5, o.getNote() == null ? "" : o.getNote());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public int createItem(Connection c, OrderItem it) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, menu_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, it.getOrderId());
            ps.setInt(2, it.getMenuId());
            ps.setInt(3, it.getQuantity());
            ps.setBigDecimal(4, it.getPrice());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean updateStatus(Connection c, int id, String expectedStatus, String newStatus) throws SQLException {
        String sql = OrderStatus.COMPLETED.equals(newStatus)
                ? "UPDATE orders SET status = ?, completed_at = CURRENT_TIMESTAMP WHERE id = ? AND status = ?"
                : "UPDATE orders SET status = ? WHERE id = ? AND status = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            ps.setString(3, expectedStatus);
            return ps.executeUpdate() > 0;
        }
    }

    public Order findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection()) {
            return findById(c, id);
        }
    }

    public Order findById(Connection c, int id) throws SQLException {
        Order o = null;
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE o.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o = map(rs);
                }
            }
        }
        if (o != null) {
            fillItems(c, List.of(o));
        }
        return o;
    }

    public Order findByIdForUser(int id, int userId) throws SQLException {
        Order o = findById(id);
        return (o != null && o.getUserId() == userId) ? o : null;
    }

    public List<Order> findByUser(int userId) throws SQLException {
        List<Order> list = new ArrayList<>();
        try (Connection c = DbUtil.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    SELECT + "WHERE o.user_id = ? ORDER BY o.created_at DESC, o.id DESC")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(map(rs));
                    }
                }
            }
            fillItems(c, list);
        }
        return list;
    }

    public Page<Order> findAll(String status, LocalDate from, LocalDate to, String q, int page, int size)
            throws SQLException {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isBlank()) {
            where.append(" AND o.status = ?");
            params.add(status);
        }
        if (from != null) {
            where.append(" AND o.created_at >= ?");
            params.add(Timestamp.valueOf(from.atStartOfDay()));
        }
        if (to != null) {
            where.append(" AND o.created_at < ?");
            params.add(Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
        }
        if (q != null && !q.isBlank()) {
            where.append(" AND (u.name LIKE ? OR u.email LIKE ? OR o.id = ?)");
            params.add(Sql.like(q));
            params.add(Sql.like(q));
            params.add(parseOrderId(q));
        }
        try (Connection c = DbUtil.getConnection()) {
            long total;
            String countSql = "SELECT COUNT(*) FROM orders o JOIN users u ON u.id = o.user_id" + where;
            try (PreparedStatement ps = c.prepareStatement(countSql)) {
                bind(ps, params, 1);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getLong(1);
                }
            }
            int p = Page.clampPage(page, size, total);
            List<Order> items = new ArrayList<>();
            String sql = SELECT + where + " ORDER BY o.created_at DESC, o.id DESC LIMIT ? OFFSET ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                int i = bind(ps, params, 1);
                ps.setInt(i++, size);
                ps.setInt(i, (p - 1) * size);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(map(rs));
                    }
                }
            }
            return new Page<>(items, p, size, total);
        }
    }

    public Map<String, Integer> countByStatus() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String s : OrderStatus.STATUSES.keySet()) {
            counts.put(s, 0);
        }
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT status, COUNT(*) FROM orders GROUP BY status");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString(1), rs.getInt(2));
            }
        }
        return counts;
    }

    public void fillItems(Connection c, List<Order> orders) throws SQLException {
        if (orders.isEmpty()) {
            return;
        }
        Map<Integer, Order> byId = new HashMap<>();
        StringBuilder in = new StringBuilder();
        for (Order o : orders) {
            byId.put(o.getId(), o);
            o.setItems(new ArrayList<>());
            in.append(in.length() == 0 ? "?" : ", ?");
        }
        String sql = "SELECT oi.id, oi.order_id, oi.menu_id, oi.quantity, oi.price, m.name AS menu_name, m.image_link "
                   + "FROM order_items oi JOIN menus m ON m.id = oi.menu_id "
                   + "WHERE oi.order_id IN (" + in + ") ORDER BY oi.id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            for (Order o : orders) {
                ps.setInt(i++, o.getId());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem it = mapItem(rs);
                    byId.get(it.getOrderId()).getItems().add(it);
                }
            }
        }
    }

    private int bind(PreparedStatement ps, List<Object> params, int start) throws SQLException {
        int i = start;
        for (Object o : params) {
            ps.setObject(i++, o);
        }
        return i;
    }

    private int parseOrderId(String q) {
        String s = q.trim();
        if (s.startsWith("#")) {
            s = s.substring(1);
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}