package com.motorph.gui;

import com.motorph.data.DataStore;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private DataStore dataStore;

    public DashboardPanel() {
        this.dataStore = DataStore.getInstance();
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(250, 250, 250));

        // Title
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Safety check for dataStore
        String employeeCount = "0";
        try {
            if (dataStore != null && dataStore.getEmployees() != null) {
                employeeCount = String.valueOf(dataStore.getEmployees().size());
            }
        } catch (Exception e) {
            employeeCount = "Error";
            e.printStackTrace();
        }

        // --- Top Cards (Row 0) ---
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 10);
        contentPanel.add(createStatCard("Total Employees", employeeCount, "Active Employees", ">"), gbc);
        
        gbc.gridx = 1; gbc.insets = new Insets(0, 10, 20, 10);
        // Mocking Total Hours for "This Week"
        contentPanel.add(createStatCard("Total Hours", "1,240 hrs", "(This Week)", ">"), gbc);
        
        gbc.gridx = 2;
        // Mocking Gross Payroll
        contentPanel.add(createStatCard("Gross Payroll", "PHP 865,500.00", "^ 5% vs last week", ">"), gbc);
        
        gbc.gridx = 3; gbc.insets = new Insets(0, 10, 20, 0);
        // Mocking Net Payroll
        contentPanel.add(createStatCard("Net Payroll", "PHP 742,300.00", "^ 5% vs last week", ">"), gbc);

        // --- Middle Section (Row 1) ---
        
        // Attendance Summary (Donut Chart placeholder)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 20, 10);
        contentPanel.add(createSectionPanel("Attendance Summary", createAttendanceContent()), gbc);
        
        // Recent Activity
        gbc.gridx = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 20, 0);
        contentPanel.add(createSectionPanel("Recent Activity", createActivityContent()), gbc);

        // --- Bottom Section (Row 2) ---
        
        // Quick Actions
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPanel.add(createSectionPanel("Quick Actions", createQuickActionsContent()), gbc);
        
        // Alerts
        gbc.gridx = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 0, 0);
        contentPanel.add(createSectionPanel("Alerts", createAlertsContent()), gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, String subtitle, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setBackground(Color.WHITE);
        header.add(iconLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        header.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        if (subtitle.contains("↑")) subLabel.setForeground(new Color(46, 204, 113));
        else subLabel.setForeground(Color.GRAY);

        card.add(header, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createSectionPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        
        return panel;
    }

    private JComponent createAttendanceContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Mock Chart (Left)
        JLabel chartPlaceholder = new JLabel("82%", SwingConstants.CENTER);
        chartPlaceholder.setFont(new Font("Arial", Font.BOLD, 40));
        chartPlaceholder.setForeground(Color.DARK_GRAY);
        panel.add(chartPlaceholder);
        
        // Legend (Right)
        JPanel legend = new JPanel(new GridLayout(3, 1, 5, 5));
        legend.setBackground(Color.WHITE);
        legend.add(new JLabel("* On-Time  82%"));
        legend.add(new JLabel("* Late     15%"));
        legend.add(new JLabel("* Absent    3%"));
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(legend, BorderLayout.CENTER);
        
        JButton viewLogsBtn = new JButton("View Logs >");
        viewLogsBtn.setContentAreaFilled(false);
        viewLogsBtn.setBorderPainted(false);
        viewLogsBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        viewLogsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        viewLogsBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ((MainFrame) window).showPanel("Time Logs");
            }
        });
        
        rightPanel.add(viewLogsBtn, BorderLayout.SOUTH);
        
        panel.add(rightPanel);
        return panel;
    }

    private JComponent createActivityContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        panel.add(new JLabel("- Time logs imported - Feb 22, 2026"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("- Payroll computed - Week 7"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("- Employee 10012 updated (Rate)"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("- New employee added - 10035"));
        
        panel.add(Box.createVerticalGlue());
        
        JButton viewAllBtn = new JButton("View All >");
        viewAllBtn.setBackground(new Color(240, 240, 240));
        viewAllBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(viewAllBtn);
        
        panel.add(btnPanel);
        return panel;
    }

    private JComponent createQuickActionsContent() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(Color.WHITE);
        
        panel.add(createActionButton("Add Employee", "+", "Employees"));
        panel.add(createActionButton("Upload Logs", "^", "Time Logs"));
        panel.add(createActionButton("Gen Payroll", "*", "Payroll"));
        panel.add(createActionButton("Export Report", "v", "Reports"));
        
        return panel;
    }
    
    private JButton createActionButton(String text, String icon, String targetPanel) {
        JButton btn = new JButton(icon + " " + text);
        btn.setPreferredSize(new Dimension(140, 50));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            // Find MainFrame and switch panel
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ((MainFrame) window).showPanel(targetPanel);
            }
        });
        
        return btn;
    }

    private JComponent createAlertsContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        JLabel alert1 = new JLabel("! 3 employees missing time-out");
        alert1.setForeground(new Color(231, 76, 60));
        panel.add(alert1);
        panel.add(Box.createVerticalStrut(10));
        
        JLabel alert2 = new JLabel("! 2 employees exceeded 48 hrs");
        alert2.setForeground(new Color(241, 196, 15));
        panel.add(alert2);
        
        return panel;
    }
}
