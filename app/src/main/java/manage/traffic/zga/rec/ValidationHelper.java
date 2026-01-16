package manage.traffic.zga.rec;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationHelper {

    // Simple validation methods
    public static boolean isValidUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        // Username should be at least 3 characters
        return username.length() >= 3;
    }

    public static String getUsernameError(String username) {
        if (TextUtils.isEmpty(username)) {
            return "Username cannot be empty";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters";
        }
        return null; // No error
    }

    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        // Password should be at least 6 characters
        return password.length() >= 6;
    }

    public static String getPasswordError(String password) {
        if (TextUtils.isEmpty(password)) {
            return "Password cannot be empty";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null; // No error
    }

    // Name validation
    public static boolean isValidName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        // Name should be at least 2 characters and contain only letters and spaces
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.matches("^[a-zA-Z\\s]+$");
    }

    public static String getNameError(String name) {
        if (TextUtils.isEmpty(name)) {
            return "Name cannot be empty";
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2) {
            return "Name must be at least 2 characters";
        }
        if (!trimmed.matches("^[a-zA-Z\\s]+$")) {
            return "Name can only contain letters and spaces";
        }
        return null; // No error
    }

    // Email validation
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Email cannot be empty";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Please enter a valid email address";
        }
        return null; // No error
    }

    // Password confirmation validation
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }

    public static String getPasswordConfirmError(String password, String confirmPassword) {
        if (TextUtils.isEmpty(confirmPassword)) {
            return "Please confirm your password";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        return null; // No error
    }
}