package com.example.androidmobilestock_bangkok.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.androidmobilestock_bangkok.fragment.InvoiceItemsFragment;
import com.example.androidmobilestock_bangkok.fragment.InvoicePaymentsFragment;
import com.example.androidmobilestock_bangkok.fragment.PlaceholderFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class InvoiceSectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = new String[]{"Item Details", "Payment Details"};
    private final Context mContext;
    private Bundle myBundle;

    public InvoiceSectionsPagerAdapter(Context context, FragmentManager fm, Bundle myBundleFP) {
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
                return InvoiceItemsFragment.newInstance(myBundle);
//                return StockDetailsFragment.newInstance(myBundle);
            }
            case 1:{
                return InvoicePaymentsFragment.newInstance(myBundle);
//                return StockDetailsFragment.newInstance(myBundle);
            }
        }
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}