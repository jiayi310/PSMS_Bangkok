package com.example.androidmobilestock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.Settings.SettingsListViewAdapter;
import com.example.androidmobilestock.databinding.ActivityDocumentANBinding;

public class ModuleSettings extends AppCompatActivity {
    ActivityDocumentANBinding binding;
    SettingsListViewAdapter adapter;

    //Initialize arrays
    String[] settingsTitles = new String[]{"Modify Upload Status - Stock Take","Modify Upload Status - Sales", "Modify Upload Status - Transfer", "Modify Upload Status - Purchase", "Modify Upload Status - PackingList", "Modify Upload Status - Collection", "Modify Upload Status - Stock Assembly", "Modify Upload Status - Purchase Packing List", "Modify Upload Status - Job Sheet", "Modify Upload Status - Stock Receive"};
    int[] settingsIcons = new int[]{R.drawable.stock_count3, R.drawable.sales3,R.drawable.transfer3, R.drawable.purchase3, R.drawable.packinglist3, R.drawable.arpayment3, R.drawable.stockassembly, R.drawable.purpackinglist, R.drawable.jobsheet_icon, R.drawable.stockreceive_icon};
    String[] settingSubtitles = new String[]{"", "", "", "", "", "","","","","","","","","","","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Modify Status");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_a_n);
        adapter = new SettingsListViewAdapter(ModuleSettings.this, settingsTitles,
                settingsIcons, settingSubtitles);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent Sus_intent_St = new Intent(ModuleSettings.this,
                                Setting_Status_StockTake.class);
                        startActivity(Sus_intent_St);
                        break;
                    case 1:
                        Intent Sus_intent = new Intent(ModuleSettings.this,
                                SalesUploadStatus.class);
                        startActivity(Sus_intent);
                        break;
                    case 2:
                        Intent Sus_tran_intent = new Intent(ModuleSettings.this,
                                TransferUploadStatus.class);
                        startActivity(Sus_tran_intent);
                        break;
                    case 3:
                        Intent purupload = new Intent(ModuleSettings.this, Setting_Status_Pur.class);
                        startActivity(purupload);
                        break;
                    case 4:
                        Intent plupload = new Intent(ModuleSettings.this, Setting_Status_PL.class);
                        startActivity(plupload);
                        break;

                    case 5:
                        Intent clupload = new Intent(ModuleSettings.this, Setting_Status_AR.class);
                        startActivity(clupload);
                        break;
                    case 6:
                        Intent saupload = new Intent(ModuleSettings.this, Setting_Status_SA.class);
                        startActivity(saupload);
                        break;

                    case 7:
                        Intent pplupload = new Intent(ModuleSettings.this, Setting_Status_PPL.class);
                        startActivity(pplupload);
                        break;

                    case 8:
                        Intent jsupload = new Intent(ModuleSettings.this, Setting_Status_JS.class);
                        startActivity(jsupload);
                        break;

                    case 9:
                        Intent srupload = new Intent(ModuleSettings.this, Setting_Status_SR.class);
                        startActivity(srupload);
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