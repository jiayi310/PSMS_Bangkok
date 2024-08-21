package com.example.androidmobilestock;

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

import com.example.androidmobilestock.databinding.PlActivityPllistBinding;

import java.util.ArrayList;
import java.util.List;

public class PL_PLList extends AppCompatActivity {

    PlActivityPllistBinding binding;
    MyClickHandler handler;
    ACDatabase db;
    String Default_curr;

    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pl_activity_pllist);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Packing List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        searchEditText = (EditText) findViewById(R.id.searchField);

        handler = new MyClickHandler(this);
        db = new ACDatabase(this);
        getDataDesc("");
        binding.dOList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PL_PLList.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");

                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int uploaded = ((AC_Class.DO)parent.getItemAtPosition(position)).getUploaded();
                        if(uploaded == 1) {
                            Toast.makeText(PL_PLList.this, "Action Failed. The Packing List already uploaded.",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            AC_Class.DO myDO = (AC_Class.DO)parent.getItemAtPosition(position);
                            Intent intent = new Intent(PL_PLList.this, PL_PL.class);
                            intent.putExtra("iPackingList", myDO);
                            intent.putExtra("FunctionKey", "Edit");
                            startActivity(intent);
                        }
                    }
                });

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final AlertDialog.Builder delDialog = new AlertDialog.Builder(PL_PLList.this);
                        delDialog.setTitle("Delete?");
                        delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String DocNo = ((AC_Class.DO) parent.getItemAtPosition(position)).getDocNo();
                                boolean delete = db.deletePackingList(DocNo);
                                searchEditText.setText("");
                                if (!delete) {
                                    Toast.makeText(PL_PLList.this, "Delete failed",
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
                        AC_Class.DO tempDO = (AC_Class.DO)parent.getItemAtPosition(position);
                        Intent new_intent = new Intent(PL_PLList.this, PL_MultipleTab.class);
                        new_intent.putExtra("iPackingList", tempDO);
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
        getMenuInflater().inflate(R.menu.pl_pl_menu, menu);
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
        if (id == R.id.add)
        {
            Intent new_intent = new Intent(PL_PLList.this, PL_SO_Search.class);
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
//        Cursor data = db.getDOLike(substring);
//        List<AC_Class.DO> myDOList = new ArrayList<>();
//        if (data.getCount() > 0)
//        {
//            while (data.moveToNext()) {
//                boolean value = data.getInt(9) > 0;
//                AC_Class.DO myDO = new AC_Class.DO(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getInt(6), data.getString(7), data.getString(8), value);
//                myDOList.add(myDO);
//            }
//        }
//
//        PL_PLListViewAdapter arrayAdapter = new PL_PLListViewAdapter(this,
//                myDOList);
//
//        binding.dOList.setAdapter(arrayAdapter);
//        arrayAdapter.notifyDataSetChanged();
//    }

    public void getDataDesc(String substring)
    {
        Cursor data = db.getDODescLike(substring);
        List<AC_Class.DO> myDOList = new ArrayList<>();
        if (data.getCount() > 0)
        {
            while (data.moveToNext()) {
                boolean value = data.getInt(9) > 0;
                AC_Class.DO myDO = new AC_Class.DO(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getInt(6), data.getString(7), data.getString(8), value);
                myDOList.add(myDO);
            }
        }

        PL_PLListViewAdapter arrayAdapter = new PL_PLListViewAdapter(this,
                myDOList);

        binding.dOList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }
}
