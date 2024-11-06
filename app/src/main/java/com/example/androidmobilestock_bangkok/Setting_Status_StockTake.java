package com.example.androidmobilestock_bangkok;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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

public class Setting_Status_StockTake extends AppCompatActivity {

    ACDatabase db;
    ListView lv_list;
    StockTakeListViewAdapter adapter;
    List<AC_Class.StockTake> myList = new ArrayList<>();
    List<AC_Class.StockTake> filteredList = new ArrayList<>();

    Cursor data;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_status_stocktake);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Stock Take Status");

        db = new ACDatabase(this);

        lv_list = findViewById(R.id.lv_list);

        myList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new StockTakeListViewAdapter(this, myList);
        lv_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getData();

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.StockTake currItem = (AC_Class.StockTake) parent.getItemAtPosition(position);
                StockTakeListViewAdapter adapter = (StockTakeListViewAdapter) lv_list.getAdapter();

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
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.resetBttn:
                // Quick and Dirty
                final List<String> options = new ArrayList<>();
                options.add("True");
                options.add("False");
                final CharSequence[] cs = options.toArray(new CharSequence[2]);
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Setting_Status_StockTake.this);
                builder.setTitle("Set Upload Status to:");
                builder.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int state=0;
                        switch (cs[which].toString()){
                            case "True":
                                state = 1;
                                break;
                            case "False":
                                state = 0;
                                break;
                        }
                        for (AC_Class.StockTake currInv : filteredList) {
                            Log.d("FilteredList: ", "docNO: " + currInv.getDocNo());
                            db.setStockTakeUploadedTo(currInv.getDocNo(), state);
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(Setting_Status_StockTake.this);
                        countNote.setMessage("Changed "+filteredList.size()+" job sheet upload status(es).");
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

    public void getData(){
        myList.clear();
        Cursor cursor = db.getStockTake();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String docNo = cursor.getString(cursor.getColumnIndex("ID"));

                String docDate = cursor.getString(cursor.getColumnIndex("DocDate"));

                String remark = cursor.getString(cursor.getColumnIndex("Remarks"));

                int uploaded = cursor.getInt(cursor.getColumnIndex("Exported"));

                AC_Class.StockTake stockTake = new AC_Class.StockTake(docNo, docDate, remark, uploaded);
                myList.add(stockTake);;
                logFilteredList();
            }

        } else {
            Log.d("Stock Take Modify Status", "No data");
        }
        cursor.close();
        adapter.notifyDataSetChanged();

    }

    private void logFilteredList() {
        Log.d("FilteredList", "Filtered List Items:");
        for (AC_Class.StockTake jobSheet : filteredList) {
            String logMessage = "DocNo: " + jobSheet.getDocNo() +
                    ", DocDate: " + jobSheet.getDocDate() +
                    ", Uploaded: " + jobSheet.getUploaded() +
                    ", remarks: " + jobSheet.getRemarks();
            Log.d("FilteredList", logMessage);
        }
    }
}