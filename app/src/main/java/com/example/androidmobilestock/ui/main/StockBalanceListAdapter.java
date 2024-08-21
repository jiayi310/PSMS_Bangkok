package com.example.androidmobilestock.ui.main;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.RowStockbalancebylocationListBatchNoBinding;
import com.example.androidmobilestock.databinding.RowStockbalancebylocationListBinding;

import java.util.List;

public class StockBalanceListAdapter extends BaseAdapter {
    Context context;
    List<AC_Class.StockBalance> stockBalanceList;
    Boolean isBatchNoEnabled = true;

    public StockBalanceListAdapter(Context context, List<AC_Class.StockBalance> stockBalanceList) {
        this.context = context;
        this.stockBalanceList = stockBalanceList;
    }

    @Override
    public int getCount() {
        return stockBalanceList.size();
    }

    @Override
    public Object getItem(int position) {
        return stockBalanceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowStockbalancebylocationListBinding binding;
        RowStockbalancebylocationListBatchNoBinding binding2;

        ACDatabase db = new ACDatabase(context);

        Cursor cursor = db.getReg("38");
        if(cursor.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor.getString(0));
        }

        float stockCount = 0.00f;
        if (convertView == null) {
            if(!isBatchNoEnabled) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.row_stockbalancebylocation_list, null);
                binding = DataBindingUtil.bind(convertView);
                convertView.setTag(binding);

                if(stockBalanceList.get(position).getItemCode()!=null) {
                    stockCount = db.getSalesDtlQty(stockBalanceList.get(position));
                }

                //stockCount = db.getAllStockCount(stockBalanceList.get(position));
                binding.setStockcount(stockCount);
                binding.setStockbalance(stockBalanceList.get(position));
                return binding.getRoot();
            }else{
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.row_stockbalancebylocation_list_batch_no, null);
                binding2 = DataBindingUtil.bind(convertView);
                convertView.setTag(binding2);
                if(stockBalanceList.get(position).getItemCode()!=null) {
                    stockCount = db.getSalesDtlQtyBatch(stockBalanceList.get(position));
                }

                //stockCount = db.getAllStockCount(stockBalanceList.get(position));
                binding2.setStockcount(stockCount);
                binding2.setStockbalance(stockBalanceList.get(position));
                return binding2.getRoot();
            }
        } else {
            if(!isBatchNoEnabled) {
                binding = (RowStockbalancebylocationListBinding) convertView.getTag();
                binding.setStockbalance(stockBalanceList.get(position));
                return binding.getRoot();
            }else{
                binding2 = (RowStockbalancebylocationListBatchNoBinding) convertView.getTag();
                binding2.setStockbalance(stockBalanceList.get(position));
                return binding2.getRoot();
            }
        }

    }
}
