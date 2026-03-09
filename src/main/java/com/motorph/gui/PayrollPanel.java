package com.motorph.gui;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;
import com.motorph.service.PayrollService;
import com.motorph.model.Payslip;
import com.motorph.model.TimeLog;

import javax.swing.*;
import java.awt.*;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

public class PayrollPanel extends JPanel {
    private JTextField employeeIdField;
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTextArea resultArea;
    private PayrollService payrollService;
    private DataStore dataStore;

    public PayrollPanel() {
        this.payrollService = new PayrollService();
        this.dataStore = DataStore.getInstance();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10)); 
        inputPanel.setBorder(BorderFactory.createTitledBorder("Payslip Generator"));

        inputPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        inputPanel.add(employeeIdField);

        inputPanel.add(new JLabel("Month:"));
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Month.of(i + 1).name();
        }
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(Month.SEPTEMBER.getValue() - 1);
        inputPanel.add(monthComboBox);

        inputPanel.add(new JLabel("Year:"));
        yearComboBox = new JComboBox<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(2024);
        inputPanel.add(yearComboBox);

        JButton calculateButton = new JButton("View Payslip");
        calculateButton.addActionListener(e -> calculatePayroll());
        inputPanel.add(new JLabel("")); // Spacer
        inputPanel.add(calculateButton);

        add(inputPanel, BorderLayout.NORTH);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Payslip Details"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void calculatePayroll() {
        String empId = employeeIdField.getText().trim();
        resultArea.setText(""); 

        try {
            if (empId.isEmpty()) {
                throw new IllegalArgumentException("Employee ID cannot be empty.");
            }

            Optional<Employee> employeeOpt = dataStore.findEmployeeById(empId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException("Employee with ID '" + empId + "' not found.");
            }
            Employee employee = employeeOpt.get();

            int selectedMonth = monthComboBox.getSelectedIndex() + 1;
            int selectedYear = (Integer) yearComboBox.getSelectedItem();
            List<TimeLog> timeLogs = dataStore.getTimeLogs();

            // Calculate Payslip
            Payslip payslip = payrollService.generatePayslip(employee, selectedMonth, selectedYear, timeLogs);

            // Display Result
            StringBuilder sb = new StringBuilder();
            sb.append("=========================================\n");
            sb.append("           MOTOR PH PAYSLIP              \n");
            sb.append("=========================================\n");
            sb.append(String.format("Period: %s %d\n", Month.of(selectedMonth).name(), selectedYear));
            sb.append("-----------------------------------------\n");
            sb.append("EMPLOYEE DETAILS\n");
            sb.append(String.format("Name:     %s, %s\n", employee.getLastName(), employee.getFirstName()));
            sb.append(String.format("ID:       %s\n", employee.getEmployeeId()));
            sb.append(String.format("Position: %s\n", employee.getPosition()));
            sb.append("-----------------------------------------\n");
            sb.append("EARNINGS\n");
            sb.append(String.format("Basic Pay:          P %10.2f\n", payslip.getBasicPay()));
            sb.append(String.format("Total Allowances:   P %10.2f\n", payslip.getAllowances()));
            sb.append(String.format("  (Rice: %.2f, Phone: %.2f, Clothing: %.2f)\n", 
                employee.getRiceSubsidy(), employee.getPhoneAllowance(), employee.getClothingAllowance()));
            sb.append(String.format("Gross Income:       P %10.2f\n", payslip.getGrossSalary()));
            sb.append("-----------------------------------------\n");
            sb.append("DEDUCTIONS\n");
            sb.append(String.format("SSS Contribution:   P %10.2f\n", payslip.getSssDeduction()));
            sb.append(String.format("PhilHealth:         P %10.2f\n", payslip.getPhilHealthDeduction()));
            sb.append(String.format("Pag-IBIG:           P %10.2f\n", payslip.getPagIbigDeduction()));
            sb.append(String.format("Withholding Tax:    P %10.2f\n", payslip.getWithholdingTax()));
            sb.append(String.format("Total Deductions:   P %10.2f\n", payslip.getTotalDeductions()));
            sb.append("-----------------------------------------\n");
            sb.append(String.format("NET PAY:            P %10.2f\n", payslip.getNetSalary()));
            sb.append("=========================================\n");
            
            resultArea.setText(sb.toString());

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
