package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.PlActivitySodtllistBinding;

import java.util.ArrayList;
import java.util.List;

public class PL_SODtlList extends AppCompatActivity {
    PlActivitySodtllistBinding binding;
    ACDatabase db;

    MyClickHandler handler;
    PL_PLDtlListViewAdapter adapter;
    String docNo;
    String url,plType;
    Boolean BatchComparison = true;
    Boolean LocationComparison = true;

    List<AC_Class.DODtl> doDtlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pl_activity_sodtllist);

        db = new ACDatabase(this);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sales Order Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        docNo = getIntent().getStringExtra("mySO");

        adapter = new PL_PLDtlListViewAdapter(this, doDtlList);
        binding.lvPackingListDtl.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Cursor cursor2 = db.getReg("44");
        if (cursor2.moveToFirst()) {
            BatchComparison = Boolean.valueOf(cursor2.getString(0));
        }

        Cursor cursor3 = db.getReg("49");
        if (cursor3.moveToFirst()) {
            LocationComparison = Boolean.valueOf(cursor3.getString(0));
        }

        getData();

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                unregisterReceiver(this);
                finish();
            }
        }, intentFilter);
    }

    public void getData()
    {
        doDtlList.clear();
        Cursor data;
        if(!BatchComparison && !LocationComparison) {
            data = db.getSODtl(docNo);
        }else {
            data = db.getSODtl2(docNo);
        }

        if (data.getCount() > 0) {

            while (data.moveToNext()) {
                AC_Class.DODtl myDtl = new AC_Class.DODtl();
                myDtl.setID(data.getInt(data.getColumnIndex("ID")));
                myDtl.setItemCode(data.getString(data.getColumnIndex("ItemCode")));
                myDtl.setLocation(data.getString(data.getColumnIndex("Location")));
                if (data.getString(data.getColumnIndex("ItemDescription")) != null)
                    myDtl.setItemDescription(data.getString(data.getColumnIndex("ItemDescription")));
                if (data.getString(data.getColumnIndex("UOM")) != null)
                    myDtl.setUOM(data.getString(data.getColumnIndex("UOM")));
                myDtl.setQty(data.getDouble(data.getColumnIndex("Qty")));
                myDtl.setRemarks(data.getString(data.getColumnIndex("RemarksDtl")));
                myDtl.setBatchNo(data.getString(data.getColumnIndex("BatchNo")));
                plType = data.getString(data.getColumnIndex("DocType"));

                doDtlList.add(myDtl);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            Intent myIntent = new Intent();
            myIntent.putExtra("mySO", "");
            myIntent.putExtra("plType", plType);
            setResult(5, myIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnbtnSaveClicked(View view)
        {
            if (doDtlList.size()>0) {
                Intent item_intent = new Intent();
                item_intent.putExtra("mySO", docNo);
                item_intent.putExtra("plType", plType);
                setResult(4, item_intent);
                finish();
            } else {
                Toast.makeText(PL_SODtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}