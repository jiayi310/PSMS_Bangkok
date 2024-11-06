package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.RowItemListBinding;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private List<AC_Class.Item> mData;
    private List<AC_Class.Item> Displayitemlist;


    ListAdapter(Context context, List<AC_Class.Item> mData){
        this.context= context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return Displayitemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return Displayitemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        RowItemListBinding binding;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_item_list,
                    null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);

        } else {
            binding = (RowItemListBinding) convertView.getTag();
        }

        try {
            binding.setItem(mData.get(position));


            if (position % 2 == 1) {
                convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
            }

        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
        return binding.getRoot();
    }
}
