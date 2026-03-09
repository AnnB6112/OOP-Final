package com.motorph.gui;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;

import javax.swing.*;
import java.awt.*;

public class NewEmployeeFrame extends JFrame {
    private JTextField idField, firstNameField, lastNameField, positionField, salaryField, rateField;
    private JTextField sssField, philHealthField, tinField, pagIbigField;
    private DataStore dataStore;
    private Runnable onSuccess;

    public NewEmployeeFrame(Runnable onSuccess) {
        this.dataStore = DataStore.getInstance();
        this.onSuccess = onSuccess;

        setTitle("New Employee");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Employee ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Position:"));
        positionField = new JTextField();
        formPanel.add(positionField);

        formPanel.add(new JLabel("Basic Salary:"));
        salaryField = new JTextField();
        formPanel.add(salaryField);

        formPanel.add(new JLabel("Hourly Rate:"));
        rateField = new JTextField();
        formPanel.add(rateField);

        formPanel.add(new JLabel("SSS Number:"));
        sssField = new JTextField();
        formPanel.add(sssField);

        formPanel.add(new JLabel("PhilHealth Number:"));
        philHealthField = new JTextField();
        formPanel.add(philHealthField);

        formPanel.add(new JLabel("TIN:"));
        tinField = new JTextField();
        formPanel.add(tinField);

        formPanel.add(new JLabel("Pag-IBIG Number:"));
        pagIbigField = new JTextField();
        formPanel.add(pagIbigField);

        add(formPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Employee");
        saveButton.addActionListener(e -> saveEmployee());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveEmployee() {
        try {
            String id = idField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String position = positionField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());
            double rate = Double.parseDouble(rateField.getText().trim());
            String sss = sssField.getText().trim();
            String ph = philHealthField.getText().trim();
            String tin = tinField.getText().trim();
            String pagIbig = pagIbigField.getText().trim();

            if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Employee newEmp = new Employee(id, firstName, lastName, position, salary, rate, sss, ph, tin, pagIbig);
            dataStore.addEmployee(newEmp);

            JOptionPane.showMessageDialog(this, "Employee saved successfully!");
            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Salary or Rate.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
