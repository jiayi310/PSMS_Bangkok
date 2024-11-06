package com.example.androidmobilestock_bangkok;

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

public class StockTakeListViewAdapter extends BaseAdapter {
    private Context context;
    private List<AC_Class.StockTake> stockTakeList;
    private Set<AC_Class.StockTake> selectedItems;

    public StockTakeListViewAdapter(Context context, List<AC_Class.StockTake> stockTakeList) {
        this.context = context;
        this.stockTakeList = stockTakeList;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return stockTakeList.size();
    }

    @Override
    public Object getItem(int position) {
        return stockTakeList.get(position);
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

        AC_Class.StockTake stockTake = stockTakeList.get(position);
        holder.docNo.setText(stockTake.getDocNo());


        if (stockTake.getDocDate() == null || stockTake.getDocDate().isEmpty()){
            holder.dateTime.setText("");
        } else {
            holder.dateTime.setText(stockTake.getDocDate());
        }

        if (stockTake.getRemarks() == null || stockTake.getRemarks().isEmpty()){
            holder.tv_remarks.setText("");
        } else {
            holder.tv_remarks.setText(stockTake.getRemarks());
        }

        if (stockTake.getUploaded() == 1){
            holder.iv_Tick.setVisibility(View.VISIBLE);
        } else {
            holder.iv_Tick.setVisibility(View.GONE);
        }


        // Set background color based on selection state
        if (selectedItems.contains(stockTake)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    // Method to select an item
    public void selectItem(AC_Class.StockTake item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.StockTake item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.StockTake> getSelectedItems() {
        return selectedItems;
    }

    private static class ViewHolder {
        TextView docNo, tv_remarks, dateTime;
        ImageView iv_Tick;
    }
}
