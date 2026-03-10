package com.motorph.gui;

import com.motorph.model.TimeLog;
import com.motorph.model.User;
import com.motorph.service.TimeLogService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TimeLogPanel extends JPanel {
    private JTable timeLogTable;
    private DefaultTableModel tableModel;
    private TimeLogService timeLogService;
    private User currentUser;

    private JButton timeInButton;
    private JButton timeOutButton;
    private JLabel statusLabel;

    public TimeLogPanel(User user) {
        this.currentUser = user;
        this.timeLogService = new TimeLogService();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Title and Controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Time Logs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Sub-header controls (Filters)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        
        JButton importBtn = new JButton("Import Time Logs");
        importBtn.setBackground(new Color(240, 240, 240));
        
        JTextField searchField = new JTextField(15);
        searchField.setText("Search...");
        
        JTextField searchEmpField = new JTextField(10);
        searchEmpField.setText("Search Emp #");
        
        filterPanel.add(importBtn);
        filterPanel.add(searchField);
        filterPanel.add(searchEmpField);
        
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Controls (Time In / Time Out) - Only for Employees (Keeping existing logic but styling)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(Color.WHITE);
        
        timeInButton = new JButton("Time In");
        timeInButton.setBackground(new Color(46, 204, 113));
        timeInButton.setForeground(Color.WHITE);
        
        timeOutButton = new JButton("Time Out");
        timeOutButton.setBackground(new Color(231, 76, 60));
        timeOutButton.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);

        timeInButton.addActionListener(e -> performTimeIn());
        timeOutButton.addActionListener(e -> performTimeOut());

        controlPanel.add(statusLabel);
        controlPanel.add(timeInButton);
        controlPanel.add(timeOutButton);

        // Only show controls if user is linked to an employee record (not generic admin)
        // We use TimeLogService to check this implicitly or add a helper in Service.
        // For now, let's just check if they are not Admin or if they are Admin but want to test features.
        // Actually, the requirement is "only show controls if user is linked to an employee record".
        // Let's assume non-Admins are always employees or have records.
        // Better: Check if employee exists via a new service method or just try to get logs.
        // Let's use a helper method.
        
        if (canShowTimeControls()) {
            topPanel.add(controlPanel, BorderLayout.EAST);
            updateButtonState();
        }

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Emp #", "Name", "Date", "Time In", "Time Out", "Paid Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timeLogTable = new JTable(tableModel);
        timeLogTable.setRowHeight(30);
        timeLogTable.getTableHeader().setBackground(new Color(245, 247, 250));
        timeLogTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        loadTimeLogs();

        JScrollPane scrollPane = new JScrollPane(timeLogTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        // Pagination (Mock)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel legend = new JLabel("Legend: Four"); // From image
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pagination.setBackground(Color.WHITE);
        pagination.add(new JButton("Prev"));
        pagination.add(new JLabel("1 - 10 of 26 time logs displayed"));
        pagination.add(new JButton("Next"));
        
        footerPanel.add(legend, BorderLayout.WEST);
        footerPanel.add(pagination, BorderLayout.EAST);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    private boolean canShowTimeControls() {
        // Only show for Employee/RegularUser or if the current user has a linked employee record
        // Since we don't have direct access to DataStore here anymore (clean architecture),
        // we can check role or add a method to TimeLogService.
        // For simplicity: Regular users always see it. Admins/HR only if they are also employees (which they might be).
        // Let's assume if they are NOT "Admin" or "HR" or "IT" purely, they see it.
        // OR better: Ask TimeLogService if an employee record exists for this username.
        return timeLogService.hasEmployeeRecord(currentUser.getUsername());
    }

    private void updateButtonState() {
        try {
            Optional<TimeLog> todayLog = timeLogService.getTodayLog(currentUser.getUsername());
            
            if (!todayLog.isPresent()) {
                // No log for today -> Can Time In
                timeInButton.setEnabled(true);
                timeOutButton.setEnabled(false);
                statusLabel.setText("Status: Not yet timed in.");
            } else if (todayLog.get().getTimeOut() == null) {
                // Timed in, but not out -> Can Time Out
                timeInButton.setEnabled(false);
                timeOutButton.setEnabled(true);
                statusLabel.setText("Status: Timed in at " + todayLog.get().getTimeIn());
            } else {
                // Completed for today
                timeInButton.setEnabled(false);
                timeOutButton.setEnabled(false);
                statusLabel.setText("Status: Completed for today.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performTimeIn() {
        try {
            timeLogService.timeIn(currentUser.getUsername());
            JOptionPane.showMessageDialog(this, "Timed in successfully!");
            updateButtonState();
            loadTimeLogs();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void performTimeOut() {
        try {
            timeLogService.timeOut(currentUser.getUsername());
            JOptionPane.showMessageDialog(this, "Timed out successfully!");
            updateButtonState();
            loadTimeLogs();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadTimeLogs() {
        List<TimeLog> logs = timeLogService.getAllTimeLogs();
        tableModel.setRowCount(0);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        boolean isAdminOrHR = currentUser.getRole().equalsIgnoreCase("Admin") || 
                              currentUser.getRole().equalsIgnoreCase("HR");

        for (TimeLog log : logs) {
            // Filter: Show all for Admin/HR, otherwise only own logs
            if (isAdminOrHR || log.getEmployeeId().equals(currentUser.getUsername())) {
                
                String status = timeLogService.isLate(log) ? "Late" : "On time";
                
                String paidTime = "-";
                double hours = timeLogService.calculateHoursWorked(log);
                if (hours > 0) {
                    paidTime = String.format("%.2f hrs", hours);
                }

                // Add full details to table
                tableModel.addRow(new Object[]{
                    log.getEmployeeId(),
                    log.getLastName() + ", " + log.getFirstName(), // Combined Name
                    log.getDate().format(dateFormatter),
                    log.getTimeIn() != null ? log.getTimeIn().format(timeFormatter) : "-",
                    log.getTimeOut() != null ? log.getTimeOut().format(timeFormatter) : "-",
                    paidTime,
                    status,
                    "OK" // Mock Notes
                });
            }
        }
    }
}
