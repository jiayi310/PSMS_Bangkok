package com.example.androidmobilestock_bangkok.Settings;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.RowUldlListBinding;

public class ULDLListViewAdapter extends BaseAdapter {
    private Context context;
    private AC_Class.UploadDownload[] uldlList;

    public ULDLListViewAdapter(Context context, AC_Class.UploadDownload[] uldlList) {
        this.context = context;
        this.uldlList = uldlList;
    }


    @Override
    public int getCount() {
        return uldlList.length;
    }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public AC_Class.UploadDownload getItem(int position) {
        return uldlList[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowUldlListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_uldl_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowUldlListBinding) convertView.getTag();
        }
        binding.setUldl(uldlList[position]);
//        binding.setSelected(false);

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (binding.getSelected()) {
//                    binding.setSelected(false);
//                    v.setBackgroundColor(Color.parseColor("#f0f8ff"));
//                } else {
//                    binding.setSelected(true);
//                    v.setBackgroundColor(Color.parseColor("#ffe4e1"));
//                }
//            }
//        });
        return binding.getRoot();
    }
}
