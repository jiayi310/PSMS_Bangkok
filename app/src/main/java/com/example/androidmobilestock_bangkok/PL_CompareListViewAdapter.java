package com.example.androidmobilestock_bangkok;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.databinding.PlRowCompareListBinding;

import java.text.DecimalFormat;
import java.util.List;

public class PL_CompareListViewAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.SODODtl> SODODtlList;
    Boolean BatchComparison = true;
    ACDatabase db;

    public PL_CompareListViewAdapter(Context context, List<AC_Class.SODODtl> soDoDtlList) {
        this.context = context;
        this.SODODtlList = soDoDtlList;
    }

    @Override
    public int getCount() {
        return this.SODODtlList.size();
    }

    @Override
    public Object getItem(int position) {
        return SODODtlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlRowCompareListBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.pl_row_compare_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (PlRowCompareListBinding) convertView.getTag();
        }

        db = new ACDatabase(context);
        Cursor cursor2 = db.getReg("44");
        if(cursor2.moveToFirst()){
            BatchComparison = Boolean.valueOf(cursor2.getString(0));
        }

        if(BatchComparison) {
            if(SODODtlList.get(position).getDOBatch()!=null || SODODtlList.get(position).getSOBatch()!=null) {
                binding.tvBatch.setVisibility(View.VISIBLE);
            }
        }
        binding.setSODODTL(SODODtlList.get(position));
        return binding.getRoot();
    }

    @BindingAdapter({"qty"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.###").format(amount));
    }
}
