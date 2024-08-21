package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.databinding.RowMultipaymentBinding;

import java.util.List;

public class MPaymentListAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.Payment> payment_list;

    public MPaymentListAdapter(Context context, List<AC_Class.Payment> payment_list) {
        this.context = context;
        this.payment_list = payment_list;
    }

    @Override
    public int getCount() {
        return payment_list.size();
    }

    @Override
    public Object getItem(int position) {
        return payment_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowMultipaymentBinding binding;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_multipayment, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowMultipaymentBinding) convertView.getTag();
        }
        binding.setPayment(payment_list.get(position));
        return binding.getRoot();
    }
}
