package com.unimate.dao;

import com.unimate.model.StudyGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO implements BaseDAO<StudyGroup> {

    private StudyGroup build(ResultSet rs) throws SQLException {
        StudyGroup g = new StudyGroup();
        g.setGroupId(rs.getInt("group_id"));
        g.setName(rs.getString("name"));
        g.setCourseCode(rs.getString("course_code"));
        g.setUniversity(rs.getString("university"));
        g.setDescription(rs.getString("description"));
        g.setMaxMembers(rs.getInt("max_members"));
        g.setCreatedBy(rs.getInt("created_by"));
        g.setMemberCount(rs.getInt("member_count"));
        return g;
    }

    private static final String SELECT =
        "SELECT g.*, (SELECT COUNT(*) FROM group_members m WHERE m.group_id = g.group_id) "
      + "AS member_count FROM study_groups g ";

    @Override
    public List<StudyGroup> findAll() throws SQLException {
        List<StudyGroup> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT + "ORDER BY g.group_id DESC")) {
            while (rs.next()) list.add(build(rs));
        }
        return list;
    }

    @Override
    public StudyGroup findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE g.group_id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? build(rs) : null;
        }
    }

    @Override
    public boolean insert(StudyGroup g) throws SQLException {
        String sql = "INSERT INTO study_groups (name, course_code, university, description, "
                   + "max_members, created_by) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, g.getName());
            ps.setString(2, g.getCourseCode());
            ps.setString(3, g.getUniversity());
            ps.setString(4, g.getDescription());
            ps.setInt(5, g.getMaxMembers());
            ps.setInt(6, g.getCreatedBy());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean join(int groupId, int studentId) throws SQLException {
        String sql = "INSERT IGNORE INTO group_members (group_id, student_id) VALUES (?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() == 1;
        }
    }

    public List<StudyGroup> findByStudent(int studentId) throws SQLException {
        List<StudyGroup> list = new ArrayList<>();
        String sql = SELECT + "JOIN group_members m ON m.group_id = g.group_id "
                   + "WHERE m.student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(build(rs));
        }
        return list;
    }
}
