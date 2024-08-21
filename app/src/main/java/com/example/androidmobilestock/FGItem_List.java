package com.example.androidmobilestock;

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

import com.example.androidmobilestock.databinding.ActivityFinishgoodListBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class FGItem_List extends AppCompatActivity {
    ListView agent_listView;
    ACDatabase db;
    ActivityFinishgoodListBinding binding;
    List<AC_Class.ItemBOM> bom = new ArrayList<>();
    ItemBOMListViewAdapter arrayAdapter;
    TextView camera;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishgood__list);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finishgood__list);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of Finish Goods");
        actionBar.setDisplayHomeAsUpEnabled(true);

        agent_listView = (ListView)findViewById(R.id.list_agent);
        db = new ACDatabase(this);
        getAgentData();

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
                    qrScan = new IntentIntegrator(FGItem_List.this);
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

    @Override
    public void onBackPressed() {
        Intent agent_intent = new Intent();
        setResult(10, agent_intent);
        finish();
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

    public void getAgentData() {
        Cursor data = db.getFG();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                AC_Class.ItemBOM itemBOM =
                        new AC_Class.ItemBOM(data.getString(data.getColumnIndex("ItemCode")),
                                data.getString(data.getColumnIndex("Description")));
                bom.add(itemBOM);
            }
            arrayAdapter = new ItemBOMListViewAdapter(this, bom);
            agent_listView.setAdapter(arrayAdapter);
            agent_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AC_Class.ItemBOM sa =(AC_Class.ItemBOM) parent.getItemAtPosition(position);
                    Intent agent_intent = new Intent();
                    agent_intent.putExtra("ItemKey", sa);
                    setResult(1, agent_intent);
                    finish();
                }
            });
        }
    }

    public void getItemData2(String substring) {
        Cursor data = db.getFGLike(substring, 0);
        if (data.getCount() > 0){
            bom.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.ItemBOM itemBOM =
                            new AC_Class.ItemBOM(data.getString(data.getColumnIndex("ItemCode")),
                                    data.getString(data.getColumnIndex("Description")));
                    bom.add(itemBOM);
                } catch (Exception e) { Log.i("custDebug", "error reading image: "+e.getMessage()); }
            }
        }
        arrayAdapter.notifyDataSetChanged();
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
}
