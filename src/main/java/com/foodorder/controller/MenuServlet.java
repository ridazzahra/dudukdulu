package com.foodorder.controller;

import com.foodorder.dao.CategoryDao;
import com.foodorder.dao.MenuDao;
import com.foodorder.model.Menu;
import com.foodorder.util.Flash;
import com.foodorder.util.ImageUploader;
import com.foodorder.util.Page;
import com.foodorder.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/admin/menus/*")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024, fileSizeThreshold = 512 * 1024)
public class MenuServlet extends BaseServlet {

    private static final int PAGE_SIZE = 10;
    private final MenuDao dao = new MenuDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (action(req)) {
                case "" -> list(req, resp);
                case "/create" -> {
                    req.setAttribute("menu", new Menu());
                    showForm(req, resp);
                }
                case "/edit" -> {
                    Menu m = dao.findById(intParam(req, "id", 0));
                    if (m == null) {
                        notFound(req, resp);
                        return;
                    }
                    req.setAttribute("menu", m);
                    showForm(req, resp);
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
        } catch (IllegalStateException tooBig) {
            Flash.error(req, "Upload failed: maximum image size is 2 MB.");
            redirect(req, resp, "/admin/menus");
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String q = Validator.trim(req.getParameter("q"));
        String categoryRaw = Validator.trim(req.getParameter("category"));
        String availability = Validator.trim(req.getParameter("availability"));
        String sort = Validator.trim(req.getParameter("sort"));
        Integer categoryId = Validator.toInt(categoryRaw);
        Boolean avail = availability.equals("1") ? Boolean.TRUE : availability.equals("0") ? Boolean.FALSE : null;

        Page<Menu> pager = dao.findAll(q, categoryId, avail, sort, intParam(req, "page", 1), PAGE_SIZE);
        req.setAttribute("pager", pager);
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("q", q);
        req.setAttribute("category", categoryId == null ? "" : String.valueOf(categoryId));
        req.setAttribute("availability", avail == null ? "" : availability);
        req.setAttribute("sort", sort);
        req.setAttribute("baseQuery", baseQuery(req, "page"));
        req.setAttribute("backQuery", req.getQueryString() == null ? "" : req.getQueryString());
        forward(req, resp, "admin/menus/list");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        req.setAttribute("categories", categoryDao.findAll());
        forward(req, resp, "admin/menus/form");
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, int id)
            throws SQLException, ServletException, IOException {
        boolean isNew = id == 0;
        Menu existing = isNew ? null : dao.findById(id);
        if (!isNew && existing == null) {
            notFound(req, resp);
            return;
        }

        Menu m = new Menu();
        m.setId(id);
        m.setName(Validator.trim(req.getParameter("name")));
        m.setDescription(Validator.trim(req.getParameter("description")));
        m.setAvailable(req.getParameter("available") != null);
        m.setImageLink(isNew ? ImageUploader.DEFAULT_IMAGE : existing.getImageLink());
        Integer categoryId = Validator.toInt(req.getParameter("categoryId"));
        m.setCategoryId(categoryId == null ? 0 : categoryId);
        String priceRaw = Validator.trim(req.getParameter("price"));
        BigDecimal price = Validator.toMoney(priceRaw);
        m.setPrice(price);

        Validator v = new Validator()
                .required("name", m.getName(), "Menu name").maxLength("name", m.getName(), 255, "Menu name")
                .maxLength("description", m.getDescription(), 255, "Description");
        if (categoryId == null || categoryDao.findById(categoryId) == null) {
            v.error("categoryId", "Please select a valid category.");
        }
        if (priceRaw.isEmpty()) {
            v.error("price", "Price is required.");
        } else if (price == null) {
            v.error("price", "Price must be a number (up to 2 decimal places), without thousands separators.");
        }

        Part image = getImagePart(req);
        if (image != null) {
            String imgError = ImageUploader.validate(image);
            if (imgError != null) {
                v.error("image", imgError);
            }
        }
        if (v.hasErrors()) {
            req.setAttribute("menu", m);
            req.setAttribute("errors", v.getErrors());
            showForm(req, resp);
            return;
        }

        String oldImage = m.getImageLink();
        String newImage = null;
        if (image != null) {
            newImage = ImageUploader.save(image);
            m.setImageLink(newImage);
        }
        try {
            if (isNew) {
                dao.create(m);
            } else {
                dao.update(m);
            }
        } catch (SQLException e) {
            if (newImage != null) {
                ImageUploader.delete(newImage); 
            }
            throw e;
        }
        if (newImage != null && !isNew) {
            ImageUploader.delete(oldImage); 
        }
        Flash.success(req, "Menu \"" + m.getName() + "\" has been " + (isNew ? "added." : "updated."));
        redirect(req, resp, "/admin/menus");
    }

    private Part getImagePart(HttpServletRequest req) throws IOException, ServletException {
        Part p = req.getPart("image");
        return ImageUploader.hasFile(p) ? p : null;
    }

    private void toggle(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Menu m = dao.findById(intParam(req, "id", 0));
        if (m == null) {
            Flash.error(req, "Menu item not found.");
        } else {
            dao.toggleAvailability(m.getId());
            Flash.success(req, "\"" + m.getName() + "\" is now " + (m.isAvailable() ? "sold out." : "available."));
        }
        String back = safeQuery(req.getParameter("back"));
        redirect(req, resp, "/admin/menus" + (back.isEmpty() ? "" : "?" + back));
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        Menu m = dao.findById(intParam(req, "id", 0));
        if (m == null) {
            Flash.error(req, "Menu item not found.");
        } else {
            int used = dao.countInOrders(m.getId());
            if (used > 0) {
                Flash.error(req, "Menu \"" + m.getName() + "\" is already in " + used
                        + " order items, so it cannot be deleted. Mark it as sold out instead.");
            } else {
                try {
                    dao.delete(m.getId());
                    ImageUploader.delete(m.getImageLink());
                    Flash.success(req, "Menu \"" + m.getName() + "\" has been deleted.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    Flash.error(req, "Menu item is still used by other data and cannot be deleted. Mark it as sold out instead.");
                }
            }
        }
        redirect(req, resp, "/admin/menus");
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Flash.error(req, "Menu item not found.");
        redirect(req, resp, "/admin/menus");
    }
}