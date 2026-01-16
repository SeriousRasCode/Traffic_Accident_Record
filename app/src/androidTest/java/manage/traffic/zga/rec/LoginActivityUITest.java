package manage.traffic.zga.rec;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import manage.traffic.zga.rec.activities.auth.LoginActivity;
import manage.traffic.zga.rec.activities.auth.SignupActivity;
import manage.traffic.zga.rec.activities.auth.ForgotPasswordActivity;

/**
 * UI tests for LoginActivity using Espresso
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityUITest {

    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        // Clear shared preferences
        sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("login_prefs", android.content.Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        // Initialize database and add test user
        dbHelper = new DBHelper(ApplicationProvider.getApplicationContext());
        dbHelper.insertUser(
                "Test User",
                "test@example.com",
                "password123",
                false,
                "What is your favorite food?",
                "pizza"
        );

        // Initialize Intents for intent verification
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
        if (dbHelper != null) {
            // Clean up if needed
        }
    }

    @Test
    public void testLoginActivityDisplays() {
        ActivityScenario.launch(LoginActivity.class);

        // Check if email field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if password field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.password))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if login button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if signup link is displayed
        Espresso.onView(ViewMatchers.withId(R.id.signupText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testLoginWithValidCredentials() {
        ActivityScenario.launch(LoginActivity.class);

        // Enter email
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("test@example.com"));

        // Enter password
        Espresso.onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText("password123"));

        // Close keyboard
        Espresso.closeSoftKeyboard();

        // Click login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Verify intent to ManagementActivity (regular user)
        Intents.intended(IntentMatchers.hasComponent(
                manage.traffic.zga.rec.activities.dashboard.ManagementActivity.class.getName()));
    }

    @Test
    public void testLoginWithInvalidEmail() {
        ActivityScenario.launch(LoginActivity.class);

        // Enter invalid email
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("invalid-email"));

        // Enter password
        Espresso.onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText("password123"));

        // Close keyboard
        Espresso.closeSoftKeyboard();

        // Click login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Should show error (email field should have error)
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please enter a valid email address")));
    }

    @Test
    public void testLoginWithEmptyFields() {
        ActivityScenario.launch(LoginActivity.class);

        // Click login button without entering anything
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Should show error for email
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Email cannot be empty")));
    }

    @Test
    public void testNavigateToSignup() {
        ActivityScenario.launch(LoginActivity.class);

        // Click signup link
        Espresso.onView(ViewMatchers.withId(R.id.signupText))
                .perform(ViewActions.click());

        // Verify intent to SignupActivity
        Intents.intended(IntentMatchers.hasComponent(SignupActivity.class.getName()));
    }

    @Test
    public void testNavigateToForgotPassword() {
        ActivityScenario.launch(LoginActivity.class);

        // Click forgot password link
        Espresso.onView(ViewMatchers.withId(R.id.forgotPasswordText))
                .perform(ViewActions.click());

        // Verify intent to ForgotPasswordActivity
        Intents.intended(IntentMatchers.hasComponent(ForgotPasswordActivity.class.getName()));
    }
}

