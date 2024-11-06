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

import com.example.androidmobilestock_bangkok.adapter.AgentListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class Agent_List extends AppCompatActivity {
    ListView agent_listView;
    ACDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent__list);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Agents");
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
        Cursor data = db.getAgent();
        if (data.getCount() > 0) {
            List<AC_Class.SalesAgent> agents = new ArrayList<>();
            agents.add(new AC_Class.SalesAgent("None", "null"));

            try {
                int salesAgentIndex = data.getColumnIndex("SalesAgent");
                int descriptionIndex = data.getColumnIndex("Description");

                while (data.moveToNext()) {
                    AC_Class.SalesAgent agent =
                            new AC_Class.SalesAgent(data.getString(salesAgentIndex),
                                    data.getString(descriptionIndex));
                    agents.add(agent);
                }

            } catch (Exception e){
                Log.e("Error", e.getMessage());
            }

            AgentListViewAdapter arrayAdapter = new AgentListViewAdapter(this, agents);
            agent_listView.setAdapter(arrayAdapter);
            agent_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.SalesAgent sa =(AC_Class.SalesAgent) parent.getItemAtPosition(position);
                    Intent agent_intent = new Intent();
                    agent_intent.putExtra("AgentsKey", sa);
                    setResult(1, agent_intent);
                    finish();
                }
            });
        }
    }

}
