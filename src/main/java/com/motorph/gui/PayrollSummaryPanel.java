package com.motorph.gui;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;
import com.motorph.service.PayrollService;
import com.motorph.model.Payslip;
import com.motorph.model.TimeLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class PayrollSummaryPanel extends JPanel {
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private PayrollService payrollService;
    private DataStore dataStore;

    public PayrollSummaryPanel() {
        this.payrollService = new PayrollService();
        this.dataStore = DataStore.getInstance();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Top Panel: Filter Controls ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Report Filter"));

        filterPanel.add(new JLabel("Month:"));
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = Month.of(i + 1).name();
        }
        monthComboBox = new JComboBox<>(months);
        // Default to current month or September (as per data usually)
        monthComboBox.setSelectedIndex(Month.SEPTEMBER.getValue() - 1); 
        filterPanel.add(monthComboBox);

        filterPanel.add(new JLabel("Year:"));
        yearComboBox = new JComboBox<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(2024); // Default to 2024 as per likely data context
        filterPanel.add(yearComboBox);

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateReport());
        filterPanel.add(generateButton);

        add(filterPanel, BorderLayout.NORTH);

        // --- Center Panel: Report Table ---
        String[] columnNames = {
            "Emp No", "Employee Name", "Basic Pay", "Allowances", "Gross Pay", 
            "SSS", "PhilHealth", "Pag-IBIG", "Tax", "Net Pay"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2) return Double.class;
                return String.class;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Adjust column widths
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        for (int i = 2; i < columnNames.length; i++) {
            reportTable.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        // Currency Renderer
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setText(currencyFormat.format(value));
                    setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    super.setValue(value);
                }
            }
        };
        
        for (int i = 2; i < columnNames.length; i++) {
            reportTable.getColumnModel().getColumn(i).setCellRenderer(currencyRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Monthly Payroll Summary"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void generateReport() {
        tableModel.setRowCount(0); // Clear table
        
        int selectedMonth = monthComboBox.getSelectedIndex() + 1;
        int selectedYear = (Integer) yearComboBox.getSelectedItem();
        
        List<Employee> employees = dataStore.getEmployees();
        List<TimeLog> timeLogs = dataStore.getTimeLogs();
        
        double totalGross = 0;
        double totalNet = 0;

        for (Employee emp : employees) {
            Payslip payslip = payrollService.generatePayslip(emp, selectedMonth, selectedYear, timeLogs);
            
            // Only show employees with hours worked or skip? 
            // Usually payroll summary shows all active employees, even if 0 pay, but let's show all.
            
            Object[] rowData = {
                emp.getEmployeeId(),
                emp.getLastName() + ", " + emp.getFirstName(),
                payslip.getBasicPay(),
                payslip.getAllowances(),
                payslip.getGrossSalary(),
                payslip.getSssDeduction(),
                payslip.getPhilHealthDeduction(),
                payslip.getPagIbigDeduction(),
                payslip.getWithholdingTax(),
                payslip.getNetSalary()
            };
            
            tableModel.addRow(rowData);
            
            totalGross += payslip.getGrossSalary();
            totalNet += payslip.getNetSalary();
        }
        
        // Optional: Add Total Row
        Object[] totalRow = {
            "TOTAL", "", 
            null, null, 
            totalGross, 
            null, null, null, null, 
            totalNet
        };
        // tableModel.addRow(totalRow); // Can add if needed
    }
}
