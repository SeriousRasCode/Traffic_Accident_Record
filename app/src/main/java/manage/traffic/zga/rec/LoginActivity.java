package manage.traffic.zga.rec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username, password;
    private MaterialButton loginButton;
    private TextView signupText;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // Quick validation check
            if (!ValidationHelper.isValidUsername(user)) {
                Toast.makeText(this, "Enter valid username (min 3 chars)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ValidationHelper.isValidPassword(pass)) {
                Toast.makeText(this, "Enter valid password (min 6 chars)", Toast.LENGTH_SHORT).show();
                return;
            }

            int role = dbHelper.checkUserRole(user, pass);

            if (role == -1) {
                Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show();
            } else {
                boolean isAdmin = (role == 1);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("username", user);
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
    }
}