package com.example.androidmobilestock;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidmobilestock.databinding.RowItemDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class CartViewListAdapter extends RecyclerView.Adapter<CartViewListAdapter.RecyclerViewHolder> {
    Context context;
    private List<AC_Class.Item> mData;
    private List<AC_Class.Item> Displayitemlist;
    List<AC_Class.Item> s_item = new ArrayList<>();
    AC_Class.Item item = new AC_Class.Item();
    private static RecyclerViewClickListener listener;
    AC_Class.InvoiceDetails invoiceDetails;
    ACDatabase db;
    android.widget.ImageView imageView;

    public CartViewListAdapter(Context context, List<AC_Class.Item> mData, RecyclerViewClickListener listener) {
        this.context = context;
        this.mData = mData;
        this.Displayitemlist = mData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_detail, parent, false);

        imageView = view.findViewById(R.id.ImageItem);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        db = new ACDatabase(context);
        Cursor cur = db.getReg("6");
        if (cur.moveToFirst()) {
            holder.binding.setDefaultCurr(cur.getString(0));
        }
        try {
            holder.binding.setItem(mData.get(position));
            if (db.getItemImage(Displayitemlist.get(position).getItemCode()) != null) {
                Glide.with(context).load(db.getItemImage(Displayitemlist.get(position).getItemCode())).fitCenter().into(holder.binding.ImageItem);
            } else {
                holder.binding.ImageItem.setImageResource(R.drawable.photo_empty);
            }
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
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
        RowItemDetailBinding binding;
        ACDatabase db;
        Context context;
        android.widget.ImageView imageView;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            imageView = itemView.findViewById(R.id.ImageItem);
            imageView.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(@NonNull View v) {

            if (v.getId() == imageView.getId()) {
                listener.onImageClick(imageView, getAdapterPosition());
            } else if (v.getId() == itemView.getId()) {
                listener.onClick(itemView, getAdapterPosition());
            }
        }


    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);

        void onImageClick(android.widget.ImageView v, int position);
    }

}
