package com.example.androidmobilestock_bangkok;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.Settings.BatchNoSettings;
import com.example.androidmobilestock_bangkok.Settings.TaxSettings;
import com.example.androidmobilestock_bangkok.databinding.ActivityDocumentANBinding;

import java.util.ArrayList;

public class TransactionSettings extends AppCompatActivity {
    ActivityDocumentANBinding binding;
    TransactionListViewAdapter adapter;
    ACDatabase db;
    Boolean NegativeInventory = true;
    Boolean MinimumSellingPrice = true;
    boolean isEnabled = false;

    //Initialize arrays
    String[] settingsTitles = new String[]{"Tax Settings","Barcode", "Permission", "Auto Price", "Hybrid Mode (beta)", "Batch No Settings", "Allow Negative Inventory", "Min Selling Price Control","Enable PL Only Tally Uploaded", "Enable PR Only Tally Uploaded","Enable Batch Comparison","Enable Location Comparison","Allow Edit PO", "Default 5 Cents Rounding"};
    int[] settingsIcons = new int[]{R.drawable.taxsettings, R.drawable.barcode,R.drawable.rfid, R.drawable.autoprice, R.drawable.process, R.drawable.batch, R.drawable.neginventory, R.drawable.neginventory , R.drawable.tallyupload, R.drawable.prupload, R.drawable.batchcompare, R.drawable.locationcompare, R.drawable.alloweditpo, R.drawable.fivecent};
    String[] settingSubtitles = new String[]{"", "", "", "", "", "", "", "", "", "","","", "", ""};
    ArrayList<String> myModules = new ArrayList<String>();
    Boolean OnlyTallyUploaded = true;
    Boolean OnlyTallyUploadedPR = true;
    Boolean BatchComparison = true;
    Boolean LocationComparison = true;
    Boolean CollectionDetails = true;
    Boolean AllowEditPO = true;
    Boolean DefaultSales5CentRounding = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Transaction");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }
        db = new ACDatabase(this);
        getModules();
        resetIconsList();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_a_n);

        getData();
        getData2();
        getData3();
        getData4();
        getData6();
        getData7();
        getData8();
        adapter = new TransactionListViewAdapter(TransactionSettings.this, settingsTitles,
                settingsIcons, settingSubtitles);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                switch (settingsTitles[position]) {
                    case "Tax Settings":
                        Intent ts_intent = new Intent(TransactionSettings.this, TaxSettings.class);
                        startActivity(ts_intent);
                        break;
                    case "Barcode":
                        Intent iBarcode = new Intent(TransactionSettings.this, Setting_Barcode.class);
                        startActivity(iBarcode);
                        break;
                    case "Permission":
                        Intent rfids_intent = new Intent(TransactionSettings.this, RFIDSettings.class);
                        startActivity(rfids_intent);
                        break;
                    case "Auto Price":
//                        Intent iAutoPrice = new Intent(TransactionSettings.this, Setting_AutoPrice.class);
//                        startActivity(iAutoPrice);
                        boolean isEnabled = false;

                        Cursor data = db.getReg("20");
                        if(data.moveToFirst()){
                            isEnabled = Boolean.valueOf(data.getString(0));
                        }

                        db.updateREG("20", String.valueOf(!isEnabled));
                        getData2();
                        break;
                    case "Hybrid Mode (beta)":
                        Intent iHybrid = new Intent(TransactionSettings.this, Setting_Hybrid.class);
                        startActivity(iHybrid);
                        break;

                    case "Batch No Settings":
                        Intent batch_no = new Intent(TransactionSettings.this, BatchNoSettings.class);
                        startActivity(batch_no);
                        break;
                    case "Allow Negative Inventory":
                        NegativeInventory = !NegativeInventory;
                        db.updateREG("42", String.valueOf(NegativeInventory));
                        getData();
                        break;

                    case "Min Selling Price Control":
                        MinimumSellingPrice = !MinimumSellingPrice;
                        db.updateREG("73", String.valueOf(MinimumSellingPrice));
                        getData();
                        break;
                    case "Enable PL Only Tally Uploaded":
                        OnlyTallyUploaded = !OnlyTallyUploaded;
                        db.updateREG("43", String.valueOf(OnlyTallyUploaded));
                        getData3();
                        break;

                    case "Enable PR Only Tally Uploaded":
                        OnlyTallyUploadedPR = !OnlyTallyUploadedPR;
                        db.updateREG("66", String.valueOf(OnlyTallyUploadedPR));
                        getData7();
                        break;

                    case "Enable Batch Comparison":
                        BatchComparison = !BatchComparison;
                        db.updateREG("44", String.valueOf(BatchComparison));
                        getData3();
                        break;

                    case "Enable Location Comparison":
                        LocationComparison = !LocationComparison;
                        db.updateREG("49", String.valueOf(LocationComparison));
                        getData4();
                        break;
                    case "Allow Edit PO":
                        AllowEditPO = !AllowEditPO;
                        db.updateREG("65", String.valueOf(AllowEditPO));
                        getData6();
                        break;
                    case "Default 5 Cents Rounding":
                        DefaultSales5CentRounding = !DefaultSales5CentRounding;
                        db.updateREG("74", String.valueOf(DefaultSales5CentRounding));
                        getData8();
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

    private void getModules() {
        Cursor data = db.getModules();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                myModules.add(data.getString(data.getColumnIndex("Name")));
            }
        }
        data.close();
    }

    public void resetIconsList() {
        if (myModules.contains("BATCH")) {
            settingsTitles = new String[]{"Tax Settings","Barcode", "Permission", "Auto Price", "Hybrid Mode (beta)", "Batch No Settings", "Allow Negative Inventory", "Min Selling Price Control","Enable PL Only Tally Uploaded", "Enable PR Only Tally Uploaded","Enable Batch Comparison","Enable Location Comparison","Allow Edit PO", "Default 5 Cents Rounding"};
            settingsIcons = new int[]{R.drawable.taxsettings, R.drawable.barcode,R.drawable.rfid, R.drawable.autoprice, R.drawable.process, R.drawable.batch, R.drawable.neginventory, R.drawable.neginventory , R.drawable.tallyupload, R.drawable.prupload, R.drawable.batchcompare, R.drawable.locationcompare,R.drawable.alloweditpo, R.drawable.fivecent};
            settingSubtitles = new String[]{"", "", "", "", "", "", "", "", "", "",""};
        } else {
            settingsTitles = new String[]{"Tax Settings","Barcode", "Permission", "Auto Price", "Hybrid Mode (beta)", "Allow Negative Inventory", "Min Selling Price Control","Enable PL Only Tally Uploaded", "Enable PR Only Tally Uploaded", "Enable Batch Comparison","Enable Location Comparison","Allow Edit PO", "Default 5 Cents Rounding"};
            settingsIcons = new int[]{R.drawable.taxsettings, R.drawable.barcode,R.drawable.rfid, R.drawable.autoprice, R.drawable.process, R.drawable.neginventory, R.drawable.neginventory ,R.drawable.tallyupload, R.drawable.batchcompare, R.drawable.prupload, R.drawable.locationcompare,  R.drawable.alloweditpo, R.drawable.fivecent};
            settingSubtitles = new String[]{"", "", "", "", "", "", "", "", "",""};
            db.updateREG("38", "false");
        }
    }

    void getData() {
        Cursor cursor = db.getReg("42");
        if(cursor.moveToFirst()){
            NegativeInventory = Boolean.valueOf(cursor.getString(0));
        }

        Cursor cursor2 = db.getReg("73");
        if(cursor2.moveToFirst()){
            MinimumSellingPrice = Boolean.valueOf(cursor2.getString(0));
        }

        refresh();
    }

    void getData2() {


        Cursor data = db.getReg("20");
        if(data.moveToFirst()){
            isEnabled = Boolean.valueOf(data.getString(0));
        }

        refresh();
    }

    void getData3() {
        Cursor cursor = db.getReg("43");
        if(cursor.moveToFirst()){
            OnlyTallyUploaded = Boolean.valueOf(cursor.getString(0));
        }

        Cursor cursor2 = db.getReg("44");
        if(cursor2.moveToFirst()){
            BatchComparison = Boolean.valueOf(cursor2.getString(0));
        }

        refresh();
    }


    void getData4() {

        Cursor cursor2 = db.getReg("49");
        if(cursor2.moveToFirst()){
            LocationComparison = Boolean.valueOf(cursor2.getString(0));
        }

        refresh();

    }

    void getData6() {

        Cursor cursor2 = db.getReg("65");
        if(cursor2.moveToFirst()){
            AllowEditPO = Boolean.valueOf(cursor2.getString(0));
        }

        refresh();

    }

    void getData7() {

        Cursor cursor2 = db.getReg("66");
        if(cursor2.moveToFirst()){
            OnlyTallyUploadedPR = Boolean.valueOf(cursor2.getString(0));
        }

        refresh();

    }

    void getData8() {

        Cursor cursor2 = db.getReg("74");
        if(cursor2.moveToFirst()){
            DefaultSales5CentRounding = Boolean.valueOf(cursor2.getString(0));
        }

        refresh();

    }

    void refresh(){
        if (myModules.contains("BATCH")) {
            settingSubtitles = new String[]{"", "", "", String.valueOf(isEnabled), "", "", String.valueOf(NegativeInventory), String.valueOf(MinimumSellingPrice), String.valueOf(OnlyTallyUploaded), String.valueOf(OnlyTallyUploadedPR),String.valueOf(BatchComparison), String.valueOf(LocationComparison), String.valueOf(AllowEditPO), String.valueOf(DefaultSales5CentRounding)};
        }else{
            settingSubtitles = new String[]{"", "", "", String.valueOf(isEnabled), "", String.valueOf(NegativeInventory), String.valueOf(MinimumSellingPrice), String.valueOf(OnlyTallyUploaded), String.valueOf(OnlyTallyUploadedPR),String.valueOf(BatchComparison), String.valueOf(LocationComparison), String.valueOf(AllowEditPO), String.valueOf(DefaultSales5CentRounding)};
        }

        adapter = new TransactionListViewAdapter(this, settingsTitles,
                settingsIcons, settingSubtitles);
        binding.listView.setAdapter(adapter);
    }


}