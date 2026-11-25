package com.foodorder.controller;

import com.foodorder.dao.PaymentMethodDao;
import com.foodorder.model.PaymentMethod;
import com.foodorder.util.Flash;
import com.foodorder.util.Page;
import com.foodorder.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/admin/payment-methods/*")
public class PaymentMethodServlet extends BaseServlet {

    private static final int PAGE_SIZE = 10;
    private final PaymentMethodDao dao = new PaymentMethodDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "" -> {
                    String q = Validator.trim(req.getParameter("q"));
                    Page<PaymentMethod> pager = dao.findAll(q, intParam(req, "page", 1), PAGE_SIZE);
                    req.setAttribute("pager", pager);
                    req.setAttribute("q", q);
                    req.setAttribute("baseQuery", baseQuery(req, "page"));
                    forward(req, resp, "admin/payment-methods/list");
                }
                case "/create" -> {
                    req.setAttribute("method", new PaymentMethod());
                    req.setAttribute("types", PaymentMethod.TYPES);
                    forward(req, resp, "admin/payment-methods/form");
                }
                case "/edit" -> {
                    PaymentMethod m = dao.findById(intParam(req, "id", 0));
                    if (m == null) {
                        notFound(req, resp);
                        return;
                    }
                    req.setAttribute("method", m);
                    req.setAttribute("types", PaymentMethod.TYPES);
                    forward(req, resp, "admin/payment-methods/form");
                }
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
                case "/create" -> save(req, resp, 0);
                case "/edit" -> save(req, resp, intParam(req, "id", 0));
                case "/delete" -> delete(req, resp);
                case "/toggle" -> toggle(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, int id)
            throws SQLException, ServletException, IOException {
        boolean isNew = id == 0;
        if (!isNew && dao.findById(id) == null) {
            notFound(req, resp);
            return;
        }
        PaymentMethod m = new PaymentMethod();
        m.setId(id);
        m.setName(Validator.trim(req.getParameter("name")));
        m.setType(Validator.trim(req.getParameter("type")));
        m.setNote(Validator.trim(req.getParameter("note")));
        m.setActive(req.getParameter("active") != null);

        Validator v = new Validator()
                .required("name", m.getName(), "Method name").maxLength("name", m.getName(), 50, "Method name")
                .maxLength("note", m.getNote(), 255, "Instructions");
        if (!PaymentMethod.TYPES.containsKey(m.getType())) {
            v.error("type", "Invalid payment type.");
        }
        if (!v.getErrors().containsKey("name") && dao.existsByName(m.getName(), id)) {
            v.error("name", "This method name is already in use.");
        }
        if (v.hasErrors()) {
            showFormWithErrors(req, resp, m, v);
            return;
        }
        try {
            if (isNew) {
                dao.create(m);
            } else {
                dao.update(m);
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            v.error("name", "This method name is already in use.");
            showFormWithErrors(req, resp, m, v);
            return;
        }
        Flash.success(req, "Payment method \"" + m.getName() + "\" was " + (isNew ? "added." : "updated."));
        redirect(req, resp, "/admin/payment-methods");
    }

    private void showFormWithErrors(HttpServletRequest req, HttpServletResponse resp, PaymentMethod m, Validator v)
            throws ServletException, IOException {
        req.setAttribute("method", m);
        req.setAttribute("errors", v.getErrors());
        req.setAttribute("types", PaymentMethod.TYPES);
        forward(req, resp, "admin/payment-methods/form");
    }

    private void toggle(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        PaymentMethod m = dao.findById(intParam(req, "id", 0));
        if (m == null) {
            Flash.error(req, "Payment method not found.");
        } else {
            dao.toggleActive(m.getId());
            Flash.success(req, "\"" + m.getName() + "\" is now " + (m.isActive() ? "inactive." : "active."));
        }
        redirect(req, resp, "/admin/payment-methods" + backQuery(req));
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        PaymentMethod m = dao.findById(intParam(req, "id", 0));
        if (m == null) {
            Flash.error(req, "Payment method not found.");
        } else {
            int used = dao.countPayments(m.getId());
            if (used > 0) {
                Flash.error(req, "\"" + m.getName() + "\" is used by " + used
                        + " payment(s), so it cannot be deleted. Deactivate it instead.");
            } else {
                try {
                    dao.delete(m.getId());
                    Flash.success(req, "Payment method \"" + m.getName() + "\" was deleted.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    Flash.error(req, "This method is still used by other data, so it cannot be deleted. Deactivate it instead.");
                }
            }
        }
        redirect(req, resp, "/admin/payment-methods");
    }

    private String backQuery(HttpServletRequest req) {
        String b = safeQuery(req.getParameter("back"));
        return b.isEmpty() ? "" : "?" + b;
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Flash.error(req, "Payment method not found.");
        redirect(req, resp, "/admin/payment-methods");
    }
}