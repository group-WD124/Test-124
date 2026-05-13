package com.hotel.dao;

import com.hotel.model.Hotel;
import com.hotel.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {
    private Hotel map(ResultSet rs) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setId(rs.getInt("id"));
        hotel.setHotelName(rs.getString("hotel_name"));
        hotel.setLocation(rs.getString("location"));
        hotel.setContactNumber(rs.getString("contact_number"));
        hotel.setEmail(rs.getString("email"));
        hotel.setDescription(rs.getString("description"));
        hotel.setFacilities(rs.getString("facilities"));
        return hotel;
    }

    public List<Hotel> getAll() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels ORDER BY hotel_name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hotels.add(map(rs));
            }
        }
        return hotels;
    }

    public Hotel getById(int id) throws SQLException {
        String sql = "SELECT * FROM hotels WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean insert(Hotel hotel) throws SQLException {
        String sql = "INSERT INTO hotels (hotel_name, location, contact_number, email, description, facilities) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, hotel);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Hotel hotel) throws SQLException {
        String sql = "UPDATE hotels SET hotel_name = ?, location = ?, contact_number = ?, email = ?, "
                + "description = ?, facilities = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, hotel);
            ps.setInt(7, hotel.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM hotels WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM hotels";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void bind(PreparedStatement ps, Hotel hotel) throws SQLException {
        ps.setString(1, hotel.getHotelName());
        ps.setString(2, hotel.getLocation());
        ps.setString(3, hotel.getContactNumber());
        ps.setString(4, hotel.getEmail());
        ps.setString(5, hotel.getDescription());
        ps.setString(6, hotel.getFacilities());
    }
}
