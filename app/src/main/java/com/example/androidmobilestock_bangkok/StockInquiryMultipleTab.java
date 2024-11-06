package com.example.androidmobilestock_bangkok;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.example.androidmobilestock_bangkok.databinding.ActivityStockInquiryMultipleTabBinding;
import com.example.androidmobilestock_bangkok.ui.main.SectionsPagerAdapter;

public class StockInquiryMultipleTab extends AppCompatActivity {
    AC_Class.Item item;
    AC_Class.ItemUOM itemUOM;
    ActivityStockInquiryMultipleTabBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_inquiry_multiple_tab);

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Stock Inquiry");
        } catch (Exception e) { Log.i("custDebug", "Stock Inquiry: "+e.getMessage()); }

        item = new AC_Class.Item();
        itemUOM = new AC_Class.ItemUOM();
        item = getIntent().getParcelableExtra("ItemDetailKey");
        Bundle bundle = new Bundle();
        bundle.putParcelable("ItemDetailKey", item);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), bundle);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}