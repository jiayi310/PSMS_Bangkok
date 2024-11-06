package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.PlActivityPldtlAddBarcodeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;

public class PL_PLDtlAddBarcode extends AppCompatActivity {

    ACDatabase db;
    PlActivityPldtlAddBarcodeBinding binding;
    AC_Class.DO packingList, packingListTemp;
    ArrayList<AC_Class.DODtl> packingListDtlList;
    PL_PLDtlListViewAdapter adapter;
    MyClickHandler handler;
    EditText et_Barcode;
    private Context context;

    private IntentIntegrator qrScan;

    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;

    String def_loc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(PL_PLDtlAddBarcode.this, R.layout.pl_activity_pldtl_add_barcode);
        packingListDtlList = new ArrayList<>();

        adapter = new PL_PLDtlListViewAdapter(this, packingListDtlList);
        binding.lvDetail.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        et_Barcode = (EditText) findViewById(R.id.trdtl_editText);

        db = new ACDatabase(this);

        //Retrieve default locations
        Cursor loc = db.getReg("7");
        if(loc.moveToFirst()){
            def_loc = loc.getString(0);
        }
        if (def_loc.equals("None")){
            def_loc = null;
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

        }

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        context = this.getApplicationContext();

        packingListTemp = packingList = getIntent().getParcelableExtra("iPackingList");
        //transfer_temp = getIntent().getParcelableExtra("TrHeaderTemp");

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Packing List Details - Add");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        binding.setDocNo(packingList.getDocNo());
        binding.etBarcode.requestFocus();
        binding.etBarcode.setShowSoftInputOnFocus(false);

//        boolean open = false;
//
//        barcode2D = Barcode2DWithSoft.getInstance();
//        open = barcode2D.open(context);
//
//        try
//        {
//            if (open) {
//                barcode2D.setParameter(324, 1);
//                barcode2D.setParameter(300, 0); // Snapshot Aiming
//                barcode2D.setParameter(361, 0); // Image Capture Illumination
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        binding.etBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
//                startScan();
                String myBarcode = s.toString();
                String default_location = "";
                double myQty = 1;

                if (!myBarcode.equals("") && !TextUtils.isEmpty(binding.etBarcode.getText())) {
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled)
                    {
                        if (myBarcode.length() == barcodeLength)
                        {
                            if (isNumeric(myBarcode)) {
                                myQty = Double.valueOf(myBarcode.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                if (qtyDecimal > 0) {
                                    myQty = myQty / Math.pow(10,qtyDecimal);
                                }
                                myBarcode = myBarcode.substring(itemStart - 1, itemStart - 1 + itemLength);
                            } else {
                                Toast.makeText(PL_PLDtlAddBarcode.this, "Invalid Barcode.",
                                        Toast.LENGTH_SHORT).show();
                                isSkip = true;
                            }
                        }
                    }

                    if (!isSkip) {

                        Cursor results = db.getItemBC(myBarcode);
                        if (results.getCount() == 0) {
                            Toast.makeText(PL_PLDtlAddBarcode.this, "Product not found.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            results.moveToNext();

                            AC_Class.DODtl packingListDtl = new AC_Class.DODtl(packingList.getDocNo(), def_loc);
                            packingListDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                            packingListDtl.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            packingListDtl.setUOM(results.getString(results.getColumnIndex("UOM")));
                            if (isCustomBarcodeEnabled)
                            {
                                packingListDtl.setQty(myQty);
                                packingListDtlList.add(packingListDtl);
                                packingList.getDODtlList().add(packingListDtl);
                                adapter.notifyDataSetChanged();
                            }
                            else {

                                if (!CheckEmpty(packingListDtl)) {
                                    if (!CheckExist(packingListDtl)) {
                                        packingListDtlList.add(packingListDtl);
                                        packingList.getDODtlList().add(packingListDtl);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(PL_PLDtlAddBarcode.this, "Invalid Barcode", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }

                binding.etBarcode.removeTextChangedListener(this);
                binding.etBarcode.clearFocus();
                binding.etBarcode.requestFocus();
                hideSoftKeyboard();
                binding.etBarcode.setText("");
                binding.etBarcode.addTextChangedListener(this);

            }
        });

    }

    private boolean CheckEmpty(AC_Class.DODtl packingListDtlFP) {
        return (packingListDtlFP.getItemCode() == null || packingListDtlFP.getUOM() == null );
    }

//    public void startScan()
//    {
//        final String[] bc = new String[1];
//
//        Log.wtf("Barcode Scan", "Start");
//        barcode2D.scan();
//        barcode2D.setScanCallback(new Barcode2DWithSoft.ScanCallback() {
//            @Override
//            public void onScanComplete(int i, int i1, byte[] bytes) {
//                if (bytes != null) {
//                    String barcode = new String(bytes);
//                    Log.wtf("Barcode",barcode);
//                    trdtl_editText.setText(barcode);
//                    barcode2D.stopScan();
//                }
//            }
//        });
//
//        if(!trdtl_editText.getText().equals(""))
//        {
//            searchForItem(trdtl_editText.getText().toString());
//        }
//
//
//    }


    private boolean CheckExist(AC_Class.DODtl packingListDtlFP) {
        boolean myresult = false;
        for(int i = 0; i < packingListDtlList.size(); i++) {
            AC_Class.DODtl tempDtl = packingListDtlList.get(i);

            if (tempDtl.getItemCode().equals(packingListDtlFP.getItemCode()) && tempDtl.getUOM().equals(packingListDtlFP.getUOM()))
            {
                Log.wtf("Item Code",packingListDtlFP.getItemCode());
                packingListDtlList.get(i).setQty(packingListDtlList.get(i).getQty()+1);
                myresult = true;
            }
        }
        return myresult;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(PL_PLDtlAddBarcode.this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.etBarcode.setText(result.getContents());
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }


    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnSaveBtnClicked(View view) {
            if (packingListDtlList.size()<1) {
                Intent newIntent = new Intent();
                setResult(99, newIntent);
//                barcode2D.close();
                finish();
            } else {
                Intent newIntent = new Intent();
                newIntent.putParcelableArrayListExtra("iPackingListDtlList", packingListDtlList);
                setResult(0, newIntent);
//                barcode2D.close();
                finish();
            }
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(PL_PLDtlAddBarcode.this);
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                //qrScan.setBarcodeImageEnabled(false);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void OnCancelBtnClicked(View view) {
            PL_PLDtlAddBarcode.this.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent();
        packingList = packingListTemp;
        newIntent.putExtra("iPackingList", packingList);
        setResult(99, newIntent);
//        barcode2D.close();
        finish();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        while (keyCode == 139 || keyCode == 293) {
//            if (event.getRepeatCount() == 0) {
//                Log.wtf("Barcode Scan", "Start");
//                startScan();
//            }
//            else{
//                barcode2D.stopScan();
//                break;
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

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

    public void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(PL_PLDtlAddBarcode.this.getCurrentFocus().getWindowToken(), 0);
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