-- Hotel Management System Database Schema
-- Run this script in MySQL before starting the application.

CREATE DATABASE IF NOT EXISTS hotel_reservation
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hotel_reservation;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS hotels;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    password_hash VARCHAR(64) NOT NULL,
    role          ENUM('Admin', 'User') NOT NULL DEFAULT 'User',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hotels (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    hotel_name     VARCHAR(120) NOT NULL,
    location       VARCHAR(160) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email          VARCHAR(120) NOT NULL,
    description    TEXT,
    facilities     TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    room_number     VARCHAR(20) NOT NULL UNIQUE,
    room_type       VARCHAR(40) NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    status          ENUM('Available', 'Booked', 'Maintenance') NOT NULL DEFAULT 'Available',
    description     TEXT,
    image           VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL,
    room_id          INT NOT NULL,
    check_in         DATE NOT NULL,
    check_out        DATE NOT NULL,
    number_of_guests INT NOT NULL DEFAULT 1,
    status           ENUM('Pending', 'Confirmed', 'Cancelled') NOT NULL DEFAULT 'Pending',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE payments (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    user_id        INT NOT NULL,
    amount         DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(40) NOT NULL,
    payment_date   DATE NOT NULL,
    status         ENUM('Pending', 'Paid', 'Failed', 'Refunded') NOT NULL DEFAULT 'Pending',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE feedback (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    reservation_id INT NOT NULL,
    rating         INT NOT NULL,
    comment        TEXT,
    feedback_date  DATE NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_feedback_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

INSERT INTO users (name, email, phone, password_hash, role) VALUES
('System Admin', 'admin@hotel.com', '0710000000', SHA2('admin123', 256), 'Admin'),
('Lakvin Perera', 'user@hotel.com', '0712345678', SHA2('user123', 256), 'User'),
('Anita Silva', 'anita@example.com', '0771122334', SHA2('user123', 256), 'User');

INSERT INTO hotels (hotel_name, location, contact_number, email, description, facilities) VALUES
('HotelReserve Grand', 'Colombo, Sri Lanka', '0112456789', 'info@hotelreserve.lk',
 'A city hotel for business stays, family trips, and weekend breaks.',
 'Pool, restaurant, free Wi-Fi, airport shuttle, conference hall');

INSERT INTO rooms (room_number, room_type, price_per_night, status, description, image) VALUES
('101', 'Single', 2200.00, 'Available', 'Compact room with a city view and workspace.', 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=900&q=80'),
('102', 'Double', 3800.00, 'Available', 'Comfortable double room for two guests.', 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=900&q=80'),
('201', 'Deluxe', 5600.00, 'Booked', 'Spacious deluxe room with balcony seating.', 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=900&q=80'),
('301', 'Suite', 8200.00, 'Available', 'Premium suite with lounge area and upgraded amenities.', 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=900&q=80'),
('401', 'Double', 3800.00, 'Maintenance', 'Temporarily unavailable while maintenance is completed.', 'https://images.unsplash.com/photo-1560185007-cde436f6a4d0?auto=format&fit=crop&w=900&q=80');

INSERT INTO reservations (user_id, room_id, check_in, check_out, number_of_guests, status) VALUES
(2, 3, '2026-05-20', '2026-05-23', 2, 'Confirmed');

INSERT INTO payments (reservation_id, user_id, amount, payment_method, payment_date, status) VALUES
(1, 2, 16800.00, 'Card', '2026-05-13', 'Paid');

INSERT INTO feedback (user_id, reservation_id, rating, comment, feedback_date) VALUES
(2, 1, 5, 'Smooth booking process and friendly service.', '2026-05-13');
