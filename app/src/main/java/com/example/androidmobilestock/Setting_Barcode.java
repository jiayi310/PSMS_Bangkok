package com.example.androidmobilestock;

import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.example.androidmobilestock.Settings.SettingsListViewAdapter;
import com.example.androidmobilestock.databinding.ActivityLocalDataBinding;

public class Setting_Barcode extends AppCompatActivity {
    ActivityLocalDataBinding binding;
    ACDatabase db;
    SettingsListViewAdapter adapter;

    //Initialize arrays
    String[] settingsTitles = new String[]{"Enable Custom Barcode", "Barcode Total Length", "ItemCode - Start Digit", "ItemCode - Length", "Qty - Start Digit", "Qty - Length", "Qty - Decimal",  "DtlRemark - Start Digit", "DtlRemark - Length",  "DtlRemark2 - Start Digit", "DtlRemark2 - Length"};
    final int[] settingsIcons = new int[]{R.drawable.stock_inquiry, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list, R.drawable.list};
    String[] settingsSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new ACDatabase(getBaseContext());
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_data);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Barcode Settings");
        } catch (Exception e)
        { Log.i("custDebug", e.getMessage()); }

        getData();
        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingsSubtitles);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Barcode.this);
                final EditText input = new EditText(Setting_Barcode.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                //input.setPadding(50 ,0, 50 , 0);

                switch (position) {
                    // Toggle Tax
                    case 0:
                        boolean isEnabled = false;

                        Cursor data = db.getReg("25");
                        if(data.moveToFirst()){
                            isEnabled = Boolean.valueOf(data.getString(0));
                        }

                        db.updateREG("25", String.valueOf(!isEnabled));
                        getData();
                        break;

                    case 1:
                        builder.setTitle("Barcode Total Length");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("26", String.valueOf(input.getText().toString()));
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

                    case 2:
                        builder.setTitle("ItemCode - Start Digit");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("27", String.valueOf(input.getText().toString()));
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

                    case 3:
                        builder.setTitle("ItemCode - Length");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("28", String.valueOf(input.getText().toString()));
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

                    case 4:
                        builder.setTitle("Qty - Start Digit");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("29", String.valueOf(input.getText().toString()));
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

                    case 5:
                        builder.setTitle("Qty - Length");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("30", String.valueOf(input.getText().toString()));
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

                    case 6:
                        builder.setTitle("Qty - Decimal");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("31", String.valueOf(input.getText().toString()));
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

                    case 7:
                        builder.setTitle("DtlRemark - Start Digit");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("51", String.valueOf(input.getText().toString()));
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

                    case 8:
                        builder.setTitle("DtlRemark - Length");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("52", String.valueOf(input.getText().toString()));
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

                    case 9:
                        builder.setTitle("DtlRemark2 - Start Digit");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("53", String.valueOf(input.getText().toString()));
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

                    case 10:
                        builder.setTitle("DtlRemark2 - Length");
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("54", String.valueOf(input.getText().toString()));
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

        Cursor data = db.getReg("25");
        if(data.moveToFirst()){
            isEnabled = Boolean.valueOf(data.getString(0));
        }

        if (!isEnabled) {
            settingsTitles = new String[]{"Enable Custom Barcode"};
            settingsSubtitles = new String[]{String.valueOf(isEnabled)};
        } else {
            settingsTitles = new String[]{"Enable Custom Barcode", "Barcode Total Length", "ItemCode - Start Digit", "ItemCode - Length", "Qty - Start Digit", "Qty - Length", "Qty - Decimal",  "DtlRemark - Start Digit", "DtlRemark - Length",  "DtlRemark2 - Start Digit", "DtlRemark2 - Length"};

            String barcodeLength = "";
            String itemStart = "";
            String itemLength = "";
            String qtyStart = "";
            String qtyLength = "";
            String qtyDecimal = "";
            String dtlRemarkStart = "";
            String dtlRemarkLength = "";
            String dtlRemark2Start = "";
            String dtlRemark2Length = "";

            Cursor data2 = db.getReg("26");
            if(data2.moveToFirst()){
                barcodeLength = data2.getString(0);
            }

            Cursor data3 = db.getReg("27");
            if(data3.moveToFirst()){
                itemStart = data3.getString(0);
            }

            Cursor data4 = db.getReg("28");
            if(data4.moveToFirst()){
                itemLength = data4.getString(0);
            }

            Cursor data5 = db.getReg("29");
            if(data5.moveToFirst()){
                qtyStart = data5.getString(0);
            }

            Cursor data6 = db.getReg("30");
            if(data6.moveToFirst()){
                qtyLength = data6.getString(0);
            }

            Cursor data7 = db.getReg("31");
            if(data7.moveToFirst()){
                qtyDecimal = data7.getString(0);
            }

            Cursor data8 = db.getReg("51");
            if(data8.moveToFirst()){
                dtlRemarkStart = data8.getString(0);
            }

            Cursor data9 = db.getReg("52");
            if(data9.moveToFirst()){
                dtlRemarkLength = data9.getString(0);
            }

            Cursor data10 = db.getReg("53");
            if(data10.moveToFirst()){
                dtlRemark2Start = data10.getString(0);
            }

            Cursor data11 = db.getReg("54");
            if(data11.moveToFirst()){
                dtlRemark2Length = data11.getString(0);
            }


            settingsSubtitles = new String[]{String.valueOf(isEnabled), barcodeLength, itemStart, itemLength, qtyStart, qtyLength, qtyDecimal, dtlRemarkStart, dtlRemarkLength, dtlRemark2Start, dtlRemark2Length};
        }
        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingsSubtitles);
        binding.listView.setAdapter(adapter);
    }




}
