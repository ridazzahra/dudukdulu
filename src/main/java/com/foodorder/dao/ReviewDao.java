package com.foodorder.dao;

import com.foodorder.model.Review;
import com.foodorder.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao {

    private static final String SELECT =
        "SELECT r.id, r.user_id, r.order_id, r.rating, r.comment, r.created_at, u.name, u.email "
      + "FROM reviews r JOIN users u ON u.id = r.user_id";
    private static final String COUNT =
        "SELECT COUNT(*) FROM reviews r JOIN users u ON u.id = r.user_id";

    public UserDao.Result<Review> search(String q, int star, int page, int size) throws SQLException {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            String like = "%" + q.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            where.append(" AND (r.comment LIKE ? OR u.name LIKE ? OR u.email LIKE ?)");
            params.add(like); params.add(like); params.add(like);
        }
        if (star >= 1 && star <= 5) {
            where.append(" AND r.rating >= ? AND r.rating < ?");
            params.add(star);
            params.add(star + 1);
        }

        try (Connection c = DbUtil.getConnection()) {
            int total;
            try (PreparedStatement ps = c.prepareStatement(COUNT + where)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); total = rs.getInt(1); }
            }
            int pages = Math.max(1, (int) Math.ceil(total / (double) size));
            int p = Math.min(Math.max(page, 1), pages);

            List<Object> p2 = new ArrayList<>(params);
            p2.add(size);
            p2.add((p - 1) * size);
            List<Review> rows = new ArrayList<>();
            String sql = SELECT + where + " ORDER BY r.created_at DESC, r.id DESC LIMIT ? OFFSET ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, p2);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Review r = new Review();
                        r.setId(rs.getInt("id"));
                        r.setUserId(rs.getInt("user_id"));
                        r.setOrderId(rs.getInt("order_id"));
                        r.setRating(rs.getBigDecimal("rating"));
                        r.setComment(rs.getString("comment"));
                        r.setCreatedAt(rs.getTimestamp("created_at"));
                        r.setUserName(rs.getString("name"));
                        r.setUserEmail(rs.getString("email"));
                        rows.add(r);
                    }
                }
            }
            return new UserDao.Result<>(rows, total, p, size);
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

        private static Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setOrderId(rs.getInt("order_id"));
        r.setRating(rs.getBigDecimal("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setUserName(rs.getString("name"));
        r.setUserEmail(rs.getString("email"));
        return r;
    }

    public Review findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE r.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Review findByOrderId(int orderId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE r.order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Review> findByUser(int userId) throws SQLException {
        List<Review> list = new ArrayList<>();
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     SELECT + " WHERE r.user_id = ? ORDER BY r.created_at DESC, r.id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public void create(Review r) throws SQLException {
        String sql = "INSERT INTO reviews (user_id, order_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getOrderId());
            ps.setBigDecimal(3, r.getRating());
            ps.setString(4, r.getComment());
            ps.executeUpdate();
        }
    }

    public boolean update(Review r) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE id = ? AND user_id = ?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, r.getRating());
            ps.setString(2, r.getComment());
            ps.setInt(3, r.getId());
            ps.setInt(4, r.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteOwn(int id, int userId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM reviews WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
    }
}