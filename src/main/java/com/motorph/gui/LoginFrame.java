package com.motorph.gui;

import com.motorph.model.User;
import com.motorph.service.AuthenticationService;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticationService authService;

    public LoginFrame() {
        this.authService = new AuthenticationService();
        System.out.println("LoginFrame: Constructor started.");
        setTitle("MotorPH Login");
        // setSize(400, 250); // Removed in favor of pack()
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Welcome to MotorPH", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticate());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(loginButton, gbc);

        add(panel, BorderLayout.CENTER);
        
        // Add Enter key listener for password field
        getRootPane().setDefaultButton(loginButton);
        
        setSize(400, 250);
        setLocationRelativeTo(null); // Center on screen
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        User validUser = authService.authenticate(username, password);

        if (validUser != null) {
            // Launch Main Frame with authenticated user
            User finalUser = validUser;
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame mainFrame = new MainFrame(finalUser);
                    mainFrame.setVisible(true);
                    dispose(); // Close Login Frame only if MainFrame launches successfully
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Error launching application: " + e.getMessage(), 
                        "System Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
