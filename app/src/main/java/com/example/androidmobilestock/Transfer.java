package com.example.androidmobilestock;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.example.androidmobilestock.databinding.ActivityTransferBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Transfer extends AppCompatActivity {

    ActivityTransferBinding binding;
    public MyClickHandler TrHandler;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private AC_Class.Transfer transfer;
    BroadcastReceiver exitReceiver;
    ACDatabase db;
    String docNo;
    String user;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(Transfer.this, R.layout.activity_transfer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf98b88));

        transfer = new AC_Class.Transfer();
        binding.setTr(transfer);

        db = new ACDatabase(this);
        TrHandler = new MyClickHandler(Transfer.this);
        binding.setTrHandler(TrHandler);
        Cursor debtor2 = db.getReg("4");
        if(debtor2.moveToFirst()){
            user = debtor2.getString(0);
        }

        getCurrentDataForEdit();

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        exitReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                unregisterReceiver(this);
                finish();
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            registerReceiver(exitReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
        }
        else {
            registerReceiver(exitReceiver, intentFilter);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
//        if (exitReceiver != null)
//        {
//            unregisterReceiver(exitReceiver);
//        }

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //            builder.setIcon(android.R.drawable.ic_dialog_alert);
        //            builder.setTitle("Attention!");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Transfer.super.onBackPressed();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Returned from details
            case 1:
                transfer = data.getParcelableExtra("Tr");
                if(data!=null) {
                    binding.setTr(transfer);
                }
                break;

            // Return from LocationFrom list
            case 2:
                if (data != null) {
                    AC_Class.Location locationFrom = data.getParcelableExtra("LocationsKey");
                    if (locationFrom != null) {
                        if(locationFrom.getLocation().equals("None")){
                            transfer.setLocationFrom(null);
                        }else {
                            transfer.setLocationFrom(locationFrom.getLocation());
                        }
                    }
                }
                break;

            // Return from LocationTo list
            case 3:
                if (data != null) {
                    AC_Class.Location locationTo = data.getParcelableExtra("LocationsKey");
                    if (locationTo != null) {
                        transfer.setLocationTo(locationTo.getLocation());
                    }
                }
                break;
        }
    }

    public void getCurrentDataForEdit() {
        Cursor data;
        ActionBar actionBar = getSupportActionBar();
        docNo = getIntent().getStringExtra("DocNoKey");
        String Func = getIntent().getStringExtra("FunctionKey");
        switch (Func) {
            case "New":
                // "Add transfer" title
                actionBar.setTitle("Add Transfer (Header)");

                transfer.setDocNo(db.getNextTransferNo());

                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                transfer.setDocDate(currentDateandTime);
                transfer.setCreatedUser(user);
                transfer.setLastModifiedUser(user);
                break;

            case "Edit":
                //"Edit transfer" title
                actionBar.setTitle("Edit Transfer (Header)");
                transfer = db.getTransfer(docNo);
                transfer.setLastModifiedUser(user);
                binding.setTr(transfer);
                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onLocationFromClicked(View view)
        {
            Intent new_intent = new Intent(Transfer.this, Location_List.class);

            startActivityForResult(new_intent, 2);
        }

        public void onLocationToClicked(View view)
        {
            Intent new_intent = new Intent(Transfer.this, Location_List.class);
            startActivityForResult(new_intent, 3);
        }

        public void OnImageButtonClicked(View view) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

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
                    transfer.setDocDate(date);
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(Transfer.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }

        public void OnbtnNextClicked(View view)
        {
            if (TextUtils.isEmpty(binding.trLocationFrom.getText().toString()))
            {
                binding.trLocationFrom.requestFocus();
                binding.trLocationFrom.setError("This field cannot be blank!", null);
                return;
            }else if (!TextUtils.isEmpty(binding.trLocationFrom.getText().toString())){
                binding.trLocationFrom.setError(null);
                binding.trLocationFrom.clearFocus();
            }

            if (TextUtils.isEmpty(binding.trLocationTo.getText().toString()))
            {
                binding.trLocationTo.requestFocus();
                binding.trLocationTo.setError("This field cannot be blank!", null);
                return;
            }else if (!TextUtils.isEmpty(binding.trLocationTo.getText().toString())){
                binding.trLocationTo.setError(null);
                binding.trLocationTo.clearFocus();
            }

            if (transfer.getLocationTo().equals(transfer.getLocationFrom()))
            {
                String sameLocation = "Location From and Location To cannot be the same!";
                binding.trLocationFrom.setError(sameLocation, null);
                binding.trLocationTo.setError(sameLocation, null);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String date = sdf.format(new Date());
            transfer.setCreatedUser("ADMIN");
            String key = getIntent().getStringExtra("FunctionKey");
            Intent new_intent = new Intent(Transfer.this, TransferDtlList.class);
            new_intent.putExtra("TransferHeader", transfer);
            new_intent.putExtra("FunctionKey", key);
            startActivityForResult(new_intent, 1);
        }
    }
}