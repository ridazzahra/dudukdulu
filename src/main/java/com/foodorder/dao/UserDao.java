package com.foodorder.dao;

import com.foodorder.model.User;
import com.foodorder.util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserDao {

    private static final String COLS = "id, name, email, password, role, phone, is_active";

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
}
