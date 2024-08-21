package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidmobilestock.adapter.InvoiceListViewAdapter;
import com.example.androidmobilestock.databinding.ActivitySalesUploadStatusBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesUploadStatus extends AppCompatActivity {
    ActivitySalesUploadStatusBinding binding;
    ArrayList<AC_Class.InvoiceMenu> inv_list;
    List<AC_Class.InvoiceMenu> filteredList;
    ACDatabase db;
    Date fromDate;
    Date toDate;
    InvoiceListViewAdapter arrayAdapter;
    SalesUploadStatus.MyClickHandler clickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales_upload_status);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sales Status");

        db = new ACDatabase(SalesUploadStatus.this);

        clickHandler = new MyClickHandler(this);
        binding.setClickHandler(clickHandler);

        inv_list = new ArrayList<>();
        arrayAdapter = new InvoiceListViewAdapter(this,
                inv_list, "");
        binding.salesMenuList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        getData();

        filteredList = new ArrayList<>();
        binding.salesMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.InvoiceMenu currItem = (AC_Class.InvoiceMenu) parent.getItemAtPosition(position);
                InvoiceListViewAdapter adapter = (InvoiceListViewAdapter) binding.salesMenuList.getAdapter();

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
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SalesUploadStatus.this);
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
                        for (AC_Class.InvoiceMenu currInv : filteredList) {
//                            View currChild = binding.salesMenuList
//                                    .getChildAt(inv_list.indexOf(currInv));
//                            currChild.setBackgroundColor(getBaseContext()
//                                    .getResources().getColor(R.color.MintCream));
                            db.setSalesUploadedTo(currInv.getDocNo(), state);
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        arrayAdapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(SalesUploadStatus.this);
                        countNote.setMessage("Changed "+filteredList.size()+" invoice upload status(es).");
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
        Cursor allSales = db.getInvoiceMenuDescLike("");
        inv_list.clear();
        while (allSales.moveToNext()){

            inv_list.add(new AC_Class.InvoiceMenu(
                    allSales.getString(0), allSales.getString(1),
                    allSales.getString(2), allSales.getString(3),
                    allSales.getString(4), allSales.getString(4), allSales.getString(5),
                    allSales.getDouble(6), allSales.getDouble(7),
                    allSales.getDouble(8), allSales.getInt(9),
                    allSales.getString(10),allSales.getString(11)
                    ,allSales.getString(12),allSales.getString(13),allSales.getString(14)));
        }
        arrayAdapter.notifyDataSetChanged();

    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context){
            this.context = context;
        }

    }
}
