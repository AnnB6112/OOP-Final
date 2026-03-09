package com.motorph.model;

import java.util.Arrays;
import java.util.List;

public class AdminUser extends User {

    public AdminUser(String username, String password) {
        super(username, password, "Admin");
    }

    @Override
    public List<String> getAllowedFeatures() {
        return Arrays.asList("Dashboard", "Employees", "Payroll", "Time Logs", "Leave Requests", "Reports", "System Tools");
    }

    @Override
    public boolean canAccess(String feature) {
        return true; // Admin has full access
    }
}
