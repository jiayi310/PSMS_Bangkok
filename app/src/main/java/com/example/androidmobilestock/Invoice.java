package com.example.androidmobilestock;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.example.androidmobilestock.databinding.ActivityInvoiceBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Invoice extends AppCompatActivity {

    ActivityInvoiceBinding binding;
    public MyClickHandler handler;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private AC_Class.Invoice invoice;
    BroadcastReceiver exitReceiver;
    ACDatabase db;
    String docNo;

    String def_Debtor;
    String def_Agent;
    String user;

    String debtorCode;
    SharedPreferences sharedPreferences;
    boolean isReorderEnabled;
    String noOfDays;
    private static final int REQUEST_CODE_INVOICE_VIEW_HISTORY = 10;
    String key;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //show view history button if feature enable
        sharedPreferences = getSharedPreferences("FeaturesEnable", Context.MODE_PRIVATE);
        isReorderEnabled = sharedPreferences.getBoolean("reorder_enabled", false);
        noOfDays = sharedPreferences.getString("reorder_days", "Last 30 days");


        db = new ACDatabase(this);

        Cursor debtor = db.getReg("17");
        if(debtor.moveToFirst()){
            def_Debtor = debtor.getString(0);
        }
        Cursor agent = db.getReg("18");
        if(agent.moveToFirst()){
            def_Agent  = agent.getString(0);
        }

        Cursor debtor2 = db.getReg("4");
        if(debtor2.moveToFirst()){
            user = debtor2.getString(0);
        }

        invoice = new AC_Class.Invoice();
        binding.setInv(invoice);

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        getCurrentDataForEdit();
        binding.invheaderDate.clearFocus();
        binding.invheaderDebtorcode.clearFocus();

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);
                finish();
            }
        }, intentFilter);

//        // Broadcast Receiver
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.package.ACTION_LOGOUT");
//
//        exitReceiver = new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context context, Intent intent){
//                unregisterReceiver(this);
//                finish();
//            }
//        };
//        registerReceiver(exitReceiver, intentFilter);
    }

//    @Override
//    public void onDestroy()
//    {
//        super.onDestroy();
////        if (exitReceiver != null)
////        {
////            unregisterReceiver(exitReceiver);
////        }
//
//    }


    @Override
    public void onBackPressed() {
        //        if (CheckEmpty())
        //        {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //            builder.setIcon(android.R.drawable.ic_dialog_alert);
        //            builder.setTitle("Attention!");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Invoice.super.onBackPressed();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Returned from add invoice details
            case 1:
                invoice = data.getParcelableExtra("Inv");
                binding.setInv(invoice);
                break;

            // Return from debtor list
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

                        //get debtor code
                        debtorCode = debtor.getAccNo();

                        Cursor deb = db.getDebtorName2(invoice.getDebtorCode());
                        if(deb.moveToFirst()){
                            binding.invheaderDebtorname2.setText(deb.getString(0));
                        }

                        if (def_Agent.equals("None")) {
                            String debAgent = debtor.getSalesAgent();
                            if (debAgent != null && !debAgent.equals("")) {
                                invoice.setAgent(debAgent);
                            }
                            else {
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
                    }
                    else {
                        invoice.setAgent(null);
                    }
//                    Log.i("custDebug", invoice.getAgent());
                } else { Log.i("custDebug", "cancelled agent"); }
                break;

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
                    // After handling the history result, navigate to item detail page
                    Intent intent = new Intent(Invoice.this, InvoiceDtlList.class);
                    intent.putParcelableArrayListExtra("selectedItems", selectedItems);


                    intent.putExtra("DataFromInvHeader", invoice);
                    intent.putExtra("taxType", invoice.getTaxType());
                    intent.putExtra("FunctionKey", key);
                    startActivityForResult(intent, 1);

                    Intent new_intent = new Intent(Invoice.this, InvoiceDtlList.class);
                    new_intent.putExtra("DataFromInvHeader", invoice);
                    new_intent.putExtra("taxType", invoice.getTaxType());
                    new_intent.putExtra("FunctionKey", key);
                    new_intent.putExtra("DebtorCode", debtorCode);
                    startActivityForResult(new_intent, 1);




                } else {
                    Log.i("custDebug", "Add item cancelled");
                }
                break;

        }
    }

    public void getCurrentDataForEdit() {
        Cursor data;
        ActionBar actionBar = getSupportActionBar();
        docNo = getIntent().getStringExtra("DocNoKey");
        String Func = getIntent().getStringExtra("FunctionKey");
        switch (Func) {
            case "New":
                // "Add invoice" title
                actionBar.setTitle("Add Sales (Header)");

                invoice.setDocNo(db.getNextNo());

                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                invoice.setDocDate(currentDateandTime);
                invoice.setCreateduser(user);
                invoice.setLastModifiedUser(user);

                if (!def_Debtor.equals("None")) {
                    invoice.setDebtorCode(def_Debtor);
                    debtorCode = invoice.getDebtorCode();

                    AC_Class.Invoice myInvoice = db.getDebtorInfo(def_Debtor);
                    if (myInvoice != null)
                    {
                        if (!def_Agent.equals("None")) {
                            invoice.setAgent(def_Agent);
                        }
                        else{
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

                        Cursor deb = db.getDebtorName2(invoice.getDebtorCode());
                        while (deb.moveToNext()) {
                            binding.invheaderDebtorname2.setText(deb.getString(0));
                        }
                    }

                }
                invoice.setStatus("Pending");
                break;
            case "Edit":
                // "Edit invoice" title
                actionBar.setTitle("Edit Invoice (Header)");

                data = db.getInvoiceHeadertoUpdate(docNo);
                if (data.getCount() == 1) {
                    data.moveToNext();
                    invoice = new AC_Class.Invoice(data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(data.getColumnIndex("SalesAgent")), data.getString(data.getColumnIndex("TaxType")), data.getString(7),data.getString(data.getColumnIndex("Signature")), data.getString(data.getColumnIndex("Phone")), data.getString(data.getColumnIndex("Fax")),
                            data.getString(data.getColumnIndex("Attention")),data.getString(data.getColumnIndex("Address1")),data.getString(data.getColumnIndex("Address2")),data.getString(data.getColumnIndex("Address3")),data.getString(data.getColumnIndex("Address4")), data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2")),
                            data.getString(data.getColumnIndex("Remarks3")), data.getString(data.getColumnIndex("Remarks4")), data.getString(data.getColumnIndex("CreatedUser")),data.getString(data.getColumnIndex("DisplayTerm")));
                    data = db.getInvoiceDetailtoUpdate(docNo);
                    while (data.moveToNext()) {
                        invoice.addInvoiceDetail(new AC_Class.InvoiceDetails(data.getInt(0),
                                data.getString(1), data.getString(2),
                                data.getString(3), data.getString(4),
                                data.getString(5), data.getDouble(6),
                                data.getDouble(7), data.getDouble(8),
                                data.getDouble(9), data.getString(10),
                                data.getDouble(11), data.getDouble(12),
                                data.getDouble(13), data.getDouble(14),data.getString(15),
                                data.getString(data.getColumnIndex("Remarks")),
                                data.getString(data.getColumnIndex("BatchNo")),
                                data.getString(data.getColumnIndex("Remarks2"))));
                    }
//                    binding.invheaderDebtorname.setVisibility(View.VISIBLE);
                    invoice.setStatus("Pending");
                    invoice.setLastModifiedUser(user);
                    Cursor deb = db.getDebtorName2(invoice.getDebtorCode());
                    if(deb.moveToFirst()){
                        binding.invheaderDebtorname2.setText(deb.getString(0));
                    }
                    binding.setInv(invoice);
                }
                break;
            case "Copy":
                // "Add invoice" title
                actionBar.setTitle("Add Invoice (Header)");

                invoice.setDocNo(db.getNextNo());

                // Insert datetime
                SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime3 = sdf3.format(new Date());
                invoice.setDocDate(currentDateandTime3);

                data = db.getInvoiceHeadertoUpdate(docNo);
                if (data.getCount() == 1) {
                    data.moveToNext();
                    invoice = new AC_Class.Invoice(db.getNextNo(), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(data.getColumnIndex("SalesAgent")), data.getString(data.getColumnIndex("TaxType")), data.getString(7),data.getString(data.getColumnIndex("Signature")), data.getString(data.getColumnIndex("Phone")), data.getString(data.getColumnIndex("Fax")),
                            data.getString(data.getColumnIndex("Attention")),data.getString(data.getColumnIndex("Address1")),data.getString(data.getColumnIndex("Address2")),data.getString(data.getColumnIndex("Address3")),data.getString(data.getColumnIndex("Address4")), data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2")),
                            data.getString(data.getColumnIndex("Remarks3")), data.getString(data.getColumnIndex("Remarks4")), data.getString(data.getColumnIndex("CreatedUser")),data.getString(data.getColumnIndex("DisplayTerm")));
                    data = db.getInvoiceDetailtoUpdate(docNo);
                    while (data.moveToNext()) {
                        invoice.addInvoiceDetail(new AC_Class.InvoiceDetails(data.getInt(0),
                                db.getNextNo(), data.getString(2),
                                data.getString(3), data.getString(4),
                                data.getString(5), data.getDouble(6),
                                data.getDouble(7), data.getDouble(8),
                                data.getDouble(9), data.getString(10),
                                data.getDouble(11), data.getDouble(12),
                                data.getDouble(13), data.getDouble(14),data.getString(15),
                                data.getString(data.getColumnIndex("Remarks")),
                                data.getString(data.getColumnIndex("BatchNo")),
                                data.getString(data.getColumnIndex("Remarks2"))));
                    }
//                    binding.invheaderDebtorname.setVisibility(View.VISIBLE);
                    invoice.setStatus("Pending");
                    invoice.setLastModifiedUser(user);
                    Cursor deb = db.getDebtorName2(invoice.getDebtorCode());
                    if(deb.moveToFirst()){
                        binding.invheaderDebtorname2.setText(deb.getString(0));
                    }
                    binding.setInv(invoice);
                }

                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnDebtorTxtViewClicked(View view)
        {
            Intent new_intent = new Intent(Invoice.this, DebtorList.class);

            startActivityForResult(new_intent, 2);


        }

        public void onAgentTxtViewClicked(View view)
        {
            Intent new_intent = new Intent(Invoice.this, Agent_List.class);
            startActivityForResult(new_intent, 3);
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(Invoice.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }


        public void OnNextButtonClicked(View view)
        {
            if (TextUtils.isEmpty(binding.invheaderDate.getText().toString()))
            {
                binding.invheaderDate.requestFocus();
                binding.invheaderDate.setError("This field cannot be blank!", null);
                return;
            }

            if (TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString()))
            {
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
            key = getIntent().getStringExtra("FunctionKey");

            if (key.equals("New") && isReorderEnabled){
                Intent intent = new Intent(Invoice.this, InvoiceViewHistory.class);

                //Bundle args = new Bundle();
                //args.putParcelable("Inv",invoice);
                //intent.putExtra("noOfDays", noOfDays);
                intent.putExtra("DebtorCode", debtorCode);
                intent.putExtra("docNo", invoice.getDocNo());
                intent.putExtra("docDate", invoice.getDocDate());
                intent.putExtra("FunctionKey", key);
                //intent.putExtras(args);
                startActivityForResult(intent, REQUEST_CODE_INVOICE_VIEW_HISTORY);
            } else {
                Intent new_intent = new Intent(Invoice.this, InvoiceDtlList.class);
                new_intent.putExtra("DataFromInvHeader", invoice);
                new_intent.putExtra("taxType", invoice.getTaxType());
                new_intent.putExtra("FunctionKey", key);
                new_intent.putExtra("DebtorCode", debtorCode);
                startActivityForResult(new_intent, 1);
            }

        }
    }
}
