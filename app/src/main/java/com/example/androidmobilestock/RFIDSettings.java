package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class RFIDSettings extends AppCompatActivity {

    public Switch sw;
    public TextView txtPermName;
    public String status,pn;
    ACDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r_f_i_d_settings);

        db = new ACDatabase(this);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Permission");
        } catch (Exception e)
        { Log.i("custDebug", e.getMessage()); }

        sw = (Switch) findViewById(R.id.switch1);
        txtPermName = (TextView) findViewById(R.id.txtPermName);

        Cursor data = db.getPermission();
        data.moveToFirst();

        pn = data.getString(data.getColumnIndex("permissionName"));
        status = getPStatus(pn);

        txtPermName.setText(pn);
        sw.setText(status);

        if(status.equals("Denied"))
        {
            sw.setChecked(false);
        }
        else{
            sw.setChecked(true);
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.wtf("isChecked", String.valueOf(isChecked));

                if(buttonView.isPressed())
                {
                  if(status.equals("Granted"))
                  {
                      db.editPermissionDeny(pn);
                      status = getPStatus(pn);
                      Log.wtf("status",status);
                      if(status.equals("Denied")){
                          sw.setChecked(false);
                          sw.setText(status);
                      }
                  }
                  else if(status.equals("Denied"))
                  {
                      db.editPermissionGranted(pn);
                      status = getPStatus(pn);
                      Log.wtf("status",status);
                      if(status.equals("Granted")){
                          sw.setChecked(true);
                          sw.setText(status);
                      }
                  }
                }
            }
        });

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

    public String getPStatus(String name)
    {
        db = new ACDatabase(this);
        Cursor data = db.getPermissionStatus(name);
        data.moveToFirst();

        return  data.getString(data.getColumnIndex("status"));
    }
}