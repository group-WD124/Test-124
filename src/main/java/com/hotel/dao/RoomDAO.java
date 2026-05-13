package com.hotel.dao;

import com.hotel.model.Room;
import com.hotel.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private Room map(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setPricePerNight(rs.getDouble("price_per_night"));
        room.setStatus(rs.getString("status"));
        room.setDescription(rs.getString("description"));
        room.setImage(rs.getString("image"));
        return room;
    }

    public List<Room> getAll() throws SQLException {
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        return query(sql);
    }

    public List<Room> getAvailable() throws SQLException {
        String sql = "SELECT * FROM rooms WHERE status = 'Available' ORDER BY price_per_night, room_number";
        return query(sql);
    }

    public Room getById(int id) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean insert(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, status, description, image) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, room);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, price_per_night = ?, status = ?, "
                + "description = ?, image = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, room);
            ps.setInt(7, room.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE status = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private List<Room> query(String sql) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rooms.add(map(rs));
            }
        }
        return rooms;
    }

    private void bind(PreparedStatement ps, Room room) throws SQLException {
        ps.setString(1, room.getRoomNumber());
        ps.setString(2, room.getRoomType());
        ps.setDouble(3, room.getPricePerNight());
        ps.setString(4, room.getStatus());
        ps.setString(5, room.getDescription());
        ps.setString(6, room.getImage());
    }
}
