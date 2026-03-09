# Motor PH Payroll & Inventory System

This repository contains the implementation of the Motor PH system, designed to manage payroll, employee details, and inventory.

## Features
- **Employee Management**: Manage employee records.
- **Payroll Processing**: Calculate salaries, taxes, and generate payslips.
- **Attendance Tracking**: Time-in/Time-out functionality.
- **Leave Management**: Request and approve leaves.
- **Role-Based Access**: Security for Admin, HR, Finance, and Employees.

## Requirements
- Java Development Kit (JDK) 8 or higher.
- Maven (optional, usually bundled with IDEs).

## How to Run in NetBeans (or IntelliJ/Eclipse)

This project is a **Maven Project**.

1.  **Open NetBeans**.
2.  Go to **File** -> **Open Project**.
3.  Navigate to the `OOP-Final` folder (where the `pom.xml` file is located).
4.  Select the project (NetBeans will recognize the Maven icon) and click **Open Project**.
5.  Right-click the project in the "Projects" pane and select **Run**.
    *   If asked for the Main Class, select `com.motorph.Main`.

## How to Run via Command Line

You can also compile and run using standard Java commands:

```bash
# Compile
javac -d bin -sourcepath src/main/java src/main/java/com/motorph/Main.java

# Run
java -cp bin com.motorph.Main
```

## System Architecture
The project follows a layered architecture:
- **UI Layer (`gui`)**: Swing-based forms and panels.
- **Service Layer (`service`)**: Business logic (Authentication, Payroll, etc.).
- **Data Access Layer (`dao`, `data`)**: CSV file handling.
- **Model Layer (`model`)**: OOP objects (Employee, User, etc.).
