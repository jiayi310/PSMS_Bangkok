package com.example.androidmobilestock;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ItemUOMList extends AppCompatActivity {
    ListView listView_UOM;
    ACDatabase db;
    String ItemCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_uomlist);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of UOM");
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView_UOM = (ListView)findViewById(R.id.list_UOM);
        db = new ACDatabase(this);
        Intent intent = getIntent();
        ItemCode = intent.getStringExtra("ItemKey");
        getItemUOM();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getItemUOM() {
        Cursor data = db.getAllItemUOM(ItemCode);
        if (data.getCount() == 0) {
            return;
        }
        else if (data.getCount() > 0) {
            List<AC_Class.ItemUOM> uoms = new ArrayList<>();
            while (data.moveToNext()) {
                AC_Class.ItemUOM itemUOM = new AC_Class.ItemUOM(data.getString(data.getColumnIndex("ItemCode")), data.getString(data.getColumnIndex("UOM")), data.getFloat(data.getColumnIndex("Rate")), data.getFloat(data.getColumnIndex("Price")), data.getFloat(data.getColumnIndex("Price2")), data.getFloat(data.getColumnIndex("Price3")), data.getFloat(data.getColumnIndex("Price4")), data.getFloat(data.getColumnIndex("Price5")), data.getFloat(data.getColumnIndex("Price6")), data.getString(data.getColumnIndex("Shelf")),  data.getString(data.getColumnIndex("BarCode")), data.getFloat(data.getColumnIndex("MinPrice")),data.getFloat(data.getColumnIndex("MaxPrice")));
                uoms.add(itemUOM);
            }
            ItemUOMListAdapter arrayAdapter = new ItemUOMListAdapter(this, uoms);
            listView_UOM.setAdapter(arrayAdapter);
            listView_UOM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.ItemUOM uom =(AC_Class.ItemUOM) parent.getItemAtPosition(position);
                    Intent UOM_intent = new Intent();
                    UOM_intent.putExtra("UOMKey", uom);
                    setResult(6, UOM_intent);
                    finish();
                }
            });
        }
    }
}
