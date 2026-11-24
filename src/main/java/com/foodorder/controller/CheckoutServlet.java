package com.foodorder.controller;

import com.foodorder.dao.CartDao;
import com.foodorder.dao.DiningTableDao;
import com.foodorder.dao.PaymentMethodDao;
import com.foodorder.model.Cart;
import com.foodorder.service.OrderService;
import com.foodorder.service.OrderService.OrderException;
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
import java.util.List;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutServlet extends BaseServlet {

    private final CartDao cartDao = new CartDao();
    private final DiningTableDao tableDao = new DiningTableDao();
    private final PaymentMethodDao methodDao = new PaymentMethodDao();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            showForm(req, resp, null, null, "", null);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Integer tableId = Validator.toInt(req.getParameter("tableId"));
            Integer methodId = Validator.toInt(req.getParameter("paymentMethodId"));
            String note = Validator.trim(req.getParameter("note"));

            Validator v = new Validator();
            if (tableId == null) {
                v.error("tableId", "Please select a table.");
            }
            if (methodId == null) {
                v.error("paymentMethodId", "Please select a payment method.");
            }
            if (note.length() > OrderService.MAX_NOTE_LENGTH) {
                v.error("note", "Note must be at most " + OrderService.MAX_NOTE_LENGTH + " characters.");
            }
            if (v.hasErrors()) {
                showForm(req, resp, tableId, methodId, note, v.getErrors());
                return;
            }

            try {
                int orderId = orderService.checkout(Auth.userId(req), tableId, note, methodId);
                Flash.success(req, "Order #" + orderId + " placed successfully. Please pay with your selected method.");
                redirect(req, resp, "/orders");
            } catch (OrderException e) {
                Flash.errorNow(req, e.getMessage());
                showForm(req, resp, tableId, methodId, note, null);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Integer tableId, Integer methodId,
                          String note, Map<String, String> errors)
            throws SQLException, ServletException, IOException {
        List<Cart> items = cartDao.findByUser(Auth.userId(req));
        if (items.isEmpty()) {
            Flash.error(req, "Your cart is empty.");
            redirect(req, resp, "/cart");
            return;
        }
        BigDecimal total = BigDecimal.ZERO;
        int totalQty = 0;
        for (Cart c : items) {
            if (!c.isMenuAvailable()) {
                Flash.error(req, "\"" + c.getMenuName() + "\" is sold out. Remove it from your cart before checking out.");
                redirect(req, resp, "/cart");
                return;
            }
            total = total.add(c.getSubtotal());
            totalQty += c.getQuantity();
        }
        req.setAttribute("items", items);
        req.setAttribute("total", total);
        req.setAttribute("totalQty", totalQty);
        req.setAttribute("tables", tableDao.findAvailable());
        req.setAttribute("paymentMethods", methodDao.findAllActive());
        req.setAttribute("selectedTable", tableId);
        req.setAttribute("selectedMethod", methodId);
        req.setAttribute("note", note);
        req.setAttribute("errors", errors);
        forward(req, resp, "customer/checkout");
    }
}