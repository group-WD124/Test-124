package com.hotel.servlet;

import com.hotel.dao.HotelDAO;
import com.hotel.model.Hotel;
import com.hotel.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hotels")
public class HotelServlet extends HttpServlet {
    private HotelDAO hotelDAO;

    @Override
    public void init() {
        hotelDAO = new HotelDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!currentUser(req).isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }
        try {
            if ("delete".equals(action)) {
                hotelDAO.delete(parseInt(req.getParameter("id"), 0));
                res.sendRedirect(req.getContextPath() + "/hotels?msg=deleted");
                return;
            }
            Hotel form = new Hotel();
            String mode = "add";
            if ("edit".equals(action)) {
                form = hotelDAO.getById(parseInt(req.getParameter("id"), 0));
                mode = "edit";
                if (form == null) {
                    res.sendRedirect(req.getContextPath() + "/hotels?error=notfound");
                    return;
                }
            }
            req.setAttribute("hotels", hotelDAO.getAll());
            req.setAttribute("form", form);
            req.setAttribute("mode", mode);
            req.getRequestDispatcher("/WEB-INF/views/hotels.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!currentUser(req).isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        req.setCharacterEncoding("UTF-8");
        Hotel hotel = bind(req);
        try {
            if (hotel.getId() > 0) {
                hotelDAO.update(hotel);
                res.sendRedirect(req.getContextPath() + "/hotels?msg=updated");
            } else {
                hotelDAO.insert(hotel);
                res.sendRedirect(req.getContextPath() + "/hotels?msg=added");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Hotel bind(HttpServletRequest req) {
        Hotel hotel = new Hotel();
        hotel.setId(parseInt(req.getParameter("id"), 0));
        hotel.setHotelName(req.getParameter("hotelName"));
        hotel.setLocation(req.getParameter("location"));
        hotel.setContactNumber(req.getParameter("contactNumber"));
        hotel.setEmail(req.getParameter("email"));
        hotel.setDescription(req.getParameter("description"));
        hotel.setFacilities(req.getParameter("facilities"));
        return hotel;
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
