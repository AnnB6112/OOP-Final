package com.motorph.tools;

import java.io.*;
import java.util.*;

public class UserGenerator {
    private static final String EMPLOYEES_CSV = "employees.csv";
    private static final String USERS_CSV = "users.csv";

    public static void main(String[] args) {
        Map<String, String> existingUsers = loadExistingUsers();
        List<String> newUsers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEES_CSV))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    String empId = parts[0];
                    String position = parts.length > 11 ? parts[11] : "";
                    
                    if (!existingUsers.containsKey(empId)) {
                        String role = determineRole(position);
                        String password = "password123";
                        newUsers.add(String.format("%s,%s,%s", empId, password, role));
                        System.out.println("Generating user for: " + empId + " (" + role + ")");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!newUsers.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_CSV, true))) {
                for (String userLine : newUsers) {
                    writer.println(userLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Successfully added " + newUsers.size() + " new users.");
        } else {
            System.out.println("No new users to add.");
        }
    }

    private static Map<String, String> loadExistingUsers() {
        Map<String, String> users = new HashMap<>();
        File file = new File(USERS_CSV);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    users.put(parts[0], parts.length > 2 ? parts[2] : "Employee");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static String determineRole(String position) {
        position = position.toLowerCase();
        if (position.contains("hr")) {
            return "HR";
        } else if (position.contains("finance") || position.contains("accounting") || position.contains("account")) {
            // Note: "Account Manager" might be Sales, but usually "Accounting" is Finance.
            // Let's be specific.
            if (position.contains("accounting") || position.contains("finance")) {
                return "Finance";
            }
        } else if (position.contains("chief executive") || position.contains("ceo")) {
            return "Admin";
        }
        return "Employee";
    }
}
