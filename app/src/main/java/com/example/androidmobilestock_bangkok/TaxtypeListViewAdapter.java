package com.example.androidmobilestock_bangkok;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.androidmobilestock_bangkok.databinding.RowTaxtypeListBinding;
import java.util.List;

public class TaxtypeListViewAdapter extends BaseAdapter {

    Context context;
    List<AC_Class.TaxType> taxtype_list;

    public TaxtypeListViewAdapter(Context context, List<AC_Class.TaxType> taxtype_list) {
        this.context = context;
        this.taxtype_list = taxtype_list;
    }

    @Override
    public int getCount() {
        return taxtype_list.size();
    }

    @Override
    public Object getItem(int position) {
        return taxtype_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowTaxtypeListBinding binding;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_taxtype_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        }
        else
        {
            binding = (RowTaxtypeListBinding) convertView.getTag();
        }
        if (position %2 == 1)
        {
            convertView.setBackgroundColor(Color.parseColor("#f0f8ff"));
        }
        else
        {
            convertView.setBackgroundColor(Color.parseColor("#ffe4e1"));
        }
        binding.setTaxtype(taxtype_list.get(position));
        return binding.getRoot();
    }
}
