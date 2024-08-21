package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.androidmobilestock.databinding.RowScBinding;

import java.util.List;

public class SCAdapter extends BaseAdapter {

    Context context;
    private List<AC_Class.StockCount> sc_classlist;

    public SCAdapter(Context context, List<AC_Class.StockCount> sc_classlist) {
        this.context = context;
        this.sc_classlist = sc_classlist;
    }

    @Override
    public int getCount() {
        return this.sc_classlist.size();
    }

    @Override
    public Object getItem(int position) {
        return sc_classlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowScBinding binding;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_sc, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowScBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        //binding.setStDetail(sc_classlist.get(position));
        return binding.getRoot();
    }

}
