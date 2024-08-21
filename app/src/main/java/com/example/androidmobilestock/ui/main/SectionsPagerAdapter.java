package com.example.androidmobilestock.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.fragment.PlaceholderFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.StockDetails, R.string.StockBalanceByLocation, R.string.CustomerHistoryPrice};
    private final Context mContext;
    private Bundle myBundle;
    int SellingPrice = 0;
    ACDatabase db;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Bundle myBundleFP) {
        super(fm);
        mContext = context;
        myBundle = myBundleFP;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position)
        {
            case 0: {
                return StockDetailsFragment.newInstance(myBundle);
            }
            case 1:{
                return StockBalanceByLocationFragment.newInstance(myBundle);
            }
            case 2:
            {
                return CustomerHistoryPriceFragment.newInstance(myBundle);
            }
        }
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        db = new ACDatabase(mContext);
        Cursor sale = db.getReg("48");
        if (sale.moveToFirst()) {
            SellingPrice = sale.getInt(0);
        }
        if(SellingPrice == 0){
            return 2;
        }else {
            return 3;
        }
    }
}