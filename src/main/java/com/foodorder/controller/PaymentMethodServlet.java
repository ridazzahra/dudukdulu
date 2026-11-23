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

/**
 * CRUD metode pembayaran (admin).
 * URL: /admin/payment-methods, /create, /edit?id=, POST /delete, POST /toggle (aktif/nonaktif)
 */
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
                .required("name", m.getName(), "Nama metode").maxLength("name", m.getName(), 50, "Nama metode")
                .maxLength("note", m.getNote(), 255, "Instruksi");
        if (!PaymentMethod.TYPES.containsKey(m.getType())) {
            v.error("type", "Jenis pembayaran tidak valid.");
        }
        if (!v.getErrors().containsKey("name") && dao.existsByName(m.getName(), id)) {
            v.error("name", "Nama metode sudah dipakai.");
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
            v.error("name", "Nama metode sudah dipakai.");
            showFormWithErrors(req, resp, m, v);
            return;
        }
        Flash.success(req, "Metode pembayaran \"" + m.getName() + "\" berhasil " + (isNew ? "ditambahkan." : "diperbarui."));
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
            Flash.error(req, "Metode pembayaran tidak ditemukan.");
        } else {
            dao.toggleActive(m.getId());
            Flash.success(req, "\"" + m.getName() + "\" sekarang " + (m.isActive() ? "nonaktif." : "aktif."));
        }
        redirect(req, resp, "/admin/payment-methods" + backQuery(req));
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        PaymentMethod m = dao.findById(intParam(req, "id", 0));
        if (m == null) {
            Flash.error(req, "Metode pembayaran tidak ditemukan.");
        } else {
            int used = dao.countPayments(m.getId());
            if (used > 0) {
                Flash.error(req, "\"" + m.getName() + "\" sudah dipakai " + used
                        + " pembayaran sehingga tidak bisa dihapus. Nonaktifkan saja.");
            } else {
                try {
                    dao.delete(m.getId());
                    Flash.success(req, "Metode pembayaran \"" + m.getName() + "\" berhasil dihapus.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    Flash.error(req, "Metode masih dipakai data lain sehingga tidak bisa dihapus. Nonaktifkan saja.");
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
        Flash.error(req, "Metode pembayaran tidak ditemukan.");
        redirect(req, resp, "/admin/payment-methods");
    }
}
