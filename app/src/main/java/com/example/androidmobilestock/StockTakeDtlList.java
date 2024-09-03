package com.example.androidmobilestock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.androidmobilestock.activity.StockTakeDtlAddRFID;
import com.zebra.adc.decoder.Barcode2DWithSoft;
import com.example.androidmobilestock.databinding.ActivityStockTakeDetailBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StockTakeDtlList extends AppCompatActivity {

    ActivityStockTakeDetailBinding binding;
    public MyClickHandler StdHandler;
    private AC_Class.StockTake st, st_temp;
    AC_Class.StockTakeDetails tempDtl;
    AC_Class.StockTakeDetails stDtl;
    List<AC_Class.StockTakeDetails> checkDtl = new ArrayList<>();
    private AC_Class.StockTakeDetails std;
    ArrayList<String> myModules = new ArrayList<String>();
    private IntentIntegrator qrScan;
    StockTakeDtlAdapter adapter;
    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;
    private Barcode2DWithSoft barcode2D;

    ACDatabase db;

    String typeFP;

    boolean isMerged = false;
    Boolean isBatchNoEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(StockTakeDtlList.this, R.layout.activity_stock_take_detail);


        st = getIntent().getParcelableExtra("STHeader");
        st_temp = getIntent().getParcelableExtra("STHeader");

        db = new ACDatabase(this);
        StdHandler = new StockTakeDtlList.MyClickHandler(StockTakeDtlList.this);
        binding.setStdHandler(StdHandler);
        checkDtl = new ArrayList<>();

        Cursor cursor = db.getReg("38");
        if(cursor.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor.getString(0));
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

        Cursor data8 = db.getModules();
        if (data8.getCount() > 0) {
            while (data8.moveToNext()) {
                int myIndex = data8.getColumnIndex("Name");
                if (myIndex >= 0) {
                    myModules.add(data8.getString(myIndex));
                }
            }
        }



        typeFP = getIntent().getStringExtra("FunctionKey");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stock Count");
        actionBar.setDisplayHomeAsUpEnabled(true);
        getStockTakeDetailList();

        binding.stdListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StockTakeDtlList.this);
                builder.setMessage("What do you want to do?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        st.removeStockTakeDetail(position);
                        getStockTakeDetailList();
                    }
                });

                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Integer id = ((AC_Class.TransferDtl)parent.getItemAtPosition(position)).getID();
                        tempDtl = (AC_Class.StockTakeDetails) parent.getItemAtPosition(position);

                        Intent intent = new Intent(StockTakeDtlList.this, StockTakeDtlAddManual.class);
                        intent.putExtra("STHeader", st);
                        intent.putExtra("TrDtl", tempDtl);
                        intent.putExtra("FunctionKey", "Edit");
                        intent.putExtra("mode", 0);

                        st.removeStockTakeDetail(position);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                double myQty = 1;

                if (!temp.equals("") && !TextUtils.isEmpty(binding.invdtlEditText.getText())) {

                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled)
                    {
                        if (temp.length() == barcodeLength)
                        {
                            if (isNumeric(temp)) {
                                myQty = Double.valueOf(temp.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                if (qtyDecimal > 0) {
                                    myQty = myQty / Math.pow(10,qtyDecimal);
                                }
                                temp = temp.substring(itemStart - 1, itemStart - 1 + itemLength);
                            } else {
                                Toast.makeText(StockTakeDtlList.this, "Invalid Barcode.",
                                        Toast.LENGTH_SHORT).show();
                                isSkip = true;
                            }
                        }
                    }

                    if (!isSkip) {
                        Cursor results = db.getItemBC(temp);
                        if (results.getCount() == 0) {
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            AlertDialog alertDialog = new AlertDialog.Builder(StockTakeDtlList.this).create();
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

                            stDtl = new AC_Class.StockTakeDetails();

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                            Date date = new Date();

                            int myItemCodeIndex = results.getColumnIndex("ItemCode");
                            if (myItemCodeIndex >= 0) {
                                stDtl.setItemCode(results.getString(myItemCodeIndex));
                            }

                            int myDescriptionIndex = results.getColumnIndex("Description");
                            if (myDescriptionIndex >= 0) {
                                stDtl.setItemDescription(results.getString(myDescriptionIndex));
                            }

                            int myUOMIndex = results.getColumnIndex("UOM");
                            if (myUOMIndex >= 0) {
                                stDtl.setUOM(results.getString(myUOMIndex));
                            }

                            stDtl.setQuantity(myQty);
                            stDtl.setStockDocNo(st.getDocNo());
                            stDtl.setCreatedTimeStamp(dateFormat.format(date));

                            if (!CheckEmpty()) {
                                if (!CheckExist(myQty)) {
                                    checkDtl.add(stDtl);
                                  //  st.getStockTakeDtlList().add(stDtl);
                                }

                                adapter.notifyDataSetChanged();

                                if (adapter.getCount() > 0)
                                    binding.stdListview.setSelection(adapter.getCount() - 1);
                            } else {
                                Toast.makeText(StockTakeDtlList.this, "Invalid Barcode", Toast.LENGTH_SHORT).show();
                            }
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

        if (android.os.Build.MODEL.equals("HC720")) {
            try {
                barcode2D = Barcode2DWithSoft.getInstance();
            } catch (Exception ex) {
                Toast.makeText(StockTakeDtlList.this, ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        if (android.os.Build.MODEL.equals("HC720")) {
            initSound();
        }

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
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //            builder.setIcon(android.R.drawable.ic_dialog_alert);
        //            builder.setTitle("Attention!");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StockTakeDtlList.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.MODEL.equals("HC720")) {
            while (keyCode == 139 || keyCode == 293) {
                if (event.getRepeatCount() == 0) {
                    startScan();
                } else {
                    barcode2D.stopScan();
                    break;
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void displayMergeDialog(ArrayList<String> i_temp) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        isMerged = false;
        String itemCodes = "";

        for (String i : i_temp) {
            itemCodes += i;
            itemCodes += "\n";
        }

        builder.setMessage("Below item code(s) are duplicated and required to be merged: \n\n" +
                itemCodes +
                "\nClick 'Cancel' to remove item code manually, or 'Merge' to proceed.");

        builder.setPositiveButton("Merge", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mergeItemCode();
                isMerged = true;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isMerged = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    MenuItem myRFID;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inv_menu2, menu);

        myRFID = menu.findItem(R.id.addStockCountRFID);

        getPermission();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Return from item
            //AddManual
            case 1:
                if (resultCode == 0) {
                    tempDtl = data.getParcelableExtra("StockDtl");
                    //Log.i("custDebug", "Item added: "+temp.getItemDescription());
                }
                if (tempDtl != null) {
                    st.getStockTakeDtlList().add(tempDtl);
                    tempDtl = null;
                }
                break;
            //AddBarcode
            case 2:
                if (resultCode == 0) {
                    List<AC_Class.StockTakeDetails> stdDtlList =
                            data.getParcelableArrayListExtra("stDtlList");

                    for (int i = 0; i < stdDtlList.size(); i++) {
                        //transferDtlList.get(i).setDocNo(transfer.getDocNo());
                        st.getStockTakeDtlList().add(stdDtlList.get(i));
                    }
                } else {

                }
                break;
            case 3:
                if (resultCode == 0) {
                    ArrayList<AC_Class.StockTakeDetails> myDetailList = (ArrayList<AC_Class.StockTakeDetails>) (data.getSerializableExtra("stDtlList"));
                    for (AC_Class.StockTakeDetails myDetail : myDetailList) {
                        st.getStockTakeDtlList().add(myDetail);
                    }
                }
                if (resultCode == 99) {
                    //st = data.getParcelableExtra("STHeader");
                    //st_temp = data.getParcelableExtra("STHeader");
                }
                if (tempDtl != null) {
                    //st.getStockTakeDtlList().add(tempDtl);
                    //getStockTakeDetailList();
                    //tempDtl = null;
                }
                break;
            case 4:
                AC_Class.Item item = data.getParcelableExtra("ItemsKey");
                if(item != null) {

                        std = new AC_Class.StockTakeDetails();
                        if (item != null) {
                            std.setItemCode(item.getItemCode());
                            std.setItemDescription(item.getDescription());
                            std.setUOM(item.getUOM());
                        }
                    if(isBatchNoEnabled ) {
                        if(item.getHasBatchNo().equals("true")) {
                            Intent new_intent = new Intent(StockTakeDtlList.this, ItemBatchList.class);
                            new_intent.putExtra("ItemCode", item.getItemCode());
                            new_intent.putExtra("DocNo", st.getDocNo());
                            new_intent.putExtra("Key", "FromStockTake");
                            // new_intent.putExtra("Loc", st.getLocation());
                            startActivityForResult(new_intent, 9);
                        }else{
                            Intent new_intent = new Intent(StockTakeDtlList.this, StockTakeUOM.class);
                            new_intent.putExtra("ItemCode", std.getItemCode());
                            new_intent.putExtra("DocNo", st.getDocNo());
                            new_intent.putExtra("Key", "FromStockTake");
                            // new_intent.putExtra("Loc", st.getLocation());
                            startActivityForResult(new_intent, 5);
                        }
                    }else{
                        Intent new_intent = new Intent(StockTakeDtlList.this, StockTakeUOM.class);
                        new_intent.putExtra("ItemCode", std.getItemCode());
                        new_intent.putExtra("DocNo", st.getDocNo());
                        new_intent.putExtra("Key", "FromStockTake");
                        // new_intent.putExtra("Loc", st.getLocation());
                        startActivityForResult(new_intent, 5);
                    }
                }

                break;
            case 5:
                ArrayList<AC_Class.StockTakeDetails> listUOM = new ArrayList<>();
                if (resultCode == 5) {
                    listUOM = data.getExtras().getParcelableArrayList("StockDtl");
                    //Log.i("custDebug", "Item added: "+temp.getItemDescription());
                }
                if (listUOM != null) {
                    for (int i =0;i<listUOM.size(); i++){
                        if(listUOM.get(i).getQuantity()>=0) {
                            st.getStockTakeDtlList().add(listUOM.get(i));
                        }
                    }
                }
                break;

            case 9:
                String item2 = data.getStringExtra("ItemBatchNo");
                if(item2 != null) {
                    std.setBatchNo(item2);

                    Intent new_intent = new Intent(StockTakeDtlList.this, StockTakeUOM.class);
                    new_intent.putExtra("ItemCode", std.getItemCode());
                    new_intent.putExtra("DocNo", st.getDocNo());
                    new_intent.putExtra("Key", "FromStockTake");
                    new_intent.putExtra("BatchNo", item2);
                    // new_intent.putExtra("Loc", st.getLocation());
                    startActivityForResult(new_intent, 5);
                }
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
        getStockTakeDetailList();

        super.onResume();

        if (barcode2D != null) {
            try {
                if (android.os.Build.MODEL.equals("HC720")) {
                    boolean result = barcode2D.open(StockTakeDtlList.this);
                    if (result) {
                        barcode2D.setParameter(324, 1);
                        barcode2D.setParameter(300, 0); // Snapshot Aiming
                        barcode2D.setParameter(361, 0); // Image Capture Illumination
                    }
                }
            }catch (Exception ex) {

                Toast.makeText(StockTakeDtlList.this, ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            case (R.id.addStockCount): {
                Intent new_intent = new Intent(StockTakeDtlList.this, Item_List.class);
                new_intent.putExtra("key","FromStockTake");
                new_intent.putExtra("substring", "");
                startActivityForResult(new_intent, 4);
//                final Intent intent = new Intent(StockTakeDtlList.this,
//                        StockTakeDtlAddManual.class);
//                intent.putExtra("FunctionKey", "New");
//                intent.putExtra("STHeader", st);
//                intent.putExtra("mode", 0);
//                startActivityForResult(intent, 1);
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
                break;
            }

            case (R.id.addStockCountRFID): {
                final Intent intent3 = new Intent(StockTakeDtlList.this, StockTakeDtlAddRFID.class);
                intent3.putExtra("STHeader",st);
                startActivityForResult(intent3, 3);
                break;
            }
        }
        return false;
    }

    public void getStockTakeDetailList() {
        adapter = new StockTakeDtlAdapter(this, st.getStockTakeDtlList());
        binding.stdListview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getSummary();

        if (adapter.getCount() > 0)
            binding.stdListview.setSelection(adapter.getCount() - 1);
    }

    public void getSummary() {
        float totalQty = 0.0f;

        for (int i = 0; i < st.getStockTakeDtlList().size(); i++) {
            totalQty += st.getStockTakeDtlList().get(i).getQuantity();
        }
        binding.lblTotal3.setText(new DecimalFormat("#.###").format(totalQty));
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
            ArrayList<String> ic = new ArrayList<String>();
            ArrayList<String> ic_duplicated = new ArrayList<String>();

            //            builder.setIcon(android.R.drawable.ic_dialog_alert);
            //            builder.setTitle("Attention!");
            if(isBatchNoEnabled){
                for (AC_Class.StockTakeDetails i : st.getStockTakeDtlList()) {
                    String str = i.getItemCode() + ", " + i.getUOM() + ", " + i.getBatchNo();
                    if (!ic.contains(str)) {
                        ic.add(str);
                    } else {
                        ic_duplicated.add(str);
                    }
                }
            }else {
                for (AC_Class.StockTakeDetails i : st.getStockTakeDtlList()) {
                    String str = i.getItemCode() + ", " + i.getUOM();
                    if (!ic.contains(str)) {
                        ic.add(str);
                    } else {
                        ic_duplicated.add(str);
                    }
                }
            }

            if (ic_duplicated.size() > 0) {
                displayMergeDialog(ic_duplicated);
            } else {
                if (st.getStockTakeDtlList().size() > 0) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    Date date = new Date();

                    st.setLastModifiedDateTime(dateFormat.format(date));
                    checkDtl = st.getStockTakeDtlList();
                    st.setUploaded(0);
                    db.saveStockTake(st, typeFP);

                    // Broadcast intent to close other activities
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                    sendBroadcast(broadcastIntent);
//                    Intent home = new Intent(StockTakeDtlList.this, StockCountHome.class);
//                    startActivity(home);
                    finish();
                }
            }
        }

        public void OnbtnMergeClicked(View view) {
            mergeItemCode();
        }

    }

    public void mergeItemCode() {
        if (st.getStockTakeDtlList().size() > 0) {
            List<AC_Class.StockTakeDetails> oriDetail = st.getStockTakeDtlList();
            ArrayList<AC_Class.StockTakeDetails> newDetail = new ArrayList<>();

            for (int i = 0; i < oriDetail.size(); i++) {
                boolean result = false;

                for (AC_Class.StockTakeDetails myDtl : newDetail) {
                    if(isBatchNoEnabled){
                        if(oriDetail.get(i).getBatchNo()!=null & myDtl.getBatchNo()!=null) {
                            if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM()) && oriDetail.get(i).getBatchNo().equals(myDtl.getBatchNo())) {
                                myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                                result = true;
                            }
                        }else{
                            if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM())) {
                                myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                                result = true;
                            }
                        }
                    }else {
                        if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) && oriDetail.get(i).getUOM().equals(myDtl.getUOM())) {
                            myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                            result = true;
                        }
                    }
                }

                if (!result) {
                    newDetail.add(oriDetail.get(i));
                }
            }

            st.getStockTakeDtlList().clear();
            st.setStockTakeDtlList(newDetail);

            getStockTakeDetailList();
        }
    }

    public void getPermission() {

        if (android.os.Build.MODEL.equals("HC720")) {

            Cursor data = db.getSelectedPermission("RFID Permission");
            data.moveToNext();

            if (data.getString(0).equals("Granted")) {
                myRFID.setVisible(true);
            }

        }
    }

    public void startScan()
    {
        final String[] bc = new String[1];

        barcode2D.scan();
        barcode2D.setScanCallback(new Barcode2DWithSoft.ScanCallback() {
            @Override
            public void onScanComplete(int i, int i1, byte[] bytes) {
                if (bytes != null) {
                    String barcode = new String(bytes);
                    binding.invdtlEditText.setText(barcode);
                    StockTakeDtlList.this.playSound(1);
                }
                else
                {
                    StockTakeDtlList.this.playSound(2);
                }
            }
        });
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;

    private void initSound(){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }

    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, // 右声道音量
                    1, // 优先级，0为最低
                    0, // 循环次数，0无不循环，-1无永远循环
                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );
        } catch (Exception e) {
            e.printStackTrace();

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
    private boolean CheckEmpty() {
        return (stDtl.getItemCode() == null || stDtl.getUOM() == null );
    }
    private boolean CheckExist(Double myQtyFP) {
        boolean myresult = false;
        checkDtl = st.getStockTakeDtlList();
        for(int i = 0; i < checkDtl.size(); i++) {
            AC_Class.StockTakeDetails tempDtl = checkDtl.get(i);

            if (tempDtl.getItemCode().equals(stDtl.getItemCode()) && tempDtl.getUOM().equals(stDtl.getUOM()))
            {
                if (isCustomBarcodeEnabled)
                    checkDtl.get(i).setQuantity(checkDtl.get(i).getQuantity()+myQtyFP);
                else
                    checkDtl.get(i).setQuantity(checkDtl.get(i).getQuantity()+1);
                myresult = true;
                break;
            }
        }
        return myresult;
    }
}