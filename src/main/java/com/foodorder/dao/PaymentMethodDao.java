package com.foodorder.dao;

import com.foodorder.model.PaymentMethod;
import com.foodorder.util.DbUtil;
import com.foodorder.util.Page;
import com.foodorder.util.Sql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDao {

    private static final String COLS = "id, name, type, note, is_active";

    private PaymentMethod map(ResultSet rs) throws SQLException {
        PaymentMethod p = new PaymentMethod();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setType(rs.getString("type"));
        p.setNote(rs.getString("note"));
        p.setActive(rs.getBoolean("is_active"));
        return p;
    }

    public List<PaymentMethod> findAllActive() throws SQLException {
        List<PaymentMethod> list = new ArrayList<>();
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT " + COLS + " FROM payment_methods WHERE is_active = 1 ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Page<PaymentMethod> findAll(String q, int page, int size) throws SQLException {
        String like = Sql.like(q);
        try (Connection c = DbUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM payment_methods WHERE name LIKE ?")) {
                ps.setString(1, like);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getLong(1);
                }
            }
            int p = Page.clampPage(page, size, total);
            List<PaymentMethod> items = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT " + COLS
                    + " FROM payment_methods WHERE name LIKE ? ORDER BY id LIMIT ? OFFSET ?")) {
                ps.setString(1, like);
                ps.setInt(2, size);
                ps.setInt(3, (p - 1) * size);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(map(rs));
                    }
                }
            }
            return new Page<>(items, p, size, total);
        }
    }

    public PaymentMethod findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM payment_methods WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int create(PaymentMethod p) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO payment_methods (name, type, note, is_active) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getType());
            ps.setString(3, p.getNote());
            ps.setBoolean(4, p.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(PaymentMethod p) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE payment_methods SET name = ?, type = ?, note = ?, is_active = ? WHERE id = ?")) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getType());
            ps.setString(3, p.getNote());
            ps.setBoolean(4, p.isActive());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM payment_methods WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleActive(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE payment_methods SET is_active = NOT is_active WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsByName(String name, int excludeId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM payment_methods WHERE name = ? AND id <> ?")) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countPayments(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM payments WHERE payment_method_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}