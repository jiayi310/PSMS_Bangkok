package com.example.androidmobilestock_bangkok;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


public class StockReceive_AddItemManual extends AppCompatActivity implements StockReceiveItemListAdapter.OnItemClickListener {

    String substring;
    AC_Class.StockReceive stockReceive;
    AC_Class.StockReceiveDetails stockReceiveDetails;
    RecyclerView itemRecyclerView;
    TextView si_item_camera;
    EditText searchField;
    ACDatabase db;
    List<AC_Class.Item> s_inquiry = new ArrayList<>();
    List<AC_Class.Item> originalList = new ArrayList<>();
    StockReceiveItemListAdapter adapter;
    String SQLINCLAUSE = "";
    String defcurr = "";
    private static final int REQUEST_CODE_ADD_ITEM_DETAIL = 2;
    int position;
    private static final int REQUEST_CODE_UPDATE_ITEM = 3;
    boolean isUpdateMode;
    IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_receive_add_item_manual);

        db = new ACDatabase(this);

        itemRecyclerView = findViewById(R.id.list_item);
        searchField = findViewById(R.id.searchField);
        si_item_camera = findViewById(R.id.si_item_camera);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("List of Items");

        stockReceive = getIntent().getParcelableExtra("stockReceive");

        //update item from the list
        substring = getIntent().getStringExtra("subString");
        stockReceiveDetails = getIntent().getParcelableExtra("nItem");
        position = getIntent().getIntExtra("nPosition", -1);
        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);


        if (stockReceiveDetails != null && position != -1 && isUpdateMode){
            Intent intent = new Intent(StockReceive_AddItemManual.this,
                    StockReceive_AddNewItemDetail.class);
            intent.putExtra("stockReceive", stockReceive);
            intent.putExtra("nItem", stockReceiveDetails);
            intent.putExtra("nPosition", position);
            intent.putExtra("isUpdateMode", isUpdateMode);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_ITEM);
        }

        Cursor data = db.getReg("6");
        if(data.moveToFirst()){
            defcurr = data.getString(0);
        }

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        originalList = new ArrayList<>(s_inquiry);
        adapter = new StockReceiveItemListAdapter(this,s_inquiry, defcurr, this);
        itemRecyclerView.setAdapter(adapter);

        loadItem(substring);

        si_item_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qrScan = new IntentIntegrator(StockReceive_AddItemManual.this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(true);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

                } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }

            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItemList(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filterItemList(String text) {
        List<AC_Class.Item> filteredList = new ArrayList<>();
        for (AC_Class.Item item : originalList) {
            if (item.getItemCode().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //From Detail Back To Manual.
            case REQUEST_CODE_ADD_ITEM_DETAIL:
                if (resultCode == RESULT_OK && data != null) {
                    AC_Class.StockReceiveDetails newItem = data.getParcelableExtra("StockReceiveDetail");
                    if (newItem != null) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("StockReceiveDetail", newItem);
                        resultIntent.putExtra("position", position);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
                break;

            //If Update item details, from detail back to manual
            case REQUEST_CODE_UPDATE_ITEM:
                if (resultCode == RESULT_OK && data != null) {
                    AC_Class.StockReceiveDetails updateItem = data.getParcelableExtra("StockReceiveDetail");
                    if (updateItem != null) {
                        Intent intent = new Intent();
                        intent.putExtra("StockReceiveDetail", updateItem);
                        intent.putExtra("position", position);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;

            //scanner
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "No result found.", Toast.LENGTH_SHORT).show();
                    } else {
                        /*
                        String scannedData = result.getContents().trim();
                        Intent intent = new Intent(this, StockReceive_AddNewItemDetail.class);
                        intent.putExtra("stockReceive", stockReceive);
                        intent.putExtra("scannedData", scannedData);
                        startActivityForResult(intent, REQUEST_CODE_ADD_ITEM_DETAIL);
                         */
                        String scannedData = result.getContents().trim();
                        searchField.setText(scannedData);
                    }
                }
                break;
        }

    }

    private void loadItem(String substring) {
        Cursor data = db.getItemLike(this.substring, 5, SQLINCLAUSE);
        int nUDF1 = data.getColumnIndex("UDF_UDF1");
        int nUDF2 = data.getColumnIndex("UDF_UDF2");
        int nUDF3 = data.getColumnIndex("UDF_UDF3");
        int nUDF4 = data.getColumnIndex("UDF_UDF4");
        int nUDF5 = data.getColumnIndex("UDF_UDF5");
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(
                            data.getString(0),
                            data.getString(1),
                            data.getString(2),
                            data.getString(3),
                            data.getString(4),
                            data.getString(5),
                            data.getString(6),
                            data.getString(7),
                            data.getString(8),
                            data.getString(9),
                            data.getFloat(10),
                            data.getFloat(11),
                            data.getFloat(12),
                            data.getFloat(13),
                            data.getFloat(14),
                            data.getFloat(15),
                            data.getString(16),
                            data.getString(17),
                            data.getFloat(18),
                            data.getFloat(19),
                            data.getFloat(20),
                            data.getString(21),
                            data.getString(22),
                            data.getString(23),
                            data.getString(24),
                            data.getString(25),
                            data.getString(26)

                    );


                    originalList.add(item);
                    s_inquiry.add(item);
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: "+e.getMessage());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed(){
        Intent newIntent = new Intent();
        setResult(10, newIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    @Override
    public void onItemClick(AC_Class.Item item) {
        if (isUpdateMode){
            Intent intent = new Intent(this, StockReceive_AddNewItemDetail.class);
            intent.putExtra("stockReceive", stockReceive);
            intent.putExtra("nPosition", position);
            intent.putExtra("isUpdateMode", isUpdateMode);
            intent.putExtra("ItemDetails", item);
            startActivityForResult(intent, REQUEST_CODE_ADD_ITEM_DETAIL);

        } else {
            Intent intent = new Intent(this, StockReceive_AddNewItemDetail.class);
            intent.putExtra("stockReceive", stockReceive);
            intent.putExtra("ItemDetails", item);
            startActivityForResult(intent, REQUEST_CODE_ADD_ITEM_DETAIL);
        }

    }
}