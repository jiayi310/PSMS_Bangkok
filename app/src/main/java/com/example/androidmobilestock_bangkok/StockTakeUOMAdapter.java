package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.RowUomListBinding;

import java.util.List;

public class StockTakeUOMAdapter extends BaseAdapter {
    Context context;
    private List<AC_Class.StockTakeDetails> item;
    Boolean isBatchNoEnabled = true;
    ACDatabase db;

    public StockTakeUOMAdapter(Context context, List<AC_Class.StockTakeDetails> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowUomListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_uom_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowUomListBinding) convertView.getTag();
        }
        binding.setItem(item.get(position));

        binding.edtTransferQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(binding.edtTransferQty.getText().toString().length()>0) {
                    Double qty = Double.valueOf(binding.edtTransferQty.getText().toString());
                    item.get(position).setQuantity(qty);
                    binding.checkBox.setChecked(true);
                }
            }
        });

        binding.btnInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.get(position).setQuantity(item.get(position).getQuantity() + 1);

                binding.edtTransferQty.setText(String.format("%.0f", item.get(position).getQuantity()));

                binding.checkBox.setChecked(true);
            }
        });

        binding.btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.get(position).setQuantity(item.get(position).getQuantity() <= 0 ? 0 :item.get(position).getQuantity() - 1);
                binding.edtTransferQty.setText(String.format("%.0f", item.get(position).getQuantity()));
                binding.checkBox.setChecked(true);
            }
        });

        db = new ACDatabase(context);

        Cursor cursor = db.getReg("38");
        if(cursor.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor.getString(0));
        }

        if(isBatchNoEnabled) {
            binding.batchno.setVisibility(View.VISIBLE);
        }else{
            binding.batchno.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }
}
