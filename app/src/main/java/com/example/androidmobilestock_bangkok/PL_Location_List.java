package com.example.androidmobilestock_bangkok;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.ActivityLocationListBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class PL_Location_List extends AppCompatActivity {

    ListView location_listView;
    ACDatabase db;
    TextView camera;
    private IntentIntegrator qrScan;
    ActivityLocationListBinding binding;
    List<AC_Class.Location> loc_inquiry = new ArrayList<>();
    PL_LocationListViewAdapter arrayAdapter;
    String docNo, Item;
    Intent pIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location__list);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location__list);
        location_listView = (ListView) findViewById(R.id.list_location);
        //Action Bar Settings
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Locations");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        pIntent = getIntent();
        docNo = pIntent.getStringExtra("DocNo");
        Item = pIntent.getStringExtra("ItemCode");
        getLocationData();

        binding.itemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                getItemData2(s.toString().trim());
            }
        });

        camera = (TextView) findViewById(R.id.si_item_camera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                Intent new_intent = new Intent(StockInquiry.this, activity_scanner.class);
//                startActivity(new_intent);
                    qrScan = new IntentIntegrator(PL_Location_List.this);
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

        location_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.Location loc = (AC_Class.Location) parent.getItemAtPosition(position);
                Intent loc_intent = new Intent();
                loc_intent.putExtra("LocationsKey", loc);
                setResult(3, loc_intent);
                finish();
            }
        });
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

    public void getLocationData() {
        if(Item !=null) {
            Cursor data = db.getPLLocationLike("", 0, Item);
            if (data.getCount() == 0) {
                return;
            } else if (data.getCount() > 0) {
                while (data.moveToNext()) {
                    AC_Class.Location location =
                            new AC_Class.Location(data.getString(0), data.getString(1),data.getDouble(2));
                    loc_inquiry.add(location);
                }
                arrayAdapter = new PL_LocationListViewAdapter(this, loc_inquiry);
                location_listView.setAdapter(arrayAdapter);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No scanned data found.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    binding.itemEditText.setText(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void getItemData2(String substring) {
        Cursor data = db.getPLLocationLike(substring, 0, Item);
        if (data.getCount() > 0){
            loc_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Location location =
                            new AC_Class.Location(data.getString(0),
                                    data.getString(1),data.getDouble(2));
                    loc_inquiry.add(location);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }
}
