package com.unimate.model;

import com.unimate.util.Constants;

public class Student extends User {

    private String studentId;
    private String semester;
//
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    @Override
    public String getRole() { return Constants.ROLE_STUDENT; }

    @Override
    public String getDashboardPath() { return "student/dashboard.html"; }
}
