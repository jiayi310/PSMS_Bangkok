package com.example.androidmobilestock;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.example.androidmobilestock.databinding.RowItemListBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

//public class ItemListViewAdapter extends BaseAdapter implements Filterable {
public class ItemListViewAdapter extends BaseAdapter {

    Context context;
    private List<AC_Class.Item> item_list;
    private List<AC_Class.Item> mdisplayitemlist;
    private String default_curr;
    ACDatabase db;
    int SellingPrice = 0;

    ItemListViewAdapter(Context context, List<AC_Class.Item> item_list, String default_curr) {
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
                    binding.textItemBalance.setText("Balance: "+ nf.format(balance));
                }
            }

            Cursor sale = db.getReg("48");
            if (sale.moveToFirst()) {
                SellingPrice = sale.getInt(0);
            }

            if(SellingPrice == 0){
                binding.text2Price.setVisibility(View.GONE);
                binding.defaultcurr.setVisibility(View.GONE);
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

//    @Override
//    @SuppressWarnings("unchecked")
//    public Filter getFilter() {
//        Filter filter = new Filter() {
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                if (results.count == 0)
//                {
//                    notifyDataSetInvalidated();
//                }
//                else {
//                    mdisplayitemlist = (ArrayList<AC_Class.Item>) results.values;
//                    notifyDataSetChanged();
//                }
//            }
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults filterResults = new FilterResults();
//                List<AC_Class.Item> FilteredArrList = new ArrayList<>();
//                if (item_list == null)
//                {
//                    item_list = new ArrayList<AC_Class.Item>(mdisplayitemlist);
//                }
//                if(constraint == null || constraint.length() == 0)
//                {
//                    filterResults.count = item_list.size();
//                    filterResults.values = item_list;
//                }
//                else {
//                    constraint = constraint.toString().toLowerCase();
//                    for (int i = 0; i < item_list.size(); i++) {
//                        String data = item_list.get(i).getItemCode();
//                        if (data.toLowerCase().startsWith(constraint.toString())) {
//                            FilteredArrList.add(new AC_Class.Item(item_list.get(i).getItemCode(),
//                                    item_list.get(i).getDescription(), item_list.get(i).getDesc2(),
//                                    item_list.get(i).getItemGroup(), item_list.get(i).getItemType(),
//                                    item_list.get(i).getTaxType(), item_list.get(i).getPurchaseTaxType(),
//                                    item_list.get(i).getBaseUOM(), item_list.get(i).getPrice(),
//                                    item_list.get(i).getPrice2(), item_list.get(i).getBarCode()));
//                        }
//                    }
//                    filterResults.count = FilteredArrList.size();
//                    filterResults.values = FilteredArrList;
//                }
//                return filterResults;
//            }
//        };
//        return filter;
//    }
}
