package com.example.androidmobilestock;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobilestock.databinding.ItembatchlistBinding;
import com.example.androidmobilestock.databinding.OutstandinglistBinding;
import com.example.androidmobilestock.ui.main.ItemBatchListAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ARPayment_OutstandingListAdapter extends RecyclerView.Adapter<ARPayment_OutstandingListAdapter.RecyclerViewHolder> {
    Context context;
    private List<AC_Class.AROutstanding> mData;
    private List<AC_Class.AROutstanding> Displayitemlist;
    private static RecyclerViewClickListener listener;

    ACDatabase db;
    public ARPayment_OutstandingListAdapter(Context context, List<AC_Class.AROutstanding> mData, RecyclerViewClickListener listener) {
        this.context = context;
        this.mData = mData;
        this.Displayitemlist = mData;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ARPayment_OutstandingListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outstandinglist, parent, false);


        return new ARPayment_OutstandingListAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ARPayment_OutstandingListAdapter.RecyclerViewHolder holder, int position) {

        NumberFormat nf = new DecimalFormat("##.###");
        db = new ACDatabase(context);
        holder.binding.setOutstanding(mData.get(position));

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return Displayitemlist.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements CheckBox.OnClickListener {
        OutstandinglistBinding binding;
        CheckBox checkBox;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == checkBox.getId()) {
                if(checkBox.isChecked()) {
                    listener.onClick(itemView, getAdapterPosition());
                }else{
                    listener.onFalseClick(itemView, getAdapterPosition());
                }
            }
        }

    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
        void onFalseClick(View v, int position);
    }

}


