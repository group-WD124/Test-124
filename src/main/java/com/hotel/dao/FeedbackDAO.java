package com.hotel.dao;

import com.hotel.model.Feedback;
import com.hotel.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    private static final String SELECT_WITH_JOINS =
            "SELECT f.*, u.name AS user_name, " +
            "CONCAT('Reservation #', r.id, ' - Room ', rm.room_number, ' (', rm.room_type, ')') AS reservation_label " +
            "FROM feedback f " +
            "JOIN users u ON f.user_id = u.id " +
            "JOIN reservations r ON f.reservation_id = r.id " +
            "JOIN rooms rm ON r.room_id = rm.id ";

    private Feedback map(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getInt("id"));
        feedback.setUserId(rs.getInt("user_id"));
        feedback.setReservationId(rs.getInt("reservation_id"));
        feedback.setRating(rs.getInt("rating"));
        feedback.setComment(rs.getString("comment"));
        feedback.setFeedbackDate(rs.getString("feedback_date"));
        feedback.setUserName(rs.getString("user_name"));
        feedback.setReservationLabel(rs.getString("reservation_label"));
        return feedback;
    }

    public List<Feedback> getAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY f.feedback_date DESC, f.id DESC";
        return query(sql);
    }

    public List<Feedback> getByUser(int userId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE f.user_id = ? ORDER BY f.feedback_date DESC, f.id DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(map(rs));
                }
            }
        }
        return feedbackList;
    }

    public Feedback getById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE f.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Feedback getByIdForUser(int id, int userId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE f.id = ? AND f.user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean insert(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO feedback (user_id, reservation_id, rating, comment, feedback_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, feedback);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Feedback feedback) throws SQLException {
        String sql = "UPDATE feedback SET user_id = ?, reservation_id = ?, rating = ?, comment = ?, feedback_date = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, feedback);
            ps.setInt(6, feedback.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM feedback WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public double averageRating() throws SQLException {
        String sql = "SELECT COALESCE(AVG(rating), 0) FROM feedback";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    private List<Feedback> query(String sql) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                feedbackList.add(map(rs));
            }
        }
        return feedbackList;
    }

    private void bind(PreparedStatement ps, Feedback feedback) throws SQLException {
        ps.setInt(1, feedback.getUserId());
        ps.setInt(2, feedback.getReservationId());
        ps.setInt(3, feedback.getRating());
        ps.setString(4, feedback.getComment());
        ps.setString(5, feedback.getFeedbackDate());
    }
}
