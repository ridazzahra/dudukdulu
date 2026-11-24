package com.foodorder.controller;

import com.foodorder.dao.OrderDao;
import com.foodorder.model.Order;
import com.foodorder.model.OrderStatus;
import com.foodorder.service.OrderService;
import com.foodorder.service.OrderService.OrderException;
import com.foodorder.util.Flash;
import com.foodorder.util.Page;
import com.foodorder.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/admin/orders/*")
public class AdminOrderServlet extends BaseServlet {

    private static final int PAGE_SIZE = 10;
    private static final int MAX_QUERY_LENGTH = 100;

    private final OrderDao orderDao = new OrderDao();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "" -> list(req, resp);
                case "/detail" -> detail(req, resp);
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
                case "/status" -> advance(req, resp);
                case "/cancel" -> cancel(req, resp);
                case "/confirm-payment" -> confirmPayment(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String status = Validator.trim(req.getParameter("status"));
        if (!OrderStatus.isValid(status)) {
            status = "";
        }
        String q = Validator.trim(req.getParameter("q"));
        if (q.length() > MAX_QUERY_LENGTH) {
            q = q.substring(0, MAX_QUERY_LENGTH);
        }
        LocalDate from = parseDate(req.getParameter("from"));
        LocalDate to = parseDate(req.getParameter("to"));
        if (from != null && to != null && from.isAfter(to)) {
            LocalDate tmp = from;
            from = to;
            to = tmp;
        }

        Page<Order> pager = orderDao.findAll(status, from, to, q, intParam(req, "page", 1), PAGE_SIZE);

        req.setAttribute("pager", pager);
        req.setAttribute("statuses", OrderStatus.STATUSES);
        req.setAttribute("counts", orderDao.countByStatus());
        req.setAttribute("status", status);
        req.setAttribute("q", q);
        req.setAttribute("from", from == null ? "" : from.toString());
        req.setAttribute("to", to == null ? "" : to.toString());
        req.setAttribute("baseQuery", baseQuery(req, "page"));
        forward(req, resp, "admin/orders/list");
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        Order order = orderDao.findById(intParam(req, "id", 0));
        if (order == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("order", order);
        forward(req, resp, "admin/orders/detail");
    }

    private void advance(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = intParam(req, "id", 0);
        try {
            String next = orderService.advanceStatus(id);
            Flash.success(req, "Order #" + id + " is now " + OrderStatus.label(next) + ".");
        } catch (OrderException e) {
            Flash.error(req, e.getMessage());
        }
        redirect(req, resp, "/admin/orders/detail?id=" + id);
    }

    private void cancel(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = intParam(req, "id", 0);
        try {
            orderService.adminCancel(id);
            Flash.success(req, "Order #" + id + " has been cancelled.");
        } catch (OrderException e) {
            Flash.error(req, e.getMessage());
        }
        redirect(req, resp, "/admin/orders/detail?id=" + id);
    }

    private void confirmPayment(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = intParam(req, "id", 0);
        try {
            orderService.confirmPayment(id);
            Flash.success(req, "Payment for order #" + id + " has been confirmed.");
        } catch (OrderException e) {
            Flash.error(req, e.getMessage());
        }
        redirect(req, resp, "/admin/orders/detail?id=" + id);
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(s.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}