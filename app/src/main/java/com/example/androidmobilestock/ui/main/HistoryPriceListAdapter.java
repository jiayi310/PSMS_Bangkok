package com.example.androidmobilestock.ui.main;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.RowHistorypriceListBinding;

import java.util.List;

public class HistoryPriceListAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.HistoryPrice> historyPriceList;
    String defaultCurr;

    public HistoryPriceListAdapter(Context context, List<AC_Class.HistoryPrice> historyPriceList, String defaultCurrFP) {
        this.context = context;
        this.historyPriceList = historyPriceList;
        defaultCurr = defaultCurrFP;
    }

    @Override
    public int getCount() {
        return historyPriceList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyPriceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHistorypriceListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_historyprice_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowHistorypriceListBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        binding.setHistprice(historyPriceList.get(position));
        binding.setDefaultCurr(defaultCurr);
        return binding.getRoot();
    }
}
