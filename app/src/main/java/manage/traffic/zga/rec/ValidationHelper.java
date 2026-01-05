package manage.traffic.zga.rec;

import android.text.TextUtils;

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
}