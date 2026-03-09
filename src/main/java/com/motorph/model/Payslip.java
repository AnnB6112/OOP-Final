package com.motorph.model;

import java.time.LocalDate;

public class Payslip {
    private Employee employee;
    private double basicPay;
    private double allowances; // Rice + Phone + Clothing
    private double grossSalary;
    private double sssDeduction;
    private double philHealthDeduction;
    private double pagIbigDeduction;
    private double withholdingTax;
    private double totalDeductions;
    private double netSalary;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private double hoursWorked;

    public Payslip(Employee employee, double basicPay, double allowances, double grossSalary,
                   double sssDeduction, double philHealthDeduction, double pagIbigDeduction, double withholdingTax,
                   double totalDeductions, double netSalary, double hoursWorked) {
        this.employee = employee;
        this.basicPay = basicPay;
        this.allowances = allowances;
        this.grossSalary = grossSalary;
        this.sssDeduction = sssDeduction;
        this.philHealthDeduction = philHealthDeduction;
        this.pagIbigDeduction = pagIbigDeduction;
        this.withholdingTax = withholdingTax;
        this.totalDeductions = totalDeductions;
        this.netSalary = netSalary;
        this.hoursWorked = hoursWorked;
        this.payPeriodStart = LocalDate.now();
        this.payPeriodEnd = LocalDate.now();
    }

    // Getters
    public Employee getEmployee() { return employee; }
    public double getBasicPay() { return basicPay; }
    public double getAllowances() { return allowances; }
    public double getGrossSalary() { return grossSalary; }
    public double getSssDeduction() { return sssDeduction; }
    public double getPhilHealthDeduction() { return philHealthDeduction; }
    public double getPagIbigDeduction() { return pagIbigDeduction; }
    public double getWithholdingTax() { return withholdingTax; }
    public double getTotalDeductions() { return totalDeductions; }
    public double getNetSalary() { return netSalary; }
    public double getHoursWorked() { return hoursWorked; }
    public LocalDate getPayPeriodStart() { return payPeriodStart; }
    public void setPayPeriodStart(LocalDate start) { this.payPeriodStart = start; }
    public LocalDate getPayPeriodEnd() { return payPeriodEnd; }
    public void setPayPeriodEnd(LocalDate end) { this.payPeriodEnd = end; }

    @Override
    public String toString() {
        return "Payslip for " + employee.getLastName() + ", " + employee.getFirstName() +
                "\nGross: " + String.format("%.2f", grossSalary) +
                "\nNet: " + String.format("%.2f", netSalary);
    }
}
