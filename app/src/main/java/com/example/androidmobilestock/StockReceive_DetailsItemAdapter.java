package com.example.androidmobilestock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class StockReceive_DetailsItemAdapter extends RecyclerView.Adapter<StockReceive_DetailsItemAdapter.MyViewHolder>{

    Context context;
    List<AC_Class.StockReceiveDetails> itemList;

    public StockReceive_DetailsItemAdapter(List<AC_Class.StockReceiveDetails> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stockreceive_row_dtl_list, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        AC_Class.StockReceiveDetails item = itemList.get(i);

        myViewHolder.txt_bill.setText(Integer.toString(i+1));
        myViewHolder.tv_itemCode.setText(item.getItemCode());
        myViewHolder.tv_description.setText(item.getItemDescription());
        myViewHolder.tv_uom.setText(item.getUOM());
        String qty = String.valueOf(item.getQuantity());
        myViewHolder.tv_qty.setText(qty);
        myViewHolder.txt_cost.setText(String.format("%.2f", item.getUTD_Cost()));
        myViewHolder.tv_location.setText(item.getLocation());

        String batchNo = item.getBatch_No();
        if (batchNo == null || batchNo.isEmpty()){
            myViewHolder.tv_batchno_lbl.setVisibility(View.GONE);
            myViewHolder.tv_batchno.setVisibility(View.GONE);
        } else {
            myViewHolder.tv_batchno.setText(item.getBatch_No());
        }

        String remarks = item.getRemarks();
        if (remarks == null || remarks.isEmpty()){
            myViewHolder.tv_remarks_lbl.setVisibility(View.GONE);
        } else {
            myViewHolder.tv_remarks_lbl.setVisibility(View.VISIBLE);
            myViewHolder.tv_remarks_lbl.setText(item.getRemarks());
        }

        String remarks2 = item.getRemarks2();
        if (remarks2 == null || remarks2.isEmpty()){
            myViewHolder.tv_Remarks_txt.setVisibility(View.GONE);
        } else {
            myViewHolder.tv_Remarks_txt.setVisibility(View.VISIBLE);
            myViewHolder.tv_Remarks_txt.setText(item.getRemarks2());
        }

        ACDatabase db;
        db= new ACDatabase(context);
        if(db.getItemImage(itemList.get(i).getItemCode())!=null) {
            myViewHolder.itemImage.setImageBitmap(db.getItemImage(itemList.get(i).getItemCode()));
        }else{
            myViewHolder.itemImage.setImageResource(R.drawable.photo_empty);
        }

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_bill, tv_itemCode, tv_description, tv_uom, tv_qty, txt_cost, tv_location, tv_Remarks_txt, tv_remarks_lbl, tv_batchno, tv_batchno_lbl;
        CircleImageView itemImage;


        public MyViewHolder(View itemView) {
            super(itemView);

            txt_bill = itemView.findViewById(R.id.txt_bill);
            itemImage = itemView.findViewById(R.id.itemImage);
            tv_itemCode = itemView.findViewById(R.id.tv_itemCode);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_uom = itemView.findViewById(R.id.tv_uom);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            txt_cost = itemView.findViewById(R.id.txt_cost);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_Remarks_txt = itemView.findViewById(R.id.tv_Remarks_txt);
            tv_remarks_lbl = itemView.findViewById(R.id.tv_remarks_lbl);
            tv_batchno = itemView.findViewById(R.id.tv_batchno);
            tv_batchno_lbl = itemView.findViewById(R.id.tv_batchno_lbl);

        }

    }
}
