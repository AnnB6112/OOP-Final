package com.motorph.tools;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceFixer {
    public static void main(String[] args) {
        String inputFile = "attendance.csv";
        DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter outputDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
        
        // Custom parser for time to handle single digit hour
        // LocalTime.parse can handle H:mm if we use a formatter, but default is strict HH:mm
        DateTimeFormatter inputTimeFormatter = DateTimeFormatter.ofPattern("H:mm"); 
        DateTimeFormatter outputTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<String> outputLines = new ArrayList<>();
        outputLines.add("EmployeeID,LastName,FirstName,Date,TimeIn,TimeOut");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        String empId = parts[0];
                        String lastName = parts[1];
                        String firstName = parts[2];
                        String dateStr = parts[3];
                        String timeInStr = parts[4];
                        String timeOutStr = parts[5];

                        // Fix Date
                        LocalDate date = LocalDate.parse(dateStr, inputDateFormatter);
                        String fixedDate = date.format(outputDateFormatter);

                        // Fix Time In
                        LocalTime timeIn = LocalTime.parse(timeInStr, inputTimeFormatter);
                        String fixedTimeIn = timeIn.format(outputTimeFormatter);

                        // Fix Time Out
                        LocalTime timeOut = LocalTime.parse(timeOutStr, inputTimeFormatter);
                        String fixedTimeOut = timeOut.format(outputTimeFormatter);

                        outputLines.add(String.format("%s,%s,%s,%s,%s,%s", 
                            empId, lastName, firstName, fixedDate, fixedTimeIn, fixedTimeOut));

                    } catch (DateTimeParseException e) {
                        System.err.println("Skipping line due to parse error: " + line + " -> " + e.getMessage());
                    }
                }
            }
            
            // Write output
            try (PrintWriter pw = new PrintWriter(new FileWriter(inputFile))) { // Overwrite original
                for (String out : outputLines) {
                    pw.println(out);
                }
            }
            System.out.println("Attendance file fixed successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
