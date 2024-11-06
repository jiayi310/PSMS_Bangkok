package com.example.androidmobilestock_bangkok;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.androidmobilestock_bangkok.databinding.ActivityStockAssemblyMultipleTabBinding;

public class StockAssemblyMultipleTab extends AppCompatActivity {

    ActivityStockAssemblyMultipleTabBinding binding;
    AC_Class.StockAssembly stockTakeMenu = new AC_Class.StockAssembly();
    String TAG = "custDebug";
    ACDatabase db = new ACDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_assembly_multiple_tab);

        stockTakeMenu = getIntent().getParcelableExtra("STMenu");

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(stockTakeMenu.getDocNo() +" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.i(TAG, "Transfer Details: "+e.getMessage());}

        binding.setStockTakeMenu(stockTakeMenu);


        getData(stockTakeMenu.getDocNo());
    }

    private void getData(String docNo){
        ACDatabase db = new ACDatabase(StockAssemblyMultipleTab.this);

        if (docNo != null) {
            stockTakeMenu = db.getStockAssembly(docNo);
            if(stockTakeMenu.getFGBatchNo()==null || stockTakeMenu.getFGBatchNo().equals("")){
                binding.txtBatchno2.setVisibility(View.GONE);
            }else{
                binding.txtBatchno2.setVisibility(View.VISIBLE);
            }
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