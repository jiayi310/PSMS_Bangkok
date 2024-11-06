package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class Jobsheet_SelectedItemAdapter extends RecyclerView.Adapter<Jobsheet_SelectedItemAdapter.ViewHolder> {

    private List<AC_Class.JobSheetDetails> itemList;

    private Context context;
    ACDatabase db;
    private static Jobsheet_SelectedItemAdapter.RecyclerViewClickListener listener;

    //InvoiceDtlAdapterListView.java
    //InvoiceDtlList_Sales.java

    public Jobsheet_SelectedItemAdapter(List<AC_Class.JobSheetDetails> itemList, Context context, Jobsheet_SelectedItemAdapter.RecyclerViewClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.db = new ACDatabase(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_jobsheetdtl_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AC_Class.JobSheetDetails item = itemList.get(position);

        holder.invdtllist_itemcode.setText(item.getItemCode());
        holder.invdtllist_description.setText(item.getItemDescription());
        holder.batchno.setText(item.getBatchNo());
        holder.remarks.setText(item.getRemarks());
        holder.remarks2.setText(item.getRemarks2());
        holder.invdtllist_chosen_qty.setText(String.valueOf(item.getQuantity()));
        holder.invdtllist_chosen_uom.setText(item.getUOM());
        holder.invdtllist_uprice.setText(String.format("%.2f", item.getUPrice()));
        holder.invdtllist_taxvalue.setText(String.format("%.2f", item.getDiscount()));
        holder.invdtllist_totalin.setText(String.format("%.2f", item.getTotal_In()));
        holder.bill.setText(Integer.toString(position+1));

        if (item.getRemarks() == null || item.getRemarks().isEmpty()){
            holder.remarks.setVisibility(View.GONE);
        }

        if (item.getRemarks2() == null || item.getRemarks2().isEmpty()){
            holder.remarks2.setVisibility(View.GONE);
        }

        Bitmap itemImage = db.getItemImage(item.getItemCode());
        if (itemImage != null) {
            holder.imageItemCart.setImageBitmap(itemImage);
        } else {
            holder.imageItemCart.setImageResource(R.drawable.photo_empty);
        }

        holder.itemView.setOnClickListener(holder);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ShapeableImageView imageItemCart;
        TextView invdtllist_itemcode, invdtllist_description, batchno, remarks, remarks2,
                invdtllist_chosen_qty, invdtllist_chosen_uom, invdtllist_uprice, invdtllist_totalin, bill, invdtllist_taxvalue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bill = itemView.findViewById(R.id.bill);
            imageItemCart = itemView.findViewById(R.id.ImageItemCart);
            invdtllist_itemcode = itemView.findViewById(R.id.invdtllist_itemcode);
            invdtllist_description = itemView.findViewById(R.id.invdtllist_description);
            invdtllist_chosen_qty = itemView.findViewById(R.id.invdtllist_chosen_qty);
            invdtllist_chosen_uom = itemView.findViewById(R.id.invdtllist_chosen_uom);
            invdtllist_uprice = itemView.findViewById(R.id.invdtllist_uprice);
            invdtllist_taxvalue = itemView.findViewById(R.id.invdtllist_taxvalue);
            invdtllist_totalin = itemView.findViewById(R.id.invdtllist_totalin);
            remarks = itemView.findViewById(R.id.remarks);
            remarks2 = itemView.findViewById(R.id.remarks2);
            batchno = itemView.findViewById(R.id.batchno);

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
