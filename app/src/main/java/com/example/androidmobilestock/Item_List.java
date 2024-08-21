package com.example.androidmobilestock;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Item_List extends AppCompatActivity {

    ListView item_listView;
    TextView modeView;
    EditText searchEditText;
    ACDatabase db;
    Intent pIntent;

    ItemListViewAdapter arrayAdapter;
    List<AC_Class.Item> s_inquiry = new ArrayList<>();
    String substring = "";
    String debtorCode;
    Boolean isAutoPrice = false;
    String key = "emptyStr";
    int SellingPrice = 0;
    TextView pricetag;
    int FilterByItemGroup, FilterByItemType;
    String ItemGroupList, ItemTypeList;
    String SQLINCLAUSE = "";
    //int searchMode = 0; //  0 for code&name; 1 for code; 2 for name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pIntent = getIntent();
        substring = pIntent.getStringExtra("substring");
        debtorCode = pIntent.getStringExtra("debtorcode");
        key = pIntent.getStringExtra("key");
        setContentView(R.layout.activity_item__list);
        item_listView = (ListView) findViewById(R.id.list_item);
        pricetag = (TextView) findViewById(R.id.price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Items");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);

        Cursor cursor4 = db.getReg("20");
        if(cursor4.moveToFirst()){
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor sale = db.getReg("48");
        if (sale.moveToFirst()) {
            SellingPrice = sale.getInt(0);
        }

        if(SellingPrice == 0){
            pricetag.setVisibility(View.GONE);
        }

        Cursor fig = db.getReg("58");
        if (fig.moveToFirst()){
            FilterByItemGroup = fig.getInt(0);
        }

        Cursor igl = db.getReg("59");
        if (igl.moveToFirst()){
            ItemGroupList = igl.getString(0).replace("\"","'");
        }

        if(FilterByItemGroup == 1){
            if(ItemGroupList !=null){
                SQLINCLAUSE += " AND b.ItemGroup IN (" + ItemGroupList + ")";
            }
        }

        Cursor fit = db.getReg("60");
        if (fit.moveToFirst()){
            FilterByItemType = fit.getInt(0);
        }

        Cursor itl = db.getReg("61");
        if (itl.moveToFirst()){
            ItemTypeList = itl.getString(0).replace("\"","'");
        }

        if(FilterByItemType == 1){
            if(ItemTypeList !=null){
                SQLINCLAUSE += " AND b.ItemType IN (" + ItemTypeList + ")";
            }
        }

        try {
            String defcurr = "";
            Cursor data = db.getReg("6");
            if(data.moveToFirst()){
                defcurr = data.getString(0);
            }

            arrayAdapter = new ItemListViewAdapter(this, s_inquiry, defcurr);
            item_listView.setAdapter(arrayAdapter);

            //item_listView.setTextFilterEnabled(true);
            item_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.Item i = (AC_Class.Item) parent.getItemAtPosition(position);
                    Intent item_intent = new Intent();
                    item_intent.putExtra("ItemsKey", i);
                    setResult(4, item_intent);
                    finish();
                }
            });
        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }

        modeView = findViewById(R.id.modeView2);
        searchEditText = (EditText) findViewById(R.id.searchField);
        searchEditText.requestFocus();
        if(key!=null){
            if(key.equals("FromStockTake")){
                getItemData2(substring);
            }
            if(key.equals("FromPurchase")){
                getItemData4(substring);
                //Action bar
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("List of Items");
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

                ConstraintLayout linearLayout = findViewById(R.id.linearLayout5);
                linearLayout.setBackgroundColor(Color.RED);
            }
        }else {
            getItemData(substring);
        }

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(key!=null){
                    if(key.equals("FromStockTake")){
                        getItemData2(s.toString().trim());
                    }else if(key.equals("FromPurchase")){
                        getItemData4(s.toString().trim());
                    }
                }else {
                    getItemData(s.toString().trim());
                }

            }
        });
    }

    private void getItemData2(String substring) {
        Cursor data = db.getItemLike(substring, 1, SQLINCLAUSE);
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1),
                            data.getString(2), data.getString(3), data.getString(4), data.getString(5),
                            data.getString(6), data.getString(7), data.getString(8), data.getString(9),
                            data.getFloat(10), data.getFloat(11),  data.getFloat(12), data.getFloat(13),
                            data.getFloat(14), data.getFloat(15),
                            data.getString(16), data.getString(17), data.getFloat(18),data.getFloat(19),
                            data.getFloat(20), data.getString(21));

                    s_inquiry.add(item);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }


    //PurchaseUOM
    private void getItemData4(String substring) {
        Cursor data = db.getItemLike(substring, 5, SQLINCLAUSE);
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1),
                            data.getString(2), data.getString(3), data.getString(4), data.getString(5),
                            data.getString(6), data.getString(7), data.getString(8), data.getString(9),
                            data.getFloat(10), data.getFloat(11), data.getString(12), data.getString(13));

                    s_inquiry.add(item);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            pIntent.putExtra("ItemsKey", "");
            setResult(4, pIntent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getItemData(String substring) {
        Cursor data = db.getItemLike(substring, 0, SQLINCLAUSE);
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(6), data.getString(7), data.getString(8), data.getFloat(9), data.getFloat(10), data.getFloat(11), data.getFloat(12), data.getFloat(13), data.getFloat(14), data.getString(15), data.getString(16), data.getFloat(17), data.getString(18),data.getFloat(19),data.getFloat(20), data.getString(21));

                    s_inquiry.add(item);
                    if(debtorCode!=null) {
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
                                                    item.setPrice(item.getPrice2());
                                                    break;
                                                case 3:
                                                    item.setPrice(item.getPrice3());
                                                    break;
                                                case 4:
                                                    item.setPrice(item.getPrice4());
                                                    break;
                                                case 5:
                                                    item.setPrice(item.getPrice5());
                                                    break;
                                                case 6:
                                                    item.setPrice(item.getPrice6());
                                                    break;
                                            }
                                        } catch (NumberFormatException e) {
                                            throw e;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
