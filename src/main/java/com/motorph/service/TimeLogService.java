package com.motorph.service;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;
import com.motorph.model.TimeLog;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeLogService {
    private DataStore dataStore;

    public TimeLogService() {
        this.dataStore = DataStore.getInstance();
    }

    public boolean hasEmployeeRecord(String employeeId) {
        return dataStore.findEmployeeById(employeeId).isPresent();
    }

    public List<TimeLog> getAllTimeLogs() {
        return dataStore.getTimeLogs();
    }

    public List<TimeLog> getTimeLogsForEmployee(String employeeId) {
        return dataStore.getTimeLogs().stream()
                .filter(log -> log.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    public Optional<TimeLog> getTodayLog(String employeeId) {
        LocalDate today = LocalDate.now();
        return dataStore.getTimeLogs().stream()
                .filter(log -> log.getEmployeeId().equals(employeeId) && log.getDate().equals(today))
                .findFirst();
    }

    public void timeIn(String employeeId) throws Exception {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (getTodayLog(employeeId).isPresent()) {
            throw new Exception("You have already timed in for today.");
        }

        Employee emp = dataStore.findEmployeeById(employeeId)
                .orElseThrow(() -> new Exception("Employee record not found."));

        TimeLog newLog = new TimeLog(
                emp.getEmployeeId(),
                emp.getLastName(),
                emp.getFirstName(),
                today,
                now,
                null
        );

        dataStore.addTimeLog(newLog);
    }

    public void timeOut(String employeeId) throws Exception {
        LocalTime now = LocalTime.now();

        TimeLog log = getTodayLog(employeeId)
                .orElseThrow(() -> new Exception("No Time In record found for today."));

        if (log.getTimeOut() != null) {
            throw new Exception("You have already timed out for today.");
        }

        log.setTimeOut(now);
        dataStore.updateTimeLog(log);
    }
    
    public double calculateHoursWorked(TimeLog log) {
        if (log.getTimeIn() != null && log.getTimeOut() != null) {
            long minutes = Duration.between(log.getTimeIn(), log.getTimeOut()).toMinutes();
            return minutes / 60.0;
        }
        return 0.0;
    }
    
    public boolean isLate(TimeLog log) {
        // Business Rule: Late if Time In > 8:10 AM
        return log.getTimeIn() != null && log.getTimeIn().isAfter(LocalTime.of(8, 10));
    }
}
