package manage.traffic.zga.rec.activities.data;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;

public class EditDataActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private String passedId;

    private MaterialToolbar toolbar;
    private TextInputEditText dName, aType, vPlate, vModel, cityAcc, country;
    private TextView dateText;
    private MaterialButton button1;

    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.edit_data);

        dbHelper = new DBHelper(this);

        initialize();
        initializeLogic();
    }

    private void initialize() {
        toolbar = findViewById(R.id.toolbar);
        dName = findViewById(R.id.dName);
        aType = findViewById(R.id.aType);
        vPlate = findViewById(R.id.vPlate);
        vModel = findViewById(R.id.vModel);
        cityAcc = findViewById(R.id.cityAcc);
        country = findViewById(R.id.country);
        dateText = findViewById(R.id.dateText);
        button1 = findViewById(R.id.button1);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Record");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        button1.setOnClickListener(_view -> {
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

            boolean isUpdated = dbHelper.updateAccident(
                    Integer.parseInt(passedId),
                    driverName,
                    accidentType,
                    vehiclePlate,
                    vehicleModel,
                    city,
                    countryName,
                    date
            );

            if (isUpdated) {
                Toast.makeText(EditDataActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditDataActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        });

        dateText.setOnClickListener(_view -> _pickDate());
    }

    private void initializeLogic() {
        passedId = getIntent().getStringExtra("id");
        if (passedId != null) {
            loadData(Integer.parseInt(passedId));
        }
    }

    public void _pickDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        new DatePickerDialog(
                this,
                dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; // Consistent date format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateText.setText(sdf.format(myCalendar.getTime()));
    }

    private void loadData(int id) {
        Cursor res = dbHelper.getData(id);
        if (res != null && res.moveToFirst()) {
            dName.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_DRIVER)));
            aType.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_TYPE)));
            vPlate.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_PLATE)));
            vModel.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_MODEL)));
            cityAcc.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_CITY)));
            country.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_COUNTRY)));

            String dateStr = res.getString(res.getColumnIndex(DBHelper.COLUMN_DATE));
            dateText.setText(dateStr);

            // Set calendar to loaded date
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            try {
                myCalendar.setTime(sdf.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!res.isClosed()) {
                res.close();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
