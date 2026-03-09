package com.motorph.service;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;
import com.motorph.model.LeaveRequest;
import com.motorph.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LeaveService {
    private DataStore dataStore;

    public LeaveService() {
        this.dataStore = DataStore.getInstance();
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return dataStore.getLeaveRequests();
    }

    public List<LeaveRequest> getLeaveRequestsByEmployee(String employeeId) {
        return dataStore.getLeaveRequests().stream()
                .filter(req -> req.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    public List<LeaveRequest> getPendingLeaveRequests() {
        return dataStore.getLeaveRequests().stream()
                .filter(req -> req.getStatus().equalsIgnoreCase("Pending"))
                .collect(Collectors.toList());
    }

    public void submitLeaveRequest(String employeeId, String type, LocalDate start, LocalDate end, String reason) throws Exception {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        Employee emp = dataStore.findEmployeeById(employeeId)
                .orElseThrow(() -> new Exception("Employee record not found."));

        LeaveRequest request = new LeaveRequest(
                emp.getEmployeeId(),
                emp.getLastName(),
                emp.getFirstName(),
                type,
                start,
                end,
                reason
        );

        dataStore.addLeaveRequest(request);
    }

    public void processLeaveRequest(String requestId, boolean approve, String approverId) throws Exception {
        List<LeaveRequest> requests = dataStore.getLeaveRequests();
        Optional<LeaveRequest> reqOpt = requests.stream()
                .filter(r -> r.getRequestId().equals(requestId))
                .findFirst();

        if (reqOpt.isPresent()) {
            LeaveRequest req = reqOpt.get();
            req.setStatus(approve ? "Approved" : "Rejected");
            req.setApproverId(approverId);
            dataStore.updateLeaveRequest(req);
        } else {
            throw new Exception("Leave request not found.");
        }
    }

    public boolean canManageLeaves(User user) {
        String role = user.getRole();
        if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("HR")) {
            return true;
        }

        // Check if employee is a supervisor based on position
        return dataStore.findEmployeeById(user.getUsername())
                .map(emp -> emp.getPosition().toLowerCase().contains("supervisor") ||
                        emp.getPosition().toLowerCase().contains("manager") ||
                        emp.getPosition().toLowerCase().contains("lead"))
                .orElse(false);
    }
    
    public Optional<Employee> getEmployee(String id) {
        return dataStore.findEmployeeById(id);
    }
}
