package com.motorph.model;

import java.util.Arrays;
import java.util.List;

public class RegularUser extends User {

    public RegularUser(String username, String password) {
        super(username, password, "Employee");
    }

    @Override
    public List<String> getAllowedFeatures() {
        return Arrays.asList("Dashboard", "My Payslip", "My Time Logs", "Leave Requests");
    }

    @Override
    public boolean canAccess(String feature) {
        return getAllowedFeatures().contains(feature);
    }
}
