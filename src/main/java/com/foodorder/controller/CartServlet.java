package com.foodorder.controller;

import com.foodorder.dao.CartDao;
import com.foodorder.dao.MenuDao;
import com.foodorder.model.Cart;
import com.foodorder.model.Menu;
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

@WebServlet("/cart/*")
public class CartServlet extends BaseServlet {

    private final CartDao cartDao = new CartDao();
    private final MenuDao menuDao = new MenuDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (action(req).isEmpty()) {
                list(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "/add" -> add(req, resp);
                case "/update" -> update(req, resp);
                case "/delete" -> delete(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        List<Cart> items = cartDao.findByUser(Auth.userId(req));
        BigDecimal total = BigDecimal.ZERO;
        int totalQty = 0;
        boolean hasUnavailable = false;
        for (Cart c : items) {
            total = total.add(c.getSubtotal());
            totalQty += c.getQuantity();
            if (!c.isMenuAvailable()) {
                hasUnavailable = true;
            }
        }
        req.setAttribute("items", items);
        req.setAttribute("total", total);
        req.setAttribute("totalQty", totalQty);
        req.setAttribute("hasUnavailable", hasUnavailable);
        forward(req, resp, "customer/cart");
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Menu m = menuDao.findById(intParam(req, "menuId", 0));
        if (m == null) {
            Flash.error(req, "Menu not found.");
        } else if (!m.isAvailable()) {
            Flash.error(req, "\"" + m.getName() + "\" is sold out and cannot be ordered.");
        } else {
            cartDao.addOrIncrement(Auth.userId(req), m.getId());
            Flash.success(req, "\"" + m.getName() + "\" added to your cart.");
        }
        String back = safeQuery(req.getParameter("back"));
        redirect(req, resp, "/catalog" + (back.isEmpty() ? "" : "?" + back));
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Cart c = ownedCart(req, resp);
        if (c == null) {
            return;
        }
        Integer qty = Validator.toInt(req.getParameter("quantity"));
        if (qty == null || qty < 0 || qty > CartDao.MAX_QUANTITY) {
            Flash.error(req, "Quantity must be a number between 0 and " + CartDao.MAX_QUANTITY + ".");
        } else if (qty > c.getQuantity() && !c.isMenuAvailable()) {
            Flash.error(req, "\"" + c.getMenuName() + "\" is sold out, so its quantity cannot be increased.");
        } else if (!cartDao.updateQuantity(c.getId(), Auth.userId(req), qty)) {
            Flash.error(req, "Cart item not found.");
        } else if (qty == 0) {
            Flash.success(req, "\"" + c.getMenuName() + "\" removed from your cart.");
        } else {
            Flash.success(req, "Quantity of \"" + c.getMenuName() + "\" updated to " + qty + ".");
        }
        redirect(req, resp, "/cart");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Cart c = ownedCart(req, resp);
        if (c == null) {
            return;
        }
        if (cartDao.delete(c.getId(), Auth.userId(req))) {
            Flash.success(req, "\"" + c.getMenuName() + "\" removed from your cart.");
        } else {
            Flash.error(req, "Cart item not found.");
        }
        redirect(req, resp, "/cart");
    }

    private Cart ownedCart(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Cart c = cartDao.findById(intParam(req, "id", 0));
        if (c == null) {
            Flash.error(req, "Cart item not found.");
            redirect(req, resp, "/cart");
            return null;
        }
        if (c.getUserId() != Auth.userId(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return c;
    }
}