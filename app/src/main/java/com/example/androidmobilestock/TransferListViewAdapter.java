package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.androidmobilestock.databinding.RowTransferListBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransferListViewAdapter extends BaseAdapter {
    Context context;
    private List<AC_Class.TransferMenu> transfer_menu_list;
    private Set<AC_Class.TransferMenu> selectedItems;

    public TransferListViewAdapter(Context context, List<AC_Class.TransferMenu> transfer_menu_list) {
        this.context = context;
        this.transfer_menu_list = transfer_menu_list;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return transfer_menu_list.size();
    }

    @Override
    public Object getItem(int position) {
        return transfer_menu_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowTransferListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_transfer_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowTransferListBinding) convertView.getTag();
        }

        if(transfer_menu_list.get(position).getReason()==null){
            binding.tvReason.setVisibility(View.GONE);
        }
        binding.setTransferMenu(transfer_menu_list.get(position));

        AC_Class.TransferMenu currentItem = transfer_menu_list.get(position);
        if (selectedItems.contains(currentItem)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return binding.getRoot();
    }

    // Method to select an item
    public void selectItem(AC_Class.TransferMenu item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.TransferMenu item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.TransferMenu> getSelectedItems() {
        return selectedItems;
    }
}
