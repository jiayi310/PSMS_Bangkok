package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.PurActivityPurchaselistBinding;

import java.util.ArrayList;
import java.util.List;

public class PUR_PurchaseList extends AppCompatActivity {

    PurActivityPurchaselistBinding binding;
    MyClickHandler handler;
    ACDatabase db;
    String Default_curr;

    EditText searchEditText;
    boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pur_activity_purchaselist);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Purchase");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchEditText = (EditText) findViewById(R.id.searchField);

        handler = new MyClickHandler(this);
        db = new ACDatabase(this);
        getDataDesc("");
        binding.lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PUR_PurchaseList.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");

                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int uploaded = ((AC_Class.PurchaseMenu)parent.getItemAtPosition(position)).getUploaded();
                        if(uploaded == 1) {
                            Toast.makeText(PUR_PurchaseList.this, "Action Failed. The Purchase already uploaded.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            String DocNo = ((AC_Class.PurchaseMenu)parent.getItemAtPosition(position)).getDocNo();
                            Intent intent = new Intent(PUR_PurchaseList.this, PUR_Purchase.class);
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
                        final AlertDialog.Builder delDialog = new AlertDialog.Builder(PUR_PurchaseList.this);
                        delDialog.setTitle("Delete?");
                        delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String DocNo = ((AC_Class.PurchaseMenu) parent.getItemAtPosition(position)).getDocNo();
                                boolean delete = db.deletePurchaseDetails(DocNo);
                                searchEditText.setText("");
                                if (!delete) {
                                    Toast.makeText(PUR_PurchaseList.this, "Delete failed",
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
                        String docNo = ((AC_Class.PurchaseMenu)parent.getItemAtPosition(position)).getDocNo();
                        String creditor = ((AC_Class.PurchaseMenu)parent.getItemAtPosition(position)).getCreditorCode();

                        Intent new_intent = new Intent(PUR_PurchaseList.this, PUR_MultipleTab.class);
                        new_intent.putExtra("iDocNo", docNo);
                        new_intent.putExtra("iCreditorCode", creditor);
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
            Intent new_intent = new Intent(PUR_PurchaseList.this, PUR_Purchase.class);
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
//        Cursor data = db.getPurchaseMenuLike(substring);
//        List<AC_Class.PurchaseMenu> menuList = new ArrayList<>();
//
//        if (data.getCount() > 0)
//        {
//            while (data.moveToNext()) {
//                AC_Class.PurchaseMenu menu = new AC_Class.PurchaseMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(5), data.getDouble(6), data.getDouble(7), data.getInt(8), data.getString(9));
//                menuList.add(menu);
//            }
//        }
//
//        PUR_PurchaseListViewAdapter arrayAdapter = new PUR_PurchaseListViewAdapter(this,
//                menuList, Default_curr);
//
//        Cursor dcurren = db.getReg("6");
//        if(dcurren.moveToFirst()){
//            Default_curr = dcurren.getString(0);
//        }
//
//        binding.lvList.setAdapter(arrayAdapter);
//        arrayAdapter.notifyDataSetChanged();
//    }

    public void getDataDesc(String substring)
    {
        Cursor data = db.getPurchaseMenuDescLike(substring);
        List<AC_Class.PurchaseMenu> menuList = new ArrayList<>();

        if (data.getCount() > 0)
        {
            while (data.moveToNext()) {
                AC_Class.PurchaseMenu menu = new AC_Class.PurchaseMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(5), data.getDouble(6), data.getDouble(7), data.getInt(8), data.getString(9), data.getString(10), data.getString(11), data.getString(12));
                menuList.add(menu);
            }
        }

        PUR_PurchaseListViewAdapter arrayAdapter = new PUR_PurchaseListViewAdapter(this,
                menuList, Default_curr);

        Cursor dcurren = db.getReg("6");
        if(dcurren.moveToFirst()){
            Default_curr = dcurren.getString(0);
        }

        binding.lvList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }
}
