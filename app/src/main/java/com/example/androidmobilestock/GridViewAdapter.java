package com.example.androidmobilestock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context ctx;
    private ArrayList<ImageModel> imageModelArrayList;
    private ImageView img_gridview;
    private TextView txt_pagename;

    public GridViewAdapter(Context ctx, ArrayList<ImageModel> imageModelArrayList) {
        this.ctx = ctx;
        this.imageModelArrayList = imageModelArrayList;
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.content_main_gridview_design, parent, false);
        img_gridview = (ImageView) itemView.findViewById(R.id.img_gridview);
        txt_pagename = (TextView) itemView.findViewById(R.id.txt_page_name);
        img_gridview.setImageResource(imageModelArrayList.get(position).getImage_drawable());
        txt_pagename.setText(imageModelArrayList.get(position).getName());
        return itemView;
    }
}
