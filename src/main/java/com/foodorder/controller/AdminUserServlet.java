package com.foodorder.controller;

import com.foodorder.dao.UserDao;
import com.foodorder.model.User;
import com.foodorder.util.Auth;
import com.foodorder.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet("/admin/users/*")
public class AdminUserServlet extends BaseServlet {

    private static final int PAGE_SIZE = 10;
    private static final String VIEWS = "/WEB-INF/views/admin/users/";
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{8,20}$");

    private final UserDao dao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            switch (subPath(req)) {
                case "/" -> list(req, resp);
                case "/create" -> {
                    User u = new User();
                    u.setRole("customer");
                    showForm(req, resp, u, false);
                }
                case "/edit" -> editForm(req, resp);
                case "/orders" -> orders(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String q = trim(req.getParameter("q"));
        String role = trim(req.getParameter("role"));
        String status = trim(req.getParameter("status"));
        int page = toInt(req.getParameter("page"), 1);

        req.setAttribute("result", dao.search(q, role, status, page, PAGE_SIZE));
        req.setAttribute("q", q);
        req.setAttribute("role", role);
        req.setAttribute("status", status);
        req.setAttribute("meId", Auth.userId(req));
        req.setAttribute("pagerUrl", "/admin/users?q=" + enc(q) + "&role=" + enc(role)
                + "&status=" + enc(status) + "&page=");
        req.getRequestDispatcher(VIEWS + "list.jsp").forward(req, resp);
    }

    private void editForm(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        User u = dao.findById(toInt(req.getParameter("id"), 0));
        if (u == null) { notFound(req, resp); return; }
        showForm(req, resp, u, true);
    }

    private void orders(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        int id = toInt(req.getParameter("id"), 0);
        User u = dao.findById(id);
        if (u == null) { notFound(req, resp); return; }
        req.setAttribute("u", u);
        req.setAttribute("result", dao.orders(id, toInt(req.getParameter("page"), 1), PAGE_SIZE));
        req.setAttribute("pagerUrl", "/admin/users/orders?id=" + id + "&page=");
        req.getRequestDispatcher(VIEWS + "orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            switch (subPath(req)) {
                case "/create" -> create(req, resp);
                case "/edit" -> update(req, resp);
                case "/toggle" -> toggle(req, resp);
                case "/delete" -> delete(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private void create(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        User u = readForm(req);
        String pw = req.getParameter("password");
        Map<String, String> err = validate(u, pw, req.getParameter("password2"), true);
        if (err.isEmpty() && dao.existsByEmail(u.getEmail())) err.put("email", "Email sudah terdaftar.");
        if (err.isEmpty()) {
            try {
                u.setPassword(BCrypt.hashpw(pw, BCrypt.gensalt()));
                u.setActive(true);
                dao.create(u);
                Flash.success(req, "New user added successfully.");
                toList(req, resp);
                return;
            } catch (SQLIntegrityConstraintViolationException e) {
                err.put("email", "Email is already registered.");
            }
        }
        formError(req, resp, u, false, err);
    }

    private void update(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        int id = toInt(req.getParameter("id"), 0);
        User existing = dao.findById(id);
        if (existing == null) { notFound(req, resp); return; }

        User u = readForm(req);
        u.setId(id);
        u.setActive(existing.isActive());

        String pw = req.getParameter("password");
        boolean changePw = pw != null && !pw.isEmpty();
        Map<String, String> err = validate(u, pw, req.getParameter("password2"), changePw);
        if (id == currentId(req) && !existing.getRole().equals(u.getRole())) {
            err.put("role", "You cannot change your own account role.");
        }
        if (err.isEmpty()) {
            User other = dao.findByEmail(u.getEmail());
            if (other != null && other.getId() != id) err.put("email", "Email is already in use by another user.");
        }
        if (err.isEmpty()) {
            try {
                dao.update(id, u.getName(), u.getEmail(), u.getPhone(), u.getRole(),
                        changePw ? BCrypt.hashpw(pw, BCrypt.gensalt()) : null);
                Flash.success(req, "User details updated successfully.");
                toList(req, resp);
                return;
            } catch (SQLIntegrityConstraintViolationException e) {
                err.put("email", "Email is already in use by another user.");
            }
        }
        formError(req, resp, u, true, err);
    }

    private void toggle(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        int id = toInt(req.getParameter("id"), 0);
        boolean makeActive = "1".equals(req.getParameter("active"));
        User u = dao.findById(id);
        if (u == null) {
            Flash.error(req, "User not found.");
        } else if (id == currentId(req)) {
            Flash.error(req, "You cannot deactivate your own account.");
        } else {
            dao.setActive(id, makeActive);
            Flash.success(req, makeActive ? "User activated." : "User deactivated.");
        }
        toList(req, resp);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        int id = toInt(req.getParameter("id"), 0);
        User u = dao.findById(id);
        if (u == null) {
            Flash.error(req, "User not found.");
        } else if (id == currentId(req)) {
            Flash.error(req, "You cannot delete your own account.");
        } else if (dao.countOrders(id) > 0) {
            Flash.error(req, "This user has order history and cannot be deleted. Deactivate the account instead.");
        } else {
            try {
                dao.delete(id);
                Flash.success(req, "User deleted successfully.");
            } catch (SQLIntegrityConstraintViolationException e) {
                Flash.error(req, "This user is referenced by other records and cannot be deleted. Deactivate the account instead.");
            }
        }
        toList(req, resp);
    }

    private Map<String, String> validate(User u, String pw, String pw2, boolean checkPassword) {
        Map<String, String> err = new LinkedHashMap<>();
        if (u.getName().isEmpty()) err.put("name", "Name is required.");
        else if (u.getName().length() < 2 || u.getName().length() > 100)
            err.put("name", "Name must be between 2 and 100 characters.");

        if (u.getEmail().isEmpty()) err.put("email", "Email is required.");
        else if (u.getEmail().length() > 100) err.put("email", "Email must not exceed 100 characters.");
        else if (!EMAIL.matcher(u.getEmail()).matches()) err.put("email", "Invalid email format.");

        if (!u.getPhone().isEmpty() && !PHONE.matcher(u.getPhone()).matches())
            err.put("phone", "Phone number must be 8–20 characters and may contain digits, spaces, +, or -.");

        if (!"admin".equals(u.getRole()) && !"customer".equals(u.getRole()))
            err.put("role", "Role must be admin or customer.");

        if (checkPassword) {
            if (pw == null || pw.isEmpty()) err.put("password", "Password is required.");
            else if (pw.length() < 6) err.put("password", "Password must be at least 6 characters.");
            else if (pw.length() > 72) err.put("password", "Password must not exceed 72 characters.");
            else if (!pw.equals(pw2)) err.put("password2", "Password confirmation does not match.");
        }
        return err;
    }

    private User readForm(HttpServletRequest req) {
        User u = new User();
        u.setName(trim(req.getParameter("name")));
        u.setEmail(trim(req.getParameter("email")).toLowerCase());
        u.setPhone(trim(req.getParameter("phone")));
        u.setRole(trim(req.getParameter("role")));
        return u;
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, User u, boolean isEdit)
            throws ServletException, IOException {
        req.setAttribute("u", u);
        req.setAttribute("isEdit", isEdit);
        req.setAttribute("meId", Auth.userId(req));
        req.getRequestDispatcher(VIEWS + "form.jsp").forward(req, resp);
    }

    private void formError(HttpServletRequest req, HttpServletResponse resp, User u,
                           boolean isEdit, Map<String, String> err) throws ServletException, IOException {
        req.setAttribute("errors", err);
        Flash.errorNow(req, "Please check your entries.");
        showForm(req, resp, u, isEdit);
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Flash.error(req, "User not found.");
        toList(req, resp);
    }

    private void toList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private static int currentId(HttpServletRequest req) {
        return (int) Auth.userId(req);
    }

    private static String subPath(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.equals("/")) return "/";
        return p.endsWith("/") ? p.substring(0, p.length() - 1) : p;
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private static int toInt(String s, int def) {
        try { return Integer.parseInt(s == null ? "" : s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private static String enc(String s) { return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8); }
}