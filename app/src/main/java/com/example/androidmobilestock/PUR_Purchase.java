package com.example.androidmobilestock;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.example.androidmobilestock.databinding.PurActivityPurchaseBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PUR_Purchase extends AppCompatActivity {

    PurActivityPurchaseBinding binding;
    public MyClickHandler handler;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private AC_Class.Purchase doc;
    BroadcastReceiver exitReceiver;
    ACDatabase db;
    String docNo;

    String def_Creditor;
    String def_Agent;
    String user;

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
        binding = DataBindingUtil.setContentView(this, R.layout.pur_activity_purchase);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        db = new ACDatabase(this);

        Cursor creditor = db.getReg("23");
        if(creditor.moveToFirst()){
            def_Creditor = creditor.getString(0);
        }
        Cursor agent = db.getReg("24");
        if(agent.moveToFirst()){
            def_Agent  = agent.getString(0);
        }
        Cursor debtor = db.getReg("4");
        if(debtor.moveToFirst()){
            user = debtor.getString(0);
        }

        doc = new AC_Class.Purchase();
        binding.setDoc(doc);

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        getCurrentDataForEdit();

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        exitReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                unregisterReceiver(this);
                finish();
            }
        };
        registerReceiver(exitReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PUR_Purchase.super.onBackPressed();
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
            // Returned from add details
            case 1:
                doc = data.getParcelableExtra("iDoc");
                binding.setDoc(doc);
                break;

            // Return from creditor list
            case 2:
                if (data != null) {
                    AC_Class.Creditor creditor = data.getParcelableExtra("iCreditor");
                    if (creditor != null) {
                        doc.setCreditorCode(creditor.getAccNo());
                        doc.setCreditorName(creditor.getCompanyName());
                        doc.setTaxType(creditor.getTaxType());
                        doc.setPhone(creditor.getPhone());
                        doc.setFax(creditor.getFax());
                        doc.setAttention(creditor.getAttention());


                        if (def_Agent.equals("None")) {
                            String debAgent = creditor.getPurchaseAgent();
                            if (debAgent != null && !debAgent.equals("")) {
                                doc.setAgent(debAgent);
                            }
                            else {
                                doc.setAgent(null);
                            }
                        } else {
                            if (doc.getAgent() == null) {
                                String debAgent = creditor.getPurchaseAgent();
                                if (debAgent != null && !debAgent.equals("")) {
                                    doc.setAgent(debAgent);
                                }
                            }
                        }
                    }
                }
                break;

            // Return from agent list
            case 3:
                if (resultCode == 1) {
                    AC_Class.PurchaseAgent purchaseAgent = data.getParcelableExtra("iPurchaseAgent");
                    if (!purchaseAgent.getPurchaseAgent().equals("None")) {
                        doc.setAgent(purchaseAgent.getPurchaseAgent());
                    }
                    else {
                        doc.setAgent(null);
                    }
                } else { Log.i("custDebug", "cancelled agent"); }
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
                actionBar.setTitle("Add Purchase (Header)");

                doc.setDocNo(db.getNextNoPurchase());

                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                doc.setDocDate(currentDateandTime);

                doc.setCreateduser(user);
                doc.setLastModifiedUser(user);

                if (!def_Creditor.equals("None")) {
                    doc.setCreditorCode(def_Creditor);

                    AC_Class.Purchase myDoc = db.getPurchaseInfo(def_Creditor);
                    if (myDoc != null)
                    {
                        if (!def_Agent.equals("None")) {
                            doc.setAgent(def_Agent);
                        }
                        else{
                            doc.setAgent(myDoc.getAgent());
                        }
                        doc.setCreditorName(myDoc.getCreditorName());
                        doc.setTaxType(myDoc.getTaxType());
                        doc.setPhone(myDoc.getPhone());
                        doc.setFax(myDoc.getFax());
                        doc.setAttention(myDoc.getAttention());

                    }


                }

                break;

            case "Edit":
                actionBar.setTitle("Edit Purchase (Header)");

                data = db.getPurchaseHeadertoUpdate(docNo);
                if (data.getCount() == 1) {
                    data.moveToNext();
                    doc = new AC_Class.Purchase(data.getString(data.getColumnIndex("DocNo")), data.getString(data.getColumnIndex("CreatedTimeStamp")), data.getString(data.getColumnIndex("DocDate")), data.getString(data.getColumnIndex("CreditorCode")), data.getString(data.getColumnIndex("CreditorName")), data.getString(data.getColumnIndex("PurchaseAgent")), data.getString(data.getColumnIndex("TaxType")), data.getString(data.getColumnIndex("Phone")), data.getString(data.getColumnIndex("Fax")), data.getString(data.getColumnIndex("Attention")), data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2")), data.getString(data.getColumnIndex("Remarks3")), data.getString(data.getColumnIndex("Remarks4")), data.getString(data.getColumnIndex("CreatedUser")));

                    data = db.getPurchaseDetailtoUpdate(docNo);
                    while (data.moveToNext()) {
                        doc.addPurchaseDetail(new AC_Class.PurchaseDetails(data.getInt(data.getColumnIndex("ID")),
                                data.getString(data.getColumnIndex("DocNo")), data.getString(data.getColumnIndex("Location")),
                                data.getString(data.getColumnIndex("ItemCode")), data.getString(data.getColumnIndex("ItemDescription")),
                                data.getString(data.getColumnIndex("UOM")), data.getDouble(data.getColumnIndex("Qty")),
                                data.getDouble(data.getColumnIndex("UPrice")), data.getDouble(data.getColumnIndex("Discount")),
                                data.getDouble(data.getColumnIndex("SubTotal")), data.getString(data.getColumnIndex("TaxType")),
                                data.getDouble(data.getColumnIndex("TaxRate")), data.getDouble(data.getColumnIndex("TaxValue")),
                                data.getDouble(data.getColumnIndex("Totalex")), data.getDouble(data.getColumnIndex("TotalIn")),
                                data.getString(data.getColumnIndex("LineNo")),data.getString(data.getColumnIndex("BatchNo")),
                                data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2"))));
                    }
                    doc.setLastModifiedUser(user);
                    binding.setDoc(doc);
                }
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnDebtorTxtViewClicked(View view)
        {
            Intent new_intent = new Intent(PUR_Purchase.this, CreditorList.class);

            startActivityForResult(new_intent, 2);
        }

        public void onAgentTxtViewClicked(View view)
        {
            Intent new_intent = new Intent(PUR_Purchase.this, PurchaseAgentList.class);
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
                    doc.setDocDate(date);;
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(PUR_Purchase.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }

        public void OnNextButtonClicked(View view)
        {
            if (TextUtils.isEmpty(binding.invheaderDate.getText().toString()))
            {
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String date = sdf.format(new Date());
            doc.setCreatedTimeStamp(date);
            String key = getIntent().getStringExtra("FunctionKey");
            Intent new_intent = new Intent(PUR_Purchase.this, PUR_PurchaseDtlList.class);
            new_intent.putExtra("iDoc", doc);
            new_intent.putExtra("iFunc", key);
            startActivityForResult(new_intent, 1);
        }
    }
}
