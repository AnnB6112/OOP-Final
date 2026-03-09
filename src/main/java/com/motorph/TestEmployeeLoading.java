package com.motorph;

import com.motorph.model.Employee;
import com.motorph.service.EmployeeService;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

public class TestEmployeeLoading {
    public static void main(String[] args) {
        try (PrintWriter out = new PrintWriter(new FileWriter("test_output.txt"))) {
            out.println("Testing Employee Loading...");
            EmployeeService service = new EmployeeService();
            List<Employee> employees = service.getAllEmployees();
            
            out.println("Loaded " + employees.size() + " employees.");
            
            if (employees.isEmpty()) {
                out.println("No employees loaded!");
                return;
            }

            for (Employee emp : employees) {
                String id = emp.getEmployeeId();
                out.println("Checking ID: '" + id + "'");
                
                Optional<Employee> found = service.getEmployeeById(id);
                if (found.isPresent()) {
                    out.println("  [OK] Found: " + found.get().getFirstName());
                } else {
                    out.println("  [FAIL] Could not find employee by ID: '" + id + "'");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
