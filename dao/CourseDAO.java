package com.unimate.dao;

import com.unimate.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO implements BaseDAO<Course> {

    private Course build(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCode(rs.getString("code"));
        c.setTitle(rs.getString("title"));
        c.setCredits(rs.getInt("credits"));
        c.setPrerequisites(rs.getString("prerequisites"));
        c.setDifficulty(rs.getString("difficulty"));
        c.setDescription(rs.getString("description"));
        c.setInstructorId(rs.getInt("instructor_id"));
        c.setInstructorName(rs.getString("instructor_name"));
        c.setEnrolledCount(rs.getInt("enrolled_count"));
        return c;
    }

    private static final String SELECT =
        "SELECT c.*, u.name AS instructor_name, "
      + "(SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.course_id) AS enrolled_count "
      + "FROM courses c LEFT JOIN users u ON u.user_id = c.instructor_id ";

    @Override
    public List<Course> findAll() throws SQLException {
        return search("");
    }

    /** One method covers both the plain list and the search box. */
    public List<Course> search(String keyword) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = SELECT + "WHERE c.code LIKE ? OR c.title LIKE ? ORDER BY c.code";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + (keyword == null ? "" : keyword) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(build(rs));
        }
        return list;
    }

    @Override
    public Course findById(int id) throws SQLException {
        String sql = SELECT + "WHERE c.course_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? build(rs) : null;
        }
    }

    @Override
    public boolean insert(Course c) throws SQLException {
        String sql = "INSERT INTO courses (code, title, credits, prerequisites, difficulty, "
                   + "description, instructor_id) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getTitle());
            ps.setInt(3, c.getCredits());
            ps.setString(4, c.getPrerequisites());
            ps.setString(5, c.getDifficulty());
            ps.setString(6, c.getDescription());
            ps.setInt(7, c.getInstructorId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean enroll(int studentId, int courseId) throws SQLException {
        if (isEnrolled(studentId, courseId)) return false;
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean isEnrolled(int studentId, int courseId) throws SQLException {
        String sql = "SELECT 1 FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeQuery().next();
        }
    }

    public List<Course> findByStudent(int studentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = SELECT + "JOIN enrollments e ON e.course_id = c.course_id "
                   + "WHERE e.student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(build(rs));
        }
        return list;
    }

    public List<Course> findByInstructor(int instructorId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = SELECT + "WHERE c.instructor_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(build(rs));
        }
        return list;
    }
}
