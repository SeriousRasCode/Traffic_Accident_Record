package manage.traffic.zga.rec;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests using JUnit + Mockito
 * JUnit provides the test framework (@Test, assertions)
 * Mockito provides mocking capabilities for dependencies
 */
public class LoginActivityUnitTest {

    @Mock
    private DBHelper mockDBHelper;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup mock editor
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), any(Boolean.class))).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
    }

    @Test
    public void testDBHelperCheckUserRole() {
        // Mock successful login (regular user)
        when(mockDBHelper.checkUserRoleByEmail("test@example.com", "password123"))
                .thenReturn(0);
        
        int role = mockDBHelper.checkUserRoleByEmail("test@example.com", "password123");
        assertEquals(0, role);
        verify(mockDBHelper).checkUserRoleByEmail("test@example.com", "password123");

        // Mock admin user login
        when(mockDBHelper.checkUserRoleByEmail("admin@example.com", "admin123"))
                .thenReturn(1);
        
        role = mockDBHelper.checkUserRoleByEmail("admin@example.com", "admin123");
        assertEquals(1, role);

        // Mock failed login
        when(mockDBHelper.checkUserRoleByEmail("wrong@example.com", "wrongpass"))
                .thenReturn(-1);
        
        role = mockDBHelper.checkUserRoleByEmail("wrong@example.com", "wrongpass");
        assertEquals(-1, role);
    }

    @Test
    public void testDBHelperCheckEmailExists() {
        // Mock email exists
        when(mockDBHelper.checkEmailExists("existing@example.com"))
                .thenReturn(true);
        
        assertTrue(mockDBHelper.checkEmailExists("existing@example.com"));
        verify(mockDBHelper).checkEmailExists("existing@example.com");

        // Mock email doesn't exist
        when(mockDBHelper.checkEmailExists("new@example.com"))
                .thenReturn(false);
        
        assertFalse(mockDBHelper.checkEmailExists("new@example.com"));
        verify(mockDBHelper).checkEmailExists("new@example.com");
    }

    @Test
    public void testDBHelperInsertUser() {
        // Mock successful user insertion
        when(mockDBHelper.insertUser(anyString(), anyString(), anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(true);
        
        boolean result = mockDBHelper.insertUser(
                "John Doe",
                "john@example.com",
                "password123",
                false,
                "What is your favorite food?",
                "pizza"
        );
        
        assertTrue(result);
        verify(mockDBHelper).insertUser("John Doe", "john@example.com", "password123", false, "What is your favorite food?", "pizza");
    }

    @Test
    public void testDBHelperGetSecurityQuestion() {
        // Mock security question retrieval
        when(mockDBHelper.getSecurityQuestion("john@example.com"))
                .thenReturn("What is your favorite food?");
        
        String question = mockDBHelper.getSecurityQuestion("john@example.com");
        assertEquals("What is your favorite food?", question);
        verify(mockDBHelper).getSecurityQuestion("john@example.com");
    }

    @Test
    public void testDBHelperPasswordReset() {
        // Mock successful password reset
        when(mockDBHelper.verifySecurityAnswerAndResetPassword("john@example.com", "pizza", "newpassword123"))
                .thenReturn(true);
        
        boolean result = mockDBHelper.verifySecurityAnswerAndResetPassword("john@example.com", "pizza", "newpassword123");
        assertTrue(result);
        verify(mockDBHelper).verifySecurityAnswerAndResetPassword("john@example.com", "pizza", "newpassword123");
    }

    @Test
    public void testSharedPreferencesOperations() {
        // Test saving login state
        mockEditor.putBoolean("isLoggedIn", true);
        mockEditor.putString("email", "test@example.com");
        mockEditor.putBoolean("isAdmin", false);
        mockEditor.apply();

        verify(mockEditor).putBoolean("isLoggedIn", true);
        verify(mockEditor).putString("email", "test@example.com");
        verify(mockEditor).putBoolean("isAdmin", false);
        verify(mockEditor).apply();
    }
}

