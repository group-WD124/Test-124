package com.hotel.dao;

import com.hotel.model.Payment;
import com.hotel.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private static final String SELECT_WITH_JOINS =
            "SELECT p.*, u.name AS user_name, " +
            "CONCAT('Reservation #', r.id, ' - Room ', rm.room_number, ' (', rm.room_type, ')') AS reservation_label " +
            "FROM payments p " +
            "JOIN users u ON p.user_id = u.id " +
            "JOIN reservations r ON p.reservation_id = r.id " +
            "JOIN rooms rm ON r.room_id = rm.id ";

    private Payment map(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setReservationId(rs.getInt("reservation_id"));
        payment.setUserId(rs.getInt("user_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setPaymentDate(rs.getString("payment_date"));
        payment.setStatus(rs.getString("status"));
        payment.setUserName(rs.getString("user_name"));
        payment.setReservationLabel(rs.getString("reservation_label"));
        return payment;
    }

    public List<Payment> getAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY p.payment_date DESC, p.id DESC";
        return query(sql);
    }

    public List<Payment> getByUser(int userId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE p.user_id = ? ORDER BY p.payment_date DESC, p.id DESC";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payments.add(map(rs));
                }
            }
        }
        return payments;
    }

    public Payment getById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE p.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Payment getByIdForUser(int id, int userId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE p.id = ? AND p.user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean insert(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (reservation_id, user_id, amount, payment_method, payment_date, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bind(ps, payment);
                boolean inserted = ps.executeUpdate() > 0;
                if (inserted) {
                    updateReservationAfterPayment(conn, payment);
                }
                conn.commit();
                return inserted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean update(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET reservation_id = ?, user_id = ?, amount = ?, payment_method = ?, "
                + "payment_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bind(ps, payment);
                ps.setInt(7, payment.getId());
                boolean updated = ps.executeUpdate() > 0;
                if (updated) {
                    updateReservationAfterPayment(conn, payment);
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

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM payments WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public double totalPaid() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'Paid'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payments WHERE status = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private List<Payment> query(String sql) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                payments.add(map(rs));
            }
        }
        return payments;
    }

    private void bind(PreparedStatement ps, Payment payment) throws SQLException {
        ps.setInt(1, payment.getReservationId());
        ps.setInt(2, payment.getUserId());
        ps.setDouble(3, payment.getAmount());
        ps.setString(4, payment.getPaymentMethod());
        ps.setString(5, payment.getPaymentDate());
        ps.setString(6, payment.getStatus() != null ? payment.getStatus() : "Pending");
    }

    private void updateReservationAfterPayment(Connection conn, Payment payment) throws SQLException {
        if (!"Paid".equalsIgnoreCase(payment.getStatus())) {
            return;
        }
        String sql = "UPDATE reservations SET status = 'Confirmed' WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getReservationId());
            ps.executeUpdate();
        }
    }
}
