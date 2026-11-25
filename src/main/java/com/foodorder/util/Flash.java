package com.foodorder.util;

import jakarta.servlet.http.HttpServletRequest;


public final class Flash {

    public static final String SUCCESS = "flash_success";
    public static final String ERROR = "flash_error";
    public static final String SUCCESS_ATTR = "flashSuccess";
    public static final String ERROR_ATTR = "flashError";

    private Flash() {
    }

    public static void success(HttpServletRequest req, String message) {
        req.getSession().setAttribute(SUCCESS, message);
    }

    public static void error(HttpServletRequest req, String message) {
        req.getSession().setAttribute(ERROR, message);
    }

    public static void successNow(HttpServletRequest req, String message) {
        req.setAttribute(SUCCESS_ATTR, message);
    }

    public static void errorNow(HttpServletRequest req, String message) {
        req.setAttribute(ERROR_ATTR, message);
    }
}
