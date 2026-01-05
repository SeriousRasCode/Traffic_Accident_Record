package manage.traffic.zga.rec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.main);

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            if (isAdmin) {
                Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), ManagementActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}