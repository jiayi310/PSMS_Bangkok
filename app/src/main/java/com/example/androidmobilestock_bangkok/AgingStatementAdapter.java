package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class AgingStatementAdapter extends RecyclerView.Adapter<AgingStatementAdapter.ViewHolder> {

    Context context;
    List<AC_Class.Statement> inv_list;
    private static RecyclerViewClickListener listener;

    public AgingStatementAdapter(Context context, List<AC_Class.Statement> inv_list, RecyclerViewClickListener listener){
        this.context=context;
        this.inv_list=inv_list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aging_statement_table,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(inv_list !=null && inv_list.size() >0){
            AC_Class.Statement invoice =inv_list.get(position);
            holder.id_Date.setText(invoice.getDocDate());
            holder.id_totaldue.setText(String.format(Locale.getDefault(),
                    " %.2f", invoice.getBalance()));
            holder.id_doc_No.setText(invoice.getDocNo());
            holder.id_docType.setText(invoice.getDocType());

        }else
            return;
    }

    @Override
    public int getItemCount() {
        return inv_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView id_Date, id_totaldue,id_docType, id_doc_No;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            id_Date = itemView.findViewById(R.id.date);
            id_totaldue = itemView.findViewById(R.id.totalDue);
            id_doc_No = itemView.findViewById(R.id.docNo);
            id_docType = itemView.findViewById(R.id.docType);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull View v) {

            if (v.getId() == itemView.getId()) {
                listener.onClick(itemView, getAdapterPosition());
            }
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
