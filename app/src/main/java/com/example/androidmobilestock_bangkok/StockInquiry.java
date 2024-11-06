package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.adapter.ItemListViewAdapter;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.ActivityStockInquiryBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockInquiry extends AppCompatActivity {
    ActivityStockInquiryBinding binding;
    AC_Class.Item items;
    MyClickHandler handler;
    ACDatabase db;
    private IntentIntegrator qrScan;

    public Barcode2DWithSoft barcode2D;
    ListView item_listView;
    ItemListViewAdapter arrayAdapter;
    List<AC_Class.Item> s_inquiry = new ArrayList<>();
    int SellingPrice = 0;
    int FilterByItemGroup, FilterByItemType;
    String ItemGroupList, ItemTypeList;
    String SQLINCLAUSE = "";
    int CustomA;
    int CustomB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_inquiry);
        items = new AC_Class.Item();
        binding.setItem(items);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.menu_stock_inquiry));
        actionBar.setDisplayHomeAsUpEnabled(true);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);
        db = new ACDatabase(this);
        binding.setItem(items);
        item_listView = (ListView) findViewById(R.id.list_item);

        binding.itemEditText.setFocusable(true);
        binding.itemEditText.setFocusableInTouchMode(true);
        binding.itemEditText.requestFocus();

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

        Cursor customA = db.getReg("75");
        if (customA.moveToFirst()){
            CustomA = customA.getInt(0);
        }

        Cursor customB = db.getReg("76");
        if (customB.moveToFirst()){
            CustomB = customB.getInt(0);
        }

        try {
            String defcurr = "";
            Cursor data = db.getReg("6");
            if(data.moveToFirst()){
                defcurr = data.getString(0);
            }

            Cursor sale = db.getReg("48");
            if (sale.moveToFirst()) {
                SellingPrice = sale.getInt(0);
            }

            if(SellingPrice == 0){
                binding.price.setVisibility(View.GONE);
            }

            arrayAdapter = new ItemListViewAdapter(this, s_inquiry, defcurr);
            item_listView.setAdapter(arrayAdapter);

            //item_listView.setTextFilterEnabled(true);
            item_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.Item i = (AC_Class.Item) parent.getItemAtPosition(position);
                    if (i != null) {
                        items = i;
                    }
                    binding.btnSearch.performClick();
                    //items.setItemCode(null);
                }
            });
        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }

        hideSoftKeyboard(binding.getRoot());
        getItemData("");

        binding.itemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String remove = removeNonAlphanumeric(s.toString().trim());
                getItemData(remove);
            }
        });

        binding.itemEditText.clearFocus();
        binding.itemEditText.requestFocus();

        if (android.os.Build.MODEL.equals("HC720")) {
            try {
                barcode2D = Barcode2DWithSoft.getInstance();
            } catch (Exception ex) {
                Toast.makeText(StockInquiry.this, ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
            initSound();
        }
    }

    public static String
    removeNonAlphanumeric(String str)
    {
        // replace the given string
        // with empty string
        // except the pattern "[^a-zA-Z0-9]"
//        str = str.replaceAll(
//                "[^a-zA-Z0-9]", "");

//        int length = str.length();
//        StringBuffer buffer = new StringBuffer( );
//        for(int i = 0; i < length; i++) {
//            int ch = ( int ) str.charAt(i);
//            if (  ( ch < 128)) {
//                buffer.append( ( char ) ch );
//            }
 //       }
        return str;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnItemTxtViewClicked(View view) {
            try {
                Intent new_intent = new Intent(StockInquiry.this, Item_List.class);
                new_intent.putExtra("substring", "");
                new_intent.putExtra("key","FromStockTake");
                startActivityForResult(new_intent, 4);
            } catch (Exception e) { Log.i("custDebug", "OnItemTxtViewClicked - " + e.getMessage()); }
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(StockInquiry.this);
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void onSearchBtnClicked(View view)
        {
            binding.getItem();
            hideSoftKeyboard(view);
            if (items.getItemCode() != null) {
                Intent intent = new Intent(StockInquiry.this, StockInquiryMultipleTab.class);
                intent.putExtra("ItemDetailKey", items);
                startActivity(intent);
            } else {
                Snackbar.make(view, "No Item selected.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //Return from item list
        if (requestCode == 4) {
            AC_Class.Item item = data.getParcelableExtra("ItemsKey");
            if (item != null) {
                items = item;
                if (CustomB == 1) {
                    items.setPrice(item.getPrice());
                    items.setPrice2(item.getPrice2());
                }else {
                    items.setPrice(0.0f);
                    items.setPrice2(0.0f);
                }
                if (CustomA == 1) {
                    items.setUDF_UDF1(item.getUDF_UDF1());
                    items.setUDF_UDF2(item.getUDF_UDF2());
                    items.setUDF_UDF3(item.getUDF_UDF3());
                    items.setUDF_UDF4(item.getUDF_UDF4());
                    items.setUDF_UDF5(item.getUDF_UDF5());
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }

            binding.btnSearch.performClick();
            //items.setItemCode(null);
        }

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {


                    Snackbar.make(findViewById(android.R.id.content), "No scanned data found.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    binding.itemEditText.setText(result.getContents().trim());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void getItemData(String substring) {
        Cursor data = db.getItemLike(substring, 1, SQLINCLAUSE);

        int nUDF1 = data.getColumnIndex("UDF_UDF1");
        int nUDF2 = data.getColumnIndex("UDF_UDF2");
        int nUDF3 = data.getColumnIndex("UDF_UDF3");
        int nUDF4 = data.getColumnIndex("UDF_UDF4");
        int nUDF5 = data.getColumnIndex("UDF_UDF5");

        s_inquiry.clear();
        while (data.moveToNext()) {

            try {
                items = new AC_Class.Item(
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
                        data.getString(nUDF1),
                        data.getString(nUDF2),
                        data.getString(nUDF3),
                        data.getString(nUDF4),
                        data.getString(nUDF5));
                s_inquiry.add(items);
            } catch (Exception e) {
                Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                Log.i("custDebug", "read image: "+e.getMessage());
            }

        }

        arrayAdapter.notifyDataSetChanged();
    }

    public void startScan()
    {
        final String[] bc = new String[1];

        barcode2D.scan();
        barcode2D.setScanCallback(new Barcode2DWithSoft.ScanCallback() {
            @Override
            public void onScanComplete(int i, int i1, byte[] bytes) {
                if (bytes != null) {
                    String barcode = new String(bytes);
                    binding.itemEditText.setText(barcode.trim());
                    StockInquiry.this.playSound(1);
                }
                else
                {
                    StockInquiry.this.playSound(2);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.MODEL.equals("HC720")) {
            while (keyCode == 139 || keyCode == 293) {
                if (event.getRepeatCount() == 0) {
                    startScan();
                } else {
                    barcode2D.stopScan();
                    break;
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (barcode2D != null) {
            try {
                if (android.os.Build.MODEL.equals("HC720")) {
                    boolean result = barcode2D.open(StockInquiry.this);
                    if (result) {
                        barcode2D.setParameter(324, 1);
                        barcode2D.setParameter(300, 0); // Snapshot Aiming
                        barcode2D.setParameter(361, 0); // Image Capture Illumination
                    }
                }
            }catch (Exception ex) {

            Toast.makeText(StockInquiry.this, ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }

    }

    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;

    private void initSound(){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }

    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, // 右声道音量
                    1, // 优先级，0为最低
                    0, // 循环次数，0无不循环，-1无永远循环
                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );
        } catch (Exception e) {
            e.printStackTrace();

        }
    }




}
