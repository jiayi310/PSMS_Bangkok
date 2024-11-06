package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.RowTransactionListBinding;

public class TransactionListViewAdapter extends BaseAdapter {
    Context context;
    private String[] title_list;
    private int[] icon_list;
    private String[] subtitle_list;
    ACDatabase db;
//    private SettingsClass[] settings_list;

    public TransactionListViewAdapter(Context context, String[] title_list, int[] icon_list, String[] subtitle_list) {
        this.context = context;
        this.icon_list = icon_list;
        this.title_list = title_list;
        this.subtitle_list = subtitle_list;
    }

//    public SettingsListViewAdapter(Context context, SettingsClass[] settings_list) {
//        this.settings_list = settings_list;
//    }

    @Override
    public int getCount() {
        return title_list.length;
    }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public String getItem(int position) {
        return title_list[position];
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (settings_list[position].isContainsSub()) {return 1;}
//        else {return 0;}
//    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowTransactionListBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_transaction_list, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (RowTransactionListBinding) convertView.getTag();
        }
        binding.settingsIconView.setImageDrawable(ContextCompat.getDrawable(context, icon_list[position]));
        binding.settingsTitleView.setText(title_list[position]);
        if (subtitle_list.length == title_list.length) {
            binding.settingsSubtitleView.setText(subtitle_list[position]);
        }
        if(title_list[position].equals("Default Currency"))
        {
            binding.settingsSubtitleView.setText(subtitle_list[2]);
        }
        if(title_list[position].equals("Default Debtor"))
        {
            binding.settingsSubtitleView.setText(subtitle_list[3]);
        }
        if(title_list[position].equals("Default Creditor"))
        {
            binding.settingsSubtitleView.setText(subtitle_list[4]);
        }
        if(title_list[position].equals("Default Agent"))
        {
            binding.settingsSubtitleView.setText(subtitle_list[5]);
        }
        if(title_list[position].equals("Default Purchase Agent"))
        {
            binding.settingsSubtitleView.setText(subtitle_list[6]);
        }
        if(title_list[position].equals("Default Location"))
        {
            binding.settingsSubtitleView.setText(subtitle_list[7]);
        }
        if(title_list[position].equals("Default Printer"))
        {
            binding.settingsSubtitleView.setText(subtitle_list[8]);
        }
        return binding.getRoot();
    }
}
