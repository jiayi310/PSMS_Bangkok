package com.example.androidmobilestock;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class JobsheetItemListAdapter extends RecyclerView.Adapter<JobsheetItemListAdapter.ItemViewHolder> {

    private List<AC_Class.Item> itemList;
    private String default_curr;
    int SellingPrice = 0;
    ACDatabase db;
    OnItemClickListener onItemClickListener;

    public JobsheetItemListAdapter(Context context, List<AC_Class.Item> itemList, String defcurr, OnItemClickListener onItemClickListener) {
        this.itemList = itemList;
        this.default_curr = defcurr;
        this.db = new ACDatabase(context);
        this.onItemClickListener = onItemClickListener;

        // Initialize SellingPrice
        Cursor sale = db.getReg("48");
        if (sale.moveToFirst()) {
            SellingPrice = sale.getInt(0);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AC_Class.Item item);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sr_item_list, parent, false);
        return new ItemViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        AC_Class.Item item = itemList.get(position);
        holder.text_itemcode.setText(item.getItemCode());
        holder.text_item_name.setText(item.getDescription());
        holder.text_item_UOM.setText(item.getUOM());
        holder.defaultcurr.setText(default_curr);

        double nPrice = item.getPrice();
        holder.text2_Price.setText(String.format("%.2f", nPrice));


        if (SellingPrice == 0) {
            holder.text2_Price.setVisibility(View.GONE);
            holder.defaultcurr.setVisibility(View.GONE);
        } else {
            holder.text2_Price.setVisibility(View.VISIBLE);
            holder.defaultcurr.setVisibility(View.VISIBLE);
        }

        Cursor data = db.getStockBalance(itemList.get(position).getItemCode(), itemList.get(position).getUOM());
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                Float balance = data.getFloat(data.getColumnIndex("SUM(BalQty)"));
                NumberFormat nf = new DecimalFormat("##.###");
                holder.text_item_balance.setText("Balance: "+ nf.format(balance));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void filterList(List<AC_Class.Item> filteredList) {
        itemList = filteredList;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView text_item_name2;
        TextView text_item_name, text_item_UOM, text_itemcode, defaultcurr, text2_Price, text_item_balance;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text_item_balance = itemView.findViewById(R.id.text_item_balance);
            text2_Price = itemView.findViewById(R.id.text2_Price);
            defaultcurr = itemView.findViewById(R.id.defaultcurr);
            text_item_name = itemView.findViewById(R.id.text_item_name);
            text_item_name2 = itemView.findViewById(R.id.text_item_name2);
            text_item_UOM = itemView.findViewById(R.id.text_item_UOM);
            text_itemcode = itemView.findViewById(R.id.text_itemcode);

        }
    }
}
