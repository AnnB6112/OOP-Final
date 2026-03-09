package com.motorph.model;

public class Employee extends Person {
    private String employeeId;
    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagIbigNumber;
    private String status;
    private String position;
    private String immediateSupervisor;
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    public Employee(String employeeId, String firstName, String lastName, String birthday, String address, String phoneNumber, 
                    String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber, 
                    String status, String position, String immediateSupervisor, 
                    double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance, 
                    double grossSemiMonthlyRate, double hourlyRate) {
        super(firstName, lastName, birthday, address, phoneNumber);
        setEmployeeId(employeeId);
        setSssNumber(sssNumber);
        setPhilHealthNumber(philHealthNumber);
        setTinNumber(tinNumber);
        setPagIbigNumber(pagIbigNumber);
        setStatus(status);
        setPosition(position);
        setImmediateSupervisor(immediateSupervisor);
        setBasicSalary(basicSalary);
        setRiceSubsidy(riceSubsidy);
        setPhoneAllowance(phoneAllowance);
        setClothingAllowance(clothingAllowance);
        setGrossSemiMonthlyRate(grossSemiMonthlyRate);
        setHourlyRate(hourlyRate);
    }

    // Default Constructor for backward compatibility / minimal usage
    public Employee(String employeeId, String firstName, String lastName, String position, double basicSalary, double hourlyRate, String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber) {
        this(employeeId, firstName, lastName, "N/A", "N/A", "N/A", sssNumber, philHealthNumber, tinNumber, pagIbigNumber, "Regular", position, "N/A", basicSalary, 0, 0, 0, basicSalary/2, hourlyRate);
    }

    // Getters and Setters with Validation
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty.");
        }
        this.employeeId = employeeId;
    }

    public String getSssNumber() { return sssNumber; }
    public void setSssNumber(String sssNumber) {
        if (sssNumber == null) throw new IllegalArgumentException("SSS Number cannot be null.");
        this.sssNumber = sssNumber;
    }

    public String getPhilHealthNumber() { return philHealthNumber; }
    public void setPhilHealthNumber(String philHealthNumber) {
        if (philHealthNumber == null) throw new IllegalArgumentException("PhilHealth Number cannot be null.");
        this.philHealthNumber = philHealthNumber;
    }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) {
        if (tinNumber == null) throw new IllegalArgumentException("TIN Number cannot be null.");
        this.tinNumber = tinNumber;
    }

    public String getPagIbigNumber() { return pagIbigNumber; }
    public void setPagIbigNumber(String pagIbigNumber) {
        if (pagIbigNumber == null) throw new IllegalArgumentException("Pag-IBIG Number cannot be null.");
        this.pagIbigNumber = pagIbigNumber;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPosition() { return position; }
    public void setPosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be empty.");
        }
        this.position = position;
    }

    public String getImmediateSupervisor() { return immediateSupervisor; }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor = immediateSupervisor; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) {
        if (basicSalary < 0) {
            throw new IllegalArgumentException("Basic salary cannot be negative.");
        }
        this.basicSalary = basicSalary;
    }

    public double getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(double riceSubsidy) {
        if (riceSubsidy < 0) throw new IllegalArgumentException("Rice Subsidy cannot be negative.");
        this.riceSubsidy = riceSubsidy;
    }

    public double getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(double phoneAllowance) {
        if (phoneAllowance < 0) throw new IllegalArgumentException("Phone Allowance cannot be negative.");
        this.phoneAllowance = phoneAllowance;
    }

    public double getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(double clothingAllowance) {
        if (clothingAllowance < 0) throw new IllegalArgumentException("Clothing Allowance cannot be negative.");
        this.clothingAllowance = clothingAllowance;
    }

    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) {
        if (grossSemiMonthlyRate < 0) throw new IllegalArgumentException("Gross Semi-Monthly Rate cannot be negative.");
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
    }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate < 0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative.");
        }
        this.hourlyRate = hourlyRate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + employeeId + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", position='" + position + '\'' +
                ", basicSalary=" + basicSalary +
                '}';
    }
}
