package com.foodorder.dao;

import com.foodorder.model.Menu;
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

public class MenuDao {

    private static final String SELECT = "SELECT m.id, m.category_id, c.name AS category_name, m.name, "
            + "m.description, m.price, m.image_link, m.is_available "
            + "FROM menus m JOIN categories c ON c.id = m.category_id";

    private Menu map(ResultSet rs) throws SQLException {
        Menu m = new Menu();
        m.setId(rs.getInt("id"));
        m.setCategoryId(rs.getInt("category_id"));
        m.setCategoryName(rs.getString("category_name"));
        m.setName(rs.getString("name"));
        m.setDescription(rs.getString("description"));
        m.setPrice(rs.getBigDecimal("price"));
        m.setImageLink(rs.getString("image_link"));
        m.setAvailable(rs.getBoolean("is_available"));
        return m;
    }

    private String orderBy(String sort) {
        if (sort == null) {
            return "m.name ASC";
        }
        switch (sort) {
            case "price_asc":  return "m.price ASC, m.name ASC";
            case "price_desc": return "m.price DESC, m.name ASC";
            case "newest":     return "m.id DESC";
            case "available":  return "m.is_available DESC, m.name ASC";
            default:           return "m.name ASC";
        }
    }


    public Page<Menu> findAll(String q, Integer categoryId, Boolean availability, String sort,
                            int page, int size) throws SQLException {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            where.append(" AND m.name LIKE ?");
            params.add(Sql.like(q));
        }
        if (categoryId != null) {
            where.append(" AND m.category_id = ?");
            params.add(categoryId);
        }
        if (availability != null) {
            where.append(" AND m.is_available = ?");
            params.add(availability);
        }

        try (Connection c = DbUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM menus m" + where)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getLong(1);
                }
            }
            int p = Page.clampPage(page, size, total);
            List<Menu> items = new ArrayList<>();
            String sql = SELECT + where + " ORDER BY " + orderBy(sort) + " LIMIT ? OFFSET ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                int idx = bind(ps, params);
                ps.setInt(idx++, size);
                ps.setInt(idx, (p - 1) * size);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(map(rs));
                    }
                }
            }
            return new Page<>(items, p, size, total);
        }
    }

    private int bind(PreparedStatement ps, List<Object> params) throws SQLException {
        int i = 1;
        for (Object o : params) {
            ps.setObject(i++, o);
        }
        return i;
    }

    public Menu findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(SELECT + " WHERE m.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int create(Menu m) throws SQLException {
        String sql = "INSERT INTO menus (category_id, name, description, price, image_link, is_available) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getCategoryId());
            ps.setString(2, m.getName());
            ps.setString(3, m.getDescription());
            ps.setBigDecimal(4, m.getPrice());
            ps.setString(5, m.getImageLink());
            ps.setBoolean(6, m.isAvailable());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(Menu m) throws SQLException {
        String sql = "UPDATE menus SET category_id = ?, name = ?, description = ?, price = ?, "
                + "image_link = ?, is_available = ? WHERE id = ?";
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.getCategoryId());
            ps.setString(2, m.getName());
            ps.setString(3, m.getDescription());
            ps.setBigDecimal(4, m.getPrice());
            ps.setString(5, m.getImageLink());
            ps.setBoolean(6, m.isAvailable());
            ps.setInt(7, m.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("DELETE FROM menus WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean toggleAvailability(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE menus SET is_available = NOT is_available WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countInOrders(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM order_items WHERE menu_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
