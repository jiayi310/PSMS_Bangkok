package com.example.androidmobilestock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.databinding.DataBindingUtil;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityArpaymentDtlListBinding;
import com.google.zxing.integration.android.IntentIntegrator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ARPaymentDtlList extends AppCompatActivity {
    ActivityArpaymentDtlListBinding binding;
    AC_Class.ARPayment arPaymentlist;
    ACDatabase db;
    String typeFP;
    String user;

    MyClickHandler handler;
    ARPaymentDtlListAdapter adapter;
    String invDocDate;
    Double netTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_arpayment_dtl_list);

        db = new ACDatabase(this);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("AR Payment Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf39f61));

        arPaymentlist = getIntent().getParcelableExtra("DataFromARHeader");

        SharedPreferences sh = getSharedPreferences("CollectionImage", MODE_PRIVATE);

        String s1 = sh.getString("image", "");

        arPaymentlist.setImage(s1);

        typeFP = getIntent().getStringExtra("FunctionKey");

        binding.setAR(arPaymentlist);
        getPLDtlList();

//        binding.add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(binding.docno.getText().toString().equals("") || binding.amount.getText().toString().equals("")){
//                    Toast.makeText(getApplicationContext(),"Please fill the form",Toast.LENGTH_SHORT).show();
//                }else
//                {
//                    AC_Class.ARPaymentDtl dtl = new AC_Class.ARPaymentDtl();
//                    dtl.setDocNo(arPaymentlist.getDocNo());
//                    dtl.setDocNumber(binding.docno.getText().toString());
//                    dtl.setDocDate(invDocDate);
//                    dtl.setOrgAmt(netTotal);
//                    dtl.setPaymentAmount(Double.parseDouble(binding.amount.getText().toString()));
//                    arPaymentlist.addARPaymentDtl(dtl);
//                    binding.docno.setText("");
//                    binding.amount.setText("");
//                    getPLDtlList();
//                }
//            }
//        });


        binding.lvPackingListDtl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ARPaymentDtlList.this);
                builder.setMessage("What do you want to do?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arPaymentlist.removeARDtl(position);
                        getPLDtlList();
                    }
                });

                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AC_Class.ARPaymentDtl tempDtl = (AC_Class.ARPaymentDtl) parent.getItemAtPosition(position);
                        AlertDialog.Builder alert = new AlertDialog.Builder(ARPaymentDtlList.this);
                        final EditText edittext = new EditText(ARPaymentDtlList.this);
                        alert.setMessage("Update Payment Amount");
                        alert.setTitle("Enter Payment Amount:");

                        alert.setView(edittext);

                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String YouEditTextValue = edittext.getText().toString();
                                tempDtl.setPaymentAmount(Double.parseDouble(YouEditTextValue));
                                getPLDtlList();
                            }
                        });

                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                            }
                        });

                        alert.show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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

    public void getPLDtlList() {
        adapter = new ARPaymentDtlListAdapter(this, arPaymentlist.getARPaymentDtl());
        binding.lvPackingListDtl.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getSummary();

        if (adapter.getCount() > 0)
            binding.lvPackingListDtl.setSelection(adapter.getCount() - 1);
    }

    public void getSummary() {
        float totalAmt = 0.0f;

        for (int i = 0; i < arPaymentlist.getARPaymentDtl().size(); i++) {
            totalAmt += arPaymentlist.getARPaymentDtl().get(i).getPaymentAmount();
        }
        binding.lblTotal.setText(String.format(Locale.getDefault(),
                " %.2f", totalAmt));

       // arPaymentlist.setAmount((double) totalAmt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Return from item
            //AddManual
            case 1:
                ArrayList<AC_Class.ARPaymentDtl> list = new ArrayList<>();
                if (resultCode == 0) {
                    list = data.getExtras().getParcelableArrayList("arPaymentList");
                }
                if (list!= null) {
                    for (int i =0;i<list.size(); i++){
                        arPaymentlist.addARPaymentDtl(list.get(i));
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPLDtlList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        arPaymentlist.setImage("");
        intent.putExtra("iPackingList", arPaymentlist);
        setResult(1, intent);
        super.onBackPressed();
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inv_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            case (R.id.addStockCount): {
                //
                final Intent intent = new Intent(ARPaymentDtlList.this,
                        ARPayment_OutstandingList.class);
                intent.putExtra("DebtorCode", arPaymentlist.getDebtorCode());
                intent.putExtra("DocNo", arPaymentlist.getDocNo());
                String docnostring = "";
                for(int i = 0; i < arPaymentlist.getARPaymentDtl().size() ; i++){
                    if(docnostring == "") {
                        docnostring += "'" + arPaymentlist.getARPaymentDtl().get(i).getDocNumber() + "'";
                    }else{
                        docnostring += ", '" + arPaymentlist.getARPaymentDtl().get(i).getDocNumber() + "'";
                    }
                }
                if(docnostring == ""){
                    docnostring = "''";
                }
                intent.putExtra("DocNoList", docnostring);
                startActivityForResult(intent, 1);
                break;
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
            if (arPaymentlist.getARPaymentDtl().size() > 0) {



                if(arPaymentlist.getAmount() < Double.parseDouble(binding.lblTotal.getText().toString())){

                    new AlertDialog.Builder(context)
                            .setTitle("Message")
                            .setMessage("KnockOff Amount > Payment Amount. Did you want to update the payment amount?")
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    //SaveData();
                                    arPaymentlist.setAmount(Double.parseDouble(binding.lblTotal.getText().toString()));
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    //SaveData();
                                    Toast.makeText(ARPaymentDtlList.this, "KnockOff Amount > Payment Amount. Abort Save!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }else{
                    SaveData();
                }

            } else {
                Toast.makeText(ARPaymentDtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void SaveData(){
        if (!typeFP.equals("Edit")) {
            arPaymentlist.setDocNo(db.getNextARNo());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());
            arPaymentlist.setCreatedTimeStamp(date);
        }
        // Broadcast intent to close other activities
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);

        Cursor debtor = db.getReg("4");
        if(debtor.moveToFirst()){
            user = debtor.getString(0);
            arPaymentlist.setCreatedUser(user);
        }

        db.UpdateARDetail(arPaymentlist);
        db.insertAR(arPaymentlist);

        if (arPaymentlist.getDocNo().equals(db.getNextNoAR())) {
            db.IncrementAutoNumbering("AR");
        }

        Intent myIntent = new Intent(ARPaymentDtlList.this, ARMultipleTab.class);
        myIntent.putExtra("arDoc", arPaymentlist.getDocNo());
        myIntent.putExtra("iType", typeFP);
        startActivityForResult(myIntent, 3);
    }

}