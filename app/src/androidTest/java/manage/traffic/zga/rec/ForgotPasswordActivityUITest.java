package manage.traffic.zga.rec;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import manage.traffic.zga.rec.activities.auth.ForgotPasswordActivity;
import manage.traffic.zga.rec.activities.auth.LoginActivity;

/**
 * UI tests for ForgotPasswordActivity using Espresso
 */
@RunWith(AndroidJUnit4.class)
public class ForgotPasswordActivityUITest {

    private DBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        dbHelper = new DBHelper(context);
        
        // Create a test user
        dbHelper.insertUser(
                "Test User",
                "test@example.com",
                "password123",
                false,
                "What is your favorite food?",
                "pizza"
        );

        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testForgotPasswordActivityDisplays() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        // Check if email field is displayed
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if verify button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.verifyButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testVerifyEmailShowsSecurityQuestion() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        // Enter valid email
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("test@example.com"));

        // Close keyboard
        Espresso.closeSoftKeyboard();

        // Click verify button
        Espresso.onView(ViewMatchers.withId(R.id.verifyButton))
                .perform(ViewActions.click());

        // Security question should be visible
        Espresso.onView(ViewMatchers.withId(R.id.securityQuestionText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Security answer field should be visible
        Espresso.onView(ViewMatchers.withId(R.id.securityAnswer))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testInvalidEmailShowsError() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        // Enter invalid email
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("invalid-email"));

        // Close keyboard
        Espresso.closeSoftKeyboard();

        // Click verify button
        Espresso.onView(ViewMatchers.withId(R.id.verifyButton))
                .perform(ViewActions.click());

        // Should show error
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please enter a valid email address")));
    }

    @Test
    public void testNonExistentEmailShowsError() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        // Enter non-existent email
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("nonexistent@example.com"));

        // Close keyboard
        Espresso.closeSoftKeyboard();

        // Click verify button
        Espresso.onView(ViewMatchers.withId(R.id.verifyButton))
                .perform(ViewActions.click());

        // Should show error
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Email not found")));
    }

    @Test
    public void testPasswordResetFlow() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        // Enter email
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("test@example.com"));

        Espresso.closeSoftKeyboard();

        // Verify email
        Espresso.onView(ViewMatchers.withId(R.id.verifyButton))
                .perform(ViewActions.click());

        // Enter security answer
        Espresso.onView(ViewMatchers.withId(R.id.securityAnswer))
                .perform(ViewActions.typeText("pizza"));

        Espresso.closeSoftKeyboard();

        // Enter new password
        Espresso.onView(ViewMatchers.withId(R.id.newPassword))
                .perform(ViewActions.typeText("newpassword123"));

        // Enter confirm password
        Espresso.onView(ViewMatchers.withId(R.id.confirmPassword))
                .perform(ViewActions.typeText("newpassword123"));

        Espresso.closeSoftKeyboard();

        // Click reset button
        Espresso.onView(ViewMatchers.withId(R.id.resetPasswordButton))
                .perform(ViewActions.click());

        // Should navigate to LoginActivity
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testNavigateToLogin() {
        ActivityScenario.launch(ForgotPasswordActivity.class);

        // Click login link
        Espresso.onView(ViewMatchers.withId(R.id.loginText))
                .perform(ViewActions.click());

        // Verify intent to LoginActivity
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }
}
