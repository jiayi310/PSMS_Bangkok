package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidmobilestock.R;

public class Discount_List extends AppCompatActivity {

    ACDatabase db;
    String Item;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView textType;
    EditText discountAmt;
    Button confirmButton;
    Button dis1,dis2,dis3,dis5,dis8,dis10,dis15,dis20,dis25,dis30;
    String isPercentage,isChecked;
    TextView textdicount;
    LinearLayout buttonlist;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Discount");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        Intent pintent = getIntent();
        Item = pintent.getStringExtra("ItemCode");

        TextView itemcode = (TextView) findViewById(R.id.itemcode);
        itemcode.setText(Item);

        radioGroup = findViewById(R.id.radioGroup);
        textType = findViewById(R.id.textType);
        discountAmt = findViewById(R.id.discountEdit);
        confirmButton = findViewById(R.id.discountConfirm);
        dis1 = findViewById(R.id.button1);
        dis2 = findViewById(R.id.button2);
        dis3 = findViewById(R.id.button3);
        dis5 = findViewById(R.id.button5);
        dis8 = findViewById(R.id.button8);
        dis10 = findViewById(R.id.button10);
        dis15 = findViewById(R.id.button15);
        dis20 = findViewById(R.id.button20);
        dis25 = findViewById(R.id.button25);
        dis30 = findViewById(R.id.button30);
        textdicount = findViewById(R.id.textdiscount);
        buttonlist = findViewById(R.id.buttonlist);
        checkBox = findViewById(R.id.CheckBox);
        checkBox.setVisibility(View.INVISIBLE);

        isPercentage="True";
        isChecked="False";

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(discountAmt.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(),"Please type an amount",Toast.LENGTH_SHORT).show();
                }else{
                    Double dis = Double.valueOf(discountAmt.getText().toString());
                    Intent myintent = new Intent();
                    myintent.putExtra("Discount", dis);
                    myintent.putExtra("IsPercentage", isPercentage);
                    myintent.putExtra("IsChecked", isChecked);
                    setResult(8, myintent);
                    finish();
                }

            }
        });

        dis1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(1.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(2.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(3.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(5.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(8.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(10.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(15.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(20.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(25.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });

        dis30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double dis = Double.valueOf(30.0);

                Intent myintent = new Intent();
                myintent.putExtra("Discount", dis);
                myintent.putExtra("IsPercentage", isPercentage);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();
            }
        });


    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        String text;
        text = radioButton.getText().toString();

        if(text.equals("by Percentage")){
            textType.setText("Percentage: ");
            isPercentage ="True";
            textdicount.setVisibility(View.VISIBLE);
            buttonlist.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.INVISIBLE);
        }else{
            textType.setText("Amount: ");
            isPercentage ="False";
            textdicount.setVisibility(View.INVISIBLE);
            buttonlist.setVisibility(View.INVISIBLE);
            checkBox.setVisibility(View.VISIBLE);
        }



    }

    public void checkBox(View v){
        if(checkBox.isChecked()){
            isChecked ="True";
        }else{
            isChecked ="False";
        }

    }

    @Override
    public void onBackPressed() {
        Intent newintent = new Intent();
        setResult(8, newintent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}