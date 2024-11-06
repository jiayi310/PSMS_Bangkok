package com.example.androidmobilestock_bangkok.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.RowUrlListBinding;

import java.util.List;

public class ConnectionListAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.Connection> connectionList;

    public ConnectionListAdapter(Context context, List<AC_Class.Connection> connectionList) {
        this.context = context;
        this.connectionList = connectionList;
    }

    @Override
    public int getCount() {
        return connectionList.size();
    }

    @Override
    public Object getItem(int position) {
        return connectionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowUrlListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_url_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowUrlListBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        binding.setConnection(connectionList.get(position));
        return binding.getRoot();
    }
}
