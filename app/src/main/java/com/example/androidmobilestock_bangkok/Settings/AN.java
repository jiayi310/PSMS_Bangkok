package com.example.androidmobilestock_bangkok.Settings;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.ActivityAnBinding;

public class AN extends AppCompatActivity {
    ActivityAnBinding binding;
    ACDatabase db;

    //Initialize arrays
    final String[] settingsTitles = new String[]{"Sales"};
    final int[] settingsIcons = new int[]{R.drawable.dollar_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new ACDatabase(getBaseContext());
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_an);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Auto-Numbering Settings");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }

        SettingsListViewAdapter adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, new String[]{});
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent;
                switch (position) {
                    // Set Default Currency
                    case 0:
                        Intent salesANIntent = new Intent(AN.this, SalesAN.class);
                        startActivity(salesANIntent);
                        break;

                    //Set Default Location
                    case 1:
                        Log.i("custDebug", ":D");
                        break;
                }
            }
        });
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
}
