package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.databinding.RowLocationListBinding;

import java.util.List;

public class LocationListViewAdapter extends BaseAdapter {

    Context context;
    List<AC_Class.Location> location_list;

    public LocationListViewAdapter(Context context, List<AC_Class.Location> location_list) {
        this.context = context;
        this.location_list = location_list;
    }

    @Override
    public int getCount() {
        return location_list.size();
    }

    @Override
    public Object getItem(int position) {
        return location_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowLocationListBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_location_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowLocationListBinding)convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        binding.setLocation(location_list.get(position));
        return binding.getRoot();
    }
}
