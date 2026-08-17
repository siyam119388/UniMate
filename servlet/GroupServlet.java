package com.unimate.servlet;

import com.unimate.dao.GroupDAO;
import com.unimate.model.StudyGroup;
import com.unimate.model.User;
import com.unimate.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/groups")
public class GroupServlet extends BaseServlet {

    private final GroupDAO groupDAO = new GroupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        String action = req.getParameter("action");
        try {
            if ("detail".equals(action)) {
                sendJson(res, groupDAO.findById(intParam(req, "id")));
            } else if ("mine".equals(action)) {
                sendJson(res, groupDAO.findByStudent(user.getUserId()));
            } else {
                sendJson(res, groupDAO.findAll());
            }
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }

    /** action=create or action=join */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        try {
            if ("join".equals(req.getParameter("action"))) {
                boolean ok = groupDAO.join(intParam(req, "groupId"), user.getUserId());
                sendJson(res, JsonUtil.message(ok ? "Joined" : "Already a member"));
                return;
            }

            StudyGroup g = new StudyGroup();
            g.setName(req.getParameter("name"));
            g.setCourseCode(req.getParameter("courseCode"));
            g.setUniversity(req.getParameter("university"));
            g.setDescription(req.getParameter("description"));
            g.setMaxMembers(intParam(req, "maxMembers"));
            g.setCreatedBy(user.getUserId());

            sendJson(res, JsonUtil.message(groupDAO.insert(g) ? "Group created" : "Failed"));
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
