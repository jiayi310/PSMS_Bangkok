package com.example.androidmobilestock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ShowCart extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton cart_button;
    ImageView empty_imageview;
    TextView no_data;

    ACDatabase db;
    ArrayList<String> id, name, price;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart);
    }
}