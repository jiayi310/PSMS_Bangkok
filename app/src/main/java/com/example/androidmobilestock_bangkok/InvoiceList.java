package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.adapter.InvoiceListViewAdapter;
import com.example.androidmobilestock_bangkok.databinding.ActivityInvoiceListBinding;

import java.util.ArrayList;
import java.util.List;

public class InvoiceList extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ActivityInvoiceListBinding binding;
    MyClickHandler handler;
    ACDatabase db;
    String Default_curr;

    EditText searchEditText;
    boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_list);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.menu_invoice));
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchEditText = (EditText) findViewById(R.id.searchField);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Sales_Status, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_item_text);
        binding.filter.setAdapter(adapter);
        binding.filter.setOnItemSelectedListener(this);

        handler = new MyClickHandler(this);
        db = new ACDatabase(this);
        getDataDesc("");
        binding.invmenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceList.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");


                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int uploaded = ((AC_Class.InvoiceMenu)parent.getItemAtPosition(position)).getUploaded();
                        String status = ((AC_Class.InvoiceMenu)parent.getItemAtPosition(position)).getStatus();
                        if(uploaded == 1 ) {
                            if (status != null){
                                if(status.equals("Rejected")){
                                    String DocNo = ((AC_Class.InvoiceMenu) parent.getItemAtPosition(position)).getDocNo();
                                    Intent intent = new Intent(InvoiceList.this, Invoice.class);
                                    intent.putExtra("DocNoKey", DocNo);
                                    intent.putExtra("FunctionKey", "Edit");
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(InvoiceList.this, "Action Failed. The Invoice already uploaded.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            String DocNo = ((AC_Class.InvoiceMenu) parent.getItemAtPosition(position)).getDocNo();
                            Intent intent = new Intent(InvoiceList.this, Invoice.class);
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
                        final AlertDialog.Builder delDialog = new AlertDialog.Builder(InvoiceList.this);
                        delDialog.setTitle("Delete?");
                        delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String DocNo = ((AC_Class.InvoiceMenu) parent.getItemAtPosition(position)).getDocNo();
                                boolean delete = db.deleteInvoiceDetails(DocNo);
                                boolean updateJS = db.updateJSSalesNo(DocNo);
                                searchEditText.setText("");
                                if (!delete) {
                                    Toast.makeText(InvoiceList.this, "Delete failed",
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
                        Intent new_intent = new Intent(InvoiceList.this, InvoiceDtlMultipleTab.class);
                        new_intent.putExtra("docNo", docNo);
                        new_intent.putExtra("debtorCode", debtor);
                        startActivity(new_intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        binding.invmenuList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceList.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");

                builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String docNo = ((AC_Class.InvoiceMenu)parent.getItemAtPosition(position)).getDocNo();

                        Intent new_intent = new Intent(InvoiceList.this, Invoice.class);
                        new_intent.putExtra("DocNoKey", docNo);
                        new_intent.putExtra("FunctionKey", "Copy");
                        startActivity(new_intent);

                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
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
            Intent new_intent = new Intent(InvoiceList.this, Invoice.class);
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
    }

    public void getDataDesc(String substring)
    {
        Cursor data = db.getInvoiceMenuDescLike(substring);
        List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();
        if (data.getCount() > 0)
        {
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
