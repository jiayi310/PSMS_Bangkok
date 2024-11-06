package com.example.androidmobilestock_bangkok;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.databinding.RowTransferdtlListBinding;

import java.text.DecimalFormat;
import java.util.List;

public class TransferDtlAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.TransferDtl> transferDtl;
    TextView bill;

    public TransferDtlAdapter(Context context, List<AC_Class.TransferDtl> transferDtlFP) {
        this.context = context;
        this.transferDtl = transferDtlFP;
    }

    @Override
    public int getCount() {
        return this.transferDtl.size();
    }

    @Override
    public Object getItem(int position) {
        return transferDtl.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowTransferdtlListBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_transferdtl_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
            bill = convertView.findViewById(R.id.bill);
        }
        else
        {
            binding = (RowTransferdtlListBinding) convertView.getTag();
        }

        if (transferDtl.get(position).getBatchNo() != null && !transferDtl.get(position).getBatchNo().equals("")) {
            binding.tvBatchno.setVisibility(View.VISIBLE);
            binding.tvBatchnoLbl.setVisibility(View.VISIBLE);
        }

        bill.setText(Integer.toString(position+1));
        binding.setTrDetail(transferDtl.get(position));
        return binding.getRoot();
    }

    @BindingAdapter({"qty"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.###").format(amount));
    }
}
