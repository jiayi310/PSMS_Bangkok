package com.example.androidmobilestock_bangkok.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingHistoryItemTypeAdapter extends RecyclerView.Adapter<SettingHistoryItemTypeAdapter.MyViewHolder>{

    Context context;
    Map<String, List<AC_Class.Item>> itemsByType;
    private String def_curr;
    SettingHistoryItemAdapter.OnQuantityChangeListener listener;


    public SettingHistoryItemTypeAdapter(Context context, Map<String, List<AC_Class.Item>> itemsByType, SettingHistoryItemAdapter.OnQuantityChangeListener listener) {
        this.context = context;
        this.itemsByType = itemsByType;
        this.listener = listener;
    }

    public void setItemsByType(Map<String, List<AC_Class.Item>> itemsByType) {
        this.itemsByType = itemsByType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SettingHistoryItemTypeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_settings_itemtype_list,viewGroup, false);
        return new SettingHistoryItemTypeAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull SettingHistoryItemTypeAdapter.MyViewHolder holder, int position) {

        String itemType = new ArrayList<>(itemsByType.keySet()).get(position);
        holder.itemType_text.setText(itemType);


        List<AC_Class.Item> items = itemsByType.get(itemType);
        // Setting up the inner RecyclerView
        SettingHistoryItemAdapter itemAdapter = new SettingHistoryItemAdapter(context, items, listener);
        holder.recycle_item_list.setLayoutManager(new LinearLayoutManager(context));
        holder.recycle_item_list.setAdapter(itemAdapter);

    }

    @Override
    public int getItemCount() {
        return itemsByType.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView itemType_text;
        RecyclerView recycle_item_list;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemType_text = itemView.findViewById(R.id.itemType_text);
            recycle_item_list = itemView.findViewById(R.id.item_list);

        }

    }

}
