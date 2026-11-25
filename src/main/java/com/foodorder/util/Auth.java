package com.foodorder.util;

import com.foodorder.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Akses user yang sedang login. KONTRAK UNTUK ANGGOTA B & C:
 *   Java : User u = Auth.currentUser(req);   int id = Auth.userId(req);   Auth.isAdmin(req)
 *   JSP  : ${sessionScope.authUser.name}  ${sessionScope.authUser.role}  ${sessionScope.authUser.admin}
 * Objek User di session TIDAK berisi hash password.
 */
public final class Auth {

    public static final String SESSION_KEY = "authUser";
    public static final String AFTER_LOGIN_KEY = "afterLogin";

    private Auth() {
    }

    public static void login(HttpServletRequest req, User user) {
        req.getSession(true);
        req.changeSessionId(); // cegah session fixation
        req.getSession().setAttribute(SESSION_KEY, safeCopy(user));
    }

    /** Segarkan data user di session (dipanggil AuthFilter agar perubahan role/nonaktif langsung berlaku). */
    public static void refresh(HttpServletRequest req, User user) {
        req.getSession().setAttribute(SESSION_KEY, safeCopy(user));
    }

    public static void logout(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) {
            s.invalidate();
        }
    }

    public static User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute(SESSION_KEY);
    }

    public static boolean isLoggedIn(HttpServletRequest req) {
        return currentUser(req) != null;
    }

    public static boolean isAdmin(HttpServletRequest req) {
        User u = currentUser(req);
        return u != null && u.isAdmin();
    }

    /** ID user login, atau 0 bila belum login. */
    public static int userId(HttpServletRequest req) {
        User u = currentUser(req);
        return u == null ? 0 : u.getId();
    }

    /** Halaman awal setelah login. TODO (Anggota C): ganti admin ke /admin/dashboard bila dashboard sudah jadi. */
    public static String homeFor(User u) {
        return u != null && u.isAdmin() ? "/admin/dashboard" : "/catalog";
    }

    private static User safeCopy(User u) {
        User c = new User();
        c.setId(u.getId());
        c.setName(u.getName());
        c.setEmail(u.getEmail());
        c.setRole(u.getRole());
        c.setPhone(u.getPhone());
        c.setActive(u.isActive());
        return c; // password sengaja tidak disalin
    }
}
