package manage.traffic.zga.rec;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
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

public class AddDataActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private MaterialToolbar toolbar;
    private TextInputEditText dName, aType, vPlate, vModel, cityAcc, country;
    private TextView dateText;
    private MaterialButton button1;

    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.add_data);
        initialize(_savedInstanceState);
        initializeLogic();
    }

    private void initialize(Bundle _savedInstanceState) {
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

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                String name = dName.getText().toString();
                String type = aType.getText().toString();
                String plate = vPlate.getText().toString();
                String model = vModel.getText().toString();
                String city = cityAcc.getText().toString();
                String countr = country.getText().toString();
                String date = dateText.getText().toString();

                if (name.isEmpty() || plate.isEmpty()) {
                    Toast.makeText(AddDataActivity.this, "Please enter Driver Name and Plate", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isInserted = dbHelper.insertAccident(name, type, plate, model, city, countr, date);

                    if (isInserted) {
                        Toast.makeText(AddDataActivity.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddDataActivity.this, "Error Saving Data", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                _pickDate();
            }
        });
    }

    private void initializeLogic() {
        dbHelper = new DBHelper(this);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new java.util.Date());
        dateText.setText(currentDate);
    }

    public void _pickDate() {
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateText.setText(sdf.format(cal.getTime()));
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }
}