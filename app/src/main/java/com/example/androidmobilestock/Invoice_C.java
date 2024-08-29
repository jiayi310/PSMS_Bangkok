package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityInvoiceCBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Invoice_C extends AppCompatActivity {
    ActivityInvoiceCBinding binding;
    public Invoice_C.MyClickHandler handler;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    ArrayList<AC_Class.InvoiceDetails> invoiceDetailsList = new ArrayList<>();
    SharedPreferences prefs;
    AC_Class.CheckOut checkout;
    private AC_Class.Invoice invoice;
    private AC_Class.Invoice sig;
    BroadcastReceiver exitReceiver;
    ACDatabase db;
    String docNo;
    String def_Debtor;
    String def_Agent;
    String user;

    EditText add1, add2, add3, add4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_c);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Invoice");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        add1 = findViewById(R.id.invheader_address1_1);
        add2 = findViewById(R.id.invheader_address2_1);
        add3 = findViewById(R.id.invheader_address3_1);
        add4 = findViewById(R.id.invheader_address4_1);


        Cursor debtor = db.getReg("17");
        if (debtor.moveToFirst()) {
            def_Debtor = debtor.getString(0);
        }
        Cursor agent = db.getReg("18");
        if (agent.moveToFirst()) {
            def_Agent = agent.getString(0);
        }

        Log.wtf("Default Agent", def_Agent);

        invoice = new AC_Class.Invoice();
        checkout = new AC_Class.CheckOut();
        binding.setInv(invoice);

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        invoice = getIntent().getParcelableExtra("Inv");
        binding.setInv(invoice);
        getCurrentDataForEdit();

//        invoiceDetailsList = getIntent().getParcelableArrayListExtra("InvoiceDetail");
//
//        for (int i = 0; i < invoiceDetailsList.size(); i++) {
//            invoice.addInvoiceDetail(invoiceDetailsList.get(i));
//        }

//        sig = getIntent().getParcelableExtra("Inv");
//
//        if (sig.getSignature() != null) {
//            invoice.setSignature(sig.getSignature());
//        }
        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        exitReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);
                finish();
            }
        };
        registerReceiver(exitReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Returned from add invoice details
            case 1:
                invoice = data.getParcelableExtra("Inv");
                binding.setInv(invoice);
                break;

            case 2:
                if (data != null) {
                    AC_Class.Debtor debtor = data.getParcelableExtra("DebtorsKey");
                    if (debtor != null) {
//                        binding.invheaderDebtorname.setVisibility(View.VISIBLE);
                        invoice.setDebtorCode(debtor.getAccNo());
                        invoice.setDebtorName(debtor.getCompanyName());
                        invoice.setTaxType(debtor.getTaxType());
                        invoice.setPhone(debtor.getPhone());
                        invoice.setFax(debtor.getFax());
                        invoice.setAttention(debtor.getAttention());
                        invoice.setAddress1(debtor.getADD1());
                        invoice.setAddress2(debtor.getADD2());
                        invoice.setAddress3(debtor.getADD3());
                        invoice.setAddress4(debtor.getADD4());
                        invoice.setCreditTerm(debtor.getDisplayTerm());

                        if (def_Agent.equals("None")) {
                            String debAgent = debtor.getSalesAgent();
                            if (debAgent != null && !debAgent.equals("")) {
                                invoice.setAgent(debAgent);
                            } else {
                                invoice.setAgent(null);
                            }
                        } else {
                            if (invoice.getAgent() == null) {
                                String debAgent = debtor.getSalesAgent();
                                if (debAgent != null && !debAgent.equals("")) {
                                    invoice.setAgent(debAgent);
                                }
                            }
                        }
                    }
                }
                break;

            // Return from agent list
            case 3:
                if (resultCode == 1) {
                    AC_Class.SalesAgent salesAgent = data.getParcelableExtra("AgentsKey");
                    if (!salesAgent.getSalesAgent().equals("None")) {
                        invoice.setAgent(salesAgent.getSalesAgent());
                    } else {
                        invoice.setAgent(null);
                    }
                }
                break;
        }
    }

    public void getCurrentDataForEdit() {
        Cursor data, data2;
        ActionBar actionBar = getSupportActionBar();
        checkout = getIntent().getParcelableExtra("CheckOut");
        docNo = getIntent().getStringExtra("docNo");
        String Func = getIntent().getStringExtra("FunctionKey");
        switch (Func) {
            case "New":
                // "Add invoice" title
                actionBar.setTitle("Debtor Details");

                invoice.setDocNo(docNo);

                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                invoice.setDocDate(currentDateandTime);

                if (!def_Debtor.equals("None")) {
//            binding.invheaderDebtorname.setVisibility(View.VISIBLE);
                    invoice.setDebtorCode(def_Debtor);

//            invoice.setDebtorName(db.getDebtorName(def_Debtor));

                    AC_Class.Invoice myInvoice = db.getDebtorInfo(def_Debtor);
                    if (myInvoice != null) {
                        if (!def_Agent.equals("None")) {
                            invoice.setAgent(def_Agent);
                        } else {
                            invoice.setAgent(myInvoice.getAgent());
                        }
                        invoice.setDebtorName(myInvoice.getDebtorName());
                        //invoice.setAgent(myInvoice.getAgent());
                        invoice.setTaxType(myInvoice.getTaxType());
                        invoice.setPhone(myInvoice.getPhone());
                        invoice.setFax(myInvoice.getFax());
                        invoice.setAttention(myInvoice.getAttention());
                        invoice.setAddress1(myInvoice.getAddress1());
                        invoice.setAddress2(myInvoice.getAddress2());
                        invoice.setAddress3(myInvoice.getAddress3());
                        invoice.setAddress4(myInvoice.getAddress4());
                        invoice.setCreateduser(user);
                        invoice.setLastModifiedUser(user);
                        invoice.setCreditTerm(myInvoice.getCreditTerm());


                    }
                }

                break;
            case "Edit":
                // "Edit invoice" title
                actionBar.setTitle("Debtor Details (Edit)");

                data = db.getInvoiceHeadertoUpdate(docNo);

                if (data.getCount() == 1) {
                    data.moveToNext();
                    invoice = new AC_Class.Invoice(data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(data.getColumnIndex("SalesAgent")), data.getString(data.getColumnIndex("TaxType")), data.getString(7), data.getString(data.getColumnIndex("Signature")), data.getString(data.getColumnIndex("Phone")), data.getString(data.getColumnIndex("Fax")),
                            data.getString(data.getColumnIndex("Attention")), data.getString(data.getColumnIndex("Address1")), data.getString(data.getColumnIndex("Address2")), data.getString(data.getColumnIndex("Address3")), data.getString(data.getColumnIndex("Address4")), data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2")), data.getString(data.getColumnIndex("Remarks3")), data.getString(data.getColumnIndex("Remarks4")), data.getString(data.getColumnIndex("CreatedUser")),data.getString(data.getColumnIndex("DisplayTerm")),data.getString(data.getColumnIndex("DetailDiscount")));
                    data = db.getInvoiceDetailtoUpdate(docNo);

                    while (data.moveToNext()) {
                    }

                    invoice.setStatus("Pending");
                    invoice.setLastModifiedUser(user);
                    Cursor deb = db.getDebtorName2(invoice.getDebtorCode());
                    if(deb.moveToFirst()){
                        binding.invheaderDebtorname2.setText(deb.getString(0));
                    }
                    binding.setInv(invoice);
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
        }
        return false;
    }

    public void check() {
        if (TextUtils.isEmpty(binding.invheaderDate.getText().toString())) {
            binding.invheaderDate.setError("This field cannot be blank!", null);
            return;
        }

        if (TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())) {
            binding.invheaderDebtorcode.setError("This field cannot be blank!", null);
            return;
        }

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
//        String date = sdf.format(new Date());
//        invoice.setCreatedTimeStamp(date);

    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnDebtorTxtViewClicked(View view) {
            Intent new_intent = new Intent(Invoice_C.this, DebtorList.class);

            startActivityForResult(new_intent, 2);
        }

        public void onAgentTxtViewClicked(View view) {
            Intent new_intent = new Intent(Invoice_C.this, Agent_List.class);
            startActivityForResult(new_intent, 3);
        }

        public void onCreditTermTxtViewClicked(View view) {
            Cursor data = db.getCreditTerm();
            if (data.getCount() > 0) {

                ArrayList<String> displayTerms = new ArrayList<>();

                while (data.moveToNext()) {
                    String displayTerm = data.getString(data.getColumnIndex("DisplayTerm"));
                    displayTerms.add(displayTerm);
                }

                TextView title = new TextView(Invoice_C.this);
                title.setText("Select Credit Term");
                title.setTextColor(Color.BLACK);
                title.setPadding(20, 20, 20, 5);
                title.setTextSize(18);
                title.setTypeface(null, Typeface.BOLD);

                String[] displayTermArray = new String[displayTerms.size()];
                displayTerms.toArray(displayTermArray);

                AlertDialog.Builder builder = new AlertDialog.Builder(Invoice_C.this);
                builder.setCustomTitle(title);
                builder.setItems(displayTermArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String selectedTerm = displayTermArray[which];

                        invoice.setCreditTerm(selectedTerm);

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(Invoice_C.this, "No credit terms available", Toast.LENGTH_SHORT).show();
            }

            data.close();
        }

        public void OnImageButtonClicked(View view) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    String monthString = String.valueOf(month);
                    String dateString = String.valueOf(dayOfMonth);

                    if (monthString.length() == 1) {
                        monthString = "0" + monthString;
                    }
                    if (dateString.length() == 1) {
                        dateString = "0" + dateString;
                    }
                    String date = dateString + "/" + monthString + "/" + year;
                    invoice.setDocDate(date);
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(Invoice_C.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }

        public void OnCashSalesTxtViewClicked(View view) {
            if (TextUtils.isEmpty(binding.invheaderDate.getText().toString())) {
                binding.invheaderDate.setError("This field cannot be blank!", null);
                return;
            }

            if (TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())) {
                binding.invheaderDebtorcode.setError("This field cannot be blank!", null);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());
            invoice.setCreatedTimeStamp(date);
            final String[] methods = {"Cash", "Credit Card", "Multi-Payment"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(Invoice_C.this);
            builder.setTitle("Pick a payment method");
            builder.setItems(methods, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: //Cash
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                            String date = sdf.format(new Date());
                            invoice.setLastModifiedDateTime(date);
                            Intent cash_intent = new Intent(Invoice_C.this,
                                    CashPayment.class);
                            db.UpdateInvoiceDetail(invoice);
                            cash_intent.putExtra("CashKey", "CashOnly");
                            cash_intent.putExtra("CheckOutKey", checkout);
                            cash_intent.putExtra("Inv", invoice);
                            startActivity(cash_intent);
                            break;

                        case 1: //Credit Card
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                            String date2 = sdf2.format(new Date());
                            invoice.setLastModifiedDateTime(date2);
                            Intent card_intent = new Intent(Invoice_C.this,
                                    CreditCardPayment.class);
                            card_intent.putExtra("CCardKey", "CreditCardOnly");
                            card_intent.putExtra("CheckOutKey", checkout);
                            card_intent.putExtra("Inv", invoice);

                            startActivity(card_intent);
                            break;

                        case 2: //MultiPayment
                            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                            String date3 = sdf3.format(new Date());
                            invoice.setLastModifiedDateTime(date3);
                            Intent multi_intent = new Intent(Invoice_C.this,
                                    MultiPayment.class);
                            multi_intent.putExtra("CheckOutKey", checkout);
                            multi_intent.putExtra("MultiKey", "MultiPayment");
                            multi_intent.putExtra("Inv", invoice);

                            startActivity(multi_intent);
                            break;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void OnCreditSalesTxtViewClicked(View view) {
            if (TextUtils.isEmpty(binding.invheaderDate.getText().toString())) {
                binding.invheaderDate.setError("This field cannot be blank!", null);
                return;
            }

            if (TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())) {
                binding.invheaderDebtorcode.requestFocus();
                binding.invheaderDebtorcode.setError("This field cannot be blank!", null);
                return;
            }else if (!TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())){
                binding.invheaderDebtorcode.setError(null);
                binding.invheaderDebtorcode.clearFocus();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());
            invoice.setCreatedTimeStamp(date);
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
        }

        void commitCreditSales() {
            // Commit details
            boolean commitDetails = db.UpdateInvoiceDetail(invoice);
            // Insert header
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
            Intent new_intent = new Intent(Invoice_C.this,
                    InvoiceDtlMultipleTab.class);
            new_intent.putExtra("docNo", invoice.getDocNo());
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        }
    }
}