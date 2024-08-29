package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.androidmobilestock.databinding.ActivityCheckListBinding;

import java.util.ArrayList;
import java.util.List;


public class Check_List extends AppCompatActivity {

    ActivityCheckListBinding binding;
    ListView Check_ListView;
    ACDatabase db;
    String Item;
    String ItemUOM;
    EditText priceEdit;
    Button priceConfirm;

    AC_Class.Item myItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_check_list);


        Check_ListView = (ListView)findViewById(R.id.check_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Price");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        Intent pintent = getIntent();
        Item = pintent.getStringExtra("ItemCode");
        ItemUOM = pintent.getStringExtra("ItemUOMKey");

        getData();
        binding.setItem(myItem);
        priceEdit = findViewById(R.id.priceEdit);
        priceConfirm = findViewById(R.id.priceConfirm);


        priceConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(priceEdit.getText().toString())) {
                    priceEdit.requestFocus();
                    priceEdit.setError("This field cannot be blank!", null);
                    return;
                }

                Float price = Float.valueOf(priceEdit.getText().toString());

                AC_Class.SellingPrice sa =new AC_Class.SellingPrice("price",price);

                Intent myintent = new Intent();
                myintent.putExtra("price", sa);
                setResult(7, myintent);
                finish();
            }
        });


    }


    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            Intent newintent = new Intent();
            setResult(7, newintent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void getData() {

        Cursor data;
        data = db.getItem(Item,ItemUOM);

        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    myItem = new AC_Class.Item(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(6), data.getString(7), data.getString(8), data.getString(9), data.getFloat(10), data.getFloat(11), data.getFloat(12), data.getFloat(13), data.getFloat(14), data.getFloat(15), data.getString(16), data.getString(17), data.getFloat(18), data.getFloat(19),data.getFloat(20),data.getString(21));

                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }

        if (myItem != null)
        {
            List<AC_Class.SellingPrice> priceList = new ArrayList<>();

            priceList.add(new AC_Class.SellingPrice("Price 1", myItem.getPrice()));
            priceList.add(new AC_Class.SellingPrice("Price 2", myItem.getPrice2()));
            priceList.add(new AC_Class.SellingPrice("Price 3", myItem.getPrice3()));
            priceList.add(new AC_Class.SellingPrice("Price 4", myItem.getPrice4()));
            priceList.add(new AC_Class.SellingPrice("Price 5", myItem.getPrice5()));
            priceList.add(new AC_Class.SellingPrice("Price 6", myItem.getPrice6()));

            SellingPriceListViewAdapter arrayAdapter = new SellingPriceListViewAdapter(this, priceList);
            Check_ListView.setAdapter(arrayAdapter);
            Check_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.SellingPrice sa =(AC_Class.SellingPrice) parent.getItemAtPosition(position);
                    Intent myintent = new Intent();
                    myintent.putExtra("price", sa);
                    setResult(7, myintent);
                    finish();
                }
            });
        }
    }


}