package com.motorph.gui;

import com.motorph.model.Payslip;
import com.motorph.service.PayrollService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Locale;

public class PayrollSummaryPanel extends JPanel {
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private PayrollService payrollService;

    public PayrollSummaryPanel() {
        this.payrollService = new PayrollService();
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
        // Use Locale for Philippines to get "PHP" or "₱" symbol
        // If "en-PH" is available, it typically uses "PHP" or "₱"
        // Alternatively, we can force a DecimalFormat with "PHP " prefix
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
        tableModel.setRowCount(0);

        int selectedMonth = monthComboBox.getSelectedIndex() + 1;
        int selectedYear = (Integer) yearComboBox.getSelectedItem();

        List<Payslip> payslips = payrollService.generateMonthlyReport(selectedMonth, selectedYear);
        
        double totalBasic = 0, totalAllowances = 0, totalGross = 0;
        double totalSss = 0, totalPhilHealth = 0, totalPagIbig = 0, totalTax = 0, totalNet = 0;

        for (Payslip payslip : payslips) {
            Object[] rowData = {
                payslip.getEmployee().getEmployeeId(),
                payslip.getEmployee().getLastName() + ", " + payslip.getEmployee().getFirstName(),
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

            totalBasic += payslip.getBasicPay();
            totalAllowances += payslip.getAllowances();
            totalGross += payslip.getGrossSalary();
            totalSss += payslip.getSssDeduction();
            totalPhilHealth += payslip.getPhilHealthDeduction();
            totalPagIbig += payslip.getPagIbigDeduction();
            totalTax += payslip.getWithholdingTax();
            totalNet += payslip.getNetSalary();
        }

        Object[] totalRow = {
            "TOTAL", "", 
            totalBasic, 
            totalAllowances, 
            totalGross, 
            totalSss, 
            totalPhilHealth, 
            totalPagIbig, 
            totalTax, 
            totalNet
        };
        tableModel.addRow(totalRow);
    }
}
