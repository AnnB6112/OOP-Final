package com.motorph.model;

import java.util.Arrays;
import java.util.List;

public class FinanceUser extends User {

    public FinanceUser(String username, String password) {
        super(username, password, "Finance");
    }

    @Override
    public List<String> getAllowedFeatures() {
        return Arrays.asList("Dashboard", "Payroll", "Reports", "Leave Requests");
    }

    @Override
    public boolean canAccess(String feature) {
        return getAllowedFeatures().contains(feature);
    }
}
