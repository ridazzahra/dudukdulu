package com.foodorder.filter;

import com.foodorder.util.Auth;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Dipetakan ke /admin/* di web.xml (berjalan setelah AuthFilter): hanya role admin yang boleh lewat. */
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (Auth.isAdmin(req)) {
            chain.doFilter(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
