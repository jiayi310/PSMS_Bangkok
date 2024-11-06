package com.example.androidmobilestock_bangkok.Settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.R;

public class BatchNoConfiguration extends AppCompatActivity {

    EditText batch_no;
    Button mm, yy, yyyy, confirm, clear;
    ACDatabase db;
    String batch_format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_no_configuration);
        db = new ACDatabase(getBaseContext());

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Batch No Configuration");
        } catch (Exception e)
        { Log.i("custDebug", e.getMessage()); }

        batch_no = (EditText) findViewById(R.id.batchno);
        mm = (Button) findViewById(R.id.mm);
        yy = (Button) findViewById(R.id.yy);
        yyyy = (Button) findViewById(R.id.yyyy);
        confirm = (Button) findViewById(R.id.confirm);
        clear = (Button) findViewById(R.id.clear);

        Cursor cursor1 = db.getReg("37");
        if(cursor1.moveToFirst()){
            batch_format = cursor1.getString(0);
        }

        if(batch_format != "0" || batch_format!=null) {
            batch_no.append(batch_format);
        }

        mm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batch_no.append("{MM}");
            }
        });

        yy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batch_no.append("{YY}");
            }
        });

        yyyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batch_no.append("{YYYY}");
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batch_no.setText("");
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batch_format = batch_no.getText().toString();
                db.updateREG("37", batch_format);
                onBackPressed();

//                String mm = "{mm}";
//                String yy = "{yy}";
//                String yyyy = "{yyyy}";
//
//                if(batch_format.contains(mm)){
//
//                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(1, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}