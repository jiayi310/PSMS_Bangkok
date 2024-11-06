package com.example.androidmobilestock_bangkok;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.ActivityCreditCardPaymentBinding;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreditCardPayment extends AppCompatActivity {
    ACDatabase db;
    Double NeedPayAmt;
    Spinner spinnercctype;
    String key;
    boolean isCCardOnly;
    Boolean IsEmpty = false;
    ActivityCreditCardPaymentBinding binding;
    AC_Class.Payment payment;
    AC_Class.CheckOut checkOut;
    AC_Class.Invoice invoice;
    String companyHeader;
    String user;
    String jobsheetDocNO;
    int func;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_card_payment);
        payment = new AC_Class.Payment();
        binding.setPayment(payment);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Card Payment");
        db = new ACDatabase(this);

        Cursor header = db.getReg("16");
        if(header.moveToFirst()){
            companyHeader = header.getString(0);
        }
        // Get invoice
        invoice = getIntent().getParcelableExtra("Inv");
        func = getIntent().getIntExtra("Func", 0);
        jobsheetDocNO = getIntent().getStringExtra("JobsheetDocNo");

        spinnercctype = (Spinner)findViewById(R.id.spinner_cardtype);
        spinnercctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals(""))
                {
                    Toast.makeText(CreditCardPayment.this, "Please Select An Option",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.tvCardNo.requestFocus();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            private String current = "";
            private String mmyy = "MMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 0; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 6) {
                        clean = clean + mmyy.substring(clean.length());
                    } else {
                        int mon = Integer.parseInt(clean.substring(0, 2));
                        int year = Integer.parseInt(clean.substring(2, 6));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        clean = String.format("%02d%02d", mon, year);
                    }

                    clean = String.format("%s/%s", clean.substring(0, 2), clean.substring(2, 6));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    binding.tvExpirydate.setText(current);
                    binding.tvExpirydate.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        binding.tvExpirydate.addTextChangedListener(textWatcher);
        PaymentSelection();
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
        return super.onOptionsItemSelected(item);
    }

    public void btnSaveClicked(View view)
    {
        if (CheckEmpty())
        {
            if (TextUtils.isEmpty(binding.tvCardPayment.getText().toString()))
            {
                binding.tvCardPayment.setError("This field cannot be blank.");
            }
            if (TextUtils.isEmpty(binding.tvCardNo.getText().toString()))
            {
                binding.tvCardNo.setError("This field cannot be blank.");
            }
            if (TextUtils.isEmpty(binding.tvExpirydate.getText().toString()))
            {
                binding.tvExpirydate.setError("This field cannot be blank.");
            }
            if (TextUtils.isEmpty(binding.tvAppCode.getText().toString()))
            {
                binding.tvAppCode.setError("This field cannot be blank.");
            }
        }
        else {
            if (isCCardOnly) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());
                payment.setPaymentTime(date);
                String spinner_text = spinnercctype.getSelectedItem().toString();
                payment.setCCType(spinner_text);
                // Exact payment
                if (!payment.getPaymentAmt().equals(payment.getOriginalAmt())) {
                    binding.tvCardPayment.requestFocus();
                    binding.tvCardPayment.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                }
                else {
//                    if (binding.tvCardNo.length() == 16) {
                    if (!TextUtils.isEmpty(binding.tvCardNo.getText().toString())) {
                        if (!TextUtils.isEmpty(binding.tvAppCode.getText().toString())) {
                            // Commit details
                            boolean commitDetails = db.UpdateInvoiceDetail(invoice);
                            // Add header
                            invoice.setDocType("CS");

                            Cursor debtor = db.getReg("4");
                            if(debtor.moveToFirst()){
                                user = debtor.getString(0);
                            }

                            boolean insertHeader = db.insertInv(invoice);
                            if (invoice.getDocNo().equals(db.getNextNo())) {
                                db.IncrementAutoNumbering("S");
                            }
                            //Insert price history
                            //boolean tks = db.insertHistoryPrice(invoice);
                            //Log.i("custDebug", String.valueOf(tks));
                            // Add payment
                            boolean insertPayment = db.insertPayment(payment.getDocNo(),
                                    payment.getPaymentTime(), "Credit Card",
                                    "Credit Card", payment.getOriginalAmt(),
                                    payment.getPaymentAmt(), null, payment.getCCType(),
                                    payment.getCCNo(), payment.getCCExpiryDate(),
                                    payment.getCCApprovalCode(), null);

                            // Upload Invoice, commit temporary invoice table
                            if (insertPayment) {
                                // Print Receipt
                                if (insertHeader && commitDetails) {
//                                    final AlertDialog.Builder builder =
//                                            new AlertDialog.Builder(this);
//                                    builder.setTitle("Attention!");
//                                    builder.setMessage("Do you want to print the receipt?");
//                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            AC_Class.PrintInvoice pi =
//                                                    new AC_Class.PrintInvoice(getApplicationContext(),
//                                                            payment.getDocNo(), companyHeader);
//                                            pi.FindBluetoothDevice();
//                                            try {
//                                                pi.openBluetoothPrinter();
//                                                pi.printData();
//                                                //pi.disconnectBT();
//                                            }
//                                            catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
////                                            Intent new_intent = new Intent(CreditCardPayment.this, Invoice_Menu.class);
////                                            startActivity(new_intent);
//                                            closeAll();
////                                            finish();
//                                        }
//                                    });
//                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
////                                            Intent intent = new Intent(CreditCardPayment.this, Invoice_Menu.class);
////                                            startActivity(intent);
//                                            closeAll();
////                                            finish();
//                                        }
//                                    });
//                                    AlertDialog dialog = builder.create();
//                                    dialog.show();
                                    closeAll();
                                }
                                else {
                                    Toast.makeText(this, "Update Went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            binding.tvAppCode.requestFocus();
                            binding.tvAppCode.selectAll();
                            binding.tvAppCode.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                        }
                    } else {
                        binding.tvCardNo.requestFocus();
                        binding.tvCardNo.selectAll();
                        binding.tvCardNo.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    }
                }
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());
                payment.setPaymentTime(date);
//                if (payment.getPaymentAmt() > payment.getOriginalAmt()) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Attention!");
//                    builder.setMessage("Amount exceeded. Please enter valid amount.");
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            binding.tvCardPayment.selectAll();
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
//                else {
                    payment.setPaymentAmt(Double.valueOf(binding.tvCardPayment.getText().toString()));
                    payment.setPaymentType("MultiPayment");
                    payment.setPaymentAmt(Double.valueOf(binding.tvCardPayment.getText().toString()));
                    payment.setPaymentMethod("Credit Card");
                    payment.setCashChanges(roundDouble(Double.valueOf(binding.tvNetTotal.getText().toString()) -
                            Double.valueOf(binding.tvCardPayment.getText().toString())));
                    payment.setChequeNo(null);
                    Intent intent = new Intent(CreditCardPayment.this, MultiPayment.class);
                    intent.putExtra("CardPaymentKey", payment);
                    setResult(RESULT_OK, intent);
                    finish();
//                }
            }
        }
    }

    // Bcast intent to close all activities
    private void closeAll(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);

        if (func == 1){
            Intent new_intent = new Intent(CreditCardPayment.this, Jobsheet_Details.class);
            new_intent.putExtra("docNo", jobsheetDocNO);
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        } else {
            // Open inv details
            Intent new_intent = new Intent(CreditCardPayment.this,
                    com.example.androidmobilestock_bangkok.InvoiceDtlMultipleTab.class);
            new_intent.putExtra("docNo", invoice.getDocNo());
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        }

    }

    private void PaymentSelection()
    {
        key = getIntent().getStringExtra("CCardKey");
        if (key.equals("CreditCardOnly"))
        {
            checkOut = getIntent().getParcelableExtra("CheckOutKey");
            payment.setDocNo(checkOut.getDocNo());
            payment.setOriginalAmt(roundDouble(checkOut.getTotal()));
            payment.setPaymentAmt(checkOut.getTotal());
            isCCardOnly = true;
        }
        else if (key.equals("CreditCard"))
        {
            payment.setDocNo(getIntent().getStringExtra("MDocNoKey"));
            NeedPayAmt = getIntent().getDoubleExtra("TotalKey", 0);
            payment.setOriginalAmt(roundDouble(Double.valueOf(NeedPayAmt)));
            isCCardOnly = false;
        }
    }

    public boolean CheckEmpty()
    {
        if (TextUtils.isEmpty(binding.tvCardPayment.getText().toString()) ||
                TextUtils.isEmpty(binding.tvCardNo.getText().toString()) ||
                TextUtils.isEmpty(binding.tvExpirydate.getText().toString()) ||
                TextUtils.isEmpty(binding.tvAppCode.getText().toString()))
        {
            IsEmpty = true;
        }
        else
        {
            IsEmpty = false;
        }
        return IsEmpty;
    }

    double roundDouble(double x) {
        BigDecimal bd = new BigDecimal(Double.toString(x));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
