package com.example.androidmobilestock_bangkok;

import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.androidmobilestock_bangkok.databinding.PurActivityMultipletabBinding;

import java.util.ArrayList;
import java.util.List;

public class PUR_MultipleTab extends AppCompatActivity {

    PurActivityMultipletabBinding binding;
    AC_Class.PurchaseMenu purchaseMenu = new AC_Class.PurchaseMenu();
    String TAG = "custDebug";

    ListView listview;
    ACDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pur_activity_multipletab);
        db = new ACDatabase(this);

        String docNo = getIntent().getStringExtra("iDocNo");
        String creditorCode = getIntent().getStringExtra("iCreditorCode");

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(docNo+" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        } catch (Exception e) {
            Log.i(TAG, "Purchase Details: "+e.getMessage());}

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            binding.setDefaultCurr(cur.getString(0));;
        }

        binding.setPurchase(purchaseMenu);
        listview = (ListView)binding.lvDetail;

        getData(docNo);
    }

    private void getData(String docNo){

        ACDatabase db = new ACDatabase(PUR_MultipleTab.this);

        if (docNo != null) {
            Cursor header = db.getPurchaseHeaderPrint(docNo);
            if (header.getCount()>0)
            {
                while (header.moveToNext()) {
                    purchaseMenu.setDocDate(header.getString(header.getColumnIndex("DocDate")));
                    purchaseMenu.setDocNo(docNo);
                    purchaseMenu.setCreditorCode(header.getString(header.getColumnIndex("CreditorCode")));
                    purchaseMenu.setCreditorName(header.getString(header.getColumnIndex("CreditorName")));
                    purchaseMenu.setAgent(header.getString(header.getColumnIndex("PurchaseAgent")));
                    purchaseMenu.setRemarks(header.getString(header.getColumnIndex("Remarks")));
                    purchaseMenu.setRemarks2(header.getString(header.getColumnIndex("Remarks2")));
                    purchaseMenu.setRemarks3(header.getString(header.getColumnIndex("Remarks3")));
                    purchaseMenu.setRemarks4(header.getString(header.getColumnIndex("Remarks4")));
                }
            }

            if(purchaseMenu.getRemarks()==null){
                binding.txtRemark.setVisibility(View.GONE);
                binding.txtRemarkTxt.setVisibility(View.GONE);
            }

            if(purchaseMenu.getRemarks2()==null){
                binding.txtRemark3.setVisibility(View.GONE);
                binding.txtRemark4.setVisibility(View.GONE);
            }

            if(purchaseMenu.getRemarks3()==null){
                binding.txtRemark5.setVisibility(View.GONE);
                binding.txtRemark6.setVisibility(View.GONE);
            }

            if(purchaseMenu.getRemarks4()==null){
                binding.txtRemark7.setVisibility(View.GONE);
                binding.txtRemark8.setVisibility(View.GONE);
            }

            Cursor data = db.getPurchaseDetailtoUpdate(docNo);
            List<AC_Class.PurchaseDetails> dtlList = new ArrayList<>();
            if (data.getCount() > 0) {
                Double totalIn = 0.00d;

                while (data.moveToNext()) {
                    AC_Class.PurchaseDetails myDtil = new AC_Class.PurchaseDetails(data.getInt(0),
                            data.getString(1), data.getString(2), data.getString(3),
                            data.getString(4), data.getString(5), data.getDouble(6),
                            data.getDouble(7), data.getDouble(8), data.getDouble(9),
                            data.getString(10), data.getDouble(11), data.getDouble(12),
                            data.getDouble(13), data.getDouble(14), data.getString(15),
                            data.getString(data.getColumnIndex("BatchNo")), data.getString(data.getColumnIndex("Remarks"))
                            , data.getString(data.getColumnIndex("Remarks2")));
                    dtlList.add(myDtil);

                    totalIn += data.getDouble(14);
                }

                purchaseMenu.setTotalIn(totalIn);
            }

            PUR_PurchaseDtlListViewAdapter adapter = new PUR_PurchaseDtlListViewAdapter(PUR_MultipleTab.this, dtlList);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
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