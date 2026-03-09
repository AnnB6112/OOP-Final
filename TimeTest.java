import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TimeTest {
    public static void main(String[] args) {
        String timeStr = "8:59";
        try {
            LocalTime time = LocalTime.parse(timeStr);
            System.out.println("Parsed: " + time);
        } catch (DateTimeParseException e) {
            System.out.println("Failed to parse: " + timeStr);
        }
    }
}