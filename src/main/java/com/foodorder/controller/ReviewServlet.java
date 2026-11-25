package com.foodorder.controller;

import com.foodorder.dao.OrderDao;
import com.foodorder.dao.ReviewDao;
import com.foodorder.model.Order;
import com.foodorder.model.Review;
import com.foodorder.util.Auth;
import com.foodorder.util.Flash;
import com.foodorder.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/reviews/*")
public class ReviewServlet extends BaseServlet {

    private final ReviewDao reviewDao = new ReviewDao();
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "" -> list(req, resp);
                case "/create" -> showCreate(req, resp);
                case "/edit" -> showEdit(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "/create" -> create(req, resp);
                case "/edit" -> edit(req, resp);
                case "/delete" -> delete(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        req.setAttribute("reviews", reviewDao.findByUser(Auth.userId(req)));
        forward(req, resp, "customer/reviews/list");
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        int orderId = intParam(req, "orderId", 0);
        Order order = orderDao.findByIdForUser(orderId, Auth.userId(req));
        if (order == null || !order.isCanBeReviewed()) {
            Flash.error(req, "Only your completed orders that have not been reviewed can be reviewed.");
            redirect(req, resp, "/orders");
            return;
        }
        Review r = new Review();
        r.setOrderId(orderId);
        showForm(req, resp, r, null);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        Review r = reviewDao.findById(intParam(req, "id", 0));
        if (r == null || r.getUserId() != Auth.userId(req)) {
            Flash.error(req, "Review not found.");
            redirect(req, resp, "/reviews");
            return;
        }
        showForm(req, resp, r, null);
    }

    private void create(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        int userId = Auth.userId(req);
        int orderId = intParam(req, "orderId", 0);
        Order order = orderDao.findByIdForUser(orderId, userId);
        if (order == null || !order.isCanBeReviewed()) {
            Flash.error(req, "Only your completed orders that have not been reviewed can be reviewed.");
            redirect(req, resp, "/orders");
            return;
        }
        Review r = new Review();
        r.setUserId(userId);
        r.setOrderId(orderId);
        Validator v = fill(req, r);
        if (v.hasErrors()) {
            showForm(req, resp, r, v);
            return;
        }
        try {
            reviewDao.create(r);
            Flash.success(req, "Thank you! Your review has been saved.");
        } catch (SQLIntegrityConstraintViolationException e) {
            Flash.error(req, "This order has already been reviewed.");
        }
        redirect(req, resp, "/reviews");
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        int userId = Auth.userId(req);
        Review r = reviewDao.findById(intParam(req, "id", 0));
        if (r == null || r.getUserId() != userId) {
            Flash.error(req, "Review not found.");
            redirect(req, resp, "/reviews");
            return;
        }
        Validator v = fill(req, r);
        if (v.hasErrors()) {
            showForm(req, resp, r, v);
            return;
        }
        if (reviewDao.update(r)) {
            Flash.success(req, "Your review has been updated.");
        } else {
            Flash.error(req, "Review not found.");
        }
        redirect(req, resp, "/reviews");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (reviewDao.deleteOwn(intParam(req, "id", 0), Auth.userId(req))) {
            Flash.success(req, "Your review has been deleted.");
        } else {
            Flash.error(req, "Review not found.");
        }
        redirect(req, resp, "/reviews");
    }

    private Validator fill(HttpServletRequest req, Review r) {
        Integer rating = Validator.toInt(req.getParameter("rating"));
        String comment = Validator.trim(req.getParameter("comment"));

        Validator v = new Validator();
        if (rating == null || rating < 1 || rating > 5) {
            v.error("rating", "Please choose a rating from 1 to 5.");
            r.setRating(null);
        } else {
            r.setRating(BigDecimal.valueOf(rating));
        }
        v.required("comment", comment, "Comment")
         .maxLength("comment", comment, 255, "Comment");
        r.setComment(comment);
        return v;
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Review r, Validator v)
            throws ServletException, IOException {
        req.setAttribute("review", r);
        if (v != null) {
            req.setAttribute("errors", v.getErrors());
        }
        forward(req, resp, "customer/reviews/form");
    }
}