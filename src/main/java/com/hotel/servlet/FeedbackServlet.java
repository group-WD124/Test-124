package com.hotel.servlet;

import com.hotel.dao.FeedbackDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.model.Feedback;
import com.hotel.model.Reservation;
import com.hotel.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {
    private FeedbackDAO feedbackDAO;
    private ReservationDAO reservationDAO;

    @Override
    public void init() {
        feedbackDAO = new FeedbackDAO();
        reservationDAO = new ReservationDAO();
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
                    if (user.isAdmin()) {
                        res.sendRedirect(req.getContextPath() + "/feedback");
                        return;
                    }
                    Feedback feedback = new Feedback();
                    feedback.setFeedbackDate(LocalDate.now().toString());
                    feedback.setRating(5);
                    feedback.setReservationId(parseInt(req.getParameter("reservationId"), 0));
                    showForm(req, res, user, feedback, "add", null);
                    break;
                case "edit":
                    Feedback toEdit = findFeedback(req, user);
                    if (toEdit == null || user.isAdmin()) {
                        res.sendRedirect(req.getContextPath() + "/feedback?error=notfound");
                        return;
                    }
                    showForm(req, res, user, toEdit, "edit", null);
                    break;
                case "delete":
                    Feedback toDelete = findFeedback(req, user);
                    if (toDelete != null) {
                        feedbackDAO.delete(toDelete.getId());
                    }
                    res.sendRedirect(req.getContextPath() + "/feedback?msg=deleted");
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
        if (user.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            Feedback feedback = bind(req, user);
            Reservation reservation = reservationDAO.getByIdForUser(feedback.getReservationId(), user.getId());
            if (reservation == null) {
                showForm(req, res, user, feedback, feedback.getId() > 0 ? "edit" : "add",
                        "Select one of your reservations.");
                return;
            }
            if (feedback.getId() > 0) {
                Feedback existing = feedbackDAO.getByIdForUser(feedback.getId(), user.getId());
                if (existing == null) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                feedbackDAO.update(feedback);
                res.sendRedirect(req.getContextPath() + "/feedback?msg=updated");
            } else {
                feedbackDAO.insert(feedback);
                res.sendRedirect(req.getContextPath() + "/feedback?msg=added");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse res, User user)
            throws Exception {
        req.setAttribute("feedbackList", user.isAdmin() ? feedbackDAO.getAll() : feedbackDAO.getByUser(user.getId()));
        req.getRequestDispatcher("/WEB-INF/views/feedback.jsp").forward(req, res);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse res, User user,
                          Feedback feedback, String mode, String error)
            throws Exception {
        req.setAttribute("form", feedback);
        req.setAttribute("mode", mode);
        req.setAttribute("formError", error);
        req.setAttribute("reservations", reservationDAO.getByUser(user.getId()));
        req.setAttribute("feedbackList", feedbackDAO.getByUser(user.getId()));
        req.getRequestDispatcher("/WEB-INF/views/feedback.jsp").forward(req, res);
    }

    private Feedback bind(HttpServletRequest req, User user) {
        Feedback feedback = new Feedback();
        feedback.setId(parseInt(req.getParameter("id"), 0));
        feedback.setUserId(user.getId());
        feedback.setReservationId(parseInt(req.getParameter("reservationId"), 0));
        feedback.setRating(Math.min(5, Math.max(1, parseInt(req.getParameter("rating"), 5))));
        feedback.setComment(req.getParameter("comment"));
        feedback.setFeedbackDate(defaultString(req.getParameter("feedbackDate"), LocalDate.now().toString()));
        return feedback;
    }

    private Feedback findFeedback(HttpServletRequest req, User user) throws Exception {
        int id = parseInt(req.getParameter("id"), 0);
        return user.isAdmin() ? feedbackDAO.getById(id) : feedbackDAO.getByIdForUser(id, user.getId());
    }

    private User currentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("authUser");
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isEmpty() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
