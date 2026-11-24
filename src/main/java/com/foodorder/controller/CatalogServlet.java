package com.foodorder.controller;

import com.foodorder.dao.CartDao;
import com.foodorder.dao.CategoryDao;
import com.foodorder.dao.MenuDao;
import com.foodorder.model.Menu;
import com.foodorder.util.Auth;
import com.foodorder.util.Page;
import com.foodorder.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/catalog")
public class CatalogServlet extends BaseServlet {

    private static final int PAGE_SIZE = 12;
    private static final int MAX_QUERY_LENGTH = 100;

    private final MenuDao menuDao = new MenuDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final CartDao cartDao = new CartDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String q = Validator.trim(req.getParameter("q"));
            if (q.length() > MAX_QUERY_LENGTH) {
                q = q.substring(0, MAX_QUERY_LENGTH);
            }
            Integer categoryId = Validator.toInt(req.getParameter("category"));
            int userId = Auth.userId(req);

            Page<Menu> pager = menuDao.findAll(q, categoryId, null, "available",
                    intParam(req, "page", 1), PAGE_SIZE);

            req.setAttribute("pager", pager);
            req.setAttribute("categories", categoryDao.findAll());
            req.setAttribute("q", q);
            req.setAttribute("category", categoryId == null ? "" : String.valueOf(categoryId));
            req.setAttribute("cartQty", cartDao.quantitiesByUser(userId));
            req.setAttribute("cartCount", cartDao.countItems(userId));
            req.setAttribute("baseQuery", baseQuery(req, "page"));
            req.setAttribute("backQuery", req.getQueryString() == null ? "" : req.getQueryString());
            forward(req, resp, "customer/catalog"); 
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}