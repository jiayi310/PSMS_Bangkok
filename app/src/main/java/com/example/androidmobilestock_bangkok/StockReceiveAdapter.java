package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class StockReceiveAdapter extends  RecyclerView.Adapter<StockReceiveAdapter.MyViewHolder>{

    Context context;
    private List<AC_Class.StockReceive> stockReceiveList;
    private static MyViewHolder.RecyclerViewClickListener mListener;

    public StockReceiveAdapter(Context context, List<AC_Class.StockReceive> stockReceiveList, MyViewHolder.RecyclerViewClickListener listener) {
        this.context = context;
        this.stockReceiveList = stockReceiveList;
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.stockreceive_row_list,viewGroup, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        AC_Class.StockReceive stockReceive = stockReceiveList.get(i);

        myViewHolder.tv_DocNo.setText(stockReceive.getDocNo());
        myViewHolder.tv_DocDate.setText(stockReceive.getDocDate());
        myViewHolder.tv_Description.setText(stockReceive.getDescription());
        myViewHolder.tv_remarks.setText(stockReceive.getRemarks());
        if (stockReceive.getUploaded() == 1){
            myViewHolder.iv_Tick.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.iv_Tick.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return stockReceiveList.size();
    }

    public void updateList(List<AC_Class.StockReceive> newList) {
        stockReceiveList.clear();
        stockReceiveList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_DocNo,tv_DocDate, tv_Description, tv_remarks;
        ImageView iv_Tick;

        public MyViewHolder(View itemView, final RecyclerViewClickListener listener) {
            super(itemView);
            tv_DocNo = itemView.findViewById(R.id.tv_DocNo);
            tv_DocDate = itemView.findViewById(R.id.tv_DocDate);
            tv_Description = itemView.findViewById(R.id.tv_Description);
            tv_remarks = itemView.findViewById(R.id.tv_remarks);
            iv_Tick = itemView.findViewById(R.id.iv_Tick);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onClick(v, position);
                        }
                    }
                }
            });
        }

        public  interface RecyclerViewClickListener{
            void onClick(View v,int position);
        }
    }
}

