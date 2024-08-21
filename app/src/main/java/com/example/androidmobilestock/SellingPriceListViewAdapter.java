package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.databinding.RowPriceListBinding;

import java.util.List;

public class SellingPriceListViewAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.SellingPrice> priceList;

    public SellingPriceListViewAdapter(Context context, List<AC_Class.SellingPrice> priceListFP) {
        this.context = context;
        this.priceList = priceListFP;
    }

    @Override
    public int getCount() {
        return priceList.size();
    }

    @Override
    public Object getItem(int position) {
        return priceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowPriceListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_price_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowPriceListBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#fedebe"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        binding.setPrice(priceList.get(position));
        return binding.getRoot();
    }
}
