package com.motorph.gui;

import com.motorph.model.Employee;
import com.motorph.model.LeaveRequest;
import com.motorph.model.User;
import com.motorph.service.LeaveService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LeavePanel extends JPanel {
    private User currentUser;
    private LeaveService leaveService;
    private JTabbedPane tabbedPane;
    
    // My Leaves Components
    private JTable myLeavesTable;
    private DefaultTableModel myLeavesModel;

    // Manage Leaves Components
    private JTable manageLeavesTable;
    private DefaultTableModel manageLeavesModel;

    public LeavePanel(User user) {
        this.currentUser = user;
        this.leaveService = new LeaveService();
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
        return leaveService.canManageLeaves(currentUser);
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
        
        // Strict Role Check: Only Admin can approve/reject
        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("Admin");
        
        JButton approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(46, 204, 113));
        approveButton.setForeground(Color.WHITE);
        approveButton.setEnabled(isAdmin); // Enabled only for Admin
        approveButton.addActionListener(e -> processLeave(true));
        
        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(231, 76, 60));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setEnabled(isAdmin); // Enabled only for Admin
        rejectButton.addActionListener(e -> processLeave(false));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadManageLeaves());

        toolbar.add(approveButton);
        toolbar.add(rejectButton);
        toolbar.add(refreshButton);
        
        if (!isAdmin) {
            JLabel notice = new JLabel(" (View Only mode for non-Admins)");
            notice.setForeground(Color.GRAY);
            toolbar.add(notice);
        }
        
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
        List<LeaveRequest> requests = leaveService.getLeaveRequestsByEmployee(currentUser.getUsername());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LeaveRequest req : requests) {
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

    private void loadManageLeaves() {
        manageLeavesModel.setRowCount(0);
        List<LeaveRequest> requests = leaveService.getPendingLeaveRequests();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LeaveRequest req : requests) {
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

    private void showRequestLeaveDialog() {
        java.util.Optional<Employee> empOpt = leaveService.getEmployee(currentUser.getUsername());
        
        // If not found, check if it's the specific "admin" account and allow mock submission or warn
        if (!empOpt.isPresent()) {
            if (currentUser.getRole().equalsIgnoreCase("Admin")) {
                String empId = JOptionPane.showInputDialog(this, "Admin Employee ID not found.\nEnter Employee ID to file leave for:", "File Leave", JOptionPane.QUESTION_MESSAGE);
                if (empId != null && !empId.trim().isEmpty()) {
                     empOpt = leaveService.getEmployee(empId);
                     if (!empOpt.isPresent()) {
                         JOptionPane.showMessageDialog(this, "Employee ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                         return;
                     }
                } else {
                    return; // Cancelled
                }
            } else {
                JOptionPane.showMessageDialog(this, "Only registered employees can submit leave requests.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Employee emp = empOpt.get(); // We have an employee record now

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
                
                leaveService.submitLeaveRequest(
                    emp.getEmployeeId(),
                    (String) typeBox.getSelectedItem(),
                    start,
                    end,
                    reasonField.getText()
                );

                JOptionPane.showMessageDialog(dialog, "Leave request submitted for " + emp.getFirstName() + " " + emp.getLastName());
                dialog.dispose();
                loadMyLeaves();
                if (canManageLeaves()) loadManageLeaves();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
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
        
        try {
            leaveService.processLeaveRequest(requestId, approve, currentUser.getUsername());
            loadManageLeaves();
            loadMyLeaves();
            JOptionPane.showMessageDialog(this, "Request " + (approve ? "Approved" : "Rejected"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing request: " + e.getMessage());
        }
    }
}
