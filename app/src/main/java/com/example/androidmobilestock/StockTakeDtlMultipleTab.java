package com.example.androidmobilestock;

import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.androidmobilestock.databinding.ActivitySotckTakeDtllMultipleTabBinding;

public class StockTakeDtlMultipleTab extends AppCompatActivity {

    ActivitySotckTakeDtllMultipleTabBinding binding;
    AC_Class.StockTakeMenu stockTakeMenu = new AC_Class.StockTakeMenu();
    AC_Class.StockTake st = new AC_Class.StockTake();
    String TAG = "custDebug";
    ACDatabase db = new ACDatabase(this);

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sotck_take_dtll_multiple_tab);

        stockTakeMenu = getIntent().getParcelableExtra("STMenu");

        Cursor data = db. getStockTakeItemTotalQty(stockTakeMenu.getDocNo());
        data.moveToFirst();
        stockTakeMenu.setTotalQty(data.getString(data.getColumnIndex("SUM(Qty)")));

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(stockTakeMenu.getDocNo() +" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.i(TAG, "Transfer Details: "+e.getMessage());}

        binding.setStockTakeMenu(stockTakeMenu);
        listview = (ListView)binding.lvDetail;

        getData(stockTakeMenu.getDocNo());
    }

    private void getData(String docNo){
        ACDatabase db = new ACDatabase(StockTakeDtlMultipleTab.this);

        if (docNo != null) {
            st = db.getStockTake(docNo);
            StockTakeDtlAdapter adapter = new StockTakeDtlAdapter(StockTakeDtlMultipleTab.this, st.getStockTakeDtlList());
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        if(!st.getRemarks().equals("") && st.getRemarks()!=null ){
            binding.tvRemark.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}