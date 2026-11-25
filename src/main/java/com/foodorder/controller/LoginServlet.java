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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Auth.isLoggedIn(req)) {
            redirect(req, resp, Auth.homeFor(Auth.currentUser(req)));
            return;
        }
        forward(req, resp, "auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = Validator.trim(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        req.setAttribute("email", email);

        if (email.isEmpty() || password.isEmpty()) {
            Flash.errorNow(req, "Email dan password wajib diisi.");
            forward(req, resp, "auth/login");
            return;
        }

        try {
            User user = userDao.findByEmail(email);
            // pesan sama untuk email tak terdaftar & password salah (tidak membocorkan email mana yang ada)
            if (user == null || !PasswordUtil.verify(password, user.getPassword())) {
                Flash.errorNow(req, "Email atau password salah.");
                forward(req, resp, "auth/login");
                return;
            }
            if (!user.isActive()) {
                Flash.errorNow(req, "Akun Anda dinonaktifkan. Hubungi admin.");
                forward(req, resp, "auth/login");
                return;
            }

            HttpSession old = req.getSession(false);
            String target = old == null ? null : (String) old.getAttribute(Auth.AFTER_LOGIN_KEY);
            Auth.login(req, user);
            req.getSession().removeAttribute(Auth.AFTER_LOGIN_KEY);

            // hanya path internal yang boleh dipakai sebagai tujuan (cegah open redirect)
            boolean safeTarget = target != null && target.startsWith("/") && !target.startsWith("//")
                    && !target.contains("\\");
            Flash.success(req, "Welcome, " + user.getName() + "!");
            redirect(req, resp, safeTarget ? target : Auth.homeFor(user));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
