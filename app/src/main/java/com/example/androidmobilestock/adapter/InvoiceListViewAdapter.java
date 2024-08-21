package com.example.androidmobilestock.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.RowInvListBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvoiceListViewAdapter extends BaseAdapter {
    Context context;
    ACDatabase db;
    private List<AC_Class.InvoiceMenu> inv_menu_list;
    private List<AC_Class.Invoice> invoice;
    String default_curr;

    private Set<AC_Class.InvoiceMenu> selectedItems;

    public InvoiceListViewAdapter(Context context, List<AC_Class.InvoiceMenu> inv_menu_list, String default_curr) {
        this.context = context;
        this.inv_menu_list = inv_menu_list;
        this.default_curr = default_curr;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return inv_menu_list.size();
    }

    @Override
    public Object getItem(int position) {
        return inv_menu_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowInvListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_inv_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowInvListBinding) convertView.getTag();
        }
        binding.setInvMenu(inv_menu_list.get(position));

        if (default_curr.equals("")){
            binding.setDefaultCurr("");
        } else {
            binding.setDefaultCurr(default_curr);
        }

        if(inv_menu_list.get(position).getDebtorName2()==null){
            binding.invmenuDebtorname2.setVisibility(View.GONE);
        }else{
            binding.invmenuDebtorname2.setVisibility(View.VISIBLE);
        }


        if(inv_menu_list.get(position).getRemarks()==null){
            binding.invmenuRemarksword.setVisibility(View.GONE);
            binding.invmenuRemarks.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarksword.setVisibility(View.VISIBLE);
            binding.invmenuRemarks.setVisibility(View.VISIBLE);
        }

        if(inv_menu_list.get(position).getRemarks2()==null){
            binding.invmenuRemarksword2.setVisibility(View.GONE);
            binding.invmenuRemarks2.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarksword2.setVisibility(View.VISIBLE);
            binding.invmenuRemarks2.setVisibility(View.VISIBLE);
        }

        if(inv_menu_list.get(position).getRemarks3()==null){
            binding.invmenuRemarksword3.setVisibility(View.GONE);
            binding.invmenuRemarks3.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarksword3.setVisibility(View.VISIBLE);
            binding.invmenuRemarks3.setVisibility(View.VISIBLE);
        }

        if(inv_menu_list.get(position).getRemarks4()==null){
            binding.invmenuRemarksword4.setVisibility(View.GONE);
            binding.invmenuRemarks4.setVisibility(View.GONE);
        }else{
            binding.invmenuRemarksword4.setVisibility(View.VISIBLE);
            binding.invmenuRemarks4.setVisibility(View.VISIBLE);
        }

        if(inv_menu_list.get(position).getStatus()!=null){
            if(inv_menu_list.get(position).getStatus().equals("Rejected")){
                binding.status.setBackgroundResource(R.drawable.circle_red);
            }else if(inv_menu_list.get(position).getStatus().equals("Completed")){
                binding.status.setBackgroundResource(R.drawable.circle_green);
            }else
            {
                binding.status.setBackgroundResource(R.drawable.circle);
            }
        }

        AC_Class.InvoiceMenu currentItem = inv_menu_list.get(position);
        if (selectedItems.contains(currentItem)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return binding.getRoot();
    }

    // Method to select an item
    public void selectItem(AC_Class.InvoiceMenu item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.InvoiceMenu item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.InvoiceMenu> getSelectedItems() {
        return selectedItems;
    }

}


