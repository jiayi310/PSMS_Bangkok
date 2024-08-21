package com.example.androidmobilestock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.databinding.RowSaBinding;

import java.text.DecimalFormat;
import java.util.List;

public class StockAssemblyDtlAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.StockAssemblyDetails> stDtl;

    public StockAssemblyDtlAdapter(Context context, List<AC_Class.StockAssemblyDetails> stDtlFP) {
        this.context = context;
        this.stDtl = stDtlFP;
    }
    @Override
    public int getCount() {
        return this.stDtl.size();
    }

    @Override
    public Object getItem(int position) {
        return this.stDtl.get(position);
    }

    @Override
    public long getItemId(int position) {
            return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowSaBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_sa, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowSaBinding) convertView.getTag();
        }
        binding.setStDetail(stDtl.get(position));
        return binding.getRoot();
    }

    @BindingAdapter({"qty"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.###").format(amount));
    }
}
