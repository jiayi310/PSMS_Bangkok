package com.example.androidmobilestock;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PPL_Item_List extends AppCompatActivity {

    ListView item_listView;
    TextView modeView;
    EditText searchEditText;
    ACDatabase db;
    Intent pIntent;

    PLItemListViewAdapter arrayAdapter;
    List<AC_Class.Item> s_inquiry = new ArrayList<>();
    String substring = "";
    String docNo;
    Boolean isAutoPrice = false;
    String key = "emptyStr";
    //int searchMode = 0; //  0 for code&name; 1 for code; 2 for name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pIntent = getIntent();
        substring = pIntent.getStringExtra("substring");
        docNo = pIntent.getStringExtra("DocNo");
        key = pIntent.getStringExtra("key");
        setContentView(R.layout.activity_plitem__list);
        item_listView = (ListView) findViewById(R.id.list_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Items");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFbe5504));

        db = new ACDatabase(this);

        try {

            arrayAdapter = new PLItemListViewAdapter(this, s_inquiry);
            item_listView.setAdapter(arrayAdapter);
            //item_listView.setTextFilterEnabled(true);
            item_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.Item i = (AC_Class.Item) parent.getItemAtPosition(position);
                    Intent item_intent = new Intent();
                    item_intent.putExtra("ItemsKey", i);
                    item_intent.putExtra("ItemsQty", i.getQty());
                    setResult(4, item_intent);
                    finish();
                }
            });
        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }

        modeView = findViewById(R.id.modeView2);
        searchEditText = (EditText) findViewById(R.id.searchField);
        searchEditText.requestFocus();
        getItemData2(substring);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                getItemData2(s.toString().trim());
            }
        });
    }

    private void getItemData2(String substring) {
        Cursor data = db.getPOItem(docNo);
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1),
                            data.getString(2), data.getString(3), data.getString(4), data.getString(5),
                            data.getString(6), data.getString(7), data.getString(8), data.getDouble(9),
                            data.getString(10));

                    s_inquiry.add(item);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            pIntent.putExtra("ItemsKey", "");
            setResult(99, pIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
