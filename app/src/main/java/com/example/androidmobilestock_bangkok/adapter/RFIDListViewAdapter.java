package com.example.androidmobilestock_bangkok.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.ListtagItemsBinding;

import java.util.List;

public class RFIDListViewAdapter extends BaseAdapter {
    Context context;
    private List<AC_Class.RFIDItemStatus> rfid_item_status;

    public RFIDListViewAdapter(Context contextFP, List<AC_Class.RFIDItemStatus> rfid_item_statusFP) {
        this.context = contextFP;
        this.rfid_item_status = rfid_item_statusFP;
    }

    @Override
    public int getCount() {
        return rfid_item_status.size();
    }

    @Override
    public Object getItem(int position) {
        return rfid_item_status.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListtagItemsBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.listtag_items, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (ListtagItemsBinding) convertView.getTag();
        }
        binding.setMyItemStatus(rfid_item_status.get(position));
        return binding.getRoot();
    }
}
