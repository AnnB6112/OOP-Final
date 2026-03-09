package com.motorph.service;

import com.motorph.model.Employee;
import com.motorph.model.Payslip;
import com.motorph.model.TimeLog;
import java.util.List;

public interface IPayrollService {
    double calculateHoursWorked(List<TimeLog> logs, int month, int year);
    double calculateBasicPay(double hourlyRate, double hoursWorked);
    double calculateAllowances(Employee employee);
    double calculateSSSDeduction(double monthlyBasicSalary);
    double calculatePhilHealthDeduction(double monthlyBasicSalary);
    double calculatePagIbigDeduction(double monthlyBasicSalary);
    double calculateTax(double taxableIncome);
    Payslip generatePayslip(Employee employee, int month, int year, List<TimeLog> allLogs);
    Payslip generatePayslip(Employee employee, double hoursWorked);
}
