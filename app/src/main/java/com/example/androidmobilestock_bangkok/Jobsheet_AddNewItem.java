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
import java.util.Locale;

public class Jobsheet_AddNewItem extends AppCompatActivity {

    RecyclerView item_recyclerView;
    TextView subtotalTxt, discountTxt, taxTxt, totalTxt;
    EditText invdtl_editText;
    Button btnClear, btnMerge, done_btn;
    String func;
    AC_Class.JobSheet jobSheet;
    AC_Class.JobSheetDetails jobSheetDetails;
    ACDatabase db;
    IntentIntegrator qrScan;

    private static final int REQUEST_CODE_ADD_ITEM_MANUAL = 1;
    private static final int REQUEST_CODE_UPDATE_ITEM = 3;
    List<AC_Class.JobSheetDetails> itemList;
    List<AC_Class.JobSheetDetails> currentItems = new ArrayList<>();
    Jobsheet_SelectedItemAdapter adapter;
    private Jobsheet_SelectedItemAdapter.RecyclerViewClickListener listener;
    double myQty = 1;
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
    Boolean isAutoPrice = false;
    Boolean isBatchNoEnabled = true;
    Boolean isSaleBatchEnabled = true;
    String def_loc;
    String salesOrderType = "";
    Boolean isTaxEnabled = true;
    String defaultTax = "";
    Cursor results;
    String default_location = "";
    Boolean isTaxInclusive;

    //StockReceive_AddNewItem.java
    //InvoiceDtlList.java


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet_add_new_item);

        item_recyclerView = findViewById(R.id.item_recyclerView);
        subtotalTxt = findViewById(R.id.subtotalTxt);
        discountTxt = findViewById(R.id.discountTxt);
        taxTxt = findViewById(R.id.taxTxt);
        totalTxt = findViewById(R.id.totalTxt);
        btnClear = findViewById(R.id.btnClear);
        btnMerge = findViewById(R.id.btnMerge);
        //btnSignature = findViewById(R.id.btnSignature);
        //signature_iv = findViewById(R.id.signature_iv);
        //btnCreditSales2 = findViewById(R.id.btnCreditSales2);
        //btnCashSales2 = findViewById(R.id.btnCashSales2);
        done_btn = findViewById(R.id.done_btn);
        invdtl_editText = findViewById(R.id.invdtl_editText);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Job Sheet - Item List");

        db = new ACDatabase(this);
        func = getIntent().getStringExtra("FunctionKey");

        itemList = new ArrayList<>();

        jobSheetDetails = new AC_Class.JobSheetDetails();

        jobSheet = getIntent().getParcelableExtra("JobSheet");
        itemList = getIntent().getParcelableArrayListExtra("itemList");

        Cursor data = db.getReg("25");
        if (data.moveToFirst()) {
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
        }

        Cursor cursor2 = db.getReg("13");
        if (cursor2.moveToFirst()) {
            isTaxInclusive = Boolean.valueOf(cursor2.getString(0));
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

        Cursor cursor4 = db.getReg("20");
        if (cursor4.moveToFirst()) {
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor5 = db.getReg("38");
        if(cursor5.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor7 = db.getReg("39");
        if(cursor7.moveToFirst()){
            isSaleBatchEnabled= Boolean.valueOf(cursor7.getString(0));
        }

        Cursor loc = db.getReg("7");
        if (loc.moveToFirst()) {
            def_loc = loc.getString(0);
        }

        if (def_loc.equals("None")) {
            def_loc = null;
        } else {
            jobSheetDetails.setLocation(def_loc);
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor1 = db.getReg("21");
        if (cursor1.moveToFirst()) {
            defaultTax = cursor1.getString(0);
        }

        //adapter
        adapter = new Jobsheet_SelectedItemAdapter(itemList, this, listener);
        adapter = new Jobsheet_SelectedItemAdapter(itemList, this, new Jobsheet_SelectedItemAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                showAlertDialog(position);
            }
        });

        item_recyclerView.setAdapter(adapter);
        item_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        item_recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        getFuncForEdit();

        updateMergeButtonText();

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

                jobSheetDetails = new AC_Class.JobSheetDetails();

                Cursor dl = db.getReg("7");
                if (dl.moveToFirst()) {
                    default_location = dl.getString(0);
                }
                jobSheetDetails.setLocation(default_location);
                jobSheetDetails.setDocNo(jobSheet.getDocNo());
                jobSheetDetails.setDocDate(jobSheet.getDocDate());

                if (!myBarcode.equals("") && !TextUtils.isEmpty(invdtl_editText.getText())){
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled){
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
                                    Toast.makeText(Jobsheet_AddNewItem.this, "Invalid Barcode.",
                                            Toast.LENGTH_SHORT).show();
                                    isSkip = true;
                                }
                            }
                        }
                    }

                    if (!isSkip){
                        Cursor results = db.getBarcodeItemDetail(myBarcode);

                        if (results.getCount() == 0) {
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            AlertDialog alertDialog = new AlertDialog.Builder(Jobsheet_AddNewItem.this).create();
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

                            jobSheetDetails.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                            jobSheetDetails.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            jobSheetDetails.setUOM(results.getString(results.getColumnIndex("BaseUOM")));
                            jobSheetDetails.setUPrice(results.getDouble(results.getColumnIndex("Price")));
                            jobSheetDetails.setRemarks(barcodeDtlRemark);
                            jobSheetDetails.setRemarks2(barcodeDtlRemark2);

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
                                                        break;
                                                    case 3:
                                                        jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price3"))));
                                                        break;
                                                    case 4:
                                                        jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price4"))));
                                                        break;
                                                    case 5:
                                                        jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price5"))));
                                                        break;
                                                    case 6:
                                                        jobSheetDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price6"))));
                                                        break;
                                                }
                                            } catch (NumberFormatException e) {
                                                throw e;
                                            }
                                        }
                                    }
                                }
                            }

                            if(isBatchNoEnabled &&  results.getString(results.getColumnIndex("HasBatchNo")).equals("true")){
                                if(isSaleBatchEnabled) {
                                    getbatchNo();
                                }else{
                                    Intent new_intent = new Intent(getApplicationContext(), ItemBatchList.class);
                                    new_intent.putExtra("ItemCode",  jobSheetDetails.getItemCode());
                                    new_intent.putExtra("UOM",  jobSheetDetails.getUOM());
                                    new_intent.putExtra("Location", default_location);
                                    startActivityForResult(new_intent, 9);

                                }
                            }

                            jobSheetDetails.setQuantity(myQty);
                            jobSheetDetails.setTaxType(getTax(results.getString(results.getColumnIndex("TaxType"))));
                            Cursor data2 = db.getTaxTypeValue(jobSheetDetails.getTaxType());
                            if (data2.moveToNext()) {
                                jobSheetDetails.setTaxRate(data2.getDouble(0));
                            }

                            if (isTaxInclusive){
                                //inclusive
                                jobSheetDetails.setTotal_In(jobSheetDetails.getQuantity() * jobSheetDetails.getUPrice() - jobSheetDetails.getDiscount());
                               // Calc. tax from total
                                jobSheetDetails.setTaxValue(jobSheetDetails.getTotal_In() * jobSheetDetails.getTaxRate() / (100 + jobSheetDetails.getTaxRate()));
                                jobSheetDetails.setTotal_Ex(jobSheetDetails.getTotal_In() - jobSheetDetails.getTaxValue());
                                jobSheetDetails.setSubTotal((jobSheetDetails.getTotal_Ex() + jobSheetDetails.getDiscount()));
                            } else {
                                //exclusive
                                jobSheetDetails.setSubTotal(jobSheetDetails.getQuantity() * jobSheetDetails.getUPrice());
                                jobSheetDetails.setTotal_Ex(jobSheetDetails.getSubTotal() - jobSheetDetails.getDiscount());
                                jobSheetDetails.setTaxValue(jobSheetDetails.getTotal_Ex() * jobSheetDetails.getTaxRate() / 100);
                                jobSheetDetails.setTotal_In(jobSheetDetails.getTotal_Ex() + jobSheetDetails.getTaxValue());
                             }


                            itemList.add(jobSheetDetails);
                            adapter.notifyDataSetChanged();
                            updateTotals();
                            updateMergeButtonText();

                            barcodeDtlRemark = "";
                            barcodeDtlRemark2 = "";
                            myQty = 1;
                        }
                    }
                }
                invdtl_editText.removeTextChangedListener(this);
                invdtl_editText.clearFocus();
                invdtl_editText.requestFocus();
                invdtl_editText.setText("");
                invdtl_editText.addTextChangedListener(this);

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Jobsheet_AddNewItem.this);

                builder.setTitle("Clear Items");
                builder.setMessage("Are you sure you want to clear all the items?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        getJobSheetDetailsList();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(Jobsheet_AddNewItem.this);
                    builder.setTitle("Merge Items");
                    builder.setMessage("Are you sure you want to merge these items?");

                    builder.setPositiveButton("Merge", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<AC_Class.JobSheetDetails> oriDetail = itemList;
                            ArrayList<AC_Class.JobSheetDetails> newDetail = new ArrayList<>();
                            for (int i = 0; i < oriDetail.size(); i++) {
                                boolean result = false;

                                for (AC_Class.JobSheetDetails myDtl : newDetail) {
                                    if(oriDetail.get(i).getBatchNo()!=null) {
                                        if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM()) && oriDetail.get(i).getUPrice().equals(myDtl.getUPrice()) && oriDetail.get(i).getRemarks().equals(myDtl.getRemarks()) && oriDetail.get(i).getDiscount().equals(myDtl.getDiscount()) && oriDetail.get(i).getBatchNo().equals(myDtl.getBatchNo())) {
                                            myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                                            myDtl.setDiscount(myDtl.getDiscount() + oriDetail.get(i).getDiscount());
                                            myDtl.setTaxValue(myDtl.getTaxValue() + oriDetail.get(i).getTaxValue());
                                            myDtl.setSubTotal(myDtl.getSubTotal() + oriDetail.get(i).getSubTotal());
                                            myDtl.setTotal_Ex(myDtl.getTotal_Ex() + oriDetail.get(i).getTotal_Ex());
                                            myDtl.setTotal_In(myDtl.getTotal_In() + oriDetail.get(i).getTotal_In());
                                            result = true;
                                        }
                                    }else{
                                        if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM()) && oriDetail.get(i).getUPrice().equals(myDtl.getUPrice()) && oriDetail.get(i).getRemarks().equals(myDtl.getRemarks()) && oriDetail.get(i).getDiscount().equals(myDtl.getDiscount())) {
                                            myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                                            myDtl.setDiscount(myDtl.getDiscount() + oriDetail.get(i).getDiscount());
                                            myDtl.setTaxValue(myDtl.getTaxValue() + oriDetail.get(i).getTaxValue());
                                            myDtl.setSubTotal(myDtl.getSubTotal() + oriDetail.get(i).getSubTotal());
                                            myDtl.setTotal_Ex(myDtl.getTotal_Ex() + oriDetail.get(i).getTotal_Ex());
                                            myDtl.setTotal_In(myDtl.getTotal_In() + oriDetail.get(i).getTotal_In());
                                            result = true;
                                        }
                                    }

                                }

                                if (!result) {
                                    newDetail.add(oriDetail.get(i));
                                }
                            }

                            itemList.clear();
                            currentItems.clear();
                            itemList.addAll(newDetail);

                            adapter.notifyDataSetChanged();

                            Toast.makeText(Jobsheet_AddNewItem.this, "Items merged successfully", Toast.LENGTH_SHORT).show();
                            updateMergeButtonText();
                            updateTotals();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(Jobsheet_AddNewItem.this, "No items to merge", Toast.LENGTH_SHORT).show();
                }
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("itemList", new ArrayList<>(itemList));
                resultIntent.putExtra("subTotal", subtotalTxt.getText().toString());
                resultIntent.putExtra("discount", discountTxt.getText().toString());
                resultIntent.putExtra("taxValue", taxTxt.getText().toString());
                resultIntent.putExtra("totalIn", totalTxt.getText().toString());

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });


    }

    private void showAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Jobsheet_AddNewItem.this);
        builder.setMessage("What do you want to do?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyDataSetChanged();
                updateTotals();
                updateMergeButtonText();
            }
        });
        builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent intent = new Intent(Jobsheet_AddNewItem.this,
                        Jobsheet_AddItemManual.class);

                intent.putExtra("JobSheet", jobSheet);
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


    private String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            String taxType = defaultTax;
            if (taxType != null && !taxType.isEmpty()){
                return taxType;
            } else if (itemTaxType != null && !itemTaxType.isEmpty()) {
                return itemTaxType;
            } else {
                return defaultTax;
            }
        }
        return "None";
    }


    private void getbatchNo() {
        Cursor data2 = db.getAllItemBatch(jobSheetDetails.getItemCode(), jobSheetDetails.getUOM(), def_loc);
        Cursor data3 = db.getAllItemBatch(jobSheetDetails.getItemCode(), jobSheetDetails.getUOM(), def_loc);
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

    private void updateMergeButtonText() {
        btnMerge.setText("Merge (" + itemList.size() + ")");
    }

    private void getJobSheetDetailsList() {
        currentItems.clear();
        itemList.clear();
        adapter.notifyDataSetChanged();
        updateMergeButtonText();
        updateTotals();
    }

    private void getFuncForEdit() {

        switch (func) {
            case "Edit":

                /*
                itemList.clear();
                Cursor cursor = db.getJobSheetDetailsByDocNo(jobsheet.getDocNo());
                if (cursor != null && cursor.getCount() > 0){
                    while (cursor.moveToNext()){
                        Integer id = cursor.getInt(1);
                        //

                        AC_Class.JobSheetDetails jobSheetDetails1 = new AC_Class.JobSheetDetails(

                        );
                        itemList.add(jobSheetDetails1);
                    }
                    cursor.close();
                }
                adapter.notifyDataSetChanged();

                 */
//                subtotalTxt.setText(String.valueOf(jobSheet.getTotalEx()));
//                discountTxt.setText(String.valueOf(jobSheet.getDiscount()));
//                taxTxt.setText(String.valueOf(jobSheet.getTotalTax()));
//                totalTxt.setText(String.valueOf(jobSheet.getTotalIn()));

                updateTotals();

                break;

            case "New":
                updateTotals();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 9:
                String batchNo = data.getStringExtra("ItemBatchNo");
                jobSheetDetails.setBatchNo(batchNo);
                break;

            //scanner in the same class is used
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        invdtl_editText.setText(result.getContents().trim());
                    }
                }
                break;

            //item -> manual -> detail -> manual -> item
            case REQUEST_CODE_ADD_ITEM_MANUAL :
                if (requestCode == REQUEST_CODE_ADD_ITEM_MANUAL && resultCode == RESULT_OK && data != null){
                    AC_Class.JobSheetDetails newItem = data.getParcelableExtra("JobSheet Details");

                    newItem.setDocDate(jobSheet.getDocDate());

                    if (newItem != null){
                        currentItems.add(newItem);
                        itemList.add(newItem);
                        adapter.notifyItemInserted(itemList.size() - 1);
                        updateTotals();
                        updateMergeButtonText();
                    }
                }
                break;

            //return from updated item
            case REQUEST_CODE_UPDATE_ITEM:
                AC_Class.JobSheetDetails updateItem = data.getParcelableExtra("JobSheet Details");
                int nPosition = data.getIntExtra("nPosition", -1);
                if (updateItem != null){
                    Log.d("1122333", "item: " + updateItem.getItemCode());
                    Log.d("1122333", "item: " + updateItem.getItemDescription());
                    Log.d("1122333", "item: " + nPosition);
                    itemList.set(nPosition, updateItem);
                    adapter.notifyItemChanged(nPosition);
                    updateTotals();
                    updateMergeButtonText();
                } else {
                    Log.d("Checking123", "Update item pass back to new item is null");
                }
                    break;

        }


    }

    private void updateTotals() {
        int i;

        AC_Class.JobSheetDetails current;
        double tempSubtotal = 0;
        double tempDiscount = 0;
        double tempTax = 0;
        double tempTotal = 0;
        for (i = 0; i < itemList.size(); i++) {
            current = itemList.get(i);
            tempSubtotal += current.getSubTotal();
            tempDiscount += current.getDiscount();
            tempTax += current.getTaxValue();
            tempTotal += current.getTotal_In();
        }
        subtotalTxt.setText(String.format(Locale.getDefault(), "%.2f", tempSubtotal));
        discountTxt.setText(String.format(Locale.getDefault(), "%.2f", tempDiscount));
        taxTxt.setText(String.format(Locale.getDefault(), "%.2f", tempTax));
        totalTxt.setText(String.format(Locale.getDefault(), "%.2f", tempTotal));

        jobSheet.setTotalEx(Double.valueOf(subtotalTxt.getText().toString()));
        jobSheet.setTotalIn(Double.valueOf(totalTxt.getText().toString()));
        jobSheet.setTotalTax(Double.valueOf(taxTxt.getText().toString()));

    }

    @Override
    public void onBackPressed() {
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

            case (R.id.addStockCount): {
                final Intent intent = new Intent(Jobsheet_AddNewItem.this,
                        Jobsheet_AddItemManual.class);
                intent.putExtra("JobSheet", jobSheet);
                intent.putExtra("fromItemPage", true);
                intent.putExtra("subString", "");
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


