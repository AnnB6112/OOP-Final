package com.motorph.gui;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(250, 250, 250));

        JLabel title = new JLabel("System Settings");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(3, 1, 0, 10));
        content.setBackground(new Color(250, 250, 250));

        content.add(createSettingRow("Company Info", "MotorPH, Inc."));
        content.add(createSettingRow("Payroll Cycle", "Semi-Monthly"));
        content.add(createSettingRow("Default Currency", "PHP"));

        add(content, BorderLayout.CENTER);
    }

    private JPanel createSettingRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lbl = new JLabel(label + ": ");
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 14));
        
        row.add(lbl);
        row.add(val);
        return row;
    }
}
