package manage.traffic.zga.rec.activities.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;
import manage.traffic.zga.rec.ValidationHelper;
import manage.traffic.zga.rec.activities.dashboard.AdminDashboardActivity;
import manage.traffic.zga.rec.activities.dashboard.ManagementActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton;
    private TextView signupText, forgotPasswordText;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

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

            int role = dbHelper.checkUserRoleByEmail(email, password);

            if (role == -1) {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                boolean isAdmin = (role == 1);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("email", email);
                editor.putBoolean("isAdmin", isAdmin);
                editor.apply();

                Intent intent = isAdmin ?
                        new Intent(this, AdminDashboardActivity.class) :
                        new Intent(this, ManagementActivity.class);

                startActivity(intent);
                finish();
            }
        });

        signupText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }
}

