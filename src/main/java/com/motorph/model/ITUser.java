package com.motorph.model;

import java.util.Arrays;
import java.util.List;

public class ITUser extends User {

    public ITUser(String username, String password) {
        super(username, password, "IT");
    }

    @Override
    public List<String> getAllowedFeatures() {
        return Arrays.asList("Dashboard", "System Tools", "Settings");
    }

    @Override
    public boolean canAccess(String feature) {
        // IT should not access employee details or payroll
        if (feature.equalsIgnoreCase("Employees") || feature.equalsIgnoreCase("Payroll")) {
            return false;
        }
        return getAllowedFeatures().contains(feature);
    }
}
