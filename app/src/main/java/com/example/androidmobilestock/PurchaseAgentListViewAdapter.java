package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.databinding.RowPurchaseagentlistBinding;

import java.util.List;

public class PurchaseAgentListViewAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.PurchaseAgent> agent_list;

    public PurchaseAgentListViewAdapter(Context context, List<AC_Class.PurchaseAgent> agent_list) {
        this.context = context;
        this.agent_list = agent_list;
    }

    @Override
    public int getCount() {
        return agent_list.size();
    }

    @Override
    public Object getItem(int position) {
        return agent_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowPurchaseagentlistBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_purchaseagentlist, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowPurchaseagentlistBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        binding.setAgent(agent_list.get(position));
        return binding.getRoot();
    }
}
