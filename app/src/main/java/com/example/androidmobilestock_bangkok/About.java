
package com.example.androidmobilestock_bangkok;

import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import com.example.androidmobilestock_bangkok.databinding.ActivityAboutBinding;

public class About extends AppCompatActivity {

    ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        ACDatabase db = new ACDatabase(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.menu_about));
        actionBar.setDisplayHomeAsUpEnabled(true);

        String uniqueID = "";

        Cursor data = db.getReg("8");
        if(data.moveToFirst()){
            uniqueID = data.getString(0);
        }

        String currVer = "";

        Cursor data2 = db.getReg("1");
        if(data2.moveToFirst()){
            currVer = data2.getString(0);
        }

        binding.txtTitle.setText("Presoft Mobile Stock\n\nBangkok_Customization");
        binding.txtabout.setText("©2019 Presoft (M) Sdn. Bhd. All Rights Reserved. The usage of this app indicates that you agree to be bound by our Terms and Conditions." + "\n\n" + "Support Line: +603 8068 2556" + "\n" +
                "Support Email: support@presoft.com.my");
        binding.txtUUID.setText(uniqueID);
        binding.txtVersion.setText("Version " + currVer);

        binding.txtTerm.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBackPressed() {
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


