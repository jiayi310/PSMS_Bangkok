package com.example.androidmobilestock;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class Jobsheet_AddItemManual extends AppCompatActivity implements JobsheetItemListAdapter.OnItemClickListener {

    RecyclerView itemRecyclerView;
    EditText searchField;
    TextView si_item_camera;
    ACDatabase db;
    List<AC_Class.Item> s_inquiry = new ArrayList<>();
    String func, subString, mode;
    List<AC_Class.Item> originalList = new ArrayList<>();
    JobsheetItemListAdapter adapter;
    String SQLINCLAUSE = "";
    String defcurr = "";
    private static final int REQUEST_CODE_ADD_ITEM_DETAIL = 2;
    private static final int REQUEST_CODE_UPDATE_ITEM = 3;
    public String scannedData;
    AC_Class.JobSheet jobSheet;
    AC_Class.JobSheetDetails jobSheetDetails;
    int position;
    boolean isUpdateMode;
    IntentIntegrator qrScan;
    boolean fromItemPage;
    private static final int REQUEST_CODE_UPDATE_ITEM2 = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet_add_item_manual);

        itemRecyclerView = findViewById(R.id.list_item);
        searchField = findViewById(R.id.searchField);
        si_item_camera = findViewById(R.id.si_item_camera);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("List of Items");

        db = new ACDatabase(this);

        jobSheet = getIntent().getParcelableExtra("JobSheet");
        subString = getIntent().getStringExtra("subString");
        jobSheetDetails = getIntent().getParcelableExtra("nItem");
        position = getIntent().getIntExtra("nPosition", -1);
        isUpdateMode = getIntent().getBooleanExtra("isUpdateMode", false);
        fromItemPage = getIntent().getBooleanExtra("fromItemPage", false);


        func = getIntent().getStringExtra("FunctionKey");
        mode = getIntent().getStringExtra("iMode");

        scannedData = getIntent().getStringExtra("scannedData");


        Cursor data = db.getReg("6");
        if(data.moveToFirst()){
            defcurr = data.getString(0);
        }

        if (jobSheetDetails != null && position != -1 && isUpdateMode){
            Intent intent = new Intent(Jobsheet_AddItemManual.this,
                    Jobsheet_AddItemDetails.class);
            intent.putExtra("JobSheet", jobSheet);
            intent.putExtra("nItem", jobSheetDetails);
            intent.putExtra("nPosition", position);
            intent.putExtra("isUpdateMode", isUpdateMode);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_ITEM);
        }

        if (scannedData != null){
            Intent intent = new Intent(Jobsheet_AddItemManual.this, Jobsheet_AddItemDetails.class);
            intent.putExtra("scannedData", scannedData);
            intent.putExtra("JobSheet", jobSheet);
            startActivity(intent);
        }

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        originalList = new ArrayList<>(s_inquiry);
        adapter = new JobsheetItemListAdapter(this,s_inquiry, defcurr, this);
        itemRecyclerView.setAdapter(adapter);

        loadItem(subString);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItemList(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        si_item_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qrScan = new IntentIntegrator(Jobsheet_AddItemManual.this);
                    qrScan.setPrompt("Scan a Barcode/QRCode");
                    qrScan.setCameraId(0);
                    qrScan.setBeepEnabled(true);
                    qrScan.setOrientationLocked(false);
                    Intent intent = qrScan.createScanIntent();
                    startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

                } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }

            }
        });

    }

    private void filterItemList(String text) {
        List<AC_Class.Item> filteredList = new ArrayList<>();
        for (AC_Class.Item item : originalList) {
            if (item.getItemCode().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            //item -> manual -> detail -> manual
            case REQUEST_CODE_ADD_ITEM_DETAIL:
                if (resultCode == RESULT_OK && data != null){
                    AC_Class.JobSheetDetails newItem = data.getParcelableExtra("JobSheet Details");
                    if (newItem != null) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("JobSheet Details", newItem);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
                break;

            //update item detail
            case REQUEST_CODE_UPDATE_ITEM:
                if (resultCode == RESULT_OK && data != null){
                    AC_Class.JobSheetDetails updatedItem = data.getParcelableExtra("JobSheet Details");
                    if (updatedItem != null){
                        Intent intent = new Intent();
                        intent.putExtra("JobSheet Details", updatedItem);
                        intent.putExtra("nPosition", position);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;



            //scanner in the same class is used
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "No result found.", Toast.LENGTH_SHORT).show();
                    } else {
                        String scannedData = result.getContents().trim();
                        /*
                        Intent intent = new Intent(this, Jobsheet_AddItemDetails.class);
                        intent.putExtra("JobSheet", jobSheet);
                        intent.putExtra("scannedData", scannedData);
                        startActivityForResult(intent, REQUEST_CODE_ADD_ITEM_DETAIL);

                         */
                        searchField.setText(scannedData);
                    }
                }
                break;
        }
    }

    private void loadItem(String subString) {
        Cursor data = db.getItemLike(subString, 5, SQLINCLAUSE);
        if (data.getCount() > 0){
            s_inquiry.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1),
                            data.getString(2), data.getString(3), data.getString(4), data.getString(5),
                            data.getString(6), data.getString(7), data.getString(8), data.getString(9),
                            data.getFloat(10), data.getFloat(11), data.getString(12), data.getString(13));

                    originalList.add(item);
                    s_inquiry.add(item);
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: "+e.getMessage());
                }
            }
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AC_Class.Item item) {
        if (isUpdateMode){
            if (item != null){
                Intent intent = new Intent();
                intent.putExtra("ItemDetails", item);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Log.d("Checking123", "no item selected");
            }
        } else {
            Intent intent = new Intent(this, Jobsheet_AddItemDetails.class);
            intent.putExtra("JobSheet", jobSheet);
            intent.putExtra("ItemDetails", item);
            intent.putExtra("fromItemPage", fromItemPage);
            startActivityForResult(intent, REQUEST_CODE_ADD_ITEM_DETAIL);
        }

    }


}