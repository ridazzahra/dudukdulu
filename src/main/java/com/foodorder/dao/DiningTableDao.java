package com.foodorder.dao;

import com.foodorder.model.DiningTable;
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

/** DAO meja. Anggota B memakai findAvailable() dan updateStatus() di checkout / penyelesaian order. */
public class DiningTableDao {

    private static final String COLS = "id, number, capacity, status";

    private DiningTable map(ResultSet rs) throws SQLException {
        DiningTable t = new DiningTable();
        t.setId(rs.getInt("id"));
        t.setNumber(rs.getInt("number"));
        t.setCapacity(rs.getInt("capacity"));
        t.setStatus(rs.getString("status"));
        return t;
    }

    /** Semua meja urut nomor. */
    public List<DiningTable> findAll() throws SQLException {
        return list("SELECT " + COLS + " FROM dining_tables ORDER BY number");
    }

    /** Hanya meja berstatus 'available' (untuk pilihan di checkout). */
    public List<DiningTable> findAvailable() throws SQLException {
        return list("SELECT " + COLS + " FROM dining_tables WHERE status = 'available' ORDER BY number");
    }

    private List<DiningTable> list(String sql) throws SQLException {
        List<DiningTable> list = new ArrayList<>();
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /** Daftar berhalaman; q mencari nomor meja, status null/kosong = semua. */
    public Page<DiningTable> findAll(String q, String status, int page, int size) throws SQLException {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            where.append(" AND CAST(number AS CHAR) LIKE ?");
            params.add(Sql.like(q));
        }
        if (status != null && !status.isBlank()) {
            where.append(" AND status = ?");
            params.add(status);
        }
        try (Connection c = DbUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM dining_tables" + where)) {
                int i = 1;
                for (Object o : params) {
                    ps.setObject(i++, o);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getLong(1);
                }
            }
            int p = Page.clampPage(page, size, total);
            List<DiningTable> items = new ArrayList<>();
            String sql = "SELECT " + COLS + " FROM dining_tables" + where + " ORDER BY number LIMIT ? OFFSET ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                int i = 1;
                for (Object o : params) {
                    ps.setObject(i++, o);
                }
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

    public DiningTable findById(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM dining_tables WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int create(DiningTable t) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO dining_tables (number, capacity, status) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getNumber());
            ps.setInt(2, t.getCapacity());
            ps.setString(3, t.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(DiningTable t) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE dining_tables SET number = ?, capacity = ?, status = ? WHERE id = ?")) {
            ps.setInt(1, t.getNumber());
            ps.setInt(2, t.getCapacity());
            ps.setString(3, t.getStatus());
            ps.setInt(4, t.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Melempar SQLIntegrityConstraintViolationException bila meja pernah dipakai order (FK RESTRICT). */
    public boolean delete(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM dining_tables WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Ubah status saja (Anggota B: 'occupied' saat order dibuat, 'available' saat selesai/batal). */
    public boolean updateStatus(int id, String status) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE dining_tables SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsNumber(int number, int excludeId) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM dining_tables WHERE number = ? AND id <> ?")) {
            ps.setInt(1, number);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countOrders(int id) throws SQLException {
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM orders WHERE table_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
