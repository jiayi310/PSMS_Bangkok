package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.example.androidmobilestock.databinding.ActivityReceiptViewBinding;

public class ReceiptView extends AppCompatActivity {
    String docNo;
    ACDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        db = new ACDatabase(this);
        docNo = getIntent().getStringExtra("docNo");

        ActivityReceiptViewBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_receipt_view);

        Bitmap bitmap = db.getARImage(docNo);
        if (bitmap != null) {
            binding.noImageTxt.setVisibility(View.INVISIBLE);
            binding.imageView3.setImageBitmap(bitmap);
        } else {
            binding.imageView3.setVisibility(View.INVISIBLE);
        }
        binding.setHandler(new MyClickHandler(ReceiptView.this));

    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void closeOnClick(View view) {
            onBackPressed();
        }
    }

}