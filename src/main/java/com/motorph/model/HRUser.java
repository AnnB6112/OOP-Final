package com.motorph.model;

import java.util.Arrays;
import java.util.List;

public class HRUser extends User {

    public HRUser(String username, String password) {
        super(username, password, "HR");
    }

    @Override
    public List<String> getAllowedFeatures() {
        return Arrays.asList("Dashboard", "Employees", "Time Logs", "Leave Requests");
    }

    @Override
    public boolean canAccess(String feature) {
        return getAllowedFeatures().contains(feature);
    }
}
