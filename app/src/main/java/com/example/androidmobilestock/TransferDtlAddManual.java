package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityTransferDtlAddManualBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class TransferDtlAddManual extends AppCompatActivity {
    ACDatabase db;
    ActivityTransferDtlAddManualBinding binding;
    AC_Class.TransferDtl transferDtl;
    AC_Class.Transfer transfer;
    private IntentIntegrator qrScan;
    String key;
    EditText edtTransferQty;
    MyClickHandler handler;
    Boolean isBatchNoEnabled = true;
    Boolean isSaleBatchEnabled = true;
    String salesOrderType = "";
    Boolean hasBatchNoItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_dtl_add_manual);

        transfer = getIntent().getParcelableExtra("Tr");
        key = getIntent().getStringExtra("FunctionKey");

        transferDtl = new AC_Class.TransferDtl(transfer.getDocNo());
        edtTransferQty = (EditText) findViewById(R.id.edtTransferQty);
        transferDtl.setQuantity(Float.valueOf(binding.edtTransferQty.getText().toString()));

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);

        getData();

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Transfer Details - Add/Edit");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf98b88));

        binding.setHandler(handler);
        binding.setTransferDtl(transferDtl);

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

        switch (key){
            case "New":
                binding.tvItem.setVisibility(View.INVISIBLE);
                break;
            case "Edit":
                Log.wtf("QTY Text ",String.format("%.0f", transferDtl.getQuantity()));
                edtTransferQty.setText(String.format("%.0f", transferDtl.getQuantity()));
                binding.etBarcode.setVisibility(View.INVISIBLE);
                break;
        }

        //Barcode editText
        binding.etBarcode.requestFocus();
        binding.etBarcode.setShowSoftInputOnFocus(false);
        //hideSoftKeyboard();

        binding.edtTransferQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtTransferQty = (EditText) findViewById(R.id.edtTransferQty);

                String qty = edtTransferQty.getText().toString();

                if(qty.length() > 0)
                {
                    transferDtl.setQuantity(Float.valueOf(qty));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String temp = s.toString();
                    Cursor data = db.getItemBC(temp);
                    if (data.moveToNext()) {
                        transferDtl.setItemCode(data.getString(data.getColumnIndex("ItemCode")));
                        transferDtl.setDescription(data.getString(data.getColumnIndex("Description")));
                        transferDtl.setUOM(data.getString(data.getColumnIndex("UOM")));

                        binding.etBarcode.setVisibility(View.INVISIBLE);
                        binding.tvItem.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(TransferDtlAddManual.this, "Product not found",
                                Toast.LENGTH_SHORT).show();
                    }
                    binding.etBarcode.removeTextChangedListener(this);
                    binding.etBarcode.clearFocus();
                    binding.etBarcode.setText("");
                    binding.etBarcode.addTextChangedListener(this);
                    binding.etBarcode.requestFocus();
                    hideSoftKeyboard();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Return from item selection
            case 4:
                AC_Class.Item item = data.getParcelableExtra("ItemsKey");
                if (item != null) {
                    transferDtl.setItemCode(item.getItemCode());
                    transferDtl.setDescription(item.getDescription());
                    transferDtl.setUOM(item.getUOM());

                    findViewById(R.id.tv_item).setVisibility(View.VISIBLE);
                    findViewById(R.id.et_barcode).setVisibility(View.INVISIBLE);


                    if (item.getHasBatchNo() != null) {
                        if (isBatchNoEnabled && item.getHasBatchNo().equals("true")) {
                            hasBatchNoItem = true;
                            binding.batchnoList.setVisibility(View.VISIBLE);
                            binding.tvBatchno.setVisibility(View.VISIBLE);
                            binding.lblBatchNo.setVisibility(View.VISIBLE);
                            if (isSaleBatchEnabled) {

                                if (salesOrderType != "0" || salesOrderType != null) {

                                    if (salesOrderType.equals("Latest Manufacture Date")) {

                                        Cursor data2 = db.getLateManuBatch(item.getItemCode(),
                                                item.getUOM(), transfer.getLocationFrom());

                                        if (data2.moveToFirst()) {

                                            transferDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            transferDtl.setBatchNo(null);
                                        }
                                    } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                                        Cursor data2 = db.getEarManuBatch(item.getItemCode(),
                                                item.getUOM(), transfer.getLocationFrom());

                                        if (data2.moveToFirst()) {

                                            transferDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            transferDtl.setBatchNo(null);
                                        }
                                    } else if (salesOrderType.equals("Latest Expiration Date")) {
                                        Cursor data2 = db.getLateExpBatch(item.getItemCode(),
                                                item.getUOM(), transfer.getLocationFrom());

                                        if (data2.moveToFirst()) {

                                            transferDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            transferDtl.setBatchNo(null);
                                        }
                                    } else if (salesOrderType.equals("Earliest Expiration Date")) {
                                        Cursor data2 = db.getEarExpBatch(item.getItemCode(),
                                                item.getUOM(), transfer.getLocationFrom());

                                        if (data2.moveToFirst()) {

                                            transferDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            transferDtl.setBatchNo(null);
                                        }
                                    }

                                } else {
                                    Cursor data2 = db.getEarManuBatch(item.getItemCode(),
                                            item.getUOM(),transfer.getLocationFrom());

                                    if (data2.moveToFirst()) {

                                        transferDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                    } else {
                                        transferDtl.setBatchNo(null);
                                    }
                                    transferDtl.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                }


                            } else {
                                Intent new_intent = new Intent(getApplicationContext(), ItemBatchList.class);
                                new_intent.putExtra("ItemCode", transferDtl.getItemCode());
                                new_intent.putExtra("UOM", transferDtl.getUOM());
                                new_intent.putExtra("Location", transfer.getLocationFrom());
                                startActivityForResult(new_intent, 9);
                            }


                            binding.tvBatchno.setText(transferDtl.getBatchNo());
                        } else {
                            hasBatchNoItem = false;
                            binding.tvBatchno.setVisibility(View.INVISIBLE);
                            binding.lblBatchNo.setVisibility(View.INVISIBLE);
                            binding.batchnoList.setVisibility(View.INVISIBLE);
                            transferDtl.setBatchNo(null);
                        }
                    }
                }
                break;

            //Return from UOM selection
            case 6:
                if(data != null) {
                    AC_Class.ItemUOM itemUOM = data.getParcelableExtra("UOMKey");
                    if (itemUOM != null) {
                        transferDtl.setUOM(itemUOM.getUOM());
                    }
                }
                break;

            case 9:
                String batchNo = data.getStringExtra("ItemBatchNo");
                if(batchNo!=null) {
                    transferDtl.setBatchNo(batchNo);
                    binding.tvBatchno.setText(batchNo);
                }

                break;

            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(TransferDtlAddManual.this, "No result found.",
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

    private void getData()
    {
        switch (key) {
            case "Edit":
                transferDtl = getIntent().getParcelableExtra("TrDtl");

                if (transferDtl.getBatchNo() != null && !transferDtl.getBatchNo().equals("")) {
                    binding.batchnoList.setVisibility(View.VISIBLE);
                    binding.tvBatchno.setVisibility(View.VISIBLE);
                    binding.lblBatchNo.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onItemSearchClicked(View view) {
            Intent new_intent = new Intent(view.getContext(), Item_List.class);
            new_intent.putExtra("key","FromStockTake");
            new_intent.putExtra("substring", "");
            startActivityForResult(new_intent, 4);
        }

        public void onItemTextViewClicked(View view)
        {
            findViewById(R.id.tv_item).setVisibility(View.INVISIBLE);
            findViewById(R.id.et_barcode).setVisibility(View.VISIBLE);
            findViewById(R.id.et_barcode).requestFocus();
            hideSoftKeyboard();
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(TransferDtlAddManual.this);
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                //qrScan.setBarcodeImageEnabled(false);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void BtnIncClicked(View view) {
            transferDtl.setQuantity(transferDtl.getQuantity() + 1);

            edtTransferQty = (EditText) findViewById(R.id.edtTransferQty);
            edtTransferQty.setText(String.format("%.0f",transferDtl.getQuantity()));
        }

        public void BtnDecClicked(View view) {
            transferDtl.setQuantity(transferDtl.getQuantity() <= 1 ? 1 : transferDtl.getQuantity() - 1);

            edtTransferQty = (EditText) findViewById(R.id.edtTransferQty);
            edtTransferQty.setText(String.format("%.0f",transferDtl.getQuantity()));
        }

        public void onUOMTxtViewClicked(View view) {
            if (transferDtl.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemUOMList.class);
                new_intent.putExtra("ItemKey", transferDtl.getItemCode());
                startActivityForResult(new_intent, 6);
            }
        }

        public void onBatchNoListClicked(View view) {
            if (transferDtl.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemBatchList.class);
                new_intent.putExtra("ItemCode", transferDtl.getItemCode());
                new_intent.putExtra("UOM", transferDtl.getUOM());
                new_intent.putExtra("Location", transfer.getLocationFrom());
                startActivityForResult(new_intent, 9);
            }
        }

        public void OnSaveBtnClicked(View view) {
            if (CheckEmpty()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please make sure that all the details have been filled.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                if (isBatchNoEnabled && hasBatchNoItem) {
                    String batch = binding.tvBatchno.getText().toString();
                    if (batch == null || batch.equals(""))
                        Toast.makeText(context, "Please fill in batch no",
                                Toast.LENGTH_SHORT).show();
                    else
                        SaveData();
                } else {
                    SaveData();
                }
            }
        }

        public void OnCancelBtnClicked(View view) {
            onBackPressed();
        }
    }

    public boolean CheckEmpty() {
        return (transferDtl.getItemCode() == null ||
                transferDtl.getUOM() == null);
    }

    public void SaveData()
    {
        Intent newIntent = new Intent();
        newIntent.putExtra("transferDtl", transferDtl);
        setResult(0, newIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent();
        setResult(99, newIntent);
        finish();
    }

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
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }
}