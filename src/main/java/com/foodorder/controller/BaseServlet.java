package com.foodorder.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public abstract class BaseServlet extends HttpServlet {

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected String action(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.equals("/")) {
            return "";
        }
        return p.endsWith("/") ? p.substring(0, p.length() - 1) : p;
    }

    protected int intParam(HttpServletRequest req, String name, int defaultValue) {
        try {
            String v = req.getParameter(name);
            return (v == null || v.isBlank()) ? defaultValue : Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    protected String baseQuery(HttpServletRequest req, String... exclude) {
        Set<String> skip = Set.of(exclude);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            if (skip.contains(e.getKey())) {
                continue;
            }
            for (String v : e.getValue()) {
                if (v == null || v.isEmpty()) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                  .append('=')
                  .append(URLEncoder.encode(v, StandardCharsets.UTF_8));
            }
        }
        return sb.toString();
    }

    protected String safeQuery(String q) {
        return q == null ? "" : q.replaceAll("[\\r\\n\\s]", "");
    }
}
