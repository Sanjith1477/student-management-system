package utils;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return phone.trim().matches("\\d{10}");
    }

    public static boolean isNumeric(String value) {
        if (isEmpty(value)) return false;
        return value.trim().matches("\\d+");
    }

    /**
     * Password policy: at least 8 characters, and at least one uppercase
     * letter, one lowercase letter, one digit, and one special character.
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if (!Character.isWhitespace(ch)) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
