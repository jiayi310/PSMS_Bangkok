package com.example.androidmobilestock;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.androidmobilestock.databinding.RowScBinding;

import java.text.DecimalFormat;
import java.util.List;

public class StockTakeDtlAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.StockTakeDetails> stDtl;

    public StockTakeDtlAdapter(Context context, List<AC_Class.StockTakeDetails> stDtlFP) {
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
        RowScBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_sc, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowScBinding) convertView.getTag();
        }

        if(stDtl.get(position).getBatchNo()!=null){
            binding.sclistBatch.setVisibility(View.VISIBLE);
        }else{
            binding.sclistBatch.setVisibility(View.GONE);
        }
        binding.setStDetail(stDtl.get(position));
        return binding.getRoot();
    }

    @BindingAdapter({"qty"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.###").format(amount));
    }
}
