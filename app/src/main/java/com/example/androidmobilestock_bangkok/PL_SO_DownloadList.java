package com.example.androidmobilestock_bangkok;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

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

public class PL_SO_DownloadList extends AppCompatActivity {

    private RecyclerView recyclerView;
    TextView modeView;
    EditText searchEditText;
    ACDatabase db;
    ProgressDialog pd;

    PL_SO_DownloadListAdapter arrayAdapter;
    List<AC_Class.SOMenu> soMenuList = new ArrayList<>();
    private PL_SO_DownloadListAdapter.RecyclerViewClickListener listener;

    String url2;
    String plType;
    String substring = "";
    TextView total;
    Button confirm;
    ArrayList<AC_Class.SOMenu> soMenus = new ArrayList<>();
    int count;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pl_activity_so_downloadlist);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        total = (TextView) findViewById(R.id.tv_total);
        confirm = (Button) findViewById(R.id.confirm);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Download List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        db = new ACDatabase(this);
        plType = getIntent().getStringExtra("PLType");

        Cursor cur = db.getReg("4");
        if(cur.moveToFirst()){
            user = cur.getString(0);
        }

        Cursor cur5 = db.getReg("2");
        if(cur5.moveToFirst()){
            url2 = cur5.getString(0);
        }
            setAdapter();

            listener = new PL_SO_DownloadListAdapter.RecyclerViewClickListener(){
                @Override
                public void onClick(View v, int position) {

                    AC_Class.SOMenu i = (AC_Class.SOMenu) soMenuList.get(position);
                    soMenus.add(i);

                }

                @Override
                public void onFalseClick(View v, int position) {

                    String removedoc = ((AC_Class.SOMenu) soMenuList.get(position)).getDocNo();
                    for(int i = 0; i < soMenus.size(); i++){
                        if(soMenus.get(i).getDocNo().equals(removedoc)){
                            soMenus.remove(soMenus.get(i));
                        }
                    }

                }

                @Override
                public void onLayoutClick(View v, int position) {
                    AC_Class.SOMenu i = (AC_Class.SOMenu) soMenuList.get(position);
                    Intent intent = new Intent(PL_SO_DownloadList.this, PL_SO_DownloadDtlList.class);
                    intent.putExtra("plType",plType);
                    intent.putExtra("mySO", i.DocNo);
                    startActivityForResult(intent, 0);
                }
            };


        modeView = findViewById(R.id.modeView2);
        searchEditText = (EditText) findViewById(R.id.searchField);
        //searchEditText.requestFocus();

        getSOData();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                substring = s.toString().trim();
                getSOData();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soMenus.size()>0) {
                pd = new ProgressDialog(PL_SO_DownloadList.this);
                pd.setMessage("Downloading SalesOrder...");
                pd.setCancelable(false);
                pd.show();
                count = soMenus.size();
                for(int i = 0; i < soMenus.size(); i++) {
                    new GetSalesOrder(PL_SO_DownloadList.this, soMenus.get(i).getDocNo()).execute(url2);
                }}
                else{
                    Toast.makeText(PL_SO_DownloadList.this, "Please select at least 1 SO",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Return from dtl list
            case 0:
                if (resultCode == 0) {
                    Object mySO = data.getStringExtra("mySO");

                    if (mySO != null)
                    {
                        Intent item_intent = new Intent();
                        item_intent.putExtra("mySO", String.valueOf(mySO));
                        item_intent.putExtra("plType", plType);
                        setResult(0, item_intent);
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null) {
            Intent myIntent = new Intent();
            myIntent.putExtra("mySO", "");
            setResult(99, myIntent);
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

    public void getSOData() {
        soMenuList.clear();
        new GetSalesOrderHeader(this).execute(url2);

    }


    class GetSalesOrderHeader extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetSalesOrderHeader(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderHeader(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();


            total.setText("Total: " + soMenuList.size());
            setAdapter();


        }
    }

    private Boolean getSalesOrderHeader(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            java.net.URL url = new URL(connUrl + "/getSalesOrderHeader/" + plType + "?sub=" + substring);
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
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    Integer ID = object.getInt("MKey");
                    String DocNo = object.getString("DocNo");
                    String DocDate = object.getString("DocDate");
                    String DebtorCode = object.getString("DebtorCode");
                    String DebtorName = object.getString("DebtorName");
                    String SalesAgent = object.getString("SalesAgent");
                    String remark = object.getString("Remark");
                    String loc = object.getString("Location");
                    String Phone = object.getString("Phone");
                    String Fax = object.getString("Fax");
                    String Attention = object.getString("Attention");

                    AC_Class.SOMenu so = new AC_Class.SOMenu(DocNo, DocDate, DebtorCode, DebtorName, SalesAgent, plType, remark, loc);
                    soMenuList.add(so);
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    public void setAdapter(){
        arrayAdapter = new PL_SO_DownloadListAdapter(this, soMenuList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(arrayAdapter);
    }

    class GetSalesOrder extends AsyncTask<String, Void, Boolean> {
        Activity context;
        String docNo;

        GetSalesOrder(Activity context, String docNo) {
            this.context = context;
            this.docNo = docNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrder(connUrl[0], docNo);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            new GetPLStatus(PL_SO_DownloadList.this, docNo).execute(url2);
        }
    }

    class GetPLStatus extends AsyncTask<String, Void, Boolean> {
        Activity context;
        String docNo;

        GetPLStatus(Activity context, String docNo) {
            this.context = context;
            this.docNo = docNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getPLStatus(connUrl[0], docNo);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);


            count--;

            if(count==0){

                final int interval = 2000; // 2 Second
                Handler handler = new Handler();
                Runnable runnable = new Runnable(){
                    public void run() {
                        if (pd.isShowing())
                            pd.dismiss();
                        onBackPressed();
                    }
                };
                handler.postAtTime(runnable, System.currentTimeMillis()+interval);
                handler.postDelayed(runnable, interval);

            }

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

        }
    }

    private Boolean getSalesOrder(String connUrl, String docNo)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            java.net.URL url = new URL(connUrl + "/getSalesOrder/" + plType + "/" + docNo);
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

                db.deleteSalesOrder(docNo);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    Integer ID = object.getInt("MKey");
                    String DocNo = object.getString("DocNo");
                    String DocDate = object.getString("DocDate");
                    String DebtorCode = object.getString("DebtorCode");
                    String DebtorName = object.getString("DebtorName");
                    String SalesAgent = object.getString("SalesAgent");
                    String Phone = object.getString("Phone");
                    String Fax = object.getString("Fax");
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


                    boolean inserted = db.insertSalesOrder(ID, DocNo, DocDate, DebtorCode, DebtorName, SalesAgent, Phone, Fax, Attention, LineNo, ItemCode, ItemDescription, Location, Qty, UOM, remark, remarkdtl,plType,batch);

                    if(!inserted){
                        new GetSalesOrder(PL_SO_DownloadList.this,DocNo).execute(url2);
                    }
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    private Boolean getPLStatus(String connUrl, String docNo)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            java.net.URL url = new URL(connUrl + "/setPLStatus/" + plType + "/" + docNo+ "/" + user);
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
