package com.example.androidmobilestock;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.androidmobilestock.databinding.ArRowArdtlListBinding;

import java.text.DecimalFormat;
import java.util.List;

public class ARPaymentDtlListAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.ARPaymentDtl> arPaymentDtls;
    TextView bill;

    public ARPaymentDtlListAdapter(Context context, List<AC_Class.ARPaymentDtl> arDtlList) {
        this.context = context;
        this.arPaymentDtls = arDtlList;
    }

    @Override
    public int getCount() {
        return this.arPaymentDtls.size();
    }

    @Override
    public Object getItem(int position) {
        return arPaymentDtls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArRowArdtlListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ar_row_ardtl_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (ArRowArdtlListBinding) convertView.getTag();
        }


        binding.setARDTL(arPaymentDtls.get(position));
        return binding.getRoot();
    }

    @BindingAdapter({"qty"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.##").format(amount));
    }
}
