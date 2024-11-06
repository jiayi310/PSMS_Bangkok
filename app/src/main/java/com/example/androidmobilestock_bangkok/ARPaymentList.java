package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.ActivityArpaymentBinding;

import java.util.ArrayList;
import java.util.List;

public class ARPaymentList extends AppCompatActivity {

    ActivityArpaymentBinding binding;
    EditText searchEditText;
    ACDatabase db;
    String Default_curr;
    boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_arpayment);
        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf39f61));
        actionBar.setTitle("Collection");
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchEditText = (EditText) findViewById(R.id.searchField);

        db = new ACDatabase(this);
        getDataDesc("");

        binding.invmenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ARPaymentList.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");

                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int uploaded = ((AC_Class.ARPayment)parent.getItemAtPosition(position)).getUploaded();
                        if(uploaded == 1) {
                            Toast.makeText(ARPaymentList.this, "Action Failed. The Collection already uploaded.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            String DocNo = ((AC_Class.ARPayment) parent.getItemAtPosition(position)).getDocNo();
                            Intent intent = new Intent(ARPaymentList.this, ARPayment.class);
                            intent.putExtra("DocNoKey", DocNo);
                            intent.putExtra("FunctionKey", "Edit");
                            startActivity(intent);
                        }
                    }
                });

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final AlertDialog.Builder delDialog = new AlertDialog.Builder(ARPaymentList.this);
                        delDialog.setTitle("Delete?");
                        delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String DocNo = ((AC_Class.ARPayment) parent.getItemAtPosition(position)).getDocNo();
                                boolean delete = db.deleteARDetails(DocNo);
                                searchEditText.setText("");
                                if (!delete) {
                                    Toast.makeText(ARPaymentList.this, "Delete failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                        delDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        delDialog.show();
                    }
                });

                builder.setNeutralButton("Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String docNo = ((AC_Class.ARPayment)parent.getItemAtPosition(position)).getDocNo();
                        String debtor = ((AC_Class.ARPayment)parent.getItemAtPosition(position)).getDebtorCode();
                        Intent new_intent = new Intent(ARPaymentList.this, ARMultipleTab.class);
                        new_intent.putExtra("arDoc", docNo);
                        new_intent.putExtra("debtorCode", debtor);
                        startActivity(new_intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override
            public void afterTextChanged(Editable s) {
                getDataDesc(s.toString().trim());
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inv_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        if (id == R.id.addStockCount)
        {
            Intent new_intent = new Intent(ARPaymentList.this, ARPayment.class);
            new_intent.putExtra("FunctionKey", "New");
            startActivity(new_intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataDesc("");
    }

    public static class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }
    }

//    public void getData(String substring)
//    {
//        Cursor data = db.getARPaymentMenuLike(substring);
//        List<AC_Class.ARPayment> arPayments = new ArrayList<>();
//
//        if (data.getCount() > 0)
//        {
//            while (data.moveToNext()) {
//                AC_Class.ARPayment arPayment = new AC_Class.ARPayment(data.getString(data.getColumnIndex("DocNo")),data.getString(data.getColumnIndex("Date")), data.getString(data.getColumnIndex("DebtorCode")),data.getString(data.getColumnIndex("DebtorName")),data.getDouble(data.getColumnIndex("Amount")),data.getInt(data.getColumnIndex("Uploaded")),data.getString(data.getColumnIndex("CreatedTimeStamp")),
//                        data.getString(data.getColumnIndex("CreatedUser")), data.getString(data.getColumnIndex("Remark")));
//                arPayments.add(arPayment);
//            }
//        }
//
//        ARPaymentAdapter arrayAdapter = new ARPaymentAdapter(this,
//                arPayments, Default_curr);
//
//        Cursor dcurren = db.getReg("6");
//        if(dcurren.moveToFirst()){
//            Default_curr = dcurren.getString(0);
//        }
//
//        binding.invmenuList.setAdapter(arrayAdapter);
//        arrayAdapter.notifyDataSetChanged();
//    }
    public void getDataDesc(String substring)
    {
        Cursor data = db.getARPaymentMenuDescLike(substring);
        List<AC_Class.ARPayment> arPayments = new ArrayList<>();

        if (data.getCount() > 0)
        {
            while (data.moveToNext()) {
                AC_Class.ARPayment arPayment = new AC_Class.ARPayment(data.getString(data.getColumnIndex("DocNo")),data.getString(data.getColumnIndex("Date")), data.getString(data.getColumnIndex("DebtorCode")),data.getString(data.getColumnIndex("DebtorName")),data.getDouble(data.getColumnIndex("Amount")),data.getInt(data.getColumnIndex("Uploaded")),data.getString(data.getColumnIndex("CreatedTimeStamp")),
                        data.getString(data.getColumnIndex("CreatedUser")), data.getString(data.getColumnIndex("Remark")));
                arPayments.add(arPayment);
            }
        }

        ARPaymentAdapter arrayAdapter = new ARPaymentAdapter(this,
                arPayments, Default_curr);

        Cursor dcurren = db.getReg("6");
        if(dcurren.moveToFirst()){
            Default_curr = dcurren.getString(0);
        }

        binding.invmenuList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

}