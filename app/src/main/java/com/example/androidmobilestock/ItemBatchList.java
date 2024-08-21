package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityItemBatchListBinding;
import com.example.androidmobilestock.ui.main.ItemBatchListAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemBatchList extends AppCompatActivity {

    private RecyclerView recyclerView;
    ACDatabase db;
    String Item, Uom, Location, Key;
    List<AC_Class.ItemBatch> itemBatchlist = new ArrayList<>();
    private ItemBatchListAdapter.RecyclerViewClickListener listener;
    private IntentIntegrator qrScan;
    ActivityItemBatchListBinding binding;
    ItemBatchListAdapter adapter;
    TextView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_batch_list);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_batch_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Item Batch");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent pintent = getIntent();
        Item = pintent.getStringExtra("ItemCode");
        Uom = pintent.getStringExtra("UOM");
        Location = pintent.getStringExtra("Location");
        Key = pintent.getStringExtra("Key");

        //Avoid Null Exception
        if(Key==null)
        {
            Key = "Others";
        }

        db = new ACDatabase(this);
        recyclerView = findViewById(R.id.itembatch_rw);

        binding.itemEditText.setFocusable(true);
        binding.itemEditText.setFocusableInTouchMode(true);
        binding.itemEditText.requestFocus();
        hideSoftKeyboard(binding.getRoot());
        camera = (TextView) findViewById(R.id.si_item_camera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                Intent new_intent = new Intent(StockInquiry.this, activity_scanner.class);
//                startActivity(new_intent);
                    qrScan = new IntentIntegrator(ItemBatchList.this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(true);
                    //qrScan.setBarcodeImageEnabled(false);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);


                } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
            }
        });

        listener = new ItemBatchListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Date d = new Date();
                String tdyDate = new SimpleDateFormat("yyyy/MM/dd").format(d);
                String itembatch = ((AC_Class.ItemBatch) itemBatchlist.get(position)).getBatchNo();
                String itemexpired = ((AC_Class.ItemBatch) itemBatchlist.get(position)).getExpiryDate();
                    //Intent item_intent = new Intent();
                    Intent item_intent = new Intent();
                    item_intent.putExtra("ItemBatchNo", itembatch);
                    setResult(9, item_intent);
                    finish();

            }
        };


        if(Key.equals("FromStockTake")) {
            getData2();
        }else{
            getData();
        }
        setAdapter();

        binding.itemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(Key.equals("FromStockTake")){
                    getItemData(s.toString().trim());
                }else {
                    getItemData2(s.toString().trim());
                }
            }
        });
        binding.itemEditText.clearFocus();
        binding.itemEditText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        Intent newintent = new Intent();
        setResult(9, newintent);
        finish();
        super.onBackPressed();
    }

    private void getData() {
        Cursor data = db.getAllItemBatch(Item, Uom, Location);
        while (data.moveToNext()) {
            AC_Class.ItemBatch itemBatch = new AC_Class.ItemBatch(data.getString(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(7),data.getString(6));
            itemBatchlist.add(itemBatch);
        }
    }

    private void getData2() {
        Cursor data = db.getItemBatch(Item);
        while (data.moveToNext()) {
            AC_Class.ItemBatch itemBatch = new AC_Class.ItemBatch(data.getString(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4));
            itemBatchlist.add(itemBatch);
        }
    }

    private void setAdapter() {
        adapter = new ItemBatchListAdapter(this,  itemBatchlist, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No scanned data found.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    binding.itemEditText.setText(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    public void getItemData2(String substring) {
        Cursor data = db.getBatchLike(Item, substring,  Uom, Location, 0);
        if (data.getCount() > 0){
            itemBatchlist.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.ItemBatch item = new AC_Class.ItemBatch(data.getString(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(7),data.getString(6));

                    itemBatchlist.add(item);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void getItemData(String substring) {
        Cursor data = db.getBatchLike(Item, substring,Uom, Location, 1);
        if (data.getCount() > 0){
            itemBatchlist.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.ItemBatch item = new AC_Class.ItemBatch(data.getString(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4));

                    itemBatchlist.add(item);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}