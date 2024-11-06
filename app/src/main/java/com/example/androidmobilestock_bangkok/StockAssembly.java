package com.example.androidmobilestock_bangkok;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.ActivityStockAssemblyBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StockAssembly extends AppCompatActivity {

    ActivityStockAssemblyBinding binding;
    public MyClickHandler StHandler;
    private AC_Class.StockAssembly st;
    ACDatabase db;
    String defaultBatchNo = "";
    Boolean isBatchNoEnabled = true;
    Boolean isPurBatchEnabled = true;

    String def_Location;
    String def_Agent;
    EditText edtSTQty;
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
        binding = DataBindingUtil.setContentView(StockAssembly.this, R.layout.activity_stock_assembly);
        db = new ACDatabase(this);

        Cursor loc = db.getReg("7");
        if(loc.moveToFirst()){
            def_Location = loc.getString(0);
        }
        Cursor cur = db.getReg("18");
        if(cur.moveToFirst()){
            def_Agent = cur.getString(0);
        }

        Cursor cursor5 = db.getReg("38");
        if(cursor5.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor6 = db.getReg("37");
        if(cursor6.moveToFirst()){
            defaultBatchNo = cursor6.getString(0);
        }

        Cursor cursor7 = db.getReg("40");
        if(cursor7.moveToFirst()){
            isPurBatchEnabled= Boolean.valueOf(cursor7.getString(0));
        }

        Cursor debtor = db.getReg("4");
        if(debtor.moveToFirst()){
            user = debtor.getString(0);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stock Assembly");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        st = new AC_Class.StockAssembly();
        binding.setSt(st);
        getCurrentDataForEdit();

        binding.stheaderDate.clearFocus();
        binding.stFG.requestFocus();
        binding.stLocation.clearFocus();

        //db.createST();

        StHandler = new MyClickHandler(StockAssembly.this);
        binding.setStHandler(StHandler);
        edtSTQty = (EditText) findViewById(R.id.edtTransferQty);

        final String id = getIntent().getStringExtra("ID");
        if(id!=null) {
            st.setDocNo(id);
        }

        binding.viewdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(st.getFGItemCode()!=null) {
                    Intent new_intent = new Intent(StockAssembly.this, StockAssemblyDtlList.class);
                    new_intent.putExtra("SAHeader", st);
                    startActivity(new_intent);
                }
            }
        });

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
                StockAssembly.super.onBackPressed();
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
                actionBar.setTitle("Add Stock Assembly (Header)");

                st.setDocNo(db.getNextTransferNo());
                if (def_Location.equals("None")){
                    def_Location = null;
                }
                else{
                    st.setLocation(def_Location);
                }
                st.setDocDate(currentDateandTime);
                st.setCreatedUser(user);
                st.setLastModifiedUser(user);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Date date = new Date();

                st.setCreatedTimeStamp(dateFormat.format(date));
                break;

            case "Edit":
                //"Edit transfer" title
                actionBar.setTitle("Edit Stock Assembly (Header)");
                st = db.getStockAssembly(id);
                binding.setSt(st);
                if(st.getFGBatchNo()==null || st.getFGBatchNo().equals("")){
                    binding.stFGbatchno.setVisibility(View.GONE);
                    binding.trBatchnoFromLbl3.setVisibility(View.GONE);
                }else{
                    binding.stFGbatchno.setVisibility(View.VISIBLE);
                    binding.trBatchnoFromLbl3.setVisibility(View.VISIBLE);
                }
                st.setLastModifiedUser(user);
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
                    AC_Class.ItemBOM itemBOM = data.getParcelableExtra("ItemKey");
                    if (!itemBOM.getItemCode().equals("None")) {
                        st.setFGItemCode(itemBOM.getItemCode());

                        Cursor data3 = db.getItemBC(itemBOM.getItemCode());
                        if (data3.moveToNext()) {
                            String hasBatch = data3.getString(data3.getColumnIndex("HasBatchNo"));

                            if(isBatchNoEnabled && hasBatch.equals("true")){
                                binding.stFGbatchno.setVisibility(View.VISIBLE);
                                binding.trBatchnoFromLbl3.setVisibility(View.VISIBLE);

                                if(isPurBatchEnabled) {
                                    Date d = new Date();
                                    String mm = new SimpleDateFormat("MM").format(d);
                                    String yy = new SimpleDateFormat("yy").format(d);
                                    String yyyy = new SimpleDateFormat("yyyy").format(d);

                                    if (defaultBatchNo.contains("{MM}")) {
                                        defaultBatchNo = defaultBatchNo.replace("{MM}", mm);
                                    }
                                    if (defaultBatchNo.contains("{YY}")) {
                                        defaultBatchNo = defaultBatchNo.replace("{YY}", yy);
                                    }
                                    if (defaultBatchNo.contains("{YYYY}")) {
                                        defaultBatchNo = defaultBatchNo.replace("{YYYY}", yyyy);
                                    }
                                    binding.stFGbatchno.setText(defaultBatchNo);
                                }
                                st.setFGBatchNo(binding.stFGbatchno.getText().toString());
                            }else{
                                binding.trBatchnoFromLbl3.setVisibility(View.GONE);
                                binding.stFGbatchno.setVisibility(View.GONE);
                                st.setFGBatchNo(null);
                            }
                        }

                    }
                    else {
                        st.setFGItemCode(null);
                    }
                } else { Log.i("custDebug", "cancelled FG Goods"); }
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
            Intent new_intent = new Intent(StockAssembly.this, Location_List.class);
            startActivityForResult(new_intent, 1);
        }

        public void onFGItemClicked(View view)
        {
            Intent new_intent = new Intent(StockAssembly.this, FGItem_List.class);
            startActivityForResult(new_intent, 2);
        }

        public void BtnIncClicked(View view) {
            st.setFGQty(st.getFGQty() + 1);

            edtSTQty = (EditText) findViewById(R.id.edtTransferQty);
            edtSTQty.setText(String.format("%.0f",st.getFGQty()));
        }

        public void BtnDecClicked(View view) {
            st.setFGQty(st.getFGQty() <= 1 ? 1 :st.getFGQty() - 1);

            edtSTQty = (EditText) findViewById(R.id.edtTransferQty);
            edtSTQty.setText(String.format("%.0f",st.getFGQty()));
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(StockAssembly.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }

        public void OnbtnNextClicked(View view)
        {
            if (TextUtils.isEmpty(binding.stFG.getText().toString())) {
                binding.stFG.requestFocus();
                binding.stFG.setError("This field cannot be blank!", null);
                return;
            } else if (!TextUtils.isEmpty(binding.stFG.getText().toString())) {
                binding.stFG.setError(null);
                binding.stFG.clearFocus();
            }

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
            st.setDescription(binding.stFGdesc.getText().toString());
            st.setFGQty(Double.parseDouble(binding.edtTransferQty.getText().toString()));

            if (!key.equals("Edit"))
                st.setDocNo(db.getNextNoSA());

            Cursor debtor = db.getReg("4");
            if(debtor.moveToFirst()){
                String user = debtor.getString(0);
                st.setCreatedUser(user);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());
            st.setLastModifiedDateTime(date);

            db.UpdateSADetail(st);
            db.insertStockAssembly(st);

            if (st.getDocNo().equals(db.getNextNoSA())) {
                db.IncrementAutoNumbering("SA");
            }

            // Broadcast intent to close other activities
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);


        }
    }

}