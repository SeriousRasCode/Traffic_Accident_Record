package manage.traffic.zga.rec.activities.data;

import android.app.DatePickerDialog;
import android.database.Cursor;
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

import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;

public class EditDataActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private String passedId;

    private MaterialToolbar toolbar;
    private TextInputEditText dName, aType, vPlate, vModel, cityAcc, country;
    private TextView dateText;
    private MaterialButton button1;

    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.edit_data);
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
                boolean isUpdated = dbHelper.updateAccident(
                        Integer.parseInt(passedId),
                        dName.getText().toString(),
                        aType.getText().toString(),
                        vPlate.getText().toString(),
                        vModel.getText().toString(),
                        cityAcc.getText().toString(),
                        country.getText().toString(),
                        dateText.getText().toString()
                );

                if (isUpdated) {
                    Toast.makeText(EditDataActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
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
        passedId = getIntent().getStringExtra("id");
        if (passedId != null) {
            loadData(Integer.parseInt(passedId));
        }
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

    private void loadData(int id) {
        Cursor res = dbHelper.getData(id);
        if (res != null && res.moveToFirst()) {
            dName.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_DRIVER)));
            aType.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_TYPE)));
            vPlate.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_PLATE)));
            vModel.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_MODEL)));
            cityAcc.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_CITY)));
            country.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_COUNTRY)));
            dateText.setText(res.getString(res.getColumnIndex(DBHelper.COLUMN_DATE)));

            if (!res.isClosed()) {
                res.close();
            }
        }
    }
}

