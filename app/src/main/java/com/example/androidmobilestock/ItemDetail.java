package com.example.androidmobilestock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityItemDetailBinding;

import java.math.BigDecimal;
import java.util.List;

public class ItemDetail extends AppCompatActivity {
    ActivityItemDetailBinding binding;
    AC_Class.InvoiceDetails invoiceDetails2;
    List<AC_Class.InvoiceDetails> itemlist;
    ACDatabase db;
    Intent pintent;
    AC_Class.Item myItem;
    EditText etQty;
    Button addtocart;
    String Func;
    String default_loc;
    Boolean isTaxEnabled = true;
    TextView tax, taxType, description2;
    MyClickHandler handler;
    TextView balance, price, discount;
    float baseQty = 0f;
    float balanceqty = 0f;
    Boolean isBatchNoEnabled = true;
    String salesOrderType = "";
    Boolean isSaleBatchEnabled = true;

    Double dis;
    Double disNumber;
    String isPercentage, isChecked;
    Boolean NegativeInventory = true;
    double qty;
    String nDiscountText = "0";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_detail);

        binding.remarks.setVisibility(View.GONE);
        binding.remarkstxt.setVisibility(View.GONE);
        binding.batchno.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        balance = findViewById(R.id.balanceqty);
        description2 = findViewById(R.id.Description2);

        db = new ACDatabase(this);
        pintent = getIntent();
        invoiceDetails2 = getIntent().getParcelableExtra("invoiceDetail");
        itemlist = pintent.getParcelableArrayListExtra("CartList");
        Func = pintent.getStringExtra("FunctionKey");
        myItem = pintent.getParcelableExtra("Item");

        addtocart = findViewById(R.id.addtocart);
        tax = findViewById(R.id.tax);
        price = findViewById(R.id.price);
        discount = findViewById(R.id.discount);
        taxType = findViewById(R.id.taxtype);
        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        Cursor loc = db.getReg("7");
        if (loc.moveToFirst()) {
            default_loc = loc.getString(0);
        }
        Cursor cur = db.getReg("6");
        if (cur.moveToFirst()) {
            binding.setDefaultCurr(cur.getString(0));
        }

        Cursor cursor7 = db.getReg("39");
        if(cursor7.moveToFirst()){
            isSaleBatchEnabled= Boolean.valueOf(cursor7.getString(0));
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        // Change format if tax enabled/disabled
        if (!isTaxEnabled) {
            tax.setVisibility(View.GONE);
        }




        Cursor cursor5 = db.getReg("38");
        if (cursor5.moveToFirst()) {
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        Cursor cursor9 = db.getReg("42");
        if (cursor9.moveToFirst()) {
            NegativeInventory = Boolean.valueOf(cursor9.getString(0));
        }

        if (Func != null) {
            getCurrentDataForEdit();

            binding.setInvoicedetail(invoiceDetails2);
            actionBar.setTitle(invoiceDetails2.getItemCode());
            addtocart.setText("Change");
            discount.setText(Double.toString(invoiceDetails2.getDiscount()));
            double n2Var = Double.parseDouble(discount.getText().toString());
            invoiceDetails2.setDiscount(n2Var);
            invoiceDetails2.setDiscountText("0");

            if (tax != null) {

                tax.setText(Double.toString(invoiceDetails2.getTaxRate()));
                taxType.setText(invoiceDetails2.getTaxType());
                invoiceDetails2.setTaxType(invoiceDetails2.getTaxType());
                invoiceDetails2.setTaxRate(invoiceDetails2.getTaxRate());
            }

            Cursor temp;
            temp = db.getSalesDtlHistoryPrice(invoiceDetails2.getItemCode(), invoiceDetails2.getUOM());
            if (temp.getCount() > 0) {
                temp.moveToNext();
                balanceqty -= temp.getFloat(0);
                baseQty -= temp.getFloat(0) * temp.getFloat(1);
            }

            balance.setText("Remaining Balance: " + Float.toString(balanceqty));

            //BatchNo
            if (Func.equals("Edit")) {
                if (isBatchNoEnabled && invoiceDetails2.getBatchNo() != null) {
                    binding.batchno.setVisibility(View.VISIBLE);
                    binding.batchno.setText(invoiceDetails2.getBatchNo());
                    Cursor temp3;
                    temp3 = db.getStockBalanceBatchNo(invoiceDetails2.getItemCode(), invoiceDetails2.getUOM(), invoiceDetails2.getBatchNo());
                    if (temp3.getCount() > 0) {
                        temp3.moveToNext();
                        balanceqty = temp3.getFloat(0);
                    }
                    binding.balanceqty.setText("Remaining Balance: " + Float.toString(balanceqty));

                    binding.batchno.setText(invoiceDetails2.getBatchNo());
                } else {
                    binding.batchno.setVisibility(View.GONE);
                }
            } else {
                if (isBatchNoEnabled && myItem.getHasBatchNo().equals("true")) {
                    getbatchNo();
                } else {
                    binding.batchno.setVisibility(View.GONE);
                }
            }

        } else {
            getData();
            binding.setInvoicedetail(invoiceDetails2);
            actionBar.setTitle(invoiceDetails2.getItemCode());
        }

        binding.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etQty = (EditText) findViewById(R.id.quantity);

                String qty = etQty.getText().toString();

                if (qty.length() > 0) {
                    invoiceDetails2.setQuantity(Double.valueOf(qty));
                    //Calculation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            pintent.putExtra("ItemKey", "");
            setResult(4, pintent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.add: {
                binding.remarks.setVisibility(View.VISIBLE);
                binding.remarkstxt.setVisibility(View.VISIBLE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 1:

                break;
            case 5:
                if (data != null) {
                    AC_Class.TaxType taxtype = data.getParcelableExtra("TaxTypesKey");
                    if (taxtype != null) {
                        invoiceDetails2.setTaxType(taxtype.getTaxType());
                        invoiceDetails2.setTaxRate(Double.valueOf(taxtype.getTaxRate()));
                        TextView tax = findViewById(R.id.tax);
                        tax.setText(taxtype.getTaxRate().toString());
                        TextView taxType = findViewById(R.id.taxtype);
                        taxType.setText(taxtype.getTaxType());
                    }
                }
                break;

            case 7:

                AC_Class.SellingPrice sellingPrice = data.getParcelableExtra("price");
                if (sellingPrice != null) {
                    if (sellingPrice.getPrice() != 0) {
                        invoiceDetails2.setUPrice(roundDouble(Double.valueOf(sellingPrice.getPrice())));
                        TextView price = findViewById(R.id.price);
                        price.setText(sellingPrice.getPrice().toString());
                    }
                }
                break;

                //return from discount
            case 8:

                nDiscountText = data.getStringExtra("DiscountText");
                invoiceDetails2.setDiscountText(nDiscountText);

                double subtotal = invoiceDetails2.getQuantity() * invoiceDetails2.getUPrice();
                double nDiscount = 0;
                double runningTotal = subtotal;

                if (invoiceDetails2.getDiscountText() != null && !invoiceDetails2.getDiscountText().isEmpty()) {
                    // Normalize the discount text: remove spaces and replace '/' with '+'
                    String discountText = invoiceDetails2.getDiscountText().replace(" ", "").replace("/", "+");
                    String[] discountParts = discountText.split("\\+"); // Split the string by '+'

                    for (String part : discountParts) {
                        if (part.endsWith("%")) {
                            // Percentage discount
                            double percentage = Double.parseDouble(part.replace("%", ""));
                            double discountAmount = (runningTotal * (percentage / 100));
                            nDiscount += discountAmount;
                            runningTotal -= discountAmount;

                        } else {
                            // Fixed amount discount
                            double amount = Double.parseDouble(part);
                            nDiscount += amount;
                            runningTotal -= amount;
                        }
                    }
                }

                invoiceDetails2.setDiscount(nDiscount);
                disNumber = nDiscount;

                if (Func != null) {
                    if (Func.equals("Edit")) {

                        if (disNumber > invoiceDetails2.getUPrice()) {
                            Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
                        } else {
                            discount.setText("- " + String.format("%.2f", disNumber));
                            invoiceDetails2.setDiscount(disNumber);
                        }
                    }
                } else {
                    if (disNumber > invoiceDetails2.getUPrice()) {
                        Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
                    } else {
                        discount.setText("- " + String.format("%.2f", disNumber));
                        invoiceDetails2.setDiscount(disNumber);

                    }
                }

                break;

            case 9:
                String batchNo = data.getStringExtra("ItemBatchNo");
                invoiceDetails2.setBatchNo(batchNo);
                binding.batchno.setText(batchNo);
                if (isBatchNoEnabled) {
                    //stock balance
                    if (invoiceDetails2.getBatchNo() != null) {
                        Cursor temp2;
                        temp2 = db.getStockBalanceBatchNo(invoiceDetails2.getItemCode(), invoiceDetails2.getUOM(), invoiceDetails2.getBatchNo());
                        if (temp2.getCount() > 0) {
                            temp2.moveToNext();
                            balanceqty = temp2.getFloat(0);
                        }
                        binding.balanceqty.setText("Remaining Balance: " + Float.toString(balanceqty));
                    }
                }

                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnInClicked(View view) {
            invoiceDetails2.setQuantity(invoiceDetails2.getQuantity() + 1);
            etQty = (EditText) findViewById(R.id.quantity);
            etQty.setText(String.format("%.0f", invoiceDetails2.getQuantity()));

            if (dis != null && dis != 0.00) {

                if (isPercentage.equals("True")) {
                    disNumber = invoiceDetails2.getUPrice() * invoiceDetails2.getQuantity() * dis;
                } else {
                    if (isChecked.equals("True")) {
                        disNumber = dis * invoiceDetails2.getQuantity();

                    } else {
                        disNumber = dis;
                    }
                }


                if (Func != null) {
                    if (Func.equals("Edit")) {

                        if (disNumber > invoiceDetails2.getUPrice()) {
                            Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
                        } else {
                            discount.setText("- " + String.format("%.2f", disNumber));
                            invoiceDetails2.setDiscount(disNumber);
                        }
                    }
                } else {
                    if (disNumber > invoiceDetails2.getUPrice()) {
                        Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
                    } else {
                        discount.setText("- " + String.format("%.2f", disNumber));
                        invoiceDetails2.setDiscount(disNumber);

                    }
                }

            }
        }


        public void OnDeClicked(View view) {
            invoiceDetails2.setQuantity(invoiceDetails2.getQuantity() <= 1 ? 1 : invoiceDetails2.getQuantity() - 1);
            etQty = (EditText) findViewById(R.id.quantity);
            etQty.setText(String.format("%.0f", invoiceDetails2.getQuantity()));
            if (dis != null && dis != 0.00) {

                if (isPercentage.equals("True")) {
                    disNumber = invoiceDetails2.getUPrice() * invoiceDetails2.getQuantity() * dis;
                } else {
                    if (isChecked.equals("True")) {
                        disNumber = dis * invoiceDetails2.getQuantity();

                    } else {
                        disNumber = dis;
                    }
                }


                if (Func != null) {
                    if (Func.equals("Edit")) {

                        if (disNumber > invoiceDetails2.getUPrice()) {
                            Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
                        } else {
                            discount.setText("- " + String.format("%.2f", disNumber));
                            invoiceDetails2.setDiscount(disNumber);
                        }
                    }
                } else {
                    if (disNumber > invoiceDetails2.getUPrice()) {
                        Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
                    } else {
                        discount.setText("- " + String.format("%.2f", disNumber));
                        invoiceDetails2.setDiscount(disNumber);

                    }
                }

            }
        }

        public void OnPriceClicked(View view) {
            Intent new_intent = new Intent(context, Check_List.class);
            new_intent.putExtra("ItemCode", invoiceDetails2.getItemCode());
            new_intent.putExtra("ItemUOMKey", invoiceDetails2.getUOM());
            startActivityForResult(new_intent, 7);
        }

        public void onTaxTypeTxtViewClicked(View view) {
            Intent new_intent = new Intent(context, TaxType_List.class);
            startActivityForResult(new_intent, 5);
        }

        public void onItemBatchTxtViewClicked(View view) {
            Intent new_intent = new Intent(context, ItemBatchList.class);
            new_intent.putExtra("ItemCode", invoiceDetails2.getItemCode());
            new_intent.putExtra("UOM", invoiceDetails2.getUOM());
            new_intent.putExtra("Location", default_loc);
            startActivityForResult(new_intent, 9);
        }

        public void OnDiscountClicked(View view) {

            if (invoiceDetails2.getItemCode() != null) {
                Intent new_intent = new Intent(context, Discount_List.class);
                new_intent.putExtra("ItemCode", invoiceDetails2.getItemCode());
                new_intent.putExtra("DiscountText", invoiceDetails2.getDiscountText());
                startActivityForResult(new_intent, 8);

            }
//            AlertDialog.Builder alert = new AlertDialog.Builder(context);
//            final EditText input = new EditText(context);
//            alert.setTitle("Set Discount Price");
//            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//
//            alert.setView(input);
//
//            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    //What ever you want to do with the value
//                    Editable dis = input.getText();
//                    TextView discount = findViewById(R.id.discount);
//                    double n2Var = Double.parseDouble(input.getText().toString());
//
//                    if (Func != null) {
//                        if (Func.equals("Edit")) {
//
//                            if (n2Var > items.getPrice()) {
//                                Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
//                            } else {
//                                discount.setText("- " + String.format("%.2f", n2Var));
//                                invoiceDetails.setDiscount(n2Var);
//                            }
//                        }
//                    } else {
//                        if (n2Var > item.getPrice()) {
//                            Toast.makeText(getApplicationContext(), "Discount Price Exceed Original Price", Toast.LENGTH_LONG).show();
//                        } else {
//                            discount.setText("- " + String.format("%.2f", n2Var));
//                            invoiceDetails.setDiscount(n2Var);
//
//                        }
//                    }
//
//                }
//            });
//
//            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    // what ever you want to do with No option.
//                }
//            });
//
//            alert.show();

        }

        public void OnAddClicked(View view) {
            if (isBatchNoEnabled) {
                String batch = binding.batchno.getText().toString();
                if (batch == null || batch.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in batch no", Toast.LENGTH_SHORT).show();
                    binding.batchno.setError("This field cannot be blank!", null);
                    return;
                }
            }
            if(itemlist!=null) {
                int myResult = checkInvoiceDetailList(invoiceDetails2);

                if (myResult != -1) {
                    if (isBatchNoEnabled) {
                        if (itemlist.get(myResult).getBatchNo() != null) {
                            Cursor temp3;
                            temp3 = db.getStockBalanceBatchNo(itemlist.get(myResult).getItemCode(), itemlist.get(myResult).getUOM(), itemlist.get(myResult).getBatchNo());
                            if (temp3.getCount() > 0) {
                                temp3.moveToNext();
                                balanceqty = temp3.getFloat(0);
                            }
                        }
                    }
                }
            }

            double t_qty = qty + invoiceDetails2.getQuantity();
            if (NegativeInventory) {
                saveData();
            } else {
                if (invoiceDetails2.getQuantity() > balanceqty) {
                    Toast.makeText(getApplicationContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                } else {
                    if(t_qty >balanceqty){
                        Toast.makeText(getApplicationContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                    }else {
                        saveData();
                    }
                }
            }
        }
    }

    private int checkInvoiceDetailList(AC_Class.InvoiceDetails invoiceDetails) {
        int result = -1;
        qty=0.0;

        for (int i = 0; i < itemlist.size(); i++) {
            AC_Class.InvoiceDetails tempDetail = itemlist.get(i);

            if(!isBatchNoEnabled) {
                if (tempDetail.getItemCode().equals(invoiceDetails.getItemCode()) && tempDetail.getUOM().equals(invoiceDetails.getUOM()) && tempDetail.getUPrice().equals(invoiceDetails.getUPrice())) {
                    qty += tempDetail.getQuantity();
                    result = i;
                }
            }else{
                if(tempDetail.getBatchNo()!=null) {
                    if (tempDetail.getItemCode().equals(invoiceDetails.getItemCode()) && tempDetail.getUOM().equals(invoiceDetails.getUOM()) && tempDetail.getUPrice().equals(invoiceDetails.getUPrice()) && tempDetail.getBatchNo().equals(invoiceDetails.getBatchNo())) {
                        qty += tempDetail.getQuantity();
                        result = i;
                    }
                }
            }
        }
        return result;
    }

    public void saveData() {

        invoiceDetails2.setRemarks(binding.remarkstxt.getText().toString());
        Intent new_intent = new Intent();
        new_intent.putExtra("InvoiceDetail", invoiceDetails2);

        if (Func != null) {
            if (Func.equals("Edit")) {
                setResult(4, new_intent);
            }
        } else {
            setResult(3, new_intent);
        }
        finish();
    }

    public void getCurrentDataForEdit() {
        Cursor data;
        switch (Func) {
            case "Edit":
                Bitmap myImage = db.getItemImage(invoiceDetails2.getItemCode());
                if (myImage != null)
                    binding.imageViewInfo.setImageBitmap(myImage);
        }

    }

    double roundDouble(double x) {
        BigDecimal bd = new BigDecimal(Double.toString(x));
        bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private void getData() {
        try {
            invoiceDetails2 = new AC_Class.InvoiceDetails();
            invoiceDetails2.setItemCode(myItem.getItemCode());
            invoiceDetails2.setItemDescription(myItem.getDescription());
            invoiceDetails2.setUOM(myItem.getUOM());
            invoiceDetails2.setDiscount(0.00);
            invoiceDetails2.setUPrice(roundDouble(Double.valueOf(myItem.getPrice())));
            invoiceDetails2.setDiscountText("0");


            Cursor cur;

            cur = db.getItemDesc2(myItem.getItemCode());
            if (cur.getCount() > 0) {
                cur.moveToNext();
                description2.setText(cur.getString(0));
            }


            Cursor temp, temp2;

            temp = db.getStockBalance(invoiceDetails2.getItemCode(), invoiceDetails2.getUOM());
            if (temp.getCount() > 0) {
                temp.moveToNext();
                balanceqty = temp.getFloat(0);
            }
            temp2 = db.getSalesDtlHistoryPrice(invoiceDetails2.getItemCode(), invoiceDetails2.getUOM());
            if (temp2.getCount() > 0) {
                temp2.moveToNext();
                balanceqty = temp2.getFloat(0);
            }
            balance.setText("Remaining Balance: " + Float.toString(balanceqty));

            Bitmap myImage = db.getItemImage(myItem.getItemCode());
            if (myImage != null)
                binding.imageViewInfo.setImageBitmap(myImage);

            //BatchNo
            if (isBatchNoEnabled && myItem.getHasBatchNo().equals("true")) {
                getbatchNo();


                Cursor temp3;
                temp3 = db.getStockBalanceBatchNo(invoiceDetails2.getItemCode(), invoiceDetails2.getUOM(), invoiceDetails2.getBatchNo());
                if (temp3.getCount() > 0) {
                    temp3.moveToNext();
                    balanceqty = temp3.getFloat(0);
                }
                binding.balanceqty.setText("Remaining Balance: " + Float.toString(balanceqty));

                binding.batchno.setText(invoiceDetails2.getBatchNo());
            }

        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }
    }

    public void getbatchNo() {
        binding.batchno.setVisibility(View.VISIBLE);
        if (isSaleBatchEnabled) {

            if (salesOrderType != "0" || salesOrderType != null) {

                if (salesOrderType.equals("Latest Manufacture Date")) {

                    Cursor data2 = db.getLateManuBatch(invoiceDetails2.getItemCode(),invoiceDetails2.getUOM(),invoiceDetails2.getLocation());

                    if (data2.moveToFirst()) {

                        invoiceDetails2.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                    }
                } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                    Cursor data2 = db.getEarManuBatch(invoiceDetails2.getItemCode(),invoiceDetails2.getUOM(),invoiceDetails2.getLocation());

                    if (data2.moveToFirst()) {

                        invoiceDetails2.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                    }
                } else if (salesOrderType.equals("Latest Expiration Date")) {
                    Cursor data2 = db.getLateExpBatch(invoiceDetails2.getItemCode(),invoiceDetails2.getUOM(),invoiceDetails2.getLocation());

                    if (data2.moveToFirst()) {

                        invoiceDetails2.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                    }
                } else if (salesOrderType.equals("Earliest Expiration Date")) {
                    Cursor data2 = db.getEarExpBatch(invoiceDetails2.getItemCode(),invoiceDetails2.getUOM(),invoiceDetails2.getLocation());

                    if (data2.moveToFirst()) {

                        invoiceDetails2.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                    }
                }

            } else {
                Cursor data2 = db.getEarManuBatch(invoiceDetails2.getItemCode(),invoiceDetails2.getUOM(),invoiceDetails2.getLocation());

                if (data2.moveToFirst()) {

                    invoiceDetails2.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                }
                invoiceDetails2.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
            }


            if (invoiceDetails2.getBatchNo() != null) {
                Cursor temp2;
                temp2 = db.getStockBalanceBatchNo(invoiceDetails2.getItemCode(), invoiceDetails2.getUOM(), invoiceDetails2.getBatchNo());
                if (temp2.getCount() > 0) {
                    temp2.moveToNext();
                    balanceqty = temp2.getFloat(0);
                }
                binding.balanceqty.setText("Remaining Balance: " + Float.toString(balanceqty));
            }
        }
        binding.batchno.setText(invoiceDetails2.getBatchNo());
    }

}
