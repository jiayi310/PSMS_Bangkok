package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityCartListBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartList extends AppCompatActivity {
    SharedPreferences prefs;
    ActivityCartListBinding binding;
    AC_Class.Item item;
//    AC_Class.InvoiceDetails invoiceDetails;
    AC_Class.Invoice invoice;
    AC_Class.CheckOut checkOut;
    AC_Class.InvoiceDetails temp;
    ACDatabase db;
    String def_loc;
    String Func;
    String docNo;
    Intent pintent;

    Button mGetSign, mClear, mCancel;
    Dialog myDialog;
    LinearLayout mContent;
    android.widget.ImageView ivSign;
    byte[] imgCA;
    RecyclerView recyclerView;
    private CartListAdapter.RecyclerViewClickListener listener;

    MyClickHandler handler;
    CartListAdapter adapter;
    Signature mySignature;
    Boolean isTaxEnabled = true;
    Button btnMerge;

    Boolean isBatchNoEnabled = true;
    float balanceqty = 0f;
    Boolean NegativeInventory = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_list);
        item = new AC_Class.Item();
        invoice = new AC_Class.Invoice();
        binding.setInv(invoice);
        db = new ACDatabase(this);

        myDialog = new Dialog(CartList.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_signature);
        myDialog.setCancelable(true);
        mContent = (LinearLayout) myDialog.findViewById(R.id.linearLayout);
        mySignature = new CartList.Signature(getApplicationContext(), null);
        mySignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mySignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) myDialog.findViewById(R.id.clear);
        mGetSign = (Button) myDialog.findViewById(R.id.getsign);
        mCancel = (Button) myDialog.findViewById(R.id.cancel);
        recyclerView = findViewById(R.id.invdtllist_itemlist2);
        ivSign = (ImageView) binding.ivSign;
        btnMerge = findViewById(R.id.btnMerge);

        //Retrieve default locations
        prefs = getSharedPreferences("com.presoft.androidmobilestock", Context.MODE_PRIVATE);
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
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor5 = db.getReg("38");
        if(cursor5.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor9 = db.getReg("42");
        if(cursor9.moveToFirst()){
            NegativeInventory = Boolean.valueOf(cursor9.getString(0));
        }

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Cart List");

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

        pintent = getIntent();

        List<AC_Class.InvoiceDetails> invoiceDetailsList =
                pintent.getParcelableArrayListExtra("invoiceDetail");
        invoice = pintent.getParcelableExtra("invoice");

                docNo = pintent.getStringExtra("DocNoKey");
        Func = pintent.getStringExtra("FunctionKey");

        if (Func != null) {
            getCurrentDataForEdit();
        }

        if (invoiceDetailsList != null) {

            invoice.getInvoiceDetailsList().clear();

            for (int i = 0; i < invoiceDetailsList.size(); i++) {

                if(def_loc!=null) {
                    if (def_loc.equals("None")) {
                        def_loc = null;
                        invoiceDetailsList.get(i).setLocation(def_loc);
                    } else {
                        invoiceDetailsList.get(i).setLocation(def_loc);
                    }
                }else
                {
                    invoiceDetailsList.get(i).setLocation("None");
                }
                invoice.addInvoiceDetail(invoiceDetailsList.get(i));
            }
            if (docNo == null) {
                invoice.setDocNo(db.getNextNo());
            } else {
                invoice.setDocNo(docNo);

            }
        }


        checkOut = new AC_Class.CheckOut();
        getInvoiceDetailList();
        btnMerge.setText("Merge (" + Integer.toString(invoice.getInvoiceDetailsList().size()) + ")");

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

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {

            if (data != null) {
                AC_Class.InvoiceDetails invoiceDetail = data.getParcelableExtra("InvoiceDetail");
                if (invoiceDetail != null) {

                    temp.setQuantity(invoiceDetail.getQuantity());
                    temp.setDiscountText(invoiceDetail.getDiscountText());
                    temp.setDiscount(invoiceDetail.getDiscount());
                    temp.setTaxRate(invoiceDetail.getTaxRate());
                    temp.setTaxType(invoiceDetail.getTaxType());
                    temp.setRemarks(invoiceDetail.getRemarks());


                    if (invoiceDetail.getUPrice() != 0) {
                        temp.setUPrice(invoiceDetail.getUPrice());
                    }
                    temp.setTotal_In((temp.getQuantity() * temp.getUPrice()) - temp.getDiscount());
                    //Double aftertaxprice = temp.getTotal_In()+temp.getTaxRate();
                    temp.setTotal_Ex((temp.getQuantity() * temp.getUPrice()));
                    temp.setTaxValue((temp.getTotal_In() * temp.getTaxRate()) / 100);
                    Double aftertaxprice = temp.getTotal_In() + temp.getTaxValue();
                    temp.setTotal_In(aftertaxprice);
                    temp.setSubTotal(temp.getTotal_Ex());

                    getInvoiceDetailList();


                }
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getInvoiceDetailList();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(1, intent);
        Intent in = new Intent();
        in.putExtra("deleteInvoice", invoice);
        setResult(5, in);
        finish();
        //super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

        }
        int id = item.getItemId();
        if (id == R.id.add) {
            Intent new_intent = new Intent(CartList.this, CartView.class);
            //new_intent.putExtra("FunctionKey", "New");
            startActivityForResult(new_intent, 4);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getCurrentDataForEdit() {
        Cursor data;
        switch (Func) {

            case "Edit":
                data = db.getInvoiceHeaderPrint(docNo);
                if (data.getCount() > 0) {
                    while (data.moveToNext()) {
                        if (data.getString(7) != null) {
                            imgCA = Base64.decode(data.getString(7), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imgCA, 0, imgCA.length);
                            //Bitmap bitmapS = Bitmap.createScaledBitmap(bitmap, 600, 600, false);
                            ivSign.setImageBitmap(bitmap);
                            ivSign.setVisibility(View.VISIBLE);
                        }
                    }
                }

        }

    }

    public void set_adapter() {
        setOnClickListener();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartListAdapter(this, invoice.getInvoiceDetailsList(), listener);
        binding.invdtllistItemlist2.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setOnClickListener() {
        listener = new CartListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CartList.this);
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
                        temp = (AC_Class.InvoiceDetails) invoice.getInvoiceDetailsList().get(position);

                        Intent intent = new Intent(CartList.this, ItemDetail.class);
//                        intent.putExtra("DebtorKey", invoice.getDebtorCode());
                        intent.putExtra("invoiceDetail", temp);
                        intent.putExtra("FunctionKey", "Edit");

                        //invoice.removeInvoiceDetail(position);

                        startActivityForResult(intent, 4);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

            @Override
            public void onAddClick(ImageButton v, int position) {
                if(isBatchNoEnabled) {
                    if(invoice.getInvoiceDetailsList().get(position).getBatchNo()!=null) {
                        //stock balance
                        Cursor temp2;
                        temp2 = db.getStockBalanceBatchNo(invoice.getInvoiceDetailsList().get(position).getItemCode(), invoice.getInvoiceDetailsList().get(position).getUOM(), invoice.getInvoiceDetailsList().get(position).getBatchNo());
                        if (temp2.getCount() > 0) {
                            temp2.moveToNext();
                            balanceqty = temp2.getFloat(0);
                        }
                    }
                }else{
                    Cursor temp;
                    temp = db.getStockBalance(invoice.getInvoiceDetailsList().get(position).getItemCode(), invoice.getInvoiceDetailsList().get(position).getUOM());
                    if (temp.getCount() > 0)
                    {
                        temp.moveToNext();
                        balanceqty = temp.getFloat(0);
                    }
                }
                if(invoice.getInvoiceDetailsList().get(position).getQuantity()>=balanceqty) {
                    if(NegativeInventory) {
                        invoice.getInvoiceDetailsList().get(position).setQuantity(invoice.getInvoiceDetailsList().get(position).getQuantity() + 1);
                        Double total_in = invoice.getInvoiceDetailsList().get(position).getQuantity() * invoice.getInvoiceDetailsList().get(position).getUPrice();
                        invoice.getInvoiceDetailsList().get(position).setTotal_In(total_in - invoice.getInvoiceDetailsList().get(position).getDiscount());

                        invoice.getInvoiceDetailsList().get(position).setSubTotal(invoice.getInvoiceDetailsList().get(position).getQuantity() * invoice.getInvoiceDetailsList().get(position).getUPrice());
                        getSummary(invoice.getInvoiceDetailsList());
                    }else {
                        Toast.makeText(getApplicationContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    invoice.getInvoiceDetailsList().get(position).setQuantity(invoice.getInvoiceDetailsList().get(position).getQuantity() + 1);
                    Double total_in = invoice.getInvoiceDetailsList().get(position).getQuantity() * invoice.getInvoiceDetailsList().get(position).getUPrice();
                    invoice.getInvoiceDetailsList().get(position).setTotal_In(total_in - invoice.getInvoiceDetailsList().get(position).getDiscount());

                    invoice.getInvoiceDetailsList().get(position).setSubTotal(invoice.getInvoiceDetailsList().get(position).getQuantity() * invoice.getInvoiceDetailsList().get(position).getUPrice());
                    getSummary(invoice.getInvoiceDetailsList());
                }

            }

            @Override
            public void onSubClick(ImageButton v, int position) {
                if (invoice.getInvoiceDetailsList().get(position).getQuantity() > 1) {
                    invoice.getInvoiceDetailsList().get(position).setQuantity(invoice.getInvoiceDetailsList().get(position).getQuantity() - 1);
                }
                Double total_in = invoice.getInvoiceDetailsList().get(position).getQuantity() * invoice.getInvoiceDetailsList().get(position).getUPrice();
                invoice.getInvoiceDetailsList().get(position).setTotal_In(total_in - invoice.getInvoiceDetailsList().get(position).getDiscount());

                invoice.getInvoiceDetailsList().get(position).setSubTotal(invoice.getInvoiceDetailsList().get(position).getQuantity() * invoice.getInvoiceDetailsList().get(position).getUPrice());
                getSummary(invoice.getInvoiceDetailsList());
            }

        };
    }

    public void getInvoiceDetailList() {

        set_adapter();
        getSummary(invoice.getInvoiceDetailsList());

        if (invoice.getSignature() != null) {
            imgCA = Base64.decode(invoice.getSignature(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgCA, 0, imgCA.length);
            //Bitmap bitmapS = Bitmap.createScaledBitmap(bitmap, 600, 600, false);
            ivSign.setImageBitmap(bitmap);
            ivSign.setVisibility(View.VISIBLE);
        }

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
        checkOut.setDocNo(invoice.getDocNo());
        checkOut.setSubTotal(tempTotal);
        checkOut.setTotalTax(tempTotal);
        checkOut.setTotal(tempTotal);

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
                invoiceDetails.getTotal_In() == null ||
                invoiceDetails.getLine_No() == null);
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
                        if(oriDetail.get(i).getRemarks() ==null ){
                            oriDetail.get(i).setRemarks("");
                        }

                        if(myDtl.getRemarks() == null){
                            myDtl.setRemarks("");
                        }
                        if (oriDetail.get(i).getBatchNo() != null && myDtl.getBatchNo() != null) {
                            if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) &&
                                    oriDetail.get(i).getUOM().equals(myDtl.getUOM()) &&
                                    oriDetail.get(i).getUPrice().equals(myDtl.getUPrice()) &&
                                    oriDetail.get(i).getRemarks().equals(myDtl.getRemarks()) &&
                                    oriDetail.get(i).getDiscount().equals(myDtl.getDiscount()) &&
                                    oriDetail.get(i).getDiscountText().equals(myDtl.getDiscountText()) &&
                                    oriDetail.get(i).getBatchNo().equals(myDtl.getBatchNo())) {

                                myDtl.setQuantity(myDtl.getQuantity() + oriDetail.get(i).getQuantity());
                                myDtl.setDiscount(myDtl.getDiscount() + oriDetail.get(i).getDiscount());
                                myDtl.setTaxValue(myDtl.getTaxValue() + oriDetail.get(i).getTaxValue());
                                myDtl.setSubTotal(myDtl.getSubTotal() + oriDetail.get(i).getSubTotal());
                                myDtl.setTotal_Ex(myDtl.getTotal_Ex() + oriDetail.get(i).getTotal_Ex());
                                myDtl.setTotal_In(myDtl.getTotal_In() + oriDetail.get(i).getTotal_In());

                                result = true;
                            }
                        } else {
                            if (oriDetail.get(i).getItemCode().equals(myDtl.getItemCode()) &&
                                    oriDetail.get(i).getUOM().equals(myDtl.getUOM()) &&
                                    oriDetail.get(i).getUPrice().equals(myDtl.getUPrice()) &&
                                    oriDetail.get(i).getRemarks().equals(myDtl.getRemarks()) &&
                                    oriDetail.get(i).getDiscount().equals(myDtl.getDiscount()) &&
                                    oriDetail.get(i).getDiscountText().equals(myDtl.getDiscountText())) {

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

        public void OnSignatureClicked(View view) {
            if (!hasImage(ivSign)) {
                dialog_action();
            } else {
                dialog_recreate();
            }
        }

        public void OnCheckOutTxtViewClicked(View view) {
            if (invoice.getInvoiceDetailsList().size() > 0) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(view.getContext());
                confirm.setTitle("Confirm");
                confirm.setMessage("Are you sure to check out?");
                confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                confirm.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent new_intent = new Intent(CartList.this,
                                Invoice_C.class);
                        if (invoice.getSignature() != null) {
                            imgCA = Base64.decode(invoice.getSignature(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imgCA, 0, imgCA.length);
                            //Bitmap bitmapS = Bitmap.createScaledBitmap(bitmap, 600, 600, false);
                            ivSign.setImageBitmap(bitmap);
                            ivSign.setVisibility(View.VISIBLE);
                        }
                        new_intent.putExtra("docNo", invoice.getDocNo());
                        if (Func == null) {
                            new_intent.putExtra("FunctionKey", "New");
                        } else {
                            new_intent.putExtra("FunctionKey", "Edit");
                        }
                        new_intent.putExtra("CheckOut", checkOut);
                        new_intent.putParcelableArrayListExtra("InvoiceDetail", (ArrayList<? extends Parcelable>) invoice.getInvoiceDetailsList());
                        new_intent.putExtra("Inv", invoice);
                        startActivity(new_intent);
                        //finish();
                        dialog.dismiss();
                    }
                });
                confirm.show();
            } else {
                Toast.makeText(CartList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
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

    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        return byteFormat;
    }

    private boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public void dialog_recreate() {
        ivSign.setImageDrawable(null);
        ivSign.setImageBitmap(null);
        myDialog.dismiss();
    }
}
