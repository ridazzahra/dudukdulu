package com.foodorder.controller;

import com.foodorder.dao.ReviewDao;
import com.foodorder.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/admin/reviews/*")
public class AdminReviewServlet extends BaseServlet {

    private static final int PAGE_SIZE = 10;
    private final ReviewDao dao = new ReviewDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path != null && !path.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String q = trim(req.getParameter("q"));
        int star = toInt(req.getParameter("star"), 0);
        int page = toInt(req.getParameter("page"), 1);
        String starParam = (star >= 1 && star <= 5) ? String.valueOf(star) : "";

        try {
            req.setAttribute("result", dao.search(q, star, page, PAGE_SIZE));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        req.setAttribute("q", q);
        req.setAttribute("star", star);
        req.setAttribute("pagerUrl", "/admin/reviews?q=" + enc(q) + "&star=" + starParam + "&page=");
        req.getRequestDispatcher("/WEB-INF/views/admin/reviews/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!"/delete".equals(req.getPathInfo())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int id = toInt(req.getParameter("id"), 0);
        try {
            if (id <= 0 || !dao.delete(id)) {
                Flash.error(req, "Review not found.");
            } else {
                Flash.success(req, "Review successfully deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/reviews");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private static int toInt(String s, int def) {
        try { return Integer.parseInt(s == null ? "" : s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
}