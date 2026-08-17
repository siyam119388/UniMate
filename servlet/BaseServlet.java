package com.unimate.servlet;

import com.unimate.model.User;
import com.unimate.util.JsonUtil;

import jakarta.servlet.http.*;
import java.io.*;

/** Shared helpers. Every other servlet extends this. */
public abstract class BaseServlet extends HttpServlet {

    protected void sendJson(HttpServletResponse res, Object data) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(JsonUtil.toJson(data));
    }

    protected void sendError(HttpServletResponse res, int code, String msg) throws IOException {
        res.setStatus(code);
        res.setContentType("application/json");
        res.getWriter().write(JsonUtil.error(msg));
    }

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session == null) ? null : (User) session.getAttribute("user");
    }

    protected String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    protected int intParam(HttpServletRequest req, String name) {
        try {
            return Integer.parseInt(req.getParameter(name));
        } catch (Exception e) {
            return 0;
        }
    }
}
