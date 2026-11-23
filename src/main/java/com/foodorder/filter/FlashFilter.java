package com.foodorder.filter;

import com.foodorder.util.Flash;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Memindahkan pesan flash dari session ke request (sekali tampil) agar JSP cukup memakai ${flashSuccess}/${flashError}. */
public class FlashFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) req).getSession(false);
        if (session != null) {
            Object ok = session.getAttribute(Flash.SUCCESS);
            Object err = session.getAttribute(Flash.ERROR);
            if (ok != null) {
                req.setAttribute(Flash.SUCCESS_ATTR, ok);
                session.removeAttribute(Flash.SUCCESS);
            }
            if (err != null) {
                req.setAttribute(Flash.ERROR_ATTR, err);
                session.removeAttribute(Flash.ERROR);
            }
        }
        chain.doFilter(req, resp);
    }
}
