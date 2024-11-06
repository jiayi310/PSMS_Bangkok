package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.RowArListBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ARPaymentAdapter extends BaseAdapter {
    Context context;
    ACDatabase db;
    private List<AC_Class.ARPayment> ar_menu_list;
    String default_curr;
    private Set<AC_Class.ARPayment> selectedItems;

    public ARPaymentAdapter(Context context, List<AC_Class.ARPayment> ar_menu_list, String default_curr) {
        this.context = context;
        this.ar_menu_list = ar_menu_list;
        this.default_curr = default_curr;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return ar_menu_list.size();
    }

    @Override
    public Object getItem(int position) {
        return ar_menu_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowArListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_ar_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowArListBinding) convertView.getTag();
        }
        binding.setARMenu(ar_menu_list.get(position));

        if (default_curr.equals("")){
            binding.setDefaultCurr("");
        } else {
            binding.setDefaultCurr(default_curr);
        }

        if(ar_menu_list.get(position).getRemark() == null || ar_menu_list.get(position).getRemark().equals("")){
            binding.invmenuRemarks.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarks.setVisibility(View.VISIBLE);
        }

        // Set background color based on selection state
        AC_Class.ARPayment currentItem = ar_menu_list.get(position);
        if (selectedItems.contains(currentItem)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return binding.getRoot();
    }


    // Method to select an item
    public void selectItem(AC_Class.ARPayment item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.ARPayment item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.ARPayment> getSelectedItems() {
        return selectedItems;
    }
}
