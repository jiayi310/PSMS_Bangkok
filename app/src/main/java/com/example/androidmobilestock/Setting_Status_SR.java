package com.example.androidmobilestock;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

    public class Setting_Status_SR extends AppCompatActivity {

        ACDatabase db;
        ListView lv_list;
        StockReceive_ListViewAdapter adapter;
        List<AC_Class.StockReceive> myList = new ArrayList<>();
        List<AC_Class.StockReceive> filteredList = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setting_status_sr);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Stock Receive Status");

            db = new ACDatabase(this);

            lv_list = findViewById(R.id.lv_list);

            myList = new ArrayList<>();
            filteredList = new ArrayList<>();

            adapter = new StockReceive_ListViewAdapter(this, myList);
            lv_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            getData();

            lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.StockReceive currItem = (AC_Class.StockReceive) parent.getItemAtPosition(position);
                    StockReceive_ListViewAdapter adapter = (StockReceive_ListViewAdapter) lv_list.getAdapter();

                    if (adapter.getSelectedItems().contains(currItem)) {
                        filteredList.remove(currItem);
                        adapter.deselectItem(currItem);
                    } else {
                        filteredList.add(currItem);
                        adapter.selectItem(currItem);
                    }
                }
            });

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.reset_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;

                case R.id.resetBttn:
                    // Quick and Dirty
                    final List<String> options = new ArrayList<>();
                    options.add("True");
                    options.add("False");
                    final CharSequence[] cs = options.toArray(new CharSequence[2]);
                    AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(com.example.androidmobilestock.Setting_Status_SR.this);
                    builder.setTitle("Set Upload Status to:");
                    builder.setItems(cs, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int state = 0;
                            switch (cs[which].toString()) {
                                case "True":
                                    state = 1;
                                    break;
                                case "False":
                                    state = 0;
                                    break;
                            }
                            for (AC_Class.StockReceive currInv : filteredList) {
                                db.setStockReceiveUploadedTo(currInv.getDocNo(), state);
                            }
                            // Update Available data
                            getData();

                            // Notify list changed
                            adapter.notifyDataSetChanged();
                            AlertDialog.Builder countNote = new AlertDialog.Builder(com.example.androidmobilestock.Setting_Status_SR.this);
                            countNote.setMessage("Changed " + filteredList.size() + " job sheet upload status(es).");
                            countNote.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filteredList.clear();
                                    dialog.dismiss();
                                }
                            });
                            countNote.show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
            return false;
        }

        public void getData() {
            myList.clear();
            Cursor cursor = db.getStockReceive();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String docNo = cursor.getString(cursor.getColumnIndex("DocNo"));
                    String docDate = cursor.getString(cursor.getColumnIndex("DocDate"));
                    String description = cursor.getString(cursor.getColumnIndex("Description"));
                    String remarks = cursor.getString(cursor.getColumnIndex("Remarks"));
                    String createdTime = cursor.getString(cursor.getColumnIndex("CreatedTimeStamp"));
                    String createdUser = cursor.getString(cursor.getColumnIndex("CreatedUser"));
                    int uploaded = cursor.getInt(cursor.getColumnIndex("Uploaded"));

                    AC_Class.StockReceive stockReceive = new AC_Class.StockReceive(docNo, docDate, description, remarks, createdTime, createdUser, uploaded);
                    myList.add(stockReceive);

                }
                cursor.close();
                adapter.notifyDataSetChanged();

            }
        }
    }