package com.example.androidmobilestock_bangkok;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TaxType_List extends AppCompatActivity {
    ListView taxtype_listview;
    ACDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Tax Types");
        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }

        setContentView(R.layout.activity_tax_type__list);
        taxtype_listview = (ListView)findViewById(R.id.list_tax_type);
        db = new ACDatabase(this);
        getTaxTypeData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
        return super.onOptionsItemSelected(item);
    }

    public void getTaxTypeData() {
        Cursor data = db.getTaxType();
        if (data.getCount() == 0) {
            return;
        }
        else if (data.getCount() > 0) {
            List<AC_Class.TaxType> taxtype_list = new ArrayList<>();
            while (data.moveToNext()) {
                AC_Class.TaxType taxtype =
                        new AC_Class.TaxType(data.getString(0), data.getString(1), data.getFloat(2));
                taxtype_list.add(taxtype);
            }
            TaxtypeListViewAdapter arrayAdapter = new TaxtypeListViewAdapter(this, taxtype_list);
            taxtype_listview.setAdapter(arrayAdapter);
            taxtype_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.TaxType tax =(AC_Class.TaxType) parent.getItemAtPosition(position);
                    Intent tax_intent = new Intent();
                    tax_intent.putExtra("TaxTypesKey", tax);
                    setResult(5, tax_intent);
                    finish();
                }
            });
        }
    }
}
