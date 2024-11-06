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

public class Setting_Status_JS extends AppCompatActivity {

    ACDatabase db;
    ListView lv_list;
    Jobsheet_ListViewAdapter adapter;
    List<AC_Class.JobSheet> myList = new ArrayList<>();
    List<AC_Class.JobSheet> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_status_js);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Job Sheet Status");

        db = new ACDatabase(this);

        lv_list = findViewById(R.id.lv_list);

        myList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new Jobsheet_ListViewAdapter(this, myList);
        lv_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getData();

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.JobSheet currItem = (AC_Class.JobSheet) parent.getItemAtPosition(position);
                Jobsheet_ListViewAdapter adapter = (Jobsheet_ListViewAdapter) lv_list.getAdapter();

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

    private void logFilteredList() {
        Log.d("FilteredList", "Filtered List Items:");
        for (AC_Class.JobSheet jobSheet : filteredList) {
            String logMessage = "DocNo: " + jobSheet.getDocNo() +
                    ", DocDate: " + jobSheet.getDocDate() +
                    ", WorkType: " + jobSheet.getWorkType() +
                    ", ReplacementType: " + jobSheet.getReplacementType() +
                    ", Uploaded: " + jobSheet.getUploaded() +
                    ", Status: " + jobSheet.getStatus() +
                    ", SalesNo: " + jobSheet.getSalesNo() +
                    ", SalesAgent: " + jobSheet.getAgent() +
                    ", DebtorCode: " + jobSheet.getDebtorCode();
            Log.d("FilteredList", logMessage);
        }
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
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Setting_Status_JS.this);
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
                        for (AC_Class.JobSheet currInv : filteredList) {
                            Log.d("FilteredList: ", "docNO: " + currInv.getDocNo());
                            db.setJobSheetUploadedTo(currInv.getDocNo(), state);
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(Setting_Status_JS.this);
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
        Cursor cursor = db.getJobSheet();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String docNo = cursor.getString(cursor.getColumnIndex("DocNo"));
                String docDate = cursor.getString(cursor.getColumnIndex("DocDate"));
                String workType = cursor.getString(cursor.getColumnIndex("WorkType"));
                String replacementType = cursor.getString(cursor.getColumnIndex("ReplacementType"));
                int uploaded = cursor.getInt(cursor.getColumnIndex("Uploaded"));
                String status = cursor.getString(cursor.getColumnIndex("Status"));
                String salesNo = cursor.getString(cursor.getColumnIndex("SalesNo"));
                String agent = cursor.getString(cursor.getColumnIndex("SalesAgent"));
                String debtorCode = cursor.getString(cursor.getColumnIndex("DebtorCode"));

                AC_Class.JobSheet jobSheet = new AC_Class.JobSheet(docNo, docDate, workType, replacementType, uploaded, status, salesNo, agent, debtorCode);
                myList.add(jobSheet);
            }

        }
        cursor.close();
        adapter.notifyDataSetChanged();

    }
}