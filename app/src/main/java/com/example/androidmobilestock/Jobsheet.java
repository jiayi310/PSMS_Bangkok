package com.example.androidmobilestock;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Jobsheet extends AppCompatActivity implements JobsheetAdapter.MyViewHolder.RecyclerViewClickListener {

    TextView no_history_textView;
    EditText searchField;
    ACDatabase db;
    RecyclerView jobSheetRecyclerView;
    List<AC_Class.JobSheet> jobSheetList, originalJobSheetList;
    JobsheetAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet);

        db = new ACDatabase(this);

        searchField = findViewById(R.id.searchField);
        no_history_textView = findViewById(R.id.no_history_textView);
        jobSheetRecyclerView = findViewById(R.id.stockReceivedList);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Job Sheet");
        actionBar.setDisplayHomeAsUpEnabled(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        jobSheetRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        jobSheetRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobSheetList = new ArrayList<>();

        loadJobSheetRecords();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJobSheetList(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void filterJobSheetList(String query) {
        if (query.isEmpty()){
            adapter.updateList(originalJobSheetList);
        } else {
            List<AC_Class.JobSheet> filteredList = new ArrayList<>();
            for (AC_Class.JobSheet item : originalJobSheetList) {
                if (item.getDocNo().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDocDate().toLowerCase().contains(query.toLowerCase()) ||
                        item.getWorkType().toLowerCase().contains(query.toLowerCase()) ||
                        item.getWorkType().toLowerCase().contains(query.toLowerCase()) ||
                        item.getReplacementType().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            adapter.updateList(filteredList);
        }
    }

    private void loadJobSheetRecords() {
        jobSheetList.clear();
        Cursor cursor = db.getJobSheet();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                no_history_textView.setVisibility(View.GONE);
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
                jobSheetList.add(jobSheet);
            }
            Collections.sort(jobSheetList, new Comparator<AC_Class.JobSheet>() {
                @Override
                public int compare(AC_Class.JobSheet o1, AC_Class.JobSheet o2) {
                    return o2.getDocNo().compareTo(o1.getDocNo()); // descending order
                }
            });
        } else {
            no_history_textView.setVisibility(View.VISIBLE);
        }
        cursor.close();

        originalJobSheetList = new ArrayList<>(jobSheetList);

        adapter = new JobsheetAdapter(this, jobSheetList, this);
        jobSheetRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ppl_ppl_menu, menu);
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
            Intent intent1= new Intent(Jobsheet.this, Jobsheet_AddNew.class);
            intent1.putExtra("FunctionKey", "New");
            startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Jobsheet.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage("What would you like to do?");

        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AC_Class.JobSheet selectedRecord = jobSheetList.get(position);

                int uploaded = selectedRecord.getUploaded();

                if (uploaded == 1) {
                    Toast.makeText(Jobsheet.this, "This job sheet cannot be edited.", Toast.LENGTH_SHORT).show();
                } else if (selectedRecord.getSalesNo()!=null) {

                    Toast.makeText(Jobsheet.this, "This job sheet cannot be edited.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1= new Intent(Jobsheet.this, Jobsheet_AddNew.class);
                    intent1.putExtra("FunctionKey", "Edit");
                    intent1.putExtra("docNo", selectedRecord.getDocNo());
                    startActivity(intent1);
                }


            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final AlertDialog.Builder delDialog = new AlertDialog.Builder(Jobsheet.this);
                delDialog.setTitle("Delete?");
                delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AC_Class.JobSheet selectedRecord = jobSheetList.get(position);

                        int uploaded = selectedRecord.getUploaded();
                        String convertToSales = selectedRecord.getSalesNo();

                        if (!convertToSales.isEmpty() || convertToSales != null){
                            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(Jobsheet.this);
                            dialog1.setMessage("This job sheet cannot be delete");
                            dialog1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });
                            dialog1.show();
                        } else if (uploaded == 1){
                            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(Jobsheet.this);
                            dialog1.setMessage("This job sheet has been uploaded. Do you still want to delete?");
                            dialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    boolean deleteSuccess = db.deleteJobSheet(selectedRecord.getDocNo());

                                    boolean deleteDetailSuccess = db.deleteJobSheetDetails(selectedRecord.getDocNo());
                                    if (deleteSuccess) {
                                        // Successfully deleted
                                        Toast.makeText(Jobsheet.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                        jobSheetList.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    } else {
                                        // Deletion failed
                                        Toast.makeText(Jobsheet.this, "Delete failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            });
                            dialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });
                            dialog1.show();

                        } else {
                            boolean deleteSuccess = db.deleteJobSheet(selectedRecord.getDocNo());

                            boolean deleteDetailSuccess = db.deleteJobSheetDetails(selectedRecord.getDocNo());
                            if (deleteSuccess) {
                                // Successfully deleted
                                Toast.makeText(Jobsheet.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                jobSheetList.remove(position);
                                adapter.notifyItemRemoved(position);
                            } else {
                                // Deletion failed
                                Toast.makeText(Jobsheet.this, "Delete failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }

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
                AC_Class.JobSheet selectedRecord = jobSheetList.get(position);
                Intent intent = new Intent(Jobsheet.this, Jobsheet_Details.class);
                intent.putExtra("FunctionKey", "Details");
                intent.putExtra("docNo", selectedRecord.getDocNo());
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}