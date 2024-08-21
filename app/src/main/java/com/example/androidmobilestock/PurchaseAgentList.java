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

public class PurchaseAgentList extends AppCompatActivity {
    ListView agent_listView;
    ACDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchaseagentlist);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Purchase Agent");
        actionBar.setDisplayHomeAsUpEnabled(true);

        agent_listView = (ListView)findViewById(R.id.list_agent);
        db = new ACDatabase(this);
        getAgentData();
    }

    @Override
    public void onBackPressed() {
        Intent agent_intent = new Intent();
        setResult(10, agent_intent);
        finish();
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

    public void getAgentData() {
        Cursor data = db.getPurchaseAgent();
        if (data.getCount() > 0) {
            List<AC_Class.PurchaseAgent> agents = new ArrayList<>();
            agents.add(new AC_Class.PurchaseAgent("None", "null"));
            while (data.moveToNext()) {
                AC_Class.PurchaseAgent agent =
                        new AC_Class.PurchaseAgent(data.getString(data.getColumnIndex("PurchaseAgent")),
                                data.getString(data.getColumnIndex("Description")));
                agents.add(agent);
            }
            PurchaseAgentListViewAdapter arrayAdapter = new PurchaseAgentListViewAdapter(this, agents);
            agent_listView.setAdapter(arrayAdapter);
            agent_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.PurchaseAgent sa =(AC_Class.PurchaseAgent) parent.getItemAtPosition(position);
                    Intent agent_intent = new Intent();
                    agent_intent.putExtra("iPurchaseAgent", sa);
                    setResult(1, agent_intent);
                    finish();
                }
            });
        }
    }
}
