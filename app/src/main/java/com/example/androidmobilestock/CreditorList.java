package com.example.androidmobilestock;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CreditorList extends AppCompatActivity {
    ListView lv;
    ACDatabase db;
    EditText searchEditText;
    CreditorListViewAdapter arrayAdapter;
    List<AC_Class.Creditor> creditorList = new ArrayList<>();
    String Agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditorlist);

        lv = (ListView) findViewById(R.id.lv_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Creditor Listing");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        db = new ACDatabase(this);

        Cursor agent = db.getReg("24");
        if (agent.moveToFirst()){
            Agent = String.valueOf(agent.getInt(0));
        }

        searchEditText = (EditText) findViewById(R.id.searchField);


        arrayAdapter = new CreditorListViewAdapter(this, 0, creditorList);

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.Creditor d = (AC_Class.Creditor) parent.getItemAtPosition(position);

                Intent intent_debtor = new Intent();
                intent_debtor.putExtra("iCreditor", d);
                setResult(2, intent_debtor);
                finish();

            }
        });

        getData("");

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override
            public void afterTextChanged(Editable s) {
                getData(s.toString().trim());
            }
        });
    }

    public void getData(String substring) {
        Cursor data;

        data = db.getCreditorLike(substring);

        if (data.getCount() > 0){
            creditorList.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Creditor myCreditor = new AC_Class.Creditor(
                            data.getString(data.getColumnIndex("AccNo")),
                            data.getString(data.getColumnIndex("CompanyName")),
                            data.getString(data.getColumnIndex("Desc2")),
                            data.getString(data.getColumnIndex("Address1")),
                            data.getString(data.getColumnIndex("Address2")),
                            data.getString(data.getColumnIndex("Address3")),
                            data.getString(data.getColumnIndex("Address4")),
                            data.getString(data.getColumnIndex("PurchaseAgent")),
                            data.getString(data.getColumnIndex("TaxType")),
                            data.getString(data.getColumnIndex("Phone")),
                            data.getString(data.getColumnIndex("Fax")),
                            data.getString(data.getColumnIndex("Attention")),
                            data.getString(data.getColumnIndex("EmailAddress")),
                            data.getString(data.getColumnIndex("CreditorType")),
                            data.getString(data.getColumnIndex("AreaCode")),
                            data.getString(data.getColumnIndex("CurrencyCode")),
                            data.getString(data.getColumnIndex("DisplayTerm")),
                            data.getString(data.getColumnIndex("Phone2")));
                    creditorList.add(myCreditor);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
            arrayAdapter.notifyDataSetChanged();
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
        return false;
    }
}
