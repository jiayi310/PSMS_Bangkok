package com.example.androidmobilestock.Settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.DocumentAN;
import com.example.androidmobilestock.GeneralSettings;
import com.example.androidmobilestock.ModuleSettings;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.TransactionSettings;
import com.example.androidmobilestock.databinding.ActivitySettingsHomeBinding;

public class SettingsHome extends AppCompatActivity {

    ACDatabase db;
    TextView appversion;
    ActivitySettingsHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_home);

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle("Settings");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.i("custDebug", "Analytics: "+e.getMessage());}

        appversion = findViewById(R.id.appversion);
        db = new ACDatabase(this);

        Cursor ver = db.getReg("1");
        if (ver.moveToFirst()) {
            appversion.setText("App Ver "+ver.getString(ver.getColumnIndex("Value")));
        }

        binding.general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent San_intent = new Intent(SettingsHome.this, GeneralSettings.class);
                startActivity(San_intent);
            }
        });

        binding.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent San_intent = new Intent(SettingsHome.this, ModuleSettings.class);
                startActivity(San_intent);
            }
        });

        binding.numbering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent San_intent = new Intent(SettingsHome.this, DocumentAN.class);
                startActivity(San_intent);
            }
        });

        binding.transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent San_intent = new Intent(SettingsHome.this, TransactionSettings.class);
                startActivity(San_intent);
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