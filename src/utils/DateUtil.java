package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String today() {
        return LocalDate.now().format(FORMAT);
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, FORMAT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String format(LocalDate date) {
        return date.format(FORMAT);
    }
}
