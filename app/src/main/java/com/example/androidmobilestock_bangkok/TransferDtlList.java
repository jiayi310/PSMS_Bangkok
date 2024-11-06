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

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.ActivityTransferDtlListBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransferDtlList extends AppCompatActivity {
    ActivityTransferDtlListBinding binding;
    AC_Class.Transfer transfer, transfer_temp;
    AC_Class.TransferDtl tempDtl;
    ACDatabase db;

    MyClickHandler handler;
    TransferDtlAdapter adapter;
    String typeFP;
    Button btnMerge;
    private IntentIntegrator qrScan;
    AC_Class.TransferDtl transferDtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_dtl_list);
        //invoiceDetails = new AC_Class.InvoiceDetails();

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);
        binding.setHandler(handler);
        btnMerge = findViewById(R.id.btnMerge);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Transfer Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf98b88));

        transfer = getIntent().getParcelableExtra("TransferHeader");
        transfer_temp = getIntent().getParcelableExtra("TransferHeader");
        typeFP = getIntent().getStringExtra("FunctionKey");
        //transferDtl.setDocNo(transfer.getDocNo());
        binding.setTr(transfer);
        getTransferDetailList();
        binding.invdtlEditText.requestFocus();
        binding.lvTransferDtl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TransferDtlList.this);
                builder.setMessage("What do you want to do?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transfer.removeTransferDetail(position);
                        getTransferDetailList();
                    }
                });

                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Integer id = ((AC_Class.TransferDtl)parent.getItemAtPosition(position)).getID();
                        tempDtl = (AC_Class.TransferDtl) parent.getItemAtPosition(position);

                        Intent intent = new Intent(TransferDtlList.this, TransferDtlAddManual.class);
                        intent.putExtra("Tr", transfer);
                        intent.putExtra("TrDtl", tempDtl);
                        intent.putExtra("FunctionKey", "Edit");
                        intent.putExtra("mode", 0);

                        transfer.removeTransferDetail(position);

                        startActivityForResult(intent, 1);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        binding.invdtlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
//                startScan();
                String temp = s.toString();

                if (!temp.equals("") && !TextUtils.isEmpty(binding.invdtlEditText.getText())) {
                    Cursor results = db.getItemBC(temp);
                    if (results.getCount() == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(TransferDtlList.this).create();
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
                        results.moveToNext();

                        transferDtl = new AC_Class.TransferDtl();
                        transferDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                        transferDtl.setDescription(results.getString(results.getColumnIndex("Description")));
                        transferDtl.setQuantity(Float.valueOf(1));
                        transferDtl.setUOM(results.getString(results.getColumnIndex("UOM")));
                        transferDtl.setDocNo(transfer.getDocNo());


                        if (!CheckExist()) {
                            transfer.getTransferDtlList().add(transferDtl);
                        }

                        adapter.notifyDataSetChanged();

                        if (adapter.getCount() > 0)
                            binding.lvTransferDtl.setSelection(adapter.getCount() - 1);
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

    public void getTransferDetailList() {
        adapter = new TransferDtlAdapter(this, transfer.getTransferDtlList());
        binding.lvTransferDtl.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getSummary();
        btnMerge.setText("Merge (" + Integer.toString(transfer.getTransferDtlList().size()) + ")");

        if (adapter.getCount() > 0)
            binding.lvTransferDtl.setSelection(adapter.getCount() - 1);
    }

    public void getSummary() {
        float totalQty = 0.0f;

        for (int i = 0; i < transfer.getTransferDtlList().size(); i++) {
            totalQty += transfer.getTransferDtlList().get(i).getQuantity();
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
                    tempDtl = data.getParcelableExtra("transferDtl");
                    //Log.i("custDebug", "Item added: "+temp.getItemDescription());
                } else {
                    //Log.i("custDebug", "Add item cancelled");
                }
                if (tempDtl != null) {
                    transfer.getTransferDtlList().add(tempDtl);
                    tempDtl = null;
                }
                break;
            //AddBarcode
            case 2:
                if (resultCode == 0) {
                    List<AC_Class.TransferDtl> transferDtlList =
                            data.getParcelableArrayListExtra("trdtlList");

                    for (int i = 0; i < transferDtlList.size(); i++) {
                        //transferDtlList.get(i).setDocNo(transfer.getDocNo());
                        transfer.getTransferDtlList().add(transferDtlList.get(i));
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
        getTransferDetailList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Tr", transfer);
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
                    qrScan.setBeepEnabled(true);
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
                final Intent intent = new Intent(TransferDtlList.this,
                        TransferDtlAddManual.class);
                intent.putExtra("Tr", transfer);
                intent.putExtra("FunctionKey", "New");
                intent.putExtra("mode", 0);
                startActivityForResult(intent, 1);
                break;

//                            case 1: //Auto
//                                //Open Auto
//                                final Intent intent2 = new Intent(TransferDtlList.this,
//                                        TransferDtlAddBarcode.class);
//                                intent2.putExtra("Tr", transfer);
//                                intent2.putExtra("TrHeaderTemp",transfer_temp);
//                                intent2.putExtra("FunctionKey", "New");
//                                intent2.putExtra("mode", 1);
//                                startActivityForResult(intent2, 2);
//                                break;

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
            if (transfer.getTransferDtlList().size() > 0) {
                db.saveTransfer(transfer, typeFP);

                // Broadcast intent to close other activities
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);

                finish();
            } else {
                Toast.makeText(TransferDtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }

        public void OnbtnMergeClicked(View view) {
            if (transfer.getTransferDtlList().size() > 0) {
                List<AC_Class.TransferDtl> oriDetail = transfer.getTransferDtlList();
                ArrayList<AC_Class.TransferDtl> newDetail = new ArrayList<>();

                for (int i = 0; i < oriDetail.size(); i++) {
                    boolean result = false;

                    for (AC_Class.TransferDtl myDtl : newDetail) {
                        if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM())) {
                            myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                            result = true;
                        }
                    }

                    if (!result) {
                        newDetail.add(oriDetail.get(i));
                    }
                }

                transfer.getTransferDtlList().clear();
                transfer.setTransferDtlList(newDetail);

                getTransferDetailList();
            }
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean CheckExist() {
        boolean myresult = false;
        for(int i = 0; i < transfer.getTransferDtlList().size(); i++) {
            AC_Class.TransferDtl tempDtl = transfer.getTransferDtlList().get(i);

            if (tempDtl.getItemCode().equals(transferDtl.getItemCode()) && tempDtl.getUOM().equals(transferDtl.getUOM()))
            {
                Log.wtf("Item Code",transferDtl.getItemCode());
                transfer.getTransferDtlList().get(i).setQuantity(transfer.getTransferDtlList().get(i).getQuantity()+1);
                myresult = true;
            }
        }
        return myresult;
    }
}