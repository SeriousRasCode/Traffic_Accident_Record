package manage.traffic.zga.rec.activities.data;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;

public class AddDataActivity extends AppCompatActivity {

    private TextInputEditText dName, aType, vPlate, vModel, cityAcc, country;
    private MaterialButton button1;
    private TextView dateText;
    private MaterialToolbar toolbar;
    private DBHelper dbHelper;
    private int userId;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_data);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add New Record");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dName = findViewById(R.id.dName);
        aType = findViewById(R.id.aType);
        vPlate = findViewById(R.id.vPlate);
        vModel = findViewById(R.id.vModel);
        cityAcc = findViewById(R.id.cityAcc);
        country = findViewById(R.id.country);
        button1 = findViewById(R.id.button1);
        dateText = findViewById(R.id.dateText);

        dbHelper = new DBHelper(this);

        userId = getIntent().getIntExtra("userId", -1);

        updateLabel();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        dateText.setOnClickListener(v -> new DatePickerDialog(AddDataActivity.this, dateSetListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        button1.setOnClickListener(v -> {
            String driverName = dName.getText().toString().trim();
            String accidentType = aType.getText().toString().trim();
            String vehiclePlate = vPlate.getText().toString().trim();
            String vehicleModel = vModel.getText().toString().trim();
            String city = cityAcc.getText().toString().trim();
            String countryName = country.getText().toString().trim();
            String date = dateText.getText().toString();

            // Validation Logic
            if (driverName.isEmpty()) {
                dName.setError("Driver name is required");
                dName.requestFocus();
                return;
            }
            if (!driverName.matches("^[a-zA-Z ]+$")) {
                dName.setError("Please enter a valid name (letters and spaces only)");
                dName.requestFocus();
                return;
            }

            if (accidentType.isEmpty()) {
                aType.setError("Accident type is required");
                aType.requestFocus();
                return;
            }
            if (vehiclePlate.isEmpty()) {
                vPlate.setError("Vehicle plate is required");
                vPlate.requestFocus();
                return;
            }
            if (vehicleModel.isEmpty()) {
                vModel.setError("Vehicle model is required");
                vModel.requestFocus();
                return;
            }

            if (city.isEmpty()) {
                cityAcc.setError("City is required");
                cityAcc.requestFocus();
                return;
            }
            if (!city.matches("^[a-zA-Z ]+$")) {
                cityAcc.setError("Please enter a valid city (letters and spaces only)");
                cityAcc.requestFocus();
                return;
            }

            if (countryName.isEmpty()) {
                country.setError("Country is required");
                country.requestFocus();
                return;
            }
            if (!countryName.matches("^[a-zA-Z ]+$")) {
                country.setError("Please enter a valid country (letters and spaces only)");
                country.requestFocus();
                return;
            }

            if (userId != -1) {
                if (dbHelper.insertAccident(userId, driverName, accidentType, vehiclePlate, vehicleModel, city, countryName, date)) {
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

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
