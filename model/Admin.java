package com.unimate.model;

import com.unimate.util.Constants;

public class Admin extends User {

    @Override
    public String getRole() { return Constants.ROLE_ADMIN; }   ///

    @Override
    public String getDashboardPath() { return "admin/dashboard.html"; }
} 
