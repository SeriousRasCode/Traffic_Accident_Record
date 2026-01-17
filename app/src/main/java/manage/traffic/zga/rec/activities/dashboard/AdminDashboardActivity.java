package manage.traffic.zga.rec.activities.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import manage.traffic.zga.rec.AccidentAdapter;
import manage.traffic.zga.rec.DBHelper;
import manage.traffic.zga.rec.R;
import manage.traffic.zga.rec.activities.auth.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton printButton;
    private FloatingActionButton manageUsersButton;
    private SharedPreferences sharedPreferences;
    private MaterialToolbar toolbar;
    private ArrayList<HashMap<String, Object>> accidentList;
    private DBHelper dbHelper;
    private AccidentAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        printButton = findViewById(R.id.printButton);
        manageUsersButton = findViewById(R.id.manageUsersButton);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        dbHelper = new DBHelper(this);

        loadAccidents();

        printButton.setOnClickListener(v -> printPDF());

        manageUsersButton.setOnClickListener(v -> showManageUsersDialog());
    }

    private void loadAccidents() {
        accidentList = dbHelper.getAllAccidents();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccidentAdapter(this, accidentList);
        recyclerView.setAdapter(adapter);
    }

    private void filter(String text) {
        ArrayList<HashMap<String, Object>> filteredList = new ArrayList<>();
        for (HashMap<String, Object> item : accidentList) {
            if (item.get(DBHelper.COLUMN_DRIVER).toString().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    private void showManageUsersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Users");

        ArrayList<HashMap<String, Object>> users = dbHelper.getAllUsers();
        if (users.isEmpty()) {
            builder.setMessage("No users found.");
            builder.setPositiveButton("OK", null);
            builder.show();
            return;
        }

        String[] userNames = new String[users.size()];
        boolean[] checkedItems = new boolean[users.size()];

        for (int i = 0; i < users.size(); i++) {
            Object usernameObj = users.get(i).get(DBHelper.USER_COLUMN_USERNAME);
            String username = usernameObj != null ? usernameObj.toString() : "";
            userNames[i] = username;
            Object isAdminObj = users.get(i).get(DBHelper.USER_COLUMN_IS_ADMIN);
            int isAdmin = isAdminObj != null ? ((Number) isAdminObj).intValue() : 0;
            checkedItems[i] = (isAdmin == 1);
        }

        builder.setMultiChoiceItems(userNames, checkedItems, (dialog, which, isChecked) -> {
            int userId = ((Number) users.get(which).get(DBHelper.USER_COLUMN_ID)).intValue();
            if (userNames[which].equals("admin") && !isChecked) {
                Toast.makeText(AdminDashboardActivity.this, "Cannot remove admin rights from superuser.", Toast.LENGTH_SHORT).show();
                ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                return;
            }
            dbHelper.updateUserRole(userId, isChecked);
        });

        builder.setPositiveButton("Done", null);
        builder.show();
    }

    private void printPDF() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(this, accidentList);
            printManager.print("Accident_Records_Document", printAdapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_dashboard_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint("Search by driver name...");

            EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchText.setTextColor(Color.WHITE);
            searchText.setHintTextColor(Color.LTGRAY);

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
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    public static class PdfDocumentAdapter extends PrintDocumentAdapter {

        Context context;
        ArrayList<HashMap<String, Object>> list;

        public PdfDocumentAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("accident_records.pdf");
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build();
            callback.onLayoutFinished(builder.build(), !newAttributes.equals(oldAttributes));
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(10);

            int x = 10, y = 25;

            paint.setTextSize(16);
            paint.setFakeBoldText(true);
            canvas.drawText("Traffic Accident Records", x, y, paint);
            y += 30;
            paint.setTextSize(10);
            paint.setFakeBoldText(false);

            for (HashMap<String, Object> map : list) {
                if (y > 780) {
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pdfDocument.getPages().size() + 1).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 25;
                }

                canvas.drawText("ID: " + map.get(DBHelper.COLUMN_ID), x, y, paint);
                y += 15;
                canvas.drawText("Driver: " + map.get(DBHelper.COLUMN_DRIVER), x, y, paint);
                y += 15;
                canvas.drawText("Accident Type: " + map.get(DBHelper.COLUMN_ACCIDENT_TYPE), x, y, paint);
                y += 15;
                canvas.drawText("Vehicle Plate: " + map.get(DBHelper.COLUMN_VEHICLE_PLATE), x, y, paint);
                y += 15;
                canvas.drawText("Vehicle Model: " + map.get(DBHelper.COLUMN_VEHICLE_MODEL), x, y, paint);
                y += 15;
                canvas.drawText("City: " + map.get(DBHelper.COLUMN_CITY), x, y, paint);
                y += 15;
                canvas.drawText("Country: " + map.get(DBHelper.COLUMN_COUNTRY), x, y, paint);
                y += 15;
                canvas.drawText("Date: " + map.get(DBHelper.COLUMN_DATE), x, y, paint);
                y += 25;
            }

            pdfDocument.finishPage(page);

            try {
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                pdfDocument.close();
            }
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        }
    }
}
