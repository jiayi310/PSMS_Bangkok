package com.example.androidmobilestock_bangkok;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StockReceive extends AppCompatActivity implements StockReceiveAdapter.MyViewHolder.RecyclerViewClickListener {

    TextView no_history_textView;
    EditText searchField;
    RecyclerView stockReceivedRecyclerView;
    StockReceiveAdapter adapter;
    List<AC_Class.StockReceive> stockReceiveList;
    ACDatabase db;
    List<AC_Class.StockReceive> originalStockReceiveList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_receive);

        searchField = findViewById(R.id.searchField);
        stockReceivedRecyclerView = findViewById(R.id.stockReceivedList);
        no_history_textView = findViewById(R.id.no_history_textView);

        db = new ACDatabase(this);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stock Receive");
        actionBar.setDisplayHomeAsUpEnabled(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        stockReceivedRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        stockReceivedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockReceiveList = new ArrayList<>();

        loadStockReceivedRecords();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStockReceiveList(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void filterStockReceiveList(String query) {
        if (query.isEmpty()) {
            adapter.updateList(originalStockReceiveList);
        } else {
            List<AC_Class.StockReceive> filteredList = new ArrayList<>();
            for (AC_Class.StockReceive item : originalStockReceiveList) {
                if (item.getDocNo().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDocDate().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                        item.getRemarks().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            adapter.updateList(filteredList);
        }
    }


    private void loadStockReceivedRecords() {
        stockReceiveList.clear();
        Cursor cursor = db.getStockReceive();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                no_history_textView.setVisibility(View.GONE);
                String docNo = cursor.getString(cursor.getColumnIndex("DocNo"));
                String docDate = cursor.getString(cursor.getColumnIndex("DocDate"));
                String description = cursor.getString(cursor.getColumnIndex("Description"));
                String remarks = cursor.getString(cursor.getColumnIndex("Remarks"));
                String createdTime = cursor.getString(cursor.getColumnIndex("CreatedTimeStamp"));
                String createdUser = cursor.getString(cursor.getColumnIndex("CreatedUser"));
                int uploaded = cursor.getInt(cursor.getColumnIndex("Uploaded"));

                AC_Class.StockReceive stockReceive = new AC_Class.StockReceive(docNo, docDate, description, remarks, createdTime, createdUser, uploaded);
                stockReceiveList.add(stockReceive);
            }
            //sort the list
            Collections.sort(stockReceiveList, new Comparator<AC_Class.StockReceive>() {
                @Override
                public int compare(AC_Class.StockReceive o1, AC_Class.StockReceive o2) {
                    return o2.getDocNo().compareTo(o1.getDocNo()); // descending order
                }
            });
        } else {
            no_history_textView.setVisibility(View.VISIBLE);
        }
        cursor.close();

        originalStockReceiveList = new ArrayList<>(stockReceiveList);

        adapter = new StockReceiveAdapter(this, stockReceiveList, this);
        stockReceivedRecyclerView.setAdapter(adapter);
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
            Intent intent1= new Intent(StockReceive.this, StockReceive_AddNew.class);
            intent1.putExtra("FunctionKey", "New");
            startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StockReceive.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage("What would you like to do?");

        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AC_Class.StockReceive selectedRecord = stockReceiveList.get(position);

                int uploaded = selectedRecord.getUploaded();

                if (uploaded == 1){
                    Toast.makeText(StockReceive.this, "This stock receive has been uploaded and cannot be edited.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(StockReceive.this, StockReceive_AddNew.class);
                    intent.putExtra("FunctionKey", "Edit");
                    intent.putExtra("StockReceive", selectedRecord);
                    startActivity(intent);
                }
            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final AlertDialog.Builder delDialog = new AlertDialog.Builder(StockReceive.this);
                delDialog.setTitle("Delete?");
                delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AC_Class.StockReceive selectedRecord = stockReceiveList.get(position);

                        int uploaded = selectedRecord.getUploaded();
                        if (uploaded == 1){
                            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(StockReceive.this);
                            dialog1.setTitle("Confirmation");
                            dialog1.setMessage("This job sheet has been uploaded. Do you still want to delete?");
                            dialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    boolean deleteSuccess = db.deleteStockReceive(selectedRecord.getDocNo());
                                    if (deleteSuccess) {

                                        boolean deleteDetailSuccess = db.deleteStockReceiveDetail(selectedRecord.getDocNo());
                                        if (!deleteDetailSuccess){
                                            Toast.makeText(StockReceive.this, "Error deleting records.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Successfully deleted
                                            Toast.makeText(StockReceive.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                            stockReceiveList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                        }

                                    } else {
                                        // Deletion failed
                                        Toast.makeText(StockReceive.this, "Deletion failed", Toast.LENGTH_SHORT).show();
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
                AC_Class.StockReceive selectedRecord = stockReceiveList.get(position);
                Intent intent = new Intent(StockReceive.this, StockReceive_Details.class);
                intent.putExtra("FunctionKey", "Details");
                intent.putExtra("StockReceive", selectedRecord);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}