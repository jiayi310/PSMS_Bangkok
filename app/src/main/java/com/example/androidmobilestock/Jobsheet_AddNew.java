package com.example.androidmobilestock;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Jobsheet_AddNew extends AppCompatActivity {

    TextView js_docNo, tv_debtorCode, tv_agent;
    EditText editText_debtorName, editText_debtorName2, editText_phone, editText_address1, editText_address2, editText_address3, editText_address4, editText_attention, editText_fax, editText_remarks1, editText_remarks2, editText_remarks3, editText_remarks4;
    Button btn_Next;
    ACDatabase db;
    String func;
    AC_Class.JobSheet jobSheet;
    String def_Debtor;
    String def_Agent;
    String user;
    String nDocNo, pDocNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet_add_new);

        db = new ACDatabase(this);

        jobSheet = new AC_Class.JobSheet();

        func = getIntent().getStringExtra("FunctionKey");

        js_docNo = findViewById(R.id.js_docNo);
        tv_debtorCode = findViewById(R.id.tv_debtorCode);
        editText_debtorName = findViewById(R.id.editText_debtorName);
        editText_debtorName2 = findViewById(R.id.editText_debtorName2);
        tv_agent = findViewById(R.id.tv_agent);
        editText_phone = findViewById(R.id.editText_phone);
        editText_address1 = findViewById(R.id.editText_address1);
        editText_address2 = findViewById(R.id.editText_address2);
        editText_address3 = findViewById(R.id.editText_address3);
        editText_address4 = findViewById(R.id.editText_address4);
        editText_attention = findViewById(R.id.editText_attention);
        editText_fax = findViewById(R.id.editText_fax);
        editText_remarks1 = findViewById(R.id.editText_remarks1);
        editText_remarks2 = findViewById(R.id.editText_remarks2);
        editText_remarks3 = findViewById(R.id.editText_remarks3);
        editText_remarks4 = findViewById(R.id.editText_remarks4);
        btn_Next = findViewById(R.id.btn_Next);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Cursor debtor = db.getReg("17");
        if (debtor.moveToFirst()) {
            def_Debtor = debtor.getString(0);
        }
        Cursor agent = db.getReg("18");
        if (agent.moveToFirst()) {
            def_Agent = agent.getString(0);
            tv_agent.setText(def_Agent);
        }

        Cursor debtor2 = db.getReg("4");
        if (debtor2.moveToFirst()) {
            user = debtor2.getString(0);
        }

        tv_debtorCode.clearFocus();

        getFuncToEditData();

        tv_debtorCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(Jobsheet_AddNew.this, DebtorList.class);
                startActivityForResult(new_intent, 2);
            }
        });

        tv_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(Jobsheet_AddNew.this, Agent_List.class);
                startActivityForResult(new_intent, 3);
            }
        });

        editText_remarks1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheet.setRemarks(editText_remarks1.getText().toString());
            }
        });
        editText_remarks2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheet.setRemarks2(editText_remarks2.getText().toString());
            }
        });
        editText_remarks3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheet.setRemarks3(editText_remarks3.getText().toString());
            }
        });
        editText_remarks4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheet.setRemarks4(editText_remarks4.getText().toString());
            }
        });

        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tv_debtorCode.getText().toString())) {
                    tv_debtorCode.requestFocus();
                    tv_debtorCode.setError("This field cannot be blank!", null);
                    return;
                } else if (!TextUtils.isEmpty(tv_debtorCode.getText().toString())) {
                    tv_debtorCode.setError(null);
                    tv_debtorCode.clearFocus();
                }

                if (func.equals("New")) {
                    jobSheet.setDocType("JS");
                    Intent intent = new Intent(Jobsheet_AddNew.this, Jobsheet_AddNew2.class);
                    intent.putExtra("JobSheet", jobSheet);
                    intent.putExtra("FunctionKey", "New");
                    startActivity(intent);
                } else if (func.equals("Edit")) {
                    Intent intent2 = new Intent(Jobsheet_AddNew.this, Jobsheet_AddNew2.class);
                    intent2.putExtra("docNo", pDocNo);
                    intent2.putExtra("FunctionKey", "Edit");
                    startActivity(intent2);
                }

            }
        });

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            // Return from debtor list
            case 2:
                if (data != null) {
                    AC_Class.Debtor debtor = data.getParcelableExtra("DebtorsKey");
                    if (debtor != null) {

                        jobSheet.setDebtorCode(debtor.getAccNo());
                        jobSheet.setDebtorName(debtor.getCompanyName());
                        jobSheet.setTaxType(debtor.getTaxType());
                        jobSheet.setPhone(debtor.getPhone());
                        jobSheet.setFax(debtor.getFax());
                        jobSheet.setAttention(debtor.getAttention());
                        jobSheet.setAddress1(debtor.getADD1());
                        jobSheet.setAddress2(debtor.getADD2());
                        jobSheet.setAddress3(debtor.getADD3());
                        jobSheet.setAddress4(debtor.getADD4());
                        //remarks?

                        tv_debtorCode.setText(jobSheet.getDebtorCode());
                        editText_debtorName.setText(jobSheet.getDebtorName());
                        editText_phone.setText(jobSheet.getPhone());
                        editText_address1.setText(jobSheet.getAddress1());
                        editText_address2.setText(jobSheet.getAddress2());
                        editText_address3.setText(jobSheet.getAddress3());
                        editText_address4.setText(jobSheet.getAddress4());
                        editText_attention.setText(jobSheet.getAttention());
                        editText_fax.setText(jobSheet.getFax());

                        Cursor deb = db.getDebtorName2(jobSheet.getDebtorCode());
                        if (deb.moveToFirst()) {
                            editText_debtorName2.setText(deb.getString(0));
                            jobSheet.setDebtorName2(deb.getString(0));
                        }

                        if (def_Agent.equals("None")) {
                            String debAgent = debtor.getSalesAgent();
                            if (debAgent != null && !debAgent.equals("")) {
                                jobSheet.setAgent(debAgent);
                                tv_agent.setText(debAgent);
                            } else {
                                jobSheet.setAgent(null);
                                tv_agent.setText("");
                            }
                        } else {
                            if (jobSheet.getAgent() == null) {
                                String debAgent = debtor.getSalesAgent();
                                if (debAgent != null && !debAgent.equals("")) {
                                    jobSheet.setAgent(debAgent);
                                    tv_agent.setText(debAgent);
                                } else {
                                    jobSheet.setAgent(def_Agent);
                                    tv_agent.setText(def_Agent);
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
                        jobSheet.setAgent(salesAgent.getSalesAgent());
                        tv_agent.setText(salesAgent.getSalesAgent());
                    } else {
                        jobSheet.setAgent(null);
                        tv_agent.setText("");
                    }
                } else {
                    Log.i("custDebug", "cancelled agent");
                }
                break;
        }
    }


    private void getFuncToEditData() {
        ActionBar actionBar = getSupportActionBar();
        switch (func) {
            case "New":
                actionBar.setTitle("New Job Sheet");

                // Set Doc No
                //String nDocNo = getNextDocNo();
                String nDocNo = db.getJSNextDocNo();
                js_docNo.setText(nDocNo);
                jobSheet.setDocNo(nDocNo);
                jobSheet.setStatus("New");

                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                jobSheet.setCreatedUser(user);
                jobSheet.setLastModifiedUser(user);
                jobSheet.setCreatedTimeStamp(currentDateandTime);
                jobSheet.setLastModifiedDateTime(currentDateandTime);

                if (!def_Debtor.equals("None")) {
                    jobSheet.setDebtorCode(def_Debtor);
                    tv_debtorCode.setText(jobSheet.getDebtorCode());

                    AC_Class.JobSheet myJobSheet = db.getDebtorInfo2(def_Debtor);
                    if (myJobSheet != null) {
                        if (!def_Agent.equals("None")) {
                            jobSheet.setAgent(def_Agent);
                            tv_agent.setText(jobSheet.getAgent());
                        } else {
                            jobSheet.setAgent(myJobSheet.getAgent());
                            tv_agent.setText(jobSheet.getAgent());
                        }
                        jobSheet.setDebtorName(myJobSheet.getDebtorName());
                        jobSheet.setTaxType(myJobSheet.getTaxType());
                        jobSheet.setPhone(myJobSheet.getPhone());
                        jobSheet.setFax(myJobSheet.getFax());
                        jobSheet.setAttention(myJobSheet.getAttention());
                        jobSheet.setAddress1(myJobSheet.getAddress1());
                        jobSheet.setAddress2(myJobSheet.getAddress2());
                        jobSheet.setAddress3(myJobSheet.getAddress3());
                        jobSheet.setAddress4(myJobSheet.getAddress4());
                        jobSheet.setRemarks(myJobSheet.getRemarks());
                        jobSheet.setRemarks2(myJobSheet.getRemarks2());
                        jobSheet.setRemarks3(myJobSheet.getRemarks3());
                        jobSheet.setRemarks4(myJobSheet.getRemarks4());

                        editText_debtorName.setText(jobSheet.getDebtorName());
                        editText_phone.setText(jobSheet.getPhone());
                        editText_fax.setText(jobSheet.getFax());
                        editText_attention.setText(jobSheet.getAttention());
                        editText_address1.setText(jobSheet.getAddress1());
                        editText_address2.setText(jobSheet.getAddress2());
                        editText_address3.setText(jobSheet.getAddress3());
                        editText_address4.setText(jobSheet.getAddress4());
                        editText_remarks1.setText(jobSheet.getRemarks());
                        editText_remarks2.setText(jobSheet.getRemarks2());
                        editText_remarks3.setText(jobSheet.getRemarks3());
                        editText_remarks4.setText(jobSheet.getRemarks4());

                        Cursor deb = db.getDebtorName2(jobSheet.getDebtorCode());
                        while (deb.moveToNext()) {
                            jobSheet.setDebtorName2(deb.getString(0));
                            editText_debtorName2.setText(deb.getString(0));
                        }
                    }

                }

                break;

            case "Edit":
                actionBar.setTitle("Edit Job Sheet");
                nDocNo = getIntent().getStringExtra("docNo");
                pDocNo = nDocNo;

                Cursor cursor = db.getJobSheetByDocNo(nDocNo);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String nDebtorCode = cursor.getString(cursor.getColumnIndex("DebtorCode"));
                        String nDebtorName = cursor.getString(cursor.getColumnIndex("DebtorName"));
                        String nDebtorName2 = cursor.getString(cursor.getColumnIndex("DebtorName2"));
                        String nAgent = cursor.getString(cursor.getColumnIndex("SalesAgent"));
                        String nPhone = cursor.getString(cursor.getColumnIndex("Phone"));
                        String nAddress1 = cursor.getString(cursor.getColumnIndex("Address1"));
                        String nAddress2 = cursor.getString(cursor.getColumnIndex("Address2"));
                        String nAddress3 = cursor.getString(cursor.getColumnIndex("Address3"));
                        String nAddress4 = cursor.getString(cursor.getColumnIndex("Address4"));
                        String nAttention = cursor.getString(cursor.getColumnIndex("Attention"));
                        String nFax = cursor.getString(cursor.getColumnIndex("Fax"));
                        String nRemarks1 = cursor.getString(cursor.getColumnIndex("Remarks"));
                        String nRemarks2 = cursor.getString(cursor.getColumnIndex("Remarks2"));
                        String nRemarks3 = cursor.getString(cursor.getColumnIndex("Remarks3"));
                        String nRemarks4 = cursor.getString(cursor.getColumnIndex("Remarks4"));

                        js_docNo.setText(nDocNo);
                        tv_debtorCode.setText(nDebtorCode);
                        editText_debtorName.setText(nDebtorName);
                        editText_debtorName2.setText(nDebtorName2);
                        tv_agent.setText(nAgent);
                        editText_phone.setText(nPhone);
                        editText_address1.setText(nAddress1);
                        editText_address2.setText(nAddress2);
                        editText_address3.setText(nAddress3);
                        editText_address4.setText(nAddress4);
                        editText_attention.setText(nAttention);
                        editText_fax.setText(nFax);
                        editText_remarks1.setText(nRemarks1);
                        editText_remarks2.setText(nRemarks2);
                        editText_remarks3.setText(nRemarks3);
                        editText_remarks4.setText(nRemarks4);
                    }
                }
                cursor.close();

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Jobsheet_AddNew.this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Jobsheet_AddNew.super.onBackPressed();
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

//    private String getNextDocNo() {
//        String nextDocNo = db.getJSNextDocNo();
//
//        if (nextDocNo != null) {
//            return "MJS - 0001";
//        }
//        /*else {
//            String prefix = "MJS - ";
//            int nextNo = Integer.parseInt(nextDocNo.substring(prefix.length())) + 1;
//            return String.format(Locale.getDefault(), prefix + "%04d", nextNo);
//        }
//
//         */
//    }
}