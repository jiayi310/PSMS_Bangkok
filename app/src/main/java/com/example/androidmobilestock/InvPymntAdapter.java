package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.databinding.RowInvpaymentListBinding;

import java.util.List;

public class InvPymntAdapter extends BaseAdapter {

    Context context;
    List<AC_Class.Payment> payment_classlist;
    String default_curr;

    public InvPymntAdapter(Context context, List<AC_Class.Payment> payment_classlist, String default_curr) {
        this.context = context;
        this.payment_classlist = payment_classlist;
        this.default_curr = default_curr;
    }

    @Override
    public int getCount() {
        return this.payment_classlist.size();
    }

    @Override
    public Object getItem(int position) {
        return payment_classlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowInvpaymentListBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_invpayment_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowInvpaymentListBinding) convertView.getTag();
        }
        binding.setInvPayment(payment_classlist.get(position));
        binding.setDefaultCurr(default_curr);
        return binding.getRoot();
    }
}
