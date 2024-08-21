package com.example.androidmobilestock.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.ItembatchlistBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ItemBatchListAdapter extends RecyclerView.Adapter<ItemBatchListAdapter.RecyclerViewHolder> {
    Context context;
    private List<AC_Class.ItemBatch> mData;
    private List<AC_Class.ItemBatch> Displayitemlist;
    private static RecyclerViewClickListener listener;

    ACDatabase db;
    public ItemBatchListAdapter(Context context, List<AC_Class.ItemBatch> mData, RecyclerViewClickListener listener) {
        this.context = context;
        this.mData = mData;
        this.Displayitemlist = mData;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ItemBatchListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itembatchlist, parent, false);


        return new ItemBatchListAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        NumberFormat nf = new DecimalFormat("##.###");
        db = new ACDatabase(context);
        holder.binding.setItembatch(mData.get(position));

        if(mData.get(position).getLocation()!=null) {
            Cursor data = db.getStockBalanceBatch(mData.get(position).getItemCode(), mData.get(position).getBatchNo(), mData.get(position).getLocation(), mData.get(position).getUOM());
            if (data.moveToFirst()) {
                holder.binding.balqty.setText(nf.format(data.getDouble(data.getColumnIndex("BalQty"))));
            }
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return Displayitemlist.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItembatchlistBinding binding;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == itemView.getId()) {
                listener.onClick(itemView, getAdapterPosition());
            }
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

}

