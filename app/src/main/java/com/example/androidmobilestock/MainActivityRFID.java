package com.example.androidmobilestock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.androidmobilestock.activity.UHFMainActivity;

public class MainActivityRFID extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setTitle("RFID Scan");

        final ProgressBar pbar = (ProgressBar) findViewById(R.id.progressBar);
        Button writeButton = (Button) findViewById(R.id.writeButton);
        Button readButton = (Button) findViewById(R.id.readButton);

        final Handler handler = new Handler();

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbar.setVisibility(View.VISIBLE);
                Intent activity = new Intent(MainActivityRFID.this, UHFMainActivity.class);
                activity.putExtra("selectedButton","R");
                startActivity(activity);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbar.setVisibility(View.GONE);
                    }
                }, 400);
            }
        });

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbar.setVisibility(View.VISIBLE);
                Intent activity = new Intent(MainActivityRFID.this, UHFMainActivity.class);
                activity.putExtra("selectedButton","W");
                startActivity(activity);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbar.setVisibility(View.GONE);
                    }
                }, 400);
            }
        });
    }
}