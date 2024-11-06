package com.example.androidmobilestock_bangkok;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock_bangkok.databinding.PlRowPlListBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PL_PLListViewAdapter extends BaseAdapter {
    Context context;
    ACDatabase db;
    private List<AC_Class.DO> DOList;
    private Set<AC_Class.DO> selectedItems;


    public PL_PLListViewAdapter(Context context, List<AC_Class.DO> doList) {
        this.context = context;
        this.DOList = doList;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return DOList.size();
    }

    @Override
    public Object getItem(int position) {
        return DOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlRowPlListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pl_row_pl_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (PlRowPlListBinding) convertView.getTag();
        }
        if(DOList.get(position).getRemarks()==null){
            binding.tvRemarks.setVisibility(View.GONE);
            binding.tvRemarksLbl.setVisibility(View.GONE);
        }else{
            binding.tvRemarks.setVisibility(View.VISIBLE);
            binding.tvRemarksLbl.setVisibility(View.VISIBLE);
        }
        binding.setDO(DOList.get(position));

        // Set background color based on selection state
        AC_Class.DO currentItem = DOList.get(position);
        if (selectedItems.contains(currentItem)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return binding.getRoot();
    }

    // Method to select an item
    public void selectItem(AC_Class.DO item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.DO item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.DO> getSelectedItems() {
        return selectedItems;
    }
}