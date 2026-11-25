package com.foodorder.dao;

import com.foodorder.model.Category;
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

public class CategoryDao {

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, name FROM categories ORDER BY name");
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                list.add(cat);
            }
        }
        return list;
    }

    public Page<Category> findAll(String q, int page, int size) throws SQLException {
        String like = Sql.like(q);
        try (Connection c = DbUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM categories WHERE name LIKE ?")) {
                ps.setString(1, like);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getLong(1);
                }
            }
            int p = Page.clampPage(page, size, total);
            String sql = "SELECT c.id, c.name, "
                    + "(SELECT COUNT(*) FROM menus m WHERE m.category_id = c.id) AS menu_count "
                    + "FROM categories c WHERE c.name LIKE ? ORDER BY c.name LIMIT ? OFFSET ?";
            List<Category> items = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, like);
                ps.setInt(2, size);
                ps.setInt(3, (p - 1) * size);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Category cat = new Category();
                        cat.setId(rs.getInt("id"));
                        cat.setName(rs.getString("name"));
                        cat.setMenuCount(rs.getInt("menu_count"));
                        items.add(cat);
                    }
                }
            }
            return new Page<>(items, p, size, total);
        }
    }

    public Category findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, name FROM categories WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                return cat;
            }
        }
    }

    public int create(Category cat) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO categories (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(Category cat) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE categories SET name = ? WHERE id = ?")) {
            ps.setString(1, cat.getName());
            ps.setInt(2, cat.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("DELETE FROM categories WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsByName(String name, int excludeId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT 1 FROM categories WHERE name = ? AND id <> ?")) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countMenus(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM menus WHERE category_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
