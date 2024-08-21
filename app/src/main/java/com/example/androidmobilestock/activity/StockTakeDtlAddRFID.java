package com.example.androidmobilestock.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.adapter.RFIDListViewAdapter;
import com.example.androidmobilestock.databinding.ActivityStockTakeDtlAddRfidBinding;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidmobilestock.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StockTakeDtlAddRFID extends AppCompatActivity {

    ACDatabase db;
    Button btnScan,btnDone, btnCancel;
    AC_Class.StockTake st;

    ArrayList<AC_Class.RFIDItemStatus> RFIDList;
    ArrayList<AC_Class.StockTakeDetails> stDtlList;

    ActivityStockTakeDtlAddRfidBinding binding;
    RFIDListViewAdapter adapter;
    public TextView tvItem,tvCount, tvDocNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(StockTakeDtlAddRFID.this, R.layout.activity_stock_take_dtl_add_rfid);

        stDtlList = new ArrayList<>();
        RFIDList = new ArrayList<>();
        adapter = new RFIDListViewAdapter(this, RFIDList);
        binding.LvTagsMain.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Stock Take Details - RFID");
        }

        db = new ACDatabase(this);

        st = new AC_Class.StockTake();
        st = getIntent().getParcelableExtra("STHeader");

        btnScan = (Button) findViewById(R.id.BtScan);
        btnDone = (Button) findViewById(R.id.BtDone);
        btnCancel = (Button) findViewById(R.id.BtCancel);

        tvItem = (TextView) findViewById(R.id.tv_TtlItem);
        tvCount = (TextView) findViewById(R.id.tv_TtlCount);
        tvDocNo = (TextView) findViewById(R.id.tv_DocNo);

        tvDocNo.setText(st.getDocNo());

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent();
                newIntent.putExtra("stDtlList", stDtlList);
                setResult(0, newIntent);
                finish();
//                Intent newIntent = new Intent();
//                newIntent.putExtra("StockDtlList", std);
//                newIntent.putExtra("STHeader",st);
//                newIntent.putExtra("isSent",isSent);
//                if(st.StockDetailListLength() > 0)
//                {
//                    mContext.setResult(0,newIntent);
//                }
//                else{
//                    mContext.setResult(99,newIntent);
//                }
//                mContext.finish();
//                mContext.mReader.free();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                Intent i = new Intent(StockTakeDtlAddRFID.this, UHFSecondActivity.class);
                i.putExtra("STHeader", st);
                startActivityForResult(i,1);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == 0) {
                ArrayList<AC_Class.RFIDItemTag> myItemTag = new ArrayList();

                myItemTag = (ArrayList<AC_Class.RFIDItemTag>) data.getSerializableExtra("myRFIDItemTag");

                for(AC_Class.RFIDItemTag myItem:myItemTag)
                {
                    int myQty = 0;
                    boolean isExists = false;

                    for (AC_Class.RFIDItemStatus myStatus: RFIDList)
                    {
                        if (myStatus.ItemCode.equals(myItem.getItemCode()))
                        {
                            isExists = true;
                        }
                    }

                    if (isExists)
                    {
                        continue;
                    }
//                    for (AC_Class.StockTakeDetails myDetail: stDtlList)
//                    {
//                        if (myDetail.ItemCode.equals(myItem.getItemCode()))
//                        {
//                            myDetail.setQuantity(myDetail.getQuantity() + myItem.getQuantity());
//                            continue;
//                        }
//                    }

                    for(AC_Class.RFIDItemTag myItem2:myItemTag)
                    {
                        if (myItem2.getItemCode().equals(myItem.getItemCode()))
                        {
                            myQty++;
                        }
                    }

                    Cursor c = db.getItemBC(myItem.getItemCode());
                    if(c.getCount() > 0)
                    {
                        c.moveToNext();
                        AC_Class.StockTakeDetails myDetails = new AC_Class.StockTakeDetails();
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        myDetails.setItemCode(myItem.getItemCode());
                        myDetails.setItemDescription(c.getString(c.getColumnIndex("Description")));
                        myDetails.setUOM(c.getString(c.getColumnIndex("UOM")));
                        myDetails.setQuantity(Double.valueOf(myQty));
                        myDetails.setStockDocNo(st.getDocNo());
                        myDetails.setCreatedTimeStamp(dateFormat.format(date));
                        stDtlList.add(myDetails);

                        RFIDList.add(new AC_Class.RFIDItemStatus(myItem.getItemCode(), myQty, "Success"));
                    }
                    else
                    {
                        RFIDList.add(new AC_Class.RFIDItemStatus(myItem.getItemCode(), myQty, "Failed"));
                    }
                }

                adapter = new RFIDListViewAdapter(this, RFIDList);
                binding.LvTagsMain.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                int itemCount = 0;
                int qtyCount = 0;

                for (AC_Class.StockTakeDetails myDetails: stDtlList)
                {
                    itemCount++;
                    qtyCount += myDetails.getQuantity();
                }

                tvItem.setText(String.valueOf(itemCount));
                tvCount.setText(String.valueOf(qtyCount));
            }
        }
    }

    private void clearData() {
        stDtlList.clear();
        RFIDList.clear();

        adapter = new RFIDListViewAdapter(this, RFIDList);
        binding.LvTagsMain.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        tvItem.setText("0");
        tvCount.setText("0");
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent();
        setResult(99,newIntent);
        finish();

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        st = getIntent().getParcelableExtra("STHeader");
    }
}