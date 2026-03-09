package com.motorph.model;

import java.time.LocalDate;
import java.util.UUID;

public class LeaveRequest {
    private String requestId;
    private String employeeId;
    private String lastName;
    private String firstName;
    private String type; // Sick, Vacation, Emergency, etc.
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status; // Pending, Approved, Rejected
    private String approverId; // ID of the person who approved/rejected
    private LocalDate dateRequested;

    public LeaveRequest(String employeeId, String lastName, String firstName, String type, LocalDate startDate, LocalDate endDate, String reason) {
        this.requestId = UUID.randomUUID().toString();
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = "Pending";
        this.dateRequested = LocalDate.now();
    }

    // Constructor for loading from CSV
    public LeaveRequest(String requestId, String employeeId, String lastName, String firstName, String type, 
                        LocalDate startDate, LocalDate endDate, String reason, String status, 
                        String approverId, LocalDate dateRequested) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.approverId = approverId;
        this.dateRequested = dateRequested;
    }

    public String getRequestId() { return requestId; }
    public String getEmployeeId() { return employeeId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getType() { return type; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }
    public LocalDate getDateRequested() { return dateRequested; }
}
