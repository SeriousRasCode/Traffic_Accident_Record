package manage.traffic.zga.rec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class ManagementActivity extends AppCompatActivity {

    private FloatingActionButton _fab;
    private RecyclerView recyclerView;
    private AccidentAdapter adapter;
    private ArrayList<HashMap<String, Object>> accidentList;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management);
        initialize();
        initializeLogic();
    }

    private void initialize() {
        _fab = findViewById(R.id._fab);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);


        // IMPORTANT FIX #1: Force light popup menu using full package name for library resources
        toolbar.setPopupTheme(androidx.appcompat.R.style.ThemeOverlay_AppCompat_Light);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Record Management");
        }

        // Toolbar title color
        toolbar.setTitleTextColor(Color.WHITE);


        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        _fab.setOnClickListener(view -> {
            Intent intent = new Intent(ManagementActivity.this, AddDataActivity.class);
            startActivity(intent);
        });
    }

    private void initializeLogic() {
        DBHelper dbHelper = new DBHelper(this);
        accidentList = dbHelper.getAllAccidents();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccidentAdapter(this, accidentList);
        recyclerView.setAdapter(adapter);
    }

    private void filter(String text) {
        ArrayList<HashMap<String, Object>> filteredList = new ArrayList<>();
        for (HashMap<String, Object> item : accidentList) {
            if (item.get(DBHelper.COLUMN_DRIVER)
                    .toString()
                    .toLowerCase()
                    .contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.main_menu, menu);

        // FIX #2: Force menu icon color to BLACK
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() != null) {
                DrawableCompat.setTint(item.getIcon(), Color.BLACK);
            }
        }



        if (searchView != null) {
            searchView.setQueryHint("Search records...");

            // FIX #3: Force SearchView text & hint to BLACK
            try {
                EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
                if (searchText != null) {
                    searchText.setTextColor(Color.BLACK);
                    searchText.setHintTextColor(Color.GRAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Traffic Accident Record App\nVersion 1.0\nDeveloped by ZGA")
                    .setPositiveButton("OK", null)
                    .show();
            return true;

        } else if (id == R.id.action_logout) {
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeLogic();
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }
}
