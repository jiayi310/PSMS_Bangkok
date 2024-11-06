package com.example.androidmobilestock_bangkok;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.example.androidmobilestock_bangkok.Settings.SettingsListViewAdapter;
import com.example.androidmobilestock_bangkok.databinding.ActivityLocalDataBinding;

public class Setting_Hybrid extends AppCompatActivity {

    ActivityLocalDataBinding binding;
    ACDatabase db;
    SettingsListViewAdapter adapter;

    //Initialize arrays
    String[] settingsTitles = new String[]{"Enable Hybrid Mode", "History Day"};
    final int[] settingsIcons = new int[]{R.drawable.stock_inquiry, R.drawable.list};
    String[] settingsSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new ACDatabase(getBaseContext());
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_data);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Hybrid Mode Settings");
        } catch (Exception e)
        { Log.i("custDebug", e.getMessage()); }

        getData();
        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingsSubtitles);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Hybrid.this);
                final EditText input = new EditText(Setting_Hybrid.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                //input.setPadding(50 ,0, 50 , 0);

                switch (position) {
                    // Toggle Tax
                    case 0:
                        boolean isEnabled = false;

                        Cursor data = db.getReg("32");
                        if(data.moveToFirst()){
                            isEnabled = Boolean.valueOf(data.getString(0));
                        }

                        db.updateREG("32", String.valueOf(!isEnabled));
                        getData();
                        break;

                    case 1:
                        builder.setTitle("History Day");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("33", String.valueOf(input.getText().toString()));
                                getData();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
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

    void getData() {
        boolean isEnabled = false;

        Cursor data = db.getReg("32");
        if(data.moveToFirst()){
            isEnabled = Boolean.valueOf(data.getString(0));
        }

        if (!isEnabled) {
            settingsTitles = new String[]{"Enable Hybrid Mode"};
            settingsSubtitles = new String[]{String.valueOf(isEnabled)};
        } else {
            settingsTitles = new String[]{"Enable Hybrid Mode", "History Day"};

            String historyDay = "";

            Cursor data2 = db.getReg("33");
            if(data2.moveToFirst()){
                historyDay = data2.getString(0);
            }

            settingsSubtitles = new String[]{String.valueOf(isEnabled), historyDay};
        }
        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingsSubtitles);
        binding.listView.setAdapter(adapter);
    }
}