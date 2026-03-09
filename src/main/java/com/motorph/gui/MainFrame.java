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
        panelMap.put("Deductions", new DeductionsPanel());
        panelMap.put("Settings", new SettingsPanel());

        // Sidebar
        createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // Search Bar (Center)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        searchPanel.setBackground(Color.WHITE);
        JTextField searchField = new JTextField(30);
        searchField.setText("Search...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.add(searchField);
        
        // User Profile & Logout (Right)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        userPanel.setBackground(Color.WHITE);
        
        JButton userBtn = new JButton(currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userBtn.setBackground(new Color(240, 240, 240));
        userBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        logoutBtn.setPreferredSize(new Dimension(80, 30));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        userPanel.add(userBtn);
        userPanel.add(logoutBtn);

        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(250, 250, 250)); // Light background
        
        for (Map.Entry<String, JPanel> entry : panelMap.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }

        // Wrap content panel in a container to respect header/sidebar
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(centerContainer, BorderLayout.CENTER);

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
        sidebarPanel.setBackground(new Color(245, 247, 250)); // Light Grey background
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220))); // Right border

        // Logo / Title in Sidebar
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        logoPanel.setBackground(new Color(245, 247, 250));
        logoPanel.setMaximumSize(new Dimension(250, 80));
        
        // Simple Logo Icon (Text based for now)
        JLabel logoIcon = new JLabel("MP");
        logoIcon.setOpaque(true);
        logoIcon.setBackground(new Color(44, 62, 80));
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setFont(new Font("Arial", Font.BOLD, 16));
        logoIcon.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel titleLabel = new JLabel("<html><b>MotorPH</b><br><font size='2' color='gray'>Employee Payroll System</font></html>");
        
        logoPanel.add(logoIcon);
        logoPanel.add(titleLabel);
        sidebarPanel.add(logoPanel);
        
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Navigation Buttons based on Role Permissions
        if (currentUser.canAccess("Dashboard")) addNavButton("Dashboard", ">");
        if (currentUser.canAccess("Employees")) addNavButton("Employees", ">");
        if (currentUser.canAccess("Time Logs") || currentUser.canAccess("My Time Logs")) addNavButton("Time Logs", ">");
        if (currentUser.canAccess("Payroll") || currentUser.canAccess("My Payslip")) addNavButton("Payroll", ">");
        if (currentUser.canAccess("Reports")) addNavButton("Reports", ">");
        if (currentUser.canAccess("Leave Requests")) addNavButton("Leave Requests", ">");
        
        // Settings and System Tools for Admin/IT
        if (currentUser.canAccess("Settings") || currentUser.canAccess("System Tools")) {
             addNavButton("Settings", ">");
        }
        if (currentUser.canAccess("Deductions")) {
             addNavButton("Deductions", ">");
        }

        // Payroll Week Section at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JPanel payrollWeekPanel = new JPanel();
        payrollWeekPanel.setLayout(new BoxLayout(payrollWeekPanel, BoxLayout.Y_AXIS));
        payrollWeekPanel.setBackground(new Color(245, 247, 250));
        payrollWeekPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        payrollWeekPanel.setMaximumSize(new Dimension(220, 100));
        
        JLabel pwTitle = new JLabel("Payroll Week");
        pwTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        pwTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel pwDate = new JLabel("Apr 16 - Apr 22, 2026");
        pwDate.setFont(new Font("Arial", Font.BOLD, 13));
        pwDate.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton changeWeekBtn = new JButton("Change Week");
        changeWeekBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeWeekBtn.setBackground(new Color(230, 230, 230));
        
        payrollWeekPanel.add(pwTitle);
        payrollWeekPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        payrollWeekPanel.add(pwDate);
        payrollWeekPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        payrollWeekPanel.add(changeWeekBtn);
        
        sidebarPanel.add(payrollWeekPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private void addNavButton(String name, String icon) {
        JButton button = new JButton(icon + "  " + name);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));
        button.setFocusPainted(false);
        button.setBackground(new Color(245, 247, 250));
        button.setForeground(new Color(80, 80, 80));
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(235, 237, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(245, 247, 250));
            }
        });

        // Simple action handling
        button.addActionListener(e -> {
            if (panelMap.containsKey(name)) {
                showPanel(name);
            } else {
                // For mock buttons like Deductions/Settings
                JOptionPane.showMessageDialog(this, name + " feature is coming soon.");
            }
        });

        sidebarPanel.add(button);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    // private void createHeader() {
    //    // Redundant method removed as logic is now in constructor, keeping method signature to avoid compilation error if called elsewhere (though not called in this file)
    // }

    public void showPanel(String name) {
        if (panelMap.containsKey(name)) {
            cardLayout.show(contentPanel, name);
        } else {
            // Mapping for Quick Action names that might not match exact panel keys
            switch (name) {
                case "Add Employee": showPanel("Employees"); break;
                case "Upload Logs": showPanel("Time Logs"); break;
                case "Gen Payroll": showPanel("Payroll"); break;
                case "Export Report": showPanel("Reports"); break;
                default: 
                    JOptionPane.showMessageDialog(this, name + " panel not found or feature coming soon.");
            }
        }
    }
}
