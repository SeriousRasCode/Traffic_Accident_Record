package manage.traffic.zga.rec.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;
import manage.traffic.zga.rec.ValidationHelper;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, securityAnswerEditText, newPasswordEditText, confirmPasswordEditText;
    private TextView securityQuestionText;
    private MaterialButton verifyButton, resetPasswordButton;
    private DBHelper dbHelper;
    private String userEmail;
    private boolean isVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        dbHelper = new DBHelper(this);

        emailEditText = findViewById(R.id.email);
        securityQuestionText = findViewById(R.id.securityQuestionText);
        securityAnswerEditText = findViewById(R.id.securityAnswer);
        newPasswordEditText = findViewById(R.id.newPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        verifyButton = findViewById(R.id.verifyButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Initially hide password reset fields
        securityQuestionText.setVisibility(View.GONE);
        securityAnswerEditText.setVisibility(View.GONE);
        newPasswordEditText.setVisibility(View.GONE);
        confirmPasswordEditText.setVisibility(View.GONE);
        resetPasswordButton.setVisibility(View.GONE);

        verifyButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Validate email
            String emailError = ValidationHelper.getEmailError(email);
            if (emailError != null) {
                emailEditText.setError(emailError);
                Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email exists
            if (!dbHelper.checkEmailExists(email)) {
                emailEditText.setError("Email not found");
                Toast.makeText(this, "Email not registered", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get security question
            String question = dbHelper.getSecurityQuestion(email);
            if (question != null && !question.isEmpty()) {
                userEmail = email;
                securityQuestionText.setText(question);
                securityQuestionText.setVisibility(View.VISIBLE);
                securityAnswerEditText.setVisibility(View.VISIBLE);
                emailEditText.setEnabled(false);
                verifyButton.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Security question not found. Please contact support", Toast.LENGTH_SHORT).show();
            }
        });

        resetPasswordButton.setOnClickListener(v -> {
            String answer = securityAnswerEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate security answer
            if (answer.isEmpty()) {
                securityAnswerEditText.setError("Please enter your security answer");
                Toast.makeText(this, "Please enter your security answer", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate new password
            String passwordError = ValidationHelper.getPasswordError(newPassword);
            if (passwordError != null) {
                newPasswordEditText.setError(passwordError);
                Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate password confirmation
            String confirmPasswordError = ValidationHelper.getPasswordConfirmError(newPassword, confirmPassword);
            if (confirmPasswordError != null) {
                confirmPasswordEditText.setError(confirmPasswordError);
                Toast.makeText(this, confirmPasswordError, Toast.LENGTH_SHORT).show();
                return;
            }

            // Verify answer and reset password
            boolean success = dbHelper.verifySecurityAnswerAndResetPassword(userEmail, answer, newPassword);
            
            if (success) {
                Toast.makeText(this, "Password reset successful. Please login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Incorrect security answer. Please try again", Toast.LENGTH_SHORT).show();
            }
        });

        // Show password fields when security answer is entered
        securityAnswerEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !securityAnswerEditText.getText().toString().trim().isEmpty()) {
                newPasswordEditText.setVisibility(View.VISIBLE);
                confirmPasswordEditText.setVisibility(View.VISIBLE);
                resetPasswordButton.setVisibility(View.VISIBLE);
            }
        });

        // Login link
        TextView loginText = findViewById(R.id.loginText);
        if (loginText != null) {
            loginText.setOnClickListener(v -> {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}

