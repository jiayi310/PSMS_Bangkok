package com.example.androidmobilestock;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.databinding.RowItemListBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

//public class ItemListViewAdapter extends BaseAdapter implements Filterable {
public class ItemListViewSalesAdapter extends BaseAdapter {

    Context context;
    private List<AC_Class.Item> item_list;
    private List<AC_Class.Item> mdisplayitemlist;
    private String default_curr;
    ACDatabase db;

    ItemListViewSalesAdapter(Context context, List<AC_Class.Item> item_list, String default_curr) {
        this.context = context;
        this.item_list = item_list;
        this.mdisplayitemlist = item_list;
        this.default_curr = default_curr;
    }

    @Override
    public int getCount() {
        return mdisplayitemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mdisplayitemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RowItemListBinding binding;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_item_list,
                    null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);

        } else {
            binding = (RowItemListBinding) convertView.getTag();
        }

        try {
            binding.setItem(item_list.get(position));

            db= new ACDatabase(context);
            //binding.imageBitmap.setImageBitmap(db.getItemImage(item_list.get(position).getItemCode()));

            binding.setDefaultCurr(default_curr);

            if (position % 2 == 1) {
                convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#f5f5f5"));
            }

            NumberFormat nf = new DecimalFormat("##.###");

            Cursor data = db.getStockBalance(item_list.get(position).getItemCode(), item_list.get(position).getUOM());
            if (data.getCount() > 0) {
                while (data.moveToNext()) {
                    Float balance = data.getFloat(data.getColumnIndex("SUM(BalQty)"));
                    binding.textItemBalance.setText("Balance: " + nf.format(balance));
                }
            }


            if(item_list.get(position).getDesc2()!=null && !item_list.get(position).getDesc2().equals("")){
                binding.textItemName2.setVisibility(View.VISIBLE);
            }else{
                binding.textItemName2.setVisibility(View.GONE);
            }

            if(item_list.get(position).getDescription()!=null && !item_list.get(position).getDescription().equals("")){
                binding.textItemName.setVisibility(View.VISIBLE);
            }else{
                binding.textItemName.setVisibility(View.GONE);
            }

        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
        return binding.getRoot();
    }

}
