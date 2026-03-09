import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateTest {
    public static void main(String[] args) {
        String dateStr = "06/03/2024";
        try {
            LocalDate date = LocalDate.parse(dateStr);
            System.out.println("Parsed: " + date);
        } catch (DateTimeParseException e) {
            System.out.println("Failed to parse: " + dateStr);
        }
    }
}