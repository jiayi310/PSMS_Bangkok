package com.example.androidmobilestock_bangkok;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.RowCartListBinding;
import com.example.androidmobilestock_bangkok.databinding.RowSoListBinding;

import java.util.List;


public class SOListViewAdapter extends RecyclerView.Adapter<SOListViewAdapter.RecyclerViewHolder> {

    Context context;
    private List<AC_Class.SOMenu> so_list;
    private static RecyclerViewClickListener listener;

    public SOListViewAdapter(Context context, List<AC_Class.SOMenu> so_listFP, RecyclerViewClickListener listener) {
        this.context = context;
        this.so_list = so_listFP;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_so_list, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.binding.setSo(so_list.get(position));

        holder.binding.bill.setText(Integer.toString(position + 1));

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#fdddaf"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#fdc89b"));
        }

        if(so_list.get(position).getRemark()==null || so_list.get(position).getRemark().equals("") || so_list.get(position).getRemark().equals("null")){
            holder.binding.tvRemark.setVisibility(View.GONE);
        }else {
            holder.binding.tvRemark.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.so_list.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        RowSoListBinding binding;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(@NonNull View v) {


            if (v.getId() == itemView.getId()) {
                listener.onClick(itemView, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClick(itemView,getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
        void onLongClick(View v, int position);
    }


//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        RowSoListBinding binding;
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.row_so_list,
//                    null);
//            binding = DataBindingUtil.bind(convertView);
//            convertView.setTag(binding);
//        } else {
//            binding = (RowSoListBinding) convertView.getTag();
//        }
//
//        try {
//            binding.setSo(so_list.get(position));
//
//            binding.bill.setText(Integer.toString(position + 1));
//
//            if (position % 2 == 1) {
//                convertView.setBackgroundColor(Color.parseColor("#fdddaf"));
//            } else {
//                convertView.setBackgroundColor(Color.parseColor("#fdc89b"));
//            }
//
//            if(so_list.get(position).getRemark()==null || so_list.get(position).getRemark().equals("") || so_list.get(position).getRemark().equals("null")){
//                binding.tvRemark.setVisibility(View.GONE);
//            }else {
//                binding.tvRemark.setVisibility(View.VISIBLE);
//            }
//
//
//        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
//        return binding.getRoot();
//    }


}

