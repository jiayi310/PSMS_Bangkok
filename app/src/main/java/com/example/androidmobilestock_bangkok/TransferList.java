package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;

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

import com.example.androidmobilestock_bangkok.databinding.ActivityTransferListBinding;

import java.util.ArrayList;
import java.util.List;

public class TransferList extends AppCompatActivity {

    ActivityTransferListBinding binding;
    TransferList.MyClickHandler handler;
    ACDatabase db;

    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_list);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.menu_transfer));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf98b88));

        searchEditText = (EditText) findViewById(R.id.searchField);

        handler = new TransferList.MyClickHandler(this);
        db = new ACDatabase(this);
        getDataDesc("");

        binding.transfermenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(TransferList.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");

                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int uploaded = ((AC_Class.TransferMenu)parent.getItemAtPosition(position)).getUploaded();
                        if(uploaded == 1) {
                            Toast.makeText(TransferList.this, "Action Failed. The Transfer already uploaded.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            String DocNo = ((AC_Class.TransferMenu)parent.getItemAtPosition(position)).getDocNo();
                            Intent intent = new Intent(TransferList.this, Transfer.class);
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
                        final AlertDialog.Builder delDialog = new AlertDialog.Builder(TransferList.this);
                        delDialog.setTitle("Delete?");
                        delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String DocNo = ((AC_Class.TransferMenu) parent.getItemAtPosition(position)).getDocNo();
                                boolean delete = db.deleteTransferDetails(DocNo);
                                searchEditText.setText("");
                                if (!delete) {
                                    Toast.makeText(TransferList.this, "Delete failed",
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
                        AC_Class.TransferMenu tempTransferMenu = (AC_Class.TransferMenu)parent.getItemAtPosition(position);
                        Intent new_intent = new Intent(TransferList.this, TransferDtlMultipleTab.class);
                        new_intent.putExtra("TransferMenu", tempTransferMenu);
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
        getMenuInflater().inflate(R.menu.transfer_menu, menu);
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
        if (id == R.id.addTr)
        {
            Intent new_intent = new Intent(TransferList.this, Transfer.class);
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

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }
    }

    public void getData(String substring)
    {
        Cursor data = db.getTransferMenuLike(substring);
        List<AC_Class.TransferMenu> transferMenus = new ArrayList<>();

        if (data.getCount() > 0)
        {
            while (data.moveToNext()) {
                AC_Class.TransferMenu transferMenu = new AC_Class.TransferMenu(data.getString(0),
                        data.getString(1), data.getString(2),
                        data.getString(3), data.getString(4), data.getFloat(5), data.getInt(6));
                transferMenus.add(transferMenu);
            }
        }

        TransferListViewAdapter arrayAdapter = new TransferListViewAdapter(this,
                transferMenus);
        binding.transfermenuList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    public void getDataDesc(String substring)
    {
        Cursor data = db.getTransferMenuDescLike(substring);
        List<AC_Class.TransferMenu> transferMenus = new ArrayList<>();

        if (data.getCount() > 0)
        {
            while (data.moveToNext()) {
                AC_Class.TransferMenu transferMenu = new AC_Class.TransferMenu(data.getString(0),
                        data.getString(1), data.getString(2),
                        data.getString(3), data.getString(4), data.getFloat(5), data.getInt(6));
                transferMenus.add(transferMenu);
            }
        }

        TransferListViewAdapter arrayAdapter = new TransferListViewAdapter(this,
                transferMenus);
        binding.transfermenuList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }
}