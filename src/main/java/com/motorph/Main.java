package com.motorph;

import com.motorph.gui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("Application Starting...");
        
        // Global Exception Handler for EDT
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            System.err.println("Uncaught Exception: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, 
                "An unexpected error occurred:\n" + e.getMessage() + "\n\nSee console for details.", 
                "Application Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        });

        // Launch Login Frame
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Initializing LoginFrame...");
                LoginFrame loginFrame = new LoginFrame();
                System.out.println("LoginFrame initialized. Setting visible...");
                loginFrame.setVisible(true);
                System.out.println("LoginFrame visible.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error launching LoginFrame: " + e.getMessage());
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Failed to launch login screen: " + e.getMessage(), 
                    "Launch Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
