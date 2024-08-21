package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
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
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.adapter.StockAssemblyListAdapter;
import com.example.androidmobilestock.databinding.ActivityStockTakeUploadStatusBinding;

import java.util.ArrayList;
import java.util.List;

public class Setting_Status_SA extends AppCompatActivity {
    List<AC_Class.StockAssemblyMenu> myList = new ArrayList<>();
    List<AC_Class.StockAssemblyMenu> filteredList = new ArrayList<>();
    ACDatabase db;
    ListView st_menu_list;
    StockAssemblyListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_upload_status);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Stock Assembly Status");

        db = new ACDatabase(this);

        st_menu_list = findViewById(R.id.st_menu_list);

        myList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new StockAssemblyListViewAdapter(this, myList);
        st_menu_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getDataDesc();

        st_menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.StockAssemblyMenu currItem = (AC_Class.StockAssemblyMenu) parent.getItemAtPosition(position);

                //AC_Class.StockAssembly sa = db.getStockAssembly(currItem);

                StockAssemblyListViewAdapter adapter = (StockAssemblyListViewAdapter) st_menu_list.getAdapter();

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
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Setting_Status_SA.this);
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
                        for (AC_Class.StockAssemblyMenu currInv : filteredList) {
//                            View currChild = binding.stMenuList
//                                    .getChildAt(mylist.indexOf(currInv));
//                            currChild.setBackgroundColor(getBaseContext()
//                                    .getResources().getColor(R.color.MintCream));
                            if(currInv != null)
                            {
                                db.setStockAssemblyUploadedTo(currInv.getDocNo(), state);
                            }
                        }
                        // Update Available data
                        getDataDesc();

                        // Notify list changed
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(Setting_Status_SA.this);
                        countNote.setMessage("Changed "+filteredList.size()+"stock take upload status(es).");
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

    public void getDataDesc()
    {
        myList.clear();
        Cursor cursor = db.getAllDocumentsSADesc();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String docNo = cursor.getString(cursor.getColumnIndex("DocNo"));

                String date = cursor.getString(cursor.getColumnIndex("Date"));

                String remarks = cursor.getString(cursor.getColumnIndex("Remark"));


                int uploaded = cursor.getInt(cursor.getColumnIndex("Uploaded"));

                AC_Class.StockAssemblyMenu stockAssembly = new AC_Class.StockAssemblyMenu(docNo, date, remarks, uploaded);
                myList.add(stockAssembly);
            }
        } else {
            Log.d("Stock Assembly Modify Status", "No data");
        }
        cursor.close();
        adapter.notifyDataSetChanged();

    }

}
