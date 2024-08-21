package com.example.androidmobilestock;

import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.androidmobilestock.databinding.PlActivityMultipleTabBinding;
import com.google.android.material.tabs.TabLayout;

public class PL_MultipleTab extends AppCompatActivity {

    PlActivityMultipleTabBinding binding;
    AC_Class.DO packingList = new AC_Class.DO();
    String TAG = "custDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pl_activity_multiple_tab);

        ACDatabase db = new ACDatabase(PL_MultipleTab.this);
        packingList = getIntent().getParcelableExtra("iPackingList");


        if (packingList != null)
            packingList = db.getPackingList(packingList.getDocNo());

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(packingList.getDocNo()+" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));
        } catch (Exception e) {
            Log.i(TAG, "Packing List Details: "+e.getMessage());}


        binding.setDO(packingList);

        Bundle bundle = new Bundle();
        bundle.putParcelable("iPackingList", packingList);

        PL_MultipleTabPagerAdapter sectionsPagerAdapter =
                new PL_MultipleTabPagerAdapter(this, getSupportFragmentManager(), bundle);
        ViewPager viewPager = findViewById(R.id.view_pager2);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs2);
        tabs.setupWithViewPager(viewPager);
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