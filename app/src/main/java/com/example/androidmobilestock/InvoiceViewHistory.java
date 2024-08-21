package com.example.androidmobilestock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobilestock.adapter.SettingHistoryItemAdapter;
import com.example.androidmobilestock.adapter.SettingHistoryItemTypeAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class InvoiceViewHistory extends AppCompatActivity implements SettingHistoryItemAdapter.OnQuantityChangeListener {

    TextView noOfDays_text;
    RecyclerView recycle_historyItem_list;
    Button next_button;
    SettingHistoryItemTypeAdapter settingHistoryItemTypeAdapter;
    ACDatabase db;
    String noOfDays;
    String debtorCode;
    int searchMode = 1;
    private ArrayList<AC_Class.InvoiceDetails> itemsToAddToInvoice;
    AC_Class.InvoiceDetails invoiceDetails;
    Bundle args;
    String default_location = "";
    Boolean isAutoPrice = false;
    Boolean isTaxEnabled = true;
    Boolean isTaxInclusive;
    String defaultTax = "";
    ArrayList<AC_Class.InvoiceDetails> selectedItems;
    String nDocNo, nDocDate;
    String nBarCode;
    String nStartDate, nEndDate;
    int nMonths;
    String pDocDate, pDescription, pUOM, pRemarks, pRemarks2, pDocNo;
    double pUPrice, pDiscount;
    double aDiscount;
    String bItemCode, bBarCode, bUOM;

    double oTotalIn, oTotalEx, oTaxValue;
    double oSubTotal, oTaxRate;
    String oTaxType;
    SharedPreferences sharedPreferences;
    String func;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_view_history);

        db = new ACDatabase(this);

        Cursor cursor4 = db.getReg("20");
        if(cursor4.moveToFirst()){
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor3 = db.getReg("22");
        if(cursor3.moveToFirst()){
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor5 = db.getReg("21");
        if (cursor5.moveToFirst()){
            defaultTax = cursor5.getString(0);
        }

        Cursor cursor2 = db.getReg("13");
        if (cursor2.moveToFirst()) {
            isTaxInclusive = Boolean.valueOf(cursor2.getString(0));
        }

        Cursor dl = db.getReg("7");
        if(dl.moveToFirst()){
            default_location = dl.getString(0);
        }

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Sales History");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }

        noOfDays_text = findViewById(R.id.historyDaysHeader_text);
        recycle_historyItem_list = findViewById(R.id.historyItem_list);
        next_button = findViewById(R.id.invheader_btnNext);


        //noOfDays = getIntent().getStringExtra("noOfDays");
        debtorCode = getIntent().getStringExtra("DebtorCode");
        nDocNo = getIntent().getStringExtra("docNo");
        nDocDate = getIntent().getStringExtra("docDate");
        func = getIntent().getStringExtra("FunctionKey");

        sharedPreferences = getSharedPreferences("FeaturesEnable", Context.MODE_PRIVATE);
        noOfDays = sharedPreferences.getString("reorder_days", "Last 30 days");

        if (noOfDays != null || noOfDays.isEmpty()){
            noOfDays_text.setText(noOfDays);
            //convert noOfDays string to days
            nMonths = convertToMonths(noOfDays);
            nStartDate = getStartDate(nMonths);
            nEndDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        }

        itemsToAddToInvoice = new ArrayList<>();
        
        settingHistoryItemTypeAdapter = new SettingHistoryItemTypeAdapter(this, new HashMap<>(), (SettingHistoryItemAdapter.OnQuantityChangeListener) this);
        recycle_historyItem_list.setLayoutManager(new LinearLayoutManager(this));
        recycle_historyItem_list.setAdapter(settingHistoryItemTypeAdapter);

        //get data from database
        loadHistory(debtorCode);


        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemsToAddToInvoice != null){
                    for (AC_Class.InvoiceDetails item : itemsToAddToInvoice){
                    }

                    selectedItems = new ArrayList<>(itemsToAddToInvoice);

                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("selectedItems", selectedItems);
                    intent.putExtra("docNo", nDocNo);
                    intent.putExtra("docDate", nDocDate);
                    setResult(RESULT_OK, intent);
                    finish();


                } else {
                    Intent intent = new Intent();
                    intent.putExtra("docNo", nDocNo);
                    intent.putExtra("docDate", nDocDate);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private String getStartDate(int nMonths) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -nMonths);
        Date startDate = calendar.getTime();
        return new SimpleDateFormat("dd/MM/YYYY").format(startDate);
    }

    private int convertToMonths(String noOfDays) {
        switch (noOfDays) {
            case "Last 30 days":
                return 1;
            case "Last 2 months":
                return 2;
            case "Last 3 months":
                return 3;
            case "Last 6 months":
                return 6;
            default:
                return 1; // Default to one month if no match found
        }
    }

    @Override
    public void onQuantityChanged(AC_Class.Item item, int quantity)
    {
        AC_Class.InvoiceDetails invoiceDetail = new AC_Class.InvoiceDetails();
        invoiceDetail.setItemCode(item.ItemCode);
        invoiceDetail.setQuantity((double)quantity);


        //retrieve detail with given itemcode
        Cursor itemData = db.getItemDetails(item.ItemCode);
        if (itemData.moveToFirst()) {
            String itemType = itemData.getString(4);
            String bUOM = itemData.getString(7);
            float bPrice = itemData.getFloat(10);
            String bDescription = itemData.getString(1);
            String bBarCode = itemData.getString(12);

            invoiceDetail.setDocNo(nDocNo);
            invoiceDetail.setDocDate(nDocDate);
            invoiceDetail.setUOM(bUOM);
            invoiceDetail.setUPrice(Double.valueOf(bPrice));
            invoiceDetail.setDiscount(Double.valueOf("0.0"));
            invoiceDetail.setItemDescription(bDescription);
            invoiceDetail.setRemarks("");


            Cursor results = db.getItemBC(bBarCode);
            if (results.getCount() == 0) {
                Toast.makeText(this, "Product not found.", Toast.LENGTH_SHORT).show();
            } else {
                results.moveToNext();


                if (isAutoPrice) {
                    Cursor cursor_pc = db.getPriceCategory(debtorCode);
                    if (cursor_pc != null) {
                        if (cursor_pc.moveToFirst()) {
                            Object myPCObj = cursor_pc.getString(0);

                            if (myPCObj != null) {
                                try {
                                    Integer myPC = 0;
                                    myPC = Integer.parseInt(myPCObj.toString());

                                    switch (myPC) {
                                        case 2:
                                            invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price2"))));
                                            break;
                                        case 3:
                                            invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price3"))));
                                            break;
                                        case 4:
                                            invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price4"))));
                                            break;
                                        case 5:
                                            invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price5"))));
                                            break;
                                        case 6:
                                            invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price6"))));
                                            break;
                                    }
                                } catch (NumberFormatException e) {
                                    throw e;
                                }
                            }
                        }
                    }
                }

                String temp = invoiceDetail.getItemCode();
                Cursor data = db.getItemBC(temp);
                if (data.moveToNext()){
                    invoiceDetail.setTaxType(getTax(data.getString(data.getColumnIndex("TaxType"))));

                    Cursor data2 = db.getTaxTypeValue(invoiceDetail.getTaxType());
                    oTaxType = invoiceDetail.getTaxType();
                    Log.i("custDebug", getTax("None"));
                    if (data2.moveToFirst()) {
                        invoiceDetail.setTaxRate(data2.getDouble(0));
                        oTaxRate = invoiceDetail.getTaxRate();

                        if (isTaxInclusive) {
                            // Inclusive
                            invoiceDetail.setTotal_In(invoiceDetail.getQuantity() * invoiceDetail.getUPrice() - invoiceDetail.getDiscount());
                            // Calc. tax from total
                            invoiceDetail.setTaxValue((invoiceDetail.getTotal_In() * invoiceDetail.getTaxRate()) / (100 + invoiceDetail.getTaxRate()));
                            invoiceDetail.setTotal_Ex(invoiceDetail.getTotal_In() - invoiceDetail.getTaxValue());
                            invoiceDetail.setSubTotal((invoiceDetail.getTotal_Ex() + invoiceDetail.getDiscount()));
                            Log.wtf("Subtotal", String.valueOf(invoiceDetail.getSubTotal()));

                            oTotalIn = invoiceDetail.getTotal_In();
                            oTaxValue = invoiceDetail.getTaxValue();
                            oTotalEx = invoiceDetail.getTotal_Ex();
                            oSubTotal = invoiceDetail.getSubTotal();


                        } else {
                            // Exclusive
                            invoiceDetail.setSubTotal((invoiceDetail.getQuantity() * invoiceDetail.getUPrice()));
                            invoiceDetail.setTotal_Ex(invoiceDetail.getSubTotal() - invoiceDetail.getDiscount());
                            invoiceDetail.setTaxValue((invoiceDetail.getTotal_Ex() * invoiceDetail.getTaxRate()) / 100);
                            invoiceDetail.setTotal_In(invoiceDetail.getTotal_Ex() + invoiceDetail.getTaxValue());
                            Log.wtf("Subtotal", String.valueOf(invoiceDetail.getSubTotal()));

                            oTotalIn = invoiceDetail.getTotal_In();
                            oTaxValue = invoiceDetail.getTaxValue();
                            oTotalEx = invoiceDetail.getTotal_Ex();
                            oSubTotal = invoiceDetail.getSubTotal();
                        }

                    }
                } else {
                    Log.d("123123123", "error calculation");
                }


            }

        }

        Cursor dl = db.getReg("7");
        if(dl.moveToFirst()){
            default_location = dl.getString(0);
        }
        invoiceDetail.setLocation(default_location);

        boolean itemExists = false;
        for (AC_Class.InvoiceDetails detail : itemsToAddToInvoice) {
            if (detail.getItemCode().equals(item.ItemCode)) {
                if (quantity > 0) {
                    detail.setQuantity((double) quantity);

                    detail.setSubTotal(oSubTotal);
                    detail.setTotal_In(oTotalIn);
                    detail.setTotal_Ex(oTotalEx);
                    detail.setTaxType(oTaxType);
                    //detail.getTaxRate(oTaxRate);
                    detail.setTaxValue(oTaxValue);
                    detail.setTotal_In(oTotalIn);
                    detail.setTotal_Ex(oTotalEx);

                } else {
                    itemsToAddToInvoice.remove(detail);
                }
                itemExists = true;
                break;
            }
        }

        //add item to invoice
        if (!itemExists && quantity > 0) {
            itemsToAddToInvoice.add(invoiceDetail);
        }

    }




    public String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            String taxType = defaultTax;
            if (taxType != null && !taxType.isEmpty()) {
                return taxType;
            } else if (itemTaxType != null && !itemTaxType.isEmpty()) {
                return itemTaxType;
            } else {
                return defaultTax;
            }
        }
        return "None";
    }



    private void loadHistory(String debtorCode) {
        // Fetch data from HistoryPrice table
        Cursor data = db.getInvoiceHist2(debtorCode);
        Map<String, List<AC_Class.Item>> itemsByType = new TreeMap<>();

        try {
            if (data.getCount() == 0) {
                Log.i("custDebug", "no entries");
                return;
            } else {
                // Iterate through each record from HistoryPrice
                while (data.moveToNext()) {
                    // Retrieve the item code from HistoryPrice
                    String itemCode = data.getString(data.getColumnIndex("ItemCode"));

                    // Retrieve item details for the current item code
                    Cursor itemData = db.getItemDetails(itemCode);

                    if (itemData.moveToFirst()) {
                        String itemType = itemData.getString(4);
                        String itemDescription = itemData.getString(1);
                        String itemUOM = itemData.getString(7);
                        float itemPrice = itemData.getFloat(10);
                        AC_Class.Item item = new AC_Class.Item();
                        item.ItemCode = itemCode;
                        item.Description = itemDescription;
                        item.UOM = itemUOM;
                        item.Price = (float) itemPrice;

                        // Add the item to the correct list based on its type
                        if (!itemsByType.containsKey(itemType)) {
                            itemsByType.put(itemType, new ArrayList<>());
                        } else {
                            // Remove any existing item with the same item code
                            itemsByType.get(itemType).removeIf(existingItem -> existingItem.ItemCode.equals(itemCode));
                        }
                        itemsByType.get(itemType).add(item);
                    }
                    itemData.close();
                }

                // Sort items by item code in ascending order
                for (List<AC_Class.Item> itemList : itemsByType.values()) {
                    Collections.sort(itemList, new Comparator<AC_Class.Item>() {
                        @Override
                        public int compare(AC_Class.Item item1, AC_Class.Item item2) {
                            // Compare item codes in ascending order
                            return item1.ItemCode.compareTo(item2.ItemCode);
                        }
                    });
                }
            }
        } finally {
            if (data != null) {
                data.close();
            }
        }

        // Pass itemsByType to the adapter
        settingHistoryItemTypeAdapter.setItemsByType(itemsByType);
    }


    private List<String> getItemCodesForInvoice(String docNo, String nStartDate) {

        List<String> itemCodes = new ArrayList<>();

        Cursor itemData = db.getInvoiceDetailtoUpdate(docNo);
        try {
            if (itemData.getCount() > 0) {
                while (itemData.moveToNext()) {
                    // Retrieve item code for each invoice detail
                    String itemCode = itemData.getString(3);// item code is at index 3
                    String description = itemData.getString(4);
                    String uOM = itemData.getString(5);
                    double uPrice = itemData.getDouble(7);
                    aDiscount = itemData.getDouble(8);
                    String remarks = itemData.getString(17);
                    String remarks2 = itemData.getString(18);
                    String pDocNo = itemData.getString(1);

                    Cursor itemDocDate = db.getDocDate(docNo);
                    if (itemDocDate.getCount() > 0){
                        while(itemDocDate.moveToNext()){
                            pDocDate = itemDocDate.getString(itemDocDate.getColumnIndex("DocDate"));

                        }
                    }

                    if (isDateAfter(pDocDate, nStartDate)){
                        itemCodes.add(itemCode);
                        itemCodes.add(description);
                        itemCodes.add(uOM);
                        itemCodes.add(String.valueOf(uPrice));
                        itemCodes.add(String.valueOf(aDiscount));

                    } else {

                        Log.d("InvoiceViewHist", "Skipping item with DocNo: " + pDocNo + ", DocDate: " + pDocDate + " as it is not after startDate.");
                    }


                }
            }
        } finally {
            if (itemData != null) {
                itemData.close();
            }
        }
        return itemCodes;
    }

    private boolean isDateAfter(String date1, String date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate parsedDate1 = LocalDate.parse(date1, formatter);
            LocalDate parsedDate2 = LocalDate.parse(date2, formatter);
            return parsedDate1.isAfter(parsedDate2);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return false; // Default to false if parsing fails or dates are not in valid format
    }

    //handle back button
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