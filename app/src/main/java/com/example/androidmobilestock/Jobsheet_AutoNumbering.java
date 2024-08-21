package com.example.androidmobilestock;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidmobilestock.Settings.SettingsListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class Jobsheet_AutoNumbering extends AppCompatActivity {

    ListView listView;
    ACDatabase db;
    AlertDialog.Builder builder;
    String AnPrefix = "";
    String AnLength = "";
    String AnNextNumber = "";
    View viewInflated;
    EditText input;
    final String[] settingsTitles = new String[]{"Change Prefix", "Change Length", "Change Next Number"};
    final int[] settingsIcons = new int[]{R.drawable.manage_stock, R.drawable.manage_stock, R.drawable.manage_stock};
    String[] settingSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet_auto_numbering);

        db = new ACDatabase(this);

        listView = findViewById(R.id.listView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Auto Numbering - Job Sheet");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        getData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent;
                switch (position) {
                    // Set Prefix
                    case 0:
                        builder = new AlertDialog.Builder(Jobsheet_AutoNumbering.this);
                        builder.setTitle("New prefix");
                        viewInflated = LayoutInflater.from(Jobsheet_AutoNumbering.this)
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
                                    if (!db.setJSANPrefix(temp)) {
                                        Toast.makeText(Jobsheet_AutoNumbering.this, "Failed", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        getData();
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
                        builder = new AlertDialog.Builder(Jobsheet_AutoNumbering.this);
                        builder.setTitle("New length");
                        viewInflated = LayoutInflater.from(Jobsheet_AutoNumbering.this)
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
                                    if (!db.setJSANLength(temp)) {
                                        Toast.makeText(Jobsheet_AutoNumbering.this, "Failed", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        getData();
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
                        builder = new AlertDialog.Builder(Jobsheet_AutoNumbering.this);
                        builder.setTitle("New Next Number");
                        viewInflated = LayoutInflater.from(Jobsheet_AutoNumbering.this)
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
                                    if (!db.setJSANNextNumber(temp)) {
                                        Toast.makeText(Jobsheet_AutoNumbering.this, "Failed", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        getData();
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

    public void getData() {
        List<String> data = new ArrayList<>();
        Cursor an = db.getRef("JS");

        if (an.getCount() > 0){
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
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.d("1122333", "jobsheet cannot get from auto numbering");
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
}