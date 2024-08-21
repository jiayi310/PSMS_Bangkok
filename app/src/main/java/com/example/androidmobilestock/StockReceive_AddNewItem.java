package com.example.androidmobilestock;

import static com.example.androidmobilestock.StockInquiry.removeNonAlphanumeric;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class StockReceive_AddNewItem extends AppCompatActivity {

    ACDatabase db;
    RecyclerView itemRecyclerView;
    TextView totalQty_textView, totalItem_textView, totalAmount_textView;
    EditText invdtl_editText;
    Button btnSave, btnClear, btnMerge;
    String func;
    AC_Class.StockReceive stockReceive;
    AC_Class.StockReceiveDetails stockReceiveDetails;
    List<AC_Class.StockReceiveDetails> itemList;
    private IntentIntegrator qrScan;
    StockReceive_SelectedItemAdapter adapter;
    StockReceive_SelectedItemAdapter.RecyclerViewClickListener listener;
    String def_loc;
    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;
    int dtlRemarkStart = 0;
    int dtlRemarkLength = 0;
    int dtlRemark2Start = 0;
    int dtlRemark2Length = 0;
    String barcodeDtlRemark = "";
    String barcodeDtlRemark2 = "";
    Boolean isTaxEnabled = true;
    Cursor results;
    double myQty = 1;
    private static final int REQUEST_CODE_ADD_ITEM_MANUAL = 1;
    private static final int REQUEST_CODE_UPDATE_ITEM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_receive_add_new_item);

        itemRecyclerView = findViewById(R.id.invdtllist_itemlist2);
        totalQty_textView = findViewById(R.id.lblTotal);
        totalItem_textView = findViewById(R.id.lblTotalItem);
        totalAmount_textView = findViewById(R.id.lblTotalAmount);
        btnSave = findViewById(R.id.btnSave);
        invdtl_editText = findViewById(R.id.invdtl_editText);
        btnClear = findViewById(R.id.btnClear);
        btnMerge = findViewById(R.id.btnMerge);

        db = new ACDatabase(this);
        func = getIntent().getStringExtra("FunctionKey");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Cursor loc = db.getReg("7");
        if (loc.moveToFirst()) {
            def_loc = loc.getString(0);
        }

        Cursor data = db.getReg("25");
        if(data.moveToFirst()){
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        if (isCustomBarcodeEnabled) {

            Cursor data2 = db.getReg("26");
            if(data2.moveToFirst()){
                barcodeLength = Integer.valueOf(data2.getString(0));
            }

            Cursor data3 = db.getReg("27");
            if(data3.moveToFirst()){
                itemStart = Integer.valueOf(data3.getString(0));
            }

            Cursor data4 = db.getReg("28");
            if(data4.moveToFirst()){
                itemLength = Integer.valueOf(data4.getString(0));
            }

            Cursor data5 = db.getReg("29");
            if(data5.moveToFirst()){
                qtyStart = Integer.valueOf(data5.getString(0));
            }

            Cursor data6 = db.getReg("30");
            if(data6.moveToFirst()){
                qtyLength = Integer.valueOf(data6.getString(0));
            }

            Cursor data7 = db.getReg("31");
            if(data7.moveToFirst()){
                qtyDecimal = Integer.valueOf(data7.getString(0));
            }

            Cursor data8 = db.getReg("51");
            if (data8.moveToFirst()) {
                dtlRemarkStart = Integer.valueOf(data8.getString(0));
            }

            Cursor data9 = db.getReg("52");
            if (data9.moveToFirst()) {
                dtlRemarkLength = Integer.valueOf(data9.getString(0));
            }

            Cursor data10 = db.getReg("53");
            if (data10.moveToFirst()) {
                dtlRemark2Start = Integer.valueOf(data10.getString(0));
            }

            Cursor data11 = db.getReg("54");
            if (data11.moveToFirst()) {
                dtlRemark2Length = Integer.valueOf(data11.getString(0));
            }

        }

        itemList = new ArrayList<>();
        adapter = new StockReceive_SelectedItemAdapter(itemList, this, listener);
        adapter = new StockReceive_SelectedItemAdapter(itemList, this, new StockReceive_SelectedItemAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                showAlertDialog(position);
            }
        });

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRecyclerView.setAdapter(adapter);

        // Add space between items
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        itemRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        getCurrentDataForEdit();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        invdtl_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String remove = removeNonAlphanumeric(s.toString().trim());
                String myBarcode = remove;

                stockReceiveDetails = new AC_Class.StockReceiveDetails();

                stockReceiveDetails.setLocation(def_loc);
                stockReceiveDetails.setDocNo(stockReceive.getDocNo());
                stockReceiveDetails.setDocDate(stockReceive.getDocDate());

                if (!myBarcode.equals("") && !TextUtils.isEmpty(invdtl_editText.getText())){
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled) {
                        String normalBarcode = invdtl_editText.getText().toString();
                        results = db.getItemBC(normalBarcode);
                        if (results.getCount() == 0){
                            if (myBarcode.length() == barcodeLength) {
                                if (isNumeric(myBarcode)) {
                                    myQty = Double.valueOf(myBarcode.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                    if(dtlRemarkStart!=0 && dtlRemarkLength!=0) {
                                        barcodeDtlRemark = myBarcode.substring(dtlRemarkStart - 1, dtlRemarkStart - 1 + dtlRemarkLength);
                                    }
                                    if(dtlRemark2Start!=0 && dtlRemark2Length!=0) {
                                        barcodeDtlRemark2 = myBarcode.substring(dtlRemark2Start - 1, dtlRemark2Start - 1 + dtlRemark2Length);
                                    }
                                    if (qtyDecimal > 0) {
                                        myQty = myQty / Math.pow(10, qtyDecimal);
                                    }
                                    myBarcode = myBarcode.substring(itemStart - 1, itemStart - 1 + itemLength);
                                } else {
                                    Toast.makeText(StockReceive_AddNewItem.this, "Invalid Barcode.",
                                            Toast.LENGTH_SHORT).show();
                                    isSkip = true;
                                }
                            }
                        }
                    }

                    if (!isSkip){
                        Cursor results = db.getItemBC(myBarcode);
                        if (results.getCount() == 0){
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            AlertDialog alertDialog = new AlertDialog.Builder(StockReceive_AddNewItem.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Product not found.");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                            results.moveToNext();

                            stockReceiveDetails.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                            stockReceiveDetails.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            stockReceiveDetails.setUOM(results.getString(results.getColumnIndex("UOM")));
                            stockReceiveDetails.setQuantity(myQty);
                            stockReceiveDetails.setRemarks(barcodeDtlRemark);
                            stockReceiveDetails.setRemarks2(barcodeDtlRemark2);

                            Cursor cursor1 = db.getAllUTDCost(stockReceiveDetails.getItemCode(), stockReceiveDetails.getUOM());
                            if (cursor1.getCount() > 0){
                                while (cursor1.moveToNext()){
                                    stockReceiveDetails.setUTD_Cost(cursor1.getDouble(cursor1.getColumnIndex("UTDCost")));
                                  }
                            } else {
                                stockReceiveDetails.setUTD_Cost(00.00);
                            }
                            cursor1.close();

                            stockReceiveDetails.setSubTotal(stockReceiveDetails.getQuantity() * stockReceiveDetails.getUTD_Cost());
                            itemList.add(stockReceiveDetails);
                            adapter.notifyItemInserted(itemList.size() - 1);
                            updateTotals();

                            barcodeDtlRemark = "";
                            barcodeDtlRemark2 = "";
                            myQty = 1;

                        }
                    }
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StockReceive_AddNewItem.this);
                builder.setTitle("Clear Items");
                builder.setMessage("Are you sure you want to clear all the items?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        itemList.clear();
                        adapter.notifyDataSetChanged();
                        updateTotals();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnMerge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.size() > 0){
                    List<AC_Class.StockReceiveDetails> oriDetail = itemList;
                    ArrayList<AC_Class.StockReceiveDetails> newDetail = new ArrayList<>();

                    for (int i = 0; i < oriDetail.size(); i++){
                        boolean result = false;
                        for (AC_Class.StockReceiveDetails myDtl : newDetail){
                            if (oriDetail.get(i).getBatch_No() != null){
                                if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM()) && oriDetail.get(i).getUTD_Cost().equals(myDtl.getUTD_Cost()) && oriDetail.get(i).getRemarks().equals(myDtl.getRemarks()) && oriDetail.get(i).getBatch_No().equals(myDtl.getBatch_No())) {
                                    myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                                    myDtl.setSubTotal(myDtl.getSubTotal() + oriDetail.get(i).getSubTotal());

                                    result = true;
                                }
                            } else {
                                if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM()) && oriDetail.get(i).getUTD_Cost().equals(myDtl.getUTD_Cost()) && oriDetail.get(i).getRemarks().equals(myDtl.getRemarks())){
                                    myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                                    myDtl.setSubTotal(myDtl.getSubTotal() + oriDetail.get(i).getSubTotal());

                                    result = true;
                                }

                            }
                        }
                        if (!result){
                            newDetail.add(oriDetail.get(i));
                        }
                    }
                    itemList.clear();
                    itemList.addAll(newDetail);
                    adapter.notifyDataSetChanged();
                    updateTotals();

                } else {
                    Toast.makeText(StockReceive_AddNewItem.this, "No items available to merge", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StockReceive_AddNewItem.this);
        builder.setMessage("What do you want to do?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemChanged(position);
                updateTotals();
            }
        });
        builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent intent = new Intent(StockReceive_AddNewItem.this,
                        StockReceive_AddItemManual.class);
                intent.putExtra("stockReceive", stockReceive);
                intent.putExtra("nItem", itemList.get(position));
                intent.putExtra("nPosition", position);
                intent.putExtra("isUpdateMode", true);
                intent.putExtra("subString", "");
                startActivityForResult(intent, REQUEST_CODE_UPDATE_ITEM);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void saveData() {
        if (func.equals("New")){

            new AlertDialog.Builder(this).setTitle("Confirmation")
                    .setMessage("Are you sure you want to save this data?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //save stock receive
                            boolean isInserted = db.insertStockReceive(stockReceive);
                            if (stockReceive != null){
                                if (isInserted){
                                    //save stock receive details
                                    boolean isItemInserted = db.insertStockReceiveDtl(itemList);
                                    if (!isItemInserted){
                                        Toast.makeText(StockReceive_AddNewItem.this, "Item failed to insert to database", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(StockReceive_AddNewItem.this, "Stock Receive Added Successfully", Toast.LENGTH_SHORT).show();

                                    if (stockReceive.getDocNo().equals(db.getSRNextDocNo())) {
                                        db.IncrementAutoNumbering("SR");
                                    }


                                    Intent intent = new Intent(StockReceive_AddNewItem.this, StockReceive_Details.class);
                                    intent.putExtra("StockReceive", stockReceive);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(StockReceive_AddNewItem.this, "Failed to insert data to database.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("AddNewItem", "Stock Receive Null");
                            }
                        }
                    })
                    .show();

        } else if (func.equals("Edit")) {
            new AlertDialog.Builder(this).setTitle("Confirmation")
                    .setMessage("Are you sure you want to update this data")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //delete existing item details record then only add new
                            boolean isSRDetailDeleted = db.deleteStockReceiveDetail(stockReceive.getDocNo());

                            if (isSRDetailDeleted){
                                boolean isItemInserted = db.insertStockReceiveDtl(itemList);
                                if (!isItemInserted){
                                    Toast.makeText(StockReceive_AddNewItem.this, "Item failed to insert to database", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    //update stock receive
                                    boolean isUpdated = db.updateStockReceive(stockReceive);
                                    if (isUpdated){
                                        Toast.makeText(StockReceive_AddNewItem.this, "Stock Receive Updated", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(StockReceive_AddNewItem.this, StockReceive_Details.class);
                                        intent.putExtra("StockReceive", stockReceive);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(StockReceive_AddNewItem.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(StockReceive_AddNewItem.this, "Update Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
                //The scanner in the same class is used.
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "No result found.", Toast.LENGTH_SHORT).show();
                    } else {
                        invdtl_editText.setText(result.getContents().trim());
                    }
                }
                break;

                //item -> manual -> detail -> manual -> item
            case REQUEST_CODE_ADD_ITEM_MANUAL:
                AC_Class.StockReceiveDetails newItem = data.getParcelableExtra("StockReceiveDetail");
                if (newItem != null) {
                    itemList.add(newItem);
                    adapter.notifyItemInserted(itemList.size() - 1);
                    updateTotals();
                }
                break;

            case REQUEST_CODE_UPDATE_ITEM:
                AC_Class.StockReceiveDetails updateItem = data.getParcelableExtra("StockReceiveDetail");
                int nPosition = data.getIntExtra("position", -1);
                if (updateItem != null){
                    itemList.set(nPosition, updateItem);
                    // Notify the adapter that the item has changed
                    adapter.notifyItemChanged(nPosition);
                    //delete the existing item in the position
//                    itemList.remove(nPosition);
//                    adapter.notifyItemRemoved(nPosition);
//                    itemList.add(updateItem);
//                    adapter.notifyItemInserted(itemList.size() - 1);
                    updateTotals();
                }
                break;
        }

    }

    private void updateTotals() {
        int totalQuantity = 0;
        double totalItems = 0.0;

        for (AC_Class.StockReceiveDetails item : itemList) {
            totalQuantity += item.getQuantity();
            totalItems += item.getSubTotal();
        }

        totalQty_textView.setText(String.valueOf(totalQuantity));
        totalItem_textView.setText(String.valueOf(itemList.size()));
        totalAmount_textView.setText(String.format("%.2f", totalItems));
        btnMerge.setText("Merge (" + itemList.size() + ")");
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void getCurrentDataForEdit() {
        ActionBar actionBar = getSupportActionBar();

        switch (func) {
            case "New":
                actionBar.setTitle("Stock Receive - Add New Item");

                stockReceive = getIntent().getParcelableExtra("stockReceive");

                break;

            case "Edit":
                actionBar.setTitle("Stock Receive - Edit Item");

                stockReceive = getIntent().getParcelableExtra("stockReceive");

                btnSave.setText("Update");

                itemList.clear();
                Cursor cursor = db.getStockReceiveDetailsByDocNo(stockReceive.getDocNo());
                if (cursor != null && cursor.getCount() > 0){
                    while (cursor.moveToNext()){
                        Integer id = cursor.getInt(cursor.getColumnIndex("ID"));
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

                        updateTotals();
                    }
                    cursor.close();
                }
                adapter.notifyDataSetChanged();

                break;


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inv_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            case (R.id.addStockCount): {
                final Intent intent = new Intent(StockReceive_AddNewItem.this,
                        StockReceive_AddItemManual.class);
                intent.putExtra("stockReceive", stockReceive);
                intent.putExtra("iFunc", "New");
                intent.putExtra("subString", "");
                //Open Manual
                intent.putExtra("iMode", 0);
                startActivityForResult(intent, REQUEST_CODE_ADD_ITEM_MANUAL);

                break;
            }

            case (R.id.addStockCount2): {
                try {
                    qrScan = new IntentIntegrator(this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(false);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

                } catch (Exception e) {
                    Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage());
                }
            }
            break;
        }
        return false;
    }

}

