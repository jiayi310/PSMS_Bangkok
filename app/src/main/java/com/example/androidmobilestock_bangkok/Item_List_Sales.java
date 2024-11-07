package com.example.androidmobilestock_bangkok;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class Item_List_Sales extends AppCompatActivity {

    ListView item_listView;
    TextView modeView;
    EditText searchEditText;
    ACDatabase db;
    Intent pIntent;

    ItemListViewSalesAdapter arrayAdapter;
    List<AC_Class.Item> s_inquiry = new ArrayList<>();
    String substring = "";
    String debtorCode;
    Boolean isAutoPrice = false;
    String key = "emptyStr";
    int FilterByItemGroup, FilterByItemType;
    String ItemGroupList, ItemTypeList;
    String SQLINCLAUSE = "";
    TextView s_inquiry_camera;
    private IntentIntegrator qrScan;
    //int searchMode = 0; //  0 for code&name; 1 for code; 2 for name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pIntent = getIntent();
        substring = pIntent.getStringExtra("substring");
        debtorCode = pIntent.getStringExtra("debtorcode");
        key = pIntent.getStringExtra("key");
        setContentView(R.layout.activity_item_list_sales);
        item_listView = (ListView) findViewById(R.id.list_item);
        s_inquiry_camera = (TextView) findViewById(R.id.si_item_camera);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Items");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);

        Cursor cursor4 = db.getReg("20");
        if(cursor4.moveToFirst()){
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
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

            arrayAdapter = new ItemListViewSalesAdapter(this, s_inquiry, defcurr);
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
        getItemData3(substring);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                getItemData(s.toString().trim());
            }
        });

        s_inquiry_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qrScan = new IntentIntegrator(Item_List_Sales.this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(true);
                    //qrScan.setBarcodeImageEnabled(false);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

                } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
            }
        });
    }

    //SalesUOM
    private void getItemData3(String substring) {
        Cursor data = db.getItemLike(substring, 4, SQLINCLAUSE);
        int nUDF1 = data.getColumnIndex("UDF_UDF1");
        int nUDF2 = data.getColumnIndex("UDF_UDF2");
        int nUDF3 = data.getColumnIndex("UDF_UDF3");
        int nUDF4 = data.getColumnIndex("UDF_UDF4");
        int nUDF5 = data.getColumnIndex("UDF_UDF5");
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(
                            data.getString(0),
                            data.getString(1),
                            data.getString(2),
                            data.getString(3),
                            data.getString(4),
                            data.getString(5),
                            data.getString(6),
                            data.getString(7),
                            data.getString(8),
                            data.getString(9),
                            data.getFloat(10),
                            data.getFloat(11),
                            data.getFloat(12),
                            data.getFloat(13),
                            data.getFloat(14),
                            data.getFloat(15),
                            data.getString(16),
                            data.getString(17),
                            data.getFloat(18),
                            data.getFloat(19),
                            data.getFloat(20),
                            data.getString(21),
                            data.getString(22),
                            data.getString(23),
                            data.getString(24),
                            data.getString(25),
                            data.getString(26)

                    );
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
        int nUDF1 = data.getColumnIndex("UDF_UDF1");
        int nUDF2 = data.getColumnIndex("UDF_UDF2");
        int nUDF3 = data.getColumnIndex("UDF_UDF3");
        int nUDF4 = data.getColumnIndex("UDF_UDF4");
        int nUDF5 = data.getColumnIndex("UDF_UDF5");
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(
                            data.getString(0),
                            data.getString(1),
                            data.getString(2),
                            data.getString(3),
                            data.getString(4),
                            data.getString(5),
                            data.getString(6),
                            data.getString(7),
                            data.getString(8),
                            data.getString(9),
                            data.getFloat(10),
                            data.getFloat(11),
                            data.getFloat(12),
                            data.getFloat(13),
                            data.getFloat(14),
                            data.getFloat(15),
                            data.getString(16),
                            data.getString(17),
                            data.getFloat(18),
                            data.getFloat(19),
                            data.getFloat(20),
                            data.getString(21),
                            data.getString(22),
                            data.getString(23),
                            data.getString(24),
                            data.getString(25),
                            data.getString(26)

                    );

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //Return from item list

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "No result found.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    searchEditText.setText(result.getContents().trim());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
