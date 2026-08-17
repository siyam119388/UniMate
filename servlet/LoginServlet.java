package com.unimate.servlet;

import com.unimate.dao.UserDAO;
import com.unimate.model.User;
import com.unimate.util.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = userDAO.findByEmail(email);

            if (user == null || !PasswordUtil.matches(password, user.getPasswordHash())) {
                sendError(res, 401, "Wrong email or password");
                return;
            }
            if (Constants.STATUS_PENDING.equals(user.getStatus())) {
                sendError(res, 403, "Your account is waiting for admin approval");
                return;
            }

            req.getSession(true).setAttribute("user", user);

            Map<String, String> out = new HashMap<>();
            out.put("name", user.getName());
            out.put("role", user.getRole());
            out.put("redirect", user.getDashboardPath());   // polymorphism does the routing
            sendJson(res, out);

        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
