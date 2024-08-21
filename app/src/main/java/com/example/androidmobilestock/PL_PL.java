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
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.example.androidmobilestock.databinding.PlActivityPlBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PL_PL extends AppCompatActivity {

    PlActivityPlBinding binding;
    public MyClickHandler handler;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private AC_Class.DO packingList;
    BroadcastReceiver exitReceiver;
    ACDatabase db;
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
        binding = DataBindingUtil.setContentView(this, R.layout.pl_activity_pl);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        db = new ACDatabase(this);

        Cursor debtor2 = db.getReg("4");
        if(debtor2.moveToFirst()){
            user = debtor2.getString(0);
        }

        getData();


        binding.setDO(packingList);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);


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
        registerReceiver(exitReceiver, intentFilter);

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.tvDocDate.getText().toString()))
                {
                    binding.tvDocDate.setError("This field cannot be blank!", null);
                    return;
                }

                if (TextUtils.isEmpty(binding.tvDebtorCode.getText().toString()))
                {
                    binding.tvDebtorCode.requestFocus();
                    binding.tvDebtorCode.setError("This field cannot be blank!", null);
                    return;
                }else if (!TextUtils.isEmpty(binding.tvDebtorCode.getText().toString())){
                    binding.tvDebtorCode.setError(null);
                    binding.tvDebtorCode.clearFocus();
                }

                //packingList.setCreatedUser("ADMIN");
                String key = getIntent().getStringExtra("FunctionKey");
                Intent new_intent = new Intent(PL_PL.this, PL_PLDtlList.class);
                new_intent.putExtra("iPackingList", packingList);
                new_intent.putExtra("FunctionKey", key);
                startActivityForResult(new_intent, 1);
            }
        });

    }


    @Override
    public void onBackPressed() {
        //        if (CheckEmpty())
        //        {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //            builder.setIcon(android.R.drawable.ic_dialog_alert);
        //            builder.setTitle("Attention!");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PL_PL.super.onBackPressed();
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
            // Returned from add invoice details
            case 1:
                packingList = data.getParcelableExtra("iPackingList");
                binding.setDO(packingList);
                break;


            // Return from agent list
            case 3:
                if (resultCode == 1) {
                    AC_Class.SalesAgent salesAgent = data.getParcelableExtra("AgentsKey");
                    if (!salesAgent.getSalesAgent().equals("None")) {
                        packingList.setSalesAgent(salesAgent.getSalesAgent());
                    }
                    else {
                        packingList.setSalesAgent(null);
                    }

                } else { Log.i("custDebug", "cancelled agent"); }
                break;
        }
    }

    public void getData() {
        ActionBar actionBar = getSupportActionBar();

        String Func = getIntent().getStringExtra("FunctionKey");
        packingList = getIntent().getParcelableExtra("iPackingList");

        switch (Func) {
            case "New":
                // "Add invoice" title
                actionBar.setTitle("Add Packing List (Header)");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                String date = sdf.format(new Date());
                packingList.setCreatedTimeStamp(date);

                String myFromDocNo = packingList.getFromDocNo();
                Cursor data = db.getSO(myFromDocNo);

                if (data.getCount() > 0)
                {
                    data.moveToFirst();
                    packingList.setDebtorCode(data.getString(0));

                    if (!data.getString(1).equals("null"))
                    {
                        packingList.setDebtorName(data.getString(1));
                    }
                    if (!data.getString(2).equals("null"))
                    {
                        packingList.setSalesAgent(data.getString(2));
                    }
                    if (!data.getString(3).equals("null")) {
                        packingList.setPhone(data.getString(3));
                    }
                    if (!data.getString(4).equals("null")) {
                        packingList.setAttention(data.getString(4));
                    }
                    if (!data.getString(5).equals("null")) {
                        packingList.setFax(data.getString(5));
                    }
                    if(!data.getString(6).equals("null")) {
                        packingList.setRemarks(data.getString(6));
                    }
                    packingList.setCreatedUser(user);
                    packingList.setLastModifiedUser(user);
                }
                break;

            case "Edit":
                actionBar.setTitle("Edit Packing List (Header)");
                packingList = db.getPackingList(packingList.getDocNo());
                packingList.setLastModifiedUser(user);
                break;

        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onAgentTxtViewClicked(View view)
        {
            Intent new_intent = new Intent(PL_PL.this, Agent_List.class);
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
                    packingList.setDocDate(date);
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(PL_PL.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }


//        public void OnNextButtonClicked(View view)
//        {
//            if (TextUtils.isEmpty(binding.tvDocDate.getText().toString()))
//            {
//                binding.tvDocDate.setError("This field cannot be blank!", null);
//                return;
//            }
//
//            if (TextUtils.isEmpty(binding.tvDebtorCode.getText().toString()))
//            {
//                binding.tvDebtorCode.setError("This field cannot be blank!", null);
//                return;
//            }
//
//            packingList.setCreatedUser("ADMIN");
//            String key = getIntent().getStringExtra("FunctionKey");
//            Intent new_intent = new Intent(PL_PL.this, PL_PLDtlList.class);
//            new_intent.putExtra("iPackingList", packingList);
//            new_intent.putExtra("FunctionKey", key);
//            startActivityForResult(new_intent, 1);
//        }
    }
}
