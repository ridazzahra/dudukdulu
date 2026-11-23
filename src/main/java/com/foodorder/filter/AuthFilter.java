package com.foodorder.filter;

import com.foodorder.dao.UserDao;
import com.foodorder.model.User;
import com.foodorder.util.Auth;
import com.foodorder.util.Flash;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Semua halaman wajib login kecuali yang publik (lihat isPublic).
 * Setiap request, data user dibaca ulang dari database supaya akun yang dinonaktifkan
 * atau diubah rolenya langsung berlaku tanpa menunggu session habis.
 */
public class AuthFilter implements Filter {

    private final UserDao userDao = new UserDao();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (isPublic(path)) {
            chain.doFilter(req, resp);
            return;
        }

        User sessionUser = Auth.currentUser(req);
        if (sessionUser != null) {
            try {
                User fresh = userDao.findById(sessionUser.getId());
                if (fresh != null && fresh.isActive()) {
                    Auth.refresh(req, fresh);
                    chain.doFilter(req, resp);
                    return;
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            // akun dihapus / dinonaktifkan saat sedang login
            Auth.logout(req);
            Flash.error(req, "Sesi berakhir atau akun Anda tidak aktif. Silakan login kembali.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // belum login: ingat halaman tujuan (GET saja) lalu arahkan ke login
        if ("GET".equals(req.getMethod())) {
            String target = path + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
            req.getSession().setAttribute(Auth.AFTER_LOGIN_KEY, target);
        }
        Flash.error(req, "Silakan login terlebih dahulu.");
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private boolean isPublic(String path) {
        return path.isEmpty() || path.equals("/") || path.equals("/index.jsp")
                || path.equals("/home") || path.equals("/login") || path.equals("/register")
                || path.equals("/logout")
                || path.startsWith("/assets/") || path.startsWith("/uploads/");
    }
}
