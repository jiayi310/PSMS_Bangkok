package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.androidmobilestock.databinding.RowItemuomListBinding;
import java.util.List;

public class ItemUOMListAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.ItemUOM> itemUOM_List;

    public ItemUOMListAdapter(Context context, List<AC_Class.ItemUOM> itemUOM_List) {
        this.context = context;
        this.itemUOM_List = itemUOM_List;
    }

    @Override
    public int getCount() {
        return itemUOM_List.size();
    }

    @Override
    public Object getItem(int position) {
        return itemUOM_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowItemuomListBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_itemuom_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowItemuomListBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        binding.setItemuom(itemUOM_List.get(position));
        return binding.getRoot();
    }
}
