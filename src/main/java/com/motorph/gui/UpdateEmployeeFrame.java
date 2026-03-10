package com.motorph.gui;

import com.motorph.model.Employee;
import com.motorph.service.EmployeeService;

import javax.swing.*;
import java.awt.*;

public class UpdateEmployeeFrame extends JFrame {
    private JTextField idField, firstNameField, lastNameField, birthdayField, addressField, phoneField;
    private JTextField sssField, philHealthField, tinField, pagIbigField;
    private JTextField statusField, positionField, supervisorField;
    private JTextField salaryField, riceField, phoneAllowField, clothingField, grossRateField, hourlyRateField;
    
    private EmployeeService employeeService;
    private Employee currentEmployee;
    private Runnable onSuccess;

    public UpdateEmployeeFrame(Employee employee, Runnable onSuccess) {
        this.currentEmployee = employee;
        this.employeeService = new EmployeeService();
        this.onSuccess = onSuccess;

        setTitle("Update Employee - " + employee.getFirstName() + " " + employee.getLastName());
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(formPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize and Add Fields
        int row = 0;
        idField = addField(formPanel, gbc, row++, "Employee ID:", employee.getEmployeeId());
        idField.setEditable(false); // ID cannot be changed
        
        statusField = addField(formPanel, gbc, row++, "Status:", employee.getStatus());
        firstNameField = addField(formPanel, gbc, row++, "First Name:", employee.getFirstName());
        lastNameField = addField(formPanel, gbc, row++, "Last Name:", employee.getLastName());
        birthdayField = addField(formPanel, gbc, row++, "Birthday:", employee.getBirthday());
        addressField = addField(formPanel, gbc, row++, "Address:", employee.getAddress());
        phoneField = addField(formPanel, gbc, row++, "Phone Number:", employee.getPhoneNumber());
        
        sssField = addField(formPanel, gbc, row++, "SSS Number:", employee.getSssNumber());
        philHealthField = addField(formPanel, gbc, row++, "PhilHealth Number:", employee.getPhilHealthNumber());
        tinField = addField(formPanel, gbc, row++, "TIN:", employee.getTinNumber());
        pagIbigField = addField(formPanel, gbc, row++, "Pag-IBIG Number:", employee.getPagIbigNumber());
        
        positionField = addField(formPanel, gbc, row++, "Position:", employee.getPosition());
        supervisorField = addField(formPanel, gbc, row++, "Immediate Supervisor:", employee.getImmediateSupervisor());
        
        salaryField = addField(formPanel, gbc, row++, "Basic Salary:", String.valueOf(employee.getBasicSalary()));
        riceField = addField(formPanel, gbc, row++, "Rice Subsidy:", String.valueOf(employee.getRiceSubsidy()));
        phoneAllowField = addField(formPanel, gbc, row++, "Phone Allowance:", String.valueOf(employee.getPhoneAllowance()));
        clothingField = addField(formPanel, gbc, row++, "Clothing Allowance:", String.valueOf(employee.getClothingAllowance()));
        grossRateField = addField(formPanel, gbc, row++, "Gross Semi-monthly Rate:", String.valueOf(employee.getGrossSemiMonthlyRate()));
        hourlyRateField = addField(formPanel, gbc, row++, "Hourly Rate:", String.valueOf(employee.getHourlyRate()));

        add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.addActionListener(e -> saveUpdates());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField field = new JTextField(value, 20);
        panel.add(field, gbc);
        return field;
    }

    private void saveUpdates() {
        try {
            // Validate required fields
            if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Name fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // Create updated Employee object
            Employee updatedEmp = new Employee(
                idField.getText(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                birthdayField.getText().trim(),
                addressField.getText().trim(),
                phoneField.getText().trim(),
                sssField.getText().trim(),
                philHealthField.getText().trim(),
                tinField.getText().trim(),
                pagIbigField.getText().trim(),
                statusField.getText().trim(),
                positionField.getText().trim(),
                supervisorField.getText().trim(),
                parseDouble(salaryField.getText()),
                parseDouble(riceField.getText()),
                parseDouble(phoneAllowField.getText()),
                parseDouble(clothingField.getText()),
                parseDouble(grossRateField.getText()),
                parseDouble(hourlyRateField.getText())
            );

            employeeService.updateEmployee(updatedEmp);

            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            if (onSuccess != null) {
                onSuccess.run();
            }
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in salary/allowance fields.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        return Double.parseDouble(text.replace(",", "").trim());
    }
}