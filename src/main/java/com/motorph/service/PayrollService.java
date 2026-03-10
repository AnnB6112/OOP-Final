package com.motorph.service;

import com.motorph.data.DataStore;
import com.motorph.model.Employee;
import com.motorph.model.Payslip;
import com.motorph.model.TimeLog;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PayrollService implements IPayrollService {
    private DataStore dataStore;

    public PayrollService() {
        this.dataStore = DataStore.getInstance();
    }

    public List<Payslip> generateMonthlyReport(int month, int year) {
        List<Employee> employees = dataStore.getEmployees();
        List<TimeLog> allLogs = dataStore.getTimeLogs();
        List<Payslip> report = new ArrayList<>();

        for (Employee emp : employees) {
            Payslip payslip = generatePayslip(emp, month, year, allLogs);
            report.add(payslip);
        }
        return report;
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
    
    // ... existing methods ...

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
        // SSS Contribution Table 2024 (Simulated Logic based on ranges)
        if (monthlyBasicSalary < 3250) return 135.00;
        else if (monthlyBasicSalary <= 3750) return 157.50;
        else if (monthlyBasicSalary <= 4250) return 180.00;
        else if (monthlyBasicSalary <= 4750) return 202.50;
        else if (monthlyBasicSalary <= 5250) return 225.00;
        else if (monthlyBasicSalary <= 5750) return 247.50;
        else if (monthlyBasicSalary <= 6250) return 270.00;
        else if (monthlyBasicSalary <= 6750) return 292.50;
        else if (monthlyBasicSalary <= 7250) return 315.00;
        else if (monthlyBasicSalary <= 7750) return 337.50;
        else if (monthlyBasicSalary <= 8250) return 360.00;
        else if (monthlyBasicSalary <= 8750) return 382.50;
        else if (monthlyBasicSalary <= 9250) return 405.00;
        else if (monthlyBasicSalary <= 9750) return 427.50;
        else if (monthlyBasicSalary <= 10250) return 450.00;
        else if (monthlyBasicSalary <= 10750) return 472.50;
        else if (monthlyBasicSalary <= 11250) return 495.00;
        else if (monthlyBasicSalary <= 11750) return 517.50;
        else if (monthlyBasicSalary <= 12250) return 540.00;
        else if (monthlyBasicSalary <= 12750) return 562.50;
        else if (monthlyBasicSalary <= 13250) return 585.00;
        else if (monthlyBasicSalary <= 13750) return 607.50;
        else if (monthlyBasicSalary <= 14250) return 630.00;
        else if (monthlyBasicSalary <= 14750) return 652.50;
        else if (monthlyBasicSalary <= 15250) return 675.00;
        else if (monthlyBasicSalary <= 15750) return 697.50;
        else if (monthlyBasicSalary <= 16250) return 720.00;
        else if (monthlyBasicSalary <= 16750) return 742.50;
        else if (monthlyBasicSalary <= 17250) return 765.00;
        else if (monthlyBasicSalary <= 17750) return 787.50;
        else if (monthlyBasicSalary <= 18250) return 810.00;
        else if (monthlyBasicSalary <= 18750) return 832.50;
        else if (monthlyBasicSalary <= 19250) return 855.00;
        else if (monthlyBasicSalary <= 19750) return 877.50;
        else if (monthlyBasicSalary <= 20250) return 900.00;
        else if (monthlyBasicSalary <= 20750) return 922.50;
        else if (monthlyBasicSalary <= 21250) return 945.00;
        else if (monthlyBasicSalary <= 21750) return 967.50;
        else if (monthlyBasicSalary <= 22250) return 990.00;
        else if (monthlyBasicSalary <= 22750) return 1012.50;
        else if (monthlyBasicSalary <= 23250) return 1035.00;
        else if (monthlyBasicSalary <= 23750) return 1057.50;
        else if (monthlyBasicSalary <= 24250) return 1080.00;
        else if (monthlyBasicSalary <= 24750) return 1102.50;
        else return 1125.00; // Max contribution
    }

    @Override
    public double calculatePhilHealthDeduction(double monthlyBasicSalary) {
        // PhilHealth 2024: 5% rate (split equally 2.5% Employee / 2.5% Employer)
        // Floor: 10,000 | Ceiling: 100,000
        double rate = 0.025; // Employee share only
        if (monthlyBasicSalary <= 10000) {
            return 10000 * rate;
        } else if (monthlyBasicSalary >= 100000) {
            return 100000 * rate;
        } else {
            return monthlyBasicSalary * rate;
        }
    }

    @Override
    public double calculatePagIbigDeduction(double monthlyBasicSalary) {
        // Pag-IBIG Fund (HDMF)
        // 1% for <= 1,500
        // 2% for > 1,500
        // Max contribution base is 5,000 (so max deduction is 100)
        double rate = (monthlyBasicSalary <= 1500) ? 0.01 : 0.02;
        double base = Math.min(monthlyBasicSalary, 5000);
        return base * rate;
    }

    @Override
    public double calculateTax(double taxableIncome) {
        // TRAIN Law Weekly Withholding Tax Table (Approximation for Semi-Monthly/Monthly)
        // Using Monthly Table logic
        if (taxableIncome <= 20833) {
            return 0;
        } else if (taxableIncome <= 33332) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66666) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166666) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666666) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }

    @Override
    public Payslip generatePayslip(Employee employee, int month, int year, List<TimeLog> allLogs) {
        // Filter logs for this specific employee
        List<TimeLog> employeeLogs = allLogs.stream()
            .filter(log -> log.getEmployeeId().equals(employee.getEmployeeId()))
            .collect(Collectors.toList());

        double hoursWorked = calculateHoursWorked(employeeLogs, month, year);
        
        // For this demo, let's assume strict hourly calculation
        // Or if they are salaried, basic pay is fixed?
        // The requirement implies functional requirements, usually basic pay is derived from hours worked for hourly employees
        // or fixed for monthly. Let's stick to the interface.
        
        double basicPay = calculateBasicPay(employee.getHourlyRate(), hoursWorked);
        // If basic pay is 0 (no hours), maybe fallback to semi-monthly rate? 
        // Let's assume hourly for now as per "Time Logs".
        
        double allowances = calculateAllowances(employee);
        double grossSalary = basicPay + allowances;

        // Deductions usually based on basic salary bracket, not hours worked
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
