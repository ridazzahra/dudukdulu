package com.foodorder.filter;

import com.foodorder.dao.UserDao;
import com.foodorder.model.User;
import com.foodorder.util.Auth;
import com.foodorder.util.Flash;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AuthFilter implements Filter {

    private final UserDao userDao = new UserDao();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (isPublic(path)) {
            chain.doFilter(req, resp);
            return;
        }

        User sessionUser = Auth.currentUser(req);
        if (sessionUser != null) {
            try {
                User fresh = userDao.findById(sessionUser.getId());
                if (fresh != null && fresh.isActive()) {
                    Auth.refresh(req, fresh);
                    chain.doFilter(req, resp);
                    return;
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            Auth.logout(req);
            Flash.error(req, "Your session has expired or your account is inactive. Please sign in again.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("GET".equals(req.getMethod())) {
            String target = path + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
            req.getSession().setAttribute(Auth.AFTER_LOGIN_KEY, target);
        }
        Flash.error(req, "Please sign in first.");
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private boolean isPublic(String path) {
        return path.isEmpty() || path.equals("/") || path.equals("/index.jsp")
                || path.equals("/home") || path.equals("/login") || path.equals("/register")
                || path.equals("/logout") || path.equals("/favicon.ico")
                || path.startsWith("/.well-known/")
                || path.startsWith("/assets/") || path.startsWith("/uploads/");
    }
}