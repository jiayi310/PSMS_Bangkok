package com.example.androidmobilestock.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.RowDebtorListBinding;

import java.util.ArrayList;
import java.util.List;

public class DebtorListViewAdapter extends ArrayAdapter<AC_Class.Debtor> {
    Context context;
    List<AC_Class.Debtor> debtor_list;
    private Filter filter;


    public DebtorListViewAdapter(Context context, int resourceId,	List<AC_Class.Debtor> objects) {
        super(context, resourceId, objects);
        this.context = context;
        this.debtor_list = objects;
    }

    @Override
    public int getCount() {
        return debtor_list.size();
    }

    @Override
    public AC_Class.Debtor getItem(int position) {
        return debtor_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public Filter getFilter() {
        if (filter == null)
            filter = new DebtorFilter();
        return filter;
    }

    private class DebtorFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = debtor_list;
                results.count = debtor_list.size();
            }
            else {
                // We perform filtering operation
                List<AC_Class.Debtor> nDebtorList = new ArrayList<AC_Class.Debtor>();

                for (AC_Class.Debtor p : debtor_list) {
                    if (p.getCompanyName().toUpperCase()
                            .startsWith(constraint.toString().toUpperCase()))
                        nDebtorList.add(p);
                }

                results.values = nDebtorList;
                results.count = nDebtorList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                debtor_list = (List<AC_Class.Debtor>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowDebtorListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_debtor_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowDebtorListBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#fedebe"));
        }

        if(debtor_list.get(position).getCompanyName()!=null && !debtor_list.get(position).getCompanyName().equals("")){
            binding.textDebtorName.setVisibility(View.VISIBLE);
        }else{
            binding.textDebtorName.setVisibility(View.GONE);
        }

        if(debtor_list.get(position).getAttention()!=null && !debtor_list.get(position).getAttention().equals("")){
            binding.textAttention.setVisibility(View.VISIBLE);
        }else{
            binding.textAttention.setVisibility(View.GONE);
        }

        if(debtor_list.get(position).getSalesAgent()!=null && !debtor_list.get(position).getSalesAgent().equals("")){
            binding.textAgentName.setVisibility(View.VISIBLE);
        }else{
            binding.textAgentName.setVisibility(View.GONE);
        }

        if(debtor_list.get(position).getPhone()!=null && !debtor_list.get(position).getPhone().equals("")){
            binding.textPhone.setVisibility(View.VISIBLE);
        }else{
            binding.textPhone.setVisibility(View.GONE);
        }

        if(debtor_list.get(position).getDescription()!=null && !debtor_list.get(position).getDescription().equals("")){
            binding.textDebtorName2.setVisibility(View.VISIBLE);
        }else{
            binding.textDebtorName2.setVisibility(View.GONE);
        }


        binding.setDebtor(debtor_list.get(position));
        return binding.getRoot();
    }
}
