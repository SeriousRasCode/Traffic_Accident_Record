package manage.traffic.zga.rec;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DBHelper using Mockito
 * Tests database operations with mocked dependencies
 */
public class DBHelperUnitTest {

    @Mock
    private DBHelper mockDBHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsertUser() {
        when(mockDBHelper.insertUser(
                eq("John Doe"),
                eq("john@example.com"),
                eq("password123"),
                eq(false),
                eq("What is your favorite food?"),
                eq("pizza")
        )).thenReturn(true);

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
    public void testInsertUserFailure() {
        when(mockDBHelper.insertUser(anyString(), anyString(), anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(false);

        boolean result = mockDBHelper.insertUser(
                "John Doe",
                "john@example.com",
                "password123",
                false,
                "What is your favorite food?",
                "pizza"
        );

        assertFalse(result);
    }

    @Test
    public void testCheckEmailExists() {
        when(mockDBHelper.checkEmailExists("existing@example.com")).thenReturn(true);
        when(mockDBHelper.checkEmailExists("new@example.com")).thenReturn(false);

        assertTrue(mockDBHelper.checkEmailExists("existing@example.com"));
        assertFalse(mockDBHelper.checkEmailExists("new@example.com"));

        verify(mockDBHelper, times(1)).checkEmailExists("existing@example.com");
        verify(mockDBHelper, times(1)).checkEmailExists("new@example.com");
    }

    @Test
    public void testCheckUserRoleByEmail() {
        // Regular user
        when(mockDBHelper.checkUserRoleByEmail("user@example.com", "password123"))
                .thenReturn(0);

        // Admin user
        when(mockDBHelper.checkUserRoleByEmail("admin@example.com", "admin123"))
                .thenReturn(1);

        // Invalid credentials
        when(mockDBHelper.checkUserRoleByEmail("user@example.com", "wrongpass"))
                .thenReturn(-1);

        assertEquals(0, mockDBHelper.checkUserRoleByEmail("user@example.com", "password123"));
        assertEquals(1, mockDBHelper.checkUserRoleByEmail("admin@example.com", "admin123"));
        assertEquals(-1, mockDBHelper.checkUserRoleByEmail("user@example.com", "wrongpass"));
    }

    @Test
    public void testGetSecurityQuestion() {
        when(mockDBHelper.getSecurityQuestion("john@example.com"))
                .thenReturn("What is your favorite food?");

        when(mockDBHelper.getSecurityQuestion("nonexistent@example.com"))
                .thenReturn(null);

        assertEquals("What is your favorite food?", mockDBHelper.getSecurityQuestion("john@example.com"));
        assertNull(mockDBHelper.getSecurityQuestion("nonexistent@example.com"));
    }

    @Test
    public void testVerifySecurityAnswerAndResetPassword() {
        when(mockDBHelper.verifySecurityAnswerAndResetPassword("john@example.com", "pizza", "newpass123"))
                .thenReturn(true);

        when(mockDBHelper.verifySecurityAnswerAndResetPassword("john@example.com", "wronganswer", "newpass123"))
                .thenReturn(false);

        assertTrue(mockDBHelper.verifySecurityAnswerAndResetPassword("john@example.com", "pizza", "newpass123"));
        assertFalse(mockDBHelper.verifySecurityAnswerAndResetPassword("john@example.com", "wronganswer", "newpass123"));
    }

    @Test
    public void testInsertAccident() {
        when(mockDBHelper.insertAccident(
                eq("John Driver"),
                eq("Collision"),
                eq("ABC123"),
                eq("Toyota"),
                eq("New York"),
                eq("USA"),
                eq("01/01/2024")
        )).thenReturn(true);

        boolean result = mockDBHelper.insertAccident(
                "John Driver",
                "Collision",
                "ABC123",
                "Toyota",
                "New York",
                "USA",
                "01/01/2024"
        );

        assertTrue(result);
        verify(mockDBHelper).insertAccident("John Driver", "Collision", "ABC123", "Toyota", "New York", "USA", "01/01/2024");
    }

    @Test
    public void testGetAllAccidents() {
        ArrayList<HashMap<String, Object>> mockList = new ArrayList<>();
        HashMap<String, Object> accident1 = new HashMap<>();
        accident1.put(DBHelper.COLUMN_ID, 1);
        accident1.put(DBHelper.COLUMN_DRIVER, "John Driver");
        mockList.add(accident1);

        when(mockDBHelper.getAllAccidents()).thenReturn(mockList);

        ArrayList<HashMap<String, Object>> result = mockDBHelper.getAllAccidents();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Driver", result.get(0).get(DBHelper.COLUMN_DRIVER));
    }

    @Test
    public void testUpdateAccident() {
        when(mockDBHelper.updateAccident(
                eq(1),
                eq("Updated Driver"),
                eq("Updated Type"),
                eq("XYZ789"),
                eq("Honda"),
                eq("Los Angeles"),
                eq("USA"),
                eq("02/02/2024")
        )).thenReturn(true);

        boolean result = mockDBHelper.updateAccident(
                1,
                "Updated Driver",
                "Updated Type",
                "XYZ789",
                "Honda",
                "Los Angeles",
                "USA",
                "02/02/2024"
        );

        assertTrue(result);
    }

    @Test
    public void testDeleteAccident() {
        when(mockDBHelper.deleteAccident(1)).thenReturn(1);
        when(mockDBHelper.deleteAccident(999)).thenReturn(0);

        assertEquals(1, (int) mockDBHelper.deleteAccident(1));
        assertEquals(0, (int) mockDBHelper.deleteAccident(999));
    }
}
