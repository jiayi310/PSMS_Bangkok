package com.example.androidmobilestock_bangkok;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.ActivityStockAssemblyDetailBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.util.ArrayList;
import java.util.List;

public class StockAssemblyDtlList extends AppCompatActivity {

    ActivityStockAssemblyDetailBinding binding;
    private AC_Class.StockAssembly st, st_temp;
    AC_Class.StockAssemblyDetails tempDtl;
    AC_Class.StockAssemblyDetails stDtl;
    List<AC_Class.StockAssemblyDetails> checkDtl = new ArrayList<>();
    private AC_Class.StockAssemblyDetails std;
    ArrayList<String> myModules = new ArrayList<String>();
    int Purchase = 0;
    private IntentIntegrator qrScan;
    StockAssemblyDtlAdapter adapter;
    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;
    private Barcode2DWithSoft barcode2D;

    ACDatabase db;

    String typeFP;

    boolean isMerged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(StockAssemblyDtlList.this, R.layout.activity_stock_assembly_detail);


        st = getIntent().getParcelableExtra("SAHeader");

        db = new ACDatabase(this);
        checkDtl = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("SubItem of " +st.getFGItemCode());
        actionBar.setDisplayHomeAsUpEnabled(true);
        getStockTakeDetailList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return false;
    }
    public void getStockTakeDetailList() {

        Cursor data = db.getItemBOM(st.getFGItemCode());
        if(data.getCount() > 0){
            while (data.moveToNext()) {
                double qty = st.getFGQty() * data.getDouble(data.getColumnIndex("Qty"));
                AC_Class.StockAssemblyDetails stockAssemblyDetails = new AC_Class.StockAssemblyDetails(data.getString(data.getColumnIndex("SubItemCode")), qty );
                checkDtl.add(stockAssemblyDetails);
            }
            st.setStockAssemblyDtlList(checkDtl);
        }
        adapter = new StockAssemblyDtlAdapter(this, st.getStockAssemblyDtlList());
        binding.stdListview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}