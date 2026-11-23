package com.foodorder.controller;

import com.foodorder.dao.DiningTableDao;
import com.foodorder.model.DiningTable;
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
 * CRUD meja (admin). URL: /admin/tables, /admin/tables/create, /admin/tables/edit?id=, POST /admin/tables/delete
 */
@WebServlet("/admin/tables/*")
public class TableServlet extends BaseServlet {

    private static final int PAGE_SIZE = 10;
    private final DiningTableDao dao = new DiningTableDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "" -> {
                    String q = Validator.trim(req.getParameter("q"));
                    String status = Validator.trim(req.getParameter("status"));
                    if (!DiningTable.STATUSES.containsKey(status)) {
                        status = "";
                    }
                    Page<DiningTable> pager = dao.findAll(q, status, intParam(req, "page", 1), PAGE_SIZE);
                    req.setAttribute("pager", pager);
                    req.setAttribute("q", q);
                    req.setAttribute("status", status);
                    req.setAttribute("statuses", DiningTable.STATUSES);
                    req.setAttribute("baseQuery", baseQuery(req, "page"));
                    forward(req, resp, "admin/tables/list");
                }
                case "/create" -> {
                    req.setAttribute("table", new DiningTable());
                    req.setAttribute("statuses", DiningTable.STATUSES);
                    forward(req, resp, "admin/tables/form");
                }
                case "/edit" -> {
                    DiningTable t = dao.findById(intParam(req, "id", 0));
                    if (t == null) {
                        notFound(req, resp);
                        return;
                    }
                    req.setAttribute("table", t);
                    req.setAttribute("statuses", DiningTable.STATUSES);
                    forward(req, resp, "admin/tables/form");
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
        String numberRaw = Validator.trim(req.getParameter("number"));
        String capacityRaw = Validator.trim(req.getParameter("capacity"));
        String status = Validator.trim(req.getParameter("status"));
        Integer number = Validator.toInt(numberRaw);
        Integer capacity = Validator.toInt(capacityRaw);

        DiningTable t = new DiningTable();
        t.setId(id);
        t.setNumber(number == null ? 0 : number);
        t.setCapacity(capacity == null ? 0 : capacity);
        t.setStatus(status);

        Validator v = new Validator();
        if (number == null || number < 1 || number > 999) {
            v.error("number", "Nomor meja harus angka 1-999.");
        }
        if (capacity == null || capacity < 1 || capacity > 50) {
            v.error("capacity", "Kapasitas harus angka 1-50.");
        }
        if (!DiningTable.STATUSES.containsKey(status)) {
            v.error("status", "Status meja tidak valid.");
        }
        if (!v.getErrors().containsKey("number") && dao.existsNumber(t.getNumber(), id)) {
            v.error("number", "Nomor meja " + t.getNumber() + " sudah ada.");
        }
        if (v.hasErrors()) {
            showFormWithErrors(req, resp, t, v);
            return;
        }
        try {
            if (isNew) {
                dao.create(t);
            } else {
                dao.update(t);
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            v.error("number", "Nomor meja " + t.getNumber() + " sudah ada.");
            showFormWithErrors(req, resp, t, v);
            return;
        }
        Flash.success(req, "Meja " + t.getNumber() + " berhasil " + (isNew ? "ditambahkan." : "diperbarui."));
        redirect(req, resp, "/admin/tables");
    }

    private void showFormWithErrors(HttpServletRequest req, HttpServletResponse resp, DiningTable t, Validator v)
            throws ServletException, IOException {
        req.setAttribute("table", t);
        req.setAttribute("errors", v.getErrors());
        req.setAttribute("statuses", DiningTable.STATUSES);
        forward(req, resp, "admin/tables/form");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        DiningTable t = dao.findById(intParam(req, "id", 0));
        if (t == null) {
            Flash.error(req, "Meja tidak ditemukan.");
        } else {
            int used = dao.countOrders(t.getId());
            if (used > 0) {
                Flash.error(req, "Meja " + t.getNumber() + " sudah dipakai " + used
                        + " order sehingga tidak bisa dihapus. Ubah statusnya saja bila tidak dipakai lagi.");
            } else {
                try {
                    dao.delete(t.getId());
                    Flash.success(req, "Meja " + t.getNumber() + " berhasil dihapus.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    Flash.error(req, "Meja masih dipakai data lain sehingga tidak bisa dihapus.");
                }
            }
        }
        redirect(req, resp, "/admin/tables");
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Flash.error(req, "Meja tidak ditemukan.");
        redirect(req, resp, "/admin/tables");
    }
}
