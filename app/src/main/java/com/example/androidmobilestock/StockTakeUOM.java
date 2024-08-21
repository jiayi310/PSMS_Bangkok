package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StockTakeUOM extends AppCompatActivity {

    String key, docNo, location, batchno;
    ListView listView;
    ACDatabase db;
    ArrayList<AC_Class.StockTakeDetails> listUOM = new ArrayList<>();
    ArrayList<AC_Class.StockTakeDetails> newlistUOM = new ArrayList<>();
    TextView textView;
    Button save, cancel;
    Double qty = 0.0;
    Boolean isBatchNoEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_uom);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Item UOM");
        actionBar.setDisplayHomeAsUpEnabled(true);

        key = getIntent().getStringExtra("ItemCode");
        docNo = getIntent().getStringExtra("DocNo");
        location = getIntent().getStringExtra("Loc");
        batchno = getIntent().getStringExtra("BatchNo");
        db = new ACDatabase(this);
        listView = (ListView) findViewById(R.id.listviewoum) ;
        textView = (TextView) findViewById(R.id.itemcodeUOM);
        textView.setText(key);
        save = (Button) findViewById(R.id.btnsaveuom);
        cancel = (Button) findViewById(R.id.btncancel);

        Cursor cursor = db.getReg("38");
        if(cursor.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor.getString(0));
        }

        getData();

        StockTakeUOMAdapter adapter = new StockTakeUOMAdapter(this, listUOM);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                for (int i=0; i<listUOM.size();i++){
//                    qty +=listUOM.get(i).getQuantity();
//                }
                for (int i=0; i<listUOM.size();i++) {
                    CheckBox cb = (CheckBox) listView.getChildAt(i).findViewById(R.id.check_box);
                    if(cb.isChecked()){
                        newlistUOM.add(listUOM.get(i));
                    }
                }


                if(newlistUOM.size()>0) {
                    Intent newIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("StockDtl", newlistUOM);
                    newIntent.putExtras(bundle);
                    setResult(5, newIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Please add at least one item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onBackPressed();
            }
        });

    }

    private void getData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        Cursor data;
            data = db.getUOMbyItemCode(key);

        if(data.getCount()>0){
            while (data.moveToNext()) {
                AC_Class.StockTakeDetails itemUOM = new AC_Class.StockTakeDetails();
                itemUOM.setUOM(data.getString(data.getColumnIndex("UOM")));
                itemUOM.setItemCode(key);
                itemUOM.setQuantity(0.00);
                itemUOM.setStockDocNo(docNo);
                itemUOM.setItemDescription(data.getString(data.getColumnIndex("Description")));
                itemUOM.setCreatedTimeStamp(String.valueOf(dateFormat.format(date)));
                if(batchno!=null) {
                    itemUOM.setBatchNo(batchno);
                }
                listUOM.add(itemUOM);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent();
        setResult(99, newIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }
}