package com.motorph.service;

import com.motorph.model.Employee;
import com.motorph.model.Payslip;
import com.motorph.model.TimeLog;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class PayrollService implements IPayrollService {

    public PayrollService() {
    }

    @Override
    public double calculateHoursWorked(List<TimeLog> logs, int month, int year) {
        double totalHours = 0;
        for (TimeLog log : logs) {
            if (log.getDate().getMonthValue() == month && log.getDate().getYear() == year) {
                if (log.getTimeIn() != null && log.getTimeOut() != null) {
                    long minutes = Duration.between(log.getTimeIn(), log.getTimeOut()).toMinutes();
                    totalHours += minutes / 60.0;
                }
            }
        }
        return totalHours;
    }

    @Override
    public double calculateBasicPay(double hourlyRate, double hoursWorked) {
        return hourlyRate * hoursWorked;
    }

    @Override
    public double calculateAllowances(Employee employee) {
        return employee.getRiceSubsidy() + employee.getPhoneAllowance() + employee.getClothingAllowance();
    }

    @Override
    public double calculateSSSDeduction(double monthlyBasicSalary) {
        return monthlyBasicSalary * 0.045;
    }

    @Override
    public double calculatePhilHealthDeduction(double monthlyBasicSalary) {
        return monthlyBasicSalary * 0.04; 
    }

    @Override
    public double calculatePagIbigDeduction(double monthlyBasicSalary) {
        return 100.0;
    }

    @Override
    public double calculateTax(double taxableIncome) {
        if (taxableIncome <= 20833) {
            return 0;
        } else if (taxableIncome <= 33333) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }

    @Override
    public Payslip generatePayslip(Employee employee, int month, int year, List<TimeLog> allLogs) {
        // Filter logs for this specific employee
        java.util.List<TimeLog> employeeLogs = new java.util.ArrayList<>();
        for (TimeLog log : allLogs) {
            if (log.getEmployeeId().equals(employee.getEmployeeId())) {
                employeeLogs.add(log);
            }
        }

        double hoursWorked = calculateHoursWorked(employeeLogs, month, year);
        
        double basicPay = calculateBasicPay(employee.getHourlyRate(), hoursWorked);
        double allowances = calculateAllowances(employee);
        double grossSalary = basicPay + allowances;

        double calculationBase = employee.getBasicSalary(); 
        
        double sss = calculateSSSDeduction(calculationBase);
        double philHealth = calculatePhilHealthDeduction(calculationBase);
        double pagIbig = calculatePagIbigDeduction(calculationBase);
        
        double taxableIncome = grossSalary - (sss + philHealth + pagIbig);
        if (taxableIncome < 0) taxableIncome = 0;
        
        double tax = calculateTax(taxableIncome);

        double totalDeductions = sss + philHealth + pagIbig + tax;
        double netSalary = grossSalary - totalDeductions;

        Payslip payslip = new Payslip(
            employee, basicPay, allowances, grossSalary,
            sss, philHealth, pagIbig, tax,
            totalDeductions, netSalary, hoursWorked
        );
        
        LocalDate start = LocalDate.of(year, month, 1);
        payslip.setPayPeriodStart(start);
        payslip.setPayPeriodEnd(start.plusMonths(1).minusDays(1));
        
        return payslip;
    }
    
    // Legacy method for backward compatibility
    @Override
    public Payslip generatePayslip(Employee employee, double hoursWorked) {
        double basicPay = calculateBasicPay(employee.getHourlyRate(), hoursWorked);
        double allowances = calculateAllowances(employee);
        double grossSalary = basicPay + allowances;
        
        double calculationBase = employee.getBasicSalary();
        double sss = calculateSSSDeduction(calculationBase);
        double philHealth = calculatePhilHealthDeduction(calculationBase);
        double pagIbig = calculatePagIbigDeduction(calculationBase);
        
        double taxableIncome = grossSalary - (sss + philHealth + pagIbig);
        if (taxableIncome < 0) taxableIncome = 0;
        
        double tax = calculateTax(taxableIncome);
        double totalDeductions = sss + philHealth + pagIbig + tax;
        double netSalary = grossSalary - totalDeductions;

        return new Payslip(employee, basicPay, allowances, grossSalary, sss, philHealth, pagIbig, tax, totalDeductions, netSalary, hoursWorked);
    }
}
