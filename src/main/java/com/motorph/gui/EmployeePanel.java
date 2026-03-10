package com.motorph.gui;

import com.motorph.model.Employee;
import com.motorph.model.TimeLog;
import com.motorph.service.EmployeeService;
import com.motorph.service.TimeLogService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class EmployeePanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private EmployeeService employeeService;
    
    // Time Logs Table (Nested inside Employee Panel)
    private JTable timeLogTable;
    private DefaultTableModel timeLogModel;
    private TimeLogService timeLogService;

    // Form Fields
    private JTextField txtEmployeeId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtBirthday;
    private JTextField txtAddress;
    private JTextField txtPhoneNumber;
    private JTextField txtSss;
    private JTextField txtPhilHealth;
    private JTextField txtTin;
    private JTextField txtPagIbig;
    private JTextField txtStatus;
    private JTextField txtPosition;
    private JTextField txtSupervisor;
    private JTextField txtBasicSalary;
    private JTextField txtRiceSubsidy;
    private JTextField txtPhoneAllowance;
    private JTextField txtClothingAllowance;
    private JTextField txtGrossRate;
    private JTextField txtHourlyRate;

    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private boolean canAddEmployee; // Store the permission flag

    public EmployeePanel(boolean canAddEmployee) {
        this.canAddEmployee = canAddEmployee;
        this.employeeService = new EmployeeService();
        this.timeLogService = new TimeLogService();
        setLayout(new BorderLayout(10, 10)); // Added gap
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP: Table Panel ---
        // Table
        String[] columns = {"ID", "Last Name", "First Name", "Position", "Supervisor", "SSS", "PhilHealth", "TIN", "Pag-IBIG", "Basic Salary", "Hourly Rate"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEmployeeToForm();
            }
        });
        JScrollPane tableScroll = new JScrollPane(employeeTable);
        tableScroll.setPreferredSize(new Dimension(800, 200));

        // --- BOTTOM: Form Panel ---
        JPanel formPanel = createFormPanel();
        
        // --- BUTTONS ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton newButton = new JButton("New Employee");
        newButton.addActionListener(e -> openNewEmployeeFrame());
        newButton.setVisible(canAddEmployee); // Visibility based on permission
        
        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> updateEmployee());

        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteEmployee());

        clearButton = new JButton("Clear Selection");
        clearButton.addActionListener(e -> clearForm());

        actionPanel.add(newButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(clearButton);

        // Combine Form and Buttons
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.add(formPanel, BorderLayout.CENTER);
        detailsPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // --- TIME LOGS PANEL (New) ---
        JPanel timeLogPanel = createTimeLogPanel();

        // Use Tabbed Pane for Details vs Logs
        JTabbedPane bottomTabs = new JTabbedPane();
        bottomTabs.addTab("Employee Details", detailsPanel);
        bottomTabs.addTab("Attendance History", timeLogPanel);

        // Main Layout using SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, bottomTabs);
        splitPane.setResizeWeight(0.4); // 40% split for table
        splitPane.setDividerLocation(250); // Initial height for table
        
        add(splitPane, BorderLayout.CENTER);

        loadEmployeeData();
    }

    public EmployeePanel() {
        this(false); // Default to no add permission
    }
    
    private JPanel createTimeLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        String[] logCols = {"Date", "Time In", "Time Out", "Hours"};
        timeLogModel = new DefaultTableModel(logCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        timeLogTable = new JTable(timeLogModel);
        panel.add(new JScrollPane(timeLogTable), BorderLayout.CENTER);
        
        return panel;
    }

    private void loadEmployeeTimeLogs(String empId) {
        timeLogModel.setRowCount(0);
        // Get logs for the specific employee ID
        List<TimeLog> logs = timeLogService.getTimeLogsForEmployee(empId);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        if (logs.isEmpty()) {
            // Optional: Add a placeholder row or just leave it empty
            // timeLogModel.addRow(new Object[]{"No records found", "-", "-", "-"});
        }

        for (TimeLog log : logs) {
            String paidTime = "-";
            double hours = timeLogService.calculateHoursWorked(log);
            if (hours > 0) {
                paidTime = String.format("%.2f", hours);
            }
            
            timeLogModel.addRow(new Object[]{
                log.getDate().format(dateFormatter),
                log.getTimeIn() != null ? log.getTimeIn().format(timeFormatter) : "-",
                log.getTimeOut() != null ? log.getTimeOut().format(timeFormatter) : "-",
                paidTime
            });
        }
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize Fields
        txtEmployeeId = new JTextField(15); txtEmployeeId.setEditable(false);
        txtFirstName = new JTextField(15); txtFirstName.setEditable(false);
        txtLastName = new JTextField(15); txtLastName.setEditable(false);
        txtBirthday = new JTextField(10); txtBirthday.setEditable(false);
        txtAddress = new JTextField(20); txtAddress.setEditable(false);
        txtPhoneNumber = new JTextField(12); txtPhoneNumber.setEditable(false);
        txtSss = new JTextField(12); txtSss.setEditable(false);
        txtPhilHealth = new JTextField(12); txtPhilHealth.setEditable(false);
        txtTin = new JTextField(12); txtTin.setEditable(false);
        txtPagIbig = new JTextField(12); txtPagIbig.setEditable(false);
        txtStatus = new JTextField(10); txtStatus.setEditable(false);
        txtPosition = new JTextField(15); txtPosition.setEditable(false);
        txtSupervisor = new JTextField(15); txtSupervisor.setEditable(false);
        txtBasicSalary = new JTextField(10); txtBasicSalary.setEditable(false);
        txtRiceSubsidy = new JTextField(10); txtRiceSubsidy.setEditable(false);
        txtPhoneAllowance = new JTextField(10); txtPhoneAllowance.setEditable(false);
        txtClothingAllowance = new JTextField(10); txtClothingAllowance.setEditable(false);
        txtGrossRate = new JTextField(10); txtGrossRate.setEditable(false);
        txtHourlyRate = new JTextField(10); txtHourlyRate.setEditable(false);

        // Apply read-only mode if the user is NOT an admin
        boolean isAdmin = canAddEmployee; // Reusing the flag which indicates admin status
        // setFieldsEditable(isAdmin); // Removed: Fields are now always read-only in this view


        // Add Fields to Grid
        // Row 0
        addLabelAndField(panel, "Employee ID:", txtEmployeeId, gbc, 0, 0);
        addLabelAndField(panel, "Status:", txtStatus, gbc, 2, 0);

        // Row 1
        addLabelAndField(panel, "First Name:", txtFirstName, gbc, 0, 1);
        addLabelAndField(panel, "Last Name:", txtLastName, gbc, 2, 1);

        // Row 2
        addLabelAndField(panel, "Birthday:", txtBirthday, gbc, 0, 2);
        addLabelAndField(panel, "Phone:", txtPhoneNumber, gbc, 2, 2);

        // Row 3
        addLabelAndField(panel, "Address:", txtAddress, gbc, 0, 3, 3); // Span 3 cols

        // Row 4 - IDs
        addLabelAndField(panel, "SSS #:", txtSss, gbc, 0, 4);
        addLabelAndField(panel, "PhilHealth #:", txtPhilHealth, gbc, 2, 4);

        // Row 5
        addLabelAndField(panel, "TIN #:", txtTin, gbc, 0, 5);
        addLabelAndField(panel, "Pag-IBIG #:", txtPagIbig, gbc, 2, 5);

        // Row 6 - Job
        addLabelAndField(panel, "Position:", txtPosition, gbc, 0, 6);
        addLabelAndField(panel, "Supervisor:", txtSupervisor, gbc, 2, 6);

        // Row 7 - Salary
        addLabelAndField(panel, "Basic Salary:", txtBasicSalary, gbc, 0, 7);
        addLabelAndField(panel, "Hourly Rate:", txtHourlyRate, gbc, 2, 7);

        // Row 8 - Allowances
        addLabelAndField(panel, "Rice Subsidy:", txtRiceSubsidy, gbc, 0, 8);
        addLabelAndField(panel, "Phone Allow.:", txtPhoneAllowance, gbc, 2, 8);

        // Row 9
        addLabelAndField(panel, "Clothing Allow.:", txtClothingAllowance, gbc, 0, 9);
        addLabelAndField(panel, "Gross SM Rate:", txtGrossRate, gbc, 2, 9);

        return new JPanel();
    }

    private void addLabelAndField(JPanel panel, String labelText, Component field, GridBagConstraints gbc, int x, int y) {
        addLabelAndField(panel, labelText, field, gbc, x, y, 1);
    }

    private void addLabelAndField(JPanel panel, String labelText, Component field, GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = width;
        panel.add(field, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Employee Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new EmployeePanel());
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void loadEmployeeData() {
        List<Employee> employees = employeeService.getAllEmployees();
        tableModel.setRowCount(0); 
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getPosition(),
                    emp.getImmediateSupervisor(),
                    emp.getSssNumber(),
                    emp.getPhilHealthNumber(),
                    emp.getTinNumber(),
                    emp.getPagIbigNumber(),
                    emp.getBasicSalary(),
                    emp.getHourlyRate()
            });
        }
    }

    private void setFieldsEditable(boolean editable) {
        txtFirstName.setEditable(editable);
        txtLastName.setEditable(editable);
        txtBirthday.setEditable(editable);
        txtAddress.setEditable(editable);
        txtPhoneNumber.setEditable(editable);
        txtSss.setEditable(editable);
        txtPhilHealth.setEditable(editable);
        txtTin.setEditable(editable);
        txtPagIbig.setEditable(editable);
        txtStatus.setEditable(editable);
        txtPosition.setEditable(editable);
        txtSupervisor.setEditable(editable);
        txtBasicSalary.setEditable(editable);
        txtRiceSubsidy.setEditable(editable);
        txtPhoneAllowance.setEditable(editable);
        txtClothingAllowance.setEditable(editable);
        txtGrossRate.setEditable(editable);
        txtHourlyRate.setEditable(editable);
    }

    private void loadSelectedEmployeeToForm() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            String empId = (String) tableModel.getValueAt(selectedRow, 0);
            
            Optional<Employee> empOpt = employeeService.getEmployeeById(empId);
            
            if (empOpt.isPresent()) {
                Employee e = empOpt.get();
                txtEmployeeId.setText(e.getEmployeeId());
                txtFirstName.setText(e.getFirstName());
                txtLastName.setText(e.getLastName());
                txtBirthday.setText(e.getBirthday());
                txtAddress.setText(e.getAddress());
                txtPhoneNumber.setText(e.getPhoneNumber());
                txtSss.setText(e.getSssNumber());
                txtPhilHealth.setText(e.getPhilHealthNumber());
                txtTin.setText(e.getTinNumber());
                txtPagIbig.setText(e.getPagIbigNumber());
                txtStatus.setText(e.getStatus());
                txtPosition.setText(e.getPosition());
                txtSupervisor.setText(e.getImmediateSupervisor());
                txtBasicSalary.setText(String.valueOf(e.getBasicSalary()));
                txtRiceSubsidy.setText(String.valueOf(e.getRiceSubsidy()));
                txtPhoneAllowance.setText(String.valueOf(e.getPhoneAllowance()));
                txtClothingAllowance.setText(String.valueOf(e.getClothingAllowance()));
                txtGrossRate.setText(String.valueOf(e.getGrossSemiMonthlyRate()));
                txtHourlyRate.setText(String.valueOf(e.getHourlyRate()));

                // Enable update/delete buttons ONLY if user is Admin
                updateButton.setEnabled(canAddEmployee); 
                deleteButton.setEnabled(canAddEmployee);
                
                // Load Time Logs for selected employee
                loadEmployeeTimeLogs(e.getEmployeeId());
            } 
        }
    }

    private void clearForm() {
        employeeTable.clearSelection();
        txtEmployeeId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtBirthday.setText("");
        txtAddress.setText("");
        txtPhoneNumber.setText("");
        txtSss.setText("");
        txtPhilHealth.setText("");
        txtTin.setText("");
        txtPagIbig.setText("");
        txtStatus.setText("");
        txtPosition.setText("");
        txtSupervisor.setText("");
        txtBasicSalary.setText("");
        txtRiceSubsidy.setText("");
        txtPhoneAllowance.setText("");
        txtClothingAllowance.setText("");
        txtGrossRate.setText("");
        txtHourlyRate.setText("");
        
        if (timeLogModel != null) {
            timeLogModel.setRowCount(0);
        }

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void updateEmployee() {
        String empId = txtEmployeeId.getText();
        if (empId.isEmpty()) return;

        Optional<Employee> empOpt = employeeService.getEmployeeById(empId);
        if (empOpt.isPresent()) {
            UpdateEmployeeFrame updateFrame = new UpdateEmployeeFrame(empOpt.get(), () -> {
                loadEmployeeData();
                loadSelectedEmployeeToForm(); // Refresh the form with new data
            });
            updateFrame.setVisible(true);
        }
    }

    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        return Double.parseDouble(text.replace(",", "").trim());
    }

    private void deleteEmployee() {
        String id = txtEmployeeId.getText();
        if (id.isEmpty()) {
            // Try to get from table selection if form is empty but row is selected (though listener usually syncs them)
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow != -1) {
                id = (String) tableModel.getValueAt(selectedRow, 0);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete employee ID: " + id + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            employeeService.deleteEmployee(id);
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            loadEmployeeData(); // Refresh Table
            clearForm();
        }
    }

    private void openNewEmployeeFrame() {
        NewEmployeeFrame newEmployeeFrame = new NewEmployeeFrame(() -> loadEmployeeData());
        newEmployeeFrame.setVisible(true);
    }
}
