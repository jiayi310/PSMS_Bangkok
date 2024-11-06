package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.PlActivityPldtlAddManualBinding;
import com.example.androidmobilestock_bangkok.databinding.PplActivityPldtlAddManualBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PPL_PLDtlAddManual extends AppCompatActivity {
    ACDatabase db;
    PplActivityPldtlAddManualBinding binding;
    AC_Class.DODtl packingListDtl;
    AC_Class.DO packingList;
    private IntentIntegrator qrScan;
    String key;
    Boolean isBatchNoEnabled = true;
    Boolean isPurBatchEnabled = true;
    String salesOrderType = "";
    float baseQty = 0f;
    float balanceqty = 0f;
    String defaultBatchNo = "";
    Boolean NegativeInventory = true;
    Boolean hasBatchNoItem = false;
    NumberFormat nf = new DecimalFormat("##.###");

    MyClickHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.ppl_activity_pldtl_add_manual);

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);

        String def_loc = null;

        //Retrieve default locations
        Cursor loc = db.getReg("7");
        if (loc.moveToFirst()) {
            def_loc = loc.getString(0);
        }
        if (def_loc.equals("None")) {
            def_loc = null;
        }

        Cursor cursor5 = db.getReg("38");
        if (cursor5.moveToFirst()) {
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor4 = db.getReg("37");
        if(cursor4.moveToFirst()){
            defaultBatchNo = cursor4.getString(0);
        }

        Cursor cursor6 = db.getReg("40");
        if(cursor6.moveToFirst()){
            isPurBatchEnabled= Boolean.valueOf(cursor6.getString(0));
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        Cursor cursor9 = db.getReg("42");
        if (cursor9.moveToFirst()) {
            NegativeInventory = Boolean.valueOf(cursor9.getString(0));
        }

        packingList = getIntent().getParcelableExtra("iPackingList");

        packingListDtl = new AC_Class.DODtl(packingList.getDocNo(), def_loc);
        key = getIntent().getStringExtra("FunctionKey");

        getData();

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("PO Packing List Details - Add/Edit");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFbe5504));

        binding.setHandler(handler);
        binding.setDODtl(packingListDtl);

        Boolean hasBatch = getIntent().getBooleanExtra("hasBatch", false);

        if (isBatchNoEnabled && hasBatch) {
            binding.tvBatchno.setVisibility(View.VISIBLE);
            binding.tvBatchnoLbl.setVisibility(View.VISIBLE);
        } else {
            binding.tvBatchno.setVisibility(View.INVISIBLE);
            binding.tvBatchnoLbl.setVisibility(View.INVISIBLE);
        }

        switch (key) {
            case "New":
                binding.tvItem.setVisibility(View.INVISIBLE);
                break;
            case "Edit":
                if (packingListDtl.getBatchNo() != null) {
                    Cursor temp2;
                    temp2 = db.getStockBalanceBatchNo(packingListDtl.getItemCode(),
                            packingListDtl.getUOM(), packingListDtl.getBatchNo());
                    if (temp2.getCount() > 0) {
                        temp2.moveToNext();
                        balanceqty = temp2.getFloat(0);
                    }
                } else {
                    Cursor temp2;
                    temp2 = db.getStockBalance(packingListDtl.getItemCode(),
                            packingListDtl.getUOM());
                    if (temp2.getCount() > 0) {
                        temp2.moveToNext();
                        balanceqty = temp2.getFloat(0);
                    }
                }
                binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));
                binding.edtQty.setText(String.format("%.0f", packingListDtl.getQty()));
                binding.etBarcode.setVisibility(View.INVISIBLE);
                break;
        }

        //Barcode editText
        binding.etBarcode.requestFocus();
        binding.etBarcode.setShowSoftInputOnFocus(false);
        //hideSoftKeyboard();

        binding.edtQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String qty = binding.edtQty.getText().toString();

                if (qty.length() > 0) {
                    packingListDtl.setQty(Double.valueOf(qty));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Return from item selection

            case 4:
                AC_Class.Item item = data.getParcelableExtra("ItemsKey");
                //binding.etBarcode.setText(item.getItemCode());
                if (item != null) {
                    packingListDtl.setItemCode(item.getItemCode());
                    packingListDtl.setItemDescription(item.getDescription());
                    packingListDtl.setUOM(item.getUOM());

                    findViewById(R.id.tv_item).setVisibility(View.VISIBLE);
                    findViewById(R.id.et_barcode).setVisibility(View.INVISIBLE);

                    //stock balance
                    Cursor temp;
                    temp = db.getStockBalance(packingListDtl.getItemCode(),
                            packingListDtl.getUOM());
                    if (temp.getCount() > 0) {
                        temp.moveToNext();
                        balanceqty = temp.getFloat(0);
                    }
                    binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));

                    if (item.getHasBatchNo() != null) {
                        if (isBatchNoEnabled && item.getHasBatchNo().equals("true")) {
                            hasBatchNoItem = true;
                            binding.tvBatchno.setVisibility(View.VISIBLE);
                            binding.tvBatchnoLbl.setVisibility(View.VISIBLE);

                                if(isPurBatchEnabled) {
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
                                    binding.tvBatchno.setText(defaultBatchNo);
                                }
                        } else {
                            hasBatchNoItem = false;
                            binding.tvBatchno.setVisibility(View.INVISIBLE);
                            binding.tvBatchnoLbl.setVisibility(View.INVISIBLE);
                            packingListDtl.setBatchNo(null);
                            Intent new_intent = new Intent(PPL_PLDtlAddManual.this,
                                    PL_Location_List.class);
                            new_intent.putExtra("ItemCode", packingListDtl.getItemCode());
                            new_intent.putExtra("UOM", packingListDtl.getUOM());
                            new_intent.putExtra("DocNo", packingList.getFromDocNo());
                            startActivityForResult(new_intent, 11);
                        }
                    }

                }


                break;

            //Return from UOM selection
            case 6:
                if (data != null) {
                    AC_Class.ItemUOM itemUOM = data.getParcelableExtra("UOMKey");
                    if (itemUOM != null) {
                        packingListDtl.setUOM(itemUOM.getUOM());
                    }
                }
                break;

            case 9:
                String batchNo = data.getStringExtra("ItemBatchNo");
                if (batchNo != null) {
                    packingListDtl.setBatchNo(batchNo);
                    binding.tvBatchno.setText(batchNo);
                    if (isBatchNoEnabled) {
                        //stock balance
                        Cursor temp2;
                        temp2 = db.getStockBalanceBatchNo(packingListDtl.getItemCode(),
                                packingListDtl.getUOM(), packingListDtl.getBatchNo());
                        if (temp2.getCount() > 0) {
                            temp2.moveToNext();
                            balanceqty = temp2.getFloat(0);
                        }
                        binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));
                    }
                }

                break;

            case 10:
                if (resultCode == 1) {
                    AC_Class.Location Location = data.getParcelableExtra("LocationsKey");

                    packingListDtl.setLocation(Location.getLocation());

                } else {
                    Log.i("custDebug", "cancelled agent");
                }

                break;

            case 11:
                if (resultCode == 3) {
                    AC_Class.Location Location = data.getParcelableExtra("LocationsKey");

                    packingListDtl.setLocation(Location.getLocation());

                } else {
                    Log.i("custDebug", "cancelled agent");
                }
                if (isBatchNoEnabled && hasBatchNoItem) {
                    binding.tvBatchno.setVisibility(View.VISIBLE);
                    binding.tvBatchnoLbl.setVisibility(View.VISIBLE);
                    if(isPurBatchEnabled) {
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
                        binding.tvBatchno.setText(defaultBatchNo);
                    }
                } else {

                    hasBatchNoItem = false;
                    binding.tvBatchnoLbl.setVisibility(View.GONE);
                    binding.tvBatchno.setVisibility(View.GONE);
                    AC_Class.Location Location = data.getParcelableExtra("LocationsKey");
                    packingListDtl.setLocation(Location.getLocation());
                }
                break;

            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(PPL_PLDtlAddManual.this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.etBarcode.setText(result.getContents());

                        Intent new_intent3 = new Intent(PPL_PLDtlAddManual.this,
                                PL_Location_List.class);
                        new_intent3.putExtra("ItemCode", packingListDtl.getItemCode());
                        new_intent3.putExtra("UOM", packingListDtl.getUOM());
                        new_intent3.putExtra("DocNo", packingList.getFromDocNo());
                        startActivityForResult(new_intent3, 11);
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    private void getData() {
        switch (key) {
            case "Edit":
                packingListDtl = getIntent().getParcelableExtra("iPackingListDtl");
                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onItemSearchClicked(View view) {
            Intent new_intent = new Intent(view.getContext(), PPL_Item_List.class);
            new_intent.putExtra("substring", "");
            new_intent.putExtra("DocNo", packingList.getFromDocNo());
            startActivityForResult(new_intent, 4);
        }

        public void onItemTextViewClicked(View view) {
            findViewById(R.id.tv_item).setVisibility(View.INVISIBLE);
            findViewById(R.id.et_barcode).setVisibility(View.VISIBLE);
            findViewById(R.id.et_barcode).requestFocus();
            hideSoftKeyboard();
        }

        public void onLocationListClicked(View view) {
            if (packingListDtl.getItemCode() != null) {
//                Intent new_intent = new Intent(context, PL_Location_List.class);
//                new_intent.putExtra("ItemCode", packingListDtl.getItemCode());
//                new_intent.putExtra("UOM", packingListDtl.getUOM());
//                new_intent.putExtra("DocNo", packingList.getFromDocNo());
                Intent new_intent = new Intent(context, Location_List.class);
                startActivityForResult(new_intent, 10);
            }
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(PPL_PLDtlAddManual.this);
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                //qrScan.setBarcodeImageEnabled(false);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) {
                Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage());
            }
        }

        public void BtnIncClicked(View view) {
            packingListDtl.setQty(packingListDtl.getQty() + 1);

            binding.edtQty.setText(String.format("%.0f", packingListDtl.getQty()));
        }

        public void BtnDecClicked(View view) {
            packingListDtl.setQty(packingListDtl.getQty() <= 1 ? 1 : packingListDtl.getQty() - 1);

            binding.edtQty.setText(String.format("%.0f", packingListDtl.getQty()));
        }

        public void onUOMTxtViewClicked(View view) {
            if (packingListDtl.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemUOMList.class);
                new_intent.putExtra("ItemKey", packingListDtl.getItemCode());
                startActivityForResult(new_intent, 6);
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
            } else if (packingListDtl.getQty() > balanceqty) {

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

            } else {
                SaveData();
            }
        }

        public void OnCancelBtnClicked(View view) {
            onBackPressed();

        }
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent();
        setResult(99, newIntent);
        finish();
    }


    public boolean CheckEmpty() {
        return (packingListDtl.getItemCode() == null ||
                packingListDtl.getUOM() == null || packingListDtl.getLocation() == null);
    }


    public void SaveData() {
        Intent newIntent = new Intent();
        if (packingListDtl.getBatchNo() != null) {
            if (packingListDtl.getBatchNo().equals("")) {
                packingListDtl.setBatchNo(null);
            }
        }
        newIntent.putExtra("iPackingListDtl", packingListDtl);
        setResult(0, newIntent);
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


    public void hideSoftKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }
}