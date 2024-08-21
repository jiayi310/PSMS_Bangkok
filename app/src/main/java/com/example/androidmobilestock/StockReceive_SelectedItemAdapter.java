package com.example.androidmobilestock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;


public class StockReceive_SelectedItemAdapter extends RecyclerView.Adapter<StockReceive_SelectedItemAdapter.ViewHolder> {

    private List<AC_Class.StockReceiveDetails> itemList;
    private Context context;
    private static RecyclerViewClickListener listener;

    public StockReceive_SelectedItemAdapter(List<AC_Class.StockReceiveDetails> itemList, Context context, RecyclerViewClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sr_row_purchasedtllist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AC_Class.StockReceiveDetails item = itemList.get(position);

        holder.invdtllist_itemcode.setText(item.getItemCode());
        holder.invdtllist_description.setText(item.getItemDescription());
        holder.invdtllist_batch_no.setText(item.getBatch_No());
        holder.invdtllist_remarks.setText(item.getRemarks());
        holder.invdtllist_remarks2.setText(item.getRemarks2());
        holder.invdtllist_chosen_qty.setText(String.valueOf(item.getQuantity()));
        holder.invdtllist_chosen_uom.setText(item.getUOM());
        holder.invdtllist_uprice.setText(String.format("%.2f", item.getUTD_Cost()));
        holder.invdtllist_totalin.setText(String.format("%.2f", item.getSubTotal()));
        holder.bill.setText(Integer.toString(position+1));

        ACDatabase db;
        db= new ACDatabase(context);
        if(db.getItemImage(itemList.get(position).getItemCode())!=null) {
            holder.itemImage.setImageBitmap(db.getItemImage(itemList.get(position).getItemCode()));
        }else{
            holder.itemImage.setImageResource(R.drawable.photo_empty);
        }

        holder.itemView.setOnClickListener(holder);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView invdtllist_itemcode, invdtllist_description, invdtllist_batch_no, invdtllist_remarks, invdtllist_remarks2,
                invdtllist_chosen_qty, invdtllist_chosen_uom, invdtllist_uprice, invdtllist_totalin, bill;
        ShapeableImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            invdtllist_itemcode = itemView.findViewById(R.id.invdtllist_itemcode);
            invdtllist_description = itemView.findViewById(R.id.invdtllist_description);
            invdtllist_batch_no = itemView.findViewById(R.id.invdtllist_batch_no);
            invdtllist_remarks = itemView.findViewById(R.id.invdtllist_remarks);
            invdtllist_remarks2 = itemView.findViewById(R.id.invdtllist_remarks2);
            invdtllist_chosen_qty = itemView.findViewById(R.id.invdtllist_chosen_qty);
            invdtllist_chosen_uom = itemView.findViewById(R.id.invdtllist_chosen_uom);
            invdtllist_uprice = itemView.findViewById(R.id.invdtllist_uprice);
            invdtllist_totalin = itemView.findViewById(R.id.invdtllist_totalin);
            itemImage = itemView.findViewById(R.id.itemImage);
            bill = itemView.findViewById(R.id.bill);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == itemView.getId()){
                if (listener != null){
                    listener.onClick(itemView, getAdapterPosition());
                }
            }
        }
    }

    public  interface RecyclerViewClickListener{
        void onClick(View v,int position);
    }
}
