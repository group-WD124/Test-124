package com.hotel.servlet;

import com.hotel.dao.ReservationDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.UserDAO;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/reservations")
public class ReservationServlet extends HttpServlet {
    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        reservationDAO = new ReservationDAO();
        roomDAO = new RoomDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = currentUser(req);
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "add":
                    showForm(req, res, user, new Reservation(), "add", null);
                    break;
                case "edit":
                    Reservation toEdit = findReservation(req, user);
                    if (toEdit == null) {
                        res.sendRedirect(req.getContextPath() + "/reservations?error=notfound");
                        return;
                    }
                    showForm(req, res, user, toEdit, "edit", null);
                    break;
                case "cancel":
                    Reservation toCancel = findReservation(req, user);
                    if (toCancel != null) {
                        reservationDAO.cancel(toCancel.getId());
                    }
                    res.sendRedirect(req.getContextPath() + "/reservations?msg=cancelled");
                    break;
                case "delete":
                    if (!user.isAdmin()) {
                        res.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                    reservationDAO.delete(parseInt(req.getParameter("id"), 0));
                    res.sendRedirect(req.getContextPath() + "/reservations?msg=deleted");
                    break;
                default:
                    list(req, res, user);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = currentUser(req);
        Reservation reservation = bind(req, user);

        try {
            Reservation existing = reservation.getId() > 0 ? findReservation(req, user) : null;
            String roomError = validateRoom(reservation, existing);
            String dateError = validateDates(reservation);
            if (roomError != null || dateError != null) {
                showForm(req, res, user, reservation, reservation.getId() > 0 ? "edit" : "add",
                        roomError != null ? roomError : dateError);
                return;
            }

            if (reservation.getId() > 0) {
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                if (!user.isAdmin()) {
                    reservation.setUserId(user.getId());
                    reservation.setStatus(existing.getStatus());
                }
                reservationDAO.update(reservation);
                res.sendRedirect(req.getContextPath() + "/reservations?msg=updated");
            } else {
                reservationDAO.insert(reservation);
                res.sendRedirect(req.getContextPath() + "/reservations?msg=added");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse res, User user)
            throws Exception {
        if (user.isAdmin()) {
            req.setAttribute("reservations", reservationDAO.getAll());
        } else {
            req.setAttribute("reservations", reservationDAO.getByUser(user.getId()));
        }
        req.getRequestDispatcher("/WEB-INF/views/reservations.jsp").forward(req, res);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse res, User user,
                          Reservation reservation, String mode, String error)
            throws Exception {
        int roomId = parseInt(req.getParameter("roomId"), reservation.getRoomId());
        if (reservation.getRoomId() == 0 && roomId > 0) {
            reservation.setRoomId(roomId);
        }
        if (reservation.getStatus() == null) {
            reservation.setStatus("Pending");
        }
        if (reservation.getNumberOfGuests() == 0) {
            reservation.setNumberOfGuests(1);
        }
        req.setAttribute("form", reservation);
        req.setAttribute("mode", mode);
        req.setAttribute("formError", error);
        req.setAttribute("rooms", roomDAO.getAll());
        if (user.isAdmin()) {
            req.setAttribute("users", userDAO.getAllUsers());
        }
        req.getRequestDispatcher("/WEB-INF/views/reservation-form.jsp").forward(req, res);
    }

    private Reservation bind(HttpServletRequest req, User user) {
        Reservation reservation = new Reservation();
        reservation.setId(parseInt(req.getParameter("id"), 0));
        reservation.setUserId(user.isAdmin() ? parseInt(req.getParameter("userId"), user.getId()) : user.getId());
        reservation.setRoomId(parseInt(req.getParameter("roomId"), 0));
        reservation.setCheckIn(req.getParameter("checkIn"));
        reservation.setCheckOut(req.getParameter("checkOut"));
        reservation.setNumberOfGuests(parseInt(req.getParameter("numberOfGuests"), 1));
        reservation.setStatus(user.isAdmin() ? req.getParameter("status") : "Pending");
        return reservation;
    }

    private Reservation findReservation(HttpServletRequest req, User user) throws Exception {
        int id = parseInt(req.getParameter("id"), 0);
        return user.isAdmin() ? reservationDAO.getById(id) : reservationDAO.getByIdForUser(id, user.getId());
    }

    private String validateRoom(Reservation reservation, Reservation existing) throws Exception {
        Room room = roomDAO.getById(reservation.getRoomId());
        if (room == null) {
            return "Select a valid room.";
        }
        boolean sameRoom = existing != null && existing.getRoomId() == reservation.getRoomId();
        if (!room.isAvailable() && !sameRoom) {
            return "That room is not available for a new reservation.";
        }
        return null;
    }

    private String validateDates(Reservation reservation) {
        try {
            LocalDate checkIn = LocalDate.parse(reservation.getCheckIn());
            LocalDate checkOut = LocalDate.parse(reservation.getCheckOut());
            if (!checkOut.isAfter(checkIn)) {
                return "Check-out date must be after check-in date.";
            }
        } catch (Exception e) {
            return "Enter valid check-in and check-out dates.";
        }
        if (reservation.getNumberOfGuests() < 1) {
            return "Number of guests must be at least 1.";
        }
        return null;
    }

    private User currentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("authUser");
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isEmpty() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
