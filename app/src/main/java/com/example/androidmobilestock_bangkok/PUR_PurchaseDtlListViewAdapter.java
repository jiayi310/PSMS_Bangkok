package com.example.androidmobilestock_bangkok;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.databinding.PurRowPurchasedtllistBinding;

import java.text.DecimalFormat;
import java.util.List;

public class PUR_PurchaseDtlListViewAdapter extends BaseAdapter {

    Context context;
    List<AC_Class.PurchaseDetails> dtlList;
    TextView bill;

    public PUR_PurchaseDtlListViewAdapter(Context context, List<AC_Class.PurchaseDetails> dtlListFP) {
        this.context = context;
        this.dtlList = dtlListFP;
    }

    @Override
    public int getCount() {
        return this.dtlList.size();
    }

    @Override
    public Object getItem(int position) {
        return dtlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PurRowPurchasedtllistBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.pur_row_purchasedtllist, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
            bill = (TextView) convertView.findViewById(R.id.bill);
        }
        else
        {
            binding = (PurRowPurchasedtllistBinding) convertView.getTag();
        }
        if(dtlList.get(position).getRemarks() == null){
            binding.invdtllistRemarks.setVisibility(View.GONE);
        }else
        {
            binding.invdtllistRemarks.setVisibility(View.VISIBLE);
        }

        if(dtlList.get(position).getRemarks2() == null){
            binding.invdtllistRemarks2.setVisibility(View.GONE);
        }else
        {
            binding.invdtllistRemarks2.setVisibility(View.VISIBLE);
        }

        if(dtlList.get(position).getBatch_No() == null){
            binding.invdtllistBatchNo.setVisibility(View.GONE);
        }else {
            binding.invdtllistBatchNo.setVisibility(View.VISIBLE);
        }
        bill.setText(Integer.toString(position+1));
        binding.setDtl(dtlList.get(position));
        return binding.getRoot();
    }

    @BindingAdapter({"amount"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.###").format(amount));
    }
}
