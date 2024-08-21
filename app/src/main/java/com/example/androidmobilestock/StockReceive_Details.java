package com.example.androidmobilestock;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StockReceive_Details extends AppCompatActivity {

    AC_Class.StockReceive stockReceive;
    List<AC_Class.StockReceiveDetails> itemList;
    String func;

    TextView txt_doc_no,txt_description, txt_remark, txt_item_docdate;
    RecyclerView item_recyclerView;
    ACDatabase db;
    StockReceive_DetailsItemAdapter adapter;


    //PPL_MultipleTab.java


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_receive_details);

        db = new ACDatabase(this);

        stockReceive = getIntent().getParcelableExtra("StockReceive");
        func = getIntent().getStringExtra("FunctionKey");

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stock Receive Detail");
        actionBar.setDisplayHomeAsUpEnabled(true);

        txt_doc_no = findViewById(R.id.txt_doc_no);
        txt_description = findViewById(R.id.txt_description);
        txt_remark = findViewById(R.id.txt_remark);
        txt_item_docdate = findViewById(R.id.txt_item_docdate);
        item_recyclerView = findViewById(R.id.item_recyclerView);

        txt_doc_no.setText(stockReceive.getDocNo());
        txt_description.setText(stockReceive.getDescription());
        txt_remark.setText(stockReceive.getRemarks());
        txt_item_docdate.setText(stockReceive.getDocDate());

        itemList = new ArrayList<>();

        loadData();

        item_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        item_recyclerView.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        item_recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        adapter = new StockReceive_DetailsItemAdapter(itemList, this);

    }

    private void loadData() {
        itemList.clear();
        Cursor cursor = db.getStockReceiveDetailsByDocNo(stockReceive.getDocNo());
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){

                int id = 1;
                String docNo = cursor.getString(cursor.getColumnIndex("DocNo"));
                String docDate = cursor.getString(cursor.getColumnIndex("DocDate"));
                String location = cursor.getString(cursor.getColumnIndex("Location"));
                String itemCode = cursor.getString(cursor.getColumnIndex("ItemCode"));
                String itemDescription = cursor.getString(cursor.getColumnIndex("ItemDescription"));
                String uom = cursor.getString(cursor.getColumnIndex("UOM"));
                double qty = cursor.getDouble(cursor.getColumnIndex("Qty"));
                double utdCost = cursor.getDouble(cursor.getColumnIndex("UTD_Cost"));
                double subTotal = cursor.getDouble(cursor.getColumnIndex("SubTotal"));
                String batchNo = cursor.getString(cursor.getColumnIndex("BatchNo"));
                String remarks = cursor.getString(cursor.getColumnIndex("Remarks"));
                String remarks2 = cursor.getString(cursor.getColumnIndex("Remarks2"));
                String lineNo = cursor.getString(cursor.getColumnIndex("LineNo"));

                AC_Class.StockReceiveDetails stockReceiveDetails = new AC_Class.StockReceiveDetails(
                        id, docNo, docDate, location, itemCode, itemDescription, uom, qty, utdCost, subTotal, batchNo, remarks, remarks2, lineNo);
                itemList.add(stockReceiveDetails);
            }
        }
        cursor.close();

        adapter = new StockReceive_DetailsItemAdapter(itemList, this);
        item_recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StockReceive.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities on top of the class
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}