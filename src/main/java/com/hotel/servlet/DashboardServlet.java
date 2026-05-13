package com.hotel.servlet;

import com.hotel.dao.FeedbackDAO;
import com.hotel.dao.HotelDAO;
import com.hotel.dao.PaymentDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.UserDAO;
import com.hotel.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private UserDAO userDAO;
    private HotelDAO hotelDAO;
    private RoomDAO roomDAO;
    private ReservationDAO reservationDAO;
    private PaymentDAO paymentDAO;
    private FeedbackDAO feedbackDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        hotelDAO = new HotelDAO();
        roomDAO = new RoomDAO();
        reservationDAO = new ReservationDAO();
        paymentDAO = new PaymentDAO();
        feedbackDAO = new FeedbackDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("authUser");
        req.setAttribute("isAdmin", user.isAdmin());
        try {
            if (user.isAdmin()) {
                req.setAttribute("userCount", userDAO.countAll());
                req.setAttribute("hotelCount", hotelDAO.countAll());
                req.setAttribute("roomCount", roomDAO.countAll());
                req.setAttribute("availableRoomCount", roomDAO.countByStatus("Available"));
                req.setAttribute("pendingCount", reservationDAO.countByStatus("Pending"));
                req.setAttribute("confirmedCount", reservationDAO.countByStatus("Confirmed"));
                req.setAttribute("cancelledCount", reservationDAO.countByStatus("Cancelled"));
                req.setAttribute("paidCount", paymentDAO.countByStatus("Paid"));
                req.setAttribute("totalRevenue", paymentDAO.totalPaid());
                req.setAttribute("averageRating", feedbackDAO.averageRating());
                req.setAttribute("recentReservations", reservationDAO.getAll());
                req.setAttribute("recentFeedback", feedbackDAO.getAll());
            } else {
                req.setAttribute("availableRooms", roomDAO.getAvailable());
                req.setAttribute("myReservations", reservationDAO.getByUser(user.getId()));
                req.setAttribute("myPayments", paymentDAO.getByUser(user.getId()));
                req.setAttribute("myFeedback", feedbackDAO.getByUser(user.getId()));
            }
        } catch (Exception e) {
            req.setAttribute("dbError", e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, res);
    }
}
