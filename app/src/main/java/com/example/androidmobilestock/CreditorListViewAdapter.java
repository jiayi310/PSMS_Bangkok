package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.RowCreditorlistBinding;

import java.util.ArrayList;
import java.util.List;

public class CreditorListViewAdapter extends ArrayAdapter<AC_Class.Creditor> {
    Context context;
    List<AC_Class.Creditor> creditorList;
    private Filter filter;


    public CreditorListViewAdapter(Context context, int resourceId,	List<AC_Class.Creditor> objects) {
        super(context, resourceId, objects);
        this.context = context;
        this.creditorList = objects;
    }

    @Override
    public int getCount() {
        return creditorList.size();
    }

    @Override
    public AC_Class.Creditor getItem(int position) {
        return creditorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public Filter getFilter() {
        if (filter == null)
            filter = new CreditorFilter();
        return filter;
    }

    private class CreditorFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = creditorList;
                results.count = creditorList.size();
            }
            else {
                // We perform filtering operation
                List<AC_Class.Creditor> nList = new ArrayList<AC_Class.Creditor>();

                for (AC_Class.Creditor p : creditorList) {
                    if (p.getCompanyName().toUpperCase()
                            .startsWith(constraint.toString().toUpperCase()))
                        nList.add(p);
                }

                results.values = nList;
                results.count = nList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                creditorList = (List<AC_Class.Creditor>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowCreditorlistBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_creditorlist, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowCreditorlistBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        binding.setCreditor(creditorList.get(position));
        return binding.getRoot();
    }
}
