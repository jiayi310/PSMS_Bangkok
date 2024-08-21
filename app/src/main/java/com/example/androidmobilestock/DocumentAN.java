package com.example.androidmobilestock;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidmobilestock.Settings.SalesAN;
import com.example.androidmobilestock.Settings.SettingsListViewAdapter;
import com.example.androidmobilestock.databinding.ActivityDocumentANBinding;

public class DocumentAN extends AppCompatActivity {
    ActivityDocumentANBinding binding;
    SettingsListViewAdapter adapter;

    //Initialize arrays
    final String[] settingsTitles = new String[]{"Stock Take","Sales", "Transfer", "Purchase", "Packing List", "Collection", "Stock Assembly", "Purchase Packing List", "Job Sheet", "Stock Receive"};
    final int[] settingsIcons = new int[]{R.drawable.stock_count3, R.drawable.sales3,R.drawable.transfer3, R.drawable.purchase3, R.drawable.packinglist3, R.drawable.arpayment3, R.drawable.stockassembly, R.drawable.purpackinglist, R.drawable.jobsheet_icon, R.drawable.stockreceive_icon};
    String[] settingSubtitles = new String[]{"", "", "", "", "", "","","","","","","","","","","","","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Document Auto Numbering");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_a_n);
        adapter = new SettingsListViewAdapter(DocumentAN.this, settingsTitles,
                settingsIcons, settingSubtitles);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        final Intent intent_st = new Intent(DocumentAN.this,
                                StockTakeAN.class);
                        startActivity(intent_st);
                        break;
                    case 1:
                        final Intent intent_s = new Intent(DocumentAN.this,
                                SalesAN.class);
                        startActivity(intent_s);
                        break;
                    case 2:
                        final Intent intent1 = new Intent(DocumentAN.this,
                                TransferAN.class);
                        startActivity(intent1);
                        break;
                    case 3:
                        final Intent intent2 = new Intent(DocumentAN.this,
                                PUR_AutoNumbering.class);
                        startActivity(intent2);
                        break;
                    case 4:
                        final Intent intent_pl = new Intent(DocumentAN.this,
                                PL_AutoNumbering.class);
                        startActivity(intent_pl);
                        break;

                    case 5:
                        final Intent intent_5 = new Intent(DocumentAN.this,
                                AR_AutoNumbering.class);
                        startActivity(intent_5);
                        break;
                    case 6:
                        final Intent intent_6 = new Intent(DocumentAN.this,
                                StockAssembly_AutoNumbering.class);
                        startActivity(intent_6);
                        break;
                    case 7:
                        final Intent intent_7 = new Intent(DocumentAN.this,
                                PPL_AutoNumbering.class);
                        startActivity(intent_7);
                        break;
                    case 8:
                        final Intent intent_8 = new Intent(DocumentAN.this,
                                Jobsheet_AutoNumbering.class);
                        startActivity(intent_8);
                        break;
                    case 9:
                        final Intent intent_9 = new Intent(DocumentAN.this,
                                StockReceive_AutoNumbering.class);
                        startActivity(intent_9);
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