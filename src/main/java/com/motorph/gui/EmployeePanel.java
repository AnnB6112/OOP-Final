package com.motorph.gui;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class EmployeePanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private DataStore dataStore;

    // Detail Fields
    private JTextField idField, lastNameField, firstNameField, positionField, salaryField, rateField;
    private JTextField sssField, philHealthField, tinField, pagIbigField;
    private JButton updateButton, deleteButton;

    public EmployeePanel() {
        this.dataStore = DataStore.getInstance();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- SPLIT PANE LAYOUT ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6); // Table takes 60% height

        // 1. TOP: Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Last Name", "First Name", "Position"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelection();
            }
        });

        tablePanel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        splitPane.setTopComponent(tablePanel);

        // 2. BOTTOM: Details & Action Panel
        JPanel detailsContainer = new JPanel(new BorderLayout());
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Selected Employee Details"));
        
        idField = createField(formPanel, "ID:", false); // ID not editable
        lastNameField = createField(formPanel, "Last Name:", true);
        firstNameField = createField(formPanel, "First Name:", true);
        positionField = createField(formPanel, "Position:", true);
        salaryField = createField(formPanel, "Basic Salary:", true);
        rateField = createField(formPanel, "Hourly Rate:", true);
        sssField = createField(formPanel, "SSS:", true);
        philHealthField = createField(formPanel, "PhilHealth:", true);
        tinField = createField(formPanel, "TIN:", true);
        pagIbigField = createField(formPanel, "Pag-IBIG:", true);

        detailsContainer.add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton viewButton = new JButton("View Full Details");
        viewButton.addActionListener(e -> viewSelectedEmployee());
        buttonPanel.add(viewButton);

        JButton addButton = new JButton("New Employee");
        addButton.addActionListener(e -> openNewEmployeeFrame());
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> updateEmployee());
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.setBackground(new Color(220, 53, 69)); // Red color
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteEmployee());
        buttonPanel.add(deleteButton);

        detailsContainer.add(buttonPanel, BorderLayout.SOUTH);
        splitPane.setBottomComponent(detailsContainer);

        add(splitPane, BorderLayout.CENTER);

        loadEmployeeData();
    }

    private JTextField createField(JPanel panel, String label, boolean editable) {
        panel.add(new JLabel(label));
        JTextField field = new JTextField();
        field.setEditable(editable);
        panel.add(field);
        return field;
    }

    private void loadEmployeeData() {
        List<Employee> employees = dataStore.getEmployees();
        tableModel.setRowCount(0); 
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getPosition()
            });
        }
    }

    private void populateFieldsFromSelection() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            String empId = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Employee> empOpt = dataStore.findEmployeeById(empId);
            
            if (empOpt.isPresent()) {
                Employee emp = empOpt.get();
                idField.setText(emp.getEmployeeId());
                lastNameField.setText(emp.getLastName());
                firstNameField.setText(emp.getFirstName());
                positionField.setText(emp.getPosition());
                salaryField.setText(String.valueOf(emp.getBasicSalary()));
                rateField.setText(String.valueOf(emp.getHourlyRate()));
                sssField.setText(emp.getSssNumber());
                philHealthField.setText(emp.getPhilHealthNumber());
                tinField.setText(emp.getTinNumber());
                pagIbigField.setText(emp.getPagIbigNumber());

                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } else {
            clearFields();
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    private void clearFields() {
        idField.setText("");
        lastNameField.setText("");
        firstNameField.setText("");
        positionField.setText("");
        salaryField.setText("");
        rateField.setText("");
        sssField.setText("");
        philHealthField.setText("");
        tinField.setText("");
        pagIbigField.setText("");
    }

    private void updateEmployee() {
        try {
            String id = idField.getText();
            Optional<Employee> empOpt = dataStore.findEmployeeById(id);
            if (!empOpt.isPresent()) return;

            Employee existingEmp = empOpt.get();

            // Validate and Update fields
            // We need to preserve existing data that isn't in the simplified form
            Employee updatedEmp = new Employee(
                id,
                firstNameField.getText(),
                lastNameField.getText(),
                existingEmp.getBirthday(),
                existingEmp.getAddress(),
                existingEmp.getPhoneNumber(),
                sssField.getText(),
                philHealthField.getText(),
                tinField.getText(),
                pagIbigField.getText(),
                existingEmp.getStatus(),
                positionField.getText(),
                existingEmp.getImmediateSupervisor(),
                Double.parseDouble(salaryField.getText()),
                existingEmp.getRiceSubsidy(),
                existingEmp.getPhoneAllowance(),
                existingEmp.getClothingAllowance(),
                existingEmp.getGrossSemiMonthlyRate(),
                Double.parseDouble(rateField.getText())
            );

            dataStore.updateEmployee(updatedEmp);
            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            
            // Refresh Data
            loadEmployeeData(); 
            
            // Clear selection
            employeeTable.clearSelection();
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Salary or Rate.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        String id = idField.getText();
        if (id.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete employee ID: " + id + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dataStore.deleteEmployee(id);
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            loadEmployeeData(); // Refresh Table
            clearFields();
        }
    }

    private void viewSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) return;

        String empId = (String) tableModel.getValueAt(selectedRow, 0);
        Optional<Employee> empOpt = dataStore.findEmployeeById(empId);
        
        if (empOpt.isPresent()) {
            EmployeeDetailFrame detailFrame = new EmployeeDetailFrame(empOpt.get());
            detailFrame.setVisible(true);
        }
    }

    private void openNewEmployeeFrame() {
        NewEmployeeFrame newEmployeeFrame = new NewEmployeeFrame(() -> loadEmployeeData());
        newEmployeeFrame.setVisible(true);
    }
}
