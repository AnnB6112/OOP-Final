package com.motorph.gui;

import javax.swing.*;
import java.awt.*;

public class DeductionsPanel extends JPanel {
    public DeductionsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(250, 250, 250));

        JLabel title = new JLabel("Deductions Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 3, 20, 0));
        content.setBackground(new Color(250, 250, 250));
        
        content.add(createDeductionCard("SSS", "Social Security System", "Tax Table 2024"));
        content.add(createDeductionCard("PhilHealth", "Health Insurance", "3% - 5%"));
        content.add(createDeductionCard("Pag-IBIG", "Home Development Mutual Fund", "Max P100"));

        add(content, BorderLayout.CENTER);
    }

    private JPanel createDeductionCard(String title, String desc, String rate) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        JLabel descLabel = new JLabel("<html>" + desc + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel rateLabel = new JLabel("Rate: " + rate);
        rateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        rateLabel.setForeground(Color.GRAY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(rateLabel, BorderLayout.SOUTH);

        return card;
    }
}
