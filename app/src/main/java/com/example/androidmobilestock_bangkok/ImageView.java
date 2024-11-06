package com.example.androidmobilestock_bangkok;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidmobilestock_bangkok.databinding.ActivityImageViewBinding;

public class ImageView extends AppCompatActivity {
    String itemCode;
    ACDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        itemCode = getIntent().getStringExtra("itemCode");
        db = new ACDatabase(this);

        ActivityImageViewBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_image_view);
        binding.imageView3.setContentDescription(itemCode);
        Bitmap bitmap = db.getItemImage(itemCode);
        if (bitmap != null) {
            binding.noImageTxt.setVisibility(View.INVISIBLE);
            binding.imageView3.setImageBitmap(bitmap);
        } else {
            binding.imageView3.setVisibility(View.INVISIBLE);
        }
        binding.setHandler(new MyClickHandler(ImageView.this));
        //Exit on touch
        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
