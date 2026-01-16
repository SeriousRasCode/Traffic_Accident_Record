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

import manage.traffic.zga.rec.activities.auth.LoginActivity;
import manage.traffic.zga.rec.activities.auth.SignupActivity;

/**
 * UI tests for SignupActivity using Espresso.
 * This class tests the UI components and user flow of the signup screen.
 */
@RunWith(AndroidJUnit4.class)
public class SignupActivityUITest {

    private DBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        // Initialize context and DB helper
        context = ApplicationProvider.getApplicationContext();
        dbHelper = new DBHelper(context);

        // Clean up the users table before each test to ensure isolation
        dbHelper.getWritableDatabase().delete("users", null, null);

        // Initialize Espresso-Intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso-Intents and close the database
        Intents.release();
        dbHelper.close();
    }

    @Test
    public void testSignupActivity_allFieldsAreDisplayed() {
        ActivityScenario.launch(SignupActivity.class);

        // Check if all input fields and buttons are visible to the user
        Espresso.onView(ViewMatchers.withId(R.id.name))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.password))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.confirmPassword))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.securityQuestion))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.securityAnswer))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.signupButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.loginText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSignup_withValidData_shouldNavigateToLogin() {
        ActivityScenario.launch(SignupActivity.class);

        // Fill form with valid user data
        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(ViewActions.typeText("New User"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("newuser@example.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.securityAnswer)).perform(ViewActions.typeText("pizza"), ViewActions.closeSoftKeyboard());

        // Click the signup button
        Espresso.onView(ViewMatchers.withId(R.id.signupButton)).perform(ViewActions.click());

        // Verify that the app navigates to the LoginActivity upon successful registration
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testSignup_withEmptyName_shouldShowError() {
        ActivityScenario.launch(SignupActivity.class);

        // Fill form but leave name empty
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("test@example.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());

        // Click the signup button
        Espresso.onView(ViewMatchers.withId(R.id.signupButton)).perform(ViewActions.click());

        // Verify that an error message is shown for the name field
        Espresso.onView(ViewMatchers.withId(R.id.name))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("This field is required")));
    }


    @Test
    public void testSignup_withInvalidEmail_shouldShowError() {
        ActivityScenario.launch(SignupActivity.class);

        // Fill form with an invalid email address
        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(ViewActions.typeText("New User"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("invalid-email"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());

        // Click the signup button
        Espresso.onView(ViewMatchers.withId(R.id.signupButton)).perform(ViewActions.click());

        // Verify that an error message about the invalid email is shown
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please enter a valid email address")));
    }

    @Test
    public void testSignup_withPasswordMismatch_shouldShowError() {
        ActivityScenario.launch(SignupActivity.class);

        // Fill form with mismatching passwords
        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(ViewActions.typeText("New User"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("newuser@example.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("password456"), ViewActions.closeSoftKeyboard());

        // Click the signup button
        Espresso.onView(ViewMatchers.withId(R.id.signupButton)).perform(ViewActions.click());

        // Verify that an error about password mismatch is shown
        Espresso.onView(ViewMatchers.withId(R.id.confirmPassword))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Passwords do not match")));
    }

    @Test
    public void testClickLoginText_shouldNavigateToLoginActivity() {
        ActivityScenario.launch(SignupActivity.class);

        // Click the "Login" text link
        Espresso.onView(ViewMatchers.withId(R.id.loginText)).perform(ViewActions.click());

        // Verify that this action triggers an intent to launch LoginActivity
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }
}
