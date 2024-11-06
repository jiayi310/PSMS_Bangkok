package com.example.androidmobilestock_bangkok.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.androidmobilestock_bangkok.fragment.KeyDwonFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-03-10.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<KeyDwonFragment> lstFrg = new ArrayList<KeyDwonFragment>();

    public ViewPagerAdapter(FragmentManager fm, List<KeyDwonFragment> fragments) {
        super(fm);

        lstFrg = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        if (lstFrg.size() > 0) {
            return lstFrg.get(position);
        }
        throw new IllegalStateException("No fragment at position " + position);
    }

    @Override
    public int getCount() {
        return lstFrg.size();
    }
}
