package com.example.androidmobilestock.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;

import java.util.List;
import java.util.Set;

public class StockAssemblyListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] docNo;
    private final String[] date;
    private final String[] remarks;
    private final Integer[] uploaded;

    private List<AC_Class.StockAssemblyMenu> sa_menu_list;
    private Set<AC_Class.StockAssemblyMenu> selectedItems;

    public StockAssemblyListAdapter(Activity context, String[] docNo, String[] date, String[] remarks, Integer[] uploaded) {
        super(context, R.layout.row_stockassemblydocument, docNo);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.docNo = docNo;
        this.date = date;
        this.remarks = remarks;
        this.uploaded = uploaded;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.row_stockassemblydocument, null,true);

        TextView docText = (TextView) rowView.findViewById(R.id.docNo);
        TextView dateText = (TextView) rowView.findViewById(R.id.dateTime);
        TextView rText = (TextView) rowView.findViewById(R.id.salesRemarks);
        ImageView tick = (ImageView) rowView.findViewById(R.id.iv_TickST);

        docText.setText(docNo[position]);
        dateText.setText(date[position]);
        if(remarks[position]==null || remarks[position].equals("")) {
            rText.setVisibility(View.GONE);
        }else{
            rText.setText("Remark: " + remarks[position]);
        }
        if(uploaded[position] == 0)
        {
            tick.setVisibility(View.GONE);
        }
        else{
            tick.setVisibility(View.VISIBLE);
        }

        return rowView;

    };
}
