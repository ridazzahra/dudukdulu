package com.foodorder.dao;

import com.foodorder.model.Cart;
import com.foodorder.util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartDao {

    public static final int MAX_QUANTITY = 99;

    private static final String SELECT =
            "SELECT c.id, c.user_id, c.menu_id, c.quantity, m.name AS menu_name, m.price AS menu_price, "
          + "m.image_link, m.is_available, cat.name AS category_name "
          + "FROM carts c JOIN menus m ON m.id = c.menu_id JOIN categories cat ON cat.id = m.category_id ";

    private Cart map(ResultSet rs) throws SQLException {
        Cart c = new Cart();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setMenuId(rs.getInt("menu_id"));
        c.setQuantity(rs.getInt("quantity"));
        c.setMenuName(rs.getString("menu_name"));
        c.setMenuPrice(rs.getBigDecimal("menu_price"));
        c.setMenuImage(rs.getString("image_link"));
        c.setMenuAvailable(rs.getBoolean("is_available"));
        c.setCategoryName(rs.getString("category_name"));
        return c;
    }

    public List<Cart> findByUser(int userId) throws SQLException {
        try (Connection c = DbUtil.getConnection()) {
            return findByUser(c, userId);
        }
    }

    public List<Cart> findByUser(Connection c, int userId) throws SQLException {
        List<Cart> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE c.user_id = ? ORDER BY c.id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Map<Integer, Integer> quantitiesByUser(int userId) throws SQLException {
        Map<Integer, Integer> map = new HashMap<>();
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT menu_id, quantity FROM carts WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt(1), rs.getInt(2));
                }
            }
        }
        return map;
    }

    public Cart findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE c.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int countItems(int userId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COALESCE(SUM(quantity), 0) FROM carts WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void addOrIncrement(int userId, int menuId) throws SQLException {
        String sql = "INSERT INTO carts (user_id, menu_id, quantity) VALUES (?, ?, 1) "
                   + "ON DUPLICATE KEY UPDATE quantity = LEAST(quantity + 1, " + MAX_QUANTITY + ")";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, menuId);
            ps.executeUpdate();
        }
    }

    public boolean updateQuantity(int id, int userId, int quantity) throws SQLException {
        if (quantity <= 0) {
            return delete(id, userId);
        }
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE carts SET quantity = ? WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, Math.min(quantity, MAX_QUANTITY));
            ps.setInt(2, id);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id, int userId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM carts WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public int clear(Connection c, int userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM carts WHERE user_id = ?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        }
    }
}