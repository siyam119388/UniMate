package com.unimate.servlet;

import com.unimate.dao.UserDAO;
import com.unimate.model.*;
import com.unimate.util.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name     = req.getParameter("name");
        String email    = req.getParameter("email");
        String password = req.getParameter("password");
        String role     = req.getParameter("role");

        if (name == null || email == null || password == null) {
            sendError(res, 400, "All fields are required");
            return;
        }
        if (!Constants.isUniversityEmail(email)) {
            sendError(res, 400, "Use your university email");
            return;
        }

        try {
            if (userDAO.findByEmail(email) != null) {
                sendError(res, 409, "This email is already registered");
                return;
            }

            User user;
            if (Constants.ROLE_INSTRUCTOR.equals(role)) {
                Instructor i = new Instructor();
                i.setDesignation(req.getParameter("designation"));
                user = i;
            } else {
                Student s = new Student();
                s.setStudentId(req.getParameter("studentId"));
                s.setSemester(req.getParameter("semester"));
                user = s;
            }

            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setUniversity(req.getParameter("university"));
            user.setDepartment(req.getParameter("department"));
            // instructors wait for an admin, students are active right away
            user.setStatus(Constants.ROLE_INSTRUCTOR.equals(role)
                    ? Constants.STATUS_PENDING : Constants.STATUS_ACTIVE);

            if (userDAO.insert(user)) {
                sendJson(res, JsonUtil.message("Account created"));
            } else {
                sendError(res, 500, "Could not create the account");
            }
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
