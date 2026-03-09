package com.motorph.gui;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;
import com.motorph.model.LeaveRequest;
import com.motorph.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class LeavePanel extends JPanel {
    private User currentUser;
    private DataStore dataStore;
    private JTabbedPane tabbedPane;
    
    // My Leaves Components
    private JTable myLeavesTable;
    private DefaultTableModel myLeavesModel;

    // Manage Leaves Components
    private JTable manageLeavesTable;
    private DefaultTableModel manageLeavesModel;

    public LeavePanel(User user) {
        this.currentUser = user;
        this.dataStore = DataStore.getInstance();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Leave Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        // Always show "My Leaves"
        tabbedPane.addTab("My Leaves", createMyLeavesPanel());

        // Show "Manage Leaves" only for Admin/HR/Supervisor
        if (canManageLeaves()) {
            tabbedPane.addTab("Manage Leaves", createManageLeavesPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private boolean canManageLeaves() {
        String role = currentUser.getRole();
        if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("HR")) {
            return true;
        }
        
        // Check if employee is a supervisor based on position
        return dataStore.findEmployeeById(currentUser.getUsername())
            .map(emp -> emp.getPosition().toLowerCase().contains("supervisor") || 
                        emp.getPosition().toLowerCase().contains("manager") ||
                        emp.getPosition().toLowerCase().contains("lead"))
            .orElse(false);
    }

    private JPanel createMyLeavesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton requestButton = new JButton("Request Leave");
        requestButton.addActionListener(e -> showRequestLeaveDialog());
        toolbar.add(requestButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyLeaves());
        toolbar.add(refreshButton);
        
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"Request Date", "Type", "Start Date", "End Date", "Reason", "Status", "Approver"};
        myLeavesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myLeavesTable = new JTable(myLeavesModel);
        loadMyLeaves();
        
        panel.add(new JScrollPane(myLeavesTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createManageLeavesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(46, 204, 113));
        approveButton.setForeground(Color.WHITE);
        approveButton.addActionListener(e -> processLeave(true));
        
        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(231, 76, 60));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.addActionListener(e -> processLeave(false));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadManageLeaves());

        toolbar.add(approveButton);
        toolbar.add(rejectButton);
        toolbar.add(refreshButton);
        
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Employee", "Type", "Start Date", "End Date", "Reason", "Status"};
        manageLeavesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        manageLeavesTable = new JTable(manageLeavesModel);
        loadManageLeaves();

        panel.add(new JScrollPane(manageLeavesTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadMyLeaves() {
        myLeavesModel.setRowCount(0);
        List<LeaveRequest> requests = dataStore.getLeaveRequests();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LeaveRequest req : requests) {
            if (req.getEmployeeId().equals(currentUser.getUsername())) {
                myLeavesModel.addRow(new Object[]{
                    req.getDateRequested().format(formatter),
                    req.getType(),
                    req.getStartDate().format(formatter),
                    req.getEndDate().format(formatter),
                    req.getReason(),
                    req.getStatus(),
                    req.getApproverId() != null ? req.getApproverId() : "-"
                });
            }
        }
    }

    private void loadManageLeaves() {
        manageLeavesModel.setRowCount(0);
        List<LeaveRequest> requests = dataStore.getLeaveRequests();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LeaveRequest req : requests) {
            // Only show Pending requests
            if (req.getStatus().equalsIgnoreCase("Pending")) {
                manageLeavesModel.addRow(new Object[]{
                    req.getRequestId(),
                    req.getLastName() + ", " + req.getFirstName(),
                    req.getType(),
                    req.getStartDate().format(formatter),
                    req.getEndDate().format(formatter),
                    req.getReason(),
                    req.getStatus()
                });
            }
        }
    }

    private void showRequestLeaveDialog() {
        // Check if current user is an employee
        if (!dataStore.findEmployeeById(currentUser.getUsername()).isPresent()) {
            JOptionPane.showMessageDialog(this, "Only registered employees can submit leave requests.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Request Leave", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        dialog.add(new JLabel("Leave Type:"));
        String[] types = {"Sick Leave", "Vacation Leave", "Emergency Leave"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        dialog.add(typeBox);

        dialog.add(new JLabel("Start Date (yyyy-MM-dd):"));
        JTextField startDateField = new JTextField(LocalDate.now().toString());
        dialog.add(startDateField);

        dialog.add(new JLabel("End Date (yyyy-MM-dd):"));
        JTextField endDateField = new JTextField(LocalDate.now().plusDays(1).toString());
        dialog.add(endDateField);

        dialog.add(new JLabel("Reason:"));
        JTextField reasonField = new JTextField();
        dialog.add(reasonField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            try {
                LocalDate start = LocalDate.parse(startDateField.getText());
                LocalDate end = LocalDate.parse(endDateField.getText());
                
                if (end.isBefore(start)) {
                    JOptionPane.showMessageDialog(dialog, "End date cannot be before start date.");
                    return;
                }

                // Get Employee details
                Employee emp = dataStore.findEmployeeById(currentUser.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Employee record not found"));

                LeaveRequest request = new LeaveRequest(
                    emp.getEmployeeId(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    (String) typeBox.getSelectedItem(),
                    start,
                    end,
                    reasonField.getText()
                );

                dataStore.addLeaveRequest(request);
                JOptionPane.showMessageDialog(dialog, "Leave request submitted!");
                dialog.dispose();
                loadMyLeaves();
                if (canManageLeaves()) loadManageLeaves();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format or data: " + ex.getMessage());
            }
        });
        dialog.add(submitButton);

        dialog.setVisible(true);
    }

    private void processLeave(boolean approve) {
        int selectedRow = manageLeavesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to process.");
            return;
        }

        String requestId = (String) manageLeavesModel.getValueAt(selectedRow, 0);
        List<LeaveRequest> requests = dataStore.getLeaveRequests();
        
        for (LeaveRequest req : requests) {
            if (req.getRequestId().equals(requestId)) {
                req.setStatus(approve ? "Approved" : "Rejected");
                req.setApproverId(currentUser.getUsername());
                dataStore.updateLeaveRequest(req);
                break;
            }
        }
        
        loadManageLeaves();
        loadMyLeaves();
        JOptionPane.showMessageDialog(this, "Request " + (approve ? "Approved" : "Rejected"));
    }
}
