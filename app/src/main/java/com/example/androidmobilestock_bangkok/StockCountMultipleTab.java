package com.example.androidmobilestock_bangkok;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import android.net.Uri;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.example.androidmobilestock_bangkok.databinding.ActivityStockCountMultipleTabBinding;
import com.example.androidmobilestock_bangkok.ui.main.SCBarcodeFragment;
import com.example.androidmobilestock_bangkok.ui.main.SCManualFragment;
import com.example.androidmobilestock_bangkok.ui.main.SCSectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StockCountMultipleTab extends AppCompatActivity implements SCManualFragment.OnFragmentInteractionListener, SCBarcodeFragment.OnFragmentInteractionListener,SCManualFragment.OnDataPass{
    ActivityStockCountMultipleTabBinding binding;
    SCSectionsPagerAdapter scSectionsPagerAdapter;
    String TAG = "custDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_count_multiple_tab);

        ActionBar actionBar = getSupportActionBar();
        Log.i(TAG, "Got into SC Mult Tab");
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Stock Count");
        } catch (Exception e) { Log.i("custDebug", "Stock Count: "+e.getMessage()); }

        Bundle bundle = new Bundle();

        scSectionsPagerAdapter = new SCSectionsPagerAdapter(this,
                getSupportFragmentManager(), bundle);
        ViewPager viewPager = findViewById(R.id.view_pager3);
        viewPager.setAdapter(scSectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs3);
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                EditText et = null;
                switch (position) {
                    case 0: {
                        et = findViewById(R.id.itemEditText);
                        break;
                    }
                    case 1: {
                        et = findViewById(R.id.itemEditTextB);
                        break;
                    }
                }

                if (et != null) {
                    et.clearFocus();
                    et.setText("");
                    et.requestFocus();
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        // Scanner not working atm
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.i("custDebug", "Received Intent");
//                if (intent != null) {
//                    String barcode = intent.getStringExtra("SCAN_BARCODE1");
//                    int barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1);
//                    if(barcode!=null) {
//                        Log.i("custDebug", barcode+", "+barcodeType);
//                    }else{
//                        Log.i("custDebug", "No Barcode");
//                    }
//                } else {
//                    Log.i("custDebug", "Scan Failed");
//                }
//            }
//        }, new IntentFilter("nlscan.action.SCANNER_RESULT"));
    }

    @Override
    public void onBackPressed() {
        String id = getIntent().getStringExtra("ID");
        String agent = getIntent().getStringExtra("SalesAgent");
        String remarks = getIntent().getStringExtra("Remarks");
        String mode = getIntent().getStringExtra("ModeChosen");

        Intent back = new Intent(StockCountMultipleTab.this, StockTake.class);
        back.putExtra("ID",id);
        back.putExtra("ModeChosen",mode);
        back.putExtra("SalesAgent",agent);
        back.putExtra("Remarks",remarks);

        Log.wtf("Agent: ",agent);
        Log.wtf("Remarks: ",remarks);

        startActivity(back);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F6){
            Log.i("custDebug", "F6 Down");
            Intent intent = new Intent("nlscan.action.SCANNER_TRIG");
            intent.putExtra("SCAN_TIMEOUT", 4);
            intent.putExtra("SCAN_TYPE ", 1);
            sendBroadcast(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.sc_menu, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        /*if (id == R.id.viewlist)
        {
            Intent intent = new Intent(StockCountMultipleTab.this, StockCountList.class);
            startActivity(intent);
            finish();
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

    @Override
    public void onDataPass(ArrayList<String> ic, ArrayList<String> dsc, ArrayList<String> loc, ArrayList<String> uom, ArrayList<String> qty) {
        for(int i=0 ; i<ic.size() ; i++)
        {
            Log.wtf("IC",ic.get(i));
            Log.wtf("DSC",dsc.get(i));
            Log.wtf("LOC",loc.get(i));
            Log.wtf("UOM",uom.get(i));
            Log.wtf("QTY",qty.get(i));
        }
    }
}
