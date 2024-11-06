package com.example.androidmobilestock_bangkok.Settings;

import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.ActivityLocalDataBinding;

import java.util.ArrayList;
import java.util.List;

public class TaxSettings extends AppCompatActivity {
    ActivityLocalDataBinding binding;
    ACDatabase db;
    SettingsListViewAdapter adapter;
    Boolean isTaxEnabled = true;
    String defaultTax = "";
    Boolean isTaxInclusive;

    //Initialize arrays
    String[] settingsTitles = new String[]{"Enable Tax", "Set Default Tax-Type",
            "Tax-Inclusive"};
    final int[] settingsIcons = new int[]{R.drawable.stock_inquiry, R.drawable.list,
            R.drawable.list};
    String[] settingsSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new ACDatabase(getBaseContext());
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_data);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Tax Settings");
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
                    // Toggle Tax
                    case 0:
                        isTaxEnabled = !isTaxEnabled;
                        db.updateREG("22", String.valueOf(isTaxEnabled));
                        getData();
                        break;

                    // Default Tax Type
                    case 1:
                        final List<String> taxTypes = new ArrayList<>();
                        taxTypes.add("None");
                        Cursor tt = db.getTaxType();
                        while (tt.moveToNext()) {
                            taxTypes.add(tt.getString(tt.getColumnIndex("TaxType")));
                        }
                        final CharSequence[] ttArray = taxTypes.toArray(new CharSequence[taxTypes.size()]);

                        AlertDialog.Builder ttOptions = new AlertDialog.Builder(TaxSettings.this);
                        ttOptions.setTitle("Select Default Tax Type:");
                        ttOptions.setItems(ttArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                defaultTax = taxTypes.get(which);
                                db.updateREG("21", defaultTax);
                                dialog.dismiss();
                                getData();
                            }
                        });
                        ttOptions.show();
                        break;

                    // Toggle Tax Inclusive
                    case 2:
                        isTaxInclusive = !isTaxInclusive;
                        db.updateREG("13", String.valueOf(isTaxInclusive));
                        getData();
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
        Cursor cursor1 = db.getReg("21");
        if(cursor1.moveToFirst()){
            defaultTax = cursor1.getString(0);
        }

        Cursor cursor3 = db.getReg("22");
        if(cursor3.moveToFirst()){
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor2 = db.getReg("13");
        if(cursor2.moveToFirst()){
            isTaxInclusive = Boolean.valueOf(cursor2.getString(0));
        }

        if (!isTaxEnabled == true) {
            settingsTitles = new String[]{"Enable Tax"};
            settingsSubtitles = new String[]{
                    String.valueOf(isTaxEnabled)
            };
        } else {
            settingsTitles = new String[]{"Enable Tax", "Set Default Tax-Type",
                    "Tax-Inclusive"};
            settingsSubtitles = new String[]{
                    String.valueOf(isTaxEnabled),
                    defaultTax,
                    String.valueOf(isTaxInclusive),
            };
        }
        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingsSubtitles);
        binding.listView.setAdapter(adapter);
    }

}
