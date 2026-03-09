package com.motorph;

import com.motorph.gui.LoginFrame;
import javax.swing.SwingUtilities;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Main {
    public static void log(String message) {
        System.out.println(message);
        try (PrintWriter out = new PrintWriter(new FileWriter("debug.log", true))) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        log("Application Starting...");
        
        // Global Exception Handler for EDT
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log("Uncaught Exception: " + e.getMessage());
            e.printStackTrace();
            try (PrintWriter out = new PrintWriter(new FileWriter("debug.log", true))) {
                e.printStackTrace(out);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        });

        // Launch Login Frame
        SwingUtilities.invokeLater(() -> {
            try {
                log("Initializing LoginFrame...");
                LoginFrame loginFrame = new LoginFrame();
                log("LoginFrame initialized. Setting visible...");
                loginFrame.setVisible(true);
                log("LoginFrame visible.");
            } catch (Exception e) {
                log("Error launching LoginFrame: " + e.getMessage());
                e.printStackTrace();
                try (PrintWriter out = new PrintWriter(new FileWriter("debug.log", true))) {
                    e.printStackTrace(out);
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        });
    }
}
