package com.foodorder.controller;

import com.foodorder.dao.UserDao;
import com.foodorder.model.User;
import com.foodorder.util.Auth;
import com.foodorder.util.Flash;
import com.foodorder.util.PasswordUtil;
import com.foodorder.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Auth.isLoggedIn(req)) {
            redirect(req, resp, Auth.homeFor(Auth.currentUser(req)));
            return;
        }
        forward(req, resp, "auth/register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = Validator.trim(req.getParameter("name"));
        String email = Validator.trim(req.getParameter("email")).toLowerCase();
        String phone = Validator.trim(req.getParameter("phone"));
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirm") == null ? "" : req.getParameter("confirm");

        Validator v = new Validator()
                .required("name", name, "Name").maxLength("name", name, 255, "Name")
                .required("email", email, "Email").maxLength("email", email, 255, "Email").email("email", email)
                .required("phone", phone, "Phone number")
                .matches("phone", phone, "^\\+?[0-9]{8,15}$", "Phone number must be 8-15 digits.")
                .required("password", password, "Password").minLength("password", password, 8, "Password")
                .maxLength("password", password, 72, "Password");
        if (!password.isEmpty() && !password.equals(confirm)) {
            v.error("confirm", "Passwords do not match.");
        }

        try {
            if (!v.getErrors().containsKey("email") && userDao.existsByEmail(email)) {
                v.error("email", "Email is already registered.");
            }
            if (v.hasErrors()) {
                req.setAttribute("errors", v.getErrors());
                req.setAttribute("name", name);
                req.setAttribute("email", email);
                req.setAttribute("phone", phone);
                forward(req, resp, "auth/register");
                return;
            }

            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPhone(phone);
            u.setPassword(PasswordUtil.hash(password));
            u.setRole(User.ROLE_CUSTOMER);
            u.setActive(true);
            try {
                userDao.create(u);
            } catch (SQLIntegrityConstraintViolationException dup) {
                v.error("email", "Email is already registered.");
                req.setAttribute("errors", v.getErrors());
                req.setAttribute("name", name);
                req.setAttribute("email", email);
                req.setAttribute("phone", phone);
                forward(req, resp, "auth/register");
                return;
            }
            Flash.success(req, "Registration successful. Please sign in.");
            redirect(req, resp, "/login");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}