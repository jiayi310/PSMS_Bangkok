package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.RowPlitemListBinding;
import com.example.androidmobilestock_bangkok.databinding.RowPplitemListBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

//public class ItemListViewAdapter extends BaseAdapter implements Filterable {
public class PPLItemListViewAdapter extends BaseAdapter {

    Context context;
    private List<AC_Class.Item> item_list;
    private List<AC_Class.Item> mdisplayitemlist;
    ACDatabase db;

    PPLItemListViewAdapter(Context context, List<AC_Class.Item> item_list) {
        this.context = context;
        this.item_list = item_list;
        this.mdisplayitemlist = item_list;
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
        RowPplitemListBinding binding;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_pplitem_list,
                    null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);

        } else {
            binding = (RowPplitemListBinding) convertView.getTag();
        }

        try {
            binding.setItem(item_list.get(position));

            db= new ACDatabase(context);
            //binding.imageBitmap.setImageBitmap(db.getItemImage(item_list.get(position).getItemCode()));
            binding.bill.setText(Integer.toString(position + 1));
            //binding.setDefaultCurr(default_curr);

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

//            if (db.getItemImage(item_list.get(position).getItemCode()) != null) {
//                Glide.with(context).load(db.getItemImage(item_list.get(position).getItemCode())).fitCenter().into(binding.ImageItemCart);
//
//            } else {
//                binding.ImageItemCart.setImageResource(R.drawable.photo_empty);
//            }
//
//            binding.ImageItemCart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, ImageView.class);
//                    intent.putExtra("itemCode", item_list.get(position).getItemCode());
//                    ((PPL_PLDtlList) context).startActivityForResult(intent, 99);
//                }
//            });


//            if(item_list.get(position).getDesc2()!=null && !item_list.get(position).equals("")){
//                binding.textItemName2.setVisibility(View.VISIBLE);
//            }else{
//                binding.textItemName2.setVisibility(View.GONE);
//            }

        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
        return binding.getRoot();
    }

}
