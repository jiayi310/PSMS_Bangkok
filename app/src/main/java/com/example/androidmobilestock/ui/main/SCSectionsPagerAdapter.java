package com.example.androidmobilestock.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.androidmobilestock.fragment.PlaceholderFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SCSectionsPagerAdapter extends FragmentPagerAdapter {
    private static final String[] TAB_TITLES = new String[]{"Manual", "Barcode"};
    private final Context mContext;
    private Bundle myBundle;

    public SCSectionsPagerAdapter(Context context, FragmentManager fm, Bundle myBundleFP) {
        super(fm);
        mContext = context;
        myBundle = new Bundle();
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        myBundle = new Bundle();
        switch (position)
        {
            case 0: {
                myBundle.putInt("mode", 0);
                return SCManualFragment.newInstance(myBundle);
            }
            case 1: {
                myBundle.putInt("mode", 1);
                return SCBarcodeFragment.newInstance(myBundle);
//                return SCBarcodeFragment.newInstance(myBundle);
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