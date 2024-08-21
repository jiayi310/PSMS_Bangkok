package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
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

import com.example.androidmobilestock.databinding.ActivityStockTakeDtlAddManualBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StockTakeDtlAddManual extends AppCompatActivity {
    ACDatabase db;
    ActivityStockTakeDtlAddManualBinding binding;
    AC_Class.StockTakeDetails std;
    AC_Class.StockTake st;
    private IntentIntegrator qrScan;
    String key;
    EditText edtSTQty;
    MyClickHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_take_dtl_add_manual);

        st = getIntent().getParcelableExtra("STHeader");
        Log.wtf("DocNo:", st.getDocNo());
        Log.wtf("TimeStamp:", st.getCreatedTimeStamp());
        Log.wtf("DocDate:", st.getDocDate());
        Log.wtf("Agent:", st.getAgent());
        Log.wtf("Location:", st.getLocation());
        Log.wtf("Remarks:", st.getRemarks());
        Log.wtf("Uploaded:", String.valueOf(st.getUploaded()));
        key = getIntent().getStringExtra("FunctionKey");

        std = new AC_Class.StockTakeDetails(st.getDocNo());
        std.setStockDocNo(st.getDocNo());
        std.setQuantity(Double.valueOf(binding.edtTransferQty.getText().toString()));

        edtSTQty = (EditText) findViewById(R.id.edtTransferQty);

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);

        getData();

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Stock Take Details - Add/Edit");

        binding.setHandler(handler);
        binding.setStockTakeDtl(std);

        switch (key){
            case "New":
                binding.tvItem.setVisibility(View.INVISIBLE);
                break;
            case "Edit":
                Log.wtf("QTY Text ",String.format("%.0f", std.getQuantity()));
                edtSTQty.setText(String.format("%.0f", std.getQuantity()));
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
                edtSTQty = (EditText) findViewById(R.id.edtTransferQty);

                String qty = edtSTQty .getText().toString();

                if(qty.length() > 0)
                {
                    std.setQuantity(Double.valueOf(qty));
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
                        std.setItemCode(data.getString(data.getColumnIndex("ItemCode")));
                        std.setItemDescription(data.getString(data.getColumnIndex("Description")));
                        std.setUOM(data.getString(data.getColumnIndex("UOM")));

                        binding.etBarcode.setVisibility(View.INVISIBLE);
                        binding.tvItem.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(StockTakeDtlAddManual.this, "Product not found",
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
                    std.setItemCode(item.getItemCode());
                    std.setItemDescription(item.getDescription());
                    std.setUOM(item.getUOM());

                    findViewById(R.id.tv_item).setVisibility(View.VISIBLE);
                    findViewById(R.id.et_barcode).setVisibility(View.INVISIBLE);
                }
                break;

            //Return from UOM selection
            case 6:
                if(data != null) {
                    AC_Class.ItemUOM itemUOM = data.getParcelableExtra("UOMKey");
                    if (itemUOM != null) {
                        std.setUOM(itemUOM.getUOM());
                    }
                }
                break;

            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(StockTakeDtlAddManual.this, "No result found.",
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
                std = getIntent().getParcelableExtra("TrDtl");
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
                qrScan = new IntentIntegrator(StockTakeDtlAddManual.this);
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
            std.setQuantity(std.getQuantity() + 1);

            edtSTQty = (EditText) findViewById(R.id.edtTransferQty);
            edtSTQty.setText(String.format("%.0f",std.getQuantity()));
        }

        public void BtnDecClicked(View view) {
            std.setQuantity(std.getQuantity() <= 1 ? 1 :std.getQuantity() - 1);

            edtSTQty = (EditText) findViewById(R.id.edtTransferQty);
            edtSTQty.setText(String.format("%.0f",std.getQuantity()));
        }

        public void onUOMTxtViewClicked(View view) {
            if (std.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemUOMList.class);
                new_intent.putExtra("ItemKey", std.getItemCode());
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
            } else {
                SaveData();
            }
        }

        public void OnCancelBtnClicked(View view) {
            onBackPressed();
        }
    }

    public boolean CheckEmpty() {
        return (std.getItemCode() == null ||
                std.getUOM() == null);
    }

    public void SaveData()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        Intent newIntent = new Intent();
        std.setCreatedTimeStamp(String.valueOf(dateFormat.format(date)));
        Log.wtf("Date",std.getCreatedTimeStamp());
        newIntent.putExtra("StockDtl", std);
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
        if(getCurrentFocus()!=null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }
}