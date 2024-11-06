package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobilestock_bangkok.databinding.OutstandinglistBinding;
import com.example.androidmobilestock_bangkok.databinding.RowPlSoListBinding;
import com.example.androidmobilestock_bangkok.databinding.RowSoListBinding;

import java.util.List;


public class PL_SO_DownloadListAdapter extends RecyclerView.Adapter<PL_SO_DownloadListAdapter.RecyclerViewHolder> {

    Context context;
    private List<AC_Class.SOMenu> so_list;
    private static RecyclerViewClickListener listener;

    public PL_SO_DownloadListAdapter(Context context, List<AC_Class.SOMenu> so_listFP, RecyclerViewClickListener listener) {
        this.context = context;
        this.so_list = so_listFP;
        this.listener = listener;
    }


    @NonNull
    @Override
    public PL_SO_DownloadListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_pl_so_list, parent, false);


        return new PL_SO_DownloadListAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PL_SO_DownloadListAdapter.RecyclerViewHolder holder,
                                 int position) {

        holder.binding.setSo(so_list.get(position));
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
        return so_list.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements CheckBox.OnClickListener {
        RowPlSoListBinding binding;
        CheckBox checkBox;
        ConstraintLayout relativeLayout;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            checkBox.setOnClickListener(this);
            relativeLayout.setOnClickListener(this);

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
            if(view.getId() == relativeLayout.getId()){
                listener.onLayoutClick(itemView, getAdapterPosition());
            }
        }

    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
        void onFalseClick(View v, int position);
        void onLayoutClick(View v, int position);
    }


}

