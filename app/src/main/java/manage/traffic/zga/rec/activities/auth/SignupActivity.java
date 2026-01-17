package manage.traffic.zga.rec.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;
import manage.traffic.zga.rec.ValidationHelper;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText, securityAnswerEditText;
    private Spinner securityQuestionSpinner;
    private MaterialButton signupButton;
    private TextView loginText;
    private DBHelper dbHelper;

    private static final String[] SECURITY_QUESTIONS = {
            "What is your favorite food?",
            "What was the name of your first pet?",
            "What city were you born in?",
            "What is your mother's maiden name?",
            "What was your childhood nickname?",
            "What is the name of your best friend?",
            "What is your favorite movie?",
            "What was the make of your first car?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        dbHelper = new DBHelper(this);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        securityQuestionSpinner = findViewById(R.id.securityQuestion);
        securityAnswerEditText = findViewById(R.id.securityAnswer);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);

        // our security question spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SECURITY_QUESTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityQuestionSpinner.setAdapter(adapter);

        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String securityQuestion = securityQuestionSpinner.getSelectedItem().toString();
            String securityAnswer = securityAnswerEditText.getText().toString().trim();

            // Validate name
            String nameError = ValidationHelper.getNameError(name);
            if (nameError != null) {
                nameEditText.setError(nameError);
                Toast.makeText(this, nameError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate email
            String emailError = ValidationHelper.getEmailError(email);
            if (emailError != null) {
                emailEditText.setError(emailError);
                Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate password
            String passwordError = ValidationHelper.getPasswordError(password);
            if (passwordError != null) {
                passwordEditText.setError(passwordError);
                Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate password confirmation
            String confirmPasswordError = ValidationHelper.getPasswordConfirmError(password, confirmPassword);
            if (confirmPasswordError != null) {
                confirmPasswordEditText.setError(confirmPasswordError);
                Toast.makeText(this, confirmPasswordError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate security answer
            if (securityAnswer.isEmpty()) {
                securityAnswerEditText.setError("Security answer cannot be empty");
                Toast.makeText(this, "Please provide a security answer", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            if (dbHelper.checkEmailExists(email)) {
                emailEditText.setError("Email already registered");
                Toast.makeText(this, "Email already exists! Please login", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert user
            boolean isInserted = dbHelper.insertUser(name, email, password, false, securityQuestion, securityAnswer.toLowerCase().trim());
            if (isInserted) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
            }
        });

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

