package com.example.androidmobilestock_bangkok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.databinding.DataBindingUtil;

import android.database.Cursor;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.PurActivityPurchasedtllistBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PUR_PurchaseDtlList extends AppCompatActivity {
    PurActivityPurchasedtllistBinding binding;
    AC_Class.PurchaseDetails docDtl, temp_docDtl;
    AC_Class.Purchase doc, temp_doc;
    Context context = this;
    ACDatabase db;
    String def_loc;
    Boolean isTaxEnabled = true;
    private IntentIntegrator qrScan;
    MyClickHandler handler;
    PUR_PurchaseDtlListViewAdapter adapter;
    Button btnMerge;

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
    String defaultTax = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        //Broadcast Receiver
//        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
//                new IntentFilter("ADD_INVDTL"));

        binding = DataBindingUtil.setContentView(this, R.layout.pur_activity_purchasedtllist);
        docDtl = new AC_Class.PurchaseDetails();
        doc = new AC_Class.Purchase();
        binding.setInv(doc);
        db = new ACDatabase(this);
        btnMerge = findViewById(R.id.btnMerge);
        binding.invdtlEditText.requestFocus();

        Cursor loc = db.getReg("7");
        if (loc.moveToFirst()) {
            def_loc = loc.getString(0);
        }

        Cursor cur = db.getReg("6");
        if (cur.moveToFirst()) {
            binding.setDefaultCurr(cur.getString(0));
        }

        if (def_loc.equals("None")) {
            def_loc = null;
        } else {
            docDtl.setLocation(def_loc);
        }
        Cursor cursor1 = db.getReg("21");
        if(cursor1.moveToFirst()){
            defaultTax = cursor1.getString(0);
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor data = db.getReg("25");
        if(data.moveToFirst()){
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
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

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Purchase Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        // Change format if tax enabled/disabled
        if (!isTaxEnabled) {
            binding.lblTax2.setVisibility(View.GONE);
            binding.taxTxt.setVisibility(View.GONE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(binding.relativeLayout14);
            constraintSet.connect(R.id.dividerco4, ConstraintSet.TOP,
                    R.id.lblDiscount, ConstraintSet.BOTTOM);
            constraintSet.applyTo(binding.relativeLayout14);
        }

        temp_doc = doc = getIntent().getParcelableExtra("iDoc");
        docDtl.setDocNo(doc.getDocNo());
        docDtl.setDocDate(doc.getDocDate());

        getDetailList();


        binding.lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PUR_PurchaseDtlList.this);
                builder.setMessage("What do you want to do?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doc.removePurchaseDetail(position);
                        getDetailList();
                    }
                });

                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String DocNo = ((AC_Class.PurchaseDetails) parent.getItemAtPosition(position))
                                .getDocNo();
                        Integer id = ((AC_Class.PurchaseDetails) parent.getItemAtPosition(position))
                                .getID();
                        temp_docDtl = (AC_Class.PurchaseDetails) parent.getItemAtPosition(position);

                        Intent intent = new Intent(PUR_PurchaseDtlList.this, PUR_EmptyAD.class);
                        intent.putExtra("iDoc", doc);
                        intent.putExtra("iDocDtl", temp_docDtl);
                        intent.putExtra("iFunc", "Edit");
                        intent.putExtra("iID", id);
                        intent.putExtra("iDocNo", DocNo);
                        intent.putExtra("iMode", 0);

                        doc.removePurchaseDetail(position);

                        startActivityForResult(intent, 1);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        binding.invdtlEditText.requestFocus();

        binding.invdtlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String myBarcode = s.toString();
                String default_location = "";
                double myQty = 1;

                docDtl = new AC_Class.PurchaseDetails();
                getDetailsFrom();

                Cursor dl = db.getReg("7");
                if(dl.moveToFirst()){
                    default_location = dl.getString(0);
                }
                docDtl.setLocation(default_location);

                if (!myBarcode.equals("") && !TextUtils.isEmpty(binding.invdtlEditText.getText())) {
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled)
                    {
                        String normalBarcode = binding.invdtlEditText.getText().toString();
                        Cursor results = db.getItemBC(normalBarcode);
                        if (results.getCount() == 0){
                            if (myBarcode.length() == barcodeLength)
                            {
                                if (isNumeric(myBarcode)) {
                                    myQty = Double.valueOf(myBarcode.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                    if(dtlRemarkStart!=0 && dtlRemarkLength!=0) {
                                        barcodeDtlRemark = myBarcode.substring(dtlRemarkStart - 1, dtlRemarkStart - 1 + dtlRemarkLength);
                                    }
                                    if(dtlRemark2Start!=0 && dtlRemark2Length!=0) {
                                        barcodeDtlRemark2 = myBarcode.substring(dtlRemark2Start - 1, dtlRemark2Start - 1 + dtlRemark2Length);
                                    }
                                    if (qtyDecimal > 0) {
                                        myQty = myQty / Math.pow(10,qtyDecimal);
                                    }
                                    myBarcode = myBarcode.substring(itemStart - 1, itemStart - 1 + itemLength);
                                } else {
                                    Toast.makeText(PUR_PurchaseDtlList.this, "Invalid Barcode.",
                                            Toast.LENGTH_SHORT).show();
                                    isSkip = true;
                                }
                            }
                        }
                    }

                    if (!isSkip)
                    {
                        Cursor results = db.getItemBC(myBarcode);
                        if (results.getCount() == 0) {
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            AlertDialog alertDialog = new AlertDialog.Builder(PUR_PurchaseDtlList.this).create();
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

                            docDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                            docDtl.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            docDtl.setUOM(results.getString(results.getColumnIndex("UOM")));
                            docDtl.setRemarks(barcodeDtlRemark);
                            docDtl.setRemarks2(barcodeDtlRemark2);
                            //docDtl.setUPrice(results.getDouble(4));

                            docDtl.setQuantity(myQty);
                            // TAX
                            docDtl.setTaxType(getTax(results.getString(results.getColumnIndex("TaxType"))));
                            Cursor data2 = db.getTaxTypeValue(docDtl.getTaxType());
                            if (data2.moveToNext()) {
                                docDtl.setTaxRate(data2.getDouble(0));
                            }
                            Calculation();
                            insertItem(docDtl);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Return from item
            case 1:
                if (resultCode == 0) {
                    temp_docDtl = data.getParcelableExtra("iDocDtl");
                    Log.i("custDebug", "Item added: " + temp_docDtl.getItemDescription());
                } else {
                    Log.i("custDebug", "Add item cancelled");
                }
                if (temp_docDtl != null) {
                    doc.addPurchaseDetail((AC_Class.PurchaseDetails) temp_docDtl);
                    temp_docDtl = null;
                }
                break;
            case 2:
                if (resultCode == 0) {
                    List<AC_Class.PurchaseDetails> docDtlList =
                            data.getParcelableArrayListExtra("iDocDtlList");
                    for (int i = 0; i < docDtlList.size(); i++) {
                        doc.addPurchaseDetail(docDtlList.get(i));
                        Log.i("custDebug", docDtlList.get(i).getItemCode() + " " +
                                docDtlList.get(i).getQuantity());
                    }
                } else {
                    Log.i("custDebug", "Add item cancelled");
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
        getDetailList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("iDoc", doc);
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

            case (R.id.addStockCount): {
                //
                final Intent intent = new Intent(PUR_PurchaseDtlList.this,
                        PUR_EmptyAD.class);
                intent.putExtra("iDoc", doc);
                intent.putExtra("iFunc", "New");
                //Open Manual
                intent.putExtra("iMode", 0);
                startActivityForResult(intent, 1);
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
                    //binding.invdtlEditText.setText("2501735075008");

                } catch (Exception e) {
                    Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage());
                }
            }
            break;
        }
        return false;
    }

    public void getDetailList() {
        adapter = new PUR_PurchaseDtlListViewAdapter(this, doc.getPurchaseDetailsList());
        binding.lvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getSummary(doc.getPurchaseDetailsList());
        btnMerge.setText("Merge (" + Integer.toString(doc.getPurchaseDetailsList().size()) + ")");

        if (adapter.getCount() > 0)
            binding.lvList.setSelection(adapter.getCount() - 1);
    }

    public void getSummary(List<AC_Class.PurchaseDetails> detailList) {
        int i;

        AC_Class.PurchaseDetails current;
        double tempSubtotal = 0;
        double tempDiscount = 0;
        double tempTax = 0;
        double tempTotal = 0;
        for (i = 0; i < detailList.size(); i++) {
            current = detailList.get(i);
            tempSubtotal += current.getSubTotal();
            tempDiscount += current.getDiscount();
            tempTax += current.getTaxValue();
            tempTotal += current.getTotal_In();

        }
        binding.subtotalTxt.setText(String.format(Locale.getDefault(),
                " %.2f", tempSubtotal));
        binding.discountTxt.setText(String.format(Locale.getDefault(),
                " %.2f", tempDiscount));
        binding.taxTxt.setText(String.format(Locale.getDefault(),
                " %.2f", tempTax));
        binding.totalTxt.setText(String.format(Locale.getDefault(),
                " %.2f", tempTotal));
    }

    public boolean CheckEmpty(AC_Class.PurchaseDetails details) {
        return (details.getLocation() == null ||
                details.getItemCode() == null ||
                details.getItemDescription() == null ||
                details.getUOM() == null ||
                details.getUPrice() == null ||
                details.getSubTotal() == null ||
                details.getTaxType() == null ||
                details.getTaxRate() == null ||
                details.getTaxValue() == null ||
                details.getTotal_Ex() == null ||
                details.getTotal_In() == null);
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

        public void OnbtnMergeClicked(View view) {
            if (doc.getPurchaseDetailsList().size() > 0) {
                List<AC_Class.PurchaseDetails> oriDetail = doc.getPurchaseDetailsList();
                ArrayList<AC_Class.PurchaseDetails> newDetail = new ArrayList<>();

                for (int i = 0; i < oriDetail.size(); i++) {
                    boolean result = false;

                    for (AC_Class.PurchaseDetails myDtl : newDetail) {
                        if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM())) {
                            myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());

                            myDtl.setDiscount(myDtl.getDiscount() + oriDetail.get(i).getDiscount());

                            myDtl.setTaxValue(myDtl.getTaxValue() + oriDetail.get(i).getTaxValue());

                            myDtl.setSubTotal(myDtl.getSubTotal() + oriDetail.get(i).getSubTotal());

                            myDtl.setTotal_Ex(myDtl.getTotal_Ex() + oriDetail.get(i).getTotal_Ex());

                            myDtl.setTotal_In(myDtl.getTotal_In() + oriDetail.get(i).getTotal_In());

                            result = true;
                        }
                    }

                    if (!result) {
                        newDetail.add(oriDetail.get(i));
                    }
                }

                doc.getPurchaseDetailsList().clear();
                doc.setPurchaseDetailsList(newDetail);

                getDetailList();
            }
        }

        public void OnSaveTxtViewClicked(View view) {
            if (doc.getPurchaseDetailsList().size() > 0) {
                commitSave();

            } else {
                Toast.makeText(PUR_PurchaseDtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }

        void commitSave() {
            // Commit details
            boolean commitDetails = db.UpdatePurchaseDetail(doc);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());
            doc.setLastModifiedDateTime(date);

            boolean insertHeader = db.insertPurchase(doc);
            if (doc.getDocNo().equals(db.getNextNoPurchase())) {
                db.IncrementAutoNumbering("P");
            }

            // Broadcast intent to close other activities
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);

            // Open inv details
            Intent new_intent = new Intent(PUR_PurchaseDtlList.this,
                    PUR_MultipleTab.class);
            new_intent.putExtra("iDocNo", doc.getDocNo());
            new_intent.putExtra("iCreditorCode", doc.getCreditorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        }
    }

    public void getDetailsFrom()
    {
        docDtl.setDocNo(doc.getDocNo());
        docDtl.setDocDate(doc.getDocDate());
    }

    public void Calculation()
    {
        docDtl.setSubTotal((docDtl.getQuantity() * docDtl.getUPrice()));
        docDtl.setTotal_Ex(docDtl.getSubTotal() - docDtl.getDiscount());
        docDtl.setTaxValue((docDtl.getTotal_Ex() * docDtl.getTaxRate()) / 100);
        docDtl.setTotal_In(docDtl.getTotal_Ex() + docDtl.getTaxValue());
    }

    public String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            if (doc.getTaxType() != null) {
                return doc.getTaxType();
            } else if (itemTaxType != null) {
                return itemTaxType;
            } else {
                return defaultTax;
            }
        }
        return "None";
    }

    private int checkDetailList(AC_Class.PurchaseDetails dtlFP){
        int result = -1;

        for(int i = 0; i <  doc.getPurchaseDetailsList().size(); i++) {
            AC_Class.PurchaseDetails tempDetail =  doc.getPurchaseDetailsList().get(i);

            if (tempDetail.getItemCode().equals(dtlFP.getItemCode()) && tempDetail.getUOM().equals(dtlFP.getUOM())){
                result = i;
            }
        }
        return result;
    }

    void insertItem(AC_Class.PurchaseDetails dtlFP) {
        if (!isCustomBarcodeEnabled) {
            int myResult = checkDetailList(dtlFP);
            if (myResult >= 0) {
                doc.getPurchaseDetailsList().get(myResult).setQuantity( doc.getPurchaseDetailsList().get(myResult).getQuantity()+1);
                updateDtl( doc.getPurchaseDetailsList().get(myResult));
            } else {
                doc.addPurchaseDetail((AC_Class.PurchaseDetails) dtlFP);
            }
        }
        else {
            doc.addPurchaseDetail((AC_Class.PurchaseDetails) dtlFP);
        }

        getDetailList();

//        if (adapter.getCount() > 0)
//            binding.lvList.setSelection(adapter.getCount() - 1);
    }

    void updateDtl(AC_Class.PurchaseDetails dtlFP) {
        dtlFP.setSubTotal((dtlFP.getQuantity() * dtlFP.getUPrice()));
        dtlFP.setTotal_Ex(dtlFP.getSubTotal() - dtlFP.getDiscount());
        dtlFP.setTaxValue((dtlFP.getTotal_Ex() * dtlFP.getTaxRate()) / 100);
        dtlFP.setTotal_In(dtlFP.getTotal_Ex() + dtlFP.getTaxValue());
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
}
