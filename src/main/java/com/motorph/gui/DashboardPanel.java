package com.motorph.gui;

import com.motorph.data.DataStore;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private DataStore dataStore;

    public DashboardPanel() {
        this.dataStore = DataStore.getInstance();
        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createStatCard("Total Employees", String.valueOf(dataStore.getEmployees().size())));
        add(createStatCard("Total Inventory Items", String.valueOf(dataStore.getInventory().size())));
        add(createStatCard("Active Users", "3")); // Mock data
        add(createStatCard("Pending Tasks", "5")); // Mock data
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 102, 204));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}
