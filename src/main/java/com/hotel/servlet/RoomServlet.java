package com.hotel.servlet;

import com.hotel.dao.RoomDAO;
import com.hotel.model.Room;
import com.hotel.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/rooms")
public class RoomServlet extends HttpServlet {
    private RoomDAO roomDAO;

    @Override
    public void init() {
        roomDAO = new RoomDAO();
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
            if ("delete".equals(action)) {
                if (!requireAdmin(user, res)) {
                    return;
                }
                roomDAO.delete(parseInt(req.getParameter("id"), 0));
                res.sendRedirect(req.getContextPath() + "/rooms?msg=deleted");
                return;
            }

            Room form = new Room();
            form.setStatus("Available");
            String mode = "add";
            if ("edit".equals(action)) {
                if (!requireAdmin(user, res)) {
                    return;
                }
                form = roomDAO.getById(parseInt(req.getParameter("id"), 0));
                mode = "edit";
                if (form == null) {
                    res.sendRedirect(req.getContextPath() + "/rooms?error=notfound");
                    return;
                }
            } else if ("add".equals(action)) {
                if (!requireAdmin(user, res)) {
                    return;
                }
            }

            req.setAttribute("rooms", roomDAO.getAll());
            req.setAttribute("form", form);
            req.setAttribute("mode", mode);
            req.getRequestDispatcher("/WEB-INF/views/rooms.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (!requireAdmin(user, res)) {
            return;
        }
        req.setCharacterEncoding("UTF-8");
        Room room = bind(req);
        try {
            if (room.getId() > 0) {
                roomDAO.update(room);
                res.sendRedirect(req.getContextPath() + "/rooms?msg=updated");
            } else {
                roomDAO.insert(room);
                res.sendRedirect(req.getContextPath() + "/rooms?msg=added");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Room bind(HttpServletRequest req) {
        Room room = new Room();
        room.setId(parseInt(req.getParameter("id"), 0));
        room.setRoomNumber(req.getParameter("roomNumber"));
        room.setRoomType(req.getParameter("roomType"));
        room.setPricePerNight(parseDouble(req.getParameter("pricePerNight"), 0));
        room.setStatus(req.getParameter("status"));
        room.setDescription(req.getParameter("description"));
        room.setImage(req.getParameter("image"));
        return room;
    }

    private boolean requireAdmin(User user, HttpServletResponse res) throws IOException {
        if (!user.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
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

    private double parseDouble(String value, double fallback) {
        try {
            return value == null || value.isEmpty() ? fallback : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
