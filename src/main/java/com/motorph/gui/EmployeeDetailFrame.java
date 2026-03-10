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
    private JTextArea salaryDetailsArea;

    public EmployeeDetailFrame(Employee employee) {
        this.employee = employee;
        this.payrollService = new PayrollService();

        setTitle("Employee Details - " + employee.getFirstName() + " " + employee.getLastName());
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- LEFT: Employee Details Panel ---
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addDetail(detailsPanel, "Employee ID:", employee.getEmployeeId());
        addDetail(detailsPanel, "Last Name:", employee.getLastName());
        addDetail(detailsPanel, "First Name:", employee.getFirstName());
        addDetail(detailsPanel, "Birthday:", employee.getBirthday());
        addDetail(detailsPanel, "Address:", employee.getAddress());
        addDetail(detailsPanel, "Phone Number:", employee.getPhoneNumber());
        addDetail(detailsPanel, "Status:", employee.getStatus());
        addDetail(detailsPanel, "Position:", employee.getPosition());
        addDetail(detailsPanel, "Immediate Supervisor:", employee.getImmediateSupervisor());
        
        // Government IDs
        addDetail(detailsPanel, "SSS Number:", employee.getSssNumber());
        addDetail(detailsPanel, "PhilHealth:", employee.getPhilHealthNumber());
        addDetail(detailsPanel, "TIN:", employee.getTinNumber());
        addDetail(detailsPanel, "Pag-IBIG:", employee.getPagIbigNumber());

        // Compensation
        addDetail(detailsPanel, "Basic Salary:", String.format("%.2f", employee.getBasicSalary()));
        addDetail(detailsPanel, "Gross Semi-monthly Rate:", String.format("%.2f", employee.getGrossSemiMonthlyRate()));
        addDetail(detailsPanel, "Hourly Rate:", String.format("%.2f", employee.getHourlyRate()));
        
        // Allowances
        addDetail(detailsPanel, "Rice Subsidy:", String.format("%.2f", employee.getRiceSubsidy()));
        addDetail(detailsPanel, "Phone Allowance:", String.format("%.2f", employee.getPhoneAllowance()));
        addDetail(detailsPanel, "Clothing Allowance:", String.format("%.2f", employee.getClothingAllowance()));

        // Wrap details in ScrollPane
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        add(detailsScrollPane, BorderLayout.CENTER);

        // --- BOTTOM: Salary Computation Panel ---
        JPanel computationPanel = new JPanel();
        computationPanel.setLayout(new BoxLayout(computationPanel, BoxLayout.Y_AXIS));
        computationPanel.setBorder(BorderFactory.createTitledBorder("Salary Computation"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Select Period:"));
        
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        inputPanel.add(monthComboBox);
        
        // Year Selection (Dynamic)
        int currentYear = java.time.Year.now().getValue();
        Integer[] years = {currentYear - 1, currentYear, currentYear + 1};
        JComboBox<Integer> yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedItem(currentYear);
        inputPanel.add(yearComboBox);

        JButton computeButton = new JButton("Compute Salary");
        computeButton.setBackground(new Color(46, 204, 113));
        computeButton.setForeground(Color.WHITE);
        computeButton.addActionListener(e -> computeSalary((Integer) yearComboBox.getSelectedItem()));
        inputPanel.add(computeButton);

        computationPanel.add(inputPanel);

        salaryDetailsArea = new JTextArea(12, 50);
        salaryDetailsArea.setEditable(false);
        salaryDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScrollPane = new JScrollPane(salaryDetailsArea);
        computationPanel.add(resultScrollPane);

        add(computationPanel, BorderLayout.SOUTH);
    }

    private void addDetail(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        JTextField field = new JTextField(value != null ? value : "N/A");
        field.setEditable(false);
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        panel.add(field);
    }

    private void computeSalary(int year) {
        try {
            int monthIndex = monthComboBox.getSelectedIndex() + 1; // 1-12
            String monthName = (String) monthComboBox.getSelectedItem();

            // Fetch TimeLogs
            com.motorph.data.DataStore dataStore = com.motorph.data.DataStore.getInstance();
            java.util.List<com.motorph.model.TimeLog> allLogs = dataStore.getTimeLogs();

            // Calculate
            Payslip payslip = payrollService.generatePayslip(employee, monthIndex, year, allLogs);

            StringBuilder sb = new StringBuilder();
            sb.append("============================================================\n");
            sb.append(String.format(" PAYSLIP FOR PERIOD: %s %d\n", monthName.toUpperCase(), year));
            sb.append("============================================================\n");
            sb.append(String.format(" %-30s : %15.2f\n", "Hours Worked", payslip.getHoursWorked()));
            sb.append(String.format(" %-30s : %15.2f\n", "Hourly Rate", employee.getHourlyRate()));
            sb.append("\n EARNINGS\n");
            sb.append(String.format(" %-30s : %15.2f\n", "Basic Pay", payslip.getBasicPay()));
            sb.append(String.format(" %-30s : %15.2f\n", "Total Allowances", payslip.getAllowances()));
            sb.append(String.format(" %-30s : %15.2f\n", "GROSS SALARY", payslip.getGrossSalary()));
            sb.append("\n DEDUCTIONS\n");
            sb.append(String.format(" %-30s : %15.2f\n", "SSS Contribution", payslip.getSssDeduction()));
            sb.append(String.format(" %-30s : %15.2f\n", "PhilHealth Contribution", payslip.getPhilHealthDeduction()));
            sb.append(String.format(" %-30s : %15.2f\n", "Pag-IBIG Contribution", payslip.getPagIbigDeduction()));
            sb.append(String.format(" %-30s : %15.2f\n", "Withholding Tax", payslip.getWithholdingTax()));
            sb.append(String.format(" %-30s : %15.2f\n", "TOTAL DEDUCTIONS", payslip.getTotalDeductions()));
            sb.append("------------------------------------------------------------\n");
            sb.append(String.format(" %-30s : %15.2f\n", "NET SALARY", payslip.getNetSalary()));
            sb.append("============================================================\n");
            
            salaryDetailsArea.setText(sb.toString());
            salaryDetailsArea.setCaretPosition(0); // Scroll to top

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error computing salary: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
