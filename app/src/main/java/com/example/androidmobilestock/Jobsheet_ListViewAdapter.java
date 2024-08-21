package com.example.androidmobilestock;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Jobsheet_ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<AC_Class.JobSheet> jobsheetList;
    private Set<AC_Class.JobSheet> selectedItems;

    public Jobsheet_ListViewAdapter(Context context, List<AC_Class.JobSheet> jobsheetList) {
        this.context = context;
        this.jobsheetList = jobsheetList;
        this.selectedItems = new HashSet<>();
    }

    @Override
    public int getCount() {
        return jobsheetList.size();
    }

    @Override
    public Object getItem(int position) {
        return jobsheetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.jobsheet_setting_row_list, parent, false);
            holder = new ViewHolder();
            holder.tvDocNo = convertView.findViewById(R.id.tv_DocNo);
            holder.tvDocDate = convertView.findViewById(R.id.tv_DocDate);
            holder.tvWorkType = convertView.findViewById(R.id.tv_Description);
            holder.tvReplacType = convertView.findViewById(R.id.tv_remarks);
            holder.lblAgent = convertView.findViewById(R.id.lbl_agent);
            holder.tvAgent = convertView.findViewById(R.id.tv_agent);
            holder.status = convertView.findViewById(R.id.status);
            holder.iv_Tick = convertView.findViewById(R.id.iv_Tick);
            holder.tv_debtorCode = convertView.findViewById(R.id.tv_debtorCode);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AC_Class.JobSheet jobSheet = jobsheetList.get(position);
        holder.tvDocNo.setText(jobSheet.getDocNo());
        holder.tvDocDate.setText(jobSheet.getDocDate());
        holder.tvWorkType.setText(jobSheet.getWorkType());
        holder.tvReplacType.setText(jobSheet.getReplacementType());
        holder.tv_debtorCode.setText(jobSheet.getDebtorCode());

        // Visibility logic for remarks
        if (jobSheet.getAgent() == null || jobSheet.getAgent().isEmpty()) {
            holder.lblAgent.setVisibility(View.GONE);
            holder.tvAgent.setVisibility(View.GONE);
        } else {
            holder.lblAgent.setVisibility(View.VISIBLE);
            holder.tvAgent.setVisibility(View.VISIBLE);
            holder.tvAgent.setText(jobSheet.getAgent());
        }

        if (jobSheet.getUploaded() == 1){
            holder.iv_Tick.setVisibility(View.VISIBLE);
        } else {
            holder.iv_Tick.setVisibility(View.GONE);
        }

        if (jobSheet.getSalesNo() != null){
            holder.status.setBackgroundResource(R.drawable.circle);
        } else {
            holder.status.setBackgroundResource(R.drawable.circle_green);
        }

        // Set background color based on selection state
        if (selectedItems.contains(jobSheet)) {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    // Method to select an item
    public void selectItem(AC_Class.JobSheet item) {
        selectedItems.add(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to deselect an item
    public void deselectItem(AC_Class.JobSheet item) {
        selectedItems.remove(item);
        notifyDataSetChanged(); // Refresh the view
    }

    // Method to get selected items
    public Set<AC_Class.JobSheet> getSelectedItems() {
        return selectedItems;
    }

    private static class ViewHolder {
        TextView tvDocNo;
        TextView tvDocDate;
        TextView tvWorkType;
        TextView tvReplacType;
        TextView lblAgent;
        TextView tvAgent;
        TextView tv_debtorCode;
        ImageView status, iv_Tick;
    }
}
