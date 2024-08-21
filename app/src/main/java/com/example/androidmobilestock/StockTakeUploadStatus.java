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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidmobilestock.adapter.StockAssemblyListAdapter;
import com.example.androidmobilestock.databinding.ActivityStockTakeUploadStatusBinding;

import java.util.ArrayList;
import java.util.List;

public class StockTakeUploadStatus extends AppCompatActivity {
    ActivityStockTakeUploadStatusBinding binding;
    ArrayList<AC_Class.StockTakeMenu> mylist;
    List<AC_Class.StockTake> filteredList;
    ACDatabase db;
    StockAssemblyListAdapter adapter;
    StockTakeUploadStatus.MyClickHandler clickHandler;
    Cursor data;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_take_upload_status);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Stock Take Status");

        db = new ACDatabase(StockTakeUploadStatus.this);

        clickHandler = new MyClickHandler(this);
        binding.setClickHandler(clickHandler);

        mylist = new ArrayList<>();
//        arrayAdapter = new StockAssemblyListAdapter(this, docNo, date, remarks, uploaded);
//        binding.stMenuList.setAdapter(arrayAdapter);
//        arrayAdapter.notifyDataSetChanged();
        getData();

        filteredList = new ArrayList<>();
        binding.stMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currItem = binding.stMenuList.getItemAtPosition(position).toString();
                AC_Class.StockTake sa = db.getStockTake(currItem);

                int index = filteredList.indexOf(currItem);
                if (index != -1) {
                    filteredList.remove(index);
                    view.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    filteredList.add(sa);
                    view.setBackgroundColor(Color.parseColor("#ffe4e1"));
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
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(StockTakeUploadStatus.this);
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
//                            View currChild = binding.stMenuList
//                                    .getChildAt(mylist.indexOf(currInv));
//                            currChild.setBackgroundColor(getBaseContext()
//                                    .getResources().getColor(R.color.MintCream));
                            if(currInv != null)
                            {
                                db.setStockTakeUploadedTo(currInv.getDocNo(), state);
                            }
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        adapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(StockTakeUploadStatus.this);
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

    public void getData(){

        data = db.getAllDocumentsSTDesc();
        size = data.getCount();

        final String[] docNo = new String[size];
        final String[] date = new String[size];
        final String[] remarks = new String[size];
        final Integer[] uploaded = new Integer[size];
        displayList(docNo,date,remarks,uploaded);
//        Cursor data = db.getStockTakeMenuLike("");
//        mylist.clear();
//        while (data.moveToNext()){
//            mylist.add(new AC_Class.StockTakeMenu(
//                    data.getString(0), data.getString(1),
//                    data.getString(2), data.getString(3),
//                    data.getString(4), data.getString(5),
//                    data.getInt(6)
//            ));
//        }
//        arrayAdapter.notifyDataSetChanged();

    }

    public void displayList(String[] docNo_,String[] date_, String[] remarks_, Integer[] uploaded_)
    {
        int i=0;

        if(data.getCount() > 0)
        {
            while (data.moveToNext())
            {
                try{
                    docNo_[i] = data.getString(data.getColumnIndex("DocNo"));
                    date_[i] = data.getString(data.getColumnIndex("Date"));
                    remarks_[i] = data.getString(data.getColumnIndex("Remark"));
                    uploaded_[i] = data.getInt(data.getColumnIndex("Uploaded"));
                    i++;
                }catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
        }


        adapter(docNo_,date_, remarks_,uploaded_);
    }

    public void adapter(String[] docNo,String[] date, String[] remarks,Integer[] uploaded)
    {
        adapter=new StockAssemblyListAdapter(this, docNo, date, remarks, uploaded);
        binding.stMenuList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context){
            this.context = context;
        }

    }
}
