package com.foodorder.controller;

import com.foodorder.dao.OrderDao;
import com.foodorder.dao.PaymentMethodDao;
import com.foodorder.dao.ReviewDao;
import com.foodorder.model.Order;
import com.foodorder.model.PaymentMethod;
import com.foodorder.service.OrderService;
import com.foodorder.service.OrderService.OrderException;
import com.foodorder.util.Auth;
import com.foodorder.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/orders/*")
public class OrderServlet extends BaseServlet {

    private final OrderDao orderDao = new OrderDao();
    private final PaymentMethodDao methodDao = new PaymentMethodDao();
    private final OrderService orderService = new OrderService();
    private final ReviewDao reviewDao = new ReviewDao();

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
            if ("/cancel".equals(action(req))) {
                cancel(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        List<Order> orders = orderDao.findByUser(Auth.userId(req));
        req.setAttribute("orders", orders);
        forward(req, resp, "customer/orders");
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        Order order = orderDao.findByIdForUser(intParam(req, "id", 0), Auth.userId(req));
        if (order == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        PaymentMethod method = null;
        if (order.getPayment() != null) {
            method = methodDao.findById(order.getPayment().getPaymentMethodId());
        }
        if (order.isReviewed()) {
            req.setAttribute("review", reviewDao.findByOrderId(order.getId()));
        }
        req.setAttribute("order", order);
        req.setAttribute("method", method);
        forward(req, resp, "customer/order-detail");
    }

    private void cancel(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = intParam(req, "id", 0);
        try {
            orderService.cancelByCustomer(id, Auth.userId(req));
            Flash.success(req, "Order #" + id + " has been cancelled.");
            redirect(req, resp, "/orders/detail?id=" + id);
        } catch (OrderException e) {
            Flash.error(req, e.getMessage());
            redirect(req, resp, "/orders");
        }
    }
}