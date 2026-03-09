package com.motorph.data;

import com.motorph.dao.CsvUtil;
import com.motorph.model.Employee;
import com.motorph.model.InventoryItem;
import com.motorph.model.TimeLog;
import com.motorph.model.LeaveRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataStore {
    private static DataStore instance;
    private List<Employee> employees;
    private List<InventoryItem> inventory;
    private List<TimeLog> timeLogs;
    private List<LeaveRequest> leaveRequests;

    private DataStore() {
        employees = new ArrayList<>();
        inventory = new ArrayList<>();
        timeLogs = new ArrayList<>();
        leaveRequests = new ArrayList<>();
        initializeData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void initializeData() {
        employees = CsvUtil.loadEmployees();
        timeLogs = CsvUtil.loadTimeLogs();
        leaveRequests = CsvUtil.loadLeaveRequests();
        
        // If CSV is empty, load dummy data and save it
        if (employees.isEmpty()) {
            // Add sample employees
            employees.add(new Employee("10001", "Juan", "Dela Cruz", "Manager", 50000, 300, "SSS1", "PH1", "TIN1", "PAG1"));
            employees.add(new Employee("10002", "Maria", "Santos", "HR Officer", 35000, 200, "SSS2", "PH2", "TIN2", "PAG2"));
            employees.add(new Employee("10003", "Pedro", "Reyes", "Staff", 25000, 150, "SSS3", "PH3", "TIN3", "PAG3"));
            
            // Save to CSV
            for (Employee e : employees) {
                CsvUtil.saveEmployee(e);
            }
        }

        // Add sample inventory
        inventory.add(new InventoryItem("P001", "Laptop", 10, 45000, "Electronics"));
        inventory.add(new InventoryItem("P002", "Office Chair", 20, 5000, "Furniture"));
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        CsvUtil.saveEmployee(employee);
    }

    public void updateEmployee(Employee updatedEmployee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeId().equals(updatedEmployee.getEmployeeId())) {
                employees.set(i, updatedEmployee);
                CsvUtil.saveAllEmployees(employees);
                break;
            }
        }
    }

    public void deleteEmployee(String employeeId) {
        if (employees.removeIf(e -> e.getEmployeeId().equals(employeeId))) {
            CsvUtil.saveAllEmployees(employees);
        }
    }

    public Optional<Employee> findEmployeeById(String id) {
        return employees.stream()
                .filter(e -> e.getEmployeeId().equals(id))
                .findFirst();
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    public void addInventoryItem(InventoryItem item) {
        inventory.add(item);
    }

    public List<TimeLog> getTimeLogs() {
        return timeLogs;
    }

    public void addTimeLog(TimeLog log) {
        timeLogs.add(log);
        CsvUtil.saveTimeLog(log);
    }

    public void updateTimeLog(TimeLog updatedLog) {
        for (int i = 0; i < timeLogs.size(); i++) {
            TimeLog log = timeLogs.get(i);
            // Identify unique log by EmployeeID and Date
            if (log.getEmployeeId().equals(updatedLog.getEmployeeId()) && 
                log.getDate().equals(updatedLog.getDate())) {
                timeLogs.set(i, updatedLog);
                break;
            }
        }
        CsvUtil.saveAllTimeLogs(timeLogs);
    }

    public List<LeaveRequest> getLeaveRequests() {
        return leaveRequests;
    }

    public void addLeaveRequest(LeaveRequest request) {
        leaveRequests.add(request);
        CsvUtil.saveLeaveRequest(request);
    }

    public void updateLeaveRequest(LeaveRequest updatedRequest) {
        for (int i = 0; i < leaveRequests.size(); i++) {
            if (leaveRequests.get(i).getRequestId().equals(updatedRequest.getRequestId())) {
                leaveRequests.set(i, updatedRequest);
                break;
            }
        }
        CsvUtil.saveAllLeaveRequests(leaveRequests);
    }
}
