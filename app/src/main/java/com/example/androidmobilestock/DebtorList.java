package com.example.androidmobilestock;

import android.content.Intent;
import android.database.Cursor;
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

import com.example.androidmobilestock.adapter.DebtorListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class DebtorList extends AppCompatActivity {
    ListView debtor_listView;
    ACDatabase db;
    EditText searchEditText;
    DebtorListViewAdapter arrayAdapter;
    List<AC_Class.Debtor> debtors = new ArrayList<>();
    boolean isView;
    int FilterByAgent;
    String Agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debtor__list);

        Intent myIntent = getIntent();
        isView = myIntent.getBooleanExtra("isView", false);
        //Toast.makeText(this,  String.valueOf(isView), Toast.LENGTH_LONG).show();

        debtor_listView = (ListView) findViewById(R.id.list_debtor);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Customer Listing");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);

        Cursor fa = db.getReg("14");
        if (fa.moveToFirst()){
            FilterByAgent = fa.getInt(0);
        }

        Cursor agent = db.getReg("56");
        if (agent.moveToFirst()){
            Agent = agent.getString(0).replace("\"","'");
        }

        searchEditText = (EditText) findViewById(R.id.searchField);


        arrayAdapter = new DebtorListViewAdapter(this, 0, debtors);
        debtor_listView.setAdapter(arrayAdapter);
        debtor_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.Debtor d = (AC_Class.Debtor) parent.getItemAtPosition(position);
                if (isView) {
                    Intent intent_debtor = new Intent(DebtorList.this, DebtorDetails.class);
                    intent_debtor.putExtra("DebtorsKey", d);
                    startActivity(intent_debtor);
                }
                else {
                    Intent intent_debtor = new Intent();
                    intent_debtor.putExtra("DebtorsKey", d);
                    setResult(2, intent_debtor);
                    finish();
                }
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
        if (FilterByAgent == 1) {
            data = db.getDebtorLikeByAgent(substring, Agent);
        }
        else {
            //if (arrayAdapter!= null) {
            data = db.getDebtorLike(substring);
        }
        if (data.getCount() > 0){
            debtors.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Debtor myDebtor = new AC_Class.Debtor(
                            data.getString(data.getColumnIndex("AccNo")),
                            data.getString(data.getColumnIndex("CompanyName")),
                            data.getString(data.getColumnIndex("Desc2")),
                            data.getString(data.getColumnIndex("Address1")),
                            data.getString(data.getColumnIndex("Address2")),
                            data.getString(data.getColumnIndex("Address3")),
                            data.getString(data.getColumnIndex("Address4")),
                            data.getString(data.getColumnIndex("SalesAgent")),
                            data.getString(data.getColumnIndex("TaxType")),
                            data.getString(data.getColumnIndex("Phone")),
                            data.getString(data.getColumnIndex("Fax")),
                            data.getString(data.getColumnIndex("Attention")),
                            data.getString(data.getColumnIndex("EmailAddress")),
                            data.getString(data.getColumnIndex("DebtorType")),
                            data.getString(data.getColumnIndex("AreaCode")),
                            data.getString(data.getColumnIndex("CurrencyCode")),
                            data.getString(data.getColumnIndex("DisplayTerm")),
                            data.getString(data.getColumnIndex("Phone2")),
                            data.getString(data.getColumnIndex("DetailDiscount")));
                    debtors.add(myDebtor);
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

//    public void getDebtorData() {
//        Cursor data = db.getDebtor();
//        if (data.getCount() == 0) {
//            return;
//        }
//        else if (data.getCount() > 0) {
////            List<AC_Class.Debtor> debtors = new ArrayList<>();
//            while (data.moveToNext()) {
//                AC_Class.Debtor debtor = new AC_Class.Debtor(
//                        data.getString(data.getColumnIndex("AccNo")),
//                        data.getString(data.getColumnIndex("CompanyName")),
//                        data.getString(data.getColumnIndex("Desc2")),
//                        data.getString(data.getColumnIndex("Address1")),
//                        data.getString(data.getColumnIndex("Address2")),
//                        data.getString(data.getColumnIndex("Address3")),
//                        data.getString(data.getColumnIndex("Address4")),
//                        data.getString(data.getColumnIndex("SalesAgent")),
//                        data.getString(data.getColumnIndex("TaxType")),
//                        data.getString(data.getColumnIndex("Phone")),
//                        data.getString(data.getColumnIndex("Fax")),
//                        data.getString(data.getColumnIndex("Attention")));
//                debtors.add(debtor);
//            }
//            arrayAdapter = new DebtorListViewAdapter(this, 0, debtors);
//            debtor_listView.setAdapter(arrayAdapter);
//            debtor_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    AC_Class.Debtor d =(AC_Class.Debtor) parent.getItemAtPosition(position);
//                    Intent debtor_intent = new Intent();
//                    debtor_intent.putExtra("DebtorsKey", d);
//                    setResult(2, debtor_intent);
//                    finish();
//                }
//            });
//        }
//    }
}
