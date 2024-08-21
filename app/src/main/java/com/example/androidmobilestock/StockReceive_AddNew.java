package com.example.androidmobilestock;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class StockReceive_AddNew extends AppCompatActivity {


    TextView tv_DocNo, tv_DocDate;
    EditText description_editText, remarks_editText;
    ImageButton btn_DocDate;
    Button btn_Next;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    ACDatabase db;
    AC_Class.StockReceive stockReceive;
    String Func;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_receive_add_new);


        tv_DocNo = findViewById(R.id.sr_docNo);
        tv_DocDate = findViewById(R.id.tv_DocDate);
        btn_DocDate = findViewById(R.id.btn_DocDate);
        description_editText = findViewById(R.id.description_editText);
        remarks_editText = findViewById(R.id.remarks_editText);
        btn_Next = findViewById(R.id.btn_Next);

        Func = getIntent().getStringExtra("FunctionKey");

        db = new ACDatabase(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Cursor debtor2 = db.getReg("4");
        if(debtor2.moveToFirst()){
            user = debtor2.getString(0);
        }

        getCurrentDataForEdit();

        //calender feature
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String monthString = String.valueOf(month);
                String dateString = String.valueOf(dayOfMonth);

                if (monthString.length() == 1) {
                    monthString = "0" + monthString;
                }
                if (dateString.length() == 1) {
                    dateString = "0" + dateString;
                }
                String date = dateString + "/" + monthString + "/" + year;
                tv_DocDate.setText(date);
                //stockReceive.setDocDate(date);
            }
        };

        btn_DocDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(StockReceive_AddNew.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        tv_DocDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(StockReceive_AddNew.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });



    }

    private void updateRecord() {
        String editDocNo = tv_DocNo.getText().toString();
        String editDocDate = tv_DocDate.getText().toString();
        String editDesc = description_editText.getText().toString();
        String editRemarks = remarks_editText.getText().toString();

        if (TextUtils.isEmpty(editDocNo) || TextUtils.isEmpty(editDocDate) || TextUtils.isEmpty(editDesc)) {
            Toast.makeText(this, "Please fill in a description", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentTimestamp = getCurrentTimestamp();

        stockReceive.setDocNo(editDocNo);
        stockReceive.setDocDate(editDocDate);
        stockReceive.setDocType("SR");
        stockReceive.setDescription(editDesc);
        stockReceive.setRemarks(editRemarks);
        stockReceive.setCreatedTimeStamp(stockReceive.getCreatedTimeStamp());
        stockReceive.setLastModifiedDateTime(currentTimestamp);
        stockReceive.setCreatedUser(stockReceive.getCreatedUser());
        stockReceive.setLastModifiedUser(user);

        Intent intent = new Intent(StockReceive_AddNew.this, StockReceive_AddNewItem.class);
        intent.putExtra("stockReceive", stockReceive);
        intent.putExtra("FunctionKey", "Edit");
        startActivity(intent);

    }


    private void saveRecord() {

        String newDocNo = tv_DocNo.getText().toString();
        String newDocDate = tv_DocDate.getText().toString();
        String newDesc = description_editText.getText().toString();
        String newRemarks = remarks_editText.getText().toString();

        if (TextUtils.isEmpty(newDocNo) || TextUtils.isEmpty(newDocDate) || TextUtils.isEmpty(newDesc)) {
            Toast.makeText(this, "Please fill in a description", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentTimestamp = getCurrentTimestamp();

        stockReceive = new AC_Class.StockReceive();

        stockReceive.setDocNo(newDocNo);
        stockReceive.setDocDate(newDocDate);
        stockReceive.setDocType("SR");
        stockReceive.setDescription(newDesc);
        stockReceive.setRemarks(newRemarks);
        stockReceive.setCreatedTimeStamp(currentTimestamp);
        stockReceive.setLastModifiedDateTime(currentTimestamp);
        stockReceive.setCreatedUser(user);
        stockReceive.setLastModifiedUser(user);

        Intent intent = new Intent(StockReceive_AddNew.this, StockReceive_AddNewItem.class);
        intent.putExtra("stockReceive", stockReceive);
        intent.putExtra("DocNo", newDocNo);
        intent.putExtra("DocDate", newDocDate);
        intent.putExtra("Description", newDesc);
        intent.putExtra("Remarks", newRemarks);
        intent.putExtra("FunctionKey", "New");
        startActivity(intent);

    }

    private static String getCurrentTimestamp() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(now);
    }

    public void getCurrentDataForEdit() {

        ActionBar actionBar = getSupportActionBar();

        switch (Func) {
            case "New":
                actionBar.setTitle("Add New Stock Receive");
                // Set Doc No
                String nDocNo = db.getSRNextDocNo();
                tv_DocNo.setText(nDocNo);
                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                tv_DocDate.setText(currentDateandTime);
                btn_Next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveRecord();
                    }
                });

                break;

            case "Edit":
                actionBar.setTitle("Edit Stock Receive");

                stockReceive = getIntent().getParcelableExtra("StockReceive");

                tv_DocNo.setText(stockReceive.getDocNo());
                tv_DocDate.setText(stockReceive.getDocDate());
                description_editText.setText(stockReceive.getDescription());
                remarks_editText.setText(stockReceive.getRemarks());

                btn_Next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateRecord();
                    }
                });

                break;
        }

    }

    /*
    private String getNextDocNo() {
        String nextDocNo = db.getSRNextDocNo();
        if (nextDocNo == null){
            return "MSR - 0001";
        } else {
            String prefix = "MSR - ";
            int nextNo = Integer.parseInt(nextDocNo.substring(prefix.length())) + 1;
            return String.format(Locale.getDefault(), prefix + "%04d", nextNo);
        }
    }

     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(StockReceive_AddNew.this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StockReceive_AddNew.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}