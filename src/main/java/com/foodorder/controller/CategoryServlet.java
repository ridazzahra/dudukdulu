package com.foodorder.controller;

import com.foodorder.dao.CategoryDao;
import com.foodorder.model.Category;
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


@WebServlet("/admin/categories/*")
public class CategoryServlet extends BaseServlet {

    private static final int PAGE_SIZE = 10;
    private final CategoryDao dao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "" -> list(req, resp);
                case "/create" -> {
                    req.setAttribute("category", new Category());
                    forward(req, resp, "admin/categories/form");
                }
                case "/edit" -> {
                    Category c = dao.findById(intParam(req, "id", 0));
                    if (c == null) {
                        notFound(req, resp);
                        return;
                    }
                    req.setAttribute("category", c);
                    forward(req, resp, "admin/categories/form");
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

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String q = Validator.trim(req.getParameter("q"));
        Page<Category> pager = dao.findAll(q, intParam(req, "page", 1), PAGE_SIZE);
        req.setAttribute("pager", pager);
        req.setAttribute("q", q);
        req.setAttribute("baseQuery", baseQuery(req, "page"));
        forward(req, resp, "admin/categories/list");
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, int id)
            throws SQLException, ServletException, IOException {
        boolean isNew = id == 0;
        if (!isNew && dao.findById(id) == null) {
            notFound(req, resp);
            return;
        }
        Category c = new Category();
        c.setId(id);
        c.setName(Validator.trim(req.getParameter("name")));

        Validator v = new Validator()
                .required("name", c.getName(), "Category's name")
                .maxLength("name", c.getName(), 255, "Category's name");
        if (!v.hasErrors() && dao.existsByName(c.getName(), id)) {
            v.error("name", "Category name is already taken.");
        }
        if (v.hasErrors()) {
            showFormWithErrors(req, resp, c, v);
            return;
        }
        try {
            if (isNew) {
                dao.create(c);
            } else {
                dao.update(c);
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            v.error("name", "Category name is already taken.");
            showFormWithErrors(req, resp, c, v);
            return;
        }
        Flash.success(req, "Category \"" + c.getName() + "\" has been " + (isNew ? "added." : "updated."));
        redirect(req, resp, "/admin/categories");
    }

    private void showFormWithErrors(HttpServletRequest req, HttpServletResponse resp, Category c, Validator v)
            throws ServletException, IOException {
        req.setAttribute("category", c);
        req.setAttribute("errors", v.getErrors());
        forward(req, resp, "admin/categories/form");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = intParam(req, "id", 0);
        Category c = dao.findById(id);
        if (c == null) {
            Flash.error(req, "Category not found.");
        } else {
            int used = dao.countMenus(id);
            if (used > 0) {
                Flash.error(req, "Category \"" + c.getName() + "\" is still used by " + used
                        + " menu items, so it cannot be deleted. Move or delete the menu items first.");
            } else {
                try {
                    dao.delete(id);
                    Flash.success(req, "Category \"" + c.getName() + "\" has been deleted.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    Flash.error(req, "Category is still in use by other data and cannot be deleted.");
                }
            }
        }
        redirect(req, resp, "/admin/categories");
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Flash.error(req, "Category not found.");
        redirect(req, resp, "/admin/categories");
    }
}