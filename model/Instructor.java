package com.unimate.model;

import com.unimate.util.Constants;

public class Instructor extends User {

    private String designation;

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    @Override
    public String getRole() { return Constants.ROLE_INSTRUCTOR; }

    @Override
    public String getDashboardPath() { return "instructor/dashboard.html"; }
}
