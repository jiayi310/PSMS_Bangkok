package com.example.androidmobilestock_bangkok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.databinding.DataBindingUtil;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.PlActivityPldtllistBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PL_PLDtlList extends AppCompatActivity {
    PlActivityPldtllistBinding binding;
    AC_Class.DO packingList;
    ACDatabase db;

    MyClickHandler handler;
    //PL_PLDtlListViewAdapter adapter;
    PLCartListAdapter adapter;
    PLItemListViewAdapter arrayAdapter;
    String typeFP;
    String def_loc;
    Button btnMerge;
    private IntentIntegrator qrScan;
    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;
    AC_Class.DODtl tempDtl;
    AC_Class.DODtl packingListDtl;
    AC_Class.Item tempItem;
    String hasBatch;
    Boolean isBatchNoEnabled = true;
    Boolean isSaleBatchEnabled = true;
    String salesOrderType = "";
    List<AC_Class.Item> s_inquiry = new ArrayList<>();
    private PLCartListAdapter.RecyclerViewClickListener listener;
    Boolean hasBatchNoItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pl_activity_pldtllist);

        db = new ACDatabase(this);

        btnMerge = findViewById(R.id.btnMerge);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Packing List Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));
        binding.invdtlEditText.requestFocus();

        packingList = getIntent().getParcelableExtra("iPackingList");
        typeFP = getIntent().getStringExtra("FunctionKey");

        binding.setPL(packingList);
        getPLDtlList();

        arrayAdapter = new PLItemListViewAdapter(this, s_inquiry);
        binding.lvPackingListDtl.setAdapter(arrayAdapter);
        getItemData2();

        Cursor data = db.getReg("25");
        if (data.moveToFirst()) {
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
        }

        Cursor cursor5 = db.getReg("38");
        if (cursor5.moveToFirst()) {
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor loc = db.getReg("7");
        if (loc.moveToFirst()) {
            def_loc = loc.getString(0);
        }
        if (def_loc.equals("None")) {
            def_loc = null;
        }

        Cursor cursor7 = db.getReg("39");
        if (cursor7.moveToFirst()) {
            isSaleBatchEnabled = Boolean.valueOf(cursor7.getString(0));
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        if (isCustomBarcodeEnabled) {

            Cursor data2 = db.getReg("26");
            if (data2.moveToFirst()) {
                barcodeLength = Integer.valueOf(data2.getString(0));
            }

            Cursor data3 = db.getReg("27");
            if (data3.moveToFirst()) {
                itemStart = Integer.valueOf(data3.getString(0));
            }

            Cursor data4 = db.getReg("28");
            if (data4.moveToFirst()) {
                itemLength = Integer.valueOf(data4.getString(0));
            }

            Cursor data5 = db.getReg("29");
            if (data5.moveToFirst()) {
                qtyStart = Integer.valueOf(data5.getString(0));
            }

            Cursor data6 = db.getReg("30");
            if (data6.moveToFirst()) {
                qtyLength = Integer.valueOf(data6.getString(0));
            }

            Cursor data7 = db.getReg("31");
            if (data7.moveToFirst()) {
                qtyDecimal = Integer.valueOf(data7.getString(0));
            }

        }

        binding.lvPackingListDtl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                tempItem = (AC_Class.Item) parent.getItemAtPosition(position);
                if (tempItem != null) {
                    tempDtl = new AC_Class.DODtl();
                    tempDtl.setDocNo(packingList.getDocNo());
                    tempDtl.setItemCode(tempItem.getItemCode());
                    tempDtl.setItemDescription(tempItem.getDescription());
                    tempDtl.setUOM(tempItem.getUOM());
                    tempDtl.setQty(1.00);

//                    if (def_loc == null)
//                    {
//                    tempDtl.setLocation(tempItem.getLocation());
//
                        Intent new_intent_loc = new Intent(PL_PLDtlList.this, Location_List.class);
                        startActivityForResult(new_intent_loc, 10);
//                    }
//                    else {
//                        tempDtl.setLocation(tempItem.getLocation());
//
//                        if (tempItem.getHasBatchNo() != null) {
//                            if (isBatchNoEnabled && tempItem.getHasBatchNo().equals("true")) {
//                                hasBatchNoItem = true;
//                                if (isSaleBatchEnabled) {
//
//                                    if (salesOrderType != "0" || salesOrderType != null) {
//
//                                        if (salesOrderType.equals("Latest Manufacture Date")) {
//
//                                            Cursor data2 =
//                                                    db.getLateManuBatch(tempDtl.getItemCode(),
//                                                            tempDtl.getUOM(),
//                                                            tempDtl.getLocation());
//
//                                            if (data2.moveToFirst()) {
//
//                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
//
//                                            } else {
//                                                tempDtl.setBatchNo(null);
//                                            }
//                                        } else if (salesOrderType.equals("Earliest Manufacture Date")) {
//                                            Cursor data2 =
//                                                    db.getEarManuBatch(tempDtl.getItemCode(),
//                                                            tempDtl.getUOM(),
//                                                            tempDtl.getLocation());
//
//                                            if (data2.moveToFirst()) {
//
//                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
//
//                                            } else {
//                                                tempDtl.setBatchNo(null);
//                                            }
//                                        } else if (salesOrderType.equals("Latest Expiration Date")) {
//                                            Cursor data2 =
//                                                    db.getLateExpBatch(tempDtl.getItemCode(),
//                                                            tempDtl.getUOM(),
//                                                            tempDtl.getLocation());
//
//                                            if (data2.moveToFirst()) {
//
//                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
//
//                                            } else {
//                                                tempDtl.setBatchNo(null);
//                                            }
//                                        } else if (salesOrderType.equals("Earliest Expiration Date")) {
//                                            Cursor data2 =
//                                                    db.getEarExpBatch(tempDtl.getItemCode(),
//                                                            tempDtl.getUOM(),
//                                                            tempDtl.getLocation());
//
//                                            if (data2.moveToFirst()) {
//
//                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
//
//                                            } else {
//                                                tempDtl.setBatchNo(null);
//                                            }
//                                        }
//
//                                    } else {
//                                        Cursor data2 =
//                                                db.getEarManuBatch(tempDtl.getItemCode(),
//                                                        tempDtl.getUOM(),
//                                                        tempDtl.getLocation());
//
//                                        if (data2.moveToFirst()) {
//
//                                            tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
//
//                                        } else {
//                                            tempDtl.setBatchNo(null);
//                                        }
//                                        tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
//                                    }
//
//                                    //packingList.getDODtlList().add(tempDtl);
//                                    //adapter.notifyDataSetChanged();
//                                    checkDup();
//                                } else {
//                                    Intent new_intent = new Intent(PL_PLDtlList.this, ItemBatchList.class);
//                                    new_intent.putExtra("ItemCode", tempDtl.getItemCode());
//                                    new_intent.putExtra("UOM", tempDtl.getUOM());
//                                    new_intent.putExtra("Location", tempDtl.getLocation());
//                                    startActivityForResult(new_intent, 9);
//                                }
//                            } else {
//                                hasBatchNoItem = false;
//                                tempDtl.setBatchNo(null);
//                                checkDup();
//                            }
//                        } else {
//                            checkDup();
//                        }
//                    }

                }
            }
        });

        binding.invdtlEditText.requestFocus();

        binding.invdtlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                startScan();
                String myBarcode = s.toString();
                String default_location = "";
                double myQty = 1;

                Cursor dl = db.getReg("7");
                if (dl.moveToFirst()) {
                    default_location = dl.getString(0);
                }

                if (!myBarcode.equals("") && !TextUtils.isEmpty(binding.invdtlEditText.getText())) {
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled) {
                        if (myBarcode.length() == barcodeLength) {
                            if (isNumeric(myBarcode)) {
                                myQty = Double.valueOf(myBarcode.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                if (qtyDecimal > 0) {
                                    myQty = myQty / Math.pow(10, qtyDecimal);
                                }
                                myBarcode = myBarcode.substring(itemStart - 1, itemStart - 1 + itemLength);
                            } else {
                                Toast.makeText(PL_PLDtlList.this, "Invalid Barcode.",
                                        Toast.LENGTH_SHORT).show();
                                isSkip = true;
                            }
                        }
                    }

                    if (!isSkip) {

                        Cursor results = db.getItemBC(myBarcode);
                        if (results.getCount() == 0) {
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            AlertDialog alertDialog = new AlertDialog.Builder(PL_PLDtlList.this).create();
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

                            tempDtl = new AC_Class.DODtl(packingList.getDocNo(), def_loc);
                            //packingListDtl.setLocation(default_location);
                            tempDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));


                            if(CheckExistSOItem()) {
                                tempDtl.setItemDescription(results.getString(results.getColumnIndex("Description")));
                                tempDtl.setUOM(results.getString(results.getColumnIndex("UOM")));
                                hasBatch = results.getString(results.getColumnIndex("HasBatchNo"));
                                if (isCustomBarcodeEnabled) {
                                    tempDtl.setQty(myQty);
                                    packingList.getDODtlList().add(tempDtl);
                                    adapter.notifyDataSetChanged();
                                } else {

//
//                                if (!CheckExist(tempDtl)) {
                                    Intent new_intent3 = new Intent(PL_PLDtlList.this,
                                            PL_Location_List.class);
                                    new_intent3.putExtra("ItemCode", tempDtl.getItemCode());
                                    new_intent3.putExtra("UOM", tempDtl.getUOM());
                                    new_intent3.putExtra("DocNo", packingList.getFromDocNo());
                                    startActivityForResult(new_intent3, 11);
                                    //packingList.getDODtlList().add(packingListDtl);
//                                }else{
//                                    checkDup();
//                                }
                                    getPLDtlList();
                                }

                            }else{
                                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                                AlertDialog alertDialog = new AlertDialog.Builder(PL_PLDtlList.this).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Product not found in SO.");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    }
                }

                binding.invdtlEditText.removeTextChangedListener(this);
                binding.invdtlEditText.clearFocus();
                binding.invdtlEditText.requestFocus();
                binding.invdtlEditText.setText("");
                binding.invdtlEditText.addTextChangedListener(this);


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

    public void getPLDtlList() {

        setOnClickListener();
        binding.invdtllistItemlist2.setHasFixedSize(true);
        binding.invdtllistItemlist2.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PLCartListAdapter(this, packingList.getDODtlList(), listener);
        binding.invdtllistItemlist2.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getSummary();
        binding.lblTotalItem.setText(Integer.toString(packingList.getDODtlList().size()));
    }

    public void setOnClickListener() {
        listener = new PLCartListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PL_PLDtlList.this);
                builder.setMessage("What do you want to do?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        packingList.removeDODtl(position);
                        getPLDtlList();
                    }
                });

                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AC_Class.DODtl tempDtl = (AC_Class.DODtl) packingList.getDODtlList().get(position);

                        Cursor cur = db.getPOItem_hasBatch(tempDtl.getItemCode());
                        if(cur.moveToFirst()){
                            String hasBatch = cur.getString(0);
                            if(hasBatch!=null){
                                if(hasBatch.equals("true")){
                                    hasBatchNoItem = true;
                                }else{
                                    hasBatchNoItem = false;
                                }
                            }else{
                                hasBatchNoItem = false;
                            }
                        }

                        Intent intent = new Intent(PL_PLDtlList.this, PL_PLDtlAddManual.class);
                        intent.putExtra("iPackingList", packingList);
                        intent.putExtra("iPackingListDtl", tempDtl);
                        intent.putExtra("FunctionKey", "Edit");
                        intent.putExtra("hasBatch", hasBatchNoItem);

                        packingList.removeDODtl(position);

                        startActivityForResult(intent, 1);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

            @Override
            public void onAddClick(ImageButton v, int position) {
                packingList.getDODtlList().get(position).setQty(packingList.getDODtlList().get(position).getQty() + 1);
                getSummary();
            }

            @Override
            public void onSubClick(ImageButton v, int position) {
                if (packingList.getDODtlList().get(position).getQty() > 1) {
                    packingList.getDODtlList().get(position).setQty(packingList.getDODtlList().get(position).getQty() - 1);
                }
                getSummary();
            }

        };
    }

    public void getSummary() {
        float totalQty = 0.0f;

        for (int i = 0; i < packingList.getDODtlList().size(); i++) {
            totalQty += packingList.getDODtlList().get(i).getQty();
        }
        binding.lblTotal.setText(String.format(Locale.getDefault(),
                " %.02f", totalQty));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Return from item
            //AddManual
            case 1:

                if (resultCode == 0) {
                    tempDtl = data.getParcelableExtra("iPackingListDtl");

                }
                if (tempDtl != null) {
                    packingList.getDODtlList().add(tempDtl);
                    tempDtl = null;
                }

                break;
            //AddBarcode
            case 2:
                if (resultCode == 0) {
                    List<AC_Class.DODtl> DODtlList =
                            data.getParcelableArrayListExtra("iPackingListDtlList");

                    for (int i = 0; i < DODtlList.size(); i++) {
                        packingList.getDODtlList().add(DODtlList.get(i));
                    }
                }
                break;

            case 9:
                String batchNo = data.getStringExtra("ItemBatchNo");
                if(batchNo != null) {
                    if(tempDtl!=null) {
                        tempDtl.setBatchNo(batchNo);
                        checkDup();
                    }

                    //packingList.getDODtlList().add(tempDtl);
                    adapter.notifyDataSetChanged();
                }

                break;

            case 10:
                if (resultCode == 1) {
                    AC_Class.Location Location = data.getParcelableExtra("LocationsKey");
                    if (!Location.getLocation().equals("None")) {
                        tempDtl.setLocation(Location.getLocation());

                        if (tempItem.getHasBatchNo() != null) {
                            if (isBatchNoEnabled && tempItem.getHasBatchNo().equals("true")) {
                                hasBatchNoItem = true;

                                if (isSaleBatchEnabled) {

                                    if (salesOrderType != "0" || salesOrderType != null) {

                                        if (salesOrderType.equals("Latest Manufacture Date")) {

                                            Cursor data2 =
                                                    db.getLateManuBatch(tempDtl.getItemCode(),
                                                            tempDtl.getUOM(),
                                                            tempDtl.getLocation());

                                            if (data2.moveToFirst()) {

                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                            } else {
                                                tempDtl.setBatchNo(null);
                                            }
                                        } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                                            Cursor data2 =
                                                    db.getEarManuBatch(tempDtl.getItemCode(),
                                                            tempDtl.getUOM(),
                                                            tempDtl.getLocation());

                                            if (data2.moveToFirst()) {

                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                            } else {
                                                tempDtl.setBatchNo(null);
                                            }
                                        } else if (salesOrderType.equals("Latest Expiration Date")) {
                                            Cursor data2 =
                                                    db.getLateExpBatch(tempDtl.getItemCode(),
                                                            tempDtl.getUOM(),
                                                            tempDtl.getLocation());

                                            if (data2.moveToFirst()) {

                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                            } else {
                                                tempDtl.setBatchNo(null);
                                            }
                                        } else if (salesOrderType.equals("Earliest Expiration Date")) {
                                            Cursor data2 =
                                                    db.getEarExpBatch(tempDtl.getItemCode(),
                                                            tempDtl.getUOM(),
                                                            tempDtl.getLocation());

                                            if (data2.moveToFirst()) {

                                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                            } else {
                                                tempDtl.setBatchNo(null);
                                            }
                                        }

                                    } else {
                                        Cursor data2 =
                                                db.getEarManuBatch(tempDtl.getItemCode(),
                                                        tempDtl.getUOM(),
                                                        tempDtl.getLocation());

                                        if (data2.moveToFirst()) {

                                            tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            tempDtl.setBatchNo(null);
                                        }
                                        tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                    }

                                    checkDup();
                                    adapter.notifyDataSetChanged();
                                }else{
                                    Intent new_intent = new Intent(PL_PLDtlList.this, ItemBatchList.class);
                                    new_intent.putExtra("ItemCode", tempDtl.getItemCode());
                                    new_intent.putExtra("UOM", tempDtl.getUOM());
                                    new_intent.putExtra("Location", tempDtl.getLocation());
                                    startActivityForResult(new_intent, 9);
                                }
                            } else {
                                hasBatchNoItem = false;
                                tempDtl.setBatchNo(null);
                                checkDup();
                            }
                        } else {
                            checkDup();
                        }
                    }
                    else {
                        Intent new_intent_loc = new Intent(PL_PLDtlList.this, Location_List.class);
                        startActivityForResult(new_intent_loc, 10);
                    }
                }
                break;

            case 11:
                if (resultCode == 3) {
                    AC_Class.Location Location = data.getParcelableExtra("LocationsKey");

                    tempDtl.setLocation(Location.getLocation());

                } else {
                    Log.i("custDebug", "cancelled agent");
                }

                if (isBatchNoEnabled && hasBatch.equals("true")) {

                    if (isSaleBatchEnabled) {

                        if (salesOrderType != "0" || salesOrderType != null) {

                            if (salesOrderType.equals("Latest Manufacture Date")) {

                                Cursor data2 =
                                        db.getLateManuBatch(tempDtl.getItemCode(),
                                                tempDtl.getUOM(),
                                                tempDtl.getLocation());

                                if (data2.moveToFirst()) {

                                    tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                } else {
                                    tempDtl.setBatchNo(null);
                                }
                            } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                                Cursor data2 =
                                        db.getEarManuBatch(tempDtl.getItemCode(),
                                                tempDtl.getUOM(),
                                                tempDtl.getLocation());

                                if (data2.moveToFirst()) {

                                    tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                } else {
                                    tempDtl.setBatchNo(null);
                                }
                            } else if (salesOrderType.equals("Latest Expiration Date")) {
                                Cursor data2 =
                                        db.getLateExpBatch(tempDtl.getItemCode(),
                                                tempDtl.getUOM(),
                                                tempDtl.getLocation());

                                if (data2.moveToFirst()) {

                                    tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                } else {
                                    tempDtl.setBatchNo(null);
                                }
                            } else if (salesOrderType.equals("Earliest Expiration Date")) {
                                Cursor data2 =
                                        db.getEarExpBatch(tempDtl.getItemCode(),
                                                tempDtl.getUOM(),
                                                tempDtl.getLocation());

                                if (data2.moveToFirst()) {

                                    tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                } else {
                                    tempDtl.setBatchNo(null);
                                }
                            }

                        } else {
                            Cursor data2 =
                                    db.getEarManuBatch(tempDtl.getItemCode(),
                                            tempDtl.getUOM(),
                                            tempDtl.getLocation());

                            if (data2.moveToFirst()) {

                                tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                            } else {
                                tempDtl.setBatchNo(null);
                            }
                            tempDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                        }

                        packingList.getDODtlList().add(tempDtl);
                        adapter.notifyDataSetChanged();
                    }else{
                        Intent new_intent = new Intent(PL_PLDtlList.this, ItemBatchList.class);
                        new_intent.putExtra("ItemCode", tempDtl.getItemCode());
                        new_intent.putExtra("UOM", tempDtl.getUOM());
                        new_intent.putExtra("Location", tempDtl.getLocation());
                        startActivityForResult(new_intent, 9);
                    }

                }else{
                    packingList.getDODtlList().add(tempDtl);
                    adapter.notifyDataSetChanged();
                }
                break;

            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.invdtlEditText.setText(result.getContents());
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPLDtlList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("iPackingList", packingList);
        setResult(1, intent);
        super.onBackPressed();
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

            case (R.id.addStockCount2): {

                try {
                    qrScan = new IntentIntegrator(this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(false);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
                    //binding.invdtlEditText.setText("2501735075008");

                } catch (Exception e) {
                    Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage());
                }
            }
            break;
            case (R.id.addStockCount): {
                //Open Manual
                final Intent intent = new Intent(PL_PLDtlList.this,
                        PL_PLDtlAddManual.class);
                intent.putExtra("iPackingList", packingList);
                intent.putExtra("FunctionKey", "New");
                intent.putExtra("mode", 0);
                startActivityForResult(intent, 1);
                break;

            }
        }
        return false;
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onEditTextViewClicked(View view) {
            findViewById(R.id.invdtl_editText).requestFocus();
            hideSoftKeyboard(view);
        }

        public void OnbtnSaveClicked(View view) {
            if (packingList.getDODtlList().size() > 0) {
                if (!typeFP.equals("Edit"))
                    packingList.setDocNo(db.getNextPackingListNo());

                Intent myIntent = new Intent(PL_PLDtlList.this, PL_Compare.class);
                myIntent.putExtra("iPackingList", packingList);
                myIntent.putExtra("iType", typeFP);
                startActivityForResult(myIntent, 3);

            } else {
                Toast.makeText(PL_PLDtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }

        public void OnbtnMergeClicked(View view) {
            if (packingList.getDODtlList().size() > 0) {
                List<AC_Class.DODtl> oriDtlList = packingList.getDODtlList();
                ArrayList<AC_Class.DODtl> newDtlList = new ArrayList<>();

                for (int i = 0; i < oriDtlList.size(); i++) {
                    boolean result = false;

                    for (AC_Class.DODtl myDtl : newDtlList) {
                        if(oriDtlList.get(i).getBatchNo()!=null) {
                            if (oriDtlList.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDtlList.get(i).getUOM().equals(myDtl.getUOM()) && oriDtlList.get(i).getBatchNo().equals(myDtl.getBatchNo()) && oriDtlList.get(i).getLocation().equals(myDtl.getLocation())) {
                                myDtl.setQty(myDtl.getQty() + oriDtlList.get(i).getQty());
                                result = true;
                            }
                        }else{
                            if (oriDtlList.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDtlList.get(i).getUOM().equals(myDtl.getUOM()) && oriDtlList.get(i).getLocation().equals(myDtl.getLocation())) {
                                myDtl.setQty(myDtl.getQty() + oriDtlList.get(i).getQty());
                                result = true;
                            }
                        }
                    }

                    if (!result) {
                        newDtlList.add(oriDtlList.get(i));
                    }
                }

                packingList.getDODtlList().clear();
                packingList.setDODtlList(newDtlList);

                getPLDtlList();
            }
        }

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

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view!=null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean CheckExist(AC_Class.DODtl packingListDtlFP) {
        boolean myresult = false;
        for (int i = 0; i < packingList.getDODtlList().size(); i++) {
            AC_Class.DODtl tempDtl = packingList.getDODtlList().get(i);

            if (tempDtl.getItemCode().equals(packingListDtlFP.getItemCode()) && tempDtl.getUOM().equals(packingListDtlFP.getUOM())) {
                Log.wtf("Item Code", packingListDtlFP.getItemCode());
                packingList.getDODtlList().get(i).setQty(packingList.getDODtlList().get(i).getQty() + 1);
                myresult = true;
            }
        }
        return myresult;
    }

    private void getItemData2() {
        Cursor data = db.getSOItem("",packingList.getFromDocNo());
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1),
                            data.getString(2), data.getString(3), data.getString(4), data.getString(5),
                            data.getString(6), data.getString(7), data.getString(8), data.getDouble(9),
                            data.getString(10));

                    s_inquiry.add(item);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void checkDup(){
        int j =0;
        for(int i =0; i < packingList.getDODtlList().size(); i++){


                if (packingList.getDODtlList().get(i).getBatchNo() != null) {
                    if (packingList.getDODtlList().get(i).getItemCode().equals(tempDtl.getItemCode()) && packingList.getDODtlList().get(i).getItemDescription().equals(tempDtl.getItemDescription()) &&
                            packingList.getDODtlList().get(i).getUOM().equals(tempDtl.getUOM()) && packingList.getDODtlList().get(i).getBatchNo().equals(tempDtl.getBatchNo()) && packingList.getDODtlList().get(i).getLocation().equals(tempDtl.getLocation())) {
                        j++;
                        packingList.getDODtlList().get(i).setQty(packingList.getDODtlList().get(i).getQty() + 1);
                        break;
                    }
                } else {
                    if (packingList.getDODtlList().get(i).getItemCode().equals(tempDtl.getItemCode()) && packingList.getDODtlList().get(i).getItemDescription().equals(tempDtl.getItemDescription()) &&
                            packingList.getDODtlList().get(i).getUOM().equals(tempDtl.getUOM()) && packingList.getDODtlList().get(i).getLocation().equals(tempDtl.getLocation())) {
                        j++;
                        packingList.getDODtlList().get(i).setQty(packingList.getDODtlList().get(i).getQty() + 1);
                        break;
                    }
                }

        }
        if(j==0){
            if(tempDtl!=null) {
                packingList.getDODtlList().add(tempDtl);
            }
        }
        getPLDtlList();
    }

    public boolean CheckExistSOItem()  {
        boolean result = false;
        for(int i=0; i<s_inquiry.size(); i++){
            if( s_inquiry.get(i).getItemCode().equals(tempDtl.getItemCode())){
                result = true;
            }
        }
        return result;
    }

}