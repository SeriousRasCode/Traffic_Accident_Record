package manage.traffic.zga.rec;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MaterialButton logoutButton;
    private FloatingActionButton printButton;
    private FloatingActionButton manageUsersButton;
    private SharedPreferences sharedPreferences;
    private MaterialToolbar toolbar;
    private ArrayList<HashMap<String, Object>> accidentList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        logoutButton = findViewById(R.id.logoutButton);
        printButton = findViewById(R.id.printButton);
        manageUsersButton = findViewById(R.id.manageUsersButton);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        dbHelper = new DBHelper(this);

        loadAccidents();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPDF();
            }
        });

        manageUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showManageUsersDialog();
            }
        });
    }

    private void loadAccidents() {
        accidentList = dbHelper.getAllAccidents();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccidentAdapter adapter = new AccidentAdapter(this, accidentList);
        recyclerView.setAdapter(adapter);
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
            userNames[i] = (String) users.get(i).get(DBHelper.USER_COLUMN_USERNAME);
            int isAdmin = (int) users.get(i).get(DBHelper.USER_COLUMN_IS_ADMIN);
            checkedItems[i] = (isAdmin == 1);
        }

        builder.setMultiChoiceItems(userNames, checkedItems, (dialog, which, isChecked) -> {
             int userId = (int) users.get(which).get(DBHelper.USER_COLUMN_ID);
             // Prevent removing admin rights from the "admin" user to avoid lockout
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
            paint.setTextSize(12);

            int x = 10, y = 25;

            canvas.drawText("Traffic Accident Records", x, y, paint);
            y += 20;

            for (HashMap<String, Object> map : list) {
                String record = "ID: " + map.get("id") + ", Location: " + map.get("location") + ", Date: " + map.get("date");
                canvas.drawText(record, x, y, paint);
                y += 20;
                if (y > 800) { // Simple page break check
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pdfDocument.getPages().size() + 1).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 25;
                }
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
