package com.example.androidmobilestock;

import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import com.example.androidmobilestock.databinding.ActivityDebtorDetailsBinding;

public class DebtorDetails extends AppCompatActivity {
    AC_Class.Debtor myDebtor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        if (myIntent != null) {
            myDebtor = myIntent.getParcelableExtra("DebtorsKey");
            ActivityDebtorDetailsBinding myBinding = DataBindingUtil.setContentView(this,
                    R.layout.activity_debtor_details);
            myBinding.setDebtor(myDebtor);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Customer Details");
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDebtor.getPhone() != null) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + myDebtor.getPhone()));
                    startActivity(i);
                }else{
                    Snackbar.make(view, "Missing phone number.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (myDebtor.getPhone() != null) {
                    String url = "https://api.whatsapp.com/send?phone=" + myDebtor.getPhone();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    try {
                        startActivity(i);
                    }catch (android.content.ActivityNotFoundException ex) {
                        Snackbar.make(view, "There is no WhatsApp installed.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }else{
                    Snackbar.make(view, "Missing phone number.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton fab3 = findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDebtor.getEmailAddress() != null) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{ myDebtor.getEmailAddress()});
                    i.setData(Uri.parse("mailto:"));
                    i.setType("text/plain");
                    //i.setType("message/rfc822");
                    try {
                        startActivity(Intent.createChooser(i, "Choose an Email client :"));
                    }
                    catch (android.content.ActivityNotFoundException ex) {
                        Snackbar.make(view, "There is no email client installed.", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }else{
                    Snackbar.make(view, "Missing email address.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton fab4 = findViewById(R.id.fab4);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDebtor.getADD1() != null) {
                    String myUri = "geo:?q=" + myDebtor.getADD1();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(myUri));
                    if (i.resolveActivity(getPackageManager()) != null) {
                        startActivity(i);
                    }else{
                        Snackbar.make(view, "Missing map app.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }else{
                    Snackbar.make(view, "Missing address.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }


}
