package com.example.androidmobilestock;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.androidmobilestock.databinding.PlRowPldtlListBinding;

import java.text.DecimalFormat;
import java.util.List;

public class PL_PLDtlListViewAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.DODtl> DODtlList;
    TextView bill;
    ACDatabase db;

    public PL_PLDtlListViewAdapter(Context context, List<AC_Class.DODtl> doDtlList) {
        this.context = context;
        this.DODtlList = doDtlList;
    }

    @Override
    public int getCount() {
        return this.DODtlList.size();
    }

    @Override
    public Object getItem(int position) {
        return DODtlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlRowPldtlListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pl_row_pldtl_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
            bill = convertView.findViewById(R.id.bill);
        } else {
            binding = (PlRowPldtlListBinding) convertView.getTag();
        }

        db= new ACDatabase(context);

        if (DODtlList.get(position).getBatchNo() != null && !DODtlList.get(position).getBatchNo().equals("") && !DODtlList.get(position).getBatchNo().equals("null")) {
                binding.tvBatchno.setVisibility(View.VISIBLE);
                binding.tvBatchnoLbl.setVisibility(View.VISIBLE);
        }

        if (DODtlList.get(position).getRemarks() != null && !DODtlList.get(position).getRemarks().equals("") && !DODtlList.get(position).getRemarks().equals("null")) {
                binding.tvRemarks.setVisibility(View.VISIBLE);
                binding.tvRemarksTxt.setVisibility(View.VISIBLE);
        }

        if (db.getItemImage(DODtlList.get(position).getItemCode()) != null) {
            Glide.with(context).load(db.getItemImage(DODtlList.get(position).getItemCode())).fitCenter().into(binding.ImageItemCart);

        } else {
            binding.ImageItemCart.setImageResource(R.drawable.photo_empty);
        }

        bill.setText(Integer.toString(position + 1));
        binding.setPLDTL(DODtlList.get(position));
        return binding.getRoot();
    }

    @BindingAdapter({"qty"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.###").format(amount));
    }
}
