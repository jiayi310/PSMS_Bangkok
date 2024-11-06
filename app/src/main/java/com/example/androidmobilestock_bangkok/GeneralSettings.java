package com.example.androidmobilestock_bangkok;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.Settings.SettingsListViewAdapter;
import com.example.androidmobilestock_bangkok.databinding.ActivityLocalDataBinding;

import java.util.ArrayList;
import java.util.List;

public class GeneralSettings extends AppCompatActivity {
    ActivityLocalDataBinding binding;
    SettingsListViewAdapter adapter;
    String defaultCurr = "";
    String defaultCurr2 = "";
    String Debtor = "";
    String Agent = "";
    String Default_loc = "";
    String PrinterName = "";
    String DefaultSalesFormat = "";
    String DefaultCreditor = "";
    String DefaultPurchaseAgent = "";
    String TerminalDevice = "";
    ACDatabase db;
    AlertDialog.Builder builder;

    //Initialize arrays
    final String[] settingsTitles = new String[]{"Logo","Company Header", "Default Currency","Default Debtor", "Default Creditor", "Default Agent","Default Purchase Agent","Default Location","Default Printer","Default Sales Format","Sales Receipt Format","Collection Receipt Format","Terminal Device", "Add Printer", "Reset Stock Count", "Additional Features"};
    final int[] settingsIcons = new int[]{R.mipmap.stock_mobile, R.drawable.companyheader,R.drawable.currency, R.drawable.debtor, R.drawable.creditor, R.drawable.agent, R.drawable.purchaseagent, R.drawable.slocation, R.drawable.sprinter, R.drawable.salesreceipt,R.drawable.collectionreceipt,R.drawable.salesformat, R.drawable.sprinter, R.drawable.sprinter, R.drawable.reset, R.drawable.additional};
    String[] settingSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new ACDatabase(this);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("General");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }

        Cursor cursor = db.getReg("6");
        if(cursor.moveToFirst()){
            defaultCurr2 = cursor.getString(0);
        }

        Cursor cursor1 = db.getReg("17");
        if(cursor1.moveToFirst()){
            Debtor = cursor1.getString(0);
        }

        Cursor cursor2 = db.getReg("18");
        if(cursor2.moveToFirst()){
            Agent = cursor2.getString(0);
        }

        Cursor cursor3 = db.getReg("7");
        if(cursor3.moveToFirst()){
            Default_loc = cursor3.getString(0);
        }

        Cursor cursor4 = db.getReg("15");
        if(cursor4.moveToFirst()){
            PrinterName = cursor4.getString(0);
        }

        Cursor cursor5 = db.getReg("23");
        if(cursor5.moveToFirst()){
            DefaultCreditor = cursor5.getString(0);
        }

        Cursor cursor6 = db.getReg("24");
        if(cursor6.moveToFirst()){
            DefaultPurchaseAgent = cursor6.getString(0);
        }

        Cursor cursor7 = db.getReg("34");
        if(cursor7.moveToFirst()){
            DefaultSalesFormat = cursor7.getString(0);
        }

        Cursor cursor8 = db.getReg("55");
        if(cursor8.moveToFirst()){
            TerminalDevice = cursor8.getString(0);
        }

        settingSubtitles = new String[]{"", "", defaultCurr2, Debtor, DefaultCreditor, Agent, DefaultPurchaseAgent, Default_loc, PrinterName, DefaultSalesFormat,"" ,"",TerminalDevice,"", ""};
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_data);
        adapter = new SettingsListViewAdapter(GeneralSettings.this, settingsTitles,
                settingsIcons, settingSubtitles);
        binding.listView.setAdapter(adapter);


        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(GeneralSettings.this, Setting_Logo.class);
                        startActivity(intent);
                        break;
                    case 1:
                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Company Header:");
                        final EditText input = new EditText(GeneralSettings.this);
                        input.layout(20,20,20,20);

                        Cursor ch = db.getReg("16");
                        if(ch.moveToFirst()){
                            input.setText(ch.getString(ch.getColumnIndex("Value")));
                        }
                        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input.setSingleLine(false);
                        input.setLines(7);
                        input.setMaxLines(10);
                        input.setGravity(Gravity.START);

                        FrameLayout container = new FrameLayout(GeneralSettings.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = 80;
                        params.rightMargin = 80;
                        input.setLayoutParams(params);
                        container.addView(input);

                        builder.setView(container);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.updateREG("16", input.getText().toString());
                            }
                        });

                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    case 2:
                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("New default currency");
                        View viewInflated = LayoutInflater.from(GeneralSettings.this)
                                .inflate(R.layout.default_currency_input, (ViewGroup) view, false);
                        final EditText input2 = (EditText) viewInflated.findViewById(R.id.input);
                        input2.setHint("Current: "+ defaultCurr);
                        Cursor dc1 = db.getReg("6");
                        if (dc1.moveToFirst()) {
                            defaultCurr = dc1.getString(0);
                        }
                        builder.setView(viewInflated);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                defaultCurr = input2.getText().toString();
                                if (!defaultCurr.equals("")) {
                                    db.updateREG("6", defaultCurr);
                                }
                                Log.i("custDebug", defaultCurr);
                                getData();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Log.i("custDebug", defaultCurr);
                            }
                        });
                        builder.show();
                        break;
                    case 3:
                        final List<String> debtors = new ArrayList<>();
                        debtors.add("None");
                        Cursor dbtors = db.getDebtor();
                        while (dbtors.moveToNext()) {
                            debtors.add(dbtors.getString(dbtors.getColumnIndex("AccNo")));
                        }
                        final CharSequence[] cs = debtors.toArray(new CharSequence[debtors.size()]);

                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Select Default Debtor:");
                        builder.setItems(cs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("17", debtors.get(which));
                                dialog.dismiss();
                                getData();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case 4:
                        final List<String> creditor = new ArrayList<>();
                        creditor.add("None");
                        Cursor data = db.getCreditor();
                        while (data.moveToNext()) {
                            creditor.add(data.getString(data.getColumnIndex("AccNo")));
                        }
                        final CharSequence[] cs_creditor = creditor.toArray(new CharSequence[creditor.size()]);

                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Select Default Creditor:");
                        builder.setItems(cs_creditor, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("23", creditor.get(which));
                                dialog.dismiss();
                                getData();
                            }
                        });
                        AlertDialog dialog_creditor = builder.create();
                        dialog_creditor.show();
                        break;

                    case 5:
                        final List<String> agents = new ArrayList<>();
                        agents.add("None");
                        Cursor agnts = db.getAgent();
                        while (agnts.moveToNext()) {
                            agents.add(agnts.getString(agnts.getColumnIndex("SalesAgent")));
                        }
                        final CharSequence[] cs2 = agents.toArray(new CharSequence[agents.size()]);

                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Select Default Agent:");
                        builder.setItems(cs2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("18", agents.get(which));
                                dialog.dismiss();
                                getData();
                            }
                        });
                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                        break;
                    case 6:
                        final List<String> purchaseagents = new ArrayList<>();
                        purchaseagents.add("None");
                        Cursor data_purchaseagent = db.getPurchaseAgent();
                        while (data_purchaseagent.moveToNext()) {
                            purchaseagents.add(data_purchaseagent.getString(data_purchaseagent.getColumnIndex("PurchaseAgent")));
                        }
                        final CharSequence[] cs_purchaseagent = purchaseagents.toArray(new CharSequence[purchaseagents.size()]);

                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Select Default Purchase Agent:");
                        builder.setItems(cs_purchaseagent, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("24", purchaseagents.get(which));
                                dialog.dismiss();
                                getData();
                            }
                        });
                        AlertDialog dialog_purchaseagent = builder.create();
                        dialog_purchaseagent.show();
                        break;

                    case 7:
                        final List<String> locations = new ArrayList<>();
                        locations.add("None");
                        Cursor locs = db.getLocation();
                        while (locs.moveToNext()) {
                            locations.add(locs.getString(locs.getColumnIndex("Location")));
                        }
                        final CharSequence[] cs3 = locations.toArray(new CharSequence[locations.size()]);

                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Select Default Location:");
                        builder.setItems(cs3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("7", locations.get(which));
                                dialog.dismiss();
                                getData();
                            }
                        });
                        AlertDialog dialog3 = builder.create();
                        dialog3.show();
                        break;

                    case 8:
                        final List<String> printer = new ArrayList<>();
                        printer.add("None");
                        Cursor prin = db.getPrinter();
                        while (prin.moveToNext()) {
                            printer.add(prin.getString(prin.getColumnIndex("PrinterName")));
                        }
                        final CharSequence[] cs4 = printer.toArray(new CharSequence[printer.size()]);

                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Select Default Printer:");
                        builder.setItems(cs4, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("15", printer.get(which));
                                dialog.dismiss();
                                getData();
                            }
                        });
                        AlertDialog dialog4 = builder.create();
                        dialog4.show();
                        break;

                    case 9:
                        final List<String> salesFormat = new ArrayList<>();
                        Cursor cur_salesFormat = db.getSalesFormat();
                        while (cur_salesFormat.moveToNext()) {
                            salesFormat.add(cur_salesFormat.getString(cur_salesFormat.getColumnIndex("ReportName")));
                        }
                        final CharSequence[] cs5 = salesFormat.toArray(new CharSequence[salesFormat.size()]);

                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Select Default Sales Format:");
                        builder.setItems(cs5, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateREG("34", salesFormat.get(which));
                                dialog.dismiss();
                                getData();
                            }
                        });
                        AlertDialog dialog5 = builder.create();
                        dialog5.show();
                        break;

                    case 10:
                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Sales Receipt Format:");
                        final EditText input3 = new EditText(GeneralSettings.this);
                        input3.layout(20,20,20,20);

                        Cursor ch2 = db.getReg("64");
                        if(ch2.moveToFirst()){
                            input3.setText(ch2.getString(ch2.getColumnIndex("Value")));
                        }
                        input3.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input3.setSingleLine(false);
                        input3.setLines(7);
                        input3.setMaxLines(10);
                        input3.setGravity(Gravity.START);

                        FrameLayout container2 = new FrameLayout(GeneralSettings.this);
                        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.leftMargin = 80;
                        params2.rightMargin = 80;
                        input3.setLayoutParams(params2);
                        container2.addView(input3);

                        builder.setView(container2);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.updateREG("64", input3.getText().toString());
                            }
                        });

                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert = builder.create();
                        alert.show();
                        break;
                    case 11:
                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Collection Receipt Format:");
                        final EditText input4 = new EditText(GeneralSettings.this);
                        input4.layout(20,20,20,20);

                        Cursor ch3 = db.getReg("63");
                        if(ch3.moveToFirst()){
                            input4.setText(ch3.getString(ch3.getColumnIndex("Value")));
                        }
                        input4.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input4.setSingleLine(false);
                        input4.setLines(7);
                        input4.setMaxLines(10);
                        input4.setGravity(Gravity.START);

                        FrameLayout container3 = new FrameLayout(GeneralSettings.this);
                        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params3.leftMargin = 80;
                        params3.rightMargin = 80;
                        input4.setLayoutParams(params3);
                        container3.addView(input4);

                        builder.setView(container3);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.updateREG("63", input4.getText().toString());
                            }
                        });

                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert = builder.create();
                        alert.show();
                        break;

                    case 12:
                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Terminal Device:");
                        final EditText input5 = new EditText(GeneralSettings.this);
                        input5.layout(20,20,20,20);

                        Cursor ch5 = db.getReg("55");
                        if(ch5.moveToFirst()){
                            input5.setText(ch5.getString(ch5.getColumnIndex("Value")));
                        }
                        input5.setSingleLine(false);
                        input5.setLines(1);
                        input5.setMaxLines(2);
                        input5.setGravity(Gravity.START);

                        FrameLayout container4 = new FrameLayout(GeneralSettings.this);
                        FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params4.leftMargin = 80;
                        params4.rightMargin = 80;
                        input5.setLayoutParams(params4);
                        container4.addView(input5);

                        builder.setView(container4);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.updateREG("55", input5.getText().toString());
                                getData();
                            }
                        });

                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert4 = builder.create();
                        alert4.show();
                        break;

                    case 13:
                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setTitle("Add Printer Name:");
                        final EditText input6 = new EditText(GeneralSettings.this);
                        input6.layout(20,20,20,20);


                        input6.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input6.setSingleLine(false);
                        input6.setLines(7);
                        input6.setMaxLines(10);
                        input6.setGravity(Gravity.START);

                        FrameLayout container6 = new FrameLayout(GeneralSettings.this);
                        FrameLayout.LayoutParams params6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params6.leftMargin = 80;
                        params6.rightMargin = 80;
                        input6.setLayoutParams(params6);
                        container6.addView(input6);

                        builder.setView(container6);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                              //  add printer
                                db.AddPrinter(input6.getText().toString());
                            }
                        });

                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert = builder.create();
                        alert.show();
                        break;

                    case 14:
                        Intent intent2 = new Intent(GeneralSettings.this, Setting_AdditionalFeatures.class);
                        startActivity(intent2);
                        break;

                    // Reset Stock Count
                    case 15:
                        builder = new AlertDialog.Builder(GeneralSettings.this);
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setMessage("Delete all stock count?");

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (db.resetSC()) {
                                        Toast.makeText(getBaseContext(), "Reset Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "Reset Failed", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
                            }
                        });
                        dialog = builder.create();
                        dialog.show();
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

        Cursor cursor = db.getReg("6");
        if(cursor.moveToFirst()){
            defaultCurr2 = cursor.getString(0);
        }

        Cursor cursor1 = db.getReg("17");
        if(cursor1.moveToFirst()){
            Debtor = cursor1.getString(0);
        }

        Cursor cursor2 = db.getReg("18");
        if(cursor2.moveToFirst()){
            Agent = cursor2.getString(0);
        }

        Cursor cursor3 = db.getReg("7");
        if(cursor3.moveToFirst()){
            Default_loc = cursor3.getString(0);
        }

        Cursor cursor4 = db.getReg("15");
        if(cursor4.moveToFirst()){
            PrinterName = cursor4.getString(0);
        }

        Cursor cursor5 = db.getReg("23");
        if(cursor5.moveToFirst()){
            DefaultCreditor = cursor5.getString(0);
        }

        Cursor cursor6 = db.getReg("24");
        if(cursor6.moveToFirst()){
            DefaultPurchaseAgent = cursor6.getString(0);
        }

        Cursor cursor7 = db.getReg("34");
        if(cursor7.moveToFirst()){
            DefaultSalesFormat = cursor7.getString(0);
        }

        Cursor cursor8 = db.getReg("55");
        if(cursor8.moveToFirst()){
            TerminalDevice = cursor8.getString(0);
        }

        settingSubtitles = new String[]{
                "", "", defaultCurr2, Debtor, DefaultCreditor, Agent, DefaultPurchaseAgent, Default_loc, PrinterName, DefaultSalesFormat,"" ,"",TerminalDevice,"", ""};


        adapter = new SettingsListViewAdapter(this, settingsTitles,
                settingsIcons, settingSubtitles);
        binding.listView.setAdapter(adapter);


    }
}