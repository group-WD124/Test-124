package com.hotel.servlet;

import com.hotel.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session != null ? (User) session.getAttribute("authUser") : null;
        if (user != null) {
            chain.doFilter(request, response);
            return;
        }

        res.sendRedirect(contextPath + "/login");
    }

    private boolean isPublicPath(String path) {
        return path.equals("")
                || path.equals("/")
                || path.equals("/index.jsp")
                || path.equals("/login")
                || path.equals("/register")
                || path.startsWith("/css/");
    }
}
