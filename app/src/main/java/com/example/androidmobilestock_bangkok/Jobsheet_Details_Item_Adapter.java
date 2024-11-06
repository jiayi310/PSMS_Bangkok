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

public class Jobsheet_Details_Item_Adapter extends RecyclerView.Adapter<Jobsheet_Details_Item_Adapter.ViewHolder> {

    private List<AC_Class.JobSheetDetails> jobSheetDetailsList;
    private Context context;
    ACDatabase db;

    public Jobsheet_Details_Item_Adapter(Context context, List<AC_Class.JobSheetDetails> jobSheetDetailsList) {
        this.context = context;
        this.jobSheetDetailsList = jobSheetDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_jobsheet_dtl_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AC_Class.JobSheetDetails details = jobSheetDetailsList.get(position);

        holder.itemCode.setText(details.getItemCode());
        holder.description.setText(details.getItemDescription());
        holder.quantity.setText(String.valueOf(details.getQuantity()));
        holder.uom.setText(details.getUOM());
        //holder.unitPrice.setText(String.format("%.2f", details.getUPrice()));
        //holder.taxValue.setText(String.format("%.2f", details.getDiscount()));
        //holder.totalValue.setText(String.format("%.2f", details.getTotal_In()));
        holder.batchNo.setText(details.getBatchNo());
        holder.remarks.setText(details.getRemarks());
        holder.remarks2.setText(details.getRemarks2());
        holder.bill.setText(Integer.toString(position+1));

        if (details.getRemarks() == null || details.getRemarks().isEmpty()){
            holder.remarks.setVisibility(View.GONE);
        }

        if (details.getRemarks2() == null || details.getRemarks2().isEmpty()){
            holder.remarks2.setVisibility(View.GONE);
        }

        db = new ACDatabase(context.getApplicationContext());

        Bitmap itemImage = db.getItemImage(details.getItemCode());
        if (itemImage != null) {
            holder.imageItemCart.setImageBitmap(itemImage);
        } else {
            holder.imageItemCart.setImageResource(R.drawable.photo_empty);
        }

        }

    @Override
    public int getItemCount() {
        return jobSheetDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView imageItemCart;
        public TextView bill, itemCode, description, quantity, uom, batchNo, remarks, remarks2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bill = itemView.findViewById(R.id.bill);
            imageItemCart = itemView.findViewById(R.id.ImageItemCart);
            itemCode = itemView.findViewById(R.id.invdtllist_itemcode);
            description = itemView.findViewById(R.id.invdtllist_description);
            quantity = itemView.findViewById(R.id.invdtllist_chosen_qty);
            uom = itemView.findViewById(R.id.invdtllist_chosen_uom);

            batchNo = itemView.findViewById(R.id.batchno);
            remarks = itemView.findViewById(R.id.remarks);
            remarks2 = itemView.findViewById(R.id.remarks2);
        }
    }
}
