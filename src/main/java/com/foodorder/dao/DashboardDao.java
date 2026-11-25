package com.foodorder.dao;

import com.foodorder.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDao {

    public Map<String, Object> getStats() throws SQLException {
        Map<String, Object> s = new HashMap<>();
        for (String k : new String[]{"pending", "processing", "ready", "completed", "cancelled"}) {
            s.put(k, 0);
        }

        try (Connection c = DbUtil.getConnection()) {

            int total = 0;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT status, COUNT(*) AS n FROM orders GROUP BY status");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    s.put(rs.getString("status"), rs.getInt("n"));
                    total += rs.getInt("n");
                }
            }
            s.put("totalOrders", total);

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM orders WHERE DATE(created_at) = CURDATE()");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                s.put("ordersToday", rs.getInt(1));
            }

            String revSql =
                "SELECT COALESCE(SUM(total_price), 0) AS rev_total, "
              + "COALESCE(SUM(CASE WHEN DATE(created_at) = CURDATE() THEN total_price END), 0) AS rev_today, "
              + "COALESCE(SUM(CASE WHEN YEAR(created_at) = YEAR(CURDATE()) "
              + "AND MONTH(created_at) = MONTH(CURDATE()) THEN total_price END), 0) AS rev_month "
              + "FROM orders WHERE status = ?";
            try (PreparedStatement ps = c.prepareStatement(revSql)) {
                ps.setString(1, "completed");
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    s.put("revenueTotal", rs.getBigDecimal("rev_total"));
                    s.put("revenueToday", rs.getBigDecimal("rev_today"));
                    s.put("revenueMonth", rs.getBigDecimal("rev_month"));
                }
            }

            String topSql =
                "SELECT m.name, SUM(oi.quantity) AS sold, SUM(oi.quantity * oi.price) AS revenue "
              + "FROM order_items oi "
              + "JOIN orders o ON o.id = oi.order_id "
              + "JOIN menus m ON m.id = oi.menu_id "
              + "WHERE o.status <> ? "
              + "GROUP BY m.id, m.name ORDER BY sold DESC, m.name LIMIT 5";
            List<Map<String, Object>> top = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(topSql)) {
                ps.setString(1, "cancelled");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("name", rs.getString("name"));
                        m.put("sold", rs.getInt("sold"));
                        m.put("revenue", rs.getBigDecimal("revenue"));
                        top.add(m);
                    }
                }
            }
            s.put("topMenus", top);

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COALESCE(AVG(rating), 0) AS avg_rating, COUNT(*) AS n FROM reviews");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                s.put("avgRating", rs.getDouble("avg_rating"));
                s.put("reviewCount", rs.getInt("n"));
            }
        }
        return s;
    }
}