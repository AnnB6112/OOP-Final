package com.motorph.gui;

import com.motorph.data.DataStore;
import com.motorph.model.TimeLog;
import com.motorph.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimeLogPanel extends JPanel {
    private JTable timeLogTable;
    private DefaultTableModel tableModel;
    private DataStore dataStore;
    private User currentUser;

    private JButton timeInButton;
    private JButton timeOutButton;
    private JLabel statusLabel;

    public TimeLogPanel(User user) {
        this.currentUser = user;
        this.dataStore = DataStore.getInstance();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Title and Controls
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Employee Time Logs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Controls (Time In / Time Out) - Only for Employees
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeInButton = new JButton("Time In");
        timeOutButton = new JButton("Time Out");
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLUE);

        timeInButton.addActionListener(e -> performTimeIn());
        timeOutButton.addActionListener(e -> performTimeOut());

        controlPanel.add(timeInButton);
        controlPanel.add(timeOutButton);
        controlPanel.add(statusLabel);

        // Only show controls if user is linked to an employee record (not generic admin)
        if (dataStore.findEmployeeById(currentUser.getUsername()).isPresent()) {
            topPanel.add(controlPanel, BorderLayout.CENTER);
            updateButtonState();
        }

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Employee ID", "Last Name", "First Name", "Date", "Time In", "Time Out"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timeLogTable = new JTable(tableModel);
        
        loadTimeLogs();

        JScrollPane scrollPane = new JScrollPane(timeLogTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateButtonState() {
        java.time.LocalDate today = java.time.LocalDate.now();
        List<TimeLog> logs = dataStore.getTimeLogs();
        TimeLog todayLog = null;

        for (TimeLog log : logs) {
            if (log.getEmployeeId().equals(currentUser.getUsername()) && log.getDate().equals(today)) {
                todayLog = log;
                break;
            }
        }

        if (todayLog == null) {
            timeInButton.setEnabled(true);
            timeOutButton.setEnabled(false);
            statusLabel.setText("Status: Not yet timed in today.");
        } else if (todayLog.getTimeOut() == null) {
            timeInButton.setEnabled(false);
            timeOutButton.setEnabled(true);
            statusLabel.setText("Status: Timed in at " + todayLog.getTimeIn().format(DateTimeFormatter.ofPattern("hh:mm a")));
        } else {
            timeInButton.setEnabled(false);
            timeOutButton.setEnabled(false);
            statusLabel.setText("Status: Attendance completed for today.");
        }
    }

    private void performTimeIn() {
        java.util.Optional<com.motorph.model.Employee> empOpt = dataStore.findEmployeeById(currentUser.getUsername());
        if (empOpt.isPresent()) {
            com.motorph.model.Employee emp = empOpt.get();
            TimeLog newLog = new TimeLog(
                emp.getEmployeeId(),
                emp.getLastName(),
                emp.getFirstName(),
                java.time.LocalDate.now(),
                java.time.LocalTime.now(),
                null
            );
            dataStore.addTimeLog(newLog);
            JOptionPane.showMessageDialog(this, "Timed In Successfully!");
            loadTimeLogs();
            updateButtonState();
        } else {
            JOptionPane.showMessageDialog(this, "Error: Employee record not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performTimeOut() {
        java.time.LocalDate today = java.time.LocalDate.now();
        List<TimeLog> logs = dataStore.getTimeLogs();
        for (TimeLog log : logs) {
            if (log.getEmployeeId().equals(currentUser.getUsername()) && log.getDate().equals(today)) {
                log.setTimeOut(java.time.LocalTime.now());
                dataStore.updateTimeLog(log);
                JOptionPane.showMessageDialog(this, "Timed Out Successfully!");
                loadTimeLogs();
                updateButtonState();
                return;
            }
        }
    }

    private void loadTimeLogs() {
        List<TimeLog> logs = dataStore.getTimeLogs();
        tableModel.setRowCount(0);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        
        boolean isAdminOrHR = currentUser.getRole().equalsIgnoreCase("Admin") || 
                              currentUser.getRole().equalsIgnoreCase("HR");

        for (TimeLog log : logs) {
            // Filter: Show all for Admin/HR, otherwise only own logs
            if (isAdminOrHR || log.getEmployeeId().equals(currentUser.getUsername())) {
                tableModel.addRow(new Object[]{
                    log.getEmployeeId(),
                    log.getLastName(),
                    log.getFirstName(),
                    log.getDate().toString(),
                    log.getTimeIn() != null ? log.getTimeIn().format(timeFormatter) : "-",
                    log.getTimeOut() != null ? log.getTimeOut().format(timeFormatter) : "-"
                });
            }
        }
    }
}
