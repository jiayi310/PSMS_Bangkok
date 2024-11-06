package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class AnalyticsAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<AnalyticModel> modelArrayList;

    public  AnalyticsAdapter (Context context, ArrayList<AnalyticModel> modelArrayList){
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<AnalyticModel> getModelArrayList() {
        return modelArrayList;
    }

    public void setModelArrayList(ArrayList<AnalyticModel> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.analytic_card,container,false);

        TextView title = view.findViewById(R.id.title);
        TextView title2 = view.findViewById(R.id.title2);
        TextView description = view.findViewById(R.id.description);
        TextView description2 = view.findViewById(R.id.description2);
        TextView amount = view.findViewById(R.id.amount);
        TextView amount2 = view.findViewById(R.id.amount2);

        AnalyticModel model = modelArrayList.get(position);
        String title_ = model.getTitle();
        String title2_ = model.getTitle2();
        String description_ = model.getDescription();
        String description2_ = model.getDescription2();
        String amount_ = model.getAmount();
        String amount2_ = model.getAmount2();

        title.setText(title_);
        title2.setText(title2_);
        description.setText(description_);
        description2.setText(description2_);
        amount.setText(amount_);
        amount2.setText(amount2_);

        container.addView(view,position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
