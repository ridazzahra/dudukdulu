package com.foodorder.dao;

import com.foodorder.model.User;
import com.foodorder.util.DbUtil;
import com.foodorder.model.OrderSummary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static final String COLS = "id, name, email, password, role, phone, is_active";
    private static final String ADMIN_SELECT =
        "SELECT u.id, u.name, u.email, u.password, u.role, u.phone, u.is_active, "
        + "(SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count FROM users u";

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setPhone(rs.getString("phone"));
        u.setActive(rs.getBoolean("is_active"));
        return u;
    }

    public User findByEmail(String email) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int create(User u) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role, phone, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getPhone());
            ps.setBoolean(6, u.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public static class Result<T> {
        private final List<T> items;
        private final int total, page, size;

        public Result(List<T> items, int total, int page, int size) {
            this.items = items; this.total = total; this.page = page; this.size = size;
        }
        public List<T> getItems() { return items; }
        public int getTotal() { return total; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public int getTotalPages() { return Math.max(1, (int) Math.ceil(total / (double) size)); }
        public int getFrom() { return total == 0 ? 0 : (page - 1) * size + 1; }
        public int getTo() { return Math.min(total, page * size); }
    }

    public Result<User> search(String q, String role, String status, int page, int size) throws SQLException {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            String like = "%" + q.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            where.append(" AND (u.name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)");
            params.add(like); params.add(like); params.add(like);
        }
        if ("admin".equals(role) || "customer".equals(role)) {
            where.append(" AND u.role = ?");
            params.add(role);
        }
        if ("active".equals(status)) where.append(" AND u.is_active = 1");
        else if ("inactive".equals(status)) where.append(" AND u.is_active = 0");

        try (Connection c = DbUtil.getConnection()) {
            int total;
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM users u" + where)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); total = rs.getInt(1); }
            }
            int p = clampPage(page, total, size);
            List<Object> p2 = new ArrayList<>(params);
            p2.add(size);
            p2.add((p - 1) * size);
            List<User> rows = new ArrayList<>();
            String sql = ADMIN_SELECT + where + " ORDER BY u.id DESC LIMIT ? OFFSET ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, p2);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        User u = map(rs);
                        u.setOrderCount(rs.getInt("order_count"));
                        rows.add(u);
                    }
                }
            }
            return new Result<>(rows, total, p, size);
        }
    }

    public void update(int id, String name, String email, String phone, String role, String passwordHash)
            throws SQLException {
        String sql = passwordHash == null
            ? "UPDATE users SET name = ?, email = ?, phone = ?, role = ? WHERE id = ?"
            : "UPDATE users SET name = ?, email = ?, phone = ?, role = ?, password = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, name);
            ps.setString(i++, email);
            ps.setString(i++, phone);
            ps.setString(i++, role);
            if (passwordHash != null) ps.setString(i++, passwordHash);
            ps.setInt(i, id);
            ps.executeUpdate();
        }
    }

    public void setActive(int id, boolean active) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE users SET is_active = ? WHERE id = ?")) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countOrders(int userId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM orders WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    public Result<OrderSummary> orders(int userId, int page, int size) throws SQLException {
        int total = countOrders(userId);
        int p = clampPage(page, total, size);
        List<OrderSummary> rows = new ArrayList<>();
        String sql = "SELECT id, status, total_price, created_at FROM orders WHERE user_id = ? "
                   + "ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, size);
            ps.setInt(3, (p - 1) * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderSummary o = new OrderSummary();
                    o.setId(rs.getInt("id"));
                    o.setStatus(rs.getString("status"));
                    o.setTotal(rs.getBigDecimal("total_price"));
                    o.setCreatedAt(rs.getTimestamp("created_at"));
                    rows.add(o);
                }
            }
        }
        return new Result<>(rows, total, p, size);
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
    }

    private static int clampPage(int page, int total, int size) {
        int pages = Math.max(1, (int) Math.ceil(total / (double) size));
        return Math.min(Math.max(page, 1), pages);
    }
}
