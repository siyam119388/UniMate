package com.unimate.servlet;

import com.unimate.dao.CourseDAO;
import com.unimate.dao.MaterialDAO;
import com.unimate.model.Course;
import com.unimate.model.Material;
import com.unimate.model.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * Simple study assistant.
 * It searches the materials of the courses the student is enrolled in
 * and answers with the closest match plus the source file.
 */
@WebServlet("/ai")
public class AIServlet extends BaseServlet {

    private final CourseDAO courseDAO = new CourseDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        String question = req.getParameter("question");
        if (question == null || question.trim().isEmpty()) {
            sendError(res, 400, "Ask a question first");
            return;
        }

        try {
            // only the student's own courses are searched
            List<Course> myCourses = courseDAO.findByStudent(user.getUserId());
            Material best = null;
            int bestScore = 0;

            for (Course c : myCourses) {
                for (Material m : materialDAO.findByCourse(c.getCourseId())) {
                    int score = score(question, m.getTitle() + " " + c.getCode() + " " + c.getTitle());
                    if (score > bestScore) { bestScore = score; best = m; }
                }
            }

            Map<String, Object> out = new HashMap<>();
            if (best == null) {
                out.put("answer", "I could not find anything in your course materials about that. "
                                + "Try a course code, for example CSE327.");
                out.put("source", null);
            } else {
                out.put("answer", "Your course material \"" + best.getTitle()
                                + "\" covers this. Open it below.");
                out.put("source", best);
            }
            sendJson(res, out);

        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }

    /** Counts how many words of the question appear in the material title. */
    private int score(String question, String text) {
        int hits = 0;
        String lower = text.toLowerCase();
        for (String word : question.toLowerCase().split("\\s+")) {
            if (word.length() > 3 && lower.contains(word)) hits++;
        }
        return hits;
    }
}
