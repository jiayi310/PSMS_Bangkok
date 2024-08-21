package com.example.androidmobilestock.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import com.example.androidmobilestock.R;

public class StockTakeListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] docNo;
    private final String[] date;
    private final String[] salesAgent;
    private final String[] remarks;
    private final Integer[] uploaded;

    public StockTakeListAdapter(Activity context, String[] docNo, String[] date, String[] salesAgent, String[] remarks, Integer[] uploaded) {
        super(context, R.layout.row_stockcountdocument, docNo);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.docNo = docNo;
        this.date = date;
        this.salesAgent = salesAgent;
        this.remarks = remarks;
        this.uploaded = uploaded;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.row_stockcountdocument, null,true);

        TextView docText = (TextView) rowView.findViewById(R.id.docNo);
        TextView dateText = (TextView) rowView.findViewById(R.id.dateTime);
        TextView saText = (TextView) rowView.findViewById(R.id.salesAgent);
        TextView rText = (TextView) rowView.findViewById(R.id.salesRemarks);
        ImageView tick = (ImageView) rowView.findViewById(R.id.iv_TickST);

        docText.setText(docNo[position]);
        dateText.setText(date[position]);
        saText.setText("Agent: "+salesAgent[position]);
        if(remarks[position].equals("") || remarks[position] == null){
            rText.setVisibility(View.GONE);
        }else{
            rText.setText("Remark: "+ remarks[position]);
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
