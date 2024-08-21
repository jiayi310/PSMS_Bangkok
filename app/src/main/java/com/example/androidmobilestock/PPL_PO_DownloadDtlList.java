package com.example.androidmobilestock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.databinding.PlActivitySoDownloaddtllistBinding;
import com.example.androidmobilestock.databinding.PplActivityPoDownloaddtllistBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PPL_PO_DownloadDtlList extends AppCompatActivity {
    PplActivityPoDownloaddtllistBinding binding;
    ACDatabase db;

    MyClickHandler handler;
    PL_PLDtlListViewAdapter adapter;
    String docNo;
    String url;
    String plType;

    List<AC_Class.DODtl> doDtlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.ppl_activity_po_downloaddtllist);

        db = new ACDatabase(this);

        Cursor cur5 = db.getReg("2");
        if(cur5.moveToFirst()){
            url = cur5.getString(0);
        }

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        plType = getIntent().getStringExtra("plType");

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("PO Packing List Details");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        docNo = getIntent().getStringExtra("mySO");

        adapter = new PL_PLDtlListViewAdapter(this, doDtlList);
        binding.lvPackingListDtl.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getData();

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                unregisterReceiver(this);
                finish();
            }
        }, intentFilter);
    }

    public void getData()
    {
        doDtlList.clear();
        new GetSalesOrderDetail(this).execute(url);
    }

    class GetSalesOrderDetail extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetSalesOrderDetail(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Inquiry PurchaseOrder List...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderDetail(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (pd.isShowing())
                pd.dismiss();

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            adapter.notifyDataSetChanged();
        }
    }

    private Boolean getSalesOrderDetail(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getPurchaseOrderDetail/" + plType + "/" + docNo);
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    String DocNo = object.getString("DocNo");
                    String Location = object.getString("Location");
                    String ItemCode = object.getString("ItemCode");
                    String ItemDescription = object.getString("ItemDescription");
                    String UOM = object.getString("UOM");
                    Double Qty = object.getDouble("Qty");
                    String remarks = object.getString("DtlRemark");
                    String batch = object.getString("BatchNo");


                    AC_Class.DODtl so = new AC_Class.DODtl(DocNo, Location, ItemCode, ItemDescription, UOM, Qty, remarks, batch);
                    doDtlList.add(so);
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnbtnSaveClicked(View view)
        {
            if (doDtlList.size()>0) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(PPL_PO_DownloadDtlList.this);
                builder.setMessage("Do you want to confirm and download this order?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    new GetSalesOrder(PPL_PO_DownloadDtlList.this).execute(url);
                    Intent item_intent = new Intent();
                    item_intent.putExtra("mySO", docNo);
                    setResult(0, item_intent);
                    finish();
                }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

//                Intent myIntent = new Intent(PL_SO_DownloadDtlList.this, PL_Compare.class);
//                myIntent.putExtra("iPackingList", packingList);
//                myIntent.putExtra("iType", typeFP);
//                startActivityForResult(myIntent, 3);

//                db.savePackingList(packingList, typeFP);
//
//                // Broadcast intent to close other activities
//                Intent broadcastIntent = new Intent();
//                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
//                sendBroadcast(broadcastIntent);
//
            } else {
                Toast.makeText(PPL_PO_DownloadDtlList.this, "Please add at least 1 item",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    class GetSalesOrder extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetSalesOrder(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Downloading Purchase Order...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrder(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (pd.isShowing())
                pd.dismiss();

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            new GetPLStatus(PPL_PO_DownloadDtlList.this).execute(url);
            adapter.notifyDataSetChanged();
        }
    }

    class GetPLStatus extends AsyncTask<String, Void, Boolean> {
        Activity context;

        GetPLStatus(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getPLStatus(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {

        if (getCallingActivity() != null) {
            Intent myIntent = new Intent();
            //myIntent.putExtra("mySO", "");
            setResult(0, myIntent);
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

    private Boolean getSalesOrder(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getPurchaseOrder/" + plType + "/" + docNo);
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                db.deletePurchaseOrder(docNo);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    Integer ID = object.getInt("MKey");
                    String DocNo = object.getString("DocNo");
                    String DocDate = object.getString("DocDate");
                    String DebtorCode = object.getString("CreditorCode");
                    String DebtorName = object.getString("CreditorName");
                    String SalesAgent = object.getString("PurchaseAgent");
                    String Phone = object.getString("Phone");
                    String Fax = object.getString("Fax");
                    String PurchaseLocation = object.getString("PurchaseLocation");
                    String Attention = object.getString("Attention");
                    Integer LineNo = object.getInt("LineNo");
                    String ItemCode = object.getString("ItemCode");
                    String ItemDescription = object.getString("ItemDescription");
                    String Location = object.getString("Location");
                    Double Qty = object.getDouble("Qty");
                    String UOM = object.getString("UOM");
                    String remark = object.getString("Remark");
                    String remarkdtl = object.getString("DtlRemark");
                    String batch = object.getString("BatchNo");

                    db.insertPurchaseOrder(ID, DocNo, DocDate, DebtorCode, DebtorName, SalesAgent, Phone, Fax, PurchaseLocation, Attention, LineNo, ItemCode, ItemDescription, Location, Qty, UOM, remark, remarkdtl,plType,batch, 1);
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    private Boolean getPLStatus(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/setPLStatus/" + plType + "/" + docNo);
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {

            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }
}