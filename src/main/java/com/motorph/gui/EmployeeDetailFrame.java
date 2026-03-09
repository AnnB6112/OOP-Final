package com.motorph.gui;

import com.motorph.model.Employee;
import com.motorph.service.PayrollService;
import com.motorph.model.Payslip;

import javax.swing.*;
import java.awt.*;

public class EmployeeDetailFrame extends JFrame {
    private Employee employee;
    private PayrollService payrollService;
    private JComboBox<String> monthComboBox;
    private JTextField hoursField;
    private JTextArea salaryDetailsArea;

    public EmployeeDetailFrame(Employee employee) {
        this.employee = employee;
        this.payrollService = new PayrollService();

        setTitle("Employee Details - " + employee.getFirstName() + " " + employee.getLastName());
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Employee Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));

        addDetail(detailsPanel, "Employee ID:", employee.getEmployeeId());
        addDetail(detailsPanel, "Name:", employee.getFirstName() + " " + employee.getLastName());
        addDetail(detailsPanel, "Position:", employee.getPosition());
        addDetail(detailsPanel, "Basic Salary:", String.format("%.2f", employee.getBasicSalary()));
        addDetail(detailsPanel, "Hourly Rate:", String.format("%.2f", employee.getHourlyRate()));
        addDetail(detailsPanel, "SSS Number:", employee.getSssNumber());
        addDetail(detailsPanel, "PhilHealth:", employee.getPhilHealthNumber());
        addDetail(detailsPanel, "TIN:", employee.getTinNumber());
        addDetail(detailsPanel, "Pag-IBIG:", employee.getPagIbigNumber());

        add(detailsPanel, BorderLayout.NORTH);

        // Salary Computation Panel
        JPanel computationPanel = new JPanel();
        computationPanel.setLayout(new BoxLayout(computationPanel, BoxLayout.Y_AXIS));
        computationPanel.setBorder(BorderFactory.createTitledBorder("Salary Computation"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Select Month:"));
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        inputPanel.add(monthComboBox);

        inputPanel.add(new JLabel("Hours Worked:"));
        hoursField = new JTextField(5);
        inputPanel.add(hoursField);

        JButton computeButton = new JButton("Compute");
        computeButton.addActionListener(e -> computeSalary());
        inputPanel.add(computeButton);

        computationPanel.add(inputPanel);

        salaryDetailsArea = new JTextArea(10, 40);
        salaryDetailsArea.setEditable(false);
        salaryDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(salaryDetailsArea);
        computationPanel.add(scrollPane);

        add(computationPanel, BorderLayout.SOUTH);

        // Add padding
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void addDetail(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        JTextField field = new JTextField(value);
        field.setEditable(false);
        panel.add(field);
    }

    private void computeSalary() {
        try {
            String hoursStr = hoursField.getText().trim();
            if (hoursStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter hours worked.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double hours = Double.parseDouble(hoursStr);
            if (hours < 0) {
                JOptionPane.showMessageDialog(this, "Hours cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedMonth = (String) monthComboBox.getSelectedItem();
            Payslip payslip = payrollService.generatePayslip(employee, hours);

            StringBuilder sb = new StringBuilder();
            sb.append("--- PAYSLIP FOR ").append(selectedMonth.toUpperCase()).append(" ---\n\n");
            sb.append(String.format("%-20s: %.2f\n", "Gross Salary", payslip.getGrossSalary()));
            sb.append(String.format("%-20s: %.2f\n", "Total Deductions", payslip.getTotalDeductions()));
            sb.append("----------------------------------------\n");
            sb.append(String.format("%-20s: %.2f\n", "NET SALARY", payslip.getNetSalary()));
            
            salaryDetailsArea.setText(sb.toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number for hours worked.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
