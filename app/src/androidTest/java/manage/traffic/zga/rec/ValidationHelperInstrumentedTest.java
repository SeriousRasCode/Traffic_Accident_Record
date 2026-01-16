package manage.traffic.zga.rec;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented tests for ValidationHelper
 * These tests require Android context
 */
@RunWith(AndroidJUnit4.class)
public class ValidationHelperInstrumentedTest {

    @Test
    public void testAppContext() {
        // Test that we have Android context
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("manage.traffic.zga.rec", appContext.getPackageName());
    }

    @Test
    public void testEmailValidation() {
        // Valid emails
        assertTrue(ValidationHelper.isValidEmail("test@example.com"));
        assertTrue(ValidationHelper.isValidEmail("user.name@example.co.uk"));
        assertTrue(ValidationHelper.isValidEmail("user123@test-domain.com"));
        
        // Invalid emails
        assertFalse(ValidationHelper.isValidEmail(""));
        assertFalse(ValidationHelper.isValidEmail("invalid-email"));
        assertFalse(ValidationHelper.isValidEmail("@example.com"));
        assertFalse(ValidationHelper.isValidEmail("test@"));
    }

    @Test
    public void testPasswordValidation() {
        // Valid passwords
        assertTrue(ValidationHelper.isValidPassword("123456"));
        assertTrue(ValidationHelper.isValidPassword("password123"));
        
        // Invalid passwords
        assertFalse(ValidationHelper.isValidPassword(""));
        assertFalse(ValidationHelper.isValidPassword("12345"));
    }

    @Test
    public void testNameValidation() {
        // Valid names
        assertTrue(ValidationHelper.isValidName("John"));
        assertTrue(ValidationHelper.isValidName("John Doe"));
        assertTrue(ValidationHelper.isValidName("Mary Jane Watson"));
        
        // Invalid names
        assertFalse(ValidationHelper.isValidName(""));
        assertFalse(ValidationHelper.isValidName("J"));
        assertFalse(ValidationHelper.isValidName("John123"));
        assertFalse(ValidationHelper.isValidName("John@Doe"));
    }

    @Test
    public void testPasswordMatch() {
        // Matching passwords
        assertTrue(ValidationHelper.doPasswordsMatch("password123", "password123"));
        
        // Non-matching passwords
        assertFalse(ValidationHelper.doPasswordsMatch("password123", "password456"));
        assertFalse(ValidationHelper.doPasswordsMatch("", "password123"));
    }

    @Test
    public void testErrorMessages() {
        // Email errors
        assertNotNull(ValidationHelper.getEmailError(""));
        assertNotNull(ValidationHelper.getEmailError("invalid"));
        assertNull(ValidationHelper.getEmailError("test@example.com"));
        
        // Password errors
        assertNotNull(ValidationHelper.getPasswordError(""));
        assertNotNull(ValidationHelper.getPasswordError("12345"));
        assertNull(ValidationHelper.getPasswordError("password123"));
        
        // Name errors
        assertNotNull(ValidationHelper.getNameError(""));
        assertNotNull(ValidationHelper.getNameError("J"));
        assertNull(ValidationHelper.getNameError("John Doe"));
        
        // Password confirm errors
        assertNotNull(ValidationHelper.getPasswordConfirmError("pass123", ""));
        assertNotNull(ValidationHelper.getPasswordConfirmError("pass123", "pass456"));
        assertNull(ValidationHelper.getPasswordConfirmError("pass123", "pass123"));
    }
}

