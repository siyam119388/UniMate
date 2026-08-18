package com.unimate.servlet;

import com.unimate.dao.CourseDAO;
import com.unimate.dao.GroupDAO;
import com.unimate.model.Student;
import com.unimate.model.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Everything the profile screen needs, in one call. *///
@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {

    private final CourseDAO courseDAO = new CourseDAO();
    private final GroupDAO groupDAO = new GroupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("name", user.getName());
            data.put("email", user.getEmail());
            data.put("role", user.getRole());
            data.put("university", user.getUniversity());
            data.put("department", user.getDepartment());

            if (user instanceof Student) {
                Student s = (Student) user;
                data.put("studentId", s.getStudentId());
                data.put("semester", s.getSemester());
                data.put("courses", courseDAO.findByStudent(user.getUserId()));
                data.put("groups", groupDAO.findByStudent(user.getUserId()));
            } else {
                data.put("courses", courseDAO.findByInstructor(user.getUserId()));
            }
            sendJson(res, data);
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
