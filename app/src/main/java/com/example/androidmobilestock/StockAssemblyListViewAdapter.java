package com.example.androidmobilestock;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockAssemblyListViewAdapter extends BaseAdapter {
    private Context context;
    private List<AC_Class.StockAssemblyMenu> stockAssemblyList;
    private Set<AC_Class.StockAssemblyMenu> selectedItems;

    public StockAssemblyListViewAdapter(Context context, List<AC_Class.StockAssemblyMenu> stockAssemblyList) {
        this.context = context;
        this.stockAssemblyList = stockAssemblyList;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return stockAssemblyList.size();
    }

    @Override
    public Object getItem(int position) {
        return stockAssemblyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.stocktake_setting_row_list, parent, false);
            holder = new ViewHolder();
            holder.docNo = convertView.findViewById(R.id.docNo);
            holder.tv_remarks = convertView.findViewById(R.id.tv_remarks);
            holder.dateTime = convertView.findViewById(R.id.dateTime);
            holder.iv_Tick = convertView.findViewById(R.id.iv_Tick);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AC_Class.StockAssemblyMenu stockAssembly = stockAssemblyList.get(position);
        holder.docNo.setText(stockAssembly.getDocNo());


        if (stockAssembly.getDocDate() == null || stockAssembly.getDocDate().isEmpty()){
            holder.dateTime.setText("");
        } else {
            holder.dateTime.setText(stockAssembly.getDocDate());
        }

        if (stockAssembly.getRemarks() == null || stockAssembly.getRemarks().isEmpty()){
            holder.tv_remarks.setText("");
        } else {
            holder.tv_remarks.setText(stockAssembly.getRemarks());
        }

        if (stockAssembly.getUploaded() == 1){
            holder.iv_Tick.setVisibility(View.VISIBLE);
        } else {
            holder.iv_Tick.setVisibility(View.GONE);
        }

        // Set background color based on selection state
        if (selectedItems.contains(stockAssembly)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    // Method to select an item
    public void selectItem(AC_Class.StockAssemblyMenu item) {
        selectedItems.add(item);
        notifyDataSetChanged();
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.StockAssemblyMenu item) {
        selectedItems.remove(item);
        notifyDataSetChanged();
    }

    // Method to get selected items
    public Set<AC_Class.StockAssemblyMenu> getSelectedItems() {
        return selectedItems;
    }

    private static class ViewHolder {
        TextView docNo, tv_remarks, dateTime;
        ImageView iv_Tick;
    }
}
