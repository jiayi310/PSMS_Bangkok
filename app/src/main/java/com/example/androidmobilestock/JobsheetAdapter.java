package com.example.androidmobilestock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class JobsheetAdapter extends  RecyclerView.Adapter<JobsheetAdapter.MyViewHolder>{

    Context context;
    private List<AC_Class.JobSheet> jobSheetList;
    private static MyViewHolder.RecyclerViewClickListener mListener;

    public JobsheetAdapter(Context context, List<AC_Class.JobSheet> jobSheetList, MyViewHolder.RecyclerViewClickListener listener) {
        this.context = context;
        this.jobSheetList = jobSheetList;
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.jobsheet_row_list,viewGroup, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        AC_Class.JobSheet jobSheet = jobSheetList.get(i);

        myViewHolder.tv_DocNo.setText(jobSheet.getDocNo());
        myViewHolder.tv_DocDate.setText(jobSheet.getDocDate());
        myViewHolder.tv_Description.setText(jobSheet.getWorkType());
        myViewHolder.tv_remarks.setText(jobSheet.getReplacementType());
        if (jobSheet.getUploaded() == 1){
            myViewHolder.iv_Tick.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.iv_Tick.setVisibility(View.GONE);
        }
        if (jobSheet.getStatus().equals("New")){
            myViewHolder.status.setBackgroundResource(R.drawable.circle_green);
        } else if (jobSheet.getStatus().equals("Converted")){
            myViewHolder.status.setBackgroundResource(R.drawable.circle);
        } else {
            myViewHolder.status.setBackgroundResource(R.drawable.circle_red);
        }
        if (jobSheet.getAgent() != null){
            myViewHolder.lbl_agent.setVisibility(View.VISIBLE);
            myViewHolder.tv_agent.setVisibility(View.VISIBLE);
            myViewHolder.tv_agent.setText(jobSheet.getAgent());
        } else {
            myViewHolder.lbl_agent.setVisibility(View.GONE);
            myViewHolder.tv_agent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return jobSheetList.size();
    }

    public void updateList(List<AC_Class.JobSheet> newList) {
        jobSheetList.clear();
        jobSheetList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_DocNo,tv_DocDate, tv_Description, tv_remarks, tv_agent, lbl_agent;
        ImageView status, iv_Tick;

        public MyViewHolder(View itemView, final RecyclerViewClickListener listener) {
            super(itemView);
            tv_DocNo = itemView.findViewById(R.id.tv_DocNo);
            tv_DocDate = itemView.findViewById(R.id.tv_DocDate);
            tv_Description = itemView.findViewById(R.id.tv_Description);
            tv_remarks = itemView.findViewById(R.id.tv_remarks);
            status = itemView.findViewById(R.id.status);
            iv_Tick = itemView.findViewById(R.id.iv_Tick);
            lbl_agent = itemView.findViewById(R.id.lbl_agent);
            tv_agent = itemView.findViewById(R.id.tv_agent);

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

