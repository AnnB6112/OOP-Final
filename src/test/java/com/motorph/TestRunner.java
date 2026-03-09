package com.motorph;

import com.motorph.model.Employee;
import com.motorph.model.InventoryItem;
import com.motorph.service.PayrollService;
import com.motorph.model.Payslip;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestRunner {
    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test_output.txt"))) {
            writer.println("Running Tests...");

            // Test Employee Creation
            Employee emp = new Employee("E001", "John", "Doe", "Developer", 50000, 300, "S1", "P1", "T1", "PI1");
            if (emp.getEmployeeId().equals("E001")) {
                writer.println("Employee Test Passed");
            } else {
                writer.println("Employee Test Failed");
            }

            // Test Payroll Calculation
            PayrollService payroll = new PayrollService();
            double hoursWorked = 160;
            Payslip payslip = payroll.generatePayslip(emp, hoursWorked);
            
            writer.println("Gross Salary: " + payslip.getGrossSalary());
            writer.println("Deductions: " + payslip.getTotalDeductions());
            writer.println("Net Salary: " + payslip.getNetSalary());
            
            if (Math.abs(payslip.getGrossSalary() - 48000) < 0.01) {
                writer.println("Payroll Gross Calculation Passed");
            } else {
                 writer.println("Payroll Gross Calculation Failed");
            }

            // Test Inventory
            InventoryItem item = new InventoryItem("P001", "Laptop", 10, 50000, "Electronics");
            item.updateStock(-2);
            if (item.getQuantity() == 8) {
                writer.println("Inventory Update Passed");
            } else {
                writer.println("Inventory Update Failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
