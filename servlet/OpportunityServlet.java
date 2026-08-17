package com.unimate.servlet;

import com.unimate.dao.OpportunityDAO;
import com.unimate.model.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/opportunities")
public class OpportunityServlet extends BaseServlet {

    private final OpportunityDAO dao = new OpportunityDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        String type = req.getParameter("type");
        try {
            if ("detail".equals(req.getParameter("action"))) {
                sendJson(res, dao.findById(intParam(req, "id")));
            } else if (type != null && !type.isEmpty()) {
                sendJson(res, dao.findByType(type));
            } else {
                sendJson(res, dao.findAll());
            }
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
