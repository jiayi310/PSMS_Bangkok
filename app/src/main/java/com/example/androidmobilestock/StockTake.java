package com.example.androidmobilestock;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.example.androidmobilestock.databinding.ActivityStockTakeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StockTake extends AppCompatActivity {

    ActivityStockTakeBinding binding;
    public MyClickHandler StHandler;
    private AC_Class.StockTake st;
    ACDatabase db;

    String def_Location;
    String def_Agent;
    String user;

    private DatePickerDialog.OnDateSetListener mDataSetListener;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    String currentDateandTime = sdf.format(new Date());

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    String timestamp = sdf2.format(new Date());

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
        binding = DataBindingUtil.setContentView(StockTake.this, R.layout.activity_stock_take);
        db = new ACDatabase(this);

        Cursor loc = db.getReg("7");
        if(loc.moveToFirst()){
            def_Location = loc.getString(0);
        }
        Cursor cur = db.getReg("18");
        if(cur.moveToFirst()){
            def_Agent = cur.getString(0);
        }

        Cursor debtor = db.getReg("4");
        if(debtor.moveToFirst()){
            user = debtor.getString(0);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stock Count");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        st = new AC_Class.StockTake();
        binding.setSt(st);
        getCurrentDataForEdit();


        binding.stheaderDate.clearFocus();
        binding.stLocation.requestFocus();

        //db.createST();

        StHandler = new StockTake.MyClickHandler(StockTake.this);
        binding.setStHandler(StHandler);

        final String id = getIntent().getStringExtra("ID");
        st.setDocNo(id);

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    unregisterReceiver(this);
                    finish();
                }
            }, intentFilter, RECEIVER_NOT_EXPORTED);
        }
        else {
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    unregisterReceiver(this);
                    finish();
                }
            }, intentFilter);
        }
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
                StockTake.super.onBackPressed();
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

    public void getCurrentDataForEdit() {
        Cursor data;
        ActionBar actionBar = getSupportActionBar();
        String Func = getIntent().getStringExtra("FunctionKey");

        String id = getIntent().getStringExtra("ID");

        switch (Func) {
            case "New":
                // "Add transfer" title
                actionBar.setTitle("Add Stock Count (Header)");

                st.setDocNo(db.getNextTransferNo());
                if (def_Agent.equals("None")){
                    def_Agent = null;
                }
                else{
                    st.setAgent(def_Agent);
                }
                if (def_Location.equals("None")){
                    def_Location = null;
                }
                else{
                    st.setLocation(def_Location);
                }
                st.setDocDate(currentDateandTime);
                st.setCreateduser(user);
                st.setLastModifiedUser(user);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();

                st.setCreatedTimeStamp(dateFormat.format(date));
                break;

            case "Edit":
                //"Edit transfer" title
                actionBar.setTitle("Edit Stock Count (Header)");
                st = db.getStockTake(id);
                st.setLastModifiedUser(user);
                binding.setSt(st);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    AC_Class.Location Location = data.getParcelableExtra("LocationsKey");
                    if (!Location.getLocation().equals("None")) {
                        st.setLocation(Location.getLocation());
                    }
                    else {
                        st.setLocation(null);
                    }
                } else { Log.i("custDebug", "cancelled agent"); }
                break;

            case 2:
                if (resultCode == 1) {
                    AC_Class.SalesAgent Agent = data.getParcelableExtra("AgentsKey");
                    if (!Agent.getSalesAgent().equals("None")) {
                        st.setAgent(Agent.getSalesAgent());
                    }
                    else {
                        st.setAgent(null);
                    }
                } else { Log.i("custDebug", "cancelled agent"); }
                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onLocationClicked(View view)
        {
            Intent new_intent = new Intent(StockTake.this, Location_List.class);
            startActivityForResult(new_intent, 1);
        }

        public void onAgentTxtViewClicked(View view)
        {
            Intent new_intent = new Intent(StockTake.this, Agent_List.class);
            startActivityForResult(new_intent, 2);
        }

        public void OnImageButtonClicked(View view)
        {
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
                    String times = year+"/"+ monthString + "/" + dateString;
                    st.setDocDate(date);
                    timestamp = times;
                }
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(StockTake.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }

        public void OnbtnNextClicked(View view)
        {

            if (TextUtils.isEmpty(binding.stLocation.getText().toString())) {
                binding.stLocation.requestFocus();
                binding.stLocation.setError("This field cannot be blank!", null);
                return;
            } else if (!TextUtils.isEmpty(binding.stLocation.getText().toString())) {
                binding.stLocation.setError(null);
                binding.stLocation.clearFocus();
            }
            //st.setCreatedTimeStamp(timestamp);
            String key = getIntent().getStringExtra("FunctionKey");
            st.setRemarks(binding.stheaderRemarks.getText().toString());
            Intent new_intent = new Intent(StockTake.this, StockTakeDtlList.class);
            new_intent.putExtra("STHeader", st);
            new_intent.putExtra("FunctionKey", key);
            startActivityForResult(new_intent, 1);
        }
    }

}