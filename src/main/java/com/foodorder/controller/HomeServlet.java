package com.foodorder.controller;

import com.foodorder.model.User;
import com.foodorder.util.Auth;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = Auth.currentUser(req);
        redirect(req, resp, u == null ? "/login" : Auth.homeFor(u));
    }
}
