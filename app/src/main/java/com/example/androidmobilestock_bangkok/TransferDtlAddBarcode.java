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

import com.example.androidmobilestock_bangkok.databinding.ActivityTransferDtlAddBarcodeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class TransferDtlAddBarcode extends AppCompatActivity {

    ACDatabase db;
    ActivityTransferDtlAddBarcodeBinding binding;
    AC_Class.Transfer transfer,transfer_temp;
    AC_Class.TransferDtl transferDtl;
    ArrayList<AC_Class.TransferDtl> itemlist;
    TransferDtlAdapter adapter;
    MyClickHandler handler;
    EditText trdtl_editText;
    //private Barcode2DWithSoft barcode2D;
    private Context context;

    private IntentIntegrator qrScan;

    int opened = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(TransferDtlAddBarcode.this, R.layout.activity_transfer_dtl_add_barcode);
        itemlist = new ArrayList<>();

        adapter = new TransferDtlAdapter(this, itemlist);
        binding.trdtladdbarcodeItemlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        trdtl_editText = (EditText) findViewById(R.id.trdtl_editText);

        db = new ACDatabase(this);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        context = this.getApplicationContext();

        transfer = getIntent().getParcelableExtra("Tr");
        transfer_temp = getIntent().getParcelableExtra("TrHeaderTemp");

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Transfer Details - Add");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf98b88));

        binding.setDocNo(transfer.getDocNo());
        binding.trdtlEditText.requestFocus();
        binding.trdtlEditText.setShowSoftInputOnFocus(false);

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

        binding.trdtlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
//                startScan();
                String temp = s.toString();

                if (!temp.equals("") && !TextUtils.isEmpty(binding.trdtlEditText.getText())) {
                    Cursor results = db.getItemBC(temp);
                    if (results.getCount() == 0) {
                        Toast.makeText(TransferDtlAddBarcode.this, "Product not found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        results.moveToNext();

                        transferDtl = new AC_Class.TransferDtl();
                        transferDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                        transferDtl.setDescription(results.getString(results.getColumnIndex("Description")));
                        transferDtl.setQuantity(Float.valueOf(1));
                        transferDtl.setUOM(results.getString(results.getColumnIndex("UOM")));
                        transferDtl.setDocNo(transfer.getDocNo());

                        if (!CheckEmpty()) {
                            if (!CheckExist()) {
                                itemlist.add(transferDtl);
                                transfer.getTransferDtlList().add(transferDtl);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(TransferDtlAddBarcode.this, "Invalid Barcode", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                binding.trdtlEditText.removeTextChangedListener(this);
                binding.trdtlEditText.clearFocus();
                binding.trdtlEditText.requestFocus();
                hideSoftKeyboard();
                binding.trdtlEditText.setText("");
                binding.trdtlEditText.addTextChangedListener(this);

            }
        });

    }

    private boolean CheckEmpty() {
        return (transferDtl.getItemCode() == null || transferDtl.getUOM() == null );
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


    private boolean CheckExist() {
        boolean myresult = false;
        for(int i = 0; i < itemlist.size(); i++) {
            AC_Class.TransferDtl tempDtl = itemlist.get(i);

            if (tempDtl.getItemCode().equals(transferDtl.getItemCode()) && tempDtl.getUOM().equals(transferDtl.getUOM()))
            {
                Log.wtf("Item Code",transferDtl.getItemCode());
                itemlist.get(i).setQuantity(itemlist.get(i).getQuantity()+1);
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
                        Toast.makeText(TransferDtlAddBarcode.this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.trdtlEditText.setText(result.getContents());
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
            if (itemlist.size()<1) {
                Intent newIntent = new Intent();
                setResult(99, newIntent);
//                barcode2D.close();
                finish();
            } else {
                Intent newIntent = new Intent();
                newIntent.putParcelableArrayListExtra("trdtlList", itemlist);
                setResult(0, newIntent);
//                barcode2D.close();
                finish();
            }
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(TransferDtlAddBarcode.this);
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
            TransferDtlAddBarcode.this.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent();
        transfer = transfer_temp;
        newIntent.putExtra("Tr", transfer);
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
        imm.hideSoftInputFromWindow(TransferDtlAddBarcode.this.getCurrentFocus().getWindowToken(), 0);
    }

//    public void searchForItem(String barcode)
//    {
//        if (!barcode.equals("")) {
//
//            if (!barcode.equals("")) {
//                Cursor temp = db.getBarcodeItem(barcode);
//                temp.moveToFirst();
//                if (temp.getCount() != 0) {
//                    String itemCode = temp.getString(temp.getColumnIndex("ItemCode"));
//                    Log.wtf("ItemCode",itemCode);
//                    addNewItem(itemCode);
//                }
//            }
//            hideSoftKeyboard();
//        }
//    }

    public void addNewItem(String itemCode)
    {
        transferDtl = new AC_Class.TransferDtl();

        Cursor results = db.getBarcodeItemDetail(itemCode);

        trdtl_editText.setText(itemCode);

        results.moveToNext();

        transferDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
        transferDtl.setDescription(results.getString(results.getColumnIndex("Description")));
        transferDtl.setUOM(results.getString(results.getColumnIndex("BaseUOM")));
        transferDtl.setQuantity(Float.valueOf(1));
        transferDtl.setDocNo(transfer.getDocNo());

        Log.wtf("Item Code",transferDtl.getItemCode());
        Log.wtf("Description",transferDtl.getDescription());
        Log.wtf("UOM",transferDtl.getUOM());
        Log.wtf("Quantity", String.valueOf(transferDtl.getQuantity()));
        Log.wtf("StockDocNo",transferDtl.getDocNo());

        if (!CheckEmpty()) {
            if (!CheckExist()) {
                itemlist.add(transferDtl);
                transfer.getTransferDtlList().add(transferDtl);
            }

            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(TransferDtlAddBarcode.this, "Invalid Barcode", Toast.LENGTH_SHORT).show();
        }
    }

}