package com.motorph.gui;

import com.motorph.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private Map<String, JPanel> panelMap;
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("MotorPH Employee Payroll System - " + user.getRole());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize Panels
        panelMap = new HashMap<>();
        panelMap.put("Dashboard", new DashboardPanel());
        panelMap.put("Employees", new EmployeePanel());
        panelMap.put("Payroll", new PayrollPanel());
        panelMap.put("Time Logs", new TimeLogPanel(currentUser));
        panelMap.put("Leave Requests", new LeavePanel(currentUser));
        panelMap.put("Reports", new PayrollSummaryPanel());

        // Sidebar
        createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        for (Map.Entry<String, JPanel> entry : panelMap.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }

        add(contentPanel, BorderLayout.CENTER);

        // Show Dashboard by default
        showPanel("Dashboard");
    }

    public MainFrame() {
        // Fallback constructor for testing or default admin
        this(new com.motorph.model.AdminUser("admin", "admin"));
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(44, 62, 80)); // Dark Blue/Grey
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Logo / Title in Sidebar
        JLabel titleLabel = new JLabel("MotorPH");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(titleLabel);
        
        JLabel roleLabel = new JLabel("(" + currentUser.getRole() + ")");
        roleLabel.setForeground(Color.LIGHT_GRAY);
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(roleLabel);
        
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Navigation Buttons based on Role Permissions
        if (currentUser.canAccess("Dashboard")) addNavButton("Dashboard");
        if (currentUser.canAccess("Employees")) addNavButton("Employees");
        if (currentUser.canAccess("Payroll") || currentUser.canAccess("My Payslip")) addNavButton("Payroll");
        if (currentUser.canAccess("Time Logs") || currentUser.canAccess("My Time Logs")) addNavButton("Time Logs");
        if (currentUser.canAccess("Leave Requests")) addNavButton("Leave Requests");
        if (currentUser.canAccess("Reports")) addNavButton("Reports");
        
        // Logout Button at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(180, 40));
        logoutButton.setBackground(new Color(192, 57, 43));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private void addNavButton(String name) {
        JButton button = new JButton(name);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(44, 62, 80));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }
        });

        button.addActionListener(e -> showPanel(name));

        sidebarPanel.add(button);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }
}
