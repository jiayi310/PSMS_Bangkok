package com.example.androidmobilestock_bangkok;

import static com.example.androidmobilestock_bangkok.StockInquiry.removeNonAlphanumeric;

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
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockReceive_AddNewItemDetail extends AppCompatActivity {

    AC_Class.Item item;
    TextView invdtl_item_textView, invdtl_item_detail_txt, uom_text, invdtl_batchno_txt, invdtl_remark_txt, invdtl_remark_txt2, si_item_camera, search_btn,invdtl_DocNo, lbl_batch_no, invdtl_subtotal_txt;
    EditText editTextNumber2, invdtl_unitprice_txt;
    ImageButton btn_Dec, btn_Inc;
    Button btncancel, btnsave;
    String func;
    AC_Class.StockReceiveDetails stockReceiveDetails;
    IntentIntegrator qrScan;
    AC_Class.StockReceive stockReceive;
    Boolean isBatchNoEnabled = true;
    Boolean isSaleBatchEnabled = true;
    Boolean isPurBatchEnabled = true;
    String defaultBatchNo = "";
    Boolean hasBatchNoItem = false;
    String defaultLocation = "";
    ACDatabase db;

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
    Cursor results;
    double myQty = 1;
    String barcodeDtlRemark = "";
    String barcodeDtlRemark2 = "";

    //PUR_PurchaseDtl_AddManual.java
    int position;
    boolean isUpdateMode;
    String scannedData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_receive_add_new_item_detail);

        invdtl_item_textView = findViewById(R.id.invdtl_item_textView);
        invdtl_item_detail_txt = findViewById(R.id.invdtl_item_detail_txt);
        uom_text = findViewById(R.id.uom_text);
        lbl_batch_no = findViewById(R.id.lbl_batch_no);
        invdtl_batchno_txt = findViewById(R.id.invdtl_batchno_txt);
        invdtl_remark_txt = findViewById(R.id.invdtl_remark_txt);
        invdtl_remark_txt2 = findViewById(R.id.invdtl_remark_txt2);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        btn_Dec = findViewById(R.id.btn_Dec);
        btn_Inc = findViewById(R.id.btn_Inc);
        invdtl_unitprice_txt = findViewById(R.id.invdtl_unitprice_txt);
        invdtl_subtotal_txt = findViewById(R.id.invdtl_subtotal_txt);
        btncancel = findViewById(R.id.btncancel);
        btnsave = findViewById(R.id.btnsave);
        si_item_camera = findViewById(R.id.si_item_camera);
        search_btn= findViewById(R.id.search_btn);
        invdtl_DocNo = findViewById(R.id.invdtl_DocNo);

        db = new ACDatabase(this);


        stockReceive = getIntent().getParcelableExtra("stockReceive");
        item = getIntent().getParcelableExtra("ItemDetails");
        func = getIntent().getStringExtra("iFunc");
        //update
        stockReceiveDetails = getIntent().getParcelableExtra("nItem");
        position = getIntent().getIntExtra("nPosition", -1);
        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);
        scannedData = getIntent().getStringExtra("scannedData");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (isUpdateMode){
            actionBar.setTitle("Edit Item Details");
        }else {
            actionBar.setTitle("Add Item Details");
        }


        if (isUpdateMode && stockReceiveDetails !=null){
            btnsave.setText("Update Item");
            invdtl_DocNo.setText(stockReceive.getDocNo());
            invdtl_item_textView.setText(stockReceiveDetails.getItemCode());
            invdtl_item_detail_txt.setText(stockReceiveDetails.getItemDescription());
            uom_text.setText(stockReceiveDetails.getUOM());
            invdtl_remark_txt.setText(stockReceiveDetails.getRemarks());
            invdtl_remark_txt2.setText(stockReceiveDetails.getRemarks2());
            editTextNumber2.setText(String.valueOf(stockReceiveDetails.getQuantity()));
            invdtl_unitprice_txt.setText(String.valueOf(stockReceiveDetails.getUTD_Cost()));
            invdtl_subtotal_txt.setText(String.valueOf(stockReceiveDetails.getSubTotal()));

            String batchNo = stockReceiveDetails.getBatch_No();

            if (batchNo == null){
                lbl_batch_no.setVisibility(View.GONE);
                invdtl_batchno_txt.setVisibility(View.GONE);

            } else {
                lbl_batch_no.setVisibility(View.VISIBLE);
                invdtl_batchno_txt.setVisibility(View.VISIBLE);
                invdtl_batchno_txt.setText(batchNo);
            }

        } else {

        }


        Cursor cursor51 = db.getReg("38");
        if (cursor51.moveToFirst()) {
            isBatchNoEnabled = Boolean.valueOf(cursor51.getString(0));
        }

        Cursor cursor61 = db.getReg("37");
        if (cursor61.moveToFirst()) {
            defaultBatchNo = cursor61.getString(0);
        }

        Cursor cursor71 = db.getReg("39");
        if (cursor71.moveToFirst()) {
            isSaleBatchEnabled = Boolean.valueOf(cursor71.getString(0));
        }

        Cursor cursor6 = db.getReg("40");
        if(cursor6.moveToFirst()){
            isPurBatchEnabled= Boolean.valueOf(cursor6.getString(0));
        }
        Cursor dl = db.getReg("7");
        if(dl.moveToFirst()){
            defaultLocation = dl.getString(0);
        }

        Cursor data = db.getReg("25");
        if (data.moveToFirst()) {
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
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

        //item choose from Manual List.
        if (item != null){
            invdtl_DocNo.setText(stockReceive.getDocNo());
            invdtl_item_textView.post(new Runnable() {
                @Override
                public void run() {
                    invdtl_item_textView.setText(item.getItemCode());
                }
            });
        }


        //scanner used in manual list
        if (scannedData != null){
            invdtl_DocNo.setText(stockReceive.getDocNo());
            invdtl_item_textView.post(new Runnable() {
                @Override
                public void run() {
                    invdtl_item_textView.setText(scannedData);
                }
            });

        }

        //barcode icon
        si_item_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qrScan = new IntentIntegrator(StockReceive_AddNewItemDetail.this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(true);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

                } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }

            }
        });

        //search button
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUpdateMode){
                    Intent resultIntent = new Intent(StockReceive_AddNewItemDetail.this, StockReceive_AddItemManual.class);
                    resultIntent.putExtra("stockReceive", stockReceive);
                    resultIntent.putExtra("nPosition", position);
                    resultIntent.putExtra("isUpdateMode", isUpdateMode);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });


        invdtl_item_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String remove = removeNonAlphanumeric(s.toString().trim());
                String myBarcode = remove;

                stockReceiveDetails = new AC_Class.StockReceiveDetails();

                stockReceiveDetails.setLocation(defaultLocation);
                stockReceiveDetails.setDocNo(stockReceive.getDocNo());
                stockReceiveDetails.setDocDate(stockReceive.getDocDate());

                if (!myBarcode.equals("") && !TextUtils.isEmpty(invdtl_item_textView.getText())){
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled) {
                        String normalBarcode = invdtl_item_textView.getText().toString();
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
                                    stockReceiveDetails.setItemCode(myBarcode);
                                    invdtl_item_textView.setText(myBarcode);
                                } else {
                                    Toast.makeText(StockReceive_AddNewItemDetail.this, "Invalid Barcode.",
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

                        if (results.getCount() == 0){
                            invdtl_item_textView.setText("");
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            AlertDialog alertDialog = new AlertDialog.Builder(StockReceive_AddNewItemDetail.this).create();
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

                            stockReceiveDetails.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            invdtl_item_detail_txt.setText(stockReceiveDetails.getItemDescription());

                            String hasBatch = results.getString(results.getColumnIndex("HasBatchNo"));

                            if(isBatchNoEnabled && hasBatch.equals("true")){
                                hasBatchNoItem = true;
                                lbl_batch_no.setVisibility(View.VISIBLE);
                                invdtl_batchno_txt.setVisibility(View.VISIBLE);

                                if (isPurBatchEnabled){
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
                                    invdtl_batchno_txt.setText(defaultBatchNo);
                                    stockReceiveDetails.setBatch_No(defaultBatchNo);
                                }
                            } else {
                                hasBatchNoItem = false;
                                lbl_batch_no.setVisibility(View.GONE);
                                invdtl_batchno_txt.setVisibility(View.GONE);
                            }

                            stockReceiveDetails.setUOM(results.getString(results.getColumnIndex("BaseUOM")));
                            stockReceiveDetails.setQuantity(myQty);
                            stockReceiveDetails.setRemarks(barcodeDtlRemark);
                            stockReceiveDetails.setRemarks2(barcodeDtlRemark2);

                            uom_text.setText(stockReceiveDetails.getUOM());
                            editTextNumber2.setText(String.valueOf(myQty));
                            invdtl_remark_txt.setText(stockReceiveDetails.getRemarks());
                            invdtl_remark_txt2.setText(stockReceiveDetails.getRemarks2());

                            stockReceiveDetails.setItemCode(invdtl_item_textView.getText().toString());

                            Cursor cursor1 = db.getAllUTDCost(stockReceiveDetails.getItemCode(), stockReceiveDetails.getUOM());
                            if (cursor1 != null) {
                                if (cursor1.getCount() > 0) {
                                    while (cursor1.moveToNext()) {
                                        stockReceiveDetails.setUTD_Cost(cursor1.getDouble(cursor1.getColumnIndex("UTDCost")));
                                        invdtl_unitprice_txt.setText(String.valueOf(stockReceiveDetails.getUTD_Cost()));
                                    }
                                } else {
                                    stockReceiveDetails.setUTD_Cost(0.00);
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    String formattedTotal = df.format(stockReceiveDetails.getUTD_Cost());
                                    invdtl_unitprice_txt.setText(formattedTotal);
                                }
                                cursor1.close();
                            } else {
                                // Handle the case where cursor1 is null
                                stockReceiveDetails.setUTD_Cost(0.00);
                                DecimalFormat df = new DecimalFormat("0.00");
                                String formattedTotal = df.format(stockReceiveDetails.getUTD_Cost());
                                invdtl_unitprice_txt.setText(formattedTotal);
                            }

                            double subTotal = stockReceiveDetails.getQuantity() * stockReceiveDetails.getUTD_Cost();
                            DecimalFormat df = new DecimalFormat("0.00");
                            String formattedTotal = df.format(subTotal);

                            stockReceiveDetails.setSubTotal(Double.valueOf(formattedTotal));
                            invdtl_subtotal_txt.setText(String.valueOf(stockReceiveDetails.getSubTotal()));

                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TextWatcher", "afterTextChanged: " + s.toString());

            }
        });



        //remarks 1
        invdtl_remark_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                stockReceiveDetails.setRemarks(invdtl_remark_txt.getText().toString());
            }
        });

        //remarks2
        invdtl_remark_txt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                stockReceiveDetails.setRemarks2(invdtl_remark_txt2.getText().toString());
            }
        });

        //qty decrease btn
        btn_Dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double qty = Double.parseDouble(editTextNumber2.getText().toString());

                if (qty > 1){
                    qty--;
                    editTextNumber2.setText(String.valueOf(qty));
                    stockReceiveDetails.setQuantity(qty);
                }
            }
        });

        //qty increase btn
        btn_Inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double qty = Double.parseDouble(editTextNumber2.getText().toString());
                qty++;
                editTextNumber2.setText(String.valueOf(qty));
                stockReceiveDetails.setQuantity(qty);
            }
        });

        //qty edit text
        editTextNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editTextNumber2.getText().toString();
                if (!text.isEmpty()) {
                    try {
                        double quantity = Double.valueOf(text);
                        stockReceiveDetails.setQuantity(quantity);

                        double nSubTotal = quantity * stockReceiveDetails.getUTD_Cost();

                        DecimalFormat df = new DecimalFormat("0.00");
                        String formattedSubTotal = df.format(nSubTotal);

                        stockReceiveDetails.setSubTotal(Double.parseDouble(formattedSubTotal));
                        invdtl_subtotal_txt.setText(String.valueOf(stockReceiveDetails.getSubTotal()));
                    } catch (NumberFormatException e) {
                        Log.e("TextWatcher", "Invalid number format", e);
                    }
                } else {
                    stockReceiveDetails.setQuantity(1.0);

                    double nSubTotal = stockReceiveDetails.getQuantity() * stockReceiveDetails.getUTD_Cost();

                    DecimalFormat df = new DecimalFormat("0.00");
                    String formattedSubTotal = df.format(nSubTotal);

                    stockReceiveDetails.setSubTotal(Double.valueOf(formattedSubTotal));
                    invdtl_subtotal_txt.setText(String.valueOf(stockReceiveDetails.getSubTotal()));
                }
            }
        });

        //cost
        invdtl_unitprice_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString().trim();
                if (inputText.isEmpty()) {
                    inputText = "0.00";
                    invdtl_unitprice_txt.setText("0.00");
                }

                try {
                    double unitPrice = Double.parseDouble(inputText);

                    stockReceiveDetails.setUTD_Cost(unitPrice);

                    double nTotal = unitPrice * stockReceiveDetails.getQuantity();

                    DecimalFormat df = new DecimalFormat("0.00");
                    String formattedTotal = df.format(nTotal);

                    invdtl_subtotal_txt.setText(formattedTotal);
                    stockReceiveDetails.setSubTotal(Double.valueOf(formattedTotal));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        //cancel button
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //add button
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemDetails();
            }
        });



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "No result found.", Toast.LENGTH_SHORT).show();
            } else {
                invdtl_item_textView.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }



    private void saveItemDetails() {
        if (isBatchNoEnabled && hasBatchNoItem){
            String batch = invdtl_batchno_txt.getText().toString();
            if(batch == null || batch.equals("")) {
                Toast.makeText(this, "Please fill in batch no", Toast.LENGTH_SHORT).show();
            } else {
                save();
            }
        } else {
            save();
        }
    }

    private void save() {
        if (isUpdateMode){
            Intent intent = new Intent();
            intent.putExtra("StockReceiveDetail", stockReceiveDetails);
            intent.putExtra("nPosition", position);
            setResult(RESULT_OK, intent);
            finish();

        } else {
            //detail -> manual
            Intent resultIntent = new Intent();
            resultIntent.putExtra("StockReceiveDetail", stockReceiveDetails);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}