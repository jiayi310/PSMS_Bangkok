package com.example.androidmobilestock;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PL_SOList extends AppCompatActivity {

    RecyclerView so_listView;
    TextView modeView;
    EditText searchEditText;
    ACDatabase db;

    SOListViewAdapter arrayAdapter;
    List<AC_Class.SOMenu> soMenuList = new ArrayList<>();
    private SOListViewAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pl_activity_solist);
        so_listView = findViewById(R.id.list_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Downloaded Packing List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        db = new ACDatabase(this);

        try {
            setOnClickListener();
            so_listView.setHasFixedSize(true);
            so_listView.setLayoutManager(new LinearLayoutManager(this));
            arrayAdapter = new SOListViewAdapter(this, soMenuList,listener);
            so_listView.setAdapter(arrayAdapter);

            //item_listView.setTextFilterEnabled(true);
//            so_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    AC_Class.SOMenu i = (AC_Class.SOMenu) parent.getItemAtPosition(position);
//
//                    Intent intent = new Intent(PL_SOList.this, PL_SODtlList.class);
//                    intent.putExtra("mySO", i.DocNo);
//                    startActivityForResult(intent, 0);
//                }
//            });
        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }

        modeView = findViewById(R.id.modeView2);
        searchEditText = (EditText) findViewById(R.id.searchField);
        searchEditText.requestFocus();
        getSOData("");

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                getSOData(s.toString().trim());
            }
        });
    }

    public void setOnClickListener() {
        listener = new SOListViewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                AC_Class.SOMenu i = (AC_Class.SOMenu) soMenuList.get(position);

                Intent intent = new Intent(PL_SOList.this, PL_SODtlList.class);
                intent.putExtra("mySO", i.DocNo);
                startActivityForResult(intent, 0);
            }

            @Override
            public void onLongClick(View v, int position) {

            }

        };
    }

    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            Intent myIntent = new Intent();
            myIntent.putExtra("mySO", "");
            setResult(4, myIntent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Return from item list
        if (resultCode == 4) {
            String mySO = data.getStringExtra("mySO");
            String plType = data.getStringExtra("plType");
            if (!mySO.isEmpty()) {
                Intent item_intent = new Intent();
                item_intent.putExtra("mySO", mySO);
                item_intent.putExtra("plType", plType);
                setResult(4, item_intent);
                finish();
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void getSOData(String substring) {

        Cursor data = db.getSOLike(substring);
        if (data.getCount() > 0){
            soMenuList.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.SOMenu so = new AC_Class.SOMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), null, data.getString(4));
                    soMenuList.add(so);
                } catch (Exception e) { Log.i("custDebug", "error "+e.getMessage()); }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

}
