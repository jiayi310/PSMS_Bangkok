package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


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

    String discountText;
    TextView lbl_DiscountText;
    Button btn_add, btn_clear;
    String text = "by Percentage";

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
        discountText = pintent.getStringExtra("DiscountText");

//        TextView itemcode = (TextView) findViewById(R.id.itemcode);
//        itemcode.setText(Item);

        lbl_DiscountText = findViewById(R.id.discountText);

        if (discountText.isEmpty() || discountText.equals("0")){
            discountText = "0";
            lbl_DiscountText.setText("No discount");
        }else {
            lbl_DiscountText.setText(discountText.toString());
        }


        btn_add = findViewById(R.id.btn_add);
        btn_clear = findViewById(R.id.btn_clear);

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


        isPercentage="True";
        isChecked="False";

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountText = "0";
                lbl_DiscountText.setText("No discount");
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text.equals("by Percentage")){
                    if(discountAmt.getText().toString().matches("")){
                        Toast.makeText(getApplicationContext(),"Please type an amount",Toast.LENGTH_SHORT).show();
                    } else {
                        if (discountText.isEmpty() || discountText.equals("0")){
                            discountText = discountAmt.getText() + "%";
                            lbl_DiscountText.setText(discountText);
                        } else {
                            discountText = discountText + " + " + discountAmt.getText() + "%";
                            lbl_DiscountText.setText(discountText);
                        }

                    }
                } else {
                    if(discountAmt.getText().toString().matches("")){
                        Toast.makeText(getApplicationContext(),"Please type an amount",Toast.LENGTH_SHORT).show();
                    } else {
                        if (discountText.isEmpty() || discountText.equals("0")){
                            discountText = discountAmt.getText().toString();
                            lbl_DiscountText.setText(discountText);
                        } else {
                            discountText = discountText + " + " + discountAmt.getText();
                            lbl_DiscountText.setText(discountText);
                        }

                    }
                }

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty()  || discountText.equals("0")){
                    discountText = "0";
                }


                Intent myintent = new Intent();
                myintent.putExtra("DiscountText", discountText);
                myintent.putExtra("IsChecked", isChecked);
                setResult(8, myintent);
                finish();


//                if(discountAmt.getText().toString().matches("")){
//                    Toast.makeText(getApplicationContext(),"Please type an amount",Toast.LENGTH_SHORT).show();
//                }else{
//                    Double dis = Double.valueOf(discountAmt.getText().toString());
//                    Intent myintent = new Intent();
//                    myintent.putExtra("Discount", dis);
//                    myintent.putExtra("IsPercentage", isPercentage);
//                    myintent.putExtra("IsChecked", isChecked);
//                    setResult(8, myintent);
//                    finish();
//                }

            }
        });

        dis1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "1%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 1%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(1.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "2%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 2%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(2.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "3%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 3%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(3.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "5%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 5%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(5.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "8%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 8%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(8.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "10%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 10%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(10.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "15%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 15%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(15.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "20%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 20%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(20.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "25%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 25%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(25.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });

        dis30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (discountText.isEmpty() || discountText.equals("0")){
                    discountText = "30%";
                    lbl_DiscountText.setText(discountText);
                } else {
                    discountText = discountText + " + 30%";
                    lbl_DiscountText.setText(discountText);
                }

//                Double dis = Double.valueOf(30.0);
//
//                Intent myintent = new Intent();
//                myintent.putExtra("Discount", dis);
//                myintent.putExtra("IsPercentage", isPercentage);
//                myintent.putExtra("IsChecked", isChecked);
//                setResult(8, myintent);
//                finish();
            }
        });


    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);


        text = radioButton.getText().toString();

        if(text.equals("by Percentage")){
            textType.setText("Percentage: ");
            isPercentage ="True";
            textdicount.setVisibility(View.VISIBLE);
            buttonlist.setVisibility(View.VISIBLE);
        }else{
            textType.setText("Amount: ");
            isPercentage ="False";
            textdicount.setVisibility(View.INVISIBLE);
            buttonlist.setVisibility(View.INVISIBLE);
        }



    }



    @Override
    public void onBackPressed() {
        Intent newintent = new Intent();
        newintent.putExtra("DiscountText", discountText);
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