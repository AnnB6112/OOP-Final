package com.motorph.dao;

import com.motorph.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
    private static final String CSV_FILE = "employees.csv";
    private static final String USERS_CSV_FILE = "users.csv";
    private static final String ATTENDANCE_CSV_FILE = "attendance.csv";
    private static final String DELIMITER = ",";

    public static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();
        File file = new File(CSV_FILE);

        if (!file.exists()) {
            return employees;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }
                String[] values = line.split(DELIMITER);
                if (values.length >= 19) {
                     employees.add(new Employee(
                            values[0], 
                            values[2], 
                            values[1], 
                            values[3], 
                            values[4], 
                            values[5], 
                            values[6], 
                            values[7], 
                            values[8], 
                            values[9], 
                            values[10], 
                            values[11], 
                            values[12], 
                            Double.parseDouble(values[13].replace(",", "")), 
                            Double.parseDouble(values[14].replace(",", "")), 
                            Double.parseDouble(values[15].replace(",", "")), 
                            Double.parseDouble(values[16].replace(",", "")), 
                            Double.parseDouble(values[17].replace(",", "")), 
                            Double.parseDouble(values[18].replace(",", ""))
                    ));
                } else if (values.length >= 10) {
                     employees.add(new Employee(
                            values[0], values[1], values[2], values[3],
                            Double.parseDouble(values[4]), Double.parseDouble(values[5]),
                            values[6], values[7], values[8], values[9]
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public static void saveEmployee(Employee employee) {
        boolean fileExists = new File(CSV_FILE).exists();
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, true))) {
            if (!fileExists) {
                writer.println("Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate");
            }
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                    employee.getEmployeeId(), employee.getLastName(), employee.getFirstName(),
                    employee.getBirthday(), employee.getAddress().replace(",", " "), employee.getPhoneNumber(),
                    employee.getSssNumber(), employee.getPhilHealthNumber(), employee.getTinNumber(), employee.getPagIbigNumber(),
                    employee.getStatus(), employee.getPosition(), employee.getImmediateSupervisor(),
                    employee.getBasicSalary(), employee.getRiceSubsidy(), employee.getPhoneAllowance(), employee.getClothingAllowance(),
                    employee.getGrossSemiMonthlyRate(), employee.getHourlyRate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllEmployees(List<Employee> employees) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            writer.println("Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate");
            for (Employee employee : employees) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                    employee.getEmployeeId(), employee.getLastName(), employee.getFirstName(),
                    employee.getBirthday(), employee.getAddress().replace(",", " "), employee.getPhoneNumber(),
                    employee.getSssNumber(), employee.getPhilHealthNumber(), employee.getTinNumber(), employee.getPagIbigNumber(),
                    employee.getStatus(), employee.getPosition(), employee.getImmediateSupervisor(),
                    employee.getBasicSalary(), employee.getRiceSubsidy(), employee.getPhoneAllowance(), employee.getClothingAllowance(),
                    employee.getGrossSemiMonthlyRate(), employee.getHourlyRate());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_CSV_FILE);

        if (!file.exists()) {
            saveUser(new AdminUser("admin", "admin123"));
            users.add(new AdminUser("admin", "admin123"));
            return users;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }
                String[] values = line.split(DELIMITER);
                if (values.length >= 2) {
                    String role = values.length >= 3 ? values[2] : "Employee";
                    String username = values[0];
                    String password = values[1];
                    
                    User user;
                    switch (role) {
                        case "Admin":
                            user = new AdminUser(username, password);
                            break;
                        case "HR":
                            user = new HRUser(username, password);
                            break;
                        case "Finance":
                            user = new FinanceUser(username, password);
                            break;
                        case "Employee":
                        default:
                            user = new RegularUser(username, password);
                            break;
                    }
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void saveUser(User user) {
        boolean fileExists = new File(USERS_CSV_FILE).exists();
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_CSV_FILE, true))) {
            if (!fileExists) {
                writer.println("Username,Password,Role");
            }
            writer.printf("%s,%s,%s%n", user.getUsername(), user.getPassword(), user.getRole());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<TimeLog> loadTimeLogs() {
        List<TimeLog> logs = new ArrayList<>();
        File file = new File(ATTENDANCE_CSV_FILE);

        if (!file.exists()) {
            return logs;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }
                String[] values = line.split(DELIMITER);
                if (values.length >= 6) {
                    logs.add(new TimeLog(
                        values[0], values[1], values[2], 
                        java.time.LocalDate.parse(values[3]),
                        java.time.LocalTime.parse(values[4]),
                        java.time.LocalTime.parse(values[5])
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public static void saveTimeLog(TimeLog log) {
        boolean fileExists = new File(ATTENDANCE_CSV_FILE).exists();
        try (PrintWriter writer = new PrintWriter(new FileWriter(ATTENDANCE_CSV_FILE, true))) {
            if (!fileExists) {
                writer.println("EmployeeID,LastName,FirstName,Date,TimeIn,TimeOut");
            }
            writer.printf("%s,%s,%s,%s,%s,%s%n",
                log.getEmployeeId(), log.getLastName(), log.getFirstName(),
                log.getDate(), log.getTimeIn(), log.getTimeOut());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllTimeLogs(List<TimeLog> logs) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ATTENDANCE_CSV_FILE))) {
            writer.println("EmployeeID,LastName,FirstName,Date,TimeIn,TimeOut");
            for (TimeLog log : logs) {
                writer.printf("%s,%s,%s,%s,%s,%s%n",
                    log.getEmployeeId(), log.getLastName(), log.getFirstName(),
                    log.getDate(), log.getTimeIn(), log.getTimeOut());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String LEAVES_CSV_FILE = "leaves.csv";

    public static List<LeaveRequest> loadLeaveRequests() {
        List<LeaveRequest> requests = new ArrayList<>();
        File file = new File(LEAVES_CSV_FILE);

        if (!file.exists()) {
            return requests;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }
                String[] values = line.split(DELIMITER);
                if (values.length >= 11) {
                    requests.add(new LeaveRequest(
                        values[0], values[1], values[2], values[3], values[4],
                        java.time.LocalDate.parse(values[5]),
                        java.time.LocalDate.parse(values[6]),
                        values[7], values[8], 
                        values[9].equals("null") ? null : values[9],
                        java.time.LocalDate.parse(values[10])
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    public static void saveLeaveRequest(LeaveRequest request) {
        boolean fileExists = new File(LEAVES_CSV_FILE).exists();
        try (PrintWriter writer = new PrintWriter(new FileWriter(LEAVES_CSV_FILE, true))) {
            if (!fileExists) {
                writer.println("RequestId,EmployeeId,LastName,FirstName,Type,StartDate,EndDate,Reason,Status,ApproverId,DateRequested");
            }
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                request.getRequestId(), request.getEmployeeId(), request.getLastName(), request.getFirstName(),
                request.getType(), request.getStartDate(), request.getEndDate(), 
                request.getReason(), request.getStatus(), request.getApproverId(), request.getDateRequested());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllLeaveRequests(List<LeaveRequest> requests) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LEAVES_CSV_FILE))) {
            writer.println("RequestId,EmployeeId,LastName,FirstName,Type,StartDate,EndDate,Reason,Status,ApproverId,DateRequested");
            for (LeaveRequest request : requests) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    request.getRequestId(), request.getEmployeeId(), request.getLastName(), request.getFirstName(),
                    request.getType(), request.getStartDate(), request.getEndDate(), 
                    request.getReason(), request.getStatus(), request.getApproverId(), request.getDateRequested());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
