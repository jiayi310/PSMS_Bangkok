package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.androidmobilestock.adapter.StockTakeListAdapter;
import com.example.androidmobilestock.databinding.ActivityStockCountHomeBinding;

public class StockCountHome extends AppCompatActivity {

    ActivityStockCountHomeBinding binding;
    public StockCountHome.MyClickHandler handler;
    AC_Class.StockTakeMenu tempStockTakeMenu = new AC_Class.StockTakeMenu();
    ACDatabase db;

    Cursor data;
    int size;

    EditText searchField;
    boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count_home);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_count_home);

        searchField = (EditText) findViewById(R.id.searchField);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stock Count");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        //db.createST();

        getDataDesc();

        if(data.getCount() > 0)
        {
            binding.scListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                    // TODO Auto-generated method stub
                        //code specific to first list item
                            final AlertDialog.Builder builder = new AlertDialog.Builder(StockCountHome.this);
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setMessage("What would you like to do?");

                            final String DocNo = binding.scListView.getItemAtPosition(position).toString();

                            builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Cursor up = db.getSTUploaded(DocNo);
                                    int uploaded = 0;
                                    if (up.moveToFirst()) {
                                        uploaded = up.getInt(0);
                                    }
                                    if(uploaded == 1) {
                                        Toast.makeText(StockCountHome.this, "Action Failed. The Stock Take already uploaded.",
                                                Toast.LENGTH_SHORT).show();
                                    }else {
                                        Intent intent = new Intent(StockCountHome.this, StockTake.class);
                                        intent.putExtra("ID", DocNo);
                                        intent.putExtra("FunctionKey", "Edit");
                                        startActivity(intent);
                                    }
                                }
                            });

                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final AlertDialog.Builder delDialog = new AlertDialog.Builder(StockCountHome.this);
                                    delDialog.setTitle("Delete?");
                                    delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            boolean delete = db.deleteStockTakeDetails(DocNo);
                                            if (!delete) {
                                                Toast.makeText(StockCountHome.this, "Delete failed",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.dismiss();
                                            finish();
                                            startActivity(getIntent());
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
                                    Cursor data = db.getStockTakeToShowInDetail(DocNo);
                                    if(data.getCount() > 0)
                                    {
                                        data.moveToFirst();

                                        tempStockTakeMenu.setDocNo(DocNo);
                                        tempStockTakeMenu.setDocDate(data.getString(data.getColumnIndex("DocDate")));
                                        tempStockTakeMenu.setAgent(data.getString(data.getColumnIndex("SalesAgent")));
                                        tempStockTakeMenu.setLocation(data.getString(data.getColumnIndex("Location")));
                                        tempStockTakeMenu.setRemarks(data.getString(data.getColumnIndex("Remarks")));
                                        tempStockTakeMenu.setTotalQty(String.valueOf(1));
                                    }

                                    Intent new_intent = new Intent(StockCountHome.this, StockTakeDtlMultipleTab.class);
                                    new_intent.putExtra("STMenu", tempStockTakeMenu);
                                    new_intent.putExtra("ModeChosen","Details");
                                    startActivity(new_intent);

                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                }
            });

            searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.wtf("Edit Text", String.valueOf(s));
                    data = db.getFilteredDocumentsST(s.toString().trim());
                    size = data.getCount();

                    String[] docNo_ = new String[size];
                    String[] date_ = new String[size];
                    String[] salesAgent_ = new String[size];
                    String[] remarks_ = new String[size];
                    Integer[] uploaded_ = new Integer[size];

                    displayList(docNo_,date_,salesAgent_,remarks_,uploaded_);
                }
            });

        }
    }

    public void getData()
    {
        data = db.getAllDocumentsST();
        size = data.getCount();

        final String[] docNo = new String[size];
        final String[] date = new String[size];
        final String[] salesAgent = new String[size];
        final String[] remarks = new String[size];
        final Integer[] uploaded = new Integer[size];
        displayList(docNo,date,salesAgent,remarks,uploaded);
    }

    public void getDataDesc()
    {
        data = db.getAllDocumentsSTDesc();
        size = data.getCount();

        final String[] docNo = new String[size];
        final String[] date = new String[size];
        final String[] salesAgent = new String[size];
        final String[] remarks = new String[size];
        final Integer[] uploaded = new Integer[size];
        displayList(docNo,date,salesAgent,remarks,uploaded);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inv_stocktake, menu);
        return true;
    }

    public void adapter(String[] docNo,String[] date,String[] sa, String[] remarks,Integer[] uploaded)
    {
        StockTakeListAdapter adapter=new StockTakeListAdapter(this, docNo, date, sa, remarks, uploaded);
        binding.scListView.setAdapter((ListAdapter) adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDataDesc();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /*public void displayDocumentList()
    {
        Cursor data2;
        data2 = db.getDocumentList();

        if(data2.getCount() > 0)
        {
            while (data2.moveToNext())
            {
                try{
                    Log.wtf("ID",data2.getString(data2.getColumnIndex("ID")));
                    Log.wtf("TS",data2.getString(data2.getColumnIndex("CreatedTimeStamp")));
                    Log.wtf("Date",data2.getString(data2.getColumnIndex("DocDate")));
                    Log.wtf("SA",data2.getString(data2.getColumnIndex("SalesAgent")));
                    Log.wtf("R",data2.getString(data2.getColumnIndex("Remarks")));
                    Log.wtf("E",data2.getString(data2.getColumnIndex("Exported")));
                }catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
        }
    }*/

    public void displayList(String[] docNo_,String[] date_,String[] sa_, String[] remarks_, Integer[] uploaded_)
    {
        int i=0;

        if(data.getCount() > 0)
            {
                while (data.moveToNext())
                {
                    try{
                        sa_[i] = data.getString(data.getColumnIndex("SalesAgent"));
                        docNo_[i] = data.getString(data.getColumnIndex("DocNo"));
                        date_[i] = data.getString(data.getColumnIndex("Date"));
                        remarks_[i] = data.getString(data.getColumnIndex("Remark"));
                        uploaded_[i] = data.getInt(data.getColumnIndex("Uploaded"));
                        i++;
                    }catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
                }
            }


        adapter(docNo_,date_,sa_, remarks_,uploaded_);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent new_intent = new Intent(StockCountHome.this, StockTake.class);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.addStockCount) {
            String id_doc = db.getNextStockTakeNo();

            new_intent.putExtra("ID",id_doc);
            new_intent.putExtra("ModeChosen","ADD");
            new_intent.putExtra("FunctionKey", "New");
            startActivity(new_intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }
    }
}