package manage.traffic.zga.rec;

import android.content.Context;
import android.content.Intent;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AccidentAdapter extends RecyclerView.Adapter<AccidentAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<HashMap<String, Object>> list;

    public AccidentAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, Object> item = list.get(position);
        holder.idd.setText(String.valueOf(position + 1));
        holder.dName.setText(String.valueOf(item.get(DBHelper.COLUMN_DRIVER)));
        holder.aType.setText(String.valueOf(item.get(DBHelper.COLUMN_TYPE)));
        holder.pNumber.setText(String.valueOf(item.get(DBHelper.COLUMN_PLATE)));
        holder.vModel.setText(String.valueOf(item.get(DBHelper.COLUMN_MODEL)));
        holder.cityAcc.setText(String.valueOf(item.get(DBHelper.COLUMN_CITY)));
        holder.country.setText(String.valueOf(item.get(DBHelper.COLUMN_COUNTRY)));
        holder.date.setText(String.valueOf(item.get(DBHelper.COLUMN_DATE)));

        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, manage.traffic.zga.rec.activities.data.EditDataActivity.class);
            intent.putExtra("id", String.valueOf(item.get(DBHelper.COLUMN_ID)));
            context.startActivity(intent);
        });

        holder.delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Record")
                    .setMessage("Are you sure you want to delete this record?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DBHelper dbHelper = new DBHelper(context);
                        dbHelper.deleteAccident((Integer) item.get(DBHelper.COLUMN_ID));
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.print.setOnClickListener(v -> printCertificate(item));
    }

    private void printCertificate(HashMap<String, Object> item) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printAdapter = new CertificatePdfDocumentAdapter(context, item);
            printManager.print("Accident_Record_Certificate", printAdapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(ArrayList<HashMap<String, Object>> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView idd, dName, aType, pNumber, vModel, cityAcc, country, date;
        public ImageView edit, delete, print;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idd = itemView.findViewById(R.id.idd);
            dName = itemView.findViewById(R.id.dName);
            aType = itemView.findViewById(R.id.aType);
            pNumber = itemView.findViewById(R.id.pNumber);
            vModel = itemView.findViewById(R.id.vModel);
            cityAcc = itemView.findViewById(R.id.cityAcc);
            country = itemView.findViewById(R.id.country);
            date = itemView.findViewById(R.id.date);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            print = itemView.findViewById(R.id.print);
        }
    }

    public static class CertificatePdfDocumentAdapter extends PrintDocumentAdapter {

        Context context;
        HashMap<String, Object> item;

        public CertificatePdfDocumentAdapter(Context context, HashMap<String, Object> item) {
            this.context = context;
            this.item = item;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("accident_certificate.pdf");
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
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
            Paint titlePaint = new Paint();

            titlePaint.setColor(Color.BLACK);
            titlePaint.setTextSize(24);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setFakeBoldText(true);

            paint.setColor(Color.BLACK);
            paint.setTextSize(14);

            int x = 50;
            int y = 100;
            int pageWidth = 595;

            // Title
            canvas.drawText("Accident Record Certificate", pageWidth / 2, y, titlePaint);
            y += 50;

            // Content
            String[] fields = {
                    "Record ID: " + item.get(DBHelper.COLUMN_ID),
                    "Driver Name: " + item.get(DBHelper.COLUMN_DRIVER),
                    "Accident Type: " + item.get(DBHelper.COLUMN_TYPE),
                    "Plate Number: " + item.get(DBHelper.COLUMN_PLATE),
                    "Vehicle Model: " + item.get(DBHelper.COLUMN_MODEL),
                    "City: " + item.get(DBHelper.COLUMN_CITY),
                    "Country: " + item.get(DBHelper.COLUMN_COUNTRY),
                    "Date: " + item.get(DBHelper.COLUMN_DATE)
            };

            for (String field : fields) {
                canvas.drawText(field, x, y, paint);
                y += 30;
            }

            // Footer
            y += 50;
            paint.setTextSize(12);
            paint.setColor(Color.GRAY);
            canvas.drawText("This document certifies the record of the above traffic accident.", x, y, paint);

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
