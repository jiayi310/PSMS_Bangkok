package com.example.androidmobilestock_bangkok;

import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.Settings.SettingsListViewAdapter;
import com.example.androidmobilestock_bangkok.databinding.PlActivityAutonumberingBinding;

import java.util.ArrayList;
import java.util.List;

public class PL_AutoNumbering extends AppCompatActivity {
    PlActivityAutonumberingBinding binding;
    AlertDialog.Builder builder;
    ACDatabase db;

    String AnPrefix = "";
    String AnLength = "";
    String AnNextNumber = "";
    View viewInflated;
    EditText input;

    //Initialize arrays
    final String[] settingsTitles = new String[]{"Change Prefix", "Change Length", "Change Next Number"};
    final int[] settingsIcons = new int[]{R.drawable.manage_stock, R.drawable.manage_stock, R.drawable.manage_stock};
    String[] settingSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new ACDatabase(getBaseContext());
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pl_activity_autonumbering);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Auto Numbering - Packing List");
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }

        //Get data
        getANData();

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent;
                switch (position) {
                    // Set Prefix
                    case 0:
                        builder = new AlertDialog.Builder(PL_AutoNumbering.this);
                        builder.setTitle("New prefix");
                        viewInflated = LayoutInflater.from(PL_AutoNumbering.this)
                                .inflate(R.layout.default_currency_input, (ViewGroup) view, false);
                        input = (EditText) viewInflated.findViewById(R.id.input);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        input.setHint("Current: " + AnPrefix);
                        builder.setView(viewInflated);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String temp = input.getText().toString();
                                if (!temp.equals("")) {
                                    if (!db.setPLANPrefix(temp)) {
                                        Toast.makeText(PL_AutoNumbering.this, "Failed", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        getANData();
                                    }
                                }
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

                    //Set Length
                    case 1:
                        builder = new AlertDialog.Builder(PL_AutoNumbering.this);
                        builder.setTitle("New length");
                        viewInflated = LayoutInflater.from(PL_AutoNumbering.this)
                                .inflate(R.layout.default_currency_input, (ViewGroup) view, false);
                        input = (EditText) viewInflated.findViewById(R.id.input);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setHint("Current: " + AnLength);
                        builder.setView(viewInflated);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String temp = input.getText().toString();
                                if (!temp.equals("")) {
                                    if (!db.setPLANLength(temp)) {
                                        Toast.makeText(PL_AutoNumbering.this, "Failed", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        getANData();
                                    }
                                }
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

                    //Set Next Number
                    case 2:
                        builder = new AlertDialog.Builder(PL_AutoNumbering.this);
                        builder.setTitle("New Next Number");
                        viewInflated = LayoutInflater.from(PL_AutoNumbering.this)
                                .inflate(R.layout.default_currency_input, (ViewGroup) view, false);
                        input = (EditText) viewInflated.findViewById(R.id.input);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setHint("Current: " + AnNextNumber);
                        builder.setView(viewInflated);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String temp = input.getText().toString();
                                if (!temp.equals("")) {
                                    if (!db.setPLANNextNumber(temp)) {
                                        Toast.makeText(PL_AutoNumbering.this, "Failed", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        getANData();
                                    }
                                }
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

    public void getANData() {
        List<String> data = new ArrayList<>();
        Cursor an = db.getRef("PL");

        while (an.moveToNext()){
            AnPrefix = an.getString(an.getColumnIndex("Prefix"));
            data.add(AnPrefix);
            AnLength = an.getString(an.getColumnIndex("Length"));
            data.add(AnLength);
            AnNextNumber = an.getString(an.getColumnIndex("NextNumber"));
            data.add(AnNextNumber);
        }

        settingSubtitles = data.toArray(new String[data.size()]);

        SettingsListViewAdapter adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingSubtitles);
        binding.listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
