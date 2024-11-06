package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidmobilestock_bangkok.databinding.PplRowCartListBinding;
import com.example.androidmobilestock_bangkok.databinding.RowCartListBinding;

import java.util.List;

public class StockReceiveCartListAdapter extends RecyclerView.Adapter<StockReceiveCartListAdapter.RecyclerViewHolder> {

    Context context;
    ACDatabase db;
    private static RecyclerViewClickListener listener;
    List<AC_Class.StockReceiveDetails> StockReceiveDetailsList;



    public StockReceiveCartListAdapter(Context context, List<AC_Class.StockReceiveDetails> stockReceiveDetailsList, RecyclerViewClickListener listener) {
        this.context = context;
        this.StockReceiveDetailsList = stockReceiveDetailsList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ppl_row_cart_list, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        //abc
        // holder.binding.setStDetail(StockReceiveDetailsList.get(position));
        holder.binding.bill.setText(Integer.toString(position + 1));
        if(StockReceiveDetailsList.get(position).getRemarks()!=null) {
            if (StockReceiveDetailsList.get(position).getRemarks().equals("")) {
                holder.binding.remarks.setVisibility(View.GONE);
            }
        }else{
            holder.binding.remarks.setVisibility(View.GONE);
        }

        if(StockReceiveDetailsList.get(position).getBatch_No()!=null) {
            if (StockReceiveDetailsList.get(position).getBatch_No().equals("")) {
                holder.binding.batchno.setVisibility(View.GONE);
            }
        }else{
            holder.binding.batchno.setVisibility(View.GONE);
        }

        db = new ACDatabase(context);
        if (db.getItemImage(StockReceiveDetailsList.get(position).getItemCode()) != null) {
            Glide.with(context).load(db.getItemImage(StockReceiveDetailsList.get(position).getItemCode())).fitCenter().into(holder.binding.ImageItemCart);

        } else {
            holder.binding.ImageItemCart.setImageResource(R.drawable.photo_empty);
        }

        holder.binding.ImageItemCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageView.class);
                intent.putExtra("itemCode", StockReceiveDetailsList.get(position).getItemCode());
                ((StockReceive_AddNewItem) context).startActivityForResult(intent, 99);
            }
        });

        holder.binding.sclistQuantity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.subquantity.setFocusable(false);
                holder.binding.addquantity.setFocusable(false);
                holder.binding.ImageItemCart.setFocusable(false);
                holder.binding.relativeLayout5.setFocusable(false);

                holder.binding.sclistQuantity2.setFocusable(true);
                holder.binding.sclistQuantity2.setEnabled(true);
                holder.binding.sclistQuantity2.setCursorVisible(true);
                holder.binding.sclistQuantity2.setFocusableInTouchMode(true);
            }
        });

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.StockReceiveDetailsList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageButton listImg;
        private final ImageButton listImgSub;
        PplRowCartListBinding binding;
        android.widget.ImageView imageView;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            listImg = (ImageButton) itemView.findViewById(R.id.addquantity);
            listImgSub = (ImageButton) itemView.findViewById(R.id.subquantity);
            imageView = (android.widget.ImageView) itemView.findViewById(R.id.ImageItemCart);
            itemView.setOnClickListener(this);
            listImg.setOnClickListener(this);
            listImgSub.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull View v) {


            if (v.getId() == itemView.getId()) {
                listener.onClick(itemView, getAdapterPosition());
            } else if (v.getId() == listImg.getId()) {
                listener.onAddClick(listImg, getAdapterPosition());
            } else if (v.getId() == listImgSub.getId()) {
                listener.onSubClick(listImgSub, getAdapterPosition());
            }
        }

    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);

        void onAddClick(ImageButton v, int position);

        void onSubClick(ImageButton v, int position);
    }

}
