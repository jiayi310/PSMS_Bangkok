package com.example.androidmobilestock;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.databinding.RowSoListBinding;

import java.util.List;


public class PPL_POListViewAdapter extends BaseAdapter {

    Context context;
    private List<AC_Class.SOMenu> so_list;

    public PPL_POListViewAdapter(Context context, List<AC_Class.SOMenu> so_listFP) {
        this.context = context;
        this.so_list = so_listFP;
    }

    @Override
    public int getCount() {
        return so_list.size();
    }

    @Override
    public Object getItem(int position) {
        return so_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RowSoListBinding binding;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_so_list,
                    null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowSoListBinding) convertView.getTag();
        }

        try {
            binding.setSo(so_list.get(position));

            if (position % 2 == 1) {
                convertView.setBackgroundColor(Color.parseColor("#df9f5f"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#f7c37A"));
            }

            if(so_list.get(position).getRemark()==null || so_list.get(position).getRemark().equals("") || so_list.get(position).getRemark().equals("null")){
                binding.tvRemark.setVisibility(View.GONE);
            }else {
                binding.tvRemark.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
        return binding.getRoot();
    }


}

