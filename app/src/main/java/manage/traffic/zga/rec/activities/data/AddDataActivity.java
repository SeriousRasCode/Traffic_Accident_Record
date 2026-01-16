package manage.traffic.zga.rec.activities.data;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;

public class AddDataActivity extends AppCompatActivity {

    private TextInputEditText dName, aType, vPlate, vModel, cityAcc, country;
    private MaterialButton button1;
    private DBHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_data);

        dName = findViewById(R.id.dName);
        aType = findViewById(R.id.aType);
        vPlate = findViewById(R.id.vPlate);
        vModel = findViewById(R.id.vModel);
        cityAcc = findViewById(R.id.cityAcc);
        country = findViewById(R.id.country);
        button1 = findViewById(R.id.button1);

        dbHelper = new DBHelper(this);

        userId = getIntent().getIntExtra("userId", -1);

        button1.setOnClickListener(v -> {
            String driverName = dName.getText().toString();
            String accidentType = aType.getText().toString();
            String vehiclePlate = vPlate.getText().toString();
            String vehicleModel = vModel.getText().toString();
            String city = cityAcc.getText().toString();
            String countryName = country.getText().toString();

            if (userId != -1) {
                if (dbHelper.insertAccident(userId, driverName, accidentType, vehiclePlate, vehicleModel, city, countryName, "")) {
                    Toast.makeText(this, "Accident recorded successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to record accident", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error: User not identified.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
