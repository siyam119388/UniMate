package com.unimate.servlet;

import com.unimate.dao.CourseDAO;
import com.unimate.model.Course;
import com.unimate.model.User;
import com.unimate.util.Constants;
import com.unimate.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
//
@WebServlet("/courses")
public class CourseServlet extends BaseServlet {

    private final CourseDAO courseDAO = new CourseDAO();

    /** ?action=list | detail | mine   */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        String action = req.getParameter("action");
        try {
            if ("detail".equals(action)) {
                sendJson(res, courseDAO.findById(intParam(req, "id")));
            } else if ("mine".equals(action)) {
                sendJson(res, courseDAO.findByStudent(user.getUserId()));
            } else if ("teaching".equals(action)) {
                sendJson(res, courseDAO.findByInstructor(user.getUserId()));
            } else {
                sendJson(res, courseDAO.search(req.getParameter("q")));
            }
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }

    /** action=create for instructors, otherwise enroll the logged-in student. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        try {
            if ("create".equals(req.getParameter("action"))) {
                if (!Constants.ROLE_INSTRUCTOR.equals(user.getRole())) {
                    sendError(res, 403, "Only instructors can create a course");
                    return;
                }
                Course c = new Course();
                c.setCode(req.getParameter("code"));
                c.setTitle(req.getParameter("title"));
                c.setCredits(intParam(req, "credits"));
                c.setPrerequisites(req.getParameter("prerequisites"));
                c.setDifficulty(req.getParameter("difficulty"));
                c.setDescription(req.getParameter("description"));
                c.setInstructorId(user.getUserId());
                sendJson(res, JsonUtil.message(
                        courseDAO.insert(c) ? "Course created" : "Could not create the course"));
                return;
            }

            boolean ok = courseDAO.enroll(user.getUserId(), intParam(req, "courseId"));
            sendJson(res, JsonUtil.message(ok ? "Enrolled" : "Already enrolled"));
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
