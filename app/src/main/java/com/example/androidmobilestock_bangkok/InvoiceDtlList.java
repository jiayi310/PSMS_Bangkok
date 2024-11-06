package com.example.androidmobilestock_bangkok;

import static com.example.androidmobilestock_bangkok.StockInquiry.removeNonAlphanumeric;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.ActivityInvoiceDetailListBinding;
import com.example.androidmobilestock_bangkok.ui.main.InvADManualFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceDtlList extends AppCompatActivity {
    ActivityInvoiceDetailListBinding binding;
    AC_Class.InvoiceDetails invoiceDetails;
    AC_Class.Invoice invoice, inv_temp;
    AC_Class.CheckOut checkOut;
    AC_Class.InvoiceDetails temp;
    Context context = this;
    ACDatabase db;
    String def_loc;
    Boolean isTaxEnabled = true;

    SharedPreferences sharedPreferences;

    Button mGetSign, mClear, mCancel;
    Dialog myDialog;
    Signature mySignature;
    LinearLayout mContent;
    ImageView ivSign;
    byte[] imgCA;
    RecyclerView recyclerView;
    private InvoiceDtlList_Sales.RecyclerViewClickListener listener;
    boolean isCustomBarcodeEnabled = false;
    Boolean isAutoPrice = false;
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
    Boolean isBatchNoEnabled = true;
    Boolean isSaleBatchEnabled = true;
    String salesOrderType = "";

    MyClickHandler handler;
    InvoiceDtlList_Sales adapter;
    Boolean statusChecked;
    Button btnMerge;
    private IntentIntegrator qrScan;
    String defaultTax = "";
    String user;
    String barcodeDtlRemark = "";
    String barcodeDtlRemark2 = "";
    private InvADManualFragment.OnFragmentInteractionListener mListener;
    String debtorCode;
    String nDocNo, nDateNo, noOfDays;
    ArrayList<AC_Class.InvoiceDetails> selectedItems;
    private static final int REQUEST_CODE_INVOICE_VIEW_HISTORY = 10;
    boolean isReorderEnabled;
    String ntaxType;
    String func;
    boolean defaultSales5CentRounding = false;
    CheckBox roundingCheckBox;
    TextView txt_rounding, txt_total;

//    private void openInvoiceViewHistory() {
//        Intent intent = new Intent(this, InvoiceViewHistory.class);
//        startActivityForResult(intent, REQUEST_CODE_INVOICE_VIEW_HISTORY);
//    }

//    //Bcast Receiver
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            AC_Class.InvoiceDetails temp = intent.getParcelableExtra("inv_dtls");
//            if (!CheckEmpty(temp)) {
//                invoice.addInvoiceDetail(temp);
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_detail_list);
        invoiceDetails = new AC_Class.InvoiceDetails();
        invoice = new AC_Class.Invoice();
        binding.setInv(invoice);
        db = new ACDatabase(this);

        myDialog = new Dialog(InvoiceDtlList.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_signature);
        myDialog.setCancelable(true);
        mContent = (LinearLayout) myDialog.findViewById(R.id.linearLayout);
        mySignature = new Signature(getApplicationContext(), null);
        mySignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mySignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) myDialog.findViewById(R.id.clear);
        mGetSign = (Button) myDialog.findViewById(R.id.getsign);
        mCancel = (Button) myDialog.findViewById(R.id.cancel);
        ivSign = (ImageView) binding.ivSign;
        recyclerView = findViewById(R.id.invdtllist_itemlist);
        btnMerge = findViewById(R.id.btnMerge);
        roundingCheckBox = findViewById(R.id.roundingCheckBox);
        txt_rounding = findViewById(R.id.txt_rounding);
        txt_total = findViewById(R.id.txt_total);

        Intent intent = getIntent();
        if (intent != null){
            debtorCode = intent.getStringExtra("DebtorCode");
            ntaxType = intent.getStringExtra("taxType");
        }


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
            invoiceDetails.setLocation(def_loc);
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor4 = db.getReg("20");
        if (cursor4.moveToFirst()) {
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor1 = db.getReg("21");
        if (cursor1.moveToFirst()) {
            defaultTax = cursor1.getString(0);
        }

        Cursor cursor5 = db.getReg("38");
        if(cursor5.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor7 = db.getReg("39");
        if(cursor7.moveToFirst()){
            isSaleBatchEnabled= Boolean.valueOf(cursor7.getString(0));
        }

        Cursor data = db.getReg("25");
        if (data.moveToFirst()) {
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        Cursor cursor9 = db.getReg("74");
        if(cursor9.moveToFirst()){
            defaultSales5CentRounding = Boolean.valueOf(cursor9.getString(0));
        }

        if (defaultSales5CentRounding){
            roundingCheckBox.setChecked(true);
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

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        //view history feature
        sharedPreferences = getSharedPreferences("FeaturesEnable", Context.MODE_PRIVATE);
        isReorderEnabled = sharedPreferences.getBoolean("reorder_enabled", false);
        noOfDays = sharedPreferences.getString("reorder_days", "Last 30 days");

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sales Details");


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


        invoice = getIntent().getParcelableExtra("DataFromInvHeader");
        invoiceDetails.setDocNo(invoice.getDocNo());
        invoiceDetails.setDocDate(invoice.getDocDate());
        nDocNo = invoice.getDocNo();
        nDateNo = invoice.getDocDate();
        func = getIntent().getStringExtra("FunctionKey");


        checkOut = new AC_Class.CheckOut();
        getInvoiceDetailList();
        btnMerge.setText("Merge (" + Integer.toString(invoice.getInvoiceDetailsList().size()) + ")");

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

                String remove = removeNonAlphanumeric(s.toString().trim());
                String myBarcode = remove;
                String default_location = "";
                double myQty = 1;

                invoiceDetails = new AC_Class.InvoiceDetails();
                // getInvoiceDetailsFrom();

                Cursor dl = db.getReg("7");
                if (dl.moveToFirst()) {
                    default_location = dl.getString(0);
                }
                invoiceDetails.setLocation(default_location);
                invoiceDetails.setDocNo(invoice.getDocNo());

                if (!myBarcode.equals("") && !TextUtils.isEmpty(binding.invdtlEditText.getText())) {
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled) {
                        String normalBarcode = binding.invdtlEditText.getText().toString();
                        Cursor data = db.getItemBC(normalBarcode);
                        if (data.getCount() == 0) {
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
                                    Toast.makeText(InvoiceDtlList.this, "Invalid Barcode.",
                                            Toast.LENGTH_SHORT).show();
                                    isSkip = true;
                                }
                            }
                        }
                    }

                    if (!isSkip) {
                        Cursor results = db.getItemBC(myBarcode);

                        if (results.getCount() == 0) {
                            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            AlertDialog alertDialog = new AlertDialog.Builder(InvoiceDtlList.this).create();
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

                            invoiceDetails.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                            invoiceDetails.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            invoiceDetails.setUOM(results.getString(results.getColumnIndex("UOM")));
                            invoiceDetails.setUPrice(results.getDouble(results.getColumnIndex("Price")));
                            invoiceDetails.setRemarks(barcodeDtlRemark);
                            invoiceDetails.setRemarks2(barcodeDtlRemark2);

                            if (isAutoPrice) {
                                Cursor cursor_pc = db.getPriceCategory(invoice.getDebtorCode());
                                if (cursor_pc != null) {
                                    if (cursor_pc.moveToFirst()) {
                                        Object myPCObj = cursor_pc.getString(0);

                                        if (myPCObj != null) {
                                            try {
                                                Integer myPC = 0;
                                                myPC = Integer.parseInt(myPCObj.toString());

                                                switch (myPC) {
                                                    case 2:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price2"))));
                                                        break;
                                                    case 3:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price3"))));
                                                        break;
                                                    case 4:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price4"))));
                                                        break;
                                                    case 5:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price5"))));
                                                        break;
                                                    case 6:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price6"))));
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
                                    new_intent.putExtra("ItemCode",  invoiceDetails.getItemCode());
                                    new_intent.putExtra("UOM",  invoiceDetails.getUOM());
                                    new_intent.putExtra("Location", default_location);
                                    startActivityForResult(new_intent, 9);

                                }
                            }

                            invoiceDetails.setQuantity(myQty);
                            invoiceDetails.setTaxType(getTax(results.getString(results.getColumnIndex("TaxType"))));
                            Cursor data2 = db.getTaxTypeValue(invoiceDetails.getTaxType());
                            if (data2.moveToNext()) {
                                invoiceDetails.setTaxRate(data2.getDouble(0));
                            }

                            Calculation();

                            //  invoice.addInvoiceDetail(invoiceDetails);
//                            if (!CheckEmpty()) {
                            insertItem(invoiceDetails);
//                            } else {
//                                Toast.makeText(getActivity(), "Invalid Barcode", Toast.LENGTH_SHORT).show();
//                            }
                            barcodeDtlRemark = "";
                            barcodeDtlRemark2 = "";
                            myQty = 1;
                        }
                    }
                }
                binding.invdtlEditText.removeTextChangedListener(this);
                binding.invdtlEditText.clearFocus();
                binding.invdtlEditText.requestFocus();
                // this.hideSoftKeyboard(getView());
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

        roundingCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInvoiceDetailList();
            }
        });
    }

//    private void deleteItem(int position) {
//        final int fposition = position;
//        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceDtlList.this);
//        builder.setMessage("Delete "+invoice.getInvoiceDetail(fposition).getItemCode()+"?");
//        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                invoice.removeInvoiceDetail(fposition);
//                getInvoiceDetailList();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Return from item
            case 1:
                if (resultCode == 0) {
                    temp = data.getParcelableExtra("invoiceDetails");
                    Log.i("custDebug", "Item added: " + temp.getItemDescription());
                } else if(resultCode == 4){

                    AC_Class.Item item = data.getParcelableExtra("ItemsKey");


                    Intent intent = new Intent(InvoiceDtlList.this,
                            EmptyAD.class);

                    intent.putExtra("Inv", invoice);
                    intent.putExtra("Item", item);
                    intent.putExtra("FunctionKey", "New");
                    intent.putExtra("mode", 0);
                    startActivityForResult(intent, 1);

                }
                else {
                    Log.i("custDebug", "Add item cancelled");
                }
                if (temp != null) {
                    invoice.addInvoiceDetail((AC_Class.InvoiceDetails) temp);
                    //insertItem((AC_Class.InvoiceDetails) temp);
                    temp = null;
                }
                break;
            case 2:
                if (resultCode == 0) {
                    List<AC_Class.InvoiceDetails> invoiceDetailsList =
                            data.getParcelableArrayListExtra("invDtlList");
                    for (int i = 0; i < invoiceDetailsList.size(); i++) {
                        invoice.addInvoiceDetail(invoiceDetailsList.get(i));
                        Log.i("custDebug", invoiceDetailsList.get(i).getItemCode() + " " +
                                invoiceDetailsList.get(i).getQuantity());
                    }
                } else {
                    Log.i("custDebug", "Add item cancelled");
                }
                break;

            case 9:
                String batchNo = data.getStringExtra("ItemBatchNo");
                invoiceDetails.setBatchNo(batchNo);
                break;

            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.invdtlEditText.setText(result.getContents().trim());
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;


            // Handle the result from InvoiceViewHistory
            case REQUEST_CODE_INVOICE_VIEW_HISTORY:
                if (resultCode == RESULT_OK) {
                    ArrayList<AC_Class.InvoiceDetails> selectedItems = data.getParcelableArrayListExtra("selectedItems");
                    if (selectedItems != null && !selectedItems.isEmpty()) {
                        for (AC_Class.InvoiceDetails item : selectedItems) {
                            invoice.addInvoiceDetail(item);
                            Log.i("custDebug", "Received item: " + item.getItemCode() + " " + item.getQuantity());
                        }
                    } else {
                        Log.i("custDebug", "No items received or selectedItems is null.");
                    }
                } else {
                    Log.i("custDebug", "Add item cancelled");
                }
                break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInvoiceDetailList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Invoice.class);
        intent.putExtra("Inv", invoice);
        intent.putExtra("FunctionKey", func);
        intent.putExtra("DocNoKey", invoice.getDocNo());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(1, intent);
        startActivity(intent);
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inv_menu3, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            case (R.id.viewHistory): {

                Intent intent = new Intent(InvoiceDtlList.this, InvoiceViewHistory.class);

                Bundle args = new Bundle();
                args.putParcelable("Inv",invoice);
                intent.putExtra("DebtorCode", invoice.getDebtorCode());
                intent.putExtra("docNo", nDocNo);
                intent.putExtra("docDate", nDateNo);
                startActivityForResult(intent, REQUEST_CODE_INVOICE_VIEW_HISTORY);

            }
            break;

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

            case (R.id.addStockCount): {

                Intent new_intent = new Intent(InvoiceDtlList.this, Item_List_Sales.class);
                new_intent.putExtra("debtorcode", invoice.getDebtorCode());
                new_intent.putExtra("substring", "");
                startActivityForResult(new_intent, 1);
                break;
            }


        }
        return false;
    }

    public void getInvoiceDetailList() {
        setOnClickListener();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemViewCacheSize(20);
        adapter = new InvoiceDtlList_Sales(this, invoice.getInvoiceDetailsList(), listener);
        binding.invdtllistItemlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getSummary(invoice.getInvoiceDetailsList());

        if (adapter.getItemCount() > 0)
            binding.invdtllistItemlist.smoothScrollToPosition(adapter.getItemCount() - 1);

        btnMerge.setText("Merge (" + Integer.toString(invoice.getInvoiceDetailsList().size()) + ")");

        if (invoice.getSignature() != null) {
            imgCA = Base64.decode(invoice.getSignature(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgCA, 0, imgCA.length);
            //Bitmap bitmapS = Bitmap.createScaledBitmap(bitmap, 600, 600, false);
            ivSign.setImageBitmap(bitmap);
            ivSign.setVisibility(View.VISIBLE);
        }

    }

    public void setOnClickListener() {
        listener = new InvoiceDtlList_Sales.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceDtlList.this);
                builder.setMessage("What do you want to do?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        invoice.removeInvoiceDetail(position);
                        getInvoiceDetailList();
                    }
                });

                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String DocNo = ((AC_Class.InvoiceDetails) invoice.getInvoiceDetailsList().get(position))
                                .getDocNo();
                        Integer id = ((AC_Class.InvoiceDetails) invoice.getInvoiceDetailsList().get(position))
                                .getID();
                        temp = (AC_Class.InvoiceDetails) invoice.getInvoiceDetailsList().get(position);

                        Intent intent = new Intent(InvoiceDtlList.this, EmptyAD.class);
//                        intent.putExtra("DebtorKey", invoice.getDebtorCode());
                        intent.putExtra("Inv", invoice);
                        intent.putExtra("invoiceDetails", temp);
                        intent.putExtra("FunctionKey", "Edit");
                        intent.putExtra("IDKey", id);
                        intent.putExtra("DocNoKey", DocNo);
                        intent.putExtra("mode", 0);

                        invoice.removeInvoiceDetail(position);

                        startActivityForResult(intent, 1);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        };
    }

    public void getSummary(List<AC_Class.InvoiceDetails> invoiceDetailsList) {
        int i;

        AC_Class.InvoiceDetails current;
        double tempSubtotal = 0;
        double tempDiscount = 0;
        double tempTax = 0;
        double tempTotal = 0;
        for (i = 0; i < invoiceDetailsList.size(); i++) {
            current = invoiceDetailsList.get(i);
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

        //calculate rounding,
        if (roundingCheckBox.isChecked()){
            double total = tempTotal;
            double roundedTotal = Math.round(total * 20) / 20.0;
            double roundingDifference = roundedTotal - total;
            binding.totalTxt.setText(String.format(Locale.getDefault(), " %.2f", roundedTotal));
            txt_rounding.setText(String.format("%.2f", roundingDifference));
            txt_total.setText(String.format(Locale.getDefault(), " %.2f", tempTotal));

        } else {
            txt_rounding.setText("0.00");
        }


        checkOut.setDocNo(invoice.getDocNo());
        checkOut.setSubTotal(tempTotal);
        checkOut.setTotalTax(tempTotal);
        checkOut.setTotal(Double.parseDouble(binding.totalTxt.getText().toString()));
        //checkOut.setTotal(tempTotal);
    }

    public boolean CheckEmpty(AC_Class.InvoiceDetails invoiceDetails) {
        return (invoiceDetails.getLocation() == null ||
                invoiceDetails.getItemCode() == null ||
                invoiceDetails.getItemDescription() == null ||
                invoiceDetails.getUOM() == null ||
                invoiceDetails.getUPrice() == null ||
                invoiceDetails.getSubTotal() == null ||
                invoiceDetails.getTaxType() == null ||
                invoiceDetails.getTaxRate() == null ||
                invoiceDetails.getTaxValue() == null ||
                invoiceDetails.getTotal_Ex() == null ||
                invoiceDetails.getTotal_In() == null);
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnbtnMergeClicked(View view) {
            if (invoice.getInvoiceDetailsList().size() > 0) {
                List<AC_Class.InvoiceDetails> oriDetail = invoice.getInvoiceDetailsList();
                ArrayList<AC_Class.InvoiceDetails> newDetail = new ArrayList<>();

                for (int i = 0; i < oriDetail.size(); i++) {
                    boolean result = false;

                    for (AC_Class.InvoiceDetails myDtl : newDetail) {
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

                invoice.getInvoiceDetailsList().clear();
                invoice.setInvoiceDetailsList(newDetail);

                getInvoiceDetailList();
            }
            btnMerge.setText("Merge (" + Integer.toString(invoice.getInvoiceDetailsList().size()) + ")");
        }

        public void onEditTextViewClicked(View view) {
            findViewById(R.id.invdtl_editText).requestFocus();
            hideSoftKeyboard(view);
        }


        public void OnSignatureClicked(View view) {
            if (!hasImage(ivSign)) {
                dialog_action();
            } else {
                dialog_recreate();
            }
        }

        public void OnCreditSalesTxtViewClicked(View view) {
            if (invoice.getInvoiceDetailsList().size() > 0) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(view.getContext());
                confirm.setTitle("Confirm");
                confirm.setMessage("Set invoice as credit sale?");
                confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                confirm.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commitCreditSales();
                        dialog.dismiss();
                    }
                });
                confirm.show();
            } else {
                Toast.makeText(InvoiceDtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }

        void commitCreditSales() {
            // Commit details
            boolean commitDetails = db.UpdateInvoiceDetail(invoice);

            // Insert header
            invoice.setSubTotal(Double.parseDouble(binding.subtotalTxt.getText().toString()));
            invoice.setTotalDiscount(Double.parseDouble(binding.discountTxt.getText().toString()));
            invoice.setTotalTax(Double.parseDouble(binding.taxTxt.getText().toString()));
            if (roundingCheckBox.isChecked()){
                invoice.setIsRound(true);
            } else {
                invoice.setIsRound(false);
            }
            invoice.setRoundingAdj(Double.parseDouble(txt_rounding.getText().toString()));
            invoice.setTotalIn(Double.parseDouble(binding.totalTxt.getText().toString()));
            invoice.setTotalEx(Double.parseDouble(binding.subtotalTxt.getText().toString()));

            invoice.setDocType("IV");
            Cursor debtor = db.getReg("4");
            if(debtor.moveToFirst()){
                user = debtor.getString(0);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());
            invoice.setLastModifiedDateTime(date);


            boolean insertHeader = db.insertInv(invoice);
            if (invoice.getDocNo().equals(db.getNextNo())) {
                db.IncrementAutoNumbering("S");
            }

            // Broadcast intent to close other activities
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);

            // Open inv details
            Intent new_intent = new Intent(InvoiceDtlList.this,
                    InvoiceDtlMultipleTab.class);
            new_intent.putExtra("docNo", invoice.getDocNo());
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        }

        public void OnCashSalesTxtViewClicked(View view) {
            if (invoice.getInvoiceDetailsList().size() > 0) {

                invoice.setSubTotal(Double.parseDouble(binding.subtotalTxt.getText().toString()));
                invoice.setTotalDiscount(Double.parseDouble(binding.discountTxt.getText().toString()));
                invoice.setTotalTax(Double.parseDouble(binding.taxTxt.getText().toString()));
                if (roundingCheckBox.isChecked()){
                    invoice.setIsRound(true);
                } else {
                    invoice.setIsRound(false);
                }
                invoice.setRoundingAdj(Double.parseDouble(txt_rounding.getText().toString()));
                invoice.setTotalIn(Double.parseDouble(binding.totalTxt.getText().toString()));
                invoice.setTotalEx(Double.parseDouble(binding.subtotalTxt.getText().toString()));

                final String[] methods = {"Cash", "Credit Card", "Multi-Payment"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceDtlList.this);
                builder.setTitle("Pick a payment method");
                builder.setItems(methods, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //Cash
                                Log.d("Test123", "test size: " + invoice.getInvoiceDetailsList().size());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                String date = sdf.format(new Date());
                                invoice.setLastModifiedDateTime(date);
                                Intent cash_intent = new Intent(InvoiceDtlList.this,
                                        CashPayment.class);
                                cash_intent.putExtra("CashKey", "CashOnly");
                                cash_intent.putExtra("CheckOutKey", checkOut);
                                cash_intent.putExtra("Inv", invoice);
                                startActivity(cash_intent);
                                break;

                            case 1: //Credit Card
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                String date2 = sdf2.format(new Date());
                                invoice.setLastModifiedDateTime(date2);
                                Intent card_intent = new Intent(InvoiceDtlList.this,
                                        CreditCardPayment.class);
                                card_intent.putExtra("CCardKey", "CreditCardOnly");
                                card_intent.putExtra("CheckOutKey", checkOut);
                                card_intent.putExtra("Inv", invoice);

                                startActivity(card_intent);
                                break;

                            case 2: //MultiPayment
                                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                String date3 = sdf3.format(new Date());
                                invoice.setLastModifiedDateTime(date3);
                                Intent multi_intent = new Intent(InvoiceDtlList.this,
                                        MultiPayment.class);
                                multi_intent.putExtra("CheckOutKey", checkOut);
                                multi_intent.putExtra("MultiKey", "MultiPayment");
                                multi_intent.putExtra("Inv", invoice);

                                startActivity(multi_intent);
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(InvoiceDtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }

        public void OnButtonClearClicked(View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceDtlList.this);

            builder.setTitle("Clear Items");
            builder.setMessage("Are you sure you want to clear all the items?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    invoice.getInvoiceDetailsList().clear();
                    getInvoiceDetailList();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public class Signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public void dialog_action() {
        mGetSign.setEnabled(false);
        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mySignature.clear();
                mySignature.setBackgroundColor(Color.WHITE);
                mContent.destroyDrawingCache();
                mGetSign.setEnabled(false);
            }
        });
        mGetSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mContent.setDrawingCacheEnabled(true);
                    myDialog.findViewById(R.id.linearLayout).buildDrawingCache();
                    myDialog.dismiss();
                    Bitmap bitmap1 = myDialog.findViewById(R.id.linearLayout).getDrawingCache();
                    ivSign.setImageBitmap(bitmap1);
                    ivSign.setVisibility(View.VISIBLE);

                    Bitmap bitmapC = Bitmap.createScaledBitmap(bitmap1, 200, 200, false);
                    imgCA = bitmapToByte(bitmapC);
                    invoice.setSignature(Base64.encodeToString(imgCA, 0, imgCA.length, Base64.DEFAULT));
                    //btnSign.setText("Clear Sign");
                } catch (Exception e) {
                    final String msg = e.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    public void dialog_recreate() {
        ivSign.setImageDrawable(null);
        ivSign.setImageBitmap(null);
        myDialog.dismiss();
    }

    private boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        return byteFormat;
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

    public String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            //AC_Class.Invoice invoice = args.getParcelable("Inv");
            if (invoice.getTaxType() != null && !invoice.getTaxType().isEmpty()) {
                return invoice.getTaxType();
            } else {
                return defaultTax;
            }
        }
        return "None";
    }

    public void Calculation() {
        invoiceDetails.setSubTotal((invoiceDetails.getQuantity() * invoiceDetails.getUPrice()));
        invoiceDetails.setTotal_Ex(invoiceDetails.getSubTotal() - invoiceDetails.getDiscount());
        invoiceDetails.setTaxValue((invoiceDetails.getTotal_Ex() * invoiceDetails.getTaxRate()) / 100);
        invoiceDetails.setTotal_In(invoiceDetails.getTotal_Ex() + invoiceDetails.getTaxValue());

        //calculate rounding,
        if (roundingCheckBox.isChecked()){
            double total = invoiceDetails.getTotal_In();
            double roundedTotal = Math.round(total * 20) / 20.0;
            double roundingDifference = roundedTotal - total;

            txt_rounding.setText(String.format("%.2f", roundingDifference));
        }
    }

    private int checkInvoiceDetailList(AC_Class.InvoiceDetails invoiceDetails) {
        int result = -1;

        for (int i = 0; i < invoice.getInvoiceDetailsList().size(); i++) {
            AC_Class.InvoiceDetails tempDetail = invoice.getInvoiceDetailsList().get(i);

            if (tempDetail.getItemCode().equals(invoiceDetails.getItemCode()) && tempDetail.getUOM().equals(invoiceDetails.getUOM()) && tempDetail.getUPrice().equals(invoiceDetails.getUPrice())) {
                result = i;
            }
        }
        return result;
    }

    void insertItem(AC_Class.InvoiceDetails invoiceDetails) {
        if (!isCustomBarcodeEnabled) {
            int myResult = checkInvoiceDetailList(invoiceDetails);

            if (myResult >= 0) {
                invoice.getInvoiceDetailsList().get(myResult).setQuantity(invoice.getInvoiceDetailsList().get(myResult).getQuantity() + 1);
                updateInvDtls(invoice.getInvoiceDetailsList().get(myResult));
            } else {
                invoice.getInvoiceDetailsList().add(invoiceDetails);
            }
        } else {
            invoice.getInvoiceDetailsList().add(invoiceDetails);
        }


//        if (locMap.containsKey(invoiceDetails.ItemCode)) {
//            Integer temp = locMap.get(invoiceDetails.ItemCode);
//            itemlist.get(temp).setQuantity(itemlist.get(temp).getQuantity()+1);
//            updateInvDtls(itemlist.get(temp));
//        } else {
//            locMap.put(invoiceDetails.ItemCode, itemlist.size());
//            itemlist.add(invoiceDetails);
//        }
        getInvoiceDetailList();
        //invoiceDetails.setRemarks("");
        //invoiceDetails.setRemarks2("");
    }

    void updateInvDtls(AC_Class.InvoiceDetails invoiceDetails) {
        invoiceDetails.setSubTotal((invoiceDetails.getQuantity() * invoiceDetails.getUPrice()));
        invoiceDetails.setTotal_Ex(invoiceDetails.getSubTotal() - invoiceDetails.getDiscount());
        invoiceDetails.setTaxValue((invoiceDetails.getTotal_Ex() * invoiceDetails.getTaxRate()) / 100);
        invoiceDetails.setTotal_In(invoiceDetails.getTotal_Ex() + invoiceDetails.getTaxValue());
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public void getbatchNo() {
        Cursor data2 = db.getAllItemBatch(invoiceDetails.getItemCode(), invoiceDetails.getUOM(), def_loc);
        Cursor data3 = db.getAllItemBatch(invoiceDetails.getItemCode(), invoiceDetails.getUOM(), def_loc);
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
                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                    int compare = Integer.compare(manudate, newManudate);
                    if (compare <= 0) {
                        newManudate = manudate;
                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Latest Expiration Date")) {
                    int compare = Integer.compare(expidate, newExpirydate);
                    if (compare >= 0) {
                        newExpirydate = expidate;
                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Earliest Expiration Date")) {
                    int compare = Integer.compare(expidate, newExpirydate);
                    if (compare <= 0) {
                        newExpirydate = expidate;
                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                }
            } else {
                invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
            }

        }

    }

}
