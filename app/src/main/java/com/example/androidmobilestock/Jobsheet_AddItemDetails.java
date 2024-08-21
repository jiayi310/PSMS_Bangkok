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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Jobsheet_AddItemDetails extends AppCompatActivity {

    TextView tv_DocNo, tv_itemCode, barcode_btn, search_btn, tv_description, uom_text, lbl_batch_no, tv_batchNo, tv_stock, tv_subTotal, tv_taxType, tv_taxRate, tv_taxValue, tv_totalEx, tv_totalIn;
    ImageButton batchNo_btn, btn_Dec, btn_Inc, checkList_btn, histList_btn, discountList_btn;
    EditText editText_remarks, editText_remarks2, editText_qty, editText_unitPrice, editText_discount;
    Button btncancel, btnsave;                                                          
    ACDatabase db;
    AC_Class.Item item;
    AC_Class.JobSheet jobSheet;
    AC_Class.JobSheetDetails jobSheetDetails;
    IntentIntegrator qrScan;
    String scannedData;
    String default_location = "";
    int FilterByAgent = 0;
    String Agent;
    float balanceQty = 0f;
    NumberFormat nf = new DecimalFormat("##.###");
    Boolean isAutoPrice = false;
    Boolean isBatchNoEnabled = true;
    Boolean hasBatchNoItem = false;
    Boolean isSaleBatchEnabled = true;
    String salesOrderType = "";
    Boolean isTaxEnabled = true;
    String defaultTax = "";
    Boolean isTaxInclusive;
    Double dis, disNumber;
    String isPercentage, isChecked;
    Boolean NegativeInventory = true;
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
    Cursor results;
    double myQty = 1;
    int position;
    boolean isUpdateMode;
    boolean fromItemPage;

    private static final int REQUEST_CODE_UPDATE_ITEM2 = 100;


    //InvADManualFragment.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet_add_item_details);

        tv_DocNo = findViewById(R.id.tv_DocNo);
        tv_itemCode = findViewById(R.id.tv_itemCode);
        barcode_btn = findViewById(R.id.barcode_btn);
        search_btn = findViewById(R.id.search_btn);
        tv_description = findViewById(R.id.tv_description);
        uom_text = findViewById(R.id.uom_text);
        lbl_batch_no = findViewById(R.id.lbl_batch_no);
        tv_batchNo = findViewById(R.id.tv_batchNo);
        batchNo_btn = findViewById(R.id.batchNo_btn);
        tv_stock = findViewById(R.id.tv_stock);
        editText_remarks = findViewById(R.id.editText_remarks);
        editText_remarks2 = findViewById(R.id.editText_remarks2);
        btn_Dec = findViewById(R.id.btn_Dec);
        btn_Inc = findViewById(R.id.btn_Inc);
        editText_qty = findViewById(R.id.editText_qty);
        editText_unitPrice = findViewById(R.id.editText_unitPrice);
        checkList_btn = findViewById(R.id.checkList_btn);
        histList_btn = findViewById(R.id.histList_btn);
        editText_discount = findViewById(R.id.editText_discount);
        discountList_btn = findViewById(R.id.discountList_btn);
        tv_subTotal = findViewById(R.id.tv_subTotal);
        tv_taxType = findViewById(R.id.tv_taxType);
        tv_taxRate = findViewById(R.id.tv_taxRate);
        tv_taxValue = findViewById(R.id.tv_taxValue);
        tv_totalEx = findViewById(R.id.tv_totalEx);
        tv_totalIn = findViewById(R.id.tv_totalIn);
        btncancel = findViewById(R.id.btncancel);
        btnsave = findViewById(R.id.btnsave);

        db = new ACDatabase(this);
        jobSheetDetails = new AC_Class.JobSheetDetails();

        jobSheet = getIntent().getParcelableExtra("JobSheet");
        item = getIntent().getParcelableExtra("ItemDetails");


        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);

        position = getIntent().getIntExtra("nPosition", -1);
        fromItemPage = getIntent().getBooleanExtra("fromItemPage", false);



        if (isUpdateMode){
            jobSheetDetails = getIntent().getParcelableExtra("nItem");
            position = getIntent().getIntExtra("nPosition", -1);
        }

        scannedData = getIntent().getStringExtra("scannedData");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (isUpdateMode){
            actionBar.setTitle("Edit Item Details");
            btnsave.setText("Update Item");
        } else {
            actionBar.setTitle("Add Item Details");
        }

        Cursor loc = db.getReg("7");
        if (loc.moveToFirst()) {
            default_location = loc.getString(0);
        }

        if (default_location.equals("None")) {
            default_location = null;
        } else {
            jobSheetDetails.setLocation(default_location);
        }

        Cursor data2 = db.getReg("14");
        if (data2.moveToFirst()) {
            FilterByAgent = Integer.valueOf(data2.getString(0));
        }

        Cursor data3 = db.getReg("4");
        if (data3.moveToFirst()) {
            Agent = data3.getString(0);
        }

        Cursor cursor1 = db.getReg("21");
        if (cursor1.moveToFirst()) {
            defaultTax = cursor1.getString(0);
        } else {
            Log.d("Checking123", "defaultTax: no data");
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        } else {
            Log.d("Checking123", "isTaxEnables: no data" );
        }

        Cursor cursor2 = db.getReg("13");
        if (cursor2.moveToFirst()) {
            isTaxInclusive = Boolean.valueOf(cursor2.getString(0));
        } else {
            Log.d("Checking123", "isTaxInclusive: no data");
        }

        Cursor cursor4 = db.getReg("20");
        if (cursor4.moveToFirst()) {
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor5 = db.getReg("38");
        if (cursor5.moveToFirst()) {
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor7 = db.getReg("39");
        if (cursor7.moveToFirst()) {
            isSaleBatchEnabled = Boolean.valueOf(cursor7.getString(0));
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        Cursor cursor9 = db.getReg("42");
        if (cursor9.moveToFirst()) {
            NegativeInventory = Boolean.valueOf(cursor9.getString(0));
        }

        Cursor data = db.getReg("25");
        if (data.moveToFirst()) {
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
        }

        if (isCustomBarcodeEnabled) {

            Cursor data12 = db.getReg("26");
            if (data12.moveToFirst()) {
                barcodeLength = Integer.valueOf(data12.getString(0));
            }

            Cursor data13 = db.getReg("27");
            if (data13.moveToFirst()) {
                itemStart = Integer.valueOf(data13.getString(0));
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

        lbl_batch_no.setVisibility(View.GONE);
        tv_batchNo.setVisibility(View.GONE);
        batchNo_btn.setVisibility(View.GONE);

        if (isBatchNoEnabled && jobSheetDetails.getBatchNo() != null) {
            tv_batchNo.setVisibility(View.VISIBLE);
            lbl_batch_no.setVisibility(View.VISIBLE);
            batchNo_btn.setVisibility(View.VISIBLE);
        } else {
            tv_batchNo.setVisibility(View.GONE);
            lbl_batch_no.setVisibility(View.GONE);
            batchNo_btn.setVisibility(View.GONE);
        }

        //getInvoiceDetailsForm();

        if (scannedData != null){
            tv_DocNo.setText(jobSheet.getDocNo());
            tv_itemCode.post(new Runnable() {
                @Override
                public void run() {
                    tv_itemCode.setText(scannedData);
                }
            });

        }

        if (item != null){
            if (fromItemPage){
                getItem();
                saveData();
            }else {
                getItem();
                //saveData();
            }

        }


        if (isUpdateMode && jobSheetDetails !=null){
            tv_DocNo.setText(jobSheet.getDocNo());
            tv_itemCode.setText(jobSheetDetails.getItemCode());
            tv_description.setText(jobSheetDetails.getItemDescription());
            uom_text.setText(jobSheetDetails.getUOM());
            tv_batchNo.setText(jobSheetDetails.getBatchNo());
            editText_remarks.setText(jobSheetDetails.getRemarks());
            editText_remarks2.setText(jobSheetDetails.getRemarks2());
            editText_qty.setText(String.valueOf(jobSheetDetails.getQuantity()));
            editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
            editText_discount.setText(String.valueOf(jobSheetDetails.getDiscount()));
            tv_subTotal.setText(String.format("%.2f", jobSheetDetails.getSubTotal()));
            tv_taxType.setText(jobSheetDetails.getTaxType());
            tv_taxRate.setText(String.format("%.2f", jobSheetDetails.getTaxRate()));
            tv_taxValue.setText(String.format("%.2f", jobSheetDetails.getTaxValue()));
            tv_totalEx.setText(String.format("%.2f", jobSheetDetails.getTotal_Ex()));
            tv_totalIn.setText(String.format("%.2f", jobSheetDetails.getTotal_In()));
        }

        //docNo
        tv_DocNo.setText(jobSheet.getDocNo());

        //when scanner is used
        tv_itemCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    Log.d("Check123", "Runnig here 2");
                    String remove = removeNonAlphanumeric(s.toString().trim());
                    String myBarcode = remove;

                    jobSheetDetails = new AC_Class.JobSheetDetails();
                    jobSheetDetails.setLocation(default_location);

                    if (!myBarcode.equals("") && !TextUtils.isEmpty(tv_itemCode.getText())){
                        boolean isSkip = false;

                        if (isCustomBarcodeEnabled){
                            String normalBarcode = tv_itemCode.getText().toString();
                            results = db.getItemBC(normalBarcode);
                            if (results.getCount() == 0){
                                if (myBarcode.length() == barcodeLength) {
                                    if (isNumeric(myBarcode)) {
                                        myQty = Double.valueOf(myBarcode.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                        if(dtlRemarkStart!=0 && dtlRemarkLength!=0) {
                                            barcodeDtlRemark = myBarcode.substring(dtlRemarkStart - 1, dtlRemarkStart - 1 + dtlRemarkLength);
                                            editText_remarks.setText(barcodeDtlRemark);
                                            jobSheetDetails.setRemarks(barcodeDtlRemark);
                                        }
                                        if(dtlRemark2Start!=0 && dtlRemark2Length!=0) {
                                            barcodeDtlRemark2 = myBarcode.substring(dtlRemark2Start - 1, dtlRemark2Start - 1 + dtlRemark2Length);
                                            editText_remarks2.setText(barcodeDtlRemark2);
                                            jobSheetDetails.setRemarks2(barcodeDtlRemark2);
                                        }
                                        if (qtyDecimal > 0) {
                                            myQty = myQty / Math.pow(10, qtyDecimal);
                                        }
                                        myBarcode = myBarcode.substring(itemStart - 1, itemStart - 1 + itemLength);
                                        tv_itemCode.setText(myBarcode);
                                    } else {
                                        Toast.makeText(Jobsheet_AddItemDetails.this, "Invalid Barcode.",
                                                Toast.LENGTH_SHORT).show();
                                        isSkip = true;
                                    }
                                }
                            }else {
                                //productFound();
                            }


                        }

                        if (!isSkip){
                            Cursor results = db.getBarcodeItemDetail(myBarcode);
                            if (results.getCount() == 0) {
                                tv_itemCode.setText("");
                                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                                AlertDialog alertDialog = new AlertDialog.Builder(Jobsheet_AddItemDetails.this).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Product not found.");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                //set field to default value
                                                tv_itemCode.setText("");
                                                tv_description.setText("");
                                                uom_text.setText("");
                                                tv_stock.setText("");
                                                editText_remarks.setText("");
                                                editText_remarks2.setText("");
                                                editText_unitPrice.setText("0.00");
                                                editText_discount.setText("0.00");
                                                tv_subTotal.setText("0.00");
                                                tv_taxType.setText("");
                                                tv_taxRate.setText("");
                                                tv_taxValue.setText("0.00");
                                                tv_totalEx.setText("0.00");
                                                tv_totalIn.setText("0.00");
                                                tv_batchNo.setText("");
                                                lbl_batch_no.setVisibility(View.GONE);
                                                tv_batchNo.setVisibility(View.GONE);

                                            }
                                        });
                                alertDialog.show();
                            } else {
                                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                                results.moveToNext();

                                jobSheetDetails.setDocDate(jobSheet.getDocDate());
                                jobSheetDetails.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                                jobSheetDetails.setItemDescription(results.getString(results.getColumnIndex("Description")));
                                tv_description.setText(jobSheetDetails.getItemDescription());
                                jobSheetDetails.setUOM(results.getString(results.getColumnIndex("BaseUOM")));
                                uom_text.setText(jobSheetDetails.getUOM());
                                jobSheetDetails.setUPrice(results.getDouble(results.getColumnIndex("Price")));
                                editText_unitPrice.setText(String.format("%.2f", jobSheetDetails.getUPrice()));

                                if (isAutoPrice){
                                    Cursor cursor_pc = db.getPriceCategory(jobSheet.getDebtorCode());
                                    if (cursor_pc != null) {
                                        if (cursor_pc.moveToFirst()) {
                                            Object myPCObj = cursor_pc.getString(0);

                                            if (myPCObj != null) {
                                                try {
                                                    Integer myPC = 0;
                                                    myPC = Integer.parseInt(myPCObj.toString());

                                                    switch (myPC) {
                                                        case 2:
                                                            jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price2"))));
                                                            editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                            break;
                                                        case 3:
                                                            jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price3"))));
                                                            editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                            break;
                                                        case 4:
                                                            jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price4"))));
                                                            editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                            break;
                                                        case 5:
                                                            jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price5"))));
                                                            editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                            break;
                                                        case 6:
                                                            jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price6"))));
                                                            editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                            break;
                                                    }
                                                } catch (NumberFormatException e) {
                                                    throw e;
                                                }
                                            }
                                        }
                                    }
                                }

                                String hasBatch = results.getString(results.getColumnIndex("HasBatchNo"));


                                if (isBatchNoEnabled && hasBatch.equals("true")){
                                    hasBatchNoItem = true;
                                    lbl_batch_no.setVisibility(View.VISIBLE);
                                    tv_batchNo.setVisibility(View.VISIBLE);
                                    batchNo_btn.setVisibility(View.VISIBLE);
                                    if (isSaleBatchEnabled){
                                        if (salesOrderType != "0" || salesOrderType != null){
                                            if (salesOrderType.equals("Latest Manufacture Date")){
                                                Cursor data1 = db.getLateManuBatch(item.getItemCode(),
                                                        jobSheetDetails.getUOM(), jobSheetDetails.getLocation());
                                                if (data1.moveToFirst()){
                                                    jobSheetDetails.setBatchNo(data1.getString(data1.getColumnIndex("BatchNo")));
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                } else {
                                                    jobSheetDetails.setBatchNo(null);
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                }
                                            } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                                                Cursor data6 = db.getEarManuBatch(item.getItemCode(),
                                                        item.getUOM(), jobSheetDetails.getLocation());
                                                if (data6.moveToFirst()){
                                                    jobSheetDetails.setBatchNo(data6.getString(data6.getColumnIndex("BatchNo")));
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                } else {
                                                    jobSheetDetails.setBatchNo(null);
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                }
                                            } else if (salesOrderType.equals("Latest Expiration Date")) {
                                                Cursor data3 = db.getLateExpBatch(item.getItemCode(),
                                                        item.getUOM(), jobSheetDetails.getLocation());

                                                if (data3.moveToFirst()) {
                                                    jobSheetDetails.setBatchNo(data3.getString(data3.getColumnIndex("BatchNo")));
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                } else {
                                                    jobSheetDetails.setBatchNo(null);
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                }
                                            } else if (salesOrderType.equals("Earliest Expiration Date")) {
                                                Cursor data4 = db.getEarExpBatch(item.getItemCode(),
                                                        item.getUOM(), jobSheetDetails.getLocation());

                                                if (data4.moveToFirst()) {
                                                    jobSheetDetails.setBatchNo(data4.getString(data4.getColumnIndex("BatchNo")));
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                } else {
                                                    jobSheetDetails.setBatchNo(null);
                                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                                }
                                            }
                                        } else {
                                            Cursor data5 = db.getEarManuBatch(item.getItemCode(),
                                                    item.getUOM(), jobSheetDetails.getLocation());
                                            if (data5.moveToFirst()) {
                                                jobSheetDetails.setBatchNo(data5.getString(data2.getColumnIndex("BatchNo")));
                                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                            } else {
                                                jobSheetDetails.setBatchNo(null);
                                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                            }
                                            jobSheetDetails.setBatchNo(data5.getString(data5.getColumnIndex("BatchNo")));
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        }

                                        if (jobSheetDetails.getBatchNo() != null){
                                            Cursor temp2;
                                            temp2 = db.getStockBalanceBatchNo(jobSheetDetails.getItemCode(),
                                                    jobSheetDetails.getUOM(), jobSheetDetails.getBatchNo());
                                            if (temp2.getCount() > 0){
                                                temp2.moveToNext();
                                                balanceQty = temp2.getFloat(0);
                                            }
                                            tv_stock.setText("Remaining Balance: " + nf.format(balanceQty));
                                        } else {
                                            tv_stock.setText("Remaining Balance: 0" );
                                        }
                                    } else {
                                        Intent intent = new Intent(Jobsheet_AddItemDetails.this, ItemBatchList.class);
                                        intent.putExtra("ItemCode", jobSheetDetails.getItemCode());
                                        intent.putExtra("UOM", jobSheetDetails.getUOM());
                                        intent.putExtra("Location", jobSheetDetails.getLocation());
                                        startActivityForResult(intent, 9);
                                    }
                                } else {
                                    hasBatchNoItem = false;
                                    lbl_batch_no.setVisibility(View.GONE);
                                    tv_batchNo.setVisibility(View.GONE);
                                    batchNo_btn.setVisibility(View.GONE);
                                    jobSheetDetails.setBatchNo(null);
                                }

                                jobSheetDetails.setQuantity(myQty);
                                editText_qty.setText(String.valueOf(myQty));
                                jobSheetDetails.setTaxType(getTax(results.getString(results.getColumnIndex("TaxType"))));
                                tv_taxType.setText(jobSheetDetails.getTaxType());
                                Cursor data2 = db.getTaxTypeValue(jobSheetDetails.getTaxType());
                                if (data2.moveToNext()) {
                                    jobSheetDetails.setTaxRate(data2.getDouble(0));
                                    tv_taxRate.setText(String.valueOf(jobSheetDetails.getTaxRate()));
                                }

                                jobSheetDetails.setSubTotal(jobSheetDetails.getQuantity() * jobSheetDetails.getUPrice());

                                Calculation();


                            }
                        }

                    } else {
                        Toast.makeText(Jobsheet_AddItemDetails.this, "No product", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });



        barcode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qrScan = new IntentIntegrator(Jobsheet_AddItemDetails.this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(true);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

                    //set fields value to default

                } catch (Exception e) {
                    Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage());
                }
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateMode){
                    Intent intent = new Intent(Jobsheet_AddItemDetails.this, Jobsheet_AddItemManual.class);
                    intent.putExtra("isUpdateMode", isUpdateMode);
                    intent.putExtra("subString", "");
                    startActivityForResult(intent, REQUEST_CODE_UPDATE_ITEM2);
                } else {
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }


            }
        });

        uom_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobSheetDetails.getItemCode() != null) {
                    Intent new_intent = new Intent(Jobsheet_AddItemDetails.this, ItemUOMList.class);
                    new_intent.putExtra("ItemKey", jobSheetDetails.getItemCode());
                    startActivityForResult(new_intent, 6);
                }
            }
        });

        editText_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheetDetails.setRemarks(editText_remarks.getText().toString());
            }
        });

        editText_remarks2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheetDetails.setRemarks2(editText_remarks2.getText().toString());
            }
        });

        batchNo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobSheetDetails.getItemCode() != null) {
                    Intent new_intent = new Intent(Jobsheet_AddItemDetails.this, ItemBatchList.class);
                    new_intent.putExtra("ItemCode", jobSheetDetails.getItemCode());
                    new_intent.putExtra("UOM", jobSheetDetails.getUOM());
                    new_intent.putExtra("Location", jobSheetDetails.getLocation());
                    startActivityForResult(new_intent, 9);
                }
            }
        });

        btn_Dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_qty.getText().equals("1")){
                    Toast.makeText(Jobsheet_AddItemDetails.this, "Quantity must be more than 1", Toast.LENGTH_SHORT).show();
                } else {
                    jobSheetDetails.setQuantity(jobSheetDetails.getQuantity() - 1);
                    editText_qty.setText(String.format("%.0f", jobSheetDetails.getQuantity()));
                    Calculation();
                }
            }
        });

        btn_Inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobSheetDetails.setQuantity(jobSheetDetails.getQuantity() + 1);
                editText_qty.setText(String.format("%.0f", jobSheetDetails.getQuantity()));
                Calculation();
            }
        });

        editText_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String qty = editText_qty.getText().toString();
                if (qty.length() > 0 && !qty.equals("-")){
                    jobSheetDetails.setQuantity(Double.valueOf(qty));
                    Calculation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_unitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    jobSheetDetails.setUPrice(0.00d);
                    return;
                }
                editText_unitPrice.removeTextChangedListener(this);
                String text = s.toString();
                String result = "";
                text = text.replaceAll("[^\\d]", "");

                try {
                    text = format(text);
                    result = text.replace(",", "");
                    jobSheetDetails.setUPrice(Double.parseDouble(result));
                    Calculation();
                } catch (NumberFormatException e) {
                    Log.e(getClass().getCanonicalName(), e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(getClass().getCanonicalName(), e.getMessage());
                }

                editText_unitPrice.setText(result);
                editText_unitPrice.setSelection(result.length());
                editText_unitPrice.addTextChangedListener(this);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobSheetDetails.getItemCode() != null) {
                    Intent new_intent = new Intent(Jobsheet_AddItemDetails.this, Check_List.class);
                    new_intent.putExtra("ItemCode", jobSheetDetails.getItemCode());
                    new_intent.putExtra("ItemUOMKey", jobSheetDetails.getUOM());
                    startActivityForResult(new_intent, 7);
                }
            }
        });

        histList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobSheetDetails.getItemCode() != null) {
                    Intent new_intent = new Intent(Jobsheet_AddItemDetails.this, HistoryPrice.class);
                    new_intent.putExtra("DebtorKey", jobSheet.getDebtorCode());
                    new_intent.putExtra("ItemKey", jobSheetDetails.getItemCode());
                    new_intent.putExtra("ItemUOMKey", jobSheetDetails.getUOM());
                    new_intent.putExtra("Agent", Agent);
                    new_intent.putExtra("FilterByAgent", FilterByAgent);
                    startActivityForResult(new_intent, 10);
                }
            }
        });

        tv_taxType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(Jobsheet_AddItemDetails.this, TaxType_List.class);
                startActivityForResult(new_intent, 5);
            }
        });

        discountList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobSheetDetails.getItemCode() != null) {
                    Intent new_intent = new Intent(Jobsheet_AddItemDetails.this, Discount_List.class);
                    new_intent.putExtra("ItemCode", jobSheetDetails.getItemCode());
                    startActivityForResult(new_intent, 8);
                }
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemDetails();
            }
        });

    }

    private void getbatchNo() {
        Cursor data2 = db.getAllItemBatch(jobSheetDetails.getItemCode(), jobSheetDetails.getUOM(), default_location);
        Cursor data3 = db.getAllItemBatch(jobSheetDetails.getItemCode(), jobSheetDetails.getUOM(), default_location);
        int newManudate = 0;
        int newExpirydate = 0;
        if (data3.moveToFirst()) {
            if(data3.getString(data3.getColumnIndex("ManufacturedDate"))!= null) {
                newManudate = Integer.parseInt(data3.getString(data3.getColumnIndex("ManufacturedDate")).replaceAll("[^0-9]", ""));
            }
            if(data3.getString(data3.getColumnIndex("ExpiryDate")) != null) {
                newExpirydate = Integer.parseInt(data3.getString(data3.getColumnIndex("ExpiryDate")).replaceAll("[^0-9]", ""));
            }
        }
        while (data2.moveToNext()) {
            String s_manudate = data2.getString(data2.getColumnIndex("ManufacturedDate"));
            int manudate = 0, expidate = 0;
            if(s_manudate != null) {
                manudate = Integer.parseInt(s_manudate.replaceAll("[^0-9]", ""));
            }

            String s_expidate = data2.getString(data2.getColumnIndex("ExpiryDate"));
            if(s_expidate !=null) {
                expidate = Integer.parseInt(s_expidate.replaceAll("[^0-9]", ""));
            }

            if (salesOrderType != "0" || salesOrderType != null) {
                if (salesOrderType.equals("Latest Manufacture Date")) {
                    int compare = Integer.compare(manudate, newManudate);
                    if (compare >= 0) {
                        newManudate = manudate;
                        jobSheetDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                    int compare = Integer.compare(manudate, newManudate);
                    if (compare <= 0) {
                        newManudate = manudate;
                        jobSheetDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Latest Expiration Date")) {
                    int compare = Integer.compare(expidate, newExpirydate);
                    if (compare >= 0) {
                        newExpirydate = expidate;
                        jobSheetDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Earliest Expiration Date")) {
                    int compare = Integer.compare(expidate, newExpirydate);
                    if (compare <= 0) {
                        newExpirydate = expidate;
                        jobSheetDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                }
            } else {
                jobSheetDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
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

    private String format(String text) throws NumberFormatException, NullPointerException {
        DecimalFormat numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(Double.parseDouble(text) / Math.pow(10, 2));
    }

    //done
    private void getItem() {
        if (item != null){
            tv_itemCode.setText(item.getItemCode());
            tv_description.setText(item.getDescription());
            uom_text.setText(item.getUOM());
            //editText_discount.setText("0.00");
            editText_unitPrice.setText(String.format("%.2f", item.getPrice()));

            jobSheetDetails.setDocNo(jobSheet.getDocNo());
            jobSheetDetails.setDocDate(jobSheet.getDocDate());
            jobSheetDetails.setItemCode(item.getItemCode());
            jobSheetDetails.setItemDescription(item.getDescription());
            jobSheetDetails.setUOM(item.getUOM());
            jobSheetDetails.setUPrice(Double.valueOf(item.getPrice()));

            jobSheetDetails.setTaxType(getTax(item.getTaxType()));
            tv_taxType.setText(jobSheetDetails.getTaxType());

            Cursor data2 = db.getTaxTypeValue(jobSheetDetails.getTaxType());
            while (data2.moveToNext()) {
                jobSheetDetails.setTaxRate(data2.getDouble(0));
                tv_taxRate.setText(String.valueOf(jobSheetDetails.getTaxRate()));
            }

            //stock balance
            Cursor temp;
            temp = db.getStockBalance(jobSheetDetails.getItemCode(),jobSheetDetails.getUOM());
            if (temp.getCount() > 0){
                temp.moveToNext();
                balanceQty = temp.getFloat(0);
            }
            tv_stock.setText("Remaining Balance: " + nf.format(balanceQty));

            if (isAutoPrice){
                Cursor cursor_pc = db.getPriceCategory(jobSheet.getDebtorCode());
                if (cursor_pc != null){
                    if (cursor_pc.moveToFirst()){
                        Object myPCObj = cursor_pc.getString(0);

                        if (myPCObj != null) {
                            try {
                                Integer myPC = 0;
                                myPC = Integer.parseInt(myPCObj.toString());
                                switch (myPC) {
                                    case 2:
                                        jobSheetDetails.setUPrice(Double.valueOf(item.getPrice2()));
                                        editText_unitPrice.setText(String.format("%.2f", item.getPrice2()));
                                        break;
                                    case 3:
                                        jobSheetDetails.setUPrice(Double.valueOf(item.getPrice3()));
                                        editText_unitPrice.setText(String.format("%.2f", item.getPrice3()));
                                        break;
                                    case 4:
                                        jobSheetDetails.setUPrice(Double.valueOf(item.getPrice4()));
                                        editText_unitPrice.setText(String.format("%.2f", item.getPrice4()));
                                        break;
                                    case 5:
                                        jobSheetDetails.setUPrice(Double.valueOf(item.getPrice5()));
                                        editText_unitPrice.setText(String.format("%.2f", item.getPrice5()));
                                        break;
                                    case 6:
                                        jobSheetDetails.setUPrice(Double.valueOf(item.getPrice6()));
                                        editText_unitPrice.setText(String.format("%.2f", item.getPrice6()));
                                        break;
                                }
                            } catch (NumberFormatException e) {
                                throw e;
                            }
                        }
                    }
                }

            }

            if (isBatchNoEnabled && item.getHasBatchNo().equals("true")){
                hasBatchNoItem = true;
                lbl_batch_no.setVisibility(View.VISIBLE);
                tv_batchNo.setVisibility(View.VISIBLE);
                batchNo_btn.setVisibility(View.VISIBLE);
                if (isSaleBatchEnabled){
                    if (salesOrderType != "0" || salesOrderType != null){
                        if (salesOrderType.equals("Latest Manufacture Date")){
                            Cursor data1 = db.getLateManuBatch(item.getItemCode(),
                                    jobSheetDetails.getUOM(), jobSheetDetails.getLocation());
                            if (data1.moveToFirst()){
                                jobSheetDetails.setBatchNo(data1.getString(data1.getColumnIndex("BatchNo")));
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            } else {
                                jobSheetDetails.setBatchNo(null);
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            }
                        } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                            Cursor data6 = db.getEarManuBatch(item.getItemCode(),
                                    item.getUOM(), jobSheetDetails.getLocation());
                            if (data6.moveToFirst()){
                                jobSheetDetails.setBatchNo(data6.getString(data6.getColumnIndex("BatchNo")));
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            } else {
                                jobSheetDetails.setBatchNo(null);
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            }
                        } else if (salesOrderType.equals("Latest Expiration Date")) {
                            Cursor data3 = db.getLateExpBatch(item.getItemCode(),
                                    item.getUOM(), jobSheetDetails.getLocation());

                            if (data3.moveToFirst()) {
                                jobSheetDetails.setBatchNo(data3.getString(data3.getColumnIndex("BatchNo")));
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            } else {
                                jobSheetDetails.setBatchNo(null);
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            }
                        } else if (salesOrderType.equals("Earliest Expiration Date")) {
                            Cursor data4 = db.getEarExpBatch(item.getItemCode(),
                                    item.getUOM(), jobSheetDetails.getLocation());

                            if (data4.moveToFirst()) {
                                jobSheetDetails.setBatchNo(data4.getString(data4.getColumnIndex("BatchNo")));
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            } else {
                                jobSheetDetails.setBatchNo(null);
                                tv_batchNo.setText(jobSheetDetails.getBatchNo());
                            }
                        }
                    } else {
                        Cursor data5 = db.getEarManuBatch(item.getItemCode(),
                                item.getUOM(), jobSheetDetails.getLocation());
                        if (data5.moveToFirst()) {
                            jobSheetDetails.setBatchNo(data5.getString(data2.getColumnIndex("BatchNo")));
                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                        } else {
                            jobSheetDetails.setBatchNo(null);
                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                        }
                        jobSheetDetails.setBatchNo(data5.getString(data5.getColumnIndex("BatchNo")));
                        tv_batchNo.setText(jobSheetDetails.getBatchNo());
                    }

                    if (jobSheetDetails.getBatchNo() != null){
                        Cursor temp2;
                        temp2 = db.getStockBalanceBatchNo(jobSheetDetails.getItemCode(),
                                jobSheetDetails.getUOM(), jobSheetDetails.getBatchNo());
                        if (temp2.getCount() > 0){
                            temp2.moveToNext();
                            balanceQty = temp2.getFloat(0);
                        }
                        tv_stock.setText("Remaining Balance: " + nf.format(balanceQty));
                    }
                } else {
                    Intent intent = new Intent(this, ItemBatchList.class);
                    intent.putExtra("ItemCode", jobSheetDetails.getItemCode());
                    intent.putExtra("UOM", jobSheetDetails.getUOM());
                    intent.putExtra("Location", jobSheetDetails.getLocation());
                    startActivityForResult(intent, 9);
                }
            } else {
                hasBatchNoItem = false;
                lbl_batch_no.setVisibility(View.GONE);
                tv_batchNo.setVisibility(View.GONE);
                batchNo_btn.setVisibility(View.GONE);
                jobSheetDetails.setBatchNo(null);
            }

            Calculation();
        }
    }

    //done
    private void Calculation() {
        if (isTaxInclusive){
            //inclusive

            jobSheetDetails.setTotal_In(jobSheetDetails.getQuantity() * jobSheetDetails.getUPrice() - jobSheetDetails.getDiscount());
            tv_totalIn.setText(String.format("%.2f", jobSheetDetails.getTotal_In()));
            // Calc. tax from total
            jobSheetDetails.setTaxValue((jobSheetDetails.getTotal_In() *
                    jobSheetDetails.getTaxRate()) / (100 + jobSheetDetails.getTaxRate()));
            tv_taxValue.setText(String.format("%.2f", jobSheetDetails.getTaxValue()));
            jobSheetDetails.setTotal_Ex(jobSheetDetails.getTotal_In() - jobSheetDetails.getTaxValue());
            tv_totalEx.setText(String.format("%.2f", jobSheetDetails.getTotal_Ex()));
            jobSheetDetails.setSubTotal(jobSheetDetails.getTotal_Ex() - jobSheetDetails.getDiscount());
            tv_subTotal.setText(String.format("%.2f", jobSheetDetails.getSubTotal()));

        } else {
            //exclusive
            jobSheetDetails.setSubTotal(jobSheetDetails.getQuantity() * jobSheetDetails.getUPrice());
            tv_subTotal.setText(String.format("%.2f", jobSheetDetails.getSubTotal()));
            jobSheetDetails.setTotal_Ex(jobSheetDetails.getSubTotal() - jobSheetDetails.getDiscount());
            tv_totalEx.setText(String.format("%.2f", jobSheetDetails.getTotal_Ex()));
            jobSheetDetails.setTaxValue(jobSheetDetails.getTotal_Ex() * jobSheetDetails.getTaxRate() / 100);
            tv_taxValue.setText(String.format("%.2f", jobSheetDetails.getTaxValue()));
            jobSheetDetails.setTotal_In(jobSheetDetails.getTotal_Ex() + jobSheetDetails.getTaxValue());
            tv_totalIn.setText(String.format("%.2f", jobSheetDetails.getTotal_In()));
        }
    }

    //done
    private String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            String taxType = defaultTax;
            if (taxType != null) {
                return taxType;
            } else if (itemTaxType != null && !itemTaxType.isEmpty()) {
                return itemTaxType;
            } else {
                return defaultTax;
            }
        }
        return "None";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:
                if (resultCode == RESULT_OK && data != null){
                    AC_Class.Item item = data.getParcelableExtra("ItemDetails");
                    if (item != null){
                        tv_itemCode.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_itemCode.setText(item.getItemCode());
                            }
                        });

                    } else {
                        Log.d("Checking123", "Item return is null");
                    }
                }
                break;

            //Return from tax selection
            case 5:
                if (data != null) {
                    AC_Class.TaxType taxtype = data.getParcelableExtra("TaxTypesKey");
                    if (taxtype != null) {
                        jobSheetDetails.setTaxType(taxtype.getTaxType());
                        tv_taxType.setText(jobSheetDetails.getTaxType());
                        jobSheetDetails.setTaxRate(Double.valueOf(taxtype.getTaxRate()));
                        tv_taxRate.setText(String.format("%.2f", jobSheetDetails.getTaxRate()));
                        Calculation();
                    }
                }
                break;

            //Return from UOM selection
            case 6:
                if (data != null) {
                    AC_Class.ItemUOM itemUOM = data.getParcelableExtra("UOMKey");
                    if (itemUOM != null) {
                        jobSheetDetails.setUOM(itemUOM.getUOM());
                        jobSheetDetails.setUPrice(Double.valueOf(itemUOM.getPrice()));
                        uom_text.setText(jobSheetDetails.getUOM());
                        editText_unitPrice.setText(String.format("%.2f", jobSheetDetails.getUPrice()));
                        Calculation();
                    }
                }
                break;

            //check price
            case 7:
                if (data != null) {
                    AC_Class.SellingPrice sellingPrice = data.getParcelableExtra("price");
                    if (sellingPrice != null) {
                        if (sellingPrice.getPrice() != 0) {
                            jobSheetDetails.setUPrice(Double.valueOf(sellingPrice.getPrice()));
                            editText_unitPrice.setText(String.format("%.2f", jobSheetDetails.getUPrice()));
                            Calculation();
                        }
                    }
                }
                break;

            //discount
            case 8:
                if (data != null) {
                    dis = data.getDoubleExtra("Discount", 0.00);

                    if (dis != null && dis != 0.00) {

                        isPercentage = data.getStringExtra("IsPercentage");
                        isChecked = data.getStringExtra("IsChecked");

                        if (isPercentage.equals("True")) {
                            dis = dis / 100;
                            disNumber = jobSheetDetails.getUPrice() * jobSheetDetails.getQuantity() * dis;
                        } else {
                            if (isChecked.equals("True")) {
                                disNumber = dis * jobSheetDetails.getQuantity();

                            } else {
                                disNumber = dis;
                            }
                        }

                        if (disNumber <= jobSheetDetails.getSubTotal()) {
                            editText_discount.setText(String.format("%.2f", disNumber));
                            jobSheetDetails.setDiscount(disNumber);
                            Calculation();
                        } else {
                            editText_discount.setError("Discount cannot exceed subtotal");
                        }

                    }

                }
                break;

            case 9:
                if (data != null){
                    String batchNo = data.getStringExtra("ItemBatchNo");
                    if (batchNo != null){
                        jobSheetDetails.setBatchNo(batchNo);
                        tv_batchNo.setText(jobSheetDetails.getBatchNo());
                        if (isBatchNoEnabled){
                            if (jobSheetDetails.getBatchNo() != null){
                                Cursor temp2;
                                temp2 = db.getStockBalanceBatchNo(jobSheetDetails.getItemCode(),
                                        jobSheetDetails.getUOM(), jobSheetDetails.getBatchNo());
                                if (temp2.getCount() > 0){
                                    temp2.moveToNext();
                                    balanceQty = temp2.getFloat(0);
                                }
                                tv_stock.setText("Remaining Balance: " + nf.format(balanceQty));
                            }
                        }
                    }
                }
                break;

            //hist list
            case 10:
                if(data != null) {
                    Float price = data.getFloatExtra("Price", 0.0f);
                    if (price != null) {
                        jobSheetDetails.setUPrice(Double.valueOf(price));
                        editText_unitPrice.setText(String.format("%.2f", jobSheetDetails.getUPrice()));
                    }

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
                        String itemCode = result.getContents();
                        //loadScannedItem(itemCode);
                        if (itemCode != null){
                            //tv_DocNo.setText(jobSheet.getDocNo());
                            tv_itemCode.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_itemCode.setText(itemCode);
                                    Log.d("Check123", "Runnig here 1");
                                }
                            });

                        }
                        //tv_itemCode.setText(result.getContents());
                        editText_remarks.setText("");
                        editText_remarks2.setText("");
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;

        }
    }

    private void loadScannedItem(String itemCode) {
        if (itemCode.length() > 0){
            String remove = removeNonAlphanumeric(itemCode.toString().trim());
            String myBarcode = remove;

            jobSheetDetails = new AC_Class.JobSheetDetails();
            jobSheetDetails.setLocation(default_location);
            tv_itemCode.setText(myBarcode);

            if (!myBarcode.equals("") && !TextUtils.isEmpty(tv_itemCode.getText())){
                boolean isSkip = false;

                if (isCustomBarcodeEnabled){
                    String normalBarcode = tv_itemCode.getText().toString();
                    results = db.getItemBC(normalBarcode);
                    if (results.getCount() == 0){
                        if (myBarcode.length() == barcodeLength) {
                            if (isNumeric(myBarcode)) {
                                myQty = Double.valueOf(myBarcode.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                if(dtlRemarkStart!=0 && dtlRemarkLength!=0) {
                                    barcodeDtlRemark = myBarcode.substring(dtlRemarkStart - 1, dtlRemarkStart - 1 + dtlRemarkLength);
                                    editText_remarks.setText(barcodeDtlRemark);
                                    jobSheetDetails.setRemarks(barcodeDtlRemark);
                                }
                                if(dtlRemark2Start!=0 && dtlRemark2Length!=0) {
                                    barcodeDtlRemark2 = myBarcode.substring(dtlRemark2Start - 1, dtlRemark2Start - 1 + dtlRemark2Length);
                                    editText_remarks2.setText(barcodeDtlRemark2);
                                    jobSheetDetails.setRemarks2(barcodeDtlRemark2);
                                }
                                if (qtyDecimal > 0) {
                                    myQty = myQty / Math.pow(10, qtyDecimal);
                                }
                                myBarcode = myBarcode.substring(itemStart - 1, itemStart - 1 + itemLength);
                                jobSheetDetails.setItemCode(myBarcode);
                                tv_itemCode.setText(myBarcode);
                            } else {
                                Toast.makeText(Jobsheet_AddItemDetails.this, "Invalid Barcode.",
                                        Toast.LENGTH_SHORT).show();
                                isSkip = true;
                            }
                        }
                    }else {
                        //productFound();
                    }


                }

                if (!isSkip){
                    Cursor results = db.getBarcodeItemDetail(myBarcode);
                    if (results.getCount() == 0) {
                        tv_itemCode.setText("");
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                        AlertDialog alertDialog = new AlertDialog.Builder(Jobsheet_AddItemDetails.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Product not found.");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //set field to default value
                                        tv_itemCode.setText("");
                                    }
                                });
                        alertDialog.show();
                    } else {
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                        results.moveToNext();

                        jobSheetDetails.setDocDate(jobSheet.getDocDate());
                        jobSheetDetails.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                        jobSheetDetails.setItemDescription(results.getString(results.getColumnIndex("Description")));
                        tv_description.setText(jobSheetDetails.getItemDescription());
                        jobSheetDetails.setUOM(results.getString(results.getColumnIndex("BaseUOM")));
                        uom_text.setText(jobSheetDetails.getUOM());
                        jobSheetDetails.setUPrice(results.getDouble(results.getColumnIndex("Price")));
                        editText_unitPrice.setText(String.format("%.2f", jobSheetDetails.getUPrice()));

                        if (isAutoPrice){
                            Cursor cursor_pc = db.getPriceCategory(jobSheet.getDebtorCode());
                            if (cursor_pc != null) {
                                if (cursor_pc.moveToFirst()) {
                                    Object myPCObj = cursor_pc.getString(0);

                                    if (myPCObj != null) {
                                        try {
                                            Integer myPC = 0;
                                            myPC = Integer.parseInt(myPCObj.toString());

                                            switch (myPC) {
                                                case 2:
                                                    jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price2"))));
                                                    editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                    break;
                                                case 3:
                                                    jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price3"))));
                                                    editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                    break;
                                                case 4:
                                                    jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price4"))));
                                                    editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                    break;
                                                case 5:
                                                    jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price5"))));
                                                    editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                    break;
                                                case 6:
                                                    jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price6"))));
                                                    editText_unitPrice.setText(String.valueOf(jobSheetDetails.getUPrice()));
                                                    break;
                                            }
                                        } catch (NumberFormatException e) {
                                            throw e;
                                        }
                                    }
                                }
                            }
                        }

                        String hasBatch = results.getString(results.getColumnIndex("HasBatchNo"));

                        if (isBatchNoEnabled && hasBatch.equals("true")){
                            hasBatchNoItem = true;
                            lbl_batch_no.setVisibility(View.VISIBLE);
                            tv_batchNo.setVisibility(View.VISIBLE);
                            batchNo_btn.setVisibility(View.VISIBLE);
                            if (isSaleBatchEnabled){
                                if (salesOrderType != "0" || salesOrderType != null){
                                    if (salesOrderType.equals("Latest Manufacture Date")){
                                        Cursor data1 = db.getLateManuBatch(item.getItemCode(),
                                                jobSheetDetails.getUOM(), jobSheetDetails.getLocation());
                                        if (data1.moveToFirst()){
                                            jobSheetDetails.setBatchNo(data1.getString(data1.getColumnIndex("BatchNo")));
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        } else {
                                            jobSheetDetails.setBatchNo(null);
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        }
                                    } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                                        Cursor data6 = db.getEarManuBatch(item.getItemCode(),
                                                item.getUOM(), jobSheetDetails.getLocation());
                                        if (data6.moveToFirst()){
                                            jobSheetDetails.setBatchNo(data6.getString(data6.getColumnIndex("BatchNo")));
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        } else {
                                            jobSheetDetails.setBatchNo(null);
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        }
                                    } else if (salesOrderType.equals("Latest Expiration Date")) {
                                        Cursor data3 = db.getLateExpBatch(item.getItemCode(),
                                                item.getUOM(), jobSheetDetails.getLocation());

                                        if (data3.moveToFirst()) {
                                            jobSheetDetails.setBatchNo(data3.getString(data3.getColumnIndex("BatchNo")));
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        } else {
                                            jobSheetDetails.setBatchNo(null);
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        }
                                    } else if (salesOrderType.equals("Earliest Expiration Date")) {
                                        Cursor data4 = db.getEarExpBatch(item.getItemCode(),
                                                item.getUOM(), jobSheetDetails.getLocation());

                                        if (data4.moveToFirst()) {
                                            jobSheetDetails.setBatchNo(data4.getString(data4.getColumnIndex("BatchNo")));
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        } else {
                                            jobSheetDetails.setBatchNo(null);
                                            tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                        }
                                    }
                                } else {
                                    Cursor data5 = db.getEarManuBatch(item.getItemCode(),
                                            item.getUOM(), jobSheetDetails.getLocation());
                                    if (data5.moveToFirst()) {
                                        jobSheetDetails.setBatchNo(data5.getString(data5.getColumnIndex("BatchNo")));
                                        tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                    } else {
                                        jobSheetDetails.setBatchNo(null);
                                        tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                    }
                                    jobSheetDetails.setBatchNo(data5.getString(data5.getColumnIndex("BatchNo")));
                                    tv_batchNo.setText(jobSheetDetails.getBatchNo());
                                }

                                if (jobSheetDetails.getBatchNo() != null){
                                    Cursor temp2;
                                    temp2 = db.getStockBalanceBatchNo(jobSheetDetails.getItemCode(),
                                            jobSheetDetails.getUOM(), jobSheetDetails.getBatchNo());
                                    if (temp2.getCount() > 0){
                                        temp2.moveToNext();
                                        balanceQty = temp2.getFloat(0);
                                    }
                                    tv_stock.setText("Remaining Balance: " + nf.format(balanceQty));
                                }
                            } else {
                                Intent intent = new Intent(Jobsheet_AddItemDetails.this, ItemBatchList.class);
                                intent.putExtra("ItemCode", jobSheetDetails.getItemCode());
                                intent.putExtra("UOM", jobSheetDetails.getUOM());
                                intent.putExtra("Location", jobSheetDetails.getLocation());
                                startActivityForResult(intent, 9);
                            }
                        } else {
                            hasBatchNoItem = false;
                            lbl_batch_no.setVisibility(View.GONE);
                            tv_batchNo.setVisibility(View.GONE);
                            batchNo_btn.setVisibility(View.GONE);
                            jobSheetDetails.setBatchNo(null);
                        }

                        jobSheetDetails.setQuantity(myQty);
                        editText_qty.setText(String.valueOf(myQty));
                        jobSheetDetails.setTaxType(getTax(results.getString(results.getColumnIndex("TaxType"))));
                        tv_taxType.setText(jobSheetDetails.getTaxType());
                        Cursor data2 = db.getTaxTypeValue(jobSheetDetails.getTaxType());
                        if (data2.moveToNext()) {
                            jobSheetDetails.setTaxRate(data2.getDouble(0));
                            tv_taxRate.setText(String.valueOf(jobSheetDetails.getTaxRate()));
                        }

                        jobSheetDetails.setSubTotal(jobSheetDetails.getQuantity() * jobSheetDetails.getUPrice());

                        Calculation();
                    }
                }

            }

        }
    }

    private void saveItemDetails() {
        if (CheckEmpty()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please make sure that all the details have been filled.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (CheckPrice()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please make sure unit price is more than minimum selling " + "price.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (jobSheetDetails.getQuantity() > balanceQty) {
            if (NegativeInventory.equals(true)) {
                if (isBatchNoEnabled && hasBatchNoItem) {
                    String batch = tv_batchNo.getText().toString();
                    if (batch == null || batch.equals(""))
                        Toast.makeText(this, "Please fill in batch no", Toast.LENGTH_SHORT).show();
                    else
                        saveData();
                } else {
                    saveData();
                }
            } else {
                Toast.makeText(this, "Out of Stock", Toast.LENGTH_SHORT).show();
            }
        } else {
            saveData();
        }
    }

    private void saveData() {
        if (jobSheetDetails.getDiscount() == null){
            jobSheetDetails.setDiscount(0.00);
        }

        if (jobSheetDetails.getRemarks() == null){
            jobSheetDetails.setRemarks("");
        }

        if (jobSheetDetails.getRemarks2() == null){
            jobSheetDetails.setRemarks2("");
        }

        if (isUpdateMode){
            Intent intent = new Intent();
            intent.putExtra("JobSheet Details", jobSheetDetails);
            intent.putExtra("nPosition", position);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            //first select item
            Intent newIntent = new Intent();
            newIntent.putExtra("JobSheet Details", jobSheetDetails);
            setResult(RESULT_OK, newIntent);
            finish();
        }

        barcodeDtlRemark = "";
        barcodeDtlRemark2 = "";
        myQty = 1;

    }

    private boolean CheckEmpty() {
        return (jobSheetDetails.getLocation() == null ||
                jobSheetDetails.getItemCode() == null ||
                jobSheetDetails.getItemDescription() == null ||
                jobSheetDetails.getUOM() == null ||
                jobSheetDetails.getUPrice() == null ||
                jobSheetDetails.getSubTotal() == null ||
                //jobSheetDetails.getTaxType() == null ||
                jobSheetDetails.getTaxRate() == null ||
                jobSheetDetails.getTaxValue() == null ||
                jobSheetDetails.getTotal_Ex() == null ||
                jobSheetDetails.getTotal_In() == null ||
                jobSheetDetails.getQuantity() <= 0);
    }

    public boolean CheckPrice() {
        Double price = null;
        Double price1 = null;
        Cursor data = db.getItem(jobSheetDetails.ItemCode, jobSheetDetails.UOM);
        while (data.moveToNext()) {
            price = data.getDouble(19);
        }

        return (jobSheetDetails.getUPrice() < price);
    }

    @Override
    public void onBackPressed() {
        if (isUpdateMode){
            Intent intent = new Intent();
            intent.putExtra("JobSheet Details", jobSheetDetails);
            intent.putExtra("nPosition", position);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
        }
        return false;
    }
}