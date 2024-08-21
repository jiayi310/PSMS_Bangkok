package com.example.androidmobilestock.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.databinding.ActivityLocalDataBinding;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.R;

import java.util.ArrayList;
import java.util.List;

public class BatchNoSettings extends AppCompatActivity {

    ActivityLocalDataBinding binding;
    ACDatabase db;
    SettingsListViewAdapter adapter;
    Boolean isBatchNoEnabled = true;
    String defaultBatchNo = "";
    Boolean isSalesBatchEnabled = true;
    Boolean isPurchaseBatchEnabled = true;
    String salesBatchOrderType = "";

    //Initialize arrays
    String[] settingsTitles = new String[]{"Enable Batch No", "Set Default BatchNo-Type","Enable Auto Purchase Batch","Enable Auto Sales Batch","Sales Order Type"};
    final int[] settingsIcons = new int[]{R.drawable.stock_inquiry, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list};
    String[] settingsSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_data);

        db = new ACDatabase(getBaseContext());

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Batch No Settings");
        } catch (Exception e)
        { Log.i("custDebug", e.getMessage()); }

        getData();
        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingsSubtitles);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    // Toggle Batch No
                    case 0:
                        isBatchNoEnabled = !isBatchNoEnabled;
                        db.updateREG("38", String.valueOf(isBatchNoEnabled));
                        getData();
                        break;

                    // Default Batch No Type
                    case 1:
                        Intent new_intent = new Intent(BatchNoSettings.this, BatchNoConfiguration.class);
                        startActivityForResult(new_intent, 1);
                        break;

                    case 2:
                        isPurchaseBatchEnabled = !isPurchaseBatchEnabled;
                        db.updateREG("40", String.valueOf(isPurchaseBatchEnabled));
                        getData();
                        break;

                    case 3:
                        isSalesBatchEnabled = !isSalesBatchEnabled;
                        db.updateREG("39", String.valueOf(isSalesBatchEnabled));
                        getData();
                        break;

                    case 4:
                        final List<String> orderType = new ArrayList<>();
                        orderType.add("Earliest Manufacture Date");
                        orderType.add("Latest Manufacture Date");
                        orderType.add("Earliest Expiration Date");
                        orderType.add("Latest Expiration Date");

                        final CharSequence[] ttArray = orderType.toArray(new CharSequence[orderType.size()]);

                        AlertDialog.Builder ttOptions = new AlertDialog.Builder(BatchNoSettings.this);
                        ttOptions.setTitle("Select Sales Order Type:");
                        ttOptions.setItems(ttArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                salesBatchOrderType = orderType.get(which);
                                db.updateREG("41", salesBatchOrderType);
                                dialog.dismiss();
                                getData();
                            }
                        });
                        ttOptions.show();
                        break;


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    getData();
                }
                break;

        }
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
        Cursor cursor1 = db.getReg("37");
        if(cursor1.moveToFirst()){
            defaultBatchNo = cursor1.getString(0);
        }

        Cursor cursor3 = db.getReg("38");
        if(cursor3.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor4 = db.getReg("39");
        if(cursor4.moveToFirst()){
            isSalesBatchEnabled = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor5 = db.getReg("40");
        if(cursor5.moveToFirst()){
            isPurchaseBatchEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor6 = db.getReg("41");
        if(cursor6.moveToFirst()){
            salesBatchOrderType = cursor6.getString(0);
        }

        if (!isBatchNoEnabled == true) {
            settingsTitles = new String[]{"Enable Batch No"};
            settingsSubtitles = new String[]{
                    String.valueOf(isBatchNoEnabled)
            };
            db.updateREG("44", "false");

        } else {
            settingsTitles = new String[]{"Enable Batch No", "Set Batch No Type","Enable Auto Purchase Batch","Enable Auto Sales Batch"};
            settingsSubtitles = new String[]{
                    String.valueOf(isBatchNoEnabled),
                    defaultBatchNo, String.valueOf(isPurchaseBatchEnabled), String.valueOf(isSalesBatchEnabled)
            };
            if (isSalesBatchEnabled == true)  {
                settingsTitles = new String[]{"Enable Batch No", "Set Batch No Type","Enable Auto Purchase Batch","Enable Auto Sales Batch","Sales Order Type"};
                settingsSubtitles = new String[]{
                        String.valueOf(isBatchNoEnabled),
                        defaultBatchNo, String.valueOf(isPurchaseBatchEnabled), String.valueOf(isSalesBatchEnabled), salesBatchOrderType
                };
            }
        }


        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingsSubtitles);
        binding.listView.setAdapter(adapter);
    }

}