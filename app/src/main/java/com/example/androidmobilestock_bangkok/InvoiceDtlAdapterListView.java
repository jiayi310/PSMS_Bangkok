package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.RowInvdtlListBinding;

import java.util.List;

public class InvoiceDtlAdapterListView extends BaseAdapter {

    Context context;
    List<AC_Class.InvoiceDetails> invdtl_classlist;
    ACDatabase db;

    public InvoiceDtlAdapterListView(Context context, List<AC_Class.InvoiceDetails> invdtl_classlist) {
        this.context = context;
        this.invdtl_classlist = invdtl_classlist;
    }

    @Override
    public int getCount() {
        return this.invdtl_classlist.size();
    }

    @Override
    public Object getItem(int position) {
        return invdtl_classlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowInvdtlListBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_invdtl_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
            db= new ACDatabase(context);
            if(db.getItemImage(invdtl_classlist.get(position).getItemCode())!=null) {
                binding.ImageItemCart.setImageBitmap(db.getItemImage(invdtl_classlist.get(position).getItemCode()));
            }else{
                binding.ImageItemCart.setImageResource(R.drawable.photo_empty);
            }
        }
        else
        {
            binding = (RowInvdtlListBinding) convertView.getTag();
        }



        binding.setInvDetail(invdtl_classlist.get(position));
        return binding.getRoot();
    }
}
