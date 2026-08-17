package com.unimate.servlet;

import com.unimate.dao.CourseDAO;
import com.unimate.dao.MaterialDAO;
import com.unimate.model.Material;
import com.unimate.model.User;
import com.unimate.util.Constants;
import com.unimate.util.JsonUtil;

import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet("/materials")
@MultipartConfig(maxFileSize = 25 * 1024 * 1024)   // 25 MB
public class MaterialServlet extends BaseServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    /** List for a course, or download one file. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null) { sendError(res, 401, "Please log in"); return; }

        try {
            if ("download".equals(req.getParameter("action"))) {
                Material m = materialDAO.findById(intParam(req, "id"));
                if (m == null) { sendError(res, 404, "File not found"); return; }

                // the access check lives on the server, not in the UI
                boolean allowed = Constants.ROLE_INSTRUCTOR.equals(user.getRole())
                        || courseDAO.isEnrolled(user.getUserId(), m.getCourseId());
                if (!allowed) { sendError(res, 403, "Enroll in the course first"); return; }

                File file = new File(getServletContext().getRealPath("/") + m.getFilePath());
                res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                Files.copy(file.toPath(), res.getOutputStream());
                materialDAO.countDownload(m.getMaterialId());
            } else {
                sendJson(res, materialDAO.findByCourse(intParam(req, "courseId")));
            }
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }

    /** Instructor uploads a file. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = currentUser(req);
        if (user == null || !Constants.ROLE_INSTRUCTOR.equals(user.getRole())) {
            sendError(res, 403, "Only instructors can upload");
            return;
        }

        try {
            Part part = req.getPart("file");
            String fileName = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
            String folder = getServletContext().getRealPath("/") + Constants.UPLOAD_DIR;
            new File(folder).mkdirs();
            part.write(folder + File.separator + fileName);

            Material m = new Material();
            m.setCourseId(intParam(req, "courseId"));
            m.setType(req.getParameter("type"));
            m.setTitle(req.getParameter("title"));
            m.setFilePath(Constants.UPLOAD_DIR + "/" + fileName);
            m.setUploadedBy(user.getUserId());

            sendJson(res, JsonUtil.message(materialDAO.insert(m) ? "Uploaded" : "Upload failed"));
        } catch (Exception e) {
            sendError(res, 500, e.getMessage());
        }
    }
}
