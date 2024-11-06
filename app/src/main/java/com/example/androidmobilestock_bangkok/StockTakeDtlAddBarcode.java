package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.ActivityStockTakeDtlAddBarcodeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StockTakeDtlAddBarcode extends AppCompatActivity {

    ACDatabase db;
    ActivityStockTakeDtlAddBarcodeBinding binding;
    AC_Class.StockTake st,st_temp;
    AC_Class.StockTakeDetails stDtl;
    ArrayList<AC_Class.StockTakeDetails> itemlist;
    StockTakeDtlAdapter adapter;
    MyClickHandler handler;
    private Barcode2DWithSoft barcode2D;
    //Button btnScan;
    private Context context;

    private IntentIntegrator qrScan;

    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;

    int opened = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(StockTakeDtlAddBarcode.this, R.layout.activity_stock_take_dtl_add_barcode);
        itemlist = new ArrayList<>();

        adapter = new StockTakeDtlAdapter(this, itemlist);
        binding.stdtladdbarcodeItemlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        db = new ACDatabase(this);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        context = this.getApplicationContext();

        //btnScan = (Button)findViewById(R.id.btnscan) ;

        st = getIntent().getParcelableExtra("STHeader");
        st_temp = getIntent().getParcelableExtra("STHeaderTemp");

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Stock Take Details - Add");

        binding.setDocNo(st.getDocNo());
        binding.trdtlEditText.requestFocus();
        binding.trdtlEditText.setShowSoftInputOnFocus(false);

        Cursor data = db.getReg("25");
        if(data.moveToFirst()){
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
        }

        if (isCustomBarcodeEnabled) {

            Cursor data2 = db.getReg("26");
            if(data2.moveToFirst()){
                barcodeLength = Integer.valueOf(data2.getString(0));
            }

            Cursor data3 = db.getReg("27");
            if(data3.moveToFirst()){
                itemStart = Integer.valueOf(data3.getString(0));
            }

            Cursor data4 = db.getReg("28");
            if(data4.moveToFirst()){
                itemLength = Integer.valueOf(data4.getString(0));
            }

            Cursor data5 = db.getReg("29");
            if(data5.moveToFirst()){
                qtyStart = Integer.valueOf(data5.getString(0));
            }

            Cursor data6 = db.getReg("30");
            if(data6.moveToFirst()){
                qtyLength = Integer.valueOf(data6.getString(0));
            }

            Cursor data7 = db.getReg("31");
            if(data7.moveToFirst()){
                qtyDecimal = Integer.valueOf(data7.getString(0));
            }

        }

//        boolean open = false;
        if (android.os.Build.MODEL.equals("HC720")) {
            try {
                barcode2D = Barcode2DWithSoft.getInstance();
            } catch (Exception ex) {
                Toast.makeText(StockTakeDtlAddBarcode.this, ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

//        try
//        {
//            if (open) {
//                barcode2D.setParameter(324, 1);
//                barcode2D.setParameter(300, 0); // Snapshot Aiming
//                barcode2D.setParameter(361, 0); // Image Capture Illumination
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

//        btnScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(opened == 0)
//                {
//                    opened = 1;
//
//                    startScan();
//                }
//                else{
//                    opened = 0;
//
//                    if (barcode2D.isPowerOn())
//                    {
//                        barcode2D.stopScan();
//                    }
//                }
//            }
//        });

        binding.trdtlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                double myQty = 1;

                if (!temp.equals("") && !TextUtils.isEmpty(binding.trdtlEditText.getText())) {

                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled)
                    {
                        if (temp.length() == barcodeLength)
                        {
                            if (isNumeric(temp)) {
                                myQty = Double.valueOf(temp.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                if (qtyDecimal > 0) {
                                    myQty = myQty / Math.pow(10,qtyDecimal);
                                }
                                temp = temp.substring(itemStart - 1, itemStart - 1 + itemLength);
                            } else {
                                Toast.makeText(StockTakeDtlAddBarcode.this, "Invalid Barcode.",
                                        Toast.LENGTH_SHORT).show();
                                isSkip = true;
                            }
                        }
                    }

                    if (!isSkip) {
                        Cursor results = db.getItemBC(temp);
                        if (results.getCount() == 0) {
                            Toast.makeText(StockTakeDtlAddBarcode.this, "Product not found.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            results.moveToNext();

                            stDtl = new AC_Class.StockTakeDetails();

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                            Date date = new Date();

                            stDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                            stDtl.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            stDtl.setUOM(results.getString(results.getColumnIndex("UOM")));
                            stDtl.setQuantity(myQty);
                            stDtl.setStockDocNo(st.getDocNo());
                            stDtl.setCreatedTimeStamp(dateFormat.format(date));

                            if (!CheckEmpty()) {
                                if (!CheckExist(myQty)) {
                                    itemlist.add(stDtl);
                                    st.getStockTakeDtlList().add(stDtl);
                                }

                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(StockTakeDtlAddBarcode.this, "Invalid Barcode", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                binding.trdtlEditText.removeTextChangedListener(this);
                binding.trdtlEditText.clearFocus();
                binding.trdtlEditText.requestFocus();
                hideSoftKeyboard();
                binding.trdtlEditText.setText("");
                binding.trdtlEditText.addTextChangedListener(this);


            }
        });

        if (android.os.Build.MODEL.equals("HC720")) {
            initSound();
        }

    }

    private boolean CheckEmpty() {
        return (stDtl.getItemCode() == null || stDtl.getUOM() == null );
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
                    binding.trdtlEditText.setText(barcode);
                    StockTakeDtlAddBarcode.this.playSound(1);
                }
                else
                {
                    StockTakeDtlAddBarcode.this.playSound(2);
                }
            }
        });

//        if(!trdtl_editText.getText().equals(""))
//        {
//            searchForItem(trdtl_editText.getText().toString());
//        }
    }


    private boolean CheckExist(Double myQtyFP) {
        boolean myresult = false;
        for(int i = 0; i < itemlist.size(); i++) {
            AC_Class.StockTakeDetails tempDtl = itemlist.get(i);

            if (tempDtl.getItemCode().equals(stDtl.getItemCode()) && tempDtl.getUOM().equals(stDtl.getUOM()))
            {
                if (isCustomBarcodeEnabled)
                    itemlist.get(i).setQuantity(itemlist.get(i).getQuantity()+myQtyFP);
                else
                    itemlist.get(i).setQuantity(itemlist.get(i).getQuantity()+1);
                myresult = true;
            }
        }
        return myresult;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(StockTakeDtlAddBarcode.this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.trdtlEditText.setText(result.getContents());
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnSaveBtnClicked(View view) {
            if (itemlist.size()<1) {
                Intent newIntent = new Intent();
                setResult(99, newIntent);
                //barcode2D.close();
                finish();
            } else {
                Intent newIntent = new Intent();
                newIntent.putParcelableArrayListExtra("stDtlList", itemlist);
                setResult(0, newIntent);
                //barcode2D.close();
                finish();
            }
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(StockTakeDtlAddBarcode.this);
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void OnCancelBtnClicked(View view) {
            StockTakeDtlAddBarcode.this.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent();
        st = st_temp;
        newIntent.putExtra("STHeader", st);
        setResult(99, newIntent);
        //barcode2D.close();
        finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (barcode2D != null) {
            try {
                if (android.os.Build.MODEL.equals("HC720")) {
                    boolean result = barcode2D.open(StockTakeDtlAddBarcode.this);
                    if (result) {
                        barcode2D.setParameter(324, 1);
                        barcode2D.setParameter(300, 0); // Snapshot Aiming
                        barcode2D.setParameter(361, 0); // Image Capture Illumination
                    }
                }
            }catch (Exception ex) {

                Toast.makeText(StockTakeDtlAddBarcode.this, ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(StockTakeDtlAddBarcode.this.getCurrentFocus().getWindowToken(), 0);
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

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}