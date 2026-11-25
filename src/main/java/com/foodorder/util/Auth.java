package com.foodorder.util;

import com.foodorder.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class Auth {

    public static final String SESSION_KEY = "authUser";
    public static final String AFTER_LOGIN_KEY = "afterLogin";

    private Auth() {
    }

    public static void login(HttpServletRequest req, User user) {
        req.getSession(true);
        req.changeSessionId(); 
        req.getSession().setAttribute(SESSION_KEY, safeCopy(user));
    }

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

    public static int userId(HttpServletRequest req) {
        User u = currentUser(req);
        return u == null ? 0 : u.getId();
    }

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
        return c; 
    }
}
