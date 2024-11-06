package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidmobilestock_bangkok.databinding.RowInvdtlListBinding;

import java.util.List;

public class InvoiceDtlList_Sales extends  RecyclerView.Adapter<InvoiceDtlList_Sales.RecyclerViewHolder> {

    Context context;
    List<AC_Class.InvoiceDetails> invdtl_classlist;
    private InvoiceDtlList_Sales adapter;
    ACDatabase db;
    private static InvoiceDtlList_Sales.RecyclerViewClickListener listener;
    android.widget.ImageView imageView;
    ImageButton listImg;
    ImageButton listImgSub;
    private int scrollStatus=0;


    public InvoiceDtlList_Sales(Context context, List<AC_Class.InvoiceDetails> invdtl_classlist, InvoiceDtlList_Sales.RecyclerViewClickListener listener) {
        this.context = context;
        this.invdtl_classlist = invdtl_classlist;
        this.adapter = this;
        this.listener =listener;

    }

    @NonNull
    @Override
    public InvoiceDtlList_Sales.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_invdtl_list, parent,false);
        return new InvoiceDtlList_Sales.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceDtlList_Sales.RecyclerViewHolder holder, int position) {

        holder.binding.setInvDetail(invdtl_classlist.get(position));
        holder.binding.bill.setText(Integer.toString(position+1));
        if(invdtl_classlist.get(position).getRemarks()!=null) {
            if (invdtl_classlist.get(position).getRemarks().equals("")) {
                holder.binding.remarks.setVisibility(View.GONE);
            }
        }else{
            holder.binding.remarks.setVisibility(View.GONE);
        }

        if(invdtl_classlist.get(position).getRemarks2()!=null) {
            if (invdtl_classlist.get(position).getRemarks2().equals("")) {
                holder.binding.remarks2.setVisibility(View.GONE);
            }
        }else{
            holder.binding.remarks2.setVisibility(View.GONE);
        }
        db = new ACDatabase(context);
        if(db.getItemImage(invdtl_classlist.get(position).getItemCode())!=null) {
            Glide.with(context).load(db.getItemImage(invdtl_classlist.get(position).getItemCode())).fitCenter().into(holder.binding.ImageItemCart);
        }else{
            holder.binding.ImageItemCart.setImageResource(R.drawable.photo_empty);
        }

        Double total_price=0.0;

        for (int i = 0; i < invdtl_classlist.size(); i++) {
            total_price += invdtl_classlist.get(i).getTotal_In();
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.invdtl_classlist.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RowInvdtlListBinding binding;
        ACDatabase db;
        Context context;
        android.widget.ImageView imageView;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            imageView = (android.widget.ImageView) itemView.findViewById(R.id.ImageItemCart);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == itemView.getId()) {
                if(listener!=null) {
                    listener.onClick(itemView, getAdapterPosition());
                }
            }
        }

    }

    public  interface RecyclerViewClickListener{
        void onClick(View v,int position);
    }
}
