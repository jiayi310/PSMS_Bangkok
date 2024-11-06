package com.example.androidmobilestock_bangkok;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.adapter.InvoiceListViewAdapter;
import com.example.androidmobilestock_bangkok.databinding.ActivityOrderListBinding;

import java.util.ArrayList;
import java.util.List;

public class OrderList extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ActivityOrderListBinding binding;
    InvoiceList.MyClickHandler handler;
    ACDatabase db;
    String Default_curr;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sales Invoice");
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchEditText = (EditText) findViewById(R.id.searchField);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Sales_Status, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_item_text);
        binding.filter.setAdapter(adapter);
        binding.filter.setOnItemSelectedListener(this);

        handler = new InvoiceList.MyClickHandler(this);
        db = new ACDatabase(this);
        getData("");
        binding.invmenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(OrderList.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");

                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String DocNo = ((AC_Class.InvoiceMenu)parent.getItemAtPosition(position)).getDocNo();
                        Intent intent = new Intent(OrderList.this, CartView.class);
                        intent.putExtra("DocNoKey", DocNo);
                        intent.putExtra("FunctionKey", "Edit");
                        startActivityForResult(intent,6);
                    }
                });

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final AlertDialog.Builder delDialog = new AlertDialog.Builder(OrderList.this);
                        delDialog.setTitle("Delete?");
                        delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String DocNo = ((AC_Class.InvoiceMenu) parent.getItemAtPosition(position)).getDocNo();
                                boolean delete = db.deleteInvoiceDetails(DocNo);
                                searchEditText.setText("");
                                if (!delete) {
                                    Toast.makeText(OrderList.this, "Delete failed",
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
                        String docNo = ((AC_Class.InvoiceMenu)parent.getItemAtPosition(position)).getDocNo();
                        String debtor = ((AC_Class.InvoiceMenu)parent.getItemAtPosition(position)).getDebtorCode();
                        Intent new_intent = new Intent(OrderList.this, InvoiceDtlMultipleTab.class);
                        new_intent.putExtra("docNo", docNo);
                        new_intent.putExtra("debtorCode", debtor);
                        startActivity(new_intent);
//                        String DocNo = ((AC_Class.InvoiceMenu)parent.getItemAtPosition(position)).getDocNo();
//                        AC_Class.PrintInvoice pi = new AC_Class.PrintInvoice(getApplicationContext(), DocNo);
//                        pi.FindBluetoothDevice();
//                        try {
//                            pi.openBluetoothPrinter();
//                            pi.printData();
//                            pi.disconnectBT();
//                        }
//                        catch (IOException e) {
//                            e.printStackTrace();
//                        }
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
                getData(s.toString().trim());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    protected void onResume() {
        super.onResume();
        getData("");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getSelectedItem().toString().equals("All")){
            binding.searchField.setText("");
        }else {
            binding.searchField.setText(adapterView.getSelectedItem().toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

//        public void OnbtnAddClicked(View view) {
//            Intent new_intent = new Intent(Invoice_Menu.this, Add_Invoice_Header.class);
//            new_intent.putExtra("FunctionKey", "New");
//            startActivity(new_intent);
//            finish();
//        }
    }

    public void getData(String substring)
    {
        Cursor data = db.getInvoiceMenuDescLike(substring);
        List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();
        //SharedPreferences prefs = getSharedPreferences("com.presoft.androidmobilestock", Context.MODE_PRIVATE);

        if (data.getCount() > 0)
        {
//            Log.i("custDebug", "debug");
            while (data.moveToNext()) {
                AC_Class.InvoiceMenu invoiceMenu = new AC_Class.InvoiceMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(6), data.getDouble(7), data.getDouble(8), data.getDouble(9), data.getInt(10),data.getString(11), data.getString(12), data.getString(13), data.getString(14), data.getString(15));

                invoiceMenus.add(invoiceMenu);
            }
        }

        InvoiceListViewAdapter arrayAdapter = new InvoiceListViewAdapter(this,
                invoiceMenus, Default_curr);

        Cursor dcurren = db.getReg("6");
        if(dcurren.moveToFirst()){
            Default_curr = dcurren.getString(0);
        }

        binding.invmenuList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }
}