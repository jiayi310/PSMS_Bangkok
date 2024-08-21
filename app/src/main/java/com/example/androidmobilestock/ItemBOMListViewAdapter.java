package com.example.androidmobilestock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.databinding.RowBomListBinding;

import java.util.List;

public class ItemBOMListViewAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.ItemBOM> itemBOMList;

    public ItemBOMListViewAdapter(Context context, List<AC_Class.ItemBOM> bom_list) {
        this.context = context;
        this.itemBOMList = bom_list;
    }

    @Override
    public int getCount() {
        return itemBOMList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemBOMList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowBomListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_bom_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowBomListBinding) convertView.getTag();
        }
        binding.setAgent(itemBOMList.get(position));
        return binding.getRoot();
    }
}
