package com.foodorder.service;

import com.foodorder.dao.CartDao;
import com.foodorder.dao.OrderDao;
import com.foodorder.dao.PaymentDao;
import com.foodorder.dao.PaymentMethodDao;
import com.foodorder.model.Cart;
import com.foodorder.model.DiningTable;
import com.foodorder.model.Order;
import com.foodorder.model.OrderItem;
import com.foodorder.model.OrderStatus;
import com.foodorder.model.Payment;
import com.foodorder.model.PaymentMethod;
import com.foodorder.model.PaymentStatus;
import com.foodorder.util.DbUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    public static final boolean REQUIRE_PAID_BEFORE_COMPLETE = true;

    public static final int MAX_NOTE_LENGTH = 255;

    public static class OrderException extends Exception {
        public OrderException(String message) {
            super(message);
        }
    }

    private final CartDao cartDao = new CartDao();
    private final OrderDao orderDao = new OrderDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final PaymentMethodDao paymentMethodDao = new PaymentMethodDao();

    public int checkout(int userId, int tableId, String note, int paymentMethodId)
            throws OrderException, SQLException {
        String cleanNote = note == null ? "" : note.trim();
        if (cleanNote.length() > MAX_NOTE_LENGTH) {
            throw new OrderException("Note length maximum is " + MAX_NOTE_LENGTH + " character.");
        }
        PaymentMethod method = paymentMethodDao.findById(paymentMethodId);
        if (method == null || !method.isActive()) {
            throw new OrderException("Payment method is unavailable. Please choose other method.");
        }

        return inTransaction(c -> {
            List<Cart> items = cartDao.findByUser(c, userId);
            if (items.isEmpty()) {
                throw new OrderException("Your cart is empty.");
            }
            BigDecimal total = BigDecimal.ZERO;
            for (Cart it : items) {
                if (!it.isMenuAvailable()) {
                    throw new OrderException("\"" + it.getMenuName() + "\" is sold out. You can remove it from cart.");
                }
                total = total.add(it.getSubtotal());
            }

            if (!occupyTable(c, tableId)) {
                throw new OrderException("Table is unavailable. Please choose other table");
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setTableId(tableId);
            order.setTotalPrice(total);
            order.setNote(cleanNote);
            int orderId = orderDao.create(c, order);

            for (Cart it : items) {
                OrderItem oi = new OrderItem();
                oi.setOrderId(orderId);
                oi.setMenuId(it.getMenuId());
                oi.setQuantity(it.getQuantity());
                oi.setPrice(it.getMenuPrice());
                orderDao.createItem(c, oi);
            }

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethodId(paymentMethodId);
            payment.setAmount(total);
            paymentDao.create(c, payment);

            cartDao.clear(c, userId);
            return orderId;
        });
    }

    public void cancelByCustomer(int orderId, int userId) throws OrderException, SQLException {
        inTransaction(c -> {
            Order o = orderDao.findById(c, orderId);
            if (o == null || o.getUserId() != userId) {
                throw new OrderException("Order not found.");
            }
            if (!OrderStatus.canCustomerCancel(o.getStatus())) {
                throw new OrderException("Only Pending orders can be cancelled.");
            }
            cancel(c, o);
            return null;
        });
    }

    public void adminCancel(int orderId) throws OrderException, SQLException {
        inTransaction(c -> {
            Order o = requireOrder(c, orderId);
            if (!OrderStatus.canAdminCancel(o.getStatus())) {
                throw new OrderException("Only Pending or Processing orders can be cancelled.");
            }
            cancel(c, o);
            return null;
        });
    }

    public String advanceStatus(int orderId) throws OrderException, SQLException {
        return inTransaction(c -> {
            Order o = requireOrder(c, orderId);
            String next = OrderStatus.next(o.getStatus());
            if (next == null) {
                throw new OrderException("This order cannot be moved to another status.");
            }
            if (OrderStatus.COMPLETED.equals(next) && REQUIRE_PAID_BEFORE_COMPLETE
                    && (o.getPayment() == null || !o.getPayment().isPaid())) {
                throw new OrderException("Confirm payment before completing order.");
            }
            if (!orderDao.updateStatus(c, orderId, o.getStatus(), next)) {
                throw new OrderException("Order status has changed. Please reload the page.");
            }
            if (OrderStatus.COMPLETED.equals(next)) {
                releaseTable(c, o.getTableId());
            }
            return next;
        });
    }

    public void confirmPayment(int orderId) throws OrderException, SQLException {
        inTransaction(c -> {
            Order o = requireOrder(c, orderId);
            if (OrderStatus.CANCELLED.equals(o.getStatus())) {
                throw new OrderException("This order has been cancelled.");
            }
            Payment p = o.getPayment();
            if (p == null) {
                throw new OrderException("This order has no payment record.");
            }
            if (!PaymentStatus.canConfirm(p.getStatus())) {
                throw new OrderException("This payment has already "
                        + PaymentStatus.label(p.getStatus()).toLowerCase() + ".");
            }
            if (!paymentDao.markPaid(c, orderId)) {
                throw new OrderException("Payment status has changed. Please reload the page.");
            }
            return null;
        });
    }

    private Order requireOrder(Connection c, int orderId) throws OrderException, SQLException {
        Order o = orderDao.findById(c, orderId);
        if (o == null) {
            throw new OrderException("Order not found.");
        }
        return o;
    }

    private void cancel(Connection c, Order o) throws OrderException, SQLException {
        if (!orderDao.updateStatus(c, o.getId(), o.getStatus(), OrderStatus.CANCELLED)) {
            throw new OrderException("Order status has changed. Please reload the page.");
        }
        
        paymentDao.markFailed(c, o.getId());
        releaseTable(c, o.getTableId());
    }

    private boolean occupyTable(Connection c, int tableId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE dining_tables SET status = ? WHERE id = ? AND status = ?")) {
            ps.setString(1, DiningTable.OCCUPIED);
            ps.setInt(2, tableId);
            ps.setString(3, DiningTable.AVAILABLE);
            return ps.executeUpdate() > 0;
        }
    }

    private void releaseTable(Connection c, int tableId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE dining_tables SET status = ? WHERE id = ? AND status = ?")) {
            ps.setString(1, DiningTable.AVAILABLE);
            ps.setInt(2, tableId);
            ps.setString(3, DiningTable.OCCUPIED);
            ps.executeUpdate();
        }
    }

    private interface TxWork<T> {
        T run(Connection c) throws OrderException, SQLException;
    }

    private <T> T inTransaction(TxWork<T> work) throws OrderException, SQLException {
        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (OrderException | SQLException | RuntimeException e) {
                try {
                    c.rollback();
                } catch (SQLException ignored) {
                }
                throw e;
            }
        }
    }
}