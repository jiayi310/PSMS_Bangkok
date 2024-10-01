package com.example.androidmobilestock;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityCashPaymentBinding;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CashPayment extends AppCompatActivity {
    ActivityCashPaymentBinding binding;
    ACDatabase db;
    boolean isCashOnly = false;
    String key;
    AC_Class.Invoice invoice;
    AC_Class.Payment paymentclass;
    AC_Class.CheckOut checkOut;
    Double NeedPayAmt;
    AC_Class.Payment payment;
    String companyHeader;
    android.widget.ImageView imageView;
    Bitmap bitmap;
    byte[] img;
    String encodedImage;
    String user;
    int func;
    String jobsheetDocNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cash_payment);
        paymentclass = new AC_Class.Payment();
        binding.setPayment(paymentclass);
        binding.setClickHandler(new OnClickHandler(this));

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Cash Payment");


        // Get invoice header
        invoice = getIntent().getParcelableExtra("Inv");
        func = getIntent().getIntExtra("Func", 0);
        jobsheetDocNO = getIntent().getStringExtra("JobsheetDocNo");

        db = new ACDatabase(this);

        PaymentSelection();
        binding.tvCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    if (!TextUtils.isEmpty(temp)) {
                        binding.tvCash.removeTextChangedListener(this);

                        temp = temp.replaceAll("[.]", "");
                        Log.i("custDebug", temp);
                        StringBuilder sb = new StringBuilder(temp);
                        sb.insert(0, "000");
                        sb.insert(sb.length()-2, ".");
                        temp = sb.toString();
                        Log.i("custDebug", temp);
                        double payAmt = roundDouble(Double.parseDouble(temp));
                        binding.tvCash.setText(String.format(Locale.getDefault(),
                                "%.2f", payAmt));
//                        binding.tvCash.setText();
                        paymentclass.setPaymentAmt(Double.valueOf(payAmt));

                        binding.tvCash.addTextChangedListener(this);
                    } else {
                        paymentclass.setPaymentAmt(0.00d);
                        binding.tvCash.setText(String.format(Locale.getDefault(),
                                "%.2f", paymentclass.getPaymentAmt()));
                    }
                    binding.tvCash.setSelection(binding.tvCash.length());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
                Calculation();
            }
        });
    }

    double roundDouble(double x) {
        BigDecimal bd = new BigDecimal(Double.toString(x));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
//        if (id == R.id.upload)
//        {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//
//            startActivityForResult(Intent.createChooser(intent, "Pick an image"), 1);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.cash_payment, menu);
//        return true;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            imageView = (android.widget.ImageView) findViewById(R.id.iv);

            try {
                String type = getContentResolver().getType(data.getData());
                System.out.println(type);
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public class OnClickHandler {
        Context context;

        OnClickHandler(Context context){
            this.context = context;
        }

        public void btnSaveClicked(View view)
        {
            if (CheckEmpty())
            {
                if(TextUtils.isEmpty(binding.tvCash.getText().toString())) {
                    binding.tvCash.setError("This field cannot be blank.");
                }
                if (TextUtils.isEmpty(binding.tvCash.getText().toString()))
                {
                    binding.tvChange.setError("This field cannot be blank.");
                }
            }
            else {
                //new saveImage(CashPayment.this).execute();
                if (isCashOnly) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
                            Locale.getDefault());
                    String date = sdf.format(new Date());
                    paymentclass.setPaymentTime(date);
//                    if ((paymentclass.getPaymentAmt() + paymentclass.getCashChanges()) <
//                            paymentclass.getOriginalAmt()) {
                    if (paymentclass.getPaymentAmt() < paymentclass.getOriginalAmt()) {
                        binding.tvCash.selectAll();
                        Toast.makeText(CashPayment.this,
                                "Please ensure the amount paid covers the cost.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                            // Commit details & payment history
                            boolean commitDetails = db.UpdateInvoiceDetail(invoice);
                            // Insert header
                            invoice.setDocType("CS");


                            Cursor debtor = db.getReg("4");
                            if(debtor.moveToFirst()){
                                user = debtor.getString(0);
                            }

                            boolean insertHeader = db.insertInv(invoice);
                            if (invoice.getDocNo().equals(db.getNextNo())) {
                                db.IncrementAutoNumbering("S");
                            }
                            // Insert payment
                            boolean insert = db.insertPayment(paymentclass.getDocNo(), date,
                                    "Cash", "Cash", paymentclass.getOriginalAmt(),
                                    paymentclass.getPaymentAmt(), paymentclass.getCashChanges(), null,
                                    null, null, null, null);

                            // Upload Invoice, commit temporary invoice table
                            if (insert) {
                                // Print Receipt
                                if (insertHeader && commitDetails) {
                                    closeAll();
                                } else {
                                    Toast.makeText(context, "Update Went Wrong",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(CashPayment.this, "Something Went Wrong",
                                        Toast.LENGTH_SHORT).show();
                            }
                    }
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    String date = sdf.format(new Date());
                    paymentclass.setPaymentTime(date);
                        paymentclass.setPaymentType("MultiPayment");
                        paymentclass.setPaymentMethod(key);
                        paymentclass.setCCType(null);
                        paymentclass.setCCNo(null);
                        paymentclass.setCCExpiryDate(null);
                        paymentclass.setCCApprovalCode(null);
                        paymentclass.setChequeNo(null);
                        Intent intent = new Intent(CashPayment.this, MultiPayment.class);
                        intent.putExtra("CashPaymentKey", paymentclass);
                        setResult(RESULT_OK, intent);
                        finish();
                }

            }
        }
    }

    // Bcast intent to close all activities
    private void closeAll(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);

        if (func == 1){
            Intent new_intent = new Intent(CashPayment.this, Jobsheet_Details.class);
            new_intent.putExtra("docNo", jobsheetDocNO);
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        } else {
            // Open inv details
            Intent new_intent = new Intent(CashPayment.this,
                    InvoiceDtlMultipleTab.class);
            new_intent.putExtra("docNo", invoice.getDocNo());
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        }


    }

    public void Calculation()
    {
        paymentclass.setCashChanges((double) roundDouble( paymentclass.getPaymentAmt() - paymentclass.getOriginalAmt()));
    }

    private void PaymentSelection()
    {
        key = getIntent().getStringExtra("CashKey");
        if (key.equals("CashOnly"))
        {
            checkOut = getIntent().getParcelableExtra("CheckOutKey");
            paymentclass.setDocNo(checkOut.getDocNo());
            paymentclass.setOriginalAmt(roundDouble(checkOut.getTotal()));
            isCashOnly = true;
        }
        else if (key.equals("Cash"))
        {
            isCashOnly = false;
            String docNo = getIntent().getStringExtra("MDocNoKey");
            paymentclass.setDocNo(docNo);
            NeedPayAmt = getIntent().getDoubleExtra("TotalKey", 0);
            paymentclass.setOriginalAmt(roundDouble(Double.valueOf(NeedPayAmt)));
        }
    }

    public boolean CheckEmpty()
    {
        return (TextUtils.isEmpty(binding.tvCash.getText().toString())
                || TextUtils.isEmpty(binding.tvChange.getText().toString()));
    }




}
