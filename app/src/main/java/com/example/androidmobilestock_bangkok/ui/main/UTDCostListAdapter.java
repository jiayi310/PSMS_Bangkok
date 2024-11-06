package com.example.androidmobilestock_bangkok.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.RowHistorypriceListBinding;
import com.example.androidmobilestock_bangkok.databinding.RowStockbalancebylocationListBatchNoBinding;
import com.example.androidmobilestock_bangkok.databinding.RowStockbalancebylocationListBinding;
import com.example.androidmobilestock_bangkok.databinding.RowUtdcostListBinding;

import java.util.List;

public class UTDCostListAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.UTDCost> UTDCostList;

    public UTDCostListAdapter(Context context, List<AC_Class.UTDCost> UTDCostList) {
        this.context = context;
        this.UTDCostList = UTDCostList;
    }

    @Override
    public int getCount() {
        return UTDCostList.size();
    }

    @Override
    public Object getItem(int position) {
        return UTDCostList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       RowUtdcostListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.row_utdcost_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowUtdcostListBinding) convertView.getTag();
        }

        binding.setUtdcost(UTDCostList.get(position));
        return binding.getRoot();




    }
}
