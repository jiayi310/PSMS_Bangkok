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

public class StockReceive_ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<AC_Class.StockReceive> stockReceiveList;
    private Set<AC_Class.StockReceive> selectedItems;

    public StockReceive_ListViewAdapter(Context context, List<AC_Class.StockReceive> stockReceiveList) {
        this.context = context;
        this.stockReceiveList = stockReceiveList;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return stockReceiveList.size();
    }

    @Override
    public Object getItem(int position) {
        return stockReceiveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.stockreceive_setting_row_list, parent, false);
            holder = new ViewHolder();
            holder.tv_DocNo = convertView.findViewById(R.id.tv_DocNo);
            holder.tv_DocDate = convertView.findViewById(R.id.tv_DocDate);
            holder.tv_Description = convertView.findViewById(R.id.tv_Description);
            holder.tv_remarks_lbl = convertView.findViewById(R.id.tv_remarks_lbl);
            holder.tv_remarks = convertView.findViewById(R.id.tv_remarks);
            holder.iv_Tick = convertView.findViewById(R.id.iv_Tick);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AC_Class.StockReceive stockReceive = stockReceiveList.get(position);
        holder.tv_DocNo.setText(stockReceive.getDocNo());
        holder.tv_DocDate.setText(stockReceive.getDocDate());
        holder.tv_Description.setText(stockReceive.getDescription());


        if (stockReceive.getRemarks() == null || stockReceive.getRemarks().isEmpty()){
            holder.tv_remarks_lbl.setVisibility(View.GONE);
            holder.tv_remarks.setVisibility(View.GONE);
        } else {
            holder.tv_remarks_lbl.setVisibility(View.VISIBLE);
            holder.tv_remarks.setVisibility(View.VISIBLE);
            holder.tv_remarks.setText(stockReceive.getRemarks());
        }

        if (stockReceive.getUploaded() == 1){
            holder.iv_Tick.setVisibility(View.VISIBLE);
        } else {
            holder.iv_Tick.setVisibility(View.GONE);
        }

        // Set background color based on selection state
        if (selectedItems.contains(stockReceive)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    // Method to select an item
    public void selectItem(AC_Class.StockReceive item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.StockReceive item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.StockReceive> getSelectedItems() {
        return selectedItems;
    }

    private static class ViewHolder {
        TextView tv_DocNo,tv_DocDate, tv_Description, tv_remarks, tv_remarks_lbl;
        ImageView iv_Tick;
    }
}
