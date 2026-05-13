package com.hotel.dao;

import com.hotel.model.Reservation;
import com.hotel.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private static final String SELECT_WITH_JOINS =
            "SELECT r.*, u.name AS user_name, rm.room_number, rm.room_type, rm.price_per_night " +
            "FROM reservations r " +
            "JOIN users u ON r.user_id = u.id " +
            "JOIN rooms rm ON r.room_id = rm.id ";

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setRoomId(rs.getInt("room_id"));
        reservation.setCheckIn(rs.getString("check_in"));
        reservation.setCheckOut(rs.getString("check_out"));
        reservation.setNumberOfGuests(rs.getInt("number_of_guests"));
        reservation.setStatus(rs.getString("status"));
        reservation.setUserName(rs.getString("user_name"));
        reservation.setRoomNumber(rs.getString("room_number"));
        reservation.setRoomType(rs.getString("room_type"));
        reservation.setPricePerNight(rs.getDouble("price_per_night"));
        Timestamp created = rs.getTimestamp("created_at");
        reservation.setCreatedAt(created != null ? created.toString() : "");
        reservation.setTotalAmount(Math.max(reservation.getNights(), 0) * reservation.getPricePerNight());
        return reservation;
    }

    public List<Reservation> getAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY r.check_in DESC, r.id DESC";
        return query(sql);
    }

    public List<Reservation> getByUser(int userId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE r.user_id = ? ORDER BY r.check_in DESC, r.id DESC";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservations.add(map(rs));
                }
            }
        }
        return reservations;
    }

    public Reservation getById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE r.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Reservation getByIdForUser(int id, int userId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE r.id = ? AND r.user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int insert(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (user_id, room_id, check_in, check_out, number_of_guests, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bind(ps, reservation);
                ps.executeUpdate();
                int id = 0;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        id = keys.getInt(1);
                    }
                }
                if (!"Cancelled".equalsIgnoreCase(reservation.getStatus())) {
                    updateRoomStatus(conn, reservation.getRoomId(), "Booked");
                }
                conn.commit();
                return id;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservations SET user_id = ?, room_id = ?, check_in = ?, check_out = ?, "
                + "number_of_guests = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int previousRoomId = findRoomId(conn, reservation.getId());
                bind(ps, reservation);
                ps.setInt(7, reservation.getId());
                boolean updated = ps.executeUpdate() > 0;
                if (updated) {
                    if (previousRoomId > 0 && previousRoomId != reservation.getRoomId()) {
                        updateRoomStatus(conn, previousRoomId, "Available");
                    }
                    if ("Cancelled".equalsIgnoreCase(reservation.getStatus())) {
                        updateRoomStatus(conn, reservation.getRoomId(), "Available");
                    } else {
                        updateRoomStatus(conn, reservation.getRoomId(), "Booked");
                    }
                }
                conn.commit();
                return updated;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean cancel(int id) throws SQLException {
        Reservation existing = getById(id);
        if (existing == null) {
            return false;
        }
        existing.setStatus("Cancelled");
        return update(existing);
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int roomId = findRoomId(conn, id);
                ps.setInt(1, id);
                boolean deleted = ps.executeUpdate() > 0;
                if (deleted && roomId > 0) {
                    updateRoomStatus(conn, roomId, "Available");
                }
                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public int countAll() throws SQLException {
        return count("SELECT COUNT(*) FROM reservations");
    }

    public int countForUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public double totalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'Paid'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    private List<Reservation> query(String sql) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reservations.add(map(rs));
            }
        }
        return reservations;
    }

    private int count(String sql) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void bind(PreparedStatement ps, Reservation reservation) throws SQLException {
        ps.setInt(1, reservation.getUserId());
        ps.setInt(2, reservation.getRoomId());
        ps.setString(3, reservation.getCheckIn());
        ps.setString(4, reservation.getCheckOut());
        ps.setInt(5, reservation.getNumberOfGuests());
        ps.setString(6, reservation.getStatus() != null ? reservation.getStatus() : "Pending");
    }

    private int findRoomId(Connection conn, int reservationId) throws SQLException {
        String sql = "SELECT room_id FROM reservations WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private void updateRoomStatus(Connection conn, int roomId, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        }
    }
}
