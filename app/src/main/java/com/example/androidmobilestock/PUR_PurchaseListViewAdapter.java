package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.databinding.PurRowPurchaselistBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PUR_PurchaseListViewAdapter extends BaseAdapter {
    Context context;
    ACDatabase db;
    private List<AC_Class.PurchaseMenu> menuList;
    String default_curr;
    private Set<AC_Class.PurchaseMenu> selectedItems;

    public PUR_PurchaseListViewAdapter(Context context, List<AC_Class.PurchaseMenu> menuListFP, String default_curr) {
        this.context = context;
        this.menuList = menuListFP;
        this.default_curr = default_curr;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PurRowPurchaselistBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pur_row_purchaselist, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (PurRowPurchaselistBinding) convertView.getTag();
        }
        binding.setInvMenu(menuList.get(position));
        if (menuList.get(position).getRemarks() == null ){
            binding.invmenuRemarks.setVisibility(View.GONE);
            binding.invmenuRemarksword.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarks.setVisibility(View.VISIBLE);
            binding.invmenuRemarksword.setVisibility(View.VISIBLE);
        }
        if (menuList.get(position).getRemarks2() == null ){
            binding.invmenuRemarks2.setVisibility(View.GONE);
            binding.invmenuRemarksword2.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarks2.setVisibility(View.VISIBLE);
            binding.invmenuRemarksword2.setVisibility(View.VISIBLE);
        }
        if (menuList.get(position).getRemarks3() == null ){
            binding.invmenuRemarks3.setVisibility(View.GONE);
            binding.invmenuRemarksword3.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarks3.setVisibility(View.VISIBLE);
            binding.invmenuRemarksword3.setVisibility(View.VISIBLE);
        }
        if (menuList.get(position).getRemarks4() == null ){
            binding.invmenuRemarks4.setVisibility(View.GONE);
            binding.invmenuRemarksword4.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarks4.setVisibility(View.VISIBLE);
            binding.invmenuRemarksword4.setVisibility(View.VISIBLE);
        }
        if (default_curr.equals("")){
            binding.setDefaultCurr("");
        } else {
            binding.setDefaultCurr(default_curr);
        }

        // Set background color based on selection state
        AC_Class.PurchaseMenu currentItem = menuList.get(position);
        if (selectedItems.contains(currentItem)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return binding.getRoot();
    }

    // Method to select an item
    public void selectItem(AC_Class.PurchaseMenu item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.PurchaseMenu item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.PurchaseMenu> getSelectedItems() {
        return selectedItems;
    }
}
